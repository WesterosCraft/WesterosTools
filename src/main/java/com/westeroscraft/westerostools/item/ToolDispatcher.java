package com.westeroscraft.westerostools.item;

import java.util.UUID;
import javax.annotation.Nullable;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerPlayer;

/**
 * Server-side entry point for running a tool's action, kept free of WorldEdit
 * types so {@link ToolItem} can be registered and rendered on the client.
 *
 * <p>The concrete implementation ({@code WorldEditToolDispatcher}) is installed
 * on {@link ToolItem} at server start, but only once WorldEdit is confirmed
 * present. On the client (or a server without WorldEdit) no dispatcher is
 * installed and the tool items simply do nothing when used.
 */
public interface ToolDispatcher {

    /**
     * Run a tool's primary (right-click) or secondary (left-click) action.
     *
     * @return true if the action was handled
     */
    boolean dispatch(ServerPlayer player, BlockPos pos, @Nullable Direction face, boolean primary, ToolType type);

    /** Release any per-player state held for the given player (e.g. on disconnect). */
    void release(UUID id);
}
