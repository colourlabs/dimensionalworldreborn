package net.colourlabs.dimensionalworldreborn.registry;

import net.colourlabs.dimensionalworldreborn.DimensionalWorldReborn;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.RegistryObject;

public class ModCreativeTabs {
    public static final DeferredRegister<CreativeModeTab> CREATIVE_TABS =
        DeferredRegister.create(Registries.CREATIVE_MODE_TAB, DimensionalWorldReborn.MODID);

    public static final RegistryObject<CreativeModeTab> DIMENSIONAL_WORLD_TAB = CREATIVE_TABS.register("dimensional_world_tab",
        () -> CreativeModeTab.builder()
            .title(Component.translatable("itemGroup.dimensionalworldreborn"))
            .icon(() -> new ItemStack(ModItems.PORTAL_FRAME_ITEM.get()))
            .displayItems((params, output) -> {
                output.accept(ModBlocks.PORTAL_FRAME.get());
                output.accept(ModBlocks.STICKY_ORE.get());
                output.accept(ModBlocks.CLAY_ORE.get());
                output.accept(ModItems.MINING_MULTITOOL.get());
                output.accept(ModItems.DIMENSION_CHANGER.get());
            })
            .build());
}
