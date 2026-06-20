package net.colourlabs.dimensionalworldreborn.world;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.server.level.WorldGenRegion;
import net.minecraft.world.level.LevelHeightAccessor;
import net.minecraft.world.level.NoiseColumn;
import net.minecraft.world.level.StructureManager;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.biome.BiomeManager;
import net.minecraft.world.level.biome.BiomeSource;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.chunk.ChunkAccess;
import net.minecraft.world.level.chunk.ChunkGenerator;
import net.minecraft.world.level.levelgen.GenerationStep;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.level.levelgen.NoiseBasedChunkGenerator;
import net.minecraft.world.level.levelgen.NoiseGeneratorSettings;
import net.minecraft.world.level.levelgen.RandomState;
import net.minecraft.world.level.levelgen.blending.Blender;

import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;

public class MiningChunkGenerator extends ChunkGenerator {
    public static final Codec<MiningChunkGenerator> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            BiomeSource.CODEC.fieldOf("biome_source").forGetter(MiningChunkGenerator::getBiomeSource),
            NoiseGeneratorSettings.CODEC.fieldOf("settings").forGetter(MiningChunkGenerator::getSettings))
            .apply(instance, MiningChunkGenerator::new));

    private final BiomeSource biomeSource;
    private final Holder<NoiseGeneratorSettings> settings;
    private final NoiseBasedChunkGenerator delegate;

    public MiningChunkGenerator(BiomeSource biomeSource, Holder<NoiseGeneratorSettings> settings) {
        super(biomeSource);
        this.biomeSource = biomeSource;
        this.settings = settings;

        NoiseGeneratorSettings original = settings.value();
        NoiseGeneratorSettings customSettings = new NoiseGeneratorSettings(
                original.noiseSettings(),
                original.defaultBlock(),
                original.defaultFluid(),
                original.noiseRouter(),
                original.surfaceRule(),
                original.spawnTarget(),
                -64, // seaLevel
                original.disableMobGeneration(),
                false, // aquifersEnabled
                original.oreVeinsEnabled(),
                original.useLegacyRandomSource());

        this.delegate = new NoiseBasedChunkGenerator(biomeSource, Holder.direct(customSettings));
    }

    public BiomeSource getBiomeSource() {
        return this.biomeSource;
    }

    public Holder<NoiseGeneratorSettings> getSettings() {
        return this.settings;
    }

    @Override
    protected Codec<? extends ChunkGenerator> codec() {
        return CODEC;
    }

    @Override
    public void buildSurface(WorldGenRegion region, StructureManager structureManager, RandomState randomState,
            ChunkAccess chunk) {
        BlockState grass = Blocks.GRASS_BLOCK.defaultBlockState();
        BlockState dirt = Blocks.DIRT.defaultBlockState();
        BlockPos.MutableBlockPos mutablePos = new BlockPos.MutableBlockPos();
        for (int x = 0; x < 16; x++) {
            for (int z = 0; z < 16; z++) {
                chunk.setBlockState(mutablePos.set(x, 70, z), grass, false);
                chunk.setBlockState(mutablePos.set(x, 69, z), dirt, false);
                chunk.setBlockState(mutablePos.set(x, 68, z), dirt, false);
                chunk.setBlockState(mutablePos.set(x, 67, z), dirt, false);
            }
        }
    }

    @Override
    public CompletableFuture<ChunkAccess> fillFromNoise(Executor executor, Blender blender, RandomState randomState,
            StructureManager structureManager, ChunkAccess chunk) {
        BlockState stone = Blocks.STONE.defaultBlockState();
        BlockState bedrock = Blocks.BEDROCK.defaultBlockState();
        BlockPos.MutableBlockPos mutablePos = new BlockPos.MutableBlockPos();
        for (int x = 0; x < 16; x++) {
            for (int z = 0; z < 16; z++) {
                chunk.setBlockState(mutablePos.set(x, 0, z), bedrock, false);
                for (int y = 1; y <= 70; y++) {
                    chunk.setBlockState(mutablePos.set(x, y, z), stone, false);
                }
            }
        }
        return CompletableFuture.completedFuture(chunk);
    }

    @Override
    public int getBaseHeight(int x, int z, Heightmap.Types type, LevelHeightAccessor level, RandomState randomState) {
        return 70;
    }

    @Override
    public NoiseColumn getBaseColumn(int x, int z, LevelHeightAccessor level, RandomState randomState) {
        BlockState[] states = new BlockState[71];
        states[0] = Blocks.BEDROCK.defaultBlockState();
        for (int i = 1; i <= 70; i++) {
            states[i] = Blocks.STONE.defaultBlockState();
        }
        return new NoiseColumn(0, states);
    }

    @Override
    public void addDebugScreenInfo(List<String> info, RandomState randomState, BlockPos pos) {
    }

    @Override
    public void applyCarvers(WorldGenRegion level, long seed, RandomState randomState,
            BiomeManager biomeManager, StructureManager structureManager,
            ChunkAccess chunk, GenerationStep.Carving step) {
        this.delegate.applyCarvers(level, seed, randomState, biomeManager, structureManager, chunk, step);

        // strip any fluid blocks the carver placed
        // this is a mining dimension after all Loo
        BlockPos.MutableBlockPos mutablePos = new BlockPos.MutableBlockPos();
        int minY = chunk.getMinBuildHeight();
        int maxY = chunk.getMaxBuildHeight();
        int chunkX = chunk.getPos().getMinBlockX();
        int chunkZ = chunk.getPos().getMinBlockZ();

        for (int x = 0; x < 16; x++) {
            for (int z = 0; z < 16; z++) {
                for (int y = minY; y < maxY; y++) {
                    mutablePos.set(chunkX + x, y, chunkZ + z);
                    BlockState state = chunk.getBlockState(mutablePos);
                    if (state.is(Blocks.WATER) || state.is(Blocks.LAVA)) {
                        chunk.setBlockState(mutablePos, Blocks.AIR.defaultBlockState(), false);
                    }
                }
            }
        }
    }

    @Override
    public void applyBiomeDecoration(WorldGenLevel level, ChunkAccess chunk, StructureManager structureManager) {
        this.delegate.applyBiomeDecoration(level, chunk, structureManager);
    }

    @Override
    public void spawnOriginalMobs(WorldGenRegion level) {
        this.delegate.spawnOriginalMobs(level);
    }

    @Override
    public int getGenDepth() {
        return this.delegate.getGenDepth();
    }

    @Override
    public int getSeaLevel() {
        return this.delegate.getSeaLevel();
    }

    @Override
    public int getMinY() {
        return this.delegate.getMinY();
    }
}
