package net.colourlabs.dimensionalworldreborn;

import net.colourlabs.dimensionalworldreborn.registry.ModBlocks;
import net.colourlabs.dimensionalworldreborn.registry.ModCreativeTabs;
import net.colourlabs.dimensionalworldreborn.registry.ModItems;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.common.Mod;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@Mod(DimensionalWorldReborn.MODID)
public class DimensionalWorldReborn {
    public static final String MODID = "dimensionalworldreborn";
    public static final Logger LOGGER = LogManager.getLogger(MODID);

    public DimensionalWorldReborn(IEventBus modEventBus) {
        ModBlocks.BLOCKS.register(modEventBus);
        ModItems.ITEMS.register(modEventBus);
        ModCreativeTabs.CREATIVE_TABS.register(modEventBus);

        LOGGER.info("DimensionalWorldReborn initializing");
    }
}