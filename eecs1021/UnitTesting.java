package eecs1021;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * Unit tests for the Secondary class logic.
 * Primarily tests the moisture threshold logic without hardware interaction.
 */
public class UnitTesting {

    // Instantiate Secondary with nulls as we are only testing the logic method
    // which does not use the hardware fields.
    private final Secondary secondaryTask = new Secondary(null, null, null, null);

    @Test
    public void testDrySoilDetection() {
        // Test voltage >= 3.4 (THRESHOLD_DRY)
        String actualStatus = secondaryTask.determineSoilStatus(5.6);
        assertEquals("dry soil", actualStatus);
    }

    @Test
    public void testDampSoilDetection() {
        // Test 3.2 <= voltage < 3.4 (THRESHOLD_DAMP)
        String actualStatus = secondaryTask.determineSoilStatus(3.2);
        assertEquals("damp soil", actualStatus);
    }

    @Test
    public void testWetSoilDetection() {
        // Test voltage < 3.2 (THRESHOLD_WET/DAMP boundary)
        String actualStatus = secondaryTask.determineSoilStatus(1.5);
        assertEquals("wet soil", actualStatus);
    }
}
