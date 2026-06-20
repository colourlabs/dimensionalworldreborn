package net.colourlabs.dimensionalworldreborn.world;

import java.util.function.Function;

import net.colourlabs.dimensionalworldreborn.registry.ModBlocks;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.level.portal.PortalInfo;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.common.util.ITeleporter;

public class MiningWorldTeleporter implements ITeleporter {
    private final BlockPos targetPos;

    private MiningWorldTeleporter(ServerPlayer player, BlockPos targetPos) {
        this.targetPos = targetPos;
    }

    public static boolean teleport(ServerPlayer player) {
        ServerLevel destination = getDestinationLevel(player);

        if (destination == null) {
            player.displayClientMessage(Component.translatable("dimensionalworldreborn.info.missing_dimension"), true);
            return false;
        }

        BlockPos targetPos = findTargetPos(player, destination);
        player.changeDimension(destination, new MiningWorldTeleporter(player, targetPos));
        player.setPortalCooldown();

        MiningWorldSettings.Values settings = MiningWorldSettings.get();
        if (destination.dimension() == ModDimensions.MINING_WORLD && settings.showWelcomeMessage() && !settings.welcomeMessage().isBlank()) {
            player.displayClientMessage(Component.literal(settings.welcomeMessage()), false);
        }

        return true;
    }

    @Override
    public PortalInfo getPortalInfo(Entity entity, ServerLevel destination, Function<ServerLevel, PortalInfo> defaultPortalInfo) {
        if (MiningWorldSettings.get().createPlatform()) {
            createPlatform(destination, targetPos);
        }

        Vec3 pos = Vec3.atBottomCenterOf(targetPos).add(0.0D, 0.1D, 0.0D);
        return new PortalInfo(pos, entity.getDeltaMovement(), entity.getYRot(), entity.getXRot());
    }

    @Override
    public Entity placeEntity(Entity entity, ServerLevel currentLevel, ServerLevel destination, float yaw, Function<Boolean, Entity> repositionEntity) {
        Entity placed = repositionEntity.apply(false);
        placed.moveTo(targetPos.getX() + 0.5D, targetPos.getY() + 0.1D, targetPos.getZ() + 0.5D, entity.getYRot(), entity.getXRot());
        return placed;
    }

    private static ServerLevel getDestinationLevel(ServerPlayer player) {
        MinecraftServer server = player.server;
        if (player.level().dimension() == ModDimensions.MINING_WORLD) {
            return server.getLevel(Level.OVERWORLD);
        }

        return server.getLevel(ModDimensions.MINING_WORLD);
    }

    private static BlockPos findTargetPos(ServerPlayer player, ServerLevel destination) {
        MiningWorldSettings.Values settings = MiningWorldSettings.get();
        double scale = destination.dimension() == ModDimensions.MINING_WORLD ? settings.coordinateScale() : 1.0D / settings.coordinateScale();
        int x = floorClamped(player.getX() * scale);
        int z = floorClamped(player.getZ() * scale);

        if (destination.dimension() == ModDimensions.MINING_WORLD) {
            int y = Math.max(destination.getMinBuildHeight() + 2, Math.min(destination.getMaxBuildHeight() - 2, settings.arrivalY()));
            return new BlockPos(x, y, z);
        }

        int surfaceY = destination.getHeight(Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, x, z);
        int y = Math.max(destination.getMinBuildHeight() + 2, Math.min(destination.getMaxBuildHeight() - 2, surfaceY));
        return new BlockPos(x, y, z);
    }

    private static void createPlatform(ServerLevel level, BlockPos center) {
        BlockState frame = ModBlocks.PORTAL_FRAME.get().defaultBlockState();
        BlockState air = Blocks.AIR.defaultBlockState();

        for (BlockPos pos : BlockPos.betweenClosed(center.offset(-2, -1, -2), center.offset(2, -1, 2))) {
            level.setBlock(pos, frame, 3);
        }

        for (BlockPos pos : BlockPos.betweenClosed(center.offset(-1, 0, -1), center.offset(1, 2, 1))) {
            level.setBlock(pos, air, 3);
        }
    }

    private static int floorClamped(double value) {
        if (value > 29_999_000.0D) {
            return 29_999_000;
        }

        if (value < -29_999_000.0D) {
            return -29_999_000;
        }

        return (int)Math.floor(value);
    }
}
