package com.westeroscraft.westerostools.item;

import java.util.List;
import java.util.UUID;
import java.util.function.Function;
import javax.annotation.Nullable;

import com.sk89q.worldedit.LocalConfiguration;
import com.sk89q.worldedit.LocalSession;
import com.sk89q.worldedit.command.tool.DoubleActionBlockTool;
import com.sk89q.worldedit.entity.Player;
import com.sk89q.worldedit.extension.platform.Capability;
import com.sk89q.worldedit.extension.platform.Platform;
import com.sk89q.worldedit.fabric.FabricAdapter;
import com.sk89q.worldedit.util.Location;

import com.westeroscraft.westerostools.WesterosTools;

import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
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
 * A custom item that directly carries one of the WesterosTools WorldEdit tools.
 *
 *   right-click (use)    -> {@link DoubleActionBlockTool#actPrimary}
 *   left-click  (attack) -> {@link DoubleActionBlockTool#actSecondary}
 *
 * All tool logic runs server-side; the client predicts nothing.
 */
public class ToolItem extends Item {

    private final Function<UUID, DoubleActionBlockTool> toolFor;

    public ToolItem(Properties props, Function<UUID, DoubleActionBlockTool> toolFor) {
        super(props);
        this.toolFor = toolFor;
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
    public boolean runSecondary(ServerPlayer sp, BlockPos pos, net.minecraft.core.Direction face) {
        return dispatch(sp, pos, face, false);
    }

    private boolean dispatch(ServerPlayer sp, BlockPos pos, @Nullable net.minecraft.core.Direction mcFace, boolean primary) {
        if (WesterosTools.worldEdit == null) {
            return false;
        }
        Player player = FabricAdapter.adaptPlayer(sp);
        DoubleActionBlockTool tool = toolFor.apply(sp.getUUID());
        if (!tool.canUse(player)) {
            return false;
        }

        LocalSession session = WesterosTools.worldEdit.getSessionManager().get(player);
        Platform platform = WesterosTools.worldEdit.getPlatformManager().queryCapability(Capability.WORLD_EDITING);
        LocalConfiguration config = platform.getConfiguration();

        com.sk89q.worldedit.util.Direction face = (mcFace == null) ? null : FabricAdapter.adaptEnumFacing(mcFace);
        Location clicked = new Location(player.getWorld(), pos.getX(), pos.getY(), pos.getZ());

        return primary
            ? tool.actPrimary(platform, config, player, session, clicked, face)
            : tool.actSecondary(platform, config, player, session, clicked, face);
    }
}
