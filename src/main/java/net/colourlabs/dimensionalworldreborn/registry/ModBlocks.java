package net.colourlabs.dimensionalworldreborn.registry;

import net.colourlabs.dimensionalworldreborn.DimensionalWorldReborn;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.material.MapColor;
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
}