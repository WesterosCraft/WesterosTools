package com.westeroscraft.westerostools.tools;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import javax.annotation.Nullable;

import com.sk89q.worldedit.LocalConfiguration;
import com.sk89q.worldedit.LocalSession;
import com.sk89q.worldedit.WorldEdit;
import com.sk89q.worldedit.command.tool.DoubleActionBlockTool;
import com.sk89q.worldedit.entity.Player;
import com.sk89q.worldedit.extension.platform.Capability;
import com.sk89q.worldedit.extension.platform.Platform;
import com.sk89q.worldedit.fabric.FabricAdapter;
import com.sk89q.worldedit.util.Location;
import com.sk89q.worldedit.util.formatting.text.TextComponent;

import com.westeroscraft.westerostools.WesterosTools;
import com.westeroscraft.westerostools.WorldEditBridge;
import com.westeroscraft.westerostools.item.ToolDispatcher;
import com.westeroscraft.westerostools.item.ToolType;
import com.westeroscraft.westerostools.tools.chisel.Chisel;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerPlayer;

/**
 * WorldEdit-backed implementation of {@link ToolDispatcher}. Because it
 * references WorldEdit types, this class is only loaded/instantiated on the
 * server once WorldEdit is confirmed present (see {@link WorldEditBridge#init}).
 *
 * <p>Cycler/Extrude/Chisel share a single tool instance each: the cycler keeps
 * its per-player selection in a UUID-keyed map internally and the other two are
 * stateless. Paint keeps its selected block set / radius in instance fields, so
 * each player gets their own Paint instance, released on disconnect.
 */
public class WorldEditToolDispatcher implements ToolDispatcher {

    private final WesterosTools wt;
    private final BlockDataCycler cycler = new BlockDataCycler();
    private final Extrude extrude = new Extrude();
    private final Chisel chisel;
    private final Map<UUID, Paint> paintTools = new ConcurrentHashMap<>();

    public WorldEditToolDispatcher(WesterosTools mod) {
        this.wt = mod;
        this.chisel = new Chisel(mod);
    }

    private Paint paintFor(UUID id) {
        return paintTools.computeIfAbsent(id, k -> new Paint(wt));
    }

    private DoubleActionBlockTool toolFor(ToolType type, UUID id) {
        return switch (type) {
            case CYCLER -> cycler;
            case EXTRUDE -> extrude;
            case CHISEL -> chisel;
            case PAINT -> paintFor(id);
        };
    }

    @Override
    public boolean configurePaint(ServerPlayer sp, @Nullable String set, double radius) {
        Paint paint = paintFor(sp.getUUID());
        paint.setRadius(radius);
        if (set != null) {
            paint.updateSet(FabricAdapter.adaptPlayer(sp), set);
        }
        return true;
    }

    @Override
    public void release(UUID id) {
        paintTools.remove(id);
    }

    @Override
    public boolean dispatch(ServerPlayer sp, BlockPos pos, @Nullable Direction mcFace, boolean primary, ToolType type) {
        WorldEdit worldEdit = WorldEditBridge.worldEdit;
        if (worldEdit == null) {
            return false;
        }
        Player player = FabricAdapter.adaptPlayer(sp);
        DoubleActionBlockTool tool = toolFor(type, sp.getUUID());
        if (!tool.canUse(player)) {
            player.printError(TextComponent.of("You do not have access to this tool"));
            return false;
        }

        LocalSession session = worldEdit.getSessionManager().get(player);
        Platform platform = worldEdit.getPlatformManager().queryCapability(Capability.WORLD_EDITING);
        LocalConfiguration config = platform.getConfiguration();

        com.sk89q.worldedit.util.Direction face = (mcFace == null) ? null : FabricAdapter.adaptEnumFacing(mcFace);
        Location clicked = new Location(player.getWorld(), pos.getX(), pos.getY(), pos.getZ());

        return primary
            ? tool.actPrimary(platform, config, player, session, clicked, face)
            : tool.actSecondary(platform, config, player, session, clicked, face);
    }
}
