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
import com.westeroscraft.westerostools.tools.BlockDataCycler;
import com.westeroscraft.westerostools.tools.Extrude;
import com.westeroscraft.westerostools.tools.Paint;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.StringArgumentType;

import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;


public class WCTOOLCommand {
	private static WesterosTools wt;

  private static final Component UNBIND_COMMAND_COMPONENT = TextComponent.builder("/tool unbind", TextColor.AQUA)
                                                                  .clickEvent(ClickEvent.suggestCommand("/tool unbind"))
                                                                  .build();
	
	public static void register(WesterosTools mod, CommandDispatcher<CommandSourceStack> source) {
		wt = mod;
    BlockSetSuggestionProvider suggestedSets = new BlockSetSuggestionProvider(wt);
		WesterosTools.log.info("Register wctool");

    source.register(Commands.literal("wctool")
      .then(Commands.literal("cycler")
        .executes(ctx -> cycler(ctx.getSource())))
      .then(Commands.literal("extrude")
        .executes(ctx -> extrude(ctx.getSource())))
      .then(Commands.literal("paint")
        .then(Commands.argument("arg", StringArgumentType.word()).suggests(suggestedSets)
          .executes(ctx -> paint(StringArgumentType.getString(ctx, "arg"), ctx.getSource())))
        .executes(ctx -> paint(null, ctx.getSource()))));
	}

  /*
   * Data cycler tool that automatically sets unconnect=true
   */
  public static int cycler(CommandSourceStack source) {
    Actor actor = wt.validateActor(source, "westerostools.data-cycler");
    if (actor != null) {
      LocalSession session = wt.worldEdit.getSessionManager().get(actor);

      // Initialize tool
      BlockDataCycler tool = new BlockDataCycler();
      Player player = (Player) actor;

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
   * Tool that can extrude blocks with directional blockstates in the direction the player is facing
   */
  public static int extrude(CommandSourceStack source) {
    Actor actor = wt.validateActor(source, "westerostools.extrude");
    if (actor != null) {
      LocalSession session = wt.worldEdit.getSessionManager().get(actor);

      // Initialize tool
      Extrude tool = new Extrude();
      Player player = (Player) actor;

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
  public static int paint(String arg, CommandSourceStack source) {
    Actor actor = wt.validateActor(source, "westerostools.paint");
    if (actor != null) {
      LocalSession session = wt.worldEdit.getSessionManager().get(actor);

      // Initialize tool
      Paint tool = new Paint(wt);
      Player player = (Player) actor;
      if (arg != null) {
        if (wt.hasBlockSet(arg)) {
          tool.updateSet(player, arg);
        }
        else {
          player.printError(TextComponent.of("Block set does not exist"));
          player.printDebug(TextComponent.of("Tip: use '/tool repl <id>' or '/tool repl ^<id>' for individual blocks"));
          return 1;
        }
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
