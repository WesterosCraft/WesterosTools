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
 * Primary (left-click) chisel transitions: solid → variant.
 *
 * UV orientation quick-reference (LOW/MID/HIGH bins):
 *
 *   NORTH face:  u=LOW→east,  u=HIGH→west,  v=LOW→bottom, v=HIGH→top
 *   SOUTH face:  u=LOW→west,  u=HIGH→east,  v=LOW→bottom, v=HIGH→top
 *   EAST  face:  u=LOW→south, u=HIGH→north, v=LOW→bottom, v=HIGH→top
 *   WEST  face:  u=LOW→north, u=HIGH→south, v=LOW→bottom, v=HIGH→top
 *   UP    face:  u=LOW→west,  u=HIGH→east,  v=LOW→north,  v=HIGH→south
 *   DOWN  face:  u=LOW→west,  u=HIGH→east,  v=LOW→north,  v=HIGH→south  (same as UP; (LOW,LOW)=NW for both)
 *
 * Template tokens (see {@link ChiselHelper.SideRule} for full docs):
 *   {half}      → "bottom" (UP) / "top" (DOWN) / "" (side)
 *   {dir.face}  → clicked face name, lowercase         (side faces only)
 *   {dir.opp}   → opposite direction, lowercase         (side faces only)
 *   {dir.cw}    → face rotated 90° CW, lowercase        (side faces only)
 *   {dir.ccw}   → face rotated 90° CCW, lowercase       (side faces only)
 *   {dir.v}     → v-bin direction, lowercase            (UP/DOWN only)
 *   {dir.v+90}  → {dir.v} rotated 90° CW               (UP/DOWN only)
 *   {dir.v-90}  → {dir.v} rotated 90° CCW              (UP/DOWN only)
 *   {dir.v+180} → {dir.v} rotated 180°                 (UP/DOWN only)
 *   {dir.u}          → u-bin direction, lowercase            (UP/DOWN only)
 *   {dir.u+90}  → {dir.u} rotated 90° CW               (UP/DOWN only)
 *   {dir.u-90}  → {dir.u} rotated 90° CCW              (UP/DOWN only)
 *   {dir.u+180} → opposite of {dir.u} (u=LOW→west, u=HIGH→east) (UP/DOWN only)
 *   {corner.inner}   → "inner_right"/"inner_left" derived from dir.v and dir.u
 *                       (UP/DOWN corner cells only; see ChiselHelper.SideRule docs)
 */
class ChiselTransitions {

   static final List<ChiselHelper.Transition> LIST;
   static {
      LIST = new ArrayList<>();

      // ===================================================================
      // SOLID
      // ===================================================================

      // --- side faces (N/S/E/W) ----------------------------------

      LIST.addAll(trAllSides(Variant.SOLID, List.of(
         // --- to stairs/slab ---------
         sr(LOW,  LOW,  Variant.STAIRS, "half:top,shape:inner_right,facing:{dir.opp}"),
         sr(MID,  LOW,  Variant.STAIRS, "half:top,shape:straight,facing:{dir.opp}"),
         sr(HIGH, LOW,  Variant.STAIRS, "half:top,shape:inner_left,facing:{dir.opp}"),
         sr(LOW,  MID,  Variant.WALL,   "{dir.face}:low,{dir.opp}:none,{dir.cw}:low,{dir.ccw}:low,up:false"),
         sr(MID,  MID,  Variant.WALL,   "{dir.face}:none,{dir.opp}:none,{dir.cw}:low,{dir.ccw}:low,up:false"),
         sr(HIGH, MID,  Variant.WALL,   "{dir.face}:low,{dir.opp}:none,{dir.cw}:low,{dir.ccw}:low,up:false"),
         sr(LOW,  HIGH, Variant.STAIRS, "half:bottom,shape:inner_right,facing:{dir.opp}"),
         sr(MID,  HIGH, Variant.STAIRS, "half:bottom,shape:straight,facing:{dir.opp}"),
         sr(HIGH, HIGH, Variant.STAIRS, "half:bottom,shape:inner_left,facing:{dir.opp}")
      )));

      // --- UP/DOWN faces -----------------------------------------

      LIST.addAll(trBothVertical(Variant.SOLID, List.of(
         // --- to path/layer ---------
         sr(ALL,  ALL,  Variant.PATH,   ""),
         sr(ALL,  ALL,  Variant.LAYER,  "layers:7"),
         // --- to stairs/slab ---------
         sr(MID,  MID,  Variant.SLAB,   "type:{half}"),
         sr(MID,  LOW,  Variant.STAIRS, "half:{half},shape:straight,facing:{dir.v}"),
         sr(MID,  HIGH, Variant.STAIRS, "half:{half},shape:straight,facing:{dir.v}"),
         sr(LOW,  MID,  Variant.STAIRS, "half:{half},shape:straight,facing:{dir.u}"),
         sr(HIGH, MID,  Variant.STAIRS, "half:{half},shape:straight,facing:{dir.u}"),
         sr(LOW,  LOW,  Variant.STAIRS, "half:{half},shape:{corner.inner},facing:{dir.v}"),
         sr(HIGH, LOW,  Variant.STAIRS, "half:{half},shape:{corner.inner},facing:{dir.v}"),
         sr(LOW,  HIGH, Variant.STAIRS, "half:{half},shape:{corner.inner},facing:{dir.v}"),
         sr(HIGH, HIGH, Variant.STAIRS, "half:{half},shape:{corner.inner},facing:{dir.v}")
      )));

      // ===================================================================
      // STAIRS
      // ===================================================================

      // --- side faces (N/S/E/W) ----------------------------------

      LIST.addAll(trAllSides(Variant.STAIRS, halfSplit(List.of(
         // --- straight shapes ---------
         sr(LOW,  LOW,  "shape:straight,facing:{dir.opp}", Variant.WALL, "{dir.face}:none,{dir.opp}:none,{dir.cw}:low,{dir.ccw}:low,up:false"),
         sr(MID,  LOW,  "shape:straight,facing:{dir.opp}", Variant.WALL, "{dir.face}:none,{dir.opp}:none,{dir.cw}:low,{dir.ccw}:low,up:false"),
         sr(HIGH, LOW,  "shape:straight,facing:{dir.opp}", Variant.WALL, "{dir.face}:none,{dir.opp}:none,{dir.cw}:low,{dir.ccw}:low,up:false"),
         sr(LOW,  MID,  "shape:straight,facing:{dir.opp}", Variant.WALL, "{dir.face}:none,{dir.opp}:none,{dir.cw}:low,{dir.ccw}:low,up:false"),
         sr(MID,  MID,  "shape:straight,facing:{dir.opp}", Variant.WALL, "{dir.face}:none,{dir.opp}:none,{dir.cw}:low,{dir.ccw}:low,up:false"),
         sr(HIGH, MID,  "shape:straight,facing:{dir.opp}", Variant.WALL, "{dir.face}:none,{dir.opp}:none,{dir.cw}:low,{dir.ccw}:low,up:false"),
         sr(LOW,  HIGH, "shape:straight,facing:{dir.opp}", Variant.STAIRS, "shape:outer_right,facing:{dir.opp}"),
         sr(MID,  HIGH, "shape:straight,facing:{dir.opp}", Variant.SLAB, "type:{src.half}"),
         sr(HIGH, HIGH, "shape:straight,facing:{dir.opp}", Variant.STAIRS, "shape:outer_left,facing:{dir.opp}"),
         // ---
         sr(LOW, HIGH,  "shape:straight,facing:{dir.cw}", Variant.STAIRS, "shape:outer_left,facing:{dir.opp}"),
         sr(MID, HIGH,  "shape:straight,facing:{dir.cw}", Variant.STAIRS, "shape:outer_left,facing:{dir.opp}"),
         // ---
         sr(LOW,  HIGH, "shape:straight,facing:{dir.face}", Variant.STAIRS, "shape:outer_left,facing:{dir.face}"),
         sr(MID,  HIGH, "shape:straight,facing:{dir.face}", Variant.SLAB, "type:{src.half}"),
         sr(HIGH, HIGH, "shape:straight,facing:{dir.face}", Variant.STAIRS, "shape:outer_right,facing:{dir.face}"),
         // ---
         sr(MID, HIGH,  "shape:straight,facing:{dir.ccw}", Variant.STAIRS, "shape:outer_right,facing:{dir.opp}"),
         sr(HIGH, HIGH, "shape:straight,facing:{dir.ccw}", Variant.STAIRS, "shape:outer_right,facing:{dir.opp}"),
         // --- inner corner shapes ---------
         sr(MID,  LOW,  "shape:inner_left,facing:{dir.opp}", Variant.WALL, "{dir.face}:none,{dir.opp}:none,{dir.cw}:low,{dir.ccw}:low,up:false"),
         sr(HIGH, LOW,  "shape:inner_left,facing:{dir.opp}", Variant.WALL, "{dir.face}:low,{dir.opp}:none,{dir.cw}:low,{dir.ccw}:low,up:false"),
         sr(LOW,  MID,  "shape:inner_left,facing:{dir.opp}", Variant.STAIRS, "shape:straight,facing:{dir.opp}"),
         sr(MID,  MID,  "shape:inner_left,facing:{dir.opp}", Variant.STAIRS, "shape:straight,facing:{dir.opp}"),
         sr(HIGH, MID,  "shape:inner_left,facing:{dir.opp}", Variant.STAIRS, "shape:straight,facing:{dir.cw}"),
         sr(LOW,  HIGH, "shape:inner_left,facing:{dir.opp}", Variant.STAIRS, "shape:straight,facing:{dir.opp}"),
         sr(MID,  HIGH, "shape:inner_left,facing:{dir.opp}", Variant.STAIRS, "shape:straight,facing:{dir.opp}"),
         sr(HIGH, HIGH, "shape:inner_left,facing:{dir.opp}", Variant.STAIRS, "shape:straight,facing:{dir.cw}"),
         // ---
         sr(LOW,  LOW,  "shape:inner_right,facing:{dir.opp}", Variant.WALL, "{dir.face}:low,{dir.opp}:none,{dir.cw}:low,{dir.ccw}:low,up:false"),
         sr(MID,  LOW,  "shape:inner_right,facing:{dir.opp}", Variant.WALL, "{dir.face}:none,{dir.opp}:none,{dir.cw}:low,{dir.ccw}:low,up:false"),
         sr(LOW,  MID,  "shape:inner_right,facing:{dir.opp}", Variant.STAIRS, "shape:straight,facing:{dir.ccw}"),
         sr(MID,  MID,  "shape:inner_right,facing:{dir.opp}", Variant.STAIRS, "shape:straight,facing:{dir.opp}"),
         sr(HIGH, MID,  "shape:inner_right,facing:{dir.opp}", Variant.STAIRS, "shape:straight,facing:{dir.opp}"),
         sr(LOW,  HIGH, "shape:inner_right,facing:{dir.opp}", Variant.STAIRS, "shape:straight,facing:{dir.ccw}"),
         sr(MID,  HIGH, "shape:inner_right,facing:{dir.opp}", Variant.STAIRS, "shape:straight,facing:{dir.opp}"),
         sr(HIGH, HIGH, "shape:inner_right,facing:{dir.opp}", Variant.STAIRS, "shape:straight,facing:{dir.opp}"),
         // ---
         sr(LOW,  MID,  "shape:inner_left,facing:{dir.cw}", Variant.WALL, "{dir.face}:low,{dir.opp}:none,{dir.cw}:low,{dir.ccw}:none,up:false"),
         sr(LOW,  HIGH, "shape:inner_left,facing:{dir.cw}", Variant.STAIRS, "shape:outer_left,facing:{dir.face}"),
         sr(MID,  HIGH, "shape:inner_left,facing:{dir.cw}", Variant.STAIRS, "shape:outer_right,facing:{dir.cw}"),
         sr(HIGH, HIGH, "shape:inner_left,facing:{dir.cw}", Variant.STAIRS, "shape:straight,facing:{dir.cw}"),
         // ---
         sr(MID,  LOW,  "shape:inner_right,facing:{dir.cw}", Variant.WALL, "{dir.face}:none,{dir.opp}:none,{dir.cw}:low,{dir.ccw}:low,up:false"),
         sr(HIGH, LOW,  "shape:inner_right,facing:{dir.cw}", Variant.WALL, "{dir.face}:low,{dir.opp}:none,{dir.cw}:low,{dir.ccw}:low,up:false"),
         sr(LOW,  MID,  "shape:inner_right,facing:{dir.cw}", Variant.STAIRS, "shape:straight,facing:{dir.opp}"),
         sr(MID,  MID,  "shape:inner_right,facing:{dir.cw}", Variant.STAIRS, "shape:straight,facing:{dir.opp}"),
         sr(HIGH, MID,  "shape:inner_right,facing:{dir.cw}", Variant.STAIRS, "shape:straight,facing:{dir.cw}"),
         sr(LOW,  HIGH, "shape:inner_right,facing:{dir.cw}", Variant.STAIRS, "shape:straight,facing:{dir.opp}"),
         sr(MID,  HIGH, "shape:inner_right,facing:{dir.cw}", Variant.STAIRS, "shape:straight,facing:{dir.opp}"),
         sr(HIGH, HIGH, "shape:inner_right,facing:{dir.cw}", Variant.STAIRS, "shape:straight,facing:{dir.cw}"),
         // ---
         sr(HIGH, MID,  "shape:inner_left,facing:{dir.face}", Variant.WALL, "{dir.face}:low,{dir.opp}:none,{dir.cw}:none,{dir.ccw}:low,up:false"),
         sr(LOW,  HIGH, "shape:inner_left,facing:{dir.face}", Variant.STAIRS, "shape:straight,facing:{dir.ccw}"),
         sr(MID,  HIGH, "shape:inner_left,facing:{dir.face}", Variant.STAIRS, "shape:outer_right,facing:{dir.opp}"),
         sr(HIGH, HIGH, "shape:inner_left,facing:{dir.face}", Variant.STAIRS, "shape:outer_right,facing:{dir.face}"),
         // ---
         sr(LOW,  MID,  "shape:inner_right,facing:{dir.face}", Variant.WALL, "{dir.face}:low,{dir.opp}:none,{dir.cw}:low,{dir.ccw}:none,up:false"),
         sr(LOW,  HIGH, "shape:inner_right,facing:{dir.face}", Variant.STAIRS, "shape:outer_left,facing:{dir.face}"),
         sr(MID,  HIGH, "shape:inner_right,facing:{dir.face}", Variant.STAIRS, "shape:outer_left,facing:{dir.opp}"),
         sr(HIGH, HIGH, "shape:inner_right,facing:{dir.face}", Variant.STAIRS, "shape:straight,facing:{dir.cw}"),
         // ---
         sr(LOW,  LOW,  "shape:inner_left,facing:{dir.ccw}", Variant.WALL, "{dir.face}:low,{dir.opp}:none,{dir.cw}:low,{dir.ccw}:low,up:false"),
         sr(MID,  LOW,  "shape:inner_left,facing:{dir.ccw}", Variant.WALL, "{dir.face}:none,{dir.opp}:none,{dir.cw}:low,{dir.ccw}:low,up:false"),
         sr(LOW,  MID,  "shape:inner_left,facing:{dir.ccw}", Variant.STAIRS, "shape:straight,facing:{dir.ccw}"),
         sr(MID,  MID,  "shape:inner_left,facing:{dir.ccw}", Variant.STAIRS, "shape:straight,facing:{dir.opp}"),
         sr(HIGH, MID,  "shape:inner_left,facing:{dir.ccw}", Variant.STAIRS, "shape:straight,facing:{dir.opp}"),
         sr(LOW,  HIGH, "shape:inner_left,facing:{dir.ccw}", Variant.STAIRS, "shape:straight,facing:{dir.ccw}"),
         sr(MID,  HIGH, "shape:inner_left,facing:{dir.ccw}", Variant.STAIRS, "shape:straight,facing:{dir.opp}"),
         sr(HIGH, HIGH, "shape:inner_left,facing:{dir.ccw}", Variant.STAIRS, "shape:straight,facing:{dir.opp}"),
         // ---
         sr(HIGH, MID,  "shape:inner_right,facing:{dir.ccw}", Variant.WALL, "{dir.face}:low,{dir.opp}:none,{dir.cw}:none,{dir.ccw}:low,up:false"),
         sr(LOW,  HIGH, "shape:inner_right,facing:{dir.ccw}", Variant.STAIRS, "shape:straight,facing:{dir.ccw}"),
         sr(MID,  HIGH, "shape:inner_right,facing:{dir.ccw}", Variant.STAIRS, "shape:outer_left,facing:{dir.ccw}"),
         sr(HIGH, HIGH, "shape:inner_right,facing:{dir.ccw}", Variant.STAIRS, "shape:outer_right,facing:{dir.face}"),
         // --- outer corner shapes ---------
         sr(LOW,  HIGH, "shape:outer_left,facing:{dir.opp}", Variant.SLAB, "type:{src.half}"),
         sr(MID,  HIGH, "shape:outer_left,facing:{dir.opp}", Variant.SLAB, "type:{src.half}"),
         // ---
         sr(MID,  HIGH, "shape:outer_right,facing:{dir.opp}", Variant.SLAB, "type:{src.half}"),
         sr(HIGH, HIGH, "shape:outer_right,facing:{dir.opp}", Variant.SLAB, "type:{src.half}"),
         // ---
         sr(LOW,  HIGH, "shape:outer_left,facing:{dir.cw}", Variant.SLAB, "type:{src.half}"),
         sr(MID,  HIGH, "shape:outer_left,facing:{dir.cw}", Variant.SLAB, "type:{src.half}"),
         // ---
         sr(LOW,  HIGH, "shape:outer_right,facing:{dir.cw}", Variant.SLAB, "type:{src.half}"),
         sr(MID,  HIGH, "shape:outer_right,facing:{dir.cw}", Variant.SLAB, "type:{src.half}"),
         // ---
         sr(MID,  HIGH, "shape:outer_left,facing:{dir.face}", Variant.SLAB, "type:{src.half}"),
         sr(HIGH, HIGH, "shape:outer_left,facing:{dir.face}", Variant.SLAB, "type:{src.half}"),
         // ---
         sr(LOW,  HIGH, "shape:outer_right,facing:{dir.face}", Variant.SLAB, "type:{src.half}"),
         sr(MID,  HIGH, "shape:outer_right,facing:{dir.face}", Variant.SLAB, "type:{src.half}"),
         // ---
         sr(MID,  HIGH, "shape:outer_left,facing:{dir.ccw}", Variant.SLAB, "type:{src.half}"),
         sr(HIGH, HIGH, "shape:outer_left,facing:{dir.ccw}", Variant.SLAB, "type:{src.half}"),
         // ---
         sr(MID,  HIGH, "shape:outer_right,facing:{dir.ccw}", Variant.SLAB, "type:{src.half}"),
         sr(HIGH, HIGH, "shape:outer_right,facing:{dir.ccw}", Variant.SLAB, "type:{src.half}")
      ))));

      // --- UP/DOWN faces -----------------------------------------

      LIST.addAll(trBothVertical(Variant.STAIRS, List.of(
         // --- straight shapes ---------
         sr(LOW,  LOW,  "shape:straight,facing:east", Variant.WALL,   "north:low,south:low,east:none,west:none,up:false"),
         sr(MID,  LOW,  "shape:straight,facing:east", Variant.STAIRS, "shape:outer_right,facing:east"),
         sr(HIGH, LOW,  "shape:straight,facing:east", Variant.STAIRS, "shape:outer_right,facing:east"),
         sr(LOW,  MID,  "shape:straight,facing:east", Variant.WALL,   "north:low,south:low,east:none,west:none,up:false"),
         sr(MID,  MID,  "shape:straight,facing:east", Variant.SLAB,   "type:{src.half}"),
         sr(HIGH, MID,  "shape:straight,facing:east", Variant.SLAB,   "type:{src.half}"),
         sr(LOW,  HIGH, "shape:straight,facing:east", Variant.WALL,   "north:low,south:low,east:none,west:none,up:false"),
         sr(MID,  HIGH, "shape:straight,facing:east", Variant.STAIRS, "shape:outer_left,facing:east"),
         sr(HIGH, HIGH, "shape:straight,facing:east", Variant.STAIRS, "shape:outer_left,facing:east"),
         // ---
         sr(LOW,  LOW,  "shape:straight,facing:west", Variant.STAIRS, "shape:outer_left,facing:west"),
         sr(MID,  LOW,  "shape:straight,facing:west", Variant.STAIRS, "shape:outer_left,facing:west"),
         sr(HIGH, LOW,  "shape:straight,facing:west", Variant.WALL,   "north:low,south:low,east:none,west:none,up:false"),
         sr(LOW,  MID,  "shape:straight,facing:west", Variant.SLAB,   "type:{src.half}"),
         sr(MID,  MID,  "shape:straight,facing:west", Variant.SLAB,   "type:{src.half}"),
         sr(HIGH, MID,  "shape:straight,facing:west", Variant.WALL,   "north:low,south:low,east:none,west:none,up:false"),
         sr(LOW,  HIGH, "shape:straight,facing:west", Variant.STAIRS, "shape:outer_right,facing:west"),
         sr(MID,  HIGH, "shape:straight,facing:west", Variant.STAIRS, "shape:outer_right,facing:west"),
         sr(HIGH, HIGH, "shape:straight,facing:west", Variant.WALL,   "north:low,south:low,east:none,west:none,up:false"),
         // ---
         sr(LOW,  LOW,  "shape:straight,facing:south", Variant.WALL,   "north:none,south:none,east:low,west:low,up:false"),
         sr(MID,  LOW,  "shape:straight,facing:south", Variant.WALL,   "north:none,south:none,east:low,west:low,up:false"),
         sr(HIGH, LOW,  "shape:straight,facing:south", Variant.WALL,   "north:none,south:none,east:low,west:low,up:false"),
         sr(LOW,  MID,  "shape:straight,facing:south", Variant.STAIRS, "shape:outer_left,facing:south"),
         sr(MID,  MID,  "shape:straight,facing:south", Variant.SLAB,   "type:{src.half}"),
         sr(HIGH, MID,  "shape:straight,facing:south", Variant.STAIRS, "shape:outer_right,facing:south"),
         sr(LOW,  HIGH, "shape:straight,facing:south", Variant.STAIRS, "shape:outer_left,facing:south"),
         sr(MID,  HIGH, "shape:straight,facing:south", Variant.SLAB,   "type:{src.half}"),
         sr(HIGH, HIGH, "shape:straight,facing:south", Variant.STAIRS, "shape:outer_right,facing:south"),
         // ---
         sr(LOW,  LOW,  "shape:straight,facing:north", Variant.STAIRS, "shape:outer_right,facing:north"),
         sr(MID,  LOW,  "shape:straight,facing:north", Variant.SLAB,   "type:{src.half}"),
         sr(HIGH, LOW,  "shape:straight,facing:north", Variant.STAIRS, "shape:outer_left,facing:north"),
         sr(LOW,  MID,  "shape:straight,facing:north", Variant.STAIRS, "shape:outer_right,facing:north"),
         sr(MID,  MID,  "shape:straight,facing:north", Variant.SLAB,   "type:{src.half}"),
         sr(HIGH, MID,  "shape:straight,facing:north", Variant.STAIRS, "shape:outer_left,facing:north"),
         sr(LOW,  HIGH, "shape:straight,facing:north", Variant.WALL,   "north:none,south:none,east:low,west:low,up:false"),
         sr(MID,  HIGH, "shape:straight,facing:north", Variant.WALL,   "north:none,south:none,east:low,west:low,up:false"),
         sr(HIGH, HIGH, "shape:straight,facing:north", Variant.WALL,   "north:none,south:none,east:low,west:low,up:false"),
         // --- inner corner shapes ---------
         sr(LOW,  LOW,  "shape:inner_left,facing:east", Variant.STAIRS,   "shape:straight,facing:east"),
         sr(MID,  LOW,  "shape:inner_left,facing:east", Variant.STAIRS,   "shape:outer_right,facing:east"),
         sr(HIGH, LOW,  "shape:inner_left,facing:east", Variant.STAIRS,   "shape:outer_right,facing:east"),
         sr(LOW,  MID,  "shape:inner_left,facing:east", Variant.STAIRS,   "shape:straight,facing:east"),
         sr(MID,  MID,  "shape:inner_left,facing:east", Variant.SLAB,     "type:{src.half}"),
         sr(HIGH, MID,  "shape:inner_left,facing:east", Variant.STAIRS,   "shape:outer_right,facing:west"),
         sr(LOW,  HIGH, "shape:inner_left,facing:east", Variant.WALL,     "north:low,south:low,east:none,west:low,up:false"),
         sr(MID,  HIGH, "shape:inner_left,facing:east", Variant.STAIRS,   "shape:straight,facing:north"),
         sr(HIGH, HIGH, "shape:inner_left,facing:east", Variant.STAIRS,   "shape:straight,facing:north"),
         // ---
         sr(LOW,  LOW,  "shape:inner_right,facing:east", Variant.WALL,     "north:low,south:low,east:none,west:low,up:false"),
         sr(MID,  LOW,  "shape:inner_right,facing:east", Variant.STAIRS,   "shape:straight,facing:south"),
         sr(HIGH, LOW,  "shape:inner_right,facing:east", Variant.STAIRS,   "shape:straight,facing:south"),
         sr(LOW,  MID,  "shape:inner_right,facing:east", Variant.STAIRS,   "shape:straight,facing:east"),
         sr(MID,  MID,  "shape:inner_right,facing:east", Variant.SLAB,     "type:{src.half}"),
         sr(HIGH, MID,  "shape:inner_right,facing:east", Variant.STAIRS,   "shape:outer_left,facing:west"),
         sr(LOW,  HIGH, "shape:inner_right,facing:east", Variant.STAIRS,   "shape:straight,facing:east"),
         sr(MID,  HIGH, "shape:inner_right,facing:east", Variant.STAIRS,   "shape:outer_left,facing:east"),
         sr(HIGH, HIGH, "shape:inner_right,facing:east", Variant.STAIRS,   "shape:outer_left,facing:east"),
         // ---
         sr(LOW,  LOW,  "shape:inner_left,facing:west", Variant.STAIRS,   "shape:straight,facing:south"),
         sr(MID,  LOW,  "shape:inner_left,facing:west", Variant.STAIRS,   "shape:straight,facing:south"),
         sr(HIGH, LOW,  "shape:inner_left,facing:west", Variant.WALL,     "north:low,south:low,east:low,west:none,up:false"),
         sr(LOW,  MID,  "shape:inner_left,facing:west", Variant.STAIRS,   "shape:outer_right,facing:east"),
         sr(MID,  MID,  "shape:inner_left,facing:west", Variant.SLAB,     "type:{src.half}"),
         sr(HIGH, MID,  "shape:inner_left,facing:west", Variant.STAIRS,   "shape:straight,facing:west"),
         sr(LOW,  HIGH, "shape:inner_left,facing:west", Variant.STAIRS,   "shape:outer_right,facing:west"),
         sr(MID,  HIGH, "shape:inner_left,facing:west", Variant.STAIRS,   "shape:outer_right,facing:west"),
         sr(HIGH, HIGH, "shape:inner_left,facing:west", Variant.STAIRS,   "shape:straight,facing:west"),
         // ---
         sr(LOW,  LOW,  "shape:inner_right,facing:west", Variant.STAIRS,   "shape:outer_left,facing:west"),
         sr(MID,  LOW,  "shape:inner_right,facing:west", Variant.STAIRS,   "shape:outer_left,facing:west"),
         sr(HIGH, LOW,  "shape:inner_right,facing:west", Variant.STAIRS,   "shape:straight,facing:west"),
         sr(LOW,  MID,  "shape:inner_right,facing:west", Variant.STAIRS,   "shape:outer_right,facing:east"),
         sr(MID,  MID,  "shape:inner_right,facing:west", Variant.SLAB,     "type:{src.half}"),
         sr(HIGH, MID,  "shape:inner_right,facing:west", Variant.STAIRS,   "shape:straight,facing:west"),
         sr(LOW,  HIGH, "shape:inner_right,facing:west", Variant.STAIRS,   "shape:straight,facing:north"),
         sr(MID,  HIGH, "shape:inner_right,facing:west", Variant.STAIRS,   "shape:straight,facing:north"),
         sr(HIGH, HIGH, "shape:inner_right,facing:west", Variant.WALL,     "north:low,south:low,east:low,west:none,up:false"),
         // ---
         sr(LOW,  LOW,  "shape:inner_left,facing:south", Variant.WALL,     "west:low,east:low,south:none,north:low,up:false"),
         sr(MID,  LOW,  "shape:inner_left,facing:south", Variant.STAIRS,   "shape:straight,facing:south"),
         sr(HIGH, LOW,  "shape:inner_left,facing:south", Variant.STAIRS,   "shape:straight,facing:south"),
         sr(LOW,  MID,  "shape:inner_left,facing:south", Variant.STAIRS,   "shape:straight,facing:east"),
         sr(MID,  MID,  "shape:inner_left,facing:south", Variant.SLAB,     "type:{src.half}"),
         sr(HIGH, MID,  "shape:inner_left,facing:south", Variant.STAIRS,   "shape:outer_right,facing:south"),
         sr(LOW,  HIGH, "shape:inner_left,facing:south", Variant.STAIRS,   "shape:straight,facing:east"),
         sr(MID,  HIGH, "shape:inner_left,facing:south", Variant.STAIRS,   "shape:outer_right,facing:north"),
         sr(HIGH, HIGH, "shape:inner_left,facing:south", Variant.STAIRS,   "shape:outer_right,facing:south"),
         // ---
         sr(LOW,  LOW,  "shape:inner_right,facing:south", Variant.STAIRS,   "shape:straight,facing:south"),
         sr(MID,  LOW,  "shape:inner_right,facing:south", Variant.STAIRS,   "shape:straight,facing:south"),
         sr(HIGH, LOW,  "shape:inner_right,facing:south", Variant.WALL,     "west:low,east:low,south:none,north:low,up:false"),
         sr(LOW,  MID,  "shape:inner_right,facing:south", Variant.STAIRS,   "shape:outer_left,facing:south"),
         sr(MID,  MID,  "shape:inner_right,facing:south", Variant.SLAB,     "type:{src.half}"),
         sr(HIGH, MID,  "shape:inner_right,facing:south", Variant.STAIRS,   "shape:straight,facing:west"),
         sr(LOW,  HIGH, "shape:inner_right,facing:south", Variant.STAIRS,   "shape:outer_left,facing:south"),
         sr(MID,  HIGH, "shape:inner_right,facing:south", Variant.STAIRS,   "shape:outer_left,facing:north"),
         sr(HIGH, HIGH, "shape:inner_right,facing:south", Variant.STAIRS,   "shape:straight,facing:west"),
         // ---
         sr(LOW,  LOW,  "shape:inner_left,facing:north", Variant.STAIRS,   "shape:outer_right,facing:north"),
         sr(MID,  LOW,  "shape:inner_left,facing:north", Variant.STAIRS,   "shape:outer_right,facing:south"),
         sr(HIGH, LOW,  "shape:inner_left,facing:north", Variant.STAIRS,   "shape:straight,facing:west"),
         sr(LOW,  MID,  "shape:inner_left,facing:north", Variant.STAIRS,   "shape:outer_right,facing:north"),
         sr(MID,  MID,  "shape:inner_left,facing:north", Variant.SLAB,     "type:{src.half}"),
         sr(HIGH, MID,  "shape:inner_left,facing:north", Variant.STAIRS,   "shape:straight,facing:west"),
         sr(LOW,  HIGH, "shape:inner_left,facing:north", Variant.STAIRS,   "shape:straight,facing:north"),
         sr(MID,  HIGH, "shape:inner_left,facing:north", Variant.STAIRS,   "shape:straight,facing:north"),
         sr(HIGH, HIGH, "shape:inner_left,facing:north", Variant.WALL,     "west:low,east:low,south:low,north:none,up:false"),
         // ---
         sr(LOW,  LOW,  "shape:inner_right,facing:north", Variant.STAIRS,   "shape:straight,facing:east"),
         sr(MID,  LOW,  "shape:inner_right,facing:north", Variant.STAIRS,   "shape:outer_left,facing:south"),
         sr(HIGH, LOW,  "shape:inner_right,facing:north", Variant.STAIRS,   "shape:outer_left,facing:north"),
         sr(LOW,  MID,  "shape:inner_right,facing:north", Variant.STAIRS,   "shape:straight,facing:east"),
         sr(MID,  MID,  "shape:inner_right,facing:north", Variant.SLAB,     "type:{src.half}"),
         sr(HIGH, MID,  "shape:inner_right,facing:north", Variant.STAIRS,   "shape:outer_left,facing:north"),
         sr(LOW,  HIGH, "shape:inner_right,facing:north", Variant.WALL,     "west:low,east:low,south:low,north:none,up:false"),
         sr(MID,  HIGH, "shape:inner_right,facing:north", Variant.STAIRS,   "shape:straight,facing:north"),
         sr(HIGH, HIGH, "shape:inner_right,facing:north", Variant.STAIRS,   "shape:straight,facing:north"),
         // --- inner corner shapes ---------
         sr(MID,  LOW,  "shape:outer_left,facing:east", Variant.SLAB,  "type:{src.half}"),
         sr(HIGH, LOW,  "shape:outer_left,facing:east", Variant.SLAB,  "type:{src.half}"),
         sr(MID,  MID,  "shape:outer_left,facing:east", Variant.SLAB,  "type:{src.half}"),
         sr(HIGH, MID,  "shape:outer_left,facing:east", Variant.SLAB,  "type:{src.half}"),
         // ---
         sr(MID,  MID,  "shape:outer_right,facing:east", Variant.SLAB,  "type:{src.half}"),
         sr(HIGH, MID,  "shape:outer_right,facing:east", Variant.SLAB,  "type:{src.half}"),
         sr(MID,  HIGH, "shape:outer_right,facing:east", Variant.SLAB,  "type:{src.half}"),
         sr(HIGH, HIGH, "shape:outer_right,facing:east", Variant.SLAB,  "type:{src.half}"),
         // ---
         sr(LOW,  MID,  "shape:outer_left,facing:west", Variant.SLAB,  "type:{src.half}"),
         sr(MID,  MID,  "shape:outer_left,facing:west", Variant.SLAB,  "type:{src.half}"),
         sr(LOW,  HIGH, "shape:outer_left,facing:west", Variant.SLAB,  "type:{src.half}"),
         sr(MID,  HIGH, "shape:outer_left,facing:west", Variant.SLAB,  "type:{src.half}"),
         // ---
         sr(LOW,  LOW,  "shape:outer_right,facing:west", Variant.SLAB,  "type:{src.half}"),
         sr(MID,  LOW,  "shape:outer_right,facing:west", Variant.SLAB,  "type:{src.half}"),
         sr(LOW,  MID,  "shape:outer_right,facing:west", Variant.SLAB,  "type:{src.half}"),
         sr(MID,  MID,  "shape:outer_right,facing:west", Variant.SLAB,  "type:{src.half}"),
         // ---
         sr(MID,  MID,  "shape:outer_left,facing:south", Variant.SLAB,  "type:{src.half}"),
         sr(HIGH, MID,  "shape:outer_left,facing:south", Variant.SLAB,  "type:{src.half}"),
         sr(MID,  HIGH, "shape:outer_left,facing:south", Variant.SLAB,  "type:{src.half}"),
         sr(HIGH, HIGH, "shape:outer_left,facing:south", Variant.SLAB,  "type:{src.half}"),
         // ---
         sr(LOW,  MID,  "shape:outer_right,facing:south", Variant.SLAB,  "type:{src.half}"),
         sr(MID,  MID,  "shape:outer_right,facing:south", Variant.SLAB,  "type:{src.half}"),
         sr(LOW,  HIGH, "shape:outer_right,facing:south", Variant.SLAB,  "type:{src.half}"),
         sr(MID,  HIGH, "shape:outer_right,facing:south", Variant.SLAB,  "type:{src.half}"),
         // ---
         sr(LOW,  LOW,  "shape:outer_left,facing:north", Variant.SLAB,  "type:{src.half}"),
         sr(MID,  LOW,  "shape:outer_left,facing:north", Variant.SLAB,  "type:{src.half}"),
         sr(LOW,  MID,  "shape:outer_left,facing:north", Variant.SLAB,  "type:{src.half}"),
         sr(MID,  MID,  "shape:outer_left,facing:north", Variant.SLAB,  "type:{src.half}"),
         // ---
         sr(MID,  LOW,  "shape:outer_right,facing:north", Variant.SLAB,  "type:{src.half}"),
         sr(HIGH, LOW,  "shape:outer_right,facing:north", Variant.SLAB,  "type:{src.half}"),
         sr(MID,  MID,  "shape:outer_right,facing:north", Variant.SLAB,  "type:{src.half}"),
         sr(HIGH, MID,  "shape:outer_right,facing:north", Variant.SLAB,  "type:{src.half}")
      )));

      // ===================================================================
      // WALL
      // ===================================================================
      
      // --- side faces (N/S/E/W) ----------------------------------

      LIST.addAll(trAllSides(Variant.WALL, List.of(
         // --- remove connection ----------
         sr(MID,  ALL,  "{dir.face}:!none", Variant.WALL, "{dir.face}:none"),
         sr(LOW,  ALL,  "{dir.cw}:!none",   Variant.WALL, "{dir.cw}:none"),
         sr(HIGH, ALL,  "{dir.ccw}:!none",  Variant.WALL, "{dir.ccw}:none"),
         // --- remove post ----------
         sr(MID,  ALL,  "{dir.cw}:!none,{dir.face}:none,{dir.ccw}:none",   Variant.WALL, "up:false"),
         sr(HIGH, ALL,  "{dir.cw}:!none,{dir.face}:none,{dir.ccw}:none",   Variant.WALL, "up:false"),
         sr(LOW,  ALL,  "{dir.cw}:none,{dir.face}:none,{dir.ccw}:!none",   Variant.WALL, "up:false"),
         sr(MID,  ALL,  "{dir.cw}:none,{dir.face}:none,{dir.ccw}:!none",   Variant.WALL, "up:false"),
         sr(ALL,  ALL,  "{dir.cw}:none,{dir.face}:none,{dir.ccw}:none,{dir.opp}:!none",   Variant.WALL, "up:false"),
         // --- fence ----------
         sr(ALL,  ALL,  "{dir.cw}:none,{dir.face}:none,{dir.ccw}:none,{dir.opp}:none",   Variant.FENCE, "{dir.cw}:false,{dir.face}:false,{dir.ccw}:false,{dir.opp}:false"),
         sr(MID,  ALL,  "{dir.cw}:!none,{dir.face}:none,{dir.ccw}:!none,{dir.opp}:none", Variant.FENCE, "{dir.cw}:true,{dir.face}:false,{dir.ccw}:true,{dir.opp}:false"),
         sr(MID,  ALL,  "{dir.cw}:!none,{dir.face}:none,{dir.ccw}:!none,{dir.opp}:!none", Variant.FENCE, "{dir.cw}:true,{dir.face}:false,{dir.ccw}:true,{dir.opp}:true")
      )));

      // --- UP/DOWN faces -----------------------------------------

      LIST.addAll(trBothVertical(Variant.WALL, List.of(
         // --- remove connection ---------
         sr(LOW,  MID,  "west:!none",  Variant.WALL,   "west:none"),
         sr(MID,  LOW,  "north:!none", Variant.WALL,   "north:none"),
         sr(HIGH, MID,  "east:!none",  Variant.WALL,   "east:none"),
         sr(MID,  HIGH, "south:!none", Variant.WALL,   "south:none"),
         // --- remove post ---------
         sr(LOW,  LOW,  "east:!none,south:!none,west:none,north:none",  Variant.WALL,  "up:false"),
         sr(MID,  LOW,  "east:!none,south:!none,west:none,north:none",  Variant.WALL,  "up:false"),
         sr(LOW,  MID,  "east:!none,south:!none,west:none,north:none",  Variant.WALL,  "up:false"),
         sr(MID,  MID,  "east:!none,south:!none,west:none,north:none",  Variant.WALL,  "up:false"),
         sr(MID,  LOW,  "east:none,south:!none,west:!none,north:none",  Variant.WALL,  "up:false"),
         sr(HIGH, LOW,  "east:none,south:!none,west:!none,north:none",  Variant.WALL,  "up:false"),
         sr(MID,  MID,  "east:none,south:!none,west:!none,north:none",  Variant.WALL,  "up:false"),
         sr(HIGH, MID,  "east:none,south:!none,west:!none,north:none",  Variant.WALL,  "up:false"),
         sr(MID,  MID,  "east:none,south:none,west:!none,north:!none",  Variant.WALL,  "up:false"),
         sr(HIGH, MID,  "east:none,south:none,west:!none,north:!none",  Variant.WALL,  "up:false"),
         sr(MID,  HIGH, "east:none,south:none,west:!none,north:!none",  Variant.WALL,  "up:false"),
         sr(HIGH, HIGH, "east:none,south:none,west:!none,north:!none",  Variant.WALL,  "up:false"),
         sr(LOW,  MID,  "east:!none,south:none,west:none,north:!none",  Variant.WALL,  "up:false"),
         sr(MID,  MID,  "east:!none,south:none,west:none,north:!none",  Variant.WALL,  "up:false"),
         sr(LOW,  HIGH, "east:!none,south:none,west:none,north:!none",  Variant.WALL,  "up:false"),
         sr(MID,  HIGH, "east:!none,south:none,west:none,north:!none",  Variant.WALL,  "up:false"),
         // ---
         sr(LOW,  ALL,  "east:!none,south:none,west:none,north:none",  Variant.WALL,  "up:false"),
         sr(MID,  ALL,  "east:!none,south:none,west:none,north:none",  Variant.WALL,  "up:false"),
         sr(ALL,  LOW,  "east:none,south:!none,west:none,north:none",  Variant.WALL,  "up:false"),
         sr(ALL,  MID,  "east:none,south:!none,west:none,north:none",  Variant.WALL,  "up:false"),
         sr(MID,  ALL,  "east:none,south:none,west:!none,north:none",  Variant.WALL,  "up:false"),
         sr(HIGH, ALL,  "east:none,south:none,west:!none,north:none",  Variant.WALL,  "up:false"),
         sr(ALL,  MID,  "east:none,south:none,west:none,north:!none",  Variant.WALL,  "up:false"),
         sr(ALL,  HIGH, "east:none,south:none,west:none,north:!none",  Variant.WALL,  "up:false"),
         // --- fence ---------
         sr(MID,  MID,  "east:!none,south:!none,west:!none,north:!none",  Variant.FENCE,  "east:true,south:true,west:true,north:true"),
         // ---
         sr(LOW,  MID,  "east:!none,south:!none,west:none,north:!none",   Variant.FENCE,  "east:true,south:true,west:false,north:true"),
         sr(MID,  MID,  "east:!none,south:!none,west:none,north:!none",   Variant.FENCE,  "east:true,south:true,west:false,north:true"),
         sr(MID,  LOW,  "east:!none,south:!none,west:!none,north:none",   Variant.FENCE,  "east:true,south:true,west:true,north:false"),
         sr(MID,  MID,  "east:!none,south:!none,west:!none,north:none",   Variant.FENCE,  "east:true,south:true,west:true,north:false"),
         sr(MID,  MID,  "east:none,south:!none,west:!none,north:!none",   Variant.FENCE,  "east:false,south:true,west:true,north:true"),
         sr(HIGH, MID,  "east:none,south:!none,west:!none,north:!none",   Variant.FENCE,  "east:false,south:true,west:true,north:true"),
         sr(MID,  MID,  "east:!none,south:none,west:!none,north:!none",   Variant.FENCE,  "east:true,south:false,west:true,north:true"),
         sr(MID,  HIGH, "east:!none,south:none,west:!none,north:!none",   Variant.FENCE,  "east:true,south:false,west:true,north:true"),
         // ---
         sr(ALL,  ALL,  "east:none,south:none,west:none,north:none",  Variant.FENCE,  "east:false,south:false,west:false,north:false")
      )));

      // ===================================================================
      // FENCE
      // ===================================================================
      
      // --- side faces (N/S/E/W) ----------------------------------

      LIST.addAll(trAllSides(Variant.WALL, List.of(
         // --- remove connection ----------
         sr(MID,  ALL,  "{dir.face}:true", Variant.WALL, "{dir.face}:false"),
         sr(LOW,  ALL,  "{dir.cw}:true",   Variant.WALL, "{dir.cw}:false"),
         sr(HIGH, ALL,  "{dir.ccw}:true",  Variant.WALL, "{dir.ccw}:false")
      )));

      // --- UP/DOWN faces -----------------------------------------

      LIST.addAll(trBothVertical(Variant.WALL, List.of(
         // --- remove connection ---------
         sr(LOW,  MID,  "west:true",  Variant.WALL,   "west:false"),
         sr(MID,  LOW,  "north:true", Variant.WALL,   "north:false"),
         sr(HIGH, MID,  "east:true",  Variant.WALL,   "east:false"),
         sr(MID,  HIGH, "south:true", Variant.WALL,   "south:false")
      )));

      // ===================================================================
      // SLAB
      // ===================================================================

      LIST.addAll(trAllFaces(Variant.SLAB, List.of(
         sr(ALL, ALL, "type:bottom",  Variant.CARPET, "")
      )));

      // ===================================================================
      // PATH
      // ===================================================================

      // --- UP/DOWN faces -----------------------------------------

      LIST.addAll(trBothVertical(Variant.PATH, List.of(
         // --- to path/layer ---------
         sr(ALL,  ALL,  Variant.LAYER,  "layers:7")
      )));

      // ===================================================================
      // LAYER
      // ===================================================================

      // --- UP/DOWN faces -----------------------------------------

      LIST.addAll(trBothVertical(Variant.LAYER, List.of(
         // --- to path/layer ---------
         sr(ALL,  ALL,  "layers:8",  Variant.LAYER,  "layers:7"),
         sr(ALL,  ALL,  "layers:7",  Variant.LAYER,  "layers:6"),
         sr(ALL,  ALL,  "layers:6",  Variant.LAYER,  "layers:5"),
         sr(ALL,  ALL,  "layers:5",  Variant.LAYER,  "layers:4"),
         sr(ALL,  ALL,  "layers:4",  Variant.LAYER,  "layers:3"),
         sr(ALL,  ALL,  "layers:3",  Variant.LAYER,  "layers:2"),
         sr(ALL,  ALL,  "layers:2",  Variant.LAYER,  "layers:1")
      )));

   }
}
