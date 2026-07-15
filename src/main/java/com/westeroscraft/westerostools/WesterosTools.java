package com.westeroscraft.westerostools;

import java.lang.reflect.Type;
import java.io.IOException;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.FileAlreadyExistsException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import com.google.gson.Gson;
import com.google.gson.JsonParseException;
import com.google.gson.JsonIOException;
import com.google.gson.JsonSyntaxException;
import com.google.gson.reflect.TypeToken;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.loader.api.FabricLoader;
import net.fabricmc.loader.api.ModContainer;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.fabricmc.fabric.api.event.player.AttackBlockCallback;
import net.fabricmc.fabric.api.event.player.UseBlockCallback;
import net.fabricmc.fabric.api.networking.v1.ServerPlayConnectionEvents;

import net.minecraft.CrashReport;
import net.minecraft.ReportedException;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionResult;

import com.westeroscraft.westerostools.BlockDef.Variant;
import com.westeroscraft.westerostools.commands.WCTOOLCommand;
import com.westeroscraft.westerostools.item.ClientClickTracker;
import com.westeroscraft.westerostools.item.ModItems;
import com.westeroscraft.westerostools.item.ModItemGroups;
import com.westeroscraft.westerostools.item.ToolItem;

public class WesterosTools implements ModInitializer {
	public static final String MOD_ID = "westerostools";

	public static final String BLOCK_SET_CONFIG = "blocksets.json";
	public static final String CUSTOM_STATES_CONFIG = "custom_states.json";

	public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

	public static Path modConfigDir;
	public static String blockSetConfigFilename;
	public static String customStatesConfigFilename;

	public static MinecraftServer server;

	public BlockSetConfig config;
	public HashMap<String, HashMap<Variant, String>> blockMap = new HashMap<String, HashMap<Variant, String>>();
	public HashMap<String, Variant> variantMap = new HashMap<String, Variant>();
	public HashMap<String, String> invBlockMap = new HashMap<String, String>();
	public HashMap<String, String> altNameMap = new HashMap<String, String>();

	public HashMap<String, HashMap<String, String>> customStatesMap = new HashMap<String, HashMap<String, String>>();
	public HashMap<String, HashMap<String, String>> invCustomStatesMap = new HashMap<String, HashMap<String, String>>();

	@Override
	public void onInitialize() {

		// Create the config folder
		Path configPath = FabricLoader.getInstance().getConfigDir();
		modConfigDir = configPath.resolve(MOD_ID);
		try {
				Files.createDirectory(modConfigDir);
		} catch (FileAlreadyExistsException e) {
				// expected
		} catch (IOException e) {
				LOGGER.error("Failed to create westerostools config directory", e);
		}
		blockSetConfigFilename = modConfigDir.resolve(BLOCK_SET_CONFIG).toString();
		customStatesConfigFilename = modConfigDir.resolve(CUSTOM_STATES_CONFIG).toString();

		// Register commands only when WorldEdit is present. This also keeps the
		// WorldEdit-referencing WCTOOLCommand class from loading on a client (or a
		// singleplayer world) that ships without WorldEdit.
		CommandRegistrationCallback.EVENT.register((dispatcher, registryAccess, environment) -> {
				if (FabricLoader.getInstance().getModContainer("worldedit").isPresent()) {
						WCTOOLCommand.register(this, dispatcher);
				}
		});

		ModItems.initialize();
		ModItemGroups.initialize();

		// Left-click (attack) on a block with a tool item -> tool secondary action.
		// Right-click is handled by ToolItem.useOn. Tool items never mine, so cancel
		// the break on BOTH sides: cancelling only server-side would let the client
		// predict a break (instant in creative) that the server rejects, leaving a
		// ghost block for actions that don't edit the clicked block. The secondary
		// action only runs server-side.
		//
		// Cancelling also skips vanilla's creative break cooldown, so this event
		// re-fires twice on the press tick and then every client tick the button is
		// held — one click must not become several actions. The client therefore
		// gates on ClientClickTracker: only the first event of a physical press
		// returns SUCCESS (which makes Fabric send the attack to the server);
		// re-fires return FAIL, which still cancels the break but sends nothing.
		AttackBlockCallback.EVENT.register((player, world, hand, pos, direction) -> {
				if (!(player.getItemInHand(hand).getItem() instanceof ToolItem tool)) {
						return InteractionResult.PASS;
				}
				if (world.isClientSide) {
						return ClientClickTracker.acceptAttack(world.getGameTime())
								? InteractionResult.SUCCESS : InteractionResult.FAIL;
				}
				if (player instanceof ServerPlayer sp) {
						tool.runSecondary(sp, pos, direction);
				}
				return InteractionResult.SUCCESS;
		});

		// Right-click repeats every 4 client ticks while the button is held
		// (vanilla hold-to-use), which turns a slightly long click into several
		// primary actions. Gate it the same way: re-fires while held return FAIL
		// before the client sends the use packet, so the server only ever sees the
		// first click of a press. Server side stays PASS so vanilla proceeds to
		// ToolItem.useOn.
		UseBlockCallback.EVENT.register((player, world, hand, hit) -> {
				if (world.isClientSide
						&& player.getItemInHand(hand).getItem() instanceof ToolItem
						&& !ClientClickTracker.acceptUse(world.getGameTime())) {
						return InteractionResult.FAIL;
				}
				return InteractionResult.PASS;
		});

		// Release a player's per-player Paint tool when they disconnect.
		ServerPlayConnectionEvents.DISCONNECT.register((handler, server) ->
				ToolItem.release(handler.player.getUUID()));

		// Server starting/stopping hooks
    ServerLifecycleEvents.SERVER_STARTING.register(this::onServerStarting);
    ServerLifecycleEvents.SERVER_STOPPING.register(this::onServerStopping);
	}

	/*
	 * Server starting event handler.
	 *
	 * Steps:
	 * 1. Load our mod and log the version
	 * 2. Load the WorldEdit mod and log the version
	 * 3. Get the WorldEdit singleton instance and set it within this mod instance
	 * 4. Try to load the block set config and create the block map
	 */
	private void onServerStarting(MinecraftServer server) {
    WesterosTools.server = server;
    Optional<ModContainer> ourMod = FabricLoader.getInstance().getModContainer(MOD_ID);
    ourMod.ifPresent(mod ->
        LOGGER.info("WesterosTools v" + mod.getMetadata().getVersion().getFriendlyString() + " loaded")
    );

    Optional<ModContainer> worldedit = FabricLoader.getInstance().getModContainer("worldedit");
    if (!worldedit.isPresent()) {
        LOGGER.error("WorldEdit not found!!");
        return;
    }
    // Resolve the WorldEdit singleton and install the server-side tool dispatcher.
    // Isolated in WorldEditBridge so WorldEdit classes never link on the client.
    WorldEditBridge.init(this);
    LOGGER.info("Found WorldEdit " + worldedit.get().getMetadata().getVersion().getFriendlyString());

		// Initialize block sets
		try {
				config = loadModConfig(blockSetConfigFilename, BlockSetConfig.class);
		} catch (ConfigNotFoundException | JsonSyntaxException | JsonIOException ex) {
				LOGGER.warn(BLOCK_SET_CONFIG + " missing or could not be read; using empty block set map.");
				config = new BlockSetConfig();
		}
		createBlockMap(config);
		LOGGER.info("Block sets initialized");

		// Load optional custom states config
		createCustomStatesMap();
	}

	/*
	 * Server stopping event handler (unused).
	 */
	private void onServerStopping(MinecraftServer server) {

	}

	private static class ConfigNotFoundException extends Exception {}

	/*
	 * Load config from external JSON, parsing into the given configType.
	 */
	private static <T> T loadModConfig(String filename, Type configType) throws ConfigNotFoundException, JsonParseException {
		T config;
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
			config = gson.fromJson(rdr, configType);
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
			if (set.altname != null && !set.altname.equals("")) {
				blockMap.put(set.altname, setMap);
				altNameMap.put(set.altname, set.id);
			}
		}
	}

	/*
	 * Load the optional custom states config and convert it into a mapping.
	 */
	private void createCustomStatesMap() {
		Type mapType = new TypeToken<HashMap<String, HashMap<String, String>>>(){}.getType();
		try {
				customStatesMap = loadModConfig(customStatesConfigFilename, mapType);
		} catch (ConfigNotFoundException | JsonSyntaxException | JsonIOException ex) {
				LOGGER.debug(CUSTOM_STATES_CONFIG + " not found");
				return;
		}

		// Create inverted mapping
		for (Map.Entry<String, HashMap<String, String>> setEntry : customStatesMap.entrySet()) {
			String blockset = setEntry.getKey();
			for (Map.Entry<String, String> blockEntry : setEntry.getValue().entrySet()) {
				String id = blockEntry.getKey();
				String state = blockEntry.getValue();
				invCustomStatesMap.computeIfAbsent(id, k -> new HashMap<>()).put(state, blockset);
			}
		}

		LOGGER.info("Loaded custom states mapping");
	}

	/*
	 * Get the variant of a given block ID, if it belongs to a set. Returns NULL otherwise.
	 */
	public Variant getBlockVariant(String id) {
		if (variantMap.containsKey(id)) {
			return variantMap.get(id);
		}
		return null;
	}

	/*
	 * Attempt to infer the variant of a given block ID from its name (solid by default).
	 */
	public Variant inferBlockVariant(String id) {
		Variant v = tryInferBlockVariant(id);
		return (v != null) ? v : Variant.SOLID;
	}

	/*
	 * Attempt to infer the variant of a given block ID from its name. Returns NULL
	 * if no variant keyword is found (no default fallback).
	 */
	public Variant tryInferBlockVariant(String id) {
		for (Variant typ : Variant.values()) {
			if (id.contains(typ.toString().toLowerCase())) {
				return typ;
			}
		}
		return null;
	}

	/*
	 * Get a target ID given a block set name and a variant. Returns NULL
	 * if a target block does not exist.
	 */
	public String getTargetId(String setname, Variant variant) {
		if (blockMap.containsKey(setname)) {
			HashMap<Variant, String> setMap = blockMap.get(setname);
			if (setMap.containsKey(variant)) {
				return setMap.get(variant);
			}
		}
		return null;
	}

	/*
	 * Gets the set name corresponding to a given block ID and properties. Returns NULL
	 * if the block ID is not associated with a block set.
	 */
	public String getBlockSet(String id, List<String> properties) {
		if (invBlockMap.containsKey(id)) {
			return invBlockMap.get(id);
		}

		// Check custom states map as a fallback
		if (invCustomStatesMap.containsKey(id)) {
			HashMap<String, String> invCustomStatesMapId = invCustomStatesMap.get(id);
			for (String prop : properties) {
				if (invCustomStatesMapId.containsKey(prop)) {
					return invCustomStatesMapId.get(prop);
				}
			}
		}

		return null;
	}

	/* 
	 * Check if a block set exists.
	 */
	public boolean hasBlockSet(String setname) {
		return blockMap.containsKey(setname);
	}

	/*
	 * If a set name is an altname, map it to the canonical set ID.
	 */
	public String getCanonicalSetName(String setname) {
		if (altNameMap.containsKey(setname)) return altNameMap.get(setname);
		return setname;
	}

	/*
	 * Check if a block id exists in the custom state mapping for a given block set.
	 */
	public boolean isInCustomStatesMap(String setname, String id) {
		if (!customStatesMap.containsKey(setname)) return false;
		HashMap<String, String> setCustomStates = customStatesMap.get(setname);
		return setCustomStates.containsKey(id);
	}

	/*
	 * Get the custom states from the custom state mapping for a given block set and ID.
	 */
	public String getCustomStates(String setname, String id) {
		if (!isInCustomStatesMap(setname, id)) return null;
		return customStatesMap.get(setname).get(id);
	}

	public static void crash(Exception x, String msg) {
			throw new ReportedException(new CrashReport(msg, x));
	}

	public static void crash(String msg) {
			crash(new Exception(), msg);
	}

	public static void debugLog(String msg) {
			LOGGER.info(msg);
	}

}