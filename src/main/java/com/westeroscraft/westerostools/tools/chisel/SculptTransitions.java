package com.westeroscraft.westerostools.tools.chisel;

import com.westeroscraft.westerostools.BlockDef.Variant;

import java.util.ArrayList;
import java.util.List;

import static com.westeroscraft.westerostools.tools.chisel.ChiselHelper.UVBin.*;
import static com.westeroscraft.westerostools.tools.chisel.ChiselHelper.halfSplit;
import static com.westeroscraft.westerostools.tools.chisel.ChiselHelper.sr;
import static com.westeroscraft.westerostools.tools.chisel.ChiselHelper.trAllSides;
import static com.westeroscraft.westerostools.tools.chisel.ChiselHelper.trAllFaces;
import static com.westeroscraft.westerostools.tools.chisel.ChiselHelper.trBothVertical;

/**
 * Secondary (right-click) chisel transitions
 */
class SculptTransitions {

   static final List<ChiselHelper.Transition> LIST;
   static {
      LIST = new ArrayList<>();

      // ===================================================================
      // STAIRS
      // ===================================================================

      // --- side faces (N/S/E/W) ----------------------------------

      LIST.addAll(trAllSides(Variant.STAIRS, halfSplit(List.of(
         // --- straight shapes ---------
         sr(LOW,  MID,  "shape:straight,facing:{dir.opp}", Variant.STAIRS, "shape:inner_left,facing:{dir.opp}"),
         sr(MID,  MID,  "shape:straight,facing:{dir.opp}", Variant.SOLID,  ""),
         sr(HIGH, MID,  "shape:straight,facing:{dir.opp}", Variant.STAIRS, "shape:inner_right,facing:{dir.opp}"),
         sr(LOW,  HIGH, "shape:straight,facing:{dir.opp}", Variant.STAIRS, "shape:inner_left,facing:{dir.opp}"),
         sr(MID,  HIGH, "shape:straight,facing:{dir.opp}", Variant.SOLID,  ""),
         sr(HIGH, HIGH, "shape:straight,facing:{dir.opp}", Variant.STAIRS, "shape:inner_right,facing:{dir.opp}"),
         // --- inner corner shapes ---------
         sr(MID,  MID,  "shape:inner_left,facing:{dir.opp}", Variant.SOLID, ""),
         sr(HIGH, MID,  "shape:inner_left,facing:{dir.opp}", Variant.SOLID, ""),
         sr(MID,  HIGH, "shape:inner_left,facing:{dir.opp}", Variant.SOLID, ""),
         sr(HIGH, HIGH, "shape:inner_left,facing:{dir.opp}", Variant.SOLID, ""),
         // ---
         sr(LOW,  MID,  "shape:inner_right,facing:{dir.opp}", Variant.SOLID, ""),
         sr(MID,  MID,  "shape:inner_right,facing:{dir.opp}", Variant.SOLID, ""),
         sr(LOW,  HIGH, "shape:inner_right,facing:{dir.opp}", Variant.SOLID, ""),
         sr(MID,  HIGH, "shape:inner_right,facing:{dir.opp}", Variant.SOLID, ""),
         // ---
         sr(MID,  MID,  "shape:inner_right,facing:{dir.cw}", Variant.SOLID, ""),
         sr(HIGH, MID,  "shape:inner_right,facing:{dir.cw}", Variant.SOLID, ""),
         sr(MID,  HIGH, "shape:inner_right,facing:{dir.cw}", Variant.SOLID, ""),
         sr(HIGH, HIGH, "shape:inner_right,facing:{dir.cw}", Variant.SOLID, ""),
         // ---
         sr(LOW,  MID,  "shape:inner_left,facing:{dir.ccw}", Variant.SOLID, ""),
         sr(MID,  MID,  "shape:inner_left,facing:{dir.ccw}", Variant.SOLID, ""),
         sr(LOW,  HIGH, "shape:inner_left,facing:{dir.ccw}", Variant.SOLID, ""),
         sr(MID,  HIGH, "shape:inner_left,facing:{dir.ccw}", Variant.SOLID, ""),
         // --- outer corner shapes ---------
         sr(LOW,  MID,  "shape:outer_left,facing:{dir.opp}", Variant.STAIRS, "shape:straight,facing:{dir.cw}"),
         sr(MID,  MID,  "shape:outer_left,facing:{dir.opp}", Variant.STAIRS, "shape:straight,facing:{dir.cw}"),
         sr(LOW,  HIGH, "shape:outer_left,facing:{dir.opp}", Variant.STAIRS, "shape:straight,facing:{dir.cw}"),
         sr(MID,  HIGH, "shape:outer_left,facing:{dir.opp}", Variant.STAIRS, "shape:straight,facing:{dir.cw}"),
         // ---
         sr(MID,  MID,  "shape:outer_right,facing:{dir.opp}", Variant.STAIRS, "shape:straight,facing:{dir.ccw}"),
         sr(HIGH, MID,  "shape:outer_right,facing:{dir.opp}", Variant.STAIRS, "shape:straight,facing:{dir.ccw}"),
         sr(MID,  HIGH, "shape:outer_right,facing:{dir.opp}", Variant.STAIRS, "shape:straight,facing:{dir.ccw}"),
         sr(HIGH, HIGH, "shape:outer_right,facing:{dir.opp}", Variant.STAIRS, "shape:straight,facing:{dir.ccw}"),
         // ---
         sr(LOW,  MID,  "shape:outer_right,facing:{dir.cw}", Variant.STAIRS, "shape:straight,facing:{dir.cw}"),
         sr(MID,  MID,  "shape:outer_right,facing:{dir.cw}", Variant.STAIRS, "shape:straight,facing:{dir.cw}"),
         sr(LOW,  HIGH, "shape:outer_right,facing:{dir.cw}", Variant.STAIRS, "shape:straight,facing:{dir.cw}"),
         sr(MID,  HIGH, "shape:outer_right,facing:{dir.cw}", Variant.STAIRS, "shape:straight,facing:{dir.cw}"),
         // ---
         sr(MID,  MID,  "shape:outer_left,facing:{dir.ccw}", Variant.STAIRS, "shape:straight,facing:{dir.ccw}"),
         sr(HIGH, MID,  "shape:outer_left,facing:{dir.ccw}", Variant.STAIRS, "shape:straight,facing:{dir.ccw}"),
         sr(MID,  HIGH, "shape:outer_left,facing:{dir.ccw}", Variant.STAIRS, "shape:straight,facing:{dir.ccw}"),
         sr(HIGH, HIGH, "shape:outer_left,facing:{dir.ccw}", Variant.STAIRS, "shape:straight,facing:{dir.ccw}")
      ))));

      // --- UP/DOWN faces -----------------------------------------

      LIST.addAll(trBothVertical(Variant.STAIRS, List.of(
         // --- straight shapes ---------
         sr(LOW,  LOW,  "shape:straight,facing:east", Variant.STAIRS,  "shape:inner_left,facing:east"),
         sr(MID,  LOW,  "shape:straight,facing:east", Variant.STAIRS,  "shape:inner_left,facing:east"),
         sr(LOW,  MID,  "shape:straight,facing:east", Variant.SOLID,   ""),
         sr(MID,  MID,  "shape:straight,facing:east", Variant.SOLID,   ""),
         sr(LOW,  HIGH, "shape:straight,facing:east", Variant.STAIRS,  "shape:inner_right,facing:east"),
         sr(MID,  HIGH, "shape:straight,facing:east", Variant.STAIRS,  "shape:inner_right,facing:east"),
         // ---
         sr(MID,  LOW,  "shape:straight,facing:west", Variant.STAIRS,  "shape:inner_right,facing:west"),
         sr(HIGH, LOW,  "shape:straight,facing:west", Variant.STAIRS,  "shape:inner_right,facing:west"),
         sr(MID,  MID,  "shape:straight,facing:west", Variant.SOLID,   ""),
         sr(HIGH, MID,  "shape:straight,facing:west", Variant.SOLID,   ""),
         sr(MID,  HIGH, "shape:straight,facing:west", Variant.STAIRS,  "shape:inner_left,facing:west"),
         sr(HIGH, HIGH, "shape:straight,facing:west", Variant.STAIRS,  "shape:inner_left,facing:west"),
         // ---
         sr(LOW,  LOW,  "shape:straight,facing:south", Variant.STAIRS,  "shape:inner_right,facing:south"),
         sr(MID,  LOW,  "shape:straight,facing:south", Variant.SOLID,   ""),
         sr(HIGH, LOW,  "shape:straight,facing:south", Variant.STAIRS,  "shape:inner_left,facing:south"),
         sr(LOW,  MID,  "shape:straight,facing:south", Variant.STAIRS,  "shape:inner_right,facing:south"),
         sr(MID,  MID,  "shape:straight,facing:south", Variant.SOLID,   ""),
         sr(HIGH, MID,  "shape:straight,facing:south", Variant.STAIRS,  "shape:inner_left,facing:south"),
         // ---
         sr(LOW,  MID,  "shape:straight,facing:north", Variant.STAIRS,  "shape:inner_left,facing:north"),
         sr(MID,  MID,  "shape:straight,facing:north", Variant.SOLID,   ""),
         sr(HIGH, MID,  "shape:straight,facing:north", Variant.STAIRS,  "shape:inner_right,facing:north"),
         sr(LOW,  HIGH, "shape:straight,facing:north", Variant.STAIRS,  "shape:inner_left,facing:north"),
         sr(MID,  HIGH, "shape:straight,facing:north", Variant.SOLID,   ""),
         sr(HIGH, HIGH, "shape:straight,facing:north", Variant.STAIRS,  "shape:inner_right,facing:north"),
         // --- inner corner shapes ---------
         sr(LOW,  MID,  "shape:inner_left,facing:east", Variant.SOLID,   ""),
         sr(MID,  MID,  "shape:inner_left,facing:east", Variant.SOLID,   ""),
         sr(LOW,  HIGH, "shape:inner_left,facing:east", Variant.SOLID,   ""),
         sr(MID,  HIGH, "shape:inner_left,facing:east", Variant.SOLID,   ""),
         // ---
         sr(LOW,  LOW,  "shape:inner_right,facing:east", Variant.SOLID,   ""),
         sr(MID,  LOW,  "shape:inner_right,facing:east", Variant.SOLID,   ""),
         sr(LOW,  MID,  "shape:inner_right,facing:east", Variant.SOLID,   ""),
         sr(MID,  MID,  "shape:inner_right,facing:east", Variant.SOLID,   ""),
         // ---
         sr(MID,  LOW,  "shape:inner_left,facing:west", Variant.SOLID,   ""),
         sr(HIGH, LOW,  "shape:inner_left,facing:west", Variant.SOLID,   ""),
         sr(MID,  MID,  "shape:inner_left,facing:west", Variant.SOLID,   ""),
         sr(HIGH, MID,  "shape:inner_left,facing:west", Variant.SOLID,   ""),
         // ---
         sr(MID,  MID,  "shape:inner_right,facing:west", Variant.SOLID,   ""),
         sr(HIGH, MID,  "shape:inner_right,facing:west", Variant.SOLID,   ""),
         sr(MID,  HIGH, "shape:inner_right,facing:west", Variant.SOLID,   ""),
         sr(HIGH, HIGH, "shape:inner_right,facing:west", Variant.SOLID,   ""),
         // ---
         sr(LOW,  LOW,  "shape:inner_left,facing:south", Variant.SOLID,   ""),
         sr(MID,  LOW,  "shape:inner_left,facing:south", Variant.SOLID,   ""),
         sr(LOW,  MID,  "shape:inner_left,facing:south", Variant.SOLID,   ""),
         sr(MID,  MID,  "shape:inner_left,facing:south", Variant.SOLID,   ""),
         // ---
         sr(MID,  LOW,  "shape:inner_right,facing:south", Variant.SOLID,   ""),
         sr(HIGH, LOW,  "shape:inner_right,facing:south", Variant.SOLID,   ""),
         sr(MID,  MID,  "shape:inner_right,facing:south", Variant.SOLID,   ""),
         sr(HIGH, MID,  "shape:inner_right,facing:south", Variant.SOLID,   ""),
         // ---
         sr(MID,  MID,  "shape:inner_left,facing:north", Variant.SOLID,   ""),
         sr(HIGH, MID,  "shape:inner_left,facing:north", Variant.SOLID,   ""),
         sr(MID,  HIGH, "shape:inner_left,facing:north", Variant.SOLID,   ""),
         sr(HIGH, HIGH, "shape:inner_left,facing:north", Variant.SOLID,   ""),
         // ---
         sr(LOW,  MID,  "shape:inner_right,facing:north", Variant.SOLID,   ""),
         sr(MID,  MID,  "shape:inner_right,facing:north", Variant.SOLID,   ""),
         sr(LOW,  HIGH, "shape:inner_right,facing:north", Variant.SOLID,   ""),
         sr(MID,  HIGH, "shape:inner_right,facing:north", Variant.SOLID,   ""),
         // --- outer corner shapes ---------
         sr(LOW,  LOW,  "shape:outer_left,facing:east", Variant.STAIRS,   "shape:straight,facing:north"),
         sr(MID,  LOW,  "shape:outer_left,facing:east", Variant.STAIRS,   "shape:straight,facing:north"),
         sr(LOW,  MID,  "shape:outer_left,facing:east", Variant.STAIRS,   "shape:straight,facing:north"),
         sr(MID,  MID,  "shape:outer_left,facing:east", Variant.SOLID,    ""),
         sr(HIGH, MID,  "shape:outer_left,facing:east", Variant.STAIRS,   "shape:straight,facing:east"),
         sr(LOW,  HIGH, "shape:outer_left,facing:east", Variant.SOLID,    ""),
         sr(MID,  HIGH, "shape:outer_left,facing:east", Variant.STAIRS,   "shape:straight,facing:east"),
         sr(HIGH, HIGH, "shape:outer_left,facing:east", Variant.STAIRS,   "shape:straight,facing:east"),
         // ---
         sr(LOW,  LOW,  "shape:outer_right,facing:east", Variant.SOLID,   ""),
         sr(MID,  LOW,  "shape:outer_right,facing:east", Variant.STAIRS,  "shape:straight,facing:east"),
         sr(HIGH, LOW,  "shape:outer_right,facing:east", Variant.STAIRS,  "shape:straight,facing:east"),
         sr(LOW,  MID,  "shape:outer_right,facing:east", Variant.STAIRS,  "shape:straight,facing:south"),
         sr(MID,  MID,  "shape:outer_right,facing:east", Variant.SOLID,   ""),
         sr(HIGH, MID,  "shape:outer_right,facing:east", Variant.STAIRS,  "shape:straight,facing:east"),
         sr(LOW,  HIGH, "shape:outer_right,facing:east", Variant.STAIRS,  "shape:straight,facing:south"),
         sr(MID,  HIGH, "shape:outer_right,facing:east", Variant.STAIRS,  "shape:straight,facing:south"),
         // ---
         sr(LOW,  LOW,  "shape:outer_left,facing:west", Variant.STAIRS,  "shape:straight,facing:west"),
         sr(MID,  LOW,  "shape:outer_left,facing:west", Variant.STAIRS,  "shape:straight,facing:west"),
         sr(HIGH, LOW,  "shape:outer_left,facing:west", Variant.SOLID,   ""),
         sr(LOW,  MID,  "shape:outer_left,facing:west", Variant.STAIRS,  "shape:straight,facing:west"),
         sr(MID,  MID,  "shape:outer_left,facing:west", Variant.SOLID,   ""),
         sr(HIGH, MID,  "shape:outer_left,facing:west", Variant.STAIRS,  "shape:straight,facing:south"),
         sr(MID,  HIGH, "shape:outer_left,facing:west", Variant.STAIRS,  "shape:straight,facing:south"),
         sr(HIGH, HIGH, "shape:outer_left,facing:west", Variant.STAIRS,  "shape:straight,facing:south"),
         // ---
         sr(MID,  LOW,  "shape:outer_right,facing:west", Variant.STAIRS, "shape:straight,facing:north"),
         sr(HIGH, LOW,  "shape:outer_right,facing:west", Variant.STAIRS, "shape:straight,facing:north"),
         sr(LOW,  MID,  "shape:outer_right,facing:west", Variant.STAIRS,  "shape:straight,facing:west"),
         sr(MID,  MID,  "shape:outer_right,facing:west", Variant.SOLID,   ""),
         sr(HIGH, MID,  "shape:outer_right,facing:west", Variant.STAIRS, "shape:straight,facing:north"),
         sr(LOW,  HIGH, "shape:outer_right,facing:west", Variant.STAIRS,  "shape:straight,facing:west"),
         sr(MID,  HIGH, "shape:outer_right,facing:west", Variant.STAIRS,  "shape:straight,facing:west"),
         sr(HIGH, HIGH, "shape:outer_right,facing:west", Variant.SOLID,   ""),
         // ---
         sr(LOW,  LOW,  "shape:outer_left,facing:south", Variant.SOLID,   ""),
         sr(MID,  LOW,  "shape:outer_left,facing:south", Variant.STAIRS,  "shape:straight,facing:east"),
         sr(HIGH, LOW,  "shape:outer_left,facing:south", Variant.STAIRS,  "shape:straight,facing:east"),
         sr(LOW,  MID,  "shape:outer_left,facing:south", Variant.STAIRS,  "shape:straight,facing:south"),
         sr(MID,  MID,  "shape:outer_left,facing:south", Variant.SOLID,   ""),
         sr(HIGH, MID,  "shape:outer_left,facing:south", Variant.STAIRS,  "shape:straight,facing:east"),
         sr(LOW,  HIGH, "shape:outer_left,facing:south", Variant.STAIRS,  "shape:straight,facing:south"),
         sr(MID,  HIGH, "shape:outer_left,facing:south", Variant.STAIRS,  "shape:straight,facing:south"),
         // ---
         sr(LOW,  LOW,  "shape:outer_right,facing:south", Variant.STAIRS,  "shape:straight,facing:west"),
         sr(MID,  LOW,  "shape:outer_right,facing:south", Variant.STAIRS,  "shape:straight,facing:west"),
         sr(HIGH, LOW,  "shape:outer_right,facing:south", Variant.SOLID,   ""),
         sr(LOW,  MID,  "shape:outer_right,facing:south", Variant.STAIRS,  "shape:straight,facing:west"),
         sr(MID,  MID,  "shape:outer_right,facing:south", Variant.SOLID,   ""),
         sr(HIGH, MID,  "shape:outer_right,facing:south", Variant.STAIRS,  "shape:straight,facing:south"),
         sr(MID,  HIGH, "shape:outer_right,facing:south", Variant.STAIRS,  "shape:straight,facing:south"),
         sr(HIGH, HIGH, "shape:outer_right,facing:south", Variant.STAIRS,  "shape:straight,facing:south"),
         // ---
         sr(MID,  LOW,  "shape:outer_left,facing:north", Variant.STAIRS, "shape:straight,facing:north"),
         sr(HIGH, LOW,  "shape:outer_left,facing:north", Variant.STAIRS, "shape:straight,facing:north"),
         sr(LOW,  MID,  "shape:outer_left,facing:north", Variant.STAIRS,  "shape:straight,facing:west"),
         sr(MID,  MID,  "shape:outer_left,facing:north", Variant.SOLID,   ""),
         sr(HIGH, MID,  "shape:outer_left,facing:north", Variant.STAIRS, "shape:straight,facing:north"),
         sr(LOW,  HIGH, "shape:outer_left,facing:north", Variant.STAIRS,  "shape:straight,facing:west"),
         sr(MID,  HIGH, "shape:outer_left,facing:north", Variant.STAIRS,  "shape:straight,facing:west"),
         sr(HIGH, HIGH, "shape:outer_left,facing:north", Variant.SOLID,   ""),
         // ---
         sr(LOW,  LOW,  "shape:outer_right,facing:north", Variant.STAIRS,   "shape:straight,facing:north"),
         sr(MID,  LOW,  "shape:outer_right,facing:north", Variant.STAIRS,   "shape:straight,facing:north"),
         sr(LOW,  MID,  "shape:outer_right,facing:north", Variant.STAIRS,   "shape:straight,facing:north"),
         sr(MID,  MID,  "shape:outer_right,facing:north", Variant.SOLID,    ""),
         sr(HIGH, MID,  "shape:outer_right,facing:north", Variant.STAIRS,   "shape:straight,facing:east"),
         sr(LOW,  HIGH, "shape:outer_right,facing:north", Variant.SOLID,    ""),
         sr(MID,  HIGH, "shape:outer_right,facing:north", Variant.STAIRS,   "shape:straight,facing:east"),
         sr(HIGH, HIGH, "shape:outer_right,facing:north", Variant.STAIRS,   "shape:straight,facing:east")
      )));

      // ===================================================================
      // WALL
      // ===================================================================

      // --- side faces (N/S/E/W) ----------------------------------

      LIST.addAll(trAllSides(Variant.WALL, List.of(
         // sr(MID,  ALL,  "{dir.face}:!none", Variant.WALL, "{dir.face}:none"),
      )));

      // --- UP/DOWN faces -----------------------------------------

      LIST.addAll(trBothVertical(Variant.WALL, List.of(
         // sr(LOW,  MID,  "west:!none",  Variant.WALL,   "west:none"),
      )));

   }
}
