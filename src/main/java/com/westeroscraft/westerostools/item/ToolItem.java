package com.westeroscraft.westerostools.item;

import java.util.List;
import java.util.UUID;
import javax.annotation.Nullable;

import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.context.UseOnContext;

/**
 * A custom item that drives one of the WesterosTools tools, identified by its
 * {@link ToolType}.
 *
 *   right-click (use)    -> tool primary action
 *   left-click  (attack) -> tool secondary action
 *
 * The item itself holds no WorldEdit types so it can be registered and rendered
 * on the client. All tool logic runs server-side through a {@link ToolDispatcher}
 * that is installed on server start only when WorldEdit is present; until then
 * (e.g. on the client) using the item is a no-op.
 */
public class ToolItem extends Item {

    /** Server-only, WorldEdit-backed dispatcher. Null on the client / without WorldEdit. */
    private static volatile ToolDispatcher dispatcher;

    private final ToolType type;

    public ToolItem(Properties props, ToolType type) {
        super(props);
        this.type = type;
    }

    /** Install the server-side dispatcher. Called from the WorldEdit bridge on server start. */
    public static void setDispatcher(ToolDispatcher d) {
        dispatcher = d;
    }

    /** Release a player's per-tool state on disconnect; no-op if no dispatcher is installed. */
    public static void release(UUID id) {
        ToolDispatcher d = dispatcher;
        if (d != null) {
            d.release(id);
        }
    }

    /** Brief usage tooltip, keyed off the item's registry id (tooltip.<ns>.<path>.*). */
    @Override
    public void appendHoverText(ItemStack stack, Item.TooltipContext context, List<Component> tooltip, TooltipFlag flag) {
        ResourceLocation id = BuiltInRegistries.ITEM.getKey(this);
        String base = "tooltip." + id.getNamespace() + "." + id.getPath();
        tooltip.add(Component.translatable(base + ".desc").withStyle(ChatFormatting.GOLD));
        tooltip.add(Component.translatable(base + ".primary").withStyle(ChatFormatting.GRAY));
        tooltip.add(Component.translatable(base + ".secondary").withStyle(ChatFormatting.GRAY));
        super.appendHoverText(stack, context, tooltip, flag);
    }

    /** Right-click on a block -> primary action. */
    @Override
    public InteractionResult useOn(UseOnContext ctx) {
        if (ctx.getLevel().isClientSide || !(ctx.getPlayer() instanceof ServerPlayer sp)) {
            return InteractionResult.PASS;
        }
        boolean acted = dispatch(sp, ctx.getClickedPos(), ctx.getClickedFace(), true);
        return acted ? InteractionResult.SUCCESS : InteractionResult.PASS;
    }

    /** Left-click on a block -> secondary action. Invoked from the AttackBlockCallback handler. */
    public boolean runSecondary(ServerPlayer sp, BlockPos pos, Direction face) {
        return dispatch(sp, pos, face, false);
    }

    private boolean dispatch(ServerPlayer sp, BlockPos pos, @Nullable Direction face, boolean primary) {
        ToolDispatcher d = dispatcher;
        return d != null && d.dispatch(sp, pos, face, primary, type);
    }
}
