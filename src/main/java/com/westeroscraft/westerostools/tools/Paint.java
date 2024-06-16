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

import java.util.HashMap;
import java.util.Map;
import javax.annotation.Nullable;
 
/**
 * A mode that allows blocks to be painted with a given block set,
 * preserving the original block variant and state.
*/
public class Paint implements DoubleActionBlockTool {
    private WesterosTools wt;
    private String selectedSet = null;
    private boolean selectedSingleton = false;

    public Paint(WesterosTools westerostools) {
        wt = westerostools;
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

    private boolean handlePaint(LocalConfiguration config, Player player, LocalSession session, Location clicked) {

        World world = (World) clicked.getExtent();

        BlockVector3 blockPoint = clicked.toVector().toBlockPoint();
        BaseBlock block = world.getFullBlock(blockPoint);
        String fromId = block.getBlockType().getId();
        Variant fromVariant = wt.getBlockVariant(fromId);
        Map<Property<?>, Object> fromStates = block.getStates();

        if (selectedSet == null) {
            player.printError(TextComponent.of("Paint material not selected"));
            return true;
        }
        if (fromVariant == null) {
            fromVariant = wt.inferBlockVariant(fromId);
            // player.printError(TextComponent.of("Block cannot be painted on since it doesn't belong to a set"));
            // return true;
        }

        String toId = (selectedSingleton) ? selectedSet : wt.getTargetId(selectedSet, fromVariant);

        if (toId == null) {
            player.printError(TextComponent.of("Block cannot be painted on since variant '" + fromVariant.toString() + "' does not exist for set '" + selectedSet + "'"));
            return true;
        }

        
        // Create new block with target type and default state
        BlockType newBlockType = BlockTypes.get(toId);
        if (newBlockType == null) {
            player.printError(TextComponent.of("Unknown block ID '" + toId + "'"));
            return true;
        }
        BlockState newBlockState = newBlockType.getDefaultState();

        // Copy properties when possible
        Map<Property<?>, Object> toStates = new HashMap<>();
        @SuppressWarnings("unchecked")
        Map<String, Property<Object>> propertyMap = (Map<String, Property<Object>>) newBlockType.getPropertyMap();
        for (Property<?> prop : fromStates.keySet()) {
            if (propertyMap.containsKey(prop.getName())) {
                Property<Object> propertyKey = propertyMap.get(prop.getName());
                if (propertyKey.getValues().contains(fromStates.get(prop))) {
                    toStates.put(prop, fromStates.get(prop));
                }
            }
        }

        // Assign blockstate from properties and create new base block
        for (Map.Entry<Property<?>, Object> state : toStates.entrySet()) {
            @SuppressWarnings("unchecked")
            Property<Object> objProp = (Property<Object>) state.getKey();
            newBlockState = newBlockState.with(objProp, state.getValue());
        }
        BaseBlock newBlock = newBlockState.toBaseBlock();

        // Apply change
        try (EditSession editSession = session.createEditSession(player)) {
            editSession.disableBuffering();

            try {
                editSession.setBlock(blockPoint, newBlock);
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

        String id = block.getBlockType().getId();
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
