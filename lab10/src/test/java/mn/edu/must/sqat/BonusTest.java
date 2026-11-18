package mn.edu.must.sqat;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

/**
 * bonus() data-flow tests. Line numbers are lines of DataFlowTargets.java.
 * Pair notation: variable:defLine->useLine (42T / 42F = true / false branch of the p-use at line 42).
 */
class BonusTest {

    // ---------------- Stage 1: ALL-DEFS (2 tests) ----------------

    @Test
    @DisplayName("B01 all-defs: present>=threshold and best>=5, with an absent day inside the streak run. "
            + "Reaches a use for every def except score@49 (killed by score@51)")
    void b01_longStreakWithBreak_thr3() {
        assertEquals(8, DataFlowTargets.bonus(new int[] {1, 1, 1, 1, 1, 0, 1}, 3));
    }

    @Test
    @DisplayName("B02 all-defs: present<threshold and best<5. Reaches score:49->56")
    void b02_belowThreshold_thr5() {
        assertEquals(0, DataFlowTargets.bonus(new int[] {1, 1, 1}, 5));
    }
}
