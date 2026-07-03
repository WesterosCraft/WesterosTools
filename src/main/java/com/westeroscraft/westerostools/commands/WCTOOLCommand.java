package com.westeroscraft.westerostools.commands;

import com.sk89q.worldedit.LocalSession;
import com.sk89q.worldedit.extension.platform.Actor;
import com.sk89q.worldedit.entity.Player;
import com.sk89q.worldedit.util.formatting.text.TextComponent;
import com.sk89q.worldedit.util.formatting.text.TranslatableComponent;
import com.sk89q.worldedit.util.formatting.text.Component;
import com.sk89q.worldedit.util.formatting.text.event.ClickEvent;
import com.sk89q.worldedit.util.formatting.text.format.TextColor;
import com.sk89q.worldedit.util.HandSide;
import com.sk89q.worldedit.command.tool.InvalidToolBindException;
import com.sk89q.worldedit.blocks.BaseItemStack;

import com.westeroscraft.westerostools.WesterosTools;
import com.westeroscraft.westerostools.WorldEditBridge;
import com.westeroscraft.westerostools.item.ModItems;
import com.westeroscraft.westerostools.item.ToolItem;
import com.westeroscraft.westerostools.tools.BlockDataCycler;
import com.westeroscraft.westerostools.tools.Extrude;
import com.westeroscraft.westerostools.tools.Paint;
import com.westeroscraft.westerostools.tools.chisel.Chisel;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.DoubleArgumentType;
import com.mojang.brigadier.arguments.StringArgumentType;

import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.server.level.ServerPlayer;


public class WCTOOLCommand {
	private static WesterosTools wt;

  private static final Component UNBIND_COMMAND_COMPONENT = TextComponent.builder("/tool unbind", TextColor.AQUA)
                                                                  .clickEvent(ClickEvent.suggestCommand("/tool unbind"))
                                                                  .build();
	
	public static void register(WesterosTools mod, CommandDispatcher<CommandSourceStack> source) {
		wt = mod;
    BlockSetSuggestionProvider suggestedSets = new BlockSetSuggestionProvider(wt);
		WesterosTools.LOGGER.info("Register wctool");

    source.register(Commands.literal("wctool")
      .then(Commands.literal("cycler")
        .executes(ctx -> cycler(ctx.getSource())))
      .then(Commands.literal("chisel")
        .executes(ctx -> chisel(ctx.getSource())))
      .then(Commands.literal("extrude")
        .executes(ctx -> extrude(ctx.getSource())))
      .then(Commands.literal("paint")
        .then(Commands.argument("radius", DoubleArgumentType.doubleArg(1.0))
          .executes(ctx -> paint(null, DoubleArgumentType.getDouble(ctx, "radius"), ctx.getSource())))
        .then(Commands.argument("arg", StringArgumentType.word()).suggests(suggestedSets)
          .then(Commands.argument("radius", DoubleArgumentType.doubleArg(1.0))
            .executes(ctx -> paint(StringArgumentType.getString(ctx, "arg"), DoubleArgumentType.getDouble(ctx, "radius"), ctx.getSource())))
          .executes(ctx -> paint(StringArgumentType.getString(ctx, "arg"), 1.0, ctx.getSource())))
        .executes(ctx -> paint(null, 1.0, ctx.getSource()))));
	}

  /**
   * True if the source is holding one of the dedicated tool items. Those
   * dispatch tool actions on their own, so binding a WorldEdit tool to them
   * would run both paths on every click.
   */
  private static boolean holdingToolItem(CommandSourceStack source) {
    return source.getEntity() instanceof ServerPlayer sp
        && sp.getMainHandItem().getItem() instanceof ToolItem;
  }

  /*
   * Data cycler tool that automatically sets unconnect=true
   */
  public static int cycler(CommandSourceStack source) {
    Actor actor = WorldEditBridge.validateActor(source, "westerostools.data-cycler");
    if (actor != null) {
      Player player = (Player) actor;
      if (holdingToolItem(source)) {
        player.printError(TextComponent.of("This item is already a Westeroscraft tool and cannot be bound over."));
        return 1;
      }
      LocalSession session = WorldEditBridge.worldEdit.getSessionManager().get(actor);

      // Initialize tool
      BlockDataCycler tool = new BlockDataCycler();

      // Bind tool to item
      try {
        BaseItemStack itemStack = player.getItemInHand(HandSide.MAIN_HAND);
        session.setTool(itemStack.getType(), tool);
        player.printInfo(TextComponent.of("Westeroscraft block data cycler tool bound to current item."));
        sendUnbindInstruction(player, UNBIND_COMMAND_COMPONENT);
      } catch (InvalidToolBindException e) {
        actor.printError(TextComponent.of(e.getMessage()));
      }
    }

    return 1;
  }

  /*
   * Tool that chisels a block into a related variant based on where the player clicks on the face.
   */
  public static int chisel(CommandSourceStack source) {
    Actor actor = WorldEditBridge.validateActor(source, "westerostools.chisel");
    if (actor != null) {
      Player player = (Player) actor;
      if (holdingToolItem(source)) {
        player.printError(TextComponent.of("This item is already a Westeroscraft tool and cannot be bound over."));
        return 1;
      }
      LocalSession session = WorldEditBridge.worldEdit.getSessionManager().get(actor);

      // Initialize tool
      Chisel tool = new Chisel(wt);

      // Bind tool to item
      try {
        BaseItemStack itemStack = player.getItemInHand(HandSide.MAIN_HAND);
        session.setTool(itemStack.getType(), tool);
        player.printInfo(TextComponent.of("Westeroscraft chisel tool bound to current item."));
        sendUnbindInstruction(player, UNBIND_COMMAND_COMPONENT);
      } catch (InvalidToolBindException e) {
        actor.printError(TextComponent.of(e.getMessage()));
      }
    }

    return 1;
  }

  /*
   * Tool that can extrude blocks with directional blockstates in the direction the player is facing
   */
  public static int extrude(CommandSourceStack source) {
    Actor actor = WorldEditBridge.validateActor(source, "westerostools.extrude");
    if (actor != null) {
      Player player = (Player) actor;
      if (holdingToolItem(source)) {
        player.printError(TextComponent.of("This item is already a Westeroscraft tool and cannot be bound over."));
        return 1;
      }
      LocalSession session = WorldEditBridge.worldEdit.getSessionManager().get(actor);

      // Initialize tool
      Extrude tool = new Extrude();

      // Bind tool to item
      try {
        BaseItemStack itemStack = player.getItemInHand(HandSide.MAIN_HAND);
        session.setTool(itemStack.getType(), tool);
        player.printInfo(TextComponent.of("Westeroscraft block extrude tool bound to current item."));
        sendUnbindInstruction(player, UNBIND_COMMAND_COMPONENT);
      } catch (InvalidToolBindException e) {
        actor.printError(TextComponent.of(e.getMessage()));
      }
    }

    return 1;
  }

  /*
   * Tool that can paint blocks with a given block set
   */
  public static int paint(String arg, double radius, CommandSourceStack source) {
    Actor actor = WorldEditBridge.validateActor(source, "westerostools.paint");
    if (actor != null) {
      Player player = (Player) actor;
      if (arg != null && !wt.hasBlockSet(arg)) {
        player.printError(TextComponent.of("Block set does not exist"));
        player.printDebug(TextComponent.of("Tip: use '/tool repl <id>' or '/tool repl ^<id>' for individual blocks"));
        return 1;
      }

      // Holding the dedicated paint tool item: configure its per-player Paint
      // instance instead of binding a WorldEdit tool to the item type. A null
      // set only changes the radius, keeping the current selection.
      if (holdingToolItem(source)) {
        ServerPlayer sp = (ServerPlayer) source.getEntity();
        if (sp.getMainHandItem().getItem() == ModItems.PAINT && ToolItem.configurePaint(sp, arg, radius)) {
          player.printInfo(TextComponent.of("Westeroscraft paint tool configured"
              + (arg != null ? " with set '" + arg + "'" : "") + " (radius " + radius + ")."));
        } else {
          player.printError(TextComponent.of("This item is already a Westeroscraft tool and cannot be bound over."));
        }
        return 1;
      }

      LocalSession session = WorldEditBridge.worldEdit.getSessionManager().get(actor);

      // Initialize tool
      Paint tool = new Paint(wt);
      tool.setRadius(radius);
      if (arg != null) {
        tool.updateSet(player, arg);
      }

      // Bind tool to item
      try {
        BaseItemStack itemStack = player.getItemInHand(HandSide.MAIN_HAND);
        session.setTool(itemStack.getType(), tool);
        player.printInfo(TextComponent.of("Westeroscraft paint tool bound to current item."));
        sendUnbindInstruction(player, UNBIND_COMMAND_COMPONENT);
      } catch (InvalidToolBindException e) {
        actor.printError(TextComponent.of(e.getMessage()));
      }
    }

    return 1;
  }

  static void sendUnbindInstruction(Player sender, Component commandComponent) {
    sender.printDebug(TranslatableComponent.of("worldedit.tool.unbind-instruction", commandComponent));
  }
}