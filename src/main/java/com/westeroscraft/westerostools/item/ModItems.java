package com.westeroscraft.westerostools.item;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Function;

import com.sk89q.worldedit.command.tool.DoubleActionBlockTool;

import com.westeroscraft.westerostools.WesterosTools;
import com.westeroscraft.westerostools.tools.BlockDataCycler;
import com.westeroscraft.westerostools.tools.Extrude;
import com.westeroscraft.westerostools.tools.Paint;
import com.westeroscraft.westerostools.tools.chisel.Chisel;

import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;

/**
 * Registers the dedicated WesterosTools tool items and owns the per-player tool
 * state each item dispatches to.
 *
 * Cycler/Extrude/Chisel can share a single tool instance: the cycler keeps its
 * per-player selection in a UUID-keyed map internally and the other two are
 * stateless. Paint keeps its selected block set / radius in instance fields, so
 * each player gets their own Paint instance.
 */
public class ModItems {

    private static WesterosTools wt;

    private static final BlockDataCycler CYCLER_TOOL = new BlockDataCycler();
    private static final Extrude EXTRUDE_TOOL = new Extrude();
    private static Chisel chiselTool;  // needs the mod instance; built in initialize()

    private static final Map<UUID, Paint> PAINT_TOOLS = new ConcurrentHashMap<>();

    /** Get (or lazily create) the per-player Paint tool used by the paint item. */
    public static Paint paintFor(UUID id) {
        return PAINT_TOOLS.computeIfAbsent(id, k -> new Paint(wt));
    }

    /** Release a player's Paint tool on disconnect so the map doesn't grow unbounded. */
    public static void release(UUID id) {
        PAINT_TOOLS.remove(id);
    }

    public static final Item CYCLER  = register("cycler",  id -> CYCLER_TOOL);
    public static final Item EXTRUDE = register("extrude", id -> EXTRUDE_TOOL);
    public static final Item CHISEL  = register("chisel",  id -> chiselTool);
    public static final Item PAINT   = register("paint",   ModItems::paintFor);

    private static Item register(String name, Function<UUID, DoubleActionBlockTool> toolFor) {
        return Registry.register(
            BuiltInRegistries.ITEM,
            ResourceLocation.fromNamespaceAndPath(WesterosTools.MOD_ID, name),
            new ToolItem(new Item.Properties().stacksTo(1), toolFor));
    }

    /**
     * Force class load (running the static registrations) and wire up the tools
     * that need the mod instance. Call from {@link WesterosTools#onInitialize()}.
     */
    public static void initialize(WesterosTools mod) {
        wt = mod;
        chiselTool = new Chisel(mod);
    }

    private ModItems() {}
}
