package net.colourlabs.dimensionalworldreborn.block;

import javax.annotation.Nullable;

import net.colourlabs.dimensionalworldreborn.registry.ModBlocks;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.StringRepresentable;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.EnumProperty;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;

public class MiningPortalBlock extends Block {
    public static final EnumProperty<PortalAxis> AXIS = EnumProperty.create("axis", PortalAxis.class);

    private static final VoxelShape X_SHAPE = Block.box(0.0D, 0.0D, 6.0D, 16.0D, 16.0D, 10.0D);
    private static final VoxelShape Z_SHAPE = Block.box(6.0D, 0.0D, 0.0D, 10.0D, 16.0D, 16.0D);
    private static final VoxelShape Y_SHAPE = Block.box(0.0D, 6.0D, 0.0D, 16.0D, 10.0D, 16.0D);

    public MiningPortalBlock(Properties properties) {
        super(properties);
        registerDefaultState(stateDefinition.any().setValue(AXIS, PortalAxis.Z));
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(AXIS);
    }

    @Override
    public VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
        return switch (state.getValue(AXIS)) {
            case X -> X_SHAPE;
            case Y -> Y_SHAPE;
            case Z -> Z_SHAPE;
        };
    }

    @Override
    public VoxelShape getCollisionShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
        return Shapes.empty();
    }

    @Override
    public RenderShape getRenderShape(BlockState state) {
        return RenderShape.MODEL;
    }

    @Override
    public boolean propagatesSkylightDown(BlockState state, BlockGetter level, BlockPos pos) {
        return true;
    }

    @Override
    public FluidState getFluidState(BlockState state) {
        return Fluids.EMPTY.defaultFluidState();
    }

    @Override
    public void neighborChanged(BlockState state, Level level, BlockPos pos, Block neighborBlock, BlockPos neighborPos, boolean movedByPiston) {
        if (!level.isClientSide()) {
            PortalSize size = new PortalSize(level, pos, state.getValue(AXIS));
            if (!size.isValidPortal()) {
                level.removeBlock(pos, false);
            }
        }
    }

    @Override
    public void entityInside(BlockState state, Level level, BlockPos pos, Entity entity) {
        if (!level.isClientSide() && entity instanceof ServerPlayer player && player.isShiftKeyDown() && player.getPortalCooldown() <= 0) {
            net.colourlabs.dimensionalworldreborn.world.MiningWorldTeleporter.teleport(player);
        }
    }

    public static class PortalSize {
        private static final int MAX_HEIGHT = 21;
        private static final int MAX_WIDTH = 21;

        private final Level level;
        private final PortalAxis axis;
        private final Direction rightDir;
        private final Direction leftDir;
        private final Direction upDir;
        private final Direction downDir;

        @Nullable
        private BlockPos bottomLeft;
        private int height;
        private int width;
        private int portalBlockCount;

        public PortalSize(Level level, BlockPos pos, PortalAxis axis) {
            this.level = level;
            this.axis = axis;

            if (axis == PortalAxis.X) {
                this.leftDir = Direction.EAST;
                this.upDir = Direction.UP;
            } else if (axis == PortalAxis.Z) {
                this.leftDir = Direction.NORTH;
                this.upDir = Direction.UP;
            } else {
                this.leftDir = Direction.NORTH;
                this.upDir = Direction.EAST;
            }

            this.rightDir = leftDir.getOpposite();
            this.downDir = upDir.getOpposite();

            BlockPos searchPos = pos;
            while (distanceAlong(pos, upDir) - distanceAlong(searchPos, upDir) < MAX_HEIGHT
                && distanceAlong(searchPos, upDir) > level.getMinBuildHeight()
                && isPortalInterior(searchPos.relative(downDir))) {
                searchPos = searchPos.relative(downDir);
            }

            int distanceUntilEdge = getDistanceUntilEdge(searchPos, leftDir) - 1;
            if (distanceUntilEdge >= 0) {
                this.bottomLeft = searchPos.relative(leftDir, distanceUntilEdge);
                this.width = getDistanceUntilEdge(bottomLeft, rightDir);
                if (width < 1 || width > MAX_WIDTH) {
                    this.bottomLeft = null;
                    this.width = 0;
                }
            }

            if (bottomLeft != null) {
                this.height = calculatePortalHeight();
            }
        }

        public boolean isValid() {
            return bottomLeft != null && width >= 1 && width <= MAX_WIDTH && height >= 1 && height <= MAX_HEIGHT;
        }

        public boolean isValidPortal() {
            return isValid() && portalBlockCount == width * height;
        }

        public void placePortalBlocks() {
            if (bottomLeft == null) {
                return;
            }

            BlockState portalState = ModBlocks.MINING_PORTAL.get().defaultBlockState().setValue(AXIS, axis);
            for (int currentWidth = 0; currentWidth < width; currentWidth++) {
                BlockPos columnStart = bottomLeft.relative(rightDir, currentWidth);
                for (int currentHeight = 0; currentHeight < height; currentHeight++) {
                    level.setBlock(columnStart.relative(upDir, currentHeight), portalState, Block.UPDATE_ALL);
                }
            }
        }

        private int getDistanceUntilEdge(BlockPos searchPos, Direction searchDir) {
            int distance;
            for (distance = 0; distance <= MAX_WIDTH; distance++) {
                BlockPos currentPos = searchPos.relative(searchDir, distance);
                if (!isPortalInterior(currentPos) || !isFrame(currentPos.relative(downDir))) {
                    break;
                }
            }

            return isFrame(searchPos.relative(searchDir, distance)) ? distance : 0;
        }

        private int calculatePortalHeight() {
            if (bottomLeft == null) {
                return 0;
            }

            for (height = 0; height < MAX_HEIGHT; height++) {
                for (int currentWidth = 0; currentWidth < width; currentWidth++) {
                    BlockPos pos = bottomLeft.relative(rightDir, currentWidth).relative(upDir, height);
                    BlockState state = level.getBlockState(pos);
                    if (!isPortalInterior(state)) {
                        return finishHeight();
                    }

                    if (state.is(ModBlocks.MINING_PORTAL.get())) {
                        portalBlockCount++;
                    }

                    if (currentWidth == 0 && !isFrame(pos.relative(leftDir))) {
                        return finishHeight();
                    }

                    if (currentWidth == width - 1 && !isFrame(pos.relative(rightDir))) {
                        return finishHeight();
                    }
                }
            }

            return finishHeight();
        }

        private int finishHeight() {
            if (bottomLeft == null) {
                return 0;
            }

            for (int currentWidth = 0; currentWidth < width; currentWidth++) {
                if (!isFrame(bottomLeft.relative(rightDir, currentWidth).relative(upDir, height))) {
                    clear();
                    return 0;
                }
            }

            if (height >= 1 && height <= MAX_HEIGHT) {
                return height;
            }

            clear();
            return 0;
        }

        private void clear() {
            bottomLeft = null;
            width = 0;
            height = 0;
            portalBlockCount = 0;
        }

        private boolean isFrame(BlockPos pos) {
            return level.getBlockState(pos).is(ModBlocks.PORTAL_FRAME.get());
        }

        private boolean isPortalInterior(BlockPos pos) {
            return isPortalInterior(level.getBlockState(pos));
        }

        private boolean isPortalInterior(BlockState state) {
            return state.isAir() || state.is(ModBlocks.MINING_PORTAL.get());
        }

        private static int distanceAlong(BlockPos pos, Direction direction) {
            return switch (direction.getAxis()) {
                case X -> pos.getX() * direction.getAxisDirection().getStep();
                case Y -> pos.getY() * direction.getAxisDirection().getStep();
                case Z -> pos.getZ() * direction.getAxisDirection().getStep();
            };
        }
    }

    public enum PortalAxis implements StringRepresentable {
        X("x"),
        Y("y"),
        Z("z");

        private final String serializedName;

        PortalAxis(String serializedName) {
            this.serializedName = serializedName;
        }

        @Override
        public String getSerializedName() {
            return serializedName;
        }

        public static PortalAxis fromDirectionAxis(Direction.Axis axis) {
            return switch (axis) {
                case X -> X;
                case Y -> Y;
                case Z -> Z;
            };
        }
    }
}
