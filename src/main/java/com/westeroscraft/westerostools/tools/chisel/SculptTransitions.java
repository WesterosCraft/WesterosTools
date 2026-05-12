package com.westeroscraft.westerostools.tools.chisel;

import com.sk89q.worldedit.util.Direction;

import com.westeroscraft.westerostools.BlockDef.Variant;

import java.util.ArrayList;
import java.util.List;

import static com.westeroscraft.westerostools.tools.chisel.ChiselHelper.UVBin.*;
import static com.westeroscraft.westerostools.tools.chisel.ChiselHelper.halfSplit;
import static com.westeroscraft.westerostools.tools.chisel.ChiselHelper.sr;
import static com.westeroscraft.westerostools.tools.chisel.ChiselHelper.trAllSides;
import static com.westeroscraft.westerostools.tools.chisel.ChiselHelper.trFace;
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
         sr(LOW,  LOW,  "{dir.face}:!none,{dir.cw}:!none,{dir.ccw}:!none", Variant.STAIRS,  "shape:inner_left,facing:{dir.opp},half:bottom"),
         sr(LOW,  MID,  "{dir.face}:!none,{dir.cw}:!none,{dir.ccw}:!none", Variant.SOLID,   ""),
         sr(LOW,  HIGH, "{dir.face}:!none,{dir.cw}:!none,{dir.ccw}:!none", Variant.STAIRS,  "shape:inner_left,facing:{dir.opp},half:top"),
         sr(MID,  LOW,  "{dir.face}:!none,{dir.cw}:!none,{dir.ccw}:!none", Variant.STAIRS,  "shape:straight,facing:{dir.opp},half:bottom"),
         sr(MID,  MID,  "{dir.face}:!none,{dir.cw}:!none,{dir.ccw}:!none", Variant.SOLID,   ""),
         sr(MID,  HIGH, "{dir.face}:!none,{dir.cw}:!none,{dir.ccw}:!none", Variant.STAIRS,  "shape:straight,facing:{dir.opp},half:top"),
         sr(HIGH, LOW,  "{dir.face}:!none,{dir.cw}:!none,{dir.ccw}:!none", Variant.STAIRS,  "shape:inner_right,facing:{dir.opp},half:bottom"),
         sr(HIGH, MID,  "{dir.face}:!none,{dir.cw}:!none,{dir.ccw}:!none", Variant.SOLID,   ""),
         sr(HIGH, HIGH, "{dir.face}:!none,{dir.cw}:!none,{dir.ccw}:!none", Variant.STAIRS,  "shape:inner_right,facing:{dir.opp},half:top"),
         // ---
         sr(LOW,  LOW,  "{dir.face}:none,{dir.cw}:!none,{dir.ccw}:!none",  Variant.STAIRS,  "shape:inner_left,facing:{dir.opp},half:bottom"),
         sr(LOW,  MID,  "{dir.face}:none,{dir.cw}:!none,{dir.ccw}:!none",  Variant.SOLID,   ""),
         sr(LOW,  HIGH, "{dir.face}:none,{dir.cw}:!none,{dir.ccw}:!none",  Variant.STAIRS,  "shape:inner_left,facing:{dir.opp},half:top"),
         sr(MID,  LOW,  "{dir.face}:none,{dir.cw}:!none,{dir.ccw}:!none",  Variant.STAIRS,  "shape:straight,facing:{dir.opp},half:bottom"),
         sr(MID,  MID,  "{dir.face}:none,{dir.cw}:!none,{dir.ccw}:!none",  Variant.WALL,    "{dir.face}:low"),
         sr(MID,  HIGH, "{dir.face}:none,{dir.cw}:!none,{dir.ccw}:!none",  Variant.STAIRS,  "shape:straight,facing:{dir.opp},half:top"),
         sr(HIGH, LOW,  "{dir.face}:none,{dir.cw}:!none,{dir.ccw}:!none",  Variant.STAIRS,  "shape:inner_right,facing:{dir.opp},half:bottom"),
         sr(HIGH, MID,  "{dir.face}:none,{dir.cw}:!none,{dir.ccw}:!none",  Variant.SOLID,   ""),
         sr(HIGH, HIGH, "{dir.face}:none,{dir.cw}:!none,{dir.ccw}:!none",  Variant.STAIRS,  "shape:inner_right,facing:{dir.opp},half:top"),
         // ---
         sr(ALL,  ALL,  "{dir.face}:none,{dir.cw}:!none,{dir.ccw}:none",   Variant.WALL,    "{dir.face}:low"),
         sr(ALL,  ALL,  "{dir.face}:none,{dir.cw}:none,{dir.ccw}:!none",   Variant.WALL,    "{dir.face}:low"),
         sr(ALL,  ALL,  "{dir.face}:none,{dir.cw}:none,{dir.ccw}:none,up:true",   Variant.WALL,    "{dir.face}:low"),
         // ---
         sr(ALL,  ALL,  "{dir.face}:none,{dir.cw}:none,{dir.ccw}:none,{dir.opp}:!none,up:false",   Variant.WALL,    "up:true"),
         sr(ALL,  ALL,  "{dir.face}:none,{dir.cw}:!none,{dir.ccw}:none,{dir.opp}:!none,up:false",  Variant.WALL,    "up:true"),
         sr(ALL,  ALL,  "{dir.face}:none,{dir.cw}:none,{dir.ccw}:!none,{dir.opp}:!none,up:false",  Variant.WALL,    "up:true")
      )));

      // --- UP/DOWN faces -----------------------------------------

      LIST.addAll(trBothVertical(Variant.WALL, List.of(
         sr(ALL,  ALL,  "up:false",  Variant.WALL,   "up:true"),
         sr(ALL,  ALL,  "up:true",   Variant.SOLID,   "")
      )));

      // ===================================================================
      // FENCE
      // ===================================================================
      
      // --- side faces (N/S/E/W) ----------------------------------

      LIST.addAll(trAllSides(Variant.FENCE, List.of(
         sr(ALL,  ALL,  "{dir.face}:true,{dir.cw}:true,{dir.opp}:true,{dir.ccw}:true",     Variant.WALL,  "{dir.face}:low,{dir.cw}:low,{dir.opp}:low,{dir.ccw}:low"),
         // ---
         sr(ALL,  ALL,  "{dir.face}:true,{dir.cw}:true,{dir.opp}:false,{dir.ccw}:true",    Variant.WALL,  "{dir.face}:low,{dir.cw}:low,{dir.opp}:none,{dir.ccw}:low"),
         sr(ALL,  ALL,  "{dir.face}:true,{dir.cw}:false,{dir.opp}:true,{dir.ccw}:true",    Variant.WALL,  "{dir.face}:low,{dir.cw}:none,{dir.opp}:low,{dir.ccw}:low"),
         sr(ALL,  ALL,  "{dir.face}:true,{dir.cw}:true,{dir.opp}:true,{dir.ccw}:false",    Variant.WALL,  "{dir.face}:low,{dir.cw}:low,{dir.opp}:low,{dir.ccw}:none"),
         sr(LOW,  ALL,  "{dir.face}:false,{dir.cw}:true,{dir.opp}:true,{dir.ccw}:true",    Variant.WALL,  "{dir.face}:none,{dir.cw}:low,{dir.opp}:low,{dir.ccw}:low"),
         sr(MID,  ALL,  "{dir.face}:false,{dir.cw}:true,{dir.opp}:true,{dir.ccw}:true",    Variant.FENCE, "{dir.face}:true,{dir.cw}:true,{dir.opp}:true,{dir.ccw}:true"),
         sr(HIGH, ALL,  "{dir.face}:false,{dir.cw}:true,{dir.opp}:true,{dir.ccw}:true",    Variant.WALL,  "{dir.face}:none,{dir.cw}:low,{dir.opp}:low,{dir.ccw}:low"),
         // ---
         sr(LOW,  ALL,  "{dir.face}:false,{dir.cw}:true,{dir.opp}:false,{dir.ccw}:true",   Variant.WALL,  "{dir.face}:none,{dir.cw}:low,{dir.opp}:none,{dir.ccw}:low"),
         sr(MID,  ALL,  "{dir.face}:false,{dir.cw}:true,{dir.opp}:false,{dir.ccw}:true",   Variant.FENCE, "{dir.face}:true,{dir.cw}:true,{dir.opp}:false,{dir.ccw}:true"),
         sr(HIGH, ALL,  "{dir.face}:false,{dir.cw}:true,{dir.opp}:false,{dir.ccw}:true",   Variant.WALL,  "{dir.face}:none,{dir.cw}:low,{dir.opp}:none,{dir.ccw}:low"),
         sr(ALL,  ALL,  "{dir.face}:true,{dir.cw}:false,{dir.opp}:true,{dir.ccw}:false",   Variant.WALL,  "{dir.face}:low,{dir.cw}:none,{dir.opp}:low,{dir.ccw}:none"),
         // ---
         sr(ALL,  ALL,  "{dir.face}:true,{dir.cw}:false,{dir.opp}:false,{dir.ccw}:false",  Variant.WALL,  "{dir.face}:low,{dir.cw}:none,{dir.opp}:none,{dir.ccw}:none"),
         sr(LOW,  ALL,  "{dir.face}:false,{dir.cw}:true,{dir.opp}:false,{dir.ccw}:false",  Variant.WALL,  "{dir.face}:none,{dir.cw}:low,{dir.opp}:none,{dir.ccw}:none"),
         sr(MID,  ALL,  "{dir.face}:false,{dir.cw}:true,{dir.opp}:false,{dir.ccw}:false",  Variant.FENCE, "{dir.face}:true,{dir.cw}:true,{dir.opp}:false,{dir.ccw}:false"),
         sr(ALL,  ALL,  "{dir.face}:false,{dir.cw}:false,{dir.opp}:true,{dir.ccw}:false",  Variant.FENCE, "{dir.face}:true,{dir.cw}:false,{dir.opp}:true,{dir.ccw}:false"),
         sr(MID,  ALL,  "{dir.face}:false,{dir.cw}:false,{dir.opp}:false,{dir.ccw}:true",  Variant.FENCE, "{dir.face}:true,{dir.cw}:false,{dir.opp}:false,{dir.ccw}:true"),
         sr(HIGH, ALL,  "{dir.face}:false,{dir.cw}:false,{dir.opp}:false,{dir.ccw}:true",  Variant.WALL,  "{dir.face}:none,{dir.cw}:none,{dir.opp}:none,{dir.ccw}:low"),
         // ---
         sr(ALL, ALL,   "{dir.face}:false,{dir.cw}:false,{dir.opp}:false,{dir.ccw}:false", Variant.FENCE, "{dir.face}:true,{dir.cw}:false,{dir.opp}:false,{dir.ccw}:false")
      )));

      // --- UP/DOWN faces -----------------------------------------

      LIST.addAll(trBothVertical(Variant.FENCE, List.of(
         sr(ALL,  ALL,  "east:true,south:true,west:true,north:true",     Variant.WALL,  "east:low,south:low,west:low,north:low"),
         // ---
         sr(ALL,  ALL,  "east:true,south:true,west:false,north:true",    Variant.WALL,  "east:low,south:low,west:none,north:low"),
         sr(ALL,  ALL,  "east:true,south:false,west:true,north:true",    Variant.WALL,  "east:low,south:none,west:low,north:low"),
         sr(ALL,  ALL,  "east:true,south:true,west:true,north:false",    Variant.WALL,  "east:low,south:low,west:low,north:none"),
         sr(ALL,  ALL,  "east:false,south:true,west:true,north:true",    Variant.WALL,  "east:none,south:low,west:low,north:low"),
         // ---
         sr(ALL,  ALL,  "east:false,south:true,west:false,north:true",   Variant.WALL,  "east:none,south:low,west:none,north:low"),
         sr(ALL,  ALL,  "east:true,south:false,west:true,north:false",   Variant.WALL,  "east:low,south:none,west:low,north:none"),
         // ---
         sr(ALL,  ALL,  "east:true,south:false,west:false,north:false",  Variant.WALL,  "east:low,south:none,west:none,north:none"),
         sr(ALL,  ALL,  "east:false,south:true,west:false,north:false",  Variant.WALL,  "east:none,south:low,west:none,north:none"),
         sr(ALL,  ALL,  "east:false,south:false,west:true,north:false",  Variant.WALL,  "east:none,south:none,west:low,north:none"),
         sr(ALL,  ALL,  "east:false,south:false,west:false,north:true",  Variant.WALL,  "east:none,south:none,west:none,north:low"),
         // ---
         sr(ALL,  ALL,  "east:false,south:false,west:false,north:false", Variant.WALL,  "east:none,south:none,west:none,north:none")
      )));

      // ===================================================================
      // SLAB
      // ===================================================================

      LIST.addAll(trFace(Direction.UP, Variant.SLAB, List.of(
         sr(LOW,  LOW,  "type:bottom",   Variant.STAIRS,  "half:bottom,shape:outer_right,facing:west"),
         sr(MID,  LOW,  "type:bottom",   Variant.STAIRS,  "half:bottom,shape:straight,facing:north"),
         sr(HIGH, LOW,  "type:bottom",   Variant.STAIRS,  "half:bottom,shape:outer_right,facing:north"),
         sr(LOW,  MID,  "type:bottom",   Variant.STAIRS,  "half:bottom,shape:straight,facing:west"),
         sr(MID,  MID,  "type:bottom",   Variant.SOLID,   ""),
         sr(HIGH, MID,  "type:bottom",   Variant.STAIRS,  "half:bottom,shape:straight,facing:east"),
         sr(LOW,  HIGH, "type:bottom",   Variant.STAIRS,  "half:bottom,shape:outer_right,facing:south"),
         sr(MID,  HIGH, "type:bottom",   Variant.STAIRS,  "half:bottom,shape:straight,facing:south"),
         sr(HIGH, HIGH, "type:bottom",   Variant.STAIRS,  "half:bottom,shape:outer_right,facing:east")
      )));

      LIST.addAll(trFace(Direction.DOWN, Variant.SLAB, List.of(
         sr(LOW,  LOW,  "type:top",   Variant.STAIRS,  "half:top,shape:outer_right,facing:west"),
         sr(MID,  LOW,  "type:top",   Variant.STAIRS,  "half:top,shape:straight,facing:north"),
         sr(HIGH, LOW,  "type:top",   Variant.STAIRS,  "half:top,shape:outer_right,facing:north"),
         sr(LOW,  MID,  "type:top",   Variant.STAIRS,  "half:top,shape:straight,facing:west"),
         sr(MID,  MID,  "type:top",   Variant.SOLID,   ""),
         sr(HIGH, MID,  "type:top",   Variant.STAIRS,  "half:top,shape:straight,facing:east"),
         sr(LOW,  HIGH, "type:top",   Variant.STAIRS,  "half:bottom,shape:outer_right,facing:south"),
         sr(MID,  HIGH, "type:top",   Variant.STAIRS,  "half:bottom,shape:straight,facing:south"),
         sr(HIGH, HIGH, "type:top",   Variant.STAIRS,  "half:bottom,shape:outer_right,facing:east")
      )));

      // ===================================================================
      // PATH
      // ===================================================================

      // --- UP/DOWN faces -----------------------------------------

      LIST.addAll(trBothVertical(Variant.PATH, List.of(
         sr(ALL,  ALL,  Variant.SOLID,  "")
      )));

      // ===================================================================
      // LAYER
      // ===================================================================

      // --- UP/DOWN faces -----------------------------------------

      LIST.addAll(trBothVertical(Variant.LAYER, List.of(
         sr(ALL,  ALL,  "layers:7",  Variant.PATH,  ""),
         sr(ALL,  ALL,  "layers:7",  Variant.SOLID, ""),
         sr(ALL,  ALL,  "layers:6",  Variant.LAYER,  "layers:7"),
         sr(ALL,  ALL,  "layers:5",  Variant.LAYER,  "layers:6"),
         sr(ALL,  ALL,  "layers:4",  Variant.LAYER,  "layers:5"),
         sr(ALL,  ALL,  "layers:3",  Variant.LAYER,  "layers:4"),
         sr(ALL,  ALL,  "layers:2",  Variant.LAYER,  "layers:3"),
         sr(ALL,  ALL,  "layers:1",  Variant.LAYER,  "layers:2")
      )));

      // ===================================================================
      // CARPET
      // ===================================================================

      // --- UP/DOWN faces -----------------------------------------

      LIST.addAll(trBothVertical(Variant.CARPET, List.of(
         sr(ALL,  ALL,  Variant.SLAB,  "type:bottom")
      )));

   }
}
