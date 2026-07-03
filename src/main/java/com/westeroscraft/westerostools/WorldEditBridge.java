package com.westeroscraft.westerostools;

import com.sk89q.worldedit.WorldEdit;
import com.sk89q.worldedit.extension.platform.Actor;
import com.sk89q.worldedit.fabric.FabricAdapter;

import com.westeroscraft.westerostools.item.ToolItem;
import com.westeroscraft.westerostools.tools.WorldEditToolDispatcher;

import net.minecraft.commands.CommandSourceStack;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;

/**
 * All WorldEdit access lives here. This class is only referenced from code paths
 * that run after WorldEdit is confirmed present (server start and server-side
 * command execution), so it — and the WorldEdit classes it links against — never
 * load on a client that ships without WorldEdit. Keeping these references out of
 * the {@code main} entrypoint and item-registration classes is what lets the mod
 * load client-side with WorldEdit installed only on the server.
 */
public final class WorldEditBridge {

    /** The WorldEdit singleton, resolved on server start. Null until {@link #init} runs. */
    public static WorldEdit worldEdit;

    /** Called on server start once WorldEdit is confirmed present. */
    public static void init(WesterosTools mod) {
        worldEdit = WorldEdit.getInstance();
        ToolItem.setDispatcher(new WorldEditToolDispatcher(mod));
    }

    /**
     * Validate that the command source is a server player and has permission for
     * the given group; otherwise send a failure message and return null.
     */
    public static Actor validateActor(CommandSourceStack source, String permissionGroup) {
        if (source.getEntity() instanceof ServerPlayer player) {
            Actor actor = FabricAdapter.adaptPlayer(player);

            // Test for command access
            if (permissionGroup != null && !actor.hasPermission(permissionGroup)) {
                source.sendFailure(Component.literal("You do not have access to this command"));
                return null;
            }

            return actor;
        }

        source.sendFailure(Component.literal("Only usable by server player"));
        return null;
    }

    private WorldEditBridge() {}
}
