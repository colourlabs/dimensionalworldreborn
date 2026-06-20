package net.colourlabs.dimensionalworldreborn.registry;

import net.colourlabs.dimensionalworldreborn.DimensionalWorldReborn;
import net.colourlabs.dimensionalworldreborn.item.DimensionChangerItem;
import net.colourlabs.dimensionalworldreborn.item.MiningMultitoolItem;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class ModItems {
    public static final DeferredRegister<Item> ITEMS =
        DeferredRegister.create(ForgeRegistries.ITEMS, DimensionalWorldReborn.MODID);

    public static final RegistryObject<Item> PORTAL_FRAME_ITEM = ITEMS.register("portal_frame",
        () -> new BlockItem(ModBlocks.PORTAL_FRAME.get(), new Item.Properties()));

    public static final RegistryObject<Item> STICKY_ORE_ITEM = ITEMS.register("sticky_ore",
        () -> new BlockItem(ModBlocks.STICKY_ORE.get(), new Item.Properties()));

    public static final RegistryObject<Item> CLAY_ORE_ITEM = ITEMS.register("clay_ore",
        () -> new BlockItem(ModBlocks.CLAY_ORE.get(), new Item.Properties()));

    public static final RegistryObject<Item> MINING_MULTITOOL = ITEMS.register("mining_multitool",
        () -> new MiningMultitoolItem(new Item.Properties()));

    public static final RegistryObject<Item> DIMENSION_CHANGER = ITEMS.register("dimension_changer",
        () -> new DimensionChangerItem(new Item.Properties()));
}
