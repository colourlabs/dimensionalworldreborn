package net.colourlabs.dimensionalworldreborn.item;

import net.colourlabs.dimensionalworldreborn.block.MiningPortalBlock;
import net.colourlabs.dimensionalworldreborn.registry.ModBlocks;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;

public class MiningMultitoolItem extends Item {
    public MiningMultitoolItem(Properties properties) {
        super(properties.durability(24));
    }

    @Override
    public InteractionResult useOn(UseOnContext context) {
        Level level = context.getLevel();
        BlockPos clickedPos = context.getClickedPos();
        BlockState clickedState = level.getBlockState(clickedPos);

        if (!clickedState.is(ModBlocks.PORTAL_FRAME.get())) {
            return InteractionResult.PASS;
        }

        BlockPos portalPos = clickedPos.relative(context.getClickedFace());
        for (Direction.Axis axis : Direction.Axis.values()) {
            MiningPortalBlock.PortalAxis portalAxis = MiningPortalBlock.PortalAxis.fromDirectionAxis(axis);
            MiningPortalBlock.PortalSize size = new MiningPortalBlock.PortalSize(level, portalPos, portalAxis);
            if (size.isValid()) {
                if (!level.isClientSide()) {
                    size.placePortalBlocks();
                    ItemStack stack = context.getItemInHand();
                    if (context.getPlayer() instanceof ServerPlayer player) {
                        stack.hurtAndBreak(1, player, brokenPlayer -> brokenPlayer.broadcastBreakEvent(context.getHand()));
                        player.displayClientMessage(Component.translatable("dimensionalworldreborn.info.portal_info"), false);
                    } else {
                        stack.setDamageValue(stack.getDamageValue() + 1);
                    }
                }

                return InteractionResult.sidedSuccess(level.isClientSide());
            }
        }

        return InteractionResult.PASS;
    }
}
