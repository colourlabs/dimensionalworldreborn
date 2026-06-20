package net.colourlabs.dimensionalworldreborn.registry;

import com.mojang.serialization.Codec;
import net.colourlabs.dimensionalworldreborn.DimensionalWorldReborn;
import net.colourlabs.dimensionalworldreborn.world.MiningChunkGenerator;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.level.chunk.ChunkGenerator;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.RegistryObject;

public class ModChunkGenerators {
    public static final DeferredRegister<Codec<? extends ChunkGenerator>> CHUNK_GENERATORS =
        DeferredRegister.create(Registries.CHUNK_GENERATOR, DimensionalWorldReborn.MODID);

    public static final RegistryObject<Codec<MiningChunkGenerator>> MINING_CHUNK_GENERATOR =
        CHUNK_GENERATORS.register("mining", () -> MiningChunkGenerator.CODEC);
}
