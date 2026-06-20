package net.colourlabs.dimensionalworldreborn;

import net.colourlabs.dimensionalworldreborn.registry.ModBlocks;
import net.colourlabs.dimensionalworldreborn.registry.ModCreativeTabs;
import net.colourlabs.dimensionalworldreborn.registry.ModItems;
import net.colourlabs.dimensionalworldreborn.registry.ModChunkGenerators;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@Mod(DimensionalWorldReborn.MODID)
public class DimensionalWorldReborn {
    public static final String MODID = "dimensionalworldreborn";
    public static final Logger LOGGER = LogManager.getLogger(MODID);

    public DimensionalWorldReborn() {
        IEventBus modEventBus = FMLJavaModLoadingContext.get().getModEventBus();

        ModBlocks.BLOCKS.register(modEventBus);
        ModItems.ITEMS.register(modEventBus);
        ModCreativeTabs.CREATIVE_TABS.register(modEventBus);
        ModChunkGenerators.CHUNK_GENERATORS.register(modEventBus);

        net.minecraftforge.common.MinecraftForge.EVENT_BUS.addListener(this::onAddReloadListeners);

        LOGGER.info("DimensionalWorldReborn initializing");
    }

    private void onAddReloadListeners(net.minecraftforge.event.AddReloadListenerEvent event) {
        event.addListener(new net.colourlabs.dimensionalworldreborn.world.MiningWorldSettings());
    }
}