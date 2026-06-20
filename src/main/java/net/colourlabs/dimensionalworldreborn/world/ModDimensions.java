package net.colourlabs.dimensionalworldreborn.world;

import net.colourlabs.dimensionalworldreborn.DimensionalWorldReborn;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.dimension.DimensionType;

public class ModDimensions {
    public static final ResourceLocation MINING_WORLD_ID = new ResourceLocation(DimensionalWorldReborn.MODID, "mining_world");

    public static final ResourceKey<Level> MINING_WORLD = ResourceKey.create(Registries.DIMENSION, MINING_WORLD_ID);
    public static final ResourceKey<DimensionType> MINING_WORLD_TYPE = ResourceKey.create(Registries.DIMENSION_TYPE, MINING_WORLD_ID);
}
