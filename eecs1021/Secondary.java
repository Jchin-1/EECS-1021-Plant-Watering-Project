package eecs1021;

import edu.princeton.cs.introcs.StdDraw;
import org.firmata4j.Pin;
import org.firmata4j.ssd1306.SSD1306;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;

/**
 * Secondary task class implementing the core logic of the Plant Watering System.
 * <p>
 * This class runs as a scheduled task, checking soil moisture levels,
 * controlling the water pump based on those levels, and updating the
 * OLED display and StdDraw graph.
 */
public class Secondary extends TimerTask {

    // Soil Moisture Voltage Thresholds
    private static final double THRESHOLD_DRY = 3.4;
    private static final double THRESHOLD_DAMP = 3.2;
    private static final double THRESHOLD_WET = 2.9;

    private final SSD1306 display;
    private final Pin sensorPin;
    private final Pin pumpPin;
    private final Timer timer;

    private final ArrayList<Double> moistureHistory = new ArrayList<>();

    /**
     * Constructor for the Secondary task.
     *
     * @param display   The OLED display object.
     * @param sensorPin The Pin connected to the moisture sensor.
     * @param pumpPin   The Pin connected to the water pump.
     * @param timer     The Timer object managing this task.
     */
    public Secondary(SSD1306 display, Pin sensorPin, Pin pumpPin, Timer timer) {
        this.display = display;
        this.sensorPin = sensorPin;
        this.pumpPin = pumpPin;
        this.timer = timer;
    }

    /**
     * Reads the analog value from the sensor AND converts it to voltage.
     *
     * @return The calculated voltage (0v - 5v).
     */
    public double getMoistureVoltage() {
        long rawValue = sensorPin.getValue();
        return rawValue * (5.0 / 1023.0);
    }

    /**
     * Determines the textual status of the soil based on voltage.
     * Useful for unit testing logic without hardware interaction.
     *
     * @param soilVoltage The voltage reading from the sensor.
     * @return A string representing "dry soil", "damp soil", or "wet soil".
     */
    public String determineSoilStatus(double soilVoltage) {
        if (soilVoltage >= THRESHOLD_DRY) {
            return "dry soil";
        } else if (soilVoltage >= THRESHOLD_DAMP) {
            return "damp soil";
        } else {
            return "wet soil";
        }
    }

    @Override
    public void run() {
        double currentVoltage = getMoistureVoltage();
        String formattedVoltage = String.format("%.3f", currentVoltage);
        String statusMessage;

        try {
            // State Machine Logic: Control Pump based on Moisture
            if (currentVoltage >= THRESHOLD_DRY) {
                statusMessage = "Status: Dry...Watering...";
                cyclePump(3500); // Water for 3.5s
            } else if (currentVoltage >= THRESHOLD_DAMP) {
                statusMessage = "Status: Damp...Watering...";
                cyclePump(2500); // Water for 2.5s
            } else {
                statusMessage = "Status: Wet...Standby...";
                pumpPin.setValue(0); // Ensure pump is OFF
                Thread.sleep(2000); // Wait 2s
            }

            // Update Graph and OLED
            updateGraph(currentVoltage);
            updateDisplay(formattedVoltage, statusMessage);

            // Log to Console
            System.out.println("Moisture value: " + formattedVoltage);
            System.out.println(statusMessage);

        } catch (IOException | InterruptedException e) {
            System.err.println("Error in monitoring loop: " + e.getMessage());
            throw new RuntimeException(e);
        }
    }

    /**
     * Helper to cycle the pump on for a specific duration.
     *
     * @param durationMs Duration in milliseconds to keep pump ON.
     * @throws IOException          If hardware communication fails.
     * @throws InterruptedException If the thread is interrupted.
     */
    private void cyclePump(int durationMs) throws IOException, InterruptedException {
        pumpPin.setValue(1); // Turn ON
        Thread.sleep(durationMs);
        pumpPin.setValue(0); // Turn OFF
    }

    /**
     * Updates the OLED display with current readings.
     *
     * @param voltageStr The formatted voltage string.
     * @param status     The status message string.
     */
    private void updateDisplay(String voltageStr, String status) {
        display.getCanvas().clear();
        display.getCanvas().drawString(0, 0, "Volts: " + voltageStr);
        display.getCanvas().drawString(0, 10, status);
        display.display();
    }

    /**
     * Updates the StdDraw graph with the new moisture data point.
     *
     * @param voltage The current moisture voltage.
     */
    private void updateGraph(double voltage) {
        moistureHistory.add(voltage);

        StdDraw.setPenColor(StdDraw.BLUE);
        StdDraw.setPenRadius(0.005);
        StdDraw.setXscale(-17, 100);
        StdDraw.setYscale(-4, 10);
        
        // Redraw axes (simple redraw approach)
        StdDraw.clear(); // Clear previous frame to avoid smearing if needed, or keep for persistence. 
        // Note based on original code, it seemed to rely on persistence or just overwriting.
        // Adding basic axes again.
        StdDraw.line(0, 0, 0, 5);
        StdDraw.line(0, 0, 100, 0);
        StdDraw.text(50, -0.5, "Time (s)");
        StdDraw.text(-12, 2.5, "Moisture (v)");
        StdDraw.text(50, 7, "Time vs Moisture Vals");

        for (int i = 0; i < moistureHistory.size(); i++) {
            StdDraw.point((double) i, moistureHistory.get(i));
        }
    }
}
