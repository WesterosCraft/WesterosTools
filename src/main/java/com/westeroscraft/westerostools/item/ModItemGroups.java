package com.westeroscraft.westerostools.item;

import com.westeroscraft.westerostools.WesterosTools;

import net.fabricmc.fabric.api.itemgroup.v1.FabricItemGroup;

import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;

public class ModItemGroups {

    public static final ResourceLocation GROUP_ID =
        ResourceLocation.fromNamespaceAndPath(WesterosTools.MOD_ID, "tools");

    public static CreativeModeTab TOOLS_TAB;

    public static void initialize() {
        TOOLS_TAB = Registry.register(
            BuiltInRegistries.CREATIVE_MODE_TAB,
            GROUP_ID,
            FabricItemGroup.builder()
                .icon(() -> new ItemStack(ModItems.CYCLER))
                .title(Component.translatable("itemGroup.westerostools"))
                .displayItems((params, output) -> {
                    output.accept(ModItems.CYCLER);
                    output.accept(ModItems.EXTRUDE);
                    output.accept(ModItems.PAINT);
                    output.accept(ModItems.CHISEL);
                })
                .build());
    }

    private ModItemGroups() {}
}
