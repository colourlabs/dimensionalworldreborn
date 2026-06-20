package net.colourlabs.dimensionalworldreborn.registry;

import net.colourlabs.dimensionalworldreborn.DimensionalWorldReborn;
import net.colourlabs.dimensionalworldreborn.block.MiningPortalBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.material.MapColor;
import net.minecraft.world.level.material.PushReaction;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class ModBlocks {
    public static final DeferredRegister<Block> BLOCKS =
        DeferredRegister.create(ForgeRegistries.BLOCKS, DimensionalWorldReborn.MODID);

    public static final RegistryObject<Block> PORTAL_FRAME = BLOCKS.register("portal_frame",
        () -> new Block(BlockBehaviour.Properties.of()
            .strength(1.5F)
            .mapColor(MapColor.STONE)));

    public static final RegistryObject<Block> MINING_PORTAL = BLOCKS.register("mining_portal",
        () -> new MiningPortalBlock(BlockBehaviour.Properties.of()
            .noCollission()
            .noOcclusion()
            .lightLevel(state -> 11)
            .strength(-1.0F)
            .pushReaction(PushReaction.BLOCK)
            .mapColor(MapColor.COLOR_PURPLE)));

    public static final RegistryObject<Block> STICKY_ORE = BLOCKS.register("sticky_ore",
        () -> new Block(BlockBehaviour.Properties.of()
            .strength(2.5F)
            .requiresCorrectToolForDrops()
            .mapColor(MapColor.STONE)));

    public static final RegistryObject<Block> CLAY_ORE = BLOCKS.register("clay_ore",
        () -> new Block(BlockBehaviour.Properties.of()
            .strength(2.5F)
            .requiresCorrectToolForDrops()
            .mapColor(MapColor.STONE)));
}
