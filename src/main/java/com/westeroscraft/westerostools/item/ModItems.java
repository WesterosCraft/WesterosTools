package com.westeroscraft.westerostools.item;

import com.westeroscraft.westerostools.WesterosTools;

import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;

/**
 * Registers the dedicated WesterosTools tool items. Each item only carries a
 * {@link ToolType} tag; the WorldEdit tool it drives is resolved server-side by
 * the dispatcher installed on {@link ToolItem}. This keeps registration (which
 * runs on both client and server) free of WorldEdit types.
 */
public class ModItems {

    public static final Item CYCLER  = register("cycler",  ToolType.CYCLER);
    public static final Item EXTRUDE = register("extrude", ToolType.EXTRUDE);
    public static final Item CHISEL  = register("chisel",  ToolType.CHISEL);
    public static final Item PAINT   = register("paint",   ToolType.PAINT);

    private static Item register(String name, ToolType type) {
        return Registry.register(
            BuiltInRegistries.ITEM,
            ResourceLocation.fromNamespaceAndPath(WesterosTools.MOD_ID, name),
            new ToolItem(new Item.Properties().stacksTo(1), type));
    }

    /**
     * Force class load (running the static registrations above).
     * Call from {@link WesterosTools#onInitialize()}.
     */
    public static void initialize() {}

    private ModItems() {}
}
