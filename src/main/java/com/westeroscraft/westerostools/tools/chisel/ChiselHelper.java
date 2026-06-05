package com.westeroscraft.westerostools.tools.chisel;

import com.sk89q.worldedit.entity.Player;
import com.sk89q.worldedit.fabric.FabricAdapter;
import com.sk89q.worldedit.math.BlockVector3;
import com.sk89q.worldedit.util.Direction;
import com.sk89q.worldedit.util.Location;
import com.sk89q.worldedit.world.block.BaseBlock;
import com.westeroscraft.westerostools.BlockDef.Variant;
import com.westeroscraft.westerostools.WesterosTools;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.annotation.Nullable;

/**
 * Static helpers for Chisel: UV binning, the Transition record,
 * table-builder methods, and lookup utilities.
 *
 * UV axes on each face (U=left->right, V=bottom->top as seen from outside):
 *
 *   NORTH  u = 1-(hx-bx)  west<->east,  v = hy-by  bottom->top
 *   SOUTH  u =   hx-bx    west<->east,  v = hy-by  bottom->top
 *   EAST   u = 1-(hz-bz)  south<->north,v = hy-by  bottom->top
 *   WEST   u =   hz-bz    north<->south,v = hy-by  bottom->top
 *   UP     u =   hx-bx    west<->east,  v = hz-bz  north->south
 *   DOWN   u =   hx-bx    west<->east,  v = hz-bz  north->south  (same axes as UP; (LOW,LOW)=NW for both)
 */
class ChiselHelper {

    // -----------------------------------------------------------------------
    // UV + binning
    // -----------------------------------------------------------------------

    enum UVBin { LOW, MID, HIGH, ALL }

    static UVBin binCoord(double t) {
        if (t < 1.0 / 3.0) return UVBin.LOW;
        if (t < 2.0 / 3.0) return UVBin.MID;
        return UVBin.HIGH;
    }

    /**
     * Compute U,V in [0,1] for where the player's eye ray intersects the clicked face.
     * Returns null if the ray is parallel to the face plane.
     */
    @Nullable
    static double[] computeFaceUV(Player player, BlockVector3 blockPos, Direction face) {
        Location eyeLoc = player.getLocation();
        // getLocation() returns feet position; add standard eye height offset
        double ex = eyeLoc.getX(), ey = eyeLoc.getY() + 1.62, ez = eyeLoc.getZ();

        double yaw   = Math.toRadians(eyeLoc.getYaw());
        double pitch = Math.toRadians(eyeLoc.getPitch());
        double lx = -Math.sin(yaw) * Math.cos(pitch);
        double ly = -Math.sin(pitch);
        double lz =  Math.cos(yaw) * Math.cos(pitch);

        double bx = blockPos.x(), by = blockPos.y(), bz = blockPos.z();

        double hx, hy, hz;
        switch (face) {
            case NORTH -> {
                if (Math.abs(lz) < 1e-9) return null;
                double t = (bz - ez) / lz;
                hx = ex + t*lx;  hy = ey + t*ly;  hz = bz;
                if (hx < bx || hx > bx + 1) hx = Math.max(bx, Math.min(bx + 1, ex));
                if (hy < by || hy > by + 1) hy = Math.max(by, Math.min(by + 1, ey));
            }
            case SOUTH -> {
                if (Math.abs(lz) < 1e-9) return null;
                double t = (bz + 1 - ez) / lz;
                hx = ex + t*lx;  hy = ey + t*ly;  hz = bz + 1;
                if (hx < bx || hx > bx + 1) hx = Math.max(bx, Math.min(bx + 1, ex));
                if (hy < by || hy > by + 1) hy = Math.max(by, Math.min(by + 1, ey));
            }
            case EAST -> {
                if (Math.abs(lx) < 1e-9) return null;
                double t = (bx + 1 - ex) / lx;
                hx = bx + 1;  hy = ey + t*ly;  hz = ez + t*lz;
                if (hz < bz || hz > bz + 1) hz = Math.max(bz, Math.min(bz + 1, ez));
                if (hy < by || hy > by + 1) hy = Math.max(by, Math.min(by + 1, ey));
            }
            case WEST -> {
                if (Math.abs(lx) < 1e-9) return null;
                double t = (bx - ex) / lx;
                hx = bx;  hy = ey + t*ly;  hz = ez + t*lz;
                if (hz < bz || hz > bz + 1) hz = Math.max(bz, Math.min(bz + 1, ez));
                if (hy < by || hy > by + 1) hy = Math.max(by, Math.min(by + 1, ey));
            }
            case UP -> {
                if (Math.abs(ly) < 1e-9) return null;
                double t = (by + 1 - ey) / ly;
                hx = ex + t*lx;  hy = by + 1;  hz = ez + t*lz;
                if (hx < bx || hx > bx + 1) hx = Math.max(bx, Math.min(bx + 1, ex));
                if (hz < bz || hz > bz + 1) hz = Math.max(bz, Math.min(bz + 1, ez));
            }
            case DOWN -> {
                if (Math.abs(ly) < 1e-9) return null;
                double t = (by - ey) / ly;
                hx = ex + t*lx;  hy = by;  hz = ez + t*lz;
                if (hx < bx || hx > bx + 1) hx = Math.max(bx, Math.min(bx + 1, ex));
                if (hz < bz || hz > bz + 1) hz = Math.max(bz, Math.min(bz + 1, ez));
            }
            default -> { return null; }
        }

        double u, v;
        switch (face) {
            case NORTH -> { u = 1.0 - (hx - bx);  v = hy - by;          }
            case SOUTH -> { u = hx - bx;           v = hy - by;          }
            case EAST  -> { u = 1.0 - (hz - bz);  v = hy - by;          }
            case WEST  -> { u = hz - bz;           v = hy - by;          }
            case UP    -> { u = hx - bx;           v = hz - bz;          }
            case DOWN  -> { u = hx - bx;           v = hz - bz;          }
            default    -> { return null; }
        }

        u = Math.max(0.0, Math.min(1.0, u));
        v = Math.max(0.0, Math.min(1.0, v));
        return new double[]{ u, v };
    }

    /**
     * Result of a precise model raycast: the clicked block position, the actual
     * face of the model sub-shape that was hit, and the UV on that face.
     */
    record HitInfo(BlockVector3 pos, Direction face, double[] uv) {}

    /**
     * Raycast against the block's real visual outline shape (the same shape used
     * for the player's crosshair target), giving the exact model intersection
     * rather than the 1x1x1 grid face plane.
     *
     * For non-full blocks (upper-half stairs, walls, slabs, ...) this returns the
     * face and UV of the actual model surface clicked, which can differ from where
     * the eye ray meets the full-block boundary.
     *
     * Returns null if the player is not a Fabric player or the crosshair is not on
     * a block (caller should fall back to computeFaceUV).
     */
    @Nullable
    static HitInfo raycastModel(Player player) {
        if (WesterosTools.server == null) return null;
        ServerPlayer sp = WesterosTools.server.getPlayerList().getPlayer(player.getUniqueId());
        if (sp == null) return null;

        // pick() uses ClipContext.Block.OUTLINE -> the visual model shape.
        HitResult hit = sp.pick(sp.blockInteractionRange(), 1.0f, false);
        if (!(hit instanceof BlockHitResult bhr) || bhr.getType() != HitResult.Type.BLOCK) return null;

        BlockPos bp = bhr.getBlockPos();
        Vec3 p = bhr.getLocation();
        Direction face = FabricAdapter.adaptEnumFacing(bhr.getDirection());

        double[] uv = localToUV(face, p.x - bp.getX(), p.y - bp.getY(), p.z - bp.getZ());
        if (uv == null) return null;
        return new HitInfo(FabricAdapter.adapt(bp), face, uv);
    }

    /**
     * Map a hit point expressed in block-local coordinates (each component the
     * offset from the block's min corner) to UV on the given face.  The axis
     * conventions match computeFaceUV; the component normal to the face
     * is ignored, so a recessed sub-face still yields the correct in-face UV.
     */
    @Nullable
    static double[] localToUV(Direction face, double lx, double ly, double lz) {
        double u, v;
        switch (face) {
            case NORTH -> { u = 1.0 - lx; v = ly; }
            case SOUTH -> { u = lx;        v = ly; }
            case EAST  -> { u = 1.0 - lz; v = ly; }
            case WEST  -> { u = lz;        v = ly; }
            case UP    -> { u = lx;        v = lz; }
            case DOWN  -> { u = lx;        v = lz; }
            default    -> { return null; }
        }
        u = Math.max(0.0, Math.min(1.0, u));
        v = Math.max(0.0, Math.min(1.0, v));
        return new double[]{ u, v };
    }

    // -----------------------------------------------------------------------
    // Transition record + builders
    // -----------------------------------------------------------------------

    /**
     * One row in a transition table.
     *
     * fromStatePattern: partial map of property->value that the current block
     * must satisfy.  Omitted keys are wildcards.  null = match any state.
     *
     * toState: property overrides applied to the target block type's default
     * state.  Properties absent from this map keep the block type's default value.
     */
    record Transition(
        Direction face,
        UVBin uBin,
        UVBin vBin,
        Variant fromVariant,
        @Nullable Map<String,String> fromStatePattern,
        Variant toVariant,
        Map<String,String> toState
    ) {
        boolean matches(Direction f, UVBin u, UVBin v, Variant variant,
                        Map<String,String> actualState) {
            if (f != face || u != uBin || v != vBin || variant != fromVariant) return false;
            if (fromStatePattern != null) {
                for (var e : fromStatePattern.entrySet()) {
                    String actual = actualState.get(e.getKey());
                    if (actual == null) return false;
                    String pat = e.getValue();
                    if (pat.startsWith("!")) {
                        if (pat.substring(1).equalsIgnoreCase(actual)) return false;  // negation
                    } else {
                        if (!pat.equalsIgnoreCase(actual)) return false;
                    }
                }
            }
            return true;
        }
    }

    // Convenience builders — keep the transition tables readable.
    static Transition tr(Direction face, UVBin u, UVBin v,
                         Variant from,
                         Variant to, String toStateStr) {
        return new Transition(face, u, v, from, null, to, parseStateString(toStateStr));
    }
    static Transition tr(Direction face, UVBin u, UVBin v,
                         Variant from, String fromPatStr,
                         Variant to, String toStateStr) {
        return new Transition(face, u, v, from, parseStateString(fromPatStr), to, parseStateString(toStateStr));
    }
    private static Map<String,String> parseStateString(String s) {
        var m = new HashMap<String,String>();
        if (s == null || s.isEmpty()) return m;
        for (String pair : s.split(",")) {
            int idx = pair.indexOf(':');
            if (idx > 0) m.put(pair.substring(0, idx), pair.substring(idx + 1));
        }
        return m;
    }

    // -----------------------------------------------------------------------
    // Symmetry-aware bulk builders
    // -----------------------------------------------------------------------

    /**
     * A single (u, v) -> target rule for use with trFace / trAllSides / trBothVertical.
     *
     * All direction tokens resolve to lowercase Minecraft property values
     * (e.g. "north", "west").
     *
     * Common tokens:
     *   {half}         - "bottom" for UP, "top" for DOWN; empty for horizontal faces.
     *
     * Horizontal-face tokens (N/S/E/W; empty for UP/DOWN):
     *   {dir.face}     - clicked face name (e.g. "north").
     *   {dir.opp}      - opposite direction name (e.g. "south").
     *   {dir.cw}       - face direction rotated 90 deg CW  (N->E->S->W->N).
     *   {dir.ccw}      - face direction rotated 90 deg CCW (N->W->S->E->N).
     *
     * Example declarative wall template:
     *   "{dir.face}:low,{dir.opp}:none,{dir.cw}:low,{dir.ccw}:low,up:false"
     *
     * UP/DOWN face tokens (empty for horizontal faces):
     *   {dir.v}        - direction the v-bin edge faces away from:
     *                    UP  v=LOW->"south", v=HIGH->"north";
     *                    DOWN v=LOW->"north", v=HIGH->"south".
     *   {dir.v+90}     - {dir.v} rotated 90 deg CW.
     *   {dir.v-90}     - {dir.v} rotated 90 deg CCW.
     *   {dir.v+180}    - {dir.v} rotated 180 deg.
     *   {dir.u}        - direction the u-bin edge faces away from:
     *                    u=LOW->"east", u=HIGH->"west".
     *                    Matches a stair whose open side is at this u-edge (e.g. an east-facing
     *                    stair at u=LOW, because its open/ascending side faces east).
     *   {dir.u+90}     - {dir.u} rotated 90 deg CW.
     *   {dir.u-90}     - {dir.u} rotated 90 deg CCW.
     *   {dir.u+180}    - opposite of {dir.u}: the direction the u-bin edge faces into
     *                    (u=LOW->"west", u=HIGH->"east").
     *                    Matches a stair whose riser is at this u-edge (e.g. a west-facing stair
     *                    at u=LOW, because its riser/closed side is on the west).
     *   {corner.inner} - "inner_right" when rotateH({dir.v}, +90) == {dir.u},
     *                    otherwise "inner_left".  Use with facing:{dir.v}.
     *                    Only valid when both u and v are non-MID (i.e. a corner cell).
     *   {corner.outer} - "outer_right" / "outer_left" by the same condition.
     *                    Use with facing:{dir.v+180} (facing points toward the filled corner,
     *                    opposite to the inner case).  Only valid for corner cells.
     *
     * The same tokens are substituted into fromPatTemplate when non-null.
     */
    record SideRule(
        UVBin u,
        UVBin v,
        @Nullable String fromPatTemplate,
        Variant toVariant,
        String toStateTemplate
    ) {}

    /** SideRule factory with no source-state filter. */
    static SideRule sr(UVBin u, UVBin v, Variant to, String toTemplate) {
        return new SideRule(u, v, null, to, toTemplate);
    }
    /** SideRule factory with a source-state filter template. */
    static SideRule sr(UVBin u, UVBin v, String fromTemplate, Variant to, String toTemplate) {
        return new SideRule(u, v, fromTemplate, to, toTemplate);
    }

    /** The cardinal direction directly opposite to {@code face}. */
    static Direction opposite(Direction face) {
        return switch (face) {
            case NORTH -> Direction.SOUTH;
            case SOUTH -> Direction.NORTH;
            case EAST  -> Direction.WEST;
            case WEST  -> Direction.EAST;
            case UP    -> Direction.DOWN;
            case DOWN  -> Direction.UP;
            default    -> throw new IllegalArgumentException("No opposite for: " + face);
        };
    }

    /**
     * Generate transitions for face by applying each rule's templates.
     * See SideRule for the full list of substitution tokens.
     */
    static List<Transition> trFace(Direction face, Variant from, List<SideRule> rules) {
        String half = switch (face) { case UP -> "bottom"; case DOWN -> "top"; default -> ""; };
        List<Transition> out = new ArrayList<>();
        for (SideRule rule : rules) {
            List<UVBin> uBins = (rule.u() == UVBin.ALL) ? List.of(UVBin.LOW, UVBin.MID, UVBin.HIGH) : List.of(rule.u());
            List<UVBin> vBins = (rule.v() == UVBin.ALL) ? List.of(UVBin.LOW, UVBin.MID, UVBin.HIGH) : List.of(rule.v());
            for (UVBin ub : uBins) {
                for (UVBin vb : vBins) {
                    String dirV = dirV(face, vb);
                    String dirU = dirU(face, ub);
                    String fromPat = rule.fromPatTemplate() == null ? null
                        : substitute(rule.fromPatTemplate(), face, half, dirV, dirU, ub);
                    String toState = substitute(rule.toStateTemplate(), face, half, dirV, dirU, ub);
                    out.add(fromPat == null
                        ? tr(face, ub, vb, from, rule.toVariant(), toState)
                        : tr(face, ub, vb, from, fromPat, rule.toVariant(), toState));
                }
            }
        }
        return out;
    }

    // -----------------------------------------------------------------------
    // Direction rotation helpers
    // -----------------------------------------------------------------------

    /** Clockwise order of horizontal directions (top-down view), uppercase. */
    private static final List<String> H_CW = List.of("NORTH", "EAST", "SOUTH", "WEST");

    /**
     * Rotate a horizontal direction name steps x 90 deg clockwise (case-insensitive input,
     * lowercase output).  Returns the input unchanged for UP/DOWN/empty.
     */
    private static String rotateH(String dir, int steps) {
        int i = H_CW.indexOf(dir.toUpperCase());
        return i < 0 ? dir : H_CW.get(Math.floorMod(i + steps, 4)).toLowerCase();
    }

    // -----------------------------------------------------------------------

    /** {dir.v}: direction the v-bin edge faces away from, for UP/DOWN only (lowercase). */
    private static String dirV(Direction face, UVBin v) {
        return switch (face) {
            case UP, DOWN -> switch (v) { case LOW -> "south"; case HIGH -> "north"; default -> ""; };
            default       -> "";
        };
    }

    /** {dir.u}: direction the u-bin edge faces away from, for UP/DOWN only (lowercase). */
    private static String dirU(Direction face, UVBin u) {
        return (face == Direction.UP || face == Direction.DOWN)
            ? switch (u) { case LOW -> "east"; case HIGH -> "west"; default -> ""; }
            : "";
    }

    private static String substitute(String template, Direction face, String half,
                                     String dirV, String dirU, UVBin u) {
        String s = template.replace("{half}", half);
        // Horizontal-face direction/arm tokens
        if (face != Direction.UP && face != Direction.DOWN) {
            String faceLc = face.name().toLowerCase();
            s = s.replace("{dir.face}", faceLc)
                 .replace("{dir.opp}",  rotateH(faceLc, +2))
                 .replace("{dir.cw}",   rotateH(faceLc, +1))
                 .replace("{dir.ccw}",  rotateH(faceLc, -1))
                 ;
        }
        // UP/DOWN v-direction tokens
        if (!dirV.isEmpty()) {
            s = s.replace("{dir.v+90}",  rotateH(dirV, +1))
                 .replace("{dir.v-90}",  rotateH(dirV, -1))
                 .replace("{dir.v+180}", rotateH(dirV, +2))
                 .replace("{dir.v}",     dirV);
        }
        // UP/DOWN u-direction tokens
        if (!dirU.isEmpty()) {
            s = s.replace("{dir.u+90}",  rotateH(dirU, +1))
                 .replace("{dir.u-90}",  rotateH(dirU, -1))
                 .replace("{dir.u+180}", rotateH(dirU, +2))
                 .replace("{dir.u}",     dirU);
        }
        // corner shapes: inner/outer right when dirU is the 90°-CW neighbour of dirV
        if (!dirV.isEmpty() && !dirU.isEmpty()) {
            boolean isRight = rotateH(dirV, +1).equals(dirU);
            s = s.replace("{corner.inner}", isRight ? "inner_right" : "inner_left")
                 .replace("{corner.outer}", isRight ? "outer_right" : "outer_left");
        }
        return s;
    }

    /**
     * For each SideRule whose v-bin is not UVBin.MID, produce two rules:
     * one with half:bottom at the original v-position, and one with half:top
     * at the vertically-mirrored v-position.
     *
     * A half:top stair has its geometry flipped relative to a half:bottom
     * stair, so the same UV click position corresponds to a geometrically different region.
     * MID-v rules are symmetric and pass through unchanged.
     */
    static List<SideRule> halfSplit(List<SideRule> rules) {
        List<SideRule> out = new ArrayList<>();
        for (SideRule r : rules) {
            String basePat   = r.fromPatTemplate();
            String bottomPat = (basePat == null) ? "half:bottom" : basePat + ",half:bottom";
            String topPat    = (basePat == null) ? "half:top"    : basePat + ",half:top";
            if (r.v() == UVBin.MID) {
                // MID is the "full-block" zone for half:bottom only; for half:top it is
                // part of the indent zone (covered by the HIGH→MID rule added below).
                // half:bottom filter is required so the unfiltered entry doesn't shadow
                // the half:top MID rule that HIGH rules insert after this one.
                out.add(new SideRule(r.u(), UVBin.MID, bottomPat, r.toVariant(), r.toStateTemplate()));
            } else {
                UVBin flippedV = (r.v() == UVBin.LOW) ? UVBin.HIGH : UVBin.LOW;
                out.add(new SideRule(r.u(), r.v(),    bottomPat, r.toVariant(), r.toStateTemplate()));
                out.add(new SideRule(r.u(), flippedV, topPat,    r.toVariant(), r.toStateTemplate()));
                // For half:top the indent zone spans both LOW and MID, so duplicate the
                // HIGH rule (which becomes half:top at LOW) as a half:top MID rule too.
                if (r.v() == UVBin.HIGH) {
                    out.add(new SideRule(r.u(), UVBin.MID, topPat, r.toVariant(), r.toStateTemplate()));
                }
            }
        }
        return out;
    }

    /**
     * Generate transitions for all four horizontal faces (N, S, E, W).
     * Delegates to trFace for each.
     */
    static List<Transition> trAllSides(Variant from, List<SideRule> rules) {
        List<Transition> out = new ArrayList<>();
        for (Direction face : List.of(Direction.NORTH, Direction.SOUTH,
                                      Direction.EAST,  Direction.WEST))
            out.addAll(trFace(face, from, rules));
        return out;
    }

    /**
     * Generate transitions for both UP and DOWN faces from a single rule set.
     *
     * Both faces share the same UV axes: u=LOW->west, u=HIGH->east, v=LOW->north, v=HIGH->south,
     * so (LOW,LOW) is the northwest corner for both.  Rules written with {dir.v},
     * {dir.u}, and {corner.inner} resolve identically for UP and DOWN.
     * Only {half} differs ("bottom" for UP, "top" for DOWN).
     */
    static List<Transition> trBothVertical(Variant from, List<SideRule> rules) {
        List<Transition> out = new ArrayList<>();
        out.addAll(trFace(Direction.UP,   from, rules));
        out.addAll(trFace(Direction.DOWN, from, rules));
        return out;
    }

    /**
     * Generate transitions for all six faces (N, S, E, W, UP, DOWN).
     * Delegates to trAllSides and trBothVertical.
     */
    static List<Transition> trAllFaces(Variant from, List<SideRule> rules) {
        List<Transition> out = new ArrayList<>();
        out.addAll(trAllSides(from, rules));
        out.addAll(trBothVertical(from, rules));
        return out;
    }

    // -----------------------------------------------------------------------
    // Lookup + state snapshot
    // -----------------------------------------------------------------------

    /** Snapshot the current block's state as a plain string map for matching. */
    static Map<String,String> stateSnapshot(BaseBlock block) {
        var snap = new HashMap<String,String>();
        for (var e : block.getStates().entrySet())
            snap.put(e.getKey().getName(), String.valueOf(e.getValue()));
        return snap;
    }
}
