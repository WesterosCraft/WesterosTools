package com.westeroscraft.westerostools.tools;

import com.sk89q.worldedit.EditSession;
import com.sk89q.worldedit.LocalConfiguration;
import com.sk89q.worldedit.LocalSession;
import com.sk89q.worldedit.MaxChangedBlocksException;
import com.sk89q.worldedit.entity.Player;
import com.sk89q.worldedit.extension.platform.Actor;
import com.sk89q.worldedit.extension.platform.Platform;
import com.sk89q.worldedit.math.BlockVector3;
import com.sk89q.worldedit.registry.state.Property;
import com.sk89q.worldedit.util.Direction;
import com.sk89q.worldedit.util.Location;
import com.sk89q.worldedit.util.formatting.text.TextComponent;
import com.sk89q.worldedit.util.formatting.text.TranslatableComponent;
import com.sk89q.worldedit.world.World;
import com.sk89q.worldedit.world.block.BaseBlock;
import com.sk89q.worldedit.world.block.BlockType;
import com.sk89q.worldedit.world.block.BlockTypes;
import com.sk89q.worldedit.world.block.BlockState;
import com.sk89q.worldedit.command.tool.DoubleActionBlockTool;

import com.westeroscraft.westerostools.WesterosTools;

import static com.westeroscraft.westerostools.BlockDef.*;

import java.util.EnumSet;
import java.util.HashMap;
import java.util.Map;
import javax.annotation.Nullable;
 
/**
 * A mode that allows blocks to be painted with a given block set,
 * preserving the original block variant and state.
*/
public class Paint implements DoubleActionBlockTool {
    private static final EnumSet<Variant> RADIUS_PAINTABLE_VARIANTS = EnumSet.of(
        Variant.SOLID, Variant.STAIRS, Variant.SLAB, Variant.WALL, Variant.FENCE
    );

    private WesterosTools wt;
    private String selectedId = null;
    private String selectedSet = null;
    private boolean selectedSingleton = false;
    private double radius = 1.0;

    public Paint(WesterosTools westerostools) {
        wt = westerostools;
    }

    public void setRadius(double radius) {
        this.radius = radius;
    }

    @Override
    public boolean canUse(Actor player) {
        return player.hasPermission("westerostools.paint");
    }

    public void updateSet(Actor player, String set) {
        updateSet(player, set, false);
    }

    public void updateSet(Actor player, String set, boolean singleton) {
        selectedSet = set;
        selectedSingleton = singleton;
    }

    /* Computes the painted block for a given position. When strict=true, returns null
     * for blocks not explicitly in the blockset mapping (used for radius mode).
     */
    private BaseBlock computePaintedBlock(World world, BlockVector3 blockPoint, boolean strict) {
        BaseBlock block = world.getFullBlock(blockPoint);
        String fromId = block.getBlockType().id();
        Variant fromVariant = wt.getBlockVariant(fromId);
        Map<Property<?>, Object> fromStates = block.getStates();

        if (fromVariant == null) {
            if (strict) {
                // Radius mode: require explicit keyword match, no SOLID default (avoids painting air etc.)
                Variant inferred = wt.tryInferBlockVariant(fromId);
                if (inferred == null || !RADIUS_PAINTABLE_VARIANTS.contains(inferred)) return null;
                fromVariant = inferred;
            } else {
                fromVariant = wt.inferBlockVariant(fromId); // defaults to SOLID for single-block
            }
        }

        String toId = (selectedSingleton) ? selectedSet : wt.getTargetId(selectedSet, fromVariant);
        if (toId == null) {
            if (selectedId == null || strict) return null;
            toId = selectedId;
        }

        BlockType newBlockType = BlockTypes.get(toId);
        if (newBlockType == null) return null;
        BlockState newBlockState = newBlockType.getDefaultState();

        // Copy properties when possible
        Map<Property<?>, Object> toStates = new HashMap<>();
        @SuppressWarnings("unchecked")
        Map<String, Property<Object>> propertyMap = (Map<String, Property<Object>>) newBlockType.getPropertyMap();
        for (Property<?> prop : fromStates.keySet()) {
            if (propertyMap.containsKey(prop.getName())) {
                Property<Object> propertyKey = propertyMap.get(prop.getName());
                if (propertyKey.getValues().contains(fromStates.get(prop))) {
                    toStates.put(propertyKey, fromStates.get(prop));
                }
            }
        }
        for (Map.Entry<Property<?>, Object> state : toStates.entrySet()) {
            @SuppressWarnings("unchecked")
            Property<Object> objProp = (Property<Object>) state.getKey();
            newBlockState = newBlockState.with(objProp, state.getValue());
        }
        return newBlockState.toBaseBlock();
    }

    private boolean handlePaint(LocalConfiguration config, Player player, LocalSession session, Location clicked) {
        if (selectedSet == null) {
            player.printError(TextComponent.of("Paint material not selected"));
            return true;
        }
        if (radius > 1.0 && config.maxRadius >= 0 && radius > config.maxRadius) {
            player.printError(TextComponent.of("Radius " + radius + " exceeds maximum allowed radius of " + config.maxRadius));
            return true;
        }

        World world = (World) clicked.getExtent();
        BlockVector3 center = clicked.toVector().toBlockPoint();

        try (EditSession editSession = session.createEditSession(player)) {
            editSession.disableBuffering();
            try {
                if (radius <= 1.0) {
                    // Attempt to resolve variant first
                    BaseBlock block = world.getFullBlock(center);
                    String fromId = block.getBlockType().id();
                    Variant fromVariant = wt.getBlockVariant(fromId);
                    if (fromVariant == null) fromVariant = wt.inferBlockVariant(fromId);

                    if (fromVariant == null) {
                        // Block doesn't belong to any known set
                    } else {
                        BaseBlock newBlock = computePaintedBlock(world, center, false);
                        if (newBlock == null) {
                            player.printError(TextComponent.of("Block cannot be painted on since variant '" + fromVariant + "' does not exist for set '" + selectedSet + "'"));
                        } else {
                            editSession.setBlock(center, newBlock);
                        }
                    }
                } else {
                    // Radius mode — only paint blocks explicitly in the blockset mapping
                    int r = (int) Math.ceil(radius);
                    double r2 = radius * radius;
                    for (int dx = -r; dx <= r; dx++) {
                        for (int dy = -r; dy <= r; dy++) {
                            for (int dz = -r; dz <= r; dz++) {
                                if (dx*dx + dy*dy + dz*dz <= r2) {
                                    BlockVector3 pt = center.add(dx, dy, dz);
                                    BaseBlock newBlock = computePaintedBlock(world, pt, true);
                                    if (newBlock != null) {
                                        editSession.setBlock(pt, newBlock);
                                    }
                                }
                            }
                        }
                    }
                }
            } catch (MaxChangedBlocksException e) {
                player.printError(TranslatableComponent.of("worldedit.tool.max-block-changes"));
            } finally {
                session.remember(editSession);
            }
        }

        return true;
    }

    private boolean handleSelect(LocalConfiguration config, Player player, LocalSession session, Location clicked) {

        World world = (World) clicked.getExtent();

        BlockVector3 blockPoint = clicked.toVector().toBlockPoint();
        BaseBlock block = world.getFullBlock(blockPoint);

        String id = block.getBlockType().id();
        selectedId = id;
        String setname = wt.getBlockSet(id);

        if (setname == null) {
            updateSet(player, id, true);
            player.printInfo(TextComponent.of("Selecting singleton block '" + selectedSet + "'"));
            // player.printError(TextComponent.of("Block '" + id + "' does not belong to a block set"));
            // player.printDebug(TextComponent.of("Tip: use '/tool repl <id>' or '/tool repl ^<id>' for individual blocks"));
            // return true;
        }
        else {
            updateSet(player, setname, false);
            player.printInfo(TextComponent.of("Selecting block set '" + selectedSet + "'"));
        }

        return true;
    }

    @Override
    public boolean actPrimary(Platform server, LocalConfiguration config, Player player, LocalSession session, Location clicked, @Nullable Direction face) {
        return handlePaint(config, player, session, clicked);
    }

    @Override
    public boolean actSecondary(Platform server, LocalConfiguration config, Player player, LocalSession session, Location clicked, @Nullable Direction face) {
        return handleSelect(config, player, session, clicked);
    }

}