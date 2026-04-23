package com.westeroscraft.westerostools.tools.chisel;

import com.sk89q.worldedit.util.Direction;

import com.westeroscraft.westerostools.BlockDef.Variant;

import java.util.ArrayList;
import java.util.List;

import static com.westeroscraft.westerostools.tools.chisel.ChiselHelper.UVBin.*;
import static com.westeroscraft.westerostools.tools.chisel.ChiselHelper.tr;

/**
 * Secondary (right-click) chisel transitions: variant → solid (undo).
 */
class SculptTransitions {

    static final List<ChiselHelper.Transition> LIST = new ArrayList<>(List.of(

        // TODO: reverse/undo transitions (e.g. stair/slab/wall → solid)

    ));
}
