package com.westeroscraft.westerostools;

import net.minecraft.ChatFormatting;
import net.minecraft.CrashReport;
import net.minecraft.ReportedException;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.RegisterCommandsEvent;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.TickEvent.ServerTickEvent;
import net.minecraftforge.event.server.ServerStartingEvent;
import net.minecraftforge.event.server.ServerStoppingEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.fml.IExtensionPoint;
import net.minecraftforge.fml.ModContainer;
import net.minecraftforge.fml.ModList;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.loading.FMLPaths;
import net.minecraftforge.network.NetworkConstants;

import com.mojang.brigadier.CommandDispatcher;

import com.sk89q.worldedit.EditSession;
import com.sk89q.worldedit.LocalSession;
import com.sk89q.worldedit.MaxChangedBlocksException;
import com.sk89q.worldedit.WorldEdit;
import com.sk89q.worldedit.command.tool.brush.Brush;
import com.sk89q.worldedit.entity.Player;
import com.sk89q.worldedit.extension.platform.Actor;
import com.sk89q.worldedit.extent.clipboard.Clipboard;
import com.sk89q.worldedit.extent.clipboard.io.ClipboardFormat;
import com.sk89q.worldedit.extent.clipboard.io.ClipboardFormats;
import com.sk89q.worldedit.extent.clipboard.io.ClipboardReader;
import com.sk89q.worldedit.forge.ForgeAdapter;
import com.sk89q.worldedit.forge.ForgePlayer;
import com.sk89q.worldedit.forge.ForgeWorldEdit;
import com.sk89q.worldedit.function.mask.BlockMask;
import com.sk89q.worldedit.function.mask.Mask;
import com.sk89q.worldedit.function.operation.Operations;
import com.sk89q.worldedit.math.BlockVector3;
import com.sk89q.worldedit.math.Vector3;
import com.sk89q.worldedit.math.transform.AffineTransform;
import com.sk89q.worldedit.regions.Region;
import com.sk89q.worldedit.session.ClipboardHolder;
import com.sk89q.worldedit.util.Direction;
import com.sk89q.worldedit.util.io.Closer;
import com.sk89q.worldedit.util.io.file.FilenameException;
import com.sk89q.worldedit.world.block.BaseBlock;
import com.sk89q.worldedit.world.block.BlockTypes;
import com.westeroscraft.westerostools.commands.WCTOOLCommand;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonParseException;
import com.google.gson.JsonIOException;
import com.google.gson.JsonSyntaxException;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.FileNotFoundException;
import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.IOException;
import java.nio.file.FileAlreadyExistsException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Random;
import java.util.concurrent.Callable;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;

import static com.westeroscraft.westerostools.BlockDef.*;

// The value here should match an entry in the META-INF/mods.toml file
@Mod(WesterosTools.MOD_ID)
public class WesterosTools {
	public static final String MOD_ID = "westerostools";

	public static final String BLOCK_SET_CONFIG = "blocksets.json";

	// Directly reference a log4j logger.
	public static final Logger log = LogManager.getLogger();

	// Says where the client and server 'proxy' code is loaded.
	public static Proxy proxy = DistExecutor.safeRunForDist(() -> ClientProxy::new, () -> Proxy::new);

	public static Path modConfigDir;
	public static String blockSetConfigFilename;

	public static ModContainer we;
	public static ForgeWorldEdit wep;
	public static WorldEdit worldEdit;

	public BlockSetConfig config;
	public HashMap<String, HashMap<Variant, String>> blockMap = new HashMap<String, HashMap<Variant, String>>();
	public HashMap<String, Variant> variantMap = new HashMap<String, Variant>();
	public HashMap<String, String> invBlockMap = new HashMap<String, String>();
	   
	public WesterosTools() {
		// Register ourselves for server and other game events we are interested in
		MinecraftForge.EVENT_BUS.register(this);

		// Create the config folder
		Path configPath = FMLPaths.CONFIGDIR.get();
		modConfigDir = Paths.get(configPath.toAbsolutePath().toString(), MOD_ID);
		try {
			Files.createDirectory(modConfigDir);
		} catch (FileAlreadyExistsException e) {
			// Do nothing
		} catch (IOException e) {
			log.error("Failed to create westerostools config directory", e);
		}
		blockSetConfigFilename = modConfigDir + "/" + BLOCK_SET_CONFIG;
	}
	
	@SubscribeEvent
	public void onRegisterCommandEvent(RegisterCommandsEvent event) {
		CommandDispatcher<CommandSourceStack> commandDispatcher = event.getDispatcher();
		WCTOOLCommand.register(this, commandDispatcher);
	}

	@SubscribeEvent
	public void serverStopping(ServerStoppingEvent event) {
		
	}
	
	public static void crash(Exception x, String msg) {
		throw new ReportedException(new CrashReport(msg, x));
	}

	public static void crash(String msg) {
		crash(new Exception(), msg);
	}

	@SubscribeEvent
	public void onServerStartingEvent(ServerStartingEvent event) {
		ModContainer ourMod = ModList.get().getModContainerById(MOD_ID).get();
		log.info("WesterosTools v" + ourMod.getModInfo().getVersion() + " loaded");

		Optional<? extends ModContainer> worldedit = ModList.get().getModContainerById("worldedit");
		if (!worldedit.isPresent()) {
				log.error("WorldEdit not found!!");
			return;
		}
		we = worldedit.get();
		wep = (ForgeWorldEdit) we.getMod();        
		worldEdit = WorldEdit.getInstance();
		log.info("Found worldedit " + we.getModInfo().getVersion());

		// Load block set configuration
		try {
			config = loadBlockSetConfig(blockSetConfigFilename);
		} catch (ConfigNotFoundException | JsonSyntaxException | JsonIOException ex) {
			log.warn(BLOCK_SET_CONFIG + " missing or could not be read; using empty block set map.");
			config = new BlockSetConfig();
		}
	  createBlockMap(config);
		log.info("Block sets initialized");
	}

	private static class ConfigNotFoundException extends Exception {
		public ConfigNotFoundException() {
		}
		public ConfigNotFoundException(String message) {
			super(message);
		}
	}

	/*
	 * Load block set config from external JSON.
	 */
	private static BlockSetConfig loadBlockSetConfig(String filename) throws ConfigNotFoundException, JsonParseException {
		BlockSetConfig config;
		File configFile = new File(filename);
		InputStream in;
		try {
			in = new FileInputStream(configFile);
		} catch (FileNotFoundException iox) {
			in = null;
		}
		if (in == null) {
			throw new ConfigNotFoundException();
		}
		BufferedReader rdr = new BufferedReader(new InputStreamReader(in));
		Gson gson = new Gson();
		try {
			config = gson.fromJson(rdr, BlockSetConfig.class);
		} catch (JsonParseException iox) {
			throw iox;
		} finally {
			if (in != null) {
				try {
					in.close();
				} catch (IOException iox) {
				}
				;
				in = null;
			}
			if (rdr != null) {
				try {
					rdr.close();
				} catch (IOException iox) {
				}
				;
				rdr = null;
			}
		}
		if (config == null) throw new ConfigNotFoundException();
		return config;
	}

	/*
	 * Load and store block map
	 */
	private void createBlockMap(BlockSetConfig config) {
		blockMap.clear(); // Reset map

		for (BlockSet set : config.blocksets) {
			HashMap<Variant, String> setMap = new HashMap<Variant, String>();
			for (BlockDef block : set.blocks) {
				setMap.put(block.variant, block.id);
				variantMap.put(block.id, block.variant);
				invBlockMap.put(block.id, set.id);
			}
			blockMap.put(set.id, setMap);
			if (set.altname != null) blockMap.put(set.altname, setMap);
		}
	}

	/*
	 * Get the variant of a given block
	 * This will try to look up the variant in the variant map; if not found, it will attempt to
	 * infer the variant from the block ID. Otherwise, it will assume "solid" as default.
	 */
	public Variant getBlockVariant(String id) {
		if (variantMap.containsKey(id)) {
			return variantMap.get(id);
		}
		for (Variant typ : Variant.values()) {
			if (id.contains(typ.toString().toLowerCase()))
				return typ;
		}
		return Variant.SOLID;
	}

	/* 
	 * Validate that actor is server player and has permissions; otherwise return null.
	 */
	public static Actor validateActor(CommandSourceStack source, String permissionGroup) {
		if (source.getEntity() instanceof ServerPlayer) {
			ServerPlayer player = (ServerPlayer) source.getEntity();
      Actor actor = ForgeAdapter.adaptPlayer(player);

			// Test for command access
			if ((permissionGroup != null) && !actor.hasPermission(permissionGroup)) {
        source.sendFailure(new TextComponent("You do not have access to this command"));
        return null;
			}

			return actor;

		} else {
			source.sendFailure(new TextComponent("Only usable by server player"));
			return null;
		}
	}
	
	public static void debugLog(String msg) {
		log.info(msg);
	}
}
