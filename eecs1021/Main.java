package eecs1021;

import org.firmata4j.I2CDevice;
import org.firmata4j.IODevice;
import org.firmata4j.Pin;
import org.firmata4j.firmata.FirmataDevice;
import org.firmata4j.ssd1306.SSD1306;
import java.io.IOException;
import java.util.Timer;

/**
 * Main class for the Automated Plant Watering Project.
 * <p>
 * This application sets up the connection to the Grove board via Firmata4j,
 * initializes the sensors and actuators (OLED, Button, Moisture Sensor, Pump, LED),
 * and schedules the recurring state machine task.
 */
public class Main {

    // Helper Constants for Pin Assignments
    private static final int PIN_POTENTIOMETER = 14; // A0
    private static final int PIN_MOISTURE_SENSOR = 15; // A1
    private static final int PIN_SOUND = 16; // A2
    private static final int PIN_BUTTON = 6; // D6
    private static final int PIN_PUMP = 7; // D7 (MOSFET/Water Pump)
    private static final int PIN_LED = 4; // D4
    private static final byte I2C_ADDRESS_OLED = 0x3C; // Grove OLED Address

    private static final String USB_PORT = "COM3"; // Port for Firmata connection

    /**
     * Entry point of the application.
     *
     * @param args Command line arguments (not used).
     * @throws InterruptedException If the thread connection is interrupted.
     * @throws IOException          If there is an IO error communicating with the device.
     */
    public static void main(String[] args) throws InterruptedException, IOException {
        System.out.println("Starting Plant Watering System...");

        // Connect to the Arduino/Grove board
        IODevice arduinoBoard = new FirmataDevice(USB_PORT);
        arduinoBoard.start();
        arduinoBoard.ensureInitializationIsDone();
        System.out.println("Board initialized.");

        // Initialize OLED Display
        I2CDevice i2cObject = arduinoBoard.getI2CDevice(I2C_ADDRESS_OLED);
        SSD1306 oledDisplay = new SSD1306(i2cObject, SSD1306.Size.SSD1306_128_64);
        oledDisplay.init();

        // Initialize Pins
        var buttonPin = arduinoBoard.getPin(PIN_BUTTON);
        buttonPin.setMode(Pin.Mode.INPUT);

        var moistureSensor = arduinoBoard.getPin(PIN_MOISTURE_SENSOR);
        moistureSensor.setMode(Pin.Mode.ANALOG);

        var waterPump = arduinoBoard.getPin(PIN_PUMP);
        waterPump.setMode(Pin.Mode.OUTPUT);

        // Schedule the secondary task (State Machine) to run every 4.5 seconds
        Timer timer = new Timer();
        var wateringTask = new Secondary(oledDisplay, moistureSensor, waterPump, timer);
        
        System.out.println("Starting monitoring task...");
        timer.scheduleAtFixedRate(wateringTask, 0, 4500);
    }
}
