package com.westeroscraft.westerostools.tools.chisel;

import com.sk89q.worldedit.EditSession;
import com.sk89q.worldedit.LocalConfiguration;
import com.sk89q.worldedit.LocalSession;
import com.sk89q.worldedit.MaxChangedBlocksException;
import com.sk89q.worldedit.entity.Player;
import com.sk89q.worldedit.extension.platform.Actor;
import com.sk89q.worldedit.extension.platform.Platform;
import com.sk89q.worldedit.math.BlockVector3;
import com.sk89q.worldedit.registry.state.BooleanProperty;
import com.sk89q.worldedit.registry.state.Property;
import com.sk89q.worldedit.util.Direction;
import com.sk89q.worldedit.util.Location;
import com.sk89q.worldedit.util.formatting.text.TextComponent;
import com.sk89q.worldedit.world.World;
import com.sk89q.worldedit.world.block.BaseBlock;
import com.sk89q.worldedit.world.block.BlockState;
import com.sk89q.worldedit.world.block.BlockType;
import com.sk89q.worldedit.world.block.BlockTypes;
import com.sk89q.worldedit.command.tool.DoubleActionBlockTool;

import com.westeroscraft.westerostools.BlockDef.Variant;
import com.westeroscraft.westerostools.WesterosTools;
import com.westeroscraft.westerostools.tools.chisel.ChiselHelper.UVBin;
import com.westeroscraft.westerostools.tools.chisel.ChiselHelper.Transition;

import java.util.List;
import java.util.Map;
import javax.annotation.Nullable;

/**
 * A tool that "chisels" a block into a related variant based on where the player
 * clicks on the face.  All logic is encoded in the transition tables in
 * ChiselTransitions and SculptTransitions:
 *
 *   (face, uBin, vBin, fromVariant, fromStatePattern) -> (toVariant, toState)
 *
 * NORTH face 3x3 grid (uBin x vBin):
 *
 *   HIGH | stair outer_left(W,top) | stair(N,top)    | stair outer_right(E,top) |
 *   MID  | stair(W)                | wall(E+W)       | stair(E)                 |
 *   LOW  | stair outer_right(W)    | stair(N,bot)    | stair outer_left(E)      |
 *          u=LOW(east)               u=MID             u=HIGH(west)
 */
public class Chisel implements DoubleActionBlockTool {

    private WesterosTools wt;

    public Chisel(WesterosTools westerostools) {
        wt = westerostools;
    }

    @Override
    public boolean canUse(Actor player) {
        return player.hasPermission("westerostools.chisel");
    }

    // -----------------------------------------------------------------------
    // Transition application
    // -----------------------------------------------------------------------

    /**
     * Apply a matched transition: look up the target block type from the
     * source block's blockset, then apply the toState overrides.
     */
    @Nullable
    private BaseBlock applyTransition(World world, BlockVector3 pos,
                                      BaseBlock fromBlock, Transition tx) {
        var fromSnap = ChiselHelper.stateSnapshot(fromBlock);
        List<String> propList = fromSnap.entrySet().stream()
            .map(e -> e.getKey() + ":" + e.getValue())
            .toList();
        String setName = wt.getBlockSet(fromBlock.getBlockType().id(), propList);
        if (setName == null) return null;  // block not in any known set

        String toId = wt.getTargetId(setName, tx.toVariant());
        if (toId == null) return null;     // set has no variant of this type

        BlockType toType = BlockTypes.get(toId);
        if (toType == null) return null;

        // Start from the target block's default state, then apply overrides
        BlockState state = toType.getDefaultState();
        @SuppressWarnings("unchecked")
        var propMap = (Map<String, Property<Object>>) toType.getPropertyMap();

        // Carry over source properties not explicitly set by toState
        for (var e : fromSnap.entrySet()) {
            if (tx.toState().containsKey(e.getKey())) continue;
            Property<Object> prop = propMap.get(e.getKey());
            if (prop == null) continue;
            Object val = prop.getValueFor(e.getValue());
            if (val != null) state = state.with(prop, val);
        }

        for (var e : tx.toState().entrySet()) {
            Property<Object> prop = propMap.get(e.getKey());
            if (prop == null) continue;
            String valStr = e.getValue();
            if (valStr.startsWith("{src.") && valStr.endsWith("}"))
                valStr = fromSnap.getOrDefault(valStr.substring(5, valStr.length() - 1), valStr);
            Object val = prop.getValueFor(valStr);
            if (val != null) state = state.with(prop, val);
        }

        // Set unconnect=true if the target block supports it
        Property<?> unconnectProp = state.getStates().keySet().stream()
            .filter(p -> p.getName().equals("unconnect")).findFirst().orElse(null);
        if (unconnectProp != null) {
            state = state.with((BooleanProperty) unconnectProp, Boolean.TRUE);
        }

        return state.toBaseBlock();
    }

    // -----------------------------------------------------------------------
    // DoubleActionBlockTool entry points
    // -----------------------------------------------------------------------

    private boolean handleChisel(List<Transition> table, Player player, LocalSession session,
                                  Location clicked, @Nullable Direction face) {
        if (face == null) return false;

        World world = (World) clicked.getExtent();
        BlockVector3 pos = clicked.toVector().toBlockPoint();
        BaseBlock block = world.getFullBlock(pos);

        double[] uv = ChiselHelper.computeFaceUV(player, pos, face);
        if (uv == null) return false;

        UVBin uBin = ChiselHelper.binCoord(uv[0]);
        UVBin vBin = ChiselHelper.binCoord(uv[1]);

        Variant fromVariant = wt.getBlockVariant(block.getBlockType().id());
        if (fromVariant == null) fromVariant = wt.inferBlockVariant(block.getBlockType().id());
        if (fromVariant == null) return false;
                                    
        // TODO: remove debug line
        player.printInfo(TextComponent.of("(Variant " + fromVariant.toString() + ") Clicked face=" + face.toString() + ", u=" + uBin.toString() + ", v=" + vBin.toString()));

        Map<String,String> fromState = ChiselHelper.stateSnapshot(block);

        for (Transition tx : table) {
            if (!tx.matches(face, uBin, vBin, fromVariant, fromState)) continue;

            BaseBlock newBlock = applyTransition(world, pos, block, tx);
            if (newBlock == null) continue;  // variant not in this block's set; try next match

            try (EditSession editSession = session.createEditSession(player)) {
                editSession.disableBuffering();
                try {
                    editSession.setBlock(pos, newBlock);
                } catch (MaxChangedBlocksException e) {
                    player.printError(com.sk89q.worldedit.util.formatting.text.TranslatableComponent
                        .of("worldedit.tool.max-block-changes"));
                } finally {
                    session.remember(editSession);
                }
            }
            return true;
        }

        player.printInfo(TextComponent.of("Could not find transition for fromState=" + fromState.toString()));
        return false;
    }

    /** Primary click - sculpt based on click zone. */
    @Override
    public boolean actPrimary(Platform server, LocalConfiguration config, Player player,
                              LocalSession session, Location clicked, @Nullable Direction face) {
        return handleChisel(SculptTransitions.LIST, player, session, clicked, face);
    }

    /** Secondary click - chisel based on click zone. */
    @Override
    public boolean actSecondary(Platform server, LocalConfiguration config, Player player,
                                LocalSession session, Location clicked, @Nullable Direction face) {
        return handleChisel(ChiselTransitions.LIST, player, session, clicked, face);
    }
}
