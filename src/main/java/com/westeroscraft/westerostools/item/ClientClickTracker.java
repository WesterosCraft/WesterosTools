package com.westeroscraft.westerostools.item;

/**
 * Client-side press-edge tracking for the tool items.
 *
 * <p>Vanilla and Fabric's interaction events re-fire while a mouse button is
 * held: attack fires twice on the press tick (once from {@code attackBlock},
 * once from {@code updateBlockBreakingProgress}) and then every client tick,
 * because cancelling the event skips vanilla's creative break cooldown; use
 * re-fires every 4 ticks via vanilla hold-to-use. Without gating, a single
 * slightly-long click dispatches the same tool action several times.
 *
 * <p>The client entrypoint records each button's held state at the end of every
 * client tick; the interaction callbacks then accept only the first event of a
 * physical press — the button was up at the end of the previous tick and no
 * event was already accepted this tick.
 *
 * <p>Written and read only on the client thread. Safe to load on a dedicated
 * server (plain statics, no client classes); it is simply never used there.
 */
public final class ClientClickTracker {

    private static boolean attackWasDown;
    private static boolean useWasDown;
    private static long lastAttackAccept = Long.MIN_VALUE;
    private static long lastUseAccept = Long.MIN_VALUE;

    /** Record end-of-tick button state; called from the client tick event. */
    public static void endTick(boolean attackDown, boolean useDown) {
        attackWasDown = attackDown;
        useWasDown = useDown;
    }

    /**
     * True only for the first attack event of a physical press: rejects
     * re-fires while the button stays held and the duplicate event vanilla
     * emits on the press tick itself.
     */
    public static boolean acceptAttack(long gameTime) {
        if (attackWasDown || lastAttackAccept == gameTime) {
            return false;
        }
        lastAttackAccept = gameTime;
        return true;
    }

    /** True only for the first use event of a physical press; see {@link #acceptAttack}. */
    public static boolean acceptUse(long gameTime) {
        if (useWasDown || lastUseAccept == gameTime) {
            return false;
        }
        lastUseAccept = gameTime;
        return true;
    }

    private ClientClickTracker() {}
}
