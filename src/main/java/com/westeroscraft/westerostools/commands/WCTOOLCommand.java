package com.westeroscraft.westerostools.commands;

import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.regex.Pattern;

import org.enginehub.piston.exception.StopExecutionException;

import com.sk89q.worldedit.EditSession;
import com.sk89q.worldedit.LocalConfiguration;
import com.sk89q.worldedit.LocalSession;
import com.sk89q.worldedit.MaxChangedBlocksException;
import com.sk89q.worldedit.math.Vector3;
import com.sk89q.worldedit.extension.platform.Actor;
import com.sk89q.worldedit.extension.platform.permission.ActorSelectorLimits;
import com.sk89q.worldedit.extent.clipboard.BlockArrayClipboard;
import com.sk89q.worldedit.extent.clipboard.Clipboard;
import com.sk89q.worldedit.extent.clipboard.io.ClipboardFormat;
import com.sk89q.worldedit.extent.clipboard.io.ClipboardFormats;
import com.sk89q.worldedit.extent.clipboard.io.ClipboardWriter;
import com.sk89q.worldedit.entity.Player;
import com.sk89q.worldedit.session.ClipboardHolder;
import com.sk89q.worldedit.util.formatting.text.TextComponent;
import com.sk89q.worldedit.util.formatting.text.TranslatableComponent;
import com.sk89q.worldedit.util.formatting.text.Component;
import com.sk89q.worldedit.util.formatting.text.event.ClickEvent;
import com.sk89q.worldedit.util.formatting.text.format.TextColor;
import com.sk89q.worldedit.util.io.Closer;
import com.sk89q.worldedit.util.Direction;
import com.sk89q.worldedit.util.HandSide;
import com.sk89q.worldedit.world.World;
import com.sk89q.worldedit.world.block.BlockState;
import com.sk89q.worldedit.command.tool.BrushTool;
import com.sk89q.worldedit.command.tool.InvalidToolBindException;
import com.sk89q.worldedit.command.tool.brush.Brush;
import com.sk89q.worldedit.command.ToolCommands;
import com.sk89q.worldedit.forge.ForgeAdapter;
import com.sk89q.worldedit.function.operation.ForwardExtentCopy;
import com.sk89q.worldedit.function.operation.Operation;
import com.sk89q.worldedit.function.operation.Operations;
import com.sk89q.worldedit.math.BlockVector3;
import com.sk89q.worldedit.math.transform.Transform;
import com.sk89q.worldedit.math.transform.AffineTransform;
import com.sk89q.worldedit.regions.Region;
import com.sk89q.worldedit.regions.RegionSelector;
import com.sk89q.worldedit.regions.selector.limit.PermissiveSelectorLimits;
import com.sk89q.worldedit.blocks.BaseItemStack;

import com.westeroscraft.westerostools.WesterosTools;
import com.westeroscraft.westerostools.tools.BlockDataCycler;
import com.westeroscraft.westerostools.tools.Extrude;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.arguments.IntegerArgumentType;

import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.chunk.ChunkAccess;
import net.minecraft.world.level.chunk.ChunkStatus;
import net.minecraft.world.phys.Vec3;


public class WCTOOLCommand {
	private static WesterosTools wt;

  private static final Component UNBIND_COMMAND_COMPONENT = TextComponent.builder("/tool unbind", TextColor.AQUA)
                                                                  .clickEvent(ClickEvent.suggestCommand("/tool unbind"))
                                                                  .build();
	
	public static void register(WesterosTools mod, CommandDispatcher<CommandSourceStack> source) {
		wt = mod;
		WesterosTools.log.info("Register wctool");

    source.register(Commands.literal("wctool")
      .then(Commands.literal("cycler")
        .executes(ctx -> cycler(ctx.getSource())))
      .then(Commands.literal("extrude")
        .executes(ctx -> extrude(ctx.getSource())))
      .then(Commands.literal("paint")
        .then(Commands.argument("arg", StringArgumentType.word())
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
    }

    return 1;
  }

  static void sendUnbindInstruction(Player sender, Component commandComponent) {
    sender.printDebug(TranslatableComponent.of("worldedit.tool.unbind-instruction", commandComponent));
  }
}
