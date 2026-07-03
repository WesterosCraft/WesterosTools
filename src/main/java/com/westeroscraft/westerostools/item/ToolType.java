package com.westeroscraft.westerostools.item;

/**
 * Identifies which WesterosTools tool a {@link ToolItem} drives. Deliberately
 * free of any WorldEdit types so item registration can load on the client
 * (where WorldEdit is not present); the mapping from type to the actual
 * WorldEdit tool lives server-side in the tool dispatcher.
 */
public enum ToolType {
    CYCLER,
    EXTRUDE,
    CHISEL,
    PAINT
}
