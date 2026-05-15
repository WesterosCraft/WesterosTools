# WesterosTools

A Fabric mod providing specialized build tools for the [WesterosCraft](https://www.westeroscraft.com/) server, designed to work alongside the WesterosBlocks modpack.

## Dependencies

| Dependency | Version | Required |
|---|---|---|
| Minecraft | 1.21.1 | Yes |
| Java | 21+ | Yes |
| [Fabric Loader](https://fabricmc.net/) | 0.18.4+ | Yes |
| [Fabric API](https://modrinth.com/mod/fabric-api) | 0.116.9+1.21.1 | Yes |
| [WorldEdit](https://enginehub.org/worldedit/) (Fabric) | 7.3.8 | Yes |

WorldEdit must be installed on the server for the mod to function. Tools are bound using WorldEdit's session system and respect WorldEdit's permission model.

## Building

The project uses Gradle with [Fabric Loom](https://github.com/FabricMC/fabric-loom).

```bash
./gradlew build
```

The compiled mod JAR will be output to `build/libs/`. Dependency versions can be adjusted in [gradle.properties](gradle.properties).

To regenerate IDE project files (IntelliJ):

```bash
./gradlew genSources idea
```

## Installation

Place the built JAR (and its dependencies: Fabric API and WorldEdit) in the server's `mods/` folder.

## Server Configuration

On first run, the mod creates a config directory at `config/westerostools/`. Two JSON files are read from this directory at server startup:

### `blocksets.json` (required for Paint and Chisel)

Defines named **block sets** - groups of related block variants that belong to the same material family. The Paint and Chisel tools use this mapping to swap one variant for another within the same set.

```json
{
  "blocksets": [
    {
      "id": "westerosblocks:mystone",
      "altname": "mystone",
      "blocks": [
        { "id": "westerosblocks:mystone", "variant": "solid" },
        { "id": "westerosblocks:mystone_stairs", "variant": "stairs" },
        { "id": "westerosblocks:mystone_slab", "variant": "slab" },
        { "id": "westerosblocks:mystone_wall", "variant": "wall" }
      ]
    }
  ]
}
```

- `id`: canonical set identifier (used in `/wctool paint`)
- `altname`: optional short alias for the set
- `blocks`: list of block definitions, each with a block `id` and a `variant` (see [Variants](#variants) below)

If `blocksets.json` is missing or empty, the mod will still load but Paint and Chisel will not be able to resolve block set relationships.

### `custom_states.json` (optional)

Allows blocks that share the same block ID but differ only in block state properties to be treated as members of different sets. Useful for blocks whose variant is determined by a property value rather than a distinct block ID.

```json
{
  "westerosblocks:mystone_set_a": {
    "westerosblocks:mystone_shared_block": "color:red"
  },
  "westerosblocks:mystone_set_b": {
    "westerosblocks:mystone_shared_block": "color:blue"
  }
}
```

The top-level keys are set IDs, the inner keys are block IDs, and the values are comma-separated `property:value` pairs that identify which state maps to that set.

### Variants

The following variant identifiers are supported in `blocksets.json`:

| Variant | Description |
|---|---|
| `solid` | Full cube block |
| `stairs` | Stair block |
| `slab` | Slab block |
| `wall` | Wall block |
| `fence` | Fence block |
| `fence_gate` | Fence gate block |
| `hopper` | Hopper-shaped block |
| `hollow_hopper` | Hollow hopper-shaped block |
| `tip` | Tip/spike shaped block |
| `carpet` | Thin carpet-like block |
| `cover` | Cover block |
| `log` | Log/pillar block |
| `directional` | Directional block |
| `layer` | Layered block (like snow layers) |
| `pane` | Thin pane block (like glass panes) |
| `sand` | Sand-like falling block |
| `path` | Path block |
| `window_frame` | Window frame block |
| `window_frame_mullion` | Window frame with mullion |
| `arrow_slit` | Arrow slit block |
| `arrow_slit_window` | Arrow slit with window |
| `arrow_slit_ornate` | Ornate arrow slit block |

### Permissions

Tools use WorldEdit's permission system. Grant the following permission nodes through your permission plugin:

| Permission | Tool |
|---|---|
| `westerostools.data-cycler` | Block Data Cycler |
| `westerostools.chisel` | Chisel |
| `westerostools.extrude` | Extrude |
| `westerostools.paint` | Paint |

## Commands and Tools

All commands are under `/wctool`. Each command binds a tool to the item currently held in the player's main hand. Use `/tool unbind` to remove a tool binding.

---

### `/wctool cycler` - Block Data Cycler

A variant of WorldEdit's built-in data cycler that automatically sets the `unconnect` property to `true` whenever it is present on a block (used by WesterosBlocks custom blocks to suppress automatic neighbor-connection logic).

| Click | Action |
|---|---|
| Left-click (primary) | Cycle the current property to its next value |
| Right-click (secondary) | Switch to the next property to cycle |

---

### `/wctool chisel` - Chisel

Converts a block into a related variant from the same block set based on where the player clicks on the face. The clicked face is divided into a 3x3 UV grid (LOW/MID/HIGH on each axis), and the target variant is determined by a transition table.

- **Left-click (primary)**: Sculpt mode - uses the sculpt transition table ([SculptTransitions](src/main/java/com/westeroscraft/westerostools/tools/chisel/SculptTransitions.java))
- **Right-click (secondary)**: Chisel mode - uses the chisel transition table ([ChiselTransitions](src/main/java/com/westeroscraft/westerostools/tools/chisel/ChiselTransitions.java))

Both modes resolve the source block's set from `blocksets.json`, find the target variant, carry over compatible block state properties, and automatically set `unconnect=true` if the target block supports it. Changes are recorded in WorldEdit's undo history.

**Example (NORTH face grid, chisel mode):**

```
HIGH | outer_left stair (W,top) | stair (N,top)    | outer_right stair (E,top) |
MID  | stair (W)                | wall (E+W)       | stair (E)                 |
LOW  | outer_right stair (W)    | stair (N,bottom) | outer_left stair (E)      |
      u=LOW (east)               u=MID              u=HIGH (west)
```

---

### `/wctool extrude` - Extrude

Cycles directional block state properties in the direction the player is facing. Useful for extending wall connections, adjusting stair shapes, or toggling directional properties on WesterosBlocks custom blocks.

| Click | Action |
|---|---|
| Left-click (primary) | Extrude in the direction the player is facing |
| Right-click (secondary) | Extrude in the opposite direction |

For blocks with a named directional property (e.g., `north`, `south`, `east`, `west`), the corresponding property is cycled. For stair blocks, the `shape` property is cycled among `inner_left`, `inner_right`, `straight` (forward) or `outer_left`, `outer_right`, `straight` (backward). Sets `unconnect=true` if present.

---

### `/wctool paint [blockset] <radius>` - Paint

Paints blocks with a material from a block set, preserving the variant and block state of each target block.

```
/wctool paint                    # bind with no set selected
/wctool paint <radius>           # bind with radius, no set selected
/wctool paint <blockset>         # bind with a specific block set (radius 1)
/wctool paint <blockset> <radius># bind with a specific block set and radius
```

| Click | Action |
|---|---|
| Left-click (primary) | Paint the clicked block (or sphere of radius R) with the selected set |
| Right-click (secondary) | Select the block set from the clicked block |

**Behavior:**

- **Right-click** samples the clicked block. If it belongs to a known set (via `blocksets.json` or `custom_states.json`), that set is selected. Otherwise the block is treated as a singleton and painting will replace blocks with that exact block ID.
- **Left-click (single block)**: Replaces the clicked block with the corresponding variant from the selected set, copying compatible block state properties.
- **Left-click (radius > 1)**: Paints all blocks within a sphere of the given radius. In radius mode, only blocks whose variant can be inferred (solid, stairs, slab, wall, or fence) are painted - air and other non-matching blocks are skipped.
- If `custom_states.json` defines a mapping for the source block ID, that mapping is applied instead of the standard variant lookup.

The `<blockset>` argument supports tab-completion from the loaded `blocksets.json`.

---

## Project Structure

```
src/main/java/com/westeroscraft/westerostools/
├── WesterosTools.java              # Mod entry point; config loading; block map management
├── BlockSetConfig.java             # Top-level container for blocksets.json (GSON model)
├── BlockSet.java                   # A named set of blocks (id, altname, list of BlockDef)
├── BlockDef.java                   # A single block entry (block id + Variant enum)
│
├── commands/
│   ├── WCTOOLCommand.java          # Registers /wctool and its subcommands
│   └── BlockSetSuggestionProvider.java  # Tab-completion for block set names
│
└── tools/
    ├── BlockDataCycler.java        # Data cycler tool (WorldEdit DoubleActionBlockTool)
    ├── Extrude.java                # Extrude tool
    ├── Paint.java                  # Paint tool
    └── chisel/
        ├── Chisel.java             # Chisel tool entry point (delegates to transition tables)
        ├── ChiselHelper.java       # UV computation, binning, Transition record, table builders
        ├── ChiselTransitions.java  # Transition table for chisel (right-click) mode
        └── SculptTransitions.java  # Transition table for sculpt (left-click) mode
```