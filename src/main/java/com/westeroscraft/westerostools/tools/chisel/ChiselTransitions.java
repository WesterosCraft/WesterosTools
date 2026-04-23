package com.westeroscraft.westerostools.tools.chisel;

import com.westeroscraft.westerostools.BlockDef.Variant;

import java.util.ArrayList;
import java.util.List;

import static com.westeroscraft.westerostools.tools.chisel.ChiselHelper.UVBin.*;
import static com.westeroscraft.westerostools.tools.chisel.ChiselHelper.halfSplit;
import static com.westeroscraft.westerostools.tools.chisel.ChiselHelper.sr;
import static com.westeroscraft.westerostools.tools.chisel.ChiselHelper.trAllSides;
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
 *   DOWN  face:  u=LOW→west,  u=HIGH→east,  v=LOW→south,  v=HIGH→north
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
         sr(LOW,  LOW,  "shape:straight,facing:{dir.u}", Variant.WALL,   "{dir.v+180}:low,{dir.v}:low,{dir.u}:none,{dir.u+180}:none,up:false"),
         sr(MID,  LOW,  "shape:straight,facing:{dir.u}", Variant.STAIRS, "shape:outer_right,facing:{dir.u}"),
         sr(HIGH, LOW,  "shape:straight,facing:{dir.u}", Variant.STAIRS, "shape:outer_right,facing:{dir.u}"),
         sr(LOW,  MID,  "shape:straight,facing:{dir.u}", Variant.WALL,   "{dir.v+180}:low,{dir.v}:low,{dir.u}:none,{dir.u+180}:none,up:false"),
         sr(MID,  MID,  "shape:straight,facing:{dir.u}", Variant.SLAB,   "type:{src.half}"),
         sr(HIGH, MID,  "shape:straight,facing:{dir.u}", Variant.SLAB,   "type:{src.half}"),
         sr(LOW,  HIGH, "shape:straight,facing:{dir.u}", Variant.WALL,   "{dir.v+180}:low,{dir.v}:low,{dir.u}:none,{dir.u+180}:none,up:false"),
         sr(MID,  HIGH, "shape:straight,facing:{dir.u}", Variant.STAIRS, "shape:outer_left,facing:{dir.u}"),
         sr(HIGH, HIGH, "shape:straight,facing:{dir.u}", Variant.STAIRS, "shape:outer_left,facing:{dir.u}"),
         // ---
         sr(LOW,  LOW,  "shape:straight,facing:{dir.u+180}", Variant.STAIRS, "shape:outer_left,facing:{dir.u+180}"),
         sr(MID,  LOW,  "shape:straight,facing:{dir.u+180}", Variant.STAIRS, "shape:outer_left,facing:{dir.u+180}"),
         sr(HIGH, LOW,  "shape:straight,facing:{dir.u+180}", Variant.WALL,   "{dir.v+180}:low,{dir.v}:low,{dir.u}:none,{dir.u+180}:none,up:false"),
         sr(LOW,  MID,  "shape:straight,facing:{dir.u+180}", Variant.SLAB,   "type:{src.half}"),
         sr(MID,  MID,  "shape:straight,facing:{dir.u+180}", Variant.SLAB,   "type:{src.half}"),
         sr(HIGH, MID,  "shape:straight,facing:{dir.u+180}", Variant.WALL,   "{dir.v+180}:low,{dir.v}:low,{dir.u}:none,{dir.u+180}:none,up:false"),
         sr(LOW,  HIGH, "shape:straight,facing:{dir.u+180}", Variant.STAIRS, "shape:outer_right,facing:{dir.u+180}"),
         sr(MID,  HIGH, "shape:straight,facing:{dir.u+180}", Variant.STAIRS, "shape:outer_right,facing:{dir.u+180}"),
         sr(HIGH, HIGH, "shape:straight,facing:{dir.u+180}", Variant.WALL,   "{dir.v+180}:low,{dir.v}:low,{dir.u}:none,{dir.u+180}:none,up:false"),
         // ---
         sr(LOW,  LOW,  "shape:straight,facing:{dir.v}", Variant.WALL,   "{dir.v+180}:none,{dir.v}:none,{dir.u}:low,{dir.u+180}:low,up:false"),
         sr(MID,  LOW,  "shape:straight,facing:{dir.v}", Variant.WALL,   "{dir.v+180}:none,{dir.v}:none,{dir.u}:low,{dir.u+180}:low,up:false"),
         sr(HIGH, LOW,  "shape:straight,facing:{dir.v}", Variant.WALL,   "{dir.v+180}:none,{dir.v}:none,{dir.u}:low,{dir.u+180}:low,up:false"),
         sr(LOW,  MID,  "shape:straight,facing:{dir.v}", Variant.STAIRS, "shape:outer_left,facing:{dir.v}"),
         sr(MID,  MID,  "shape:straight,facing:{dir.v}", Variant.SLAB,   "type:{src.half}"),
         sr(HIGH, MID,  "shape:straight,facing:{dir.v}", Variant.STAIRS, "shape:outer_right,facing:{dir.v}"),
         sr(LOW,  HIGH, "shape:straight,facing:{dir.v}", Variant.STAIRS, "shape:outer_left,facing:{dir.v}"),
         sr(MID,  HIGH, "shape:straight,facing:{dir.v}", Variant.SLAB,   "type:{src.half}"),
         sr(HIGH, HIGH, "shape:straight,facing:{dir.v}", Variant.STAIRS, "shape:outer_right,facing:{dir.v}"),
         // ---
         sr(LOW,  LOW,  "shape:straight,facing:{dir.v+180}", Variant.STAIRS, "shape:outer_right,facing:{dir.v+180}"),
         sr(MID,  LOW,  "shape:straight,facing:{dir.v+180}", Variant.SLAB,   "type:{src.half}"),
         sr(HIGH, LOW,  "shape:straight,facing:{dir.v+180}", Variant.STAIRS, "shape:outer_left,facing:{dir.v+180}"),
         sr(LOW,  MID,  "shape:straight,facing:{dir.v+180}", Variant.STAIRS, "shape:outer_right,facing:{dir.v+180}"),
         sr(MID,  MID,  "shape:straight,facing:{dir.v+180}", Variant.SLAB,   "type:{src.half}"),
         sr(HIGH, MID,  "shape:straight,facing:{dir.v+180}", Variant.STAIRS, "shape:outer_left,facing:{dir.v+180}"),
         sr(LOW,  HIGH, "shape:straight,facing:{dir.v+180}", Variant.WALL,   "{dir.v+180}:none,{dir.v}:none,{dir.u}:low,{dir.u+180}:low,up:false"),
         sr(MID,  HIGH, "shape:straight,facing:{dir.v+180}", Variant.WALL,   "{dir.v+180}:none,{dir.v}:none,{dir.u}:low,{dir.u+180}:low,up:false"),
         sr(HIGH, HIGH, "shape:straight,facing:{dir.v+180}", Variant.WALL,   "{dir.v+180}:none,{dir.v}:none,{dir.u}:low,{dir.u+180}:low,up:false")
         // --- inner_left corner shapes ---------
         // sr(LOW,  LOW,  "shape:inner_left,facing:{dir.v}",     Variant.STAIRS, "shape:straight,facing:{dir.v}"),
         // sr(LOW,  LOW,  "shape:inner_left,facing:{dir.v+180}", Variant.WALL,   "{dir.v+180}:none,{dir.v}:none,{dir.u}:low,{dir.u+180}:low,up:false"),
         // sr(LOW,  LOW,  "shape:inner_left,facing:{dir.u}",     Variant.STAIRS, "shape:straight,facing:{dir.u}"),
         // sr(LOW,  LOW,  "shape:inner_left,facing:{dir.u+180}", Variant.STAIRS, "shape:straight,facing:{}"),
         // --- inner corner shapes ---------
         // TODO
      )));

      // ===================================================================
      // WALL
      // ===================================================================
      // TODO

      // TODO: slab -> carpet?

   }
}
