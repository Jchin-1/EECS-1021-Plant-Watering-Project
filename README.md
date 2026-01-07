# 🌱 Automated Plant Watering System

![Java](https://img.shields.io/badge/Java-17-orange)
![Firmata4j](https://img.shields.io/badge/Firmata4j-2.3.9-blue)
![Status](https://img.shields.io/badge/Status-Active-green)

An intelligent IoT plant watering solution developed for **EECS 1021**. This project monitors soil moisture levels in real-time and automatically activates a water pump when the soil becomes too dry. It features a live OLED dashboard and a visual data graph.

---

## 🚀 Features

- **Automated Watering**: Automatically waters the plant when soil moisture drops below specific voltage thresholds.
- **Real-time Monitoring**:
    - **OLED Display**: Shows current status (Dry/Damp/Wet) and raw voltage readings.
    - **Live Graph**: Plots moisture levels over time using `StdDraw`.
- **State Machine Logic**: Robust logic cycle running every 4.5 seconds to prevent over-watering.
- **Hardware Integration**: Seamless control of Grove sensors and actuators via Firmata protocol.

---

## 🛠️ Hardware Setup

The system interacts with a **Grove Board** (Arduino-compatible) via USB (COM3).

| Component | Pin / Port | Type | Description |
| :--- | :--- | :--- | :--- |
| **Potentiometer** | `A0` (14) | Input | Sensitivity adjustment (optional) |
| **Moisture Sensor** | `A1` (15) | Analog Input | Reads soil conductivity |
| **Sound Sensor** | `A2` (16) | Input | Ambient controls (optional) |
| **Button** | `D6` (6) | Digital Input | Manual override / Reset |
| **Water Pump** | `D7` (7) | Digital Output | Controlled via MOSFET |
| **LED** | `D4` (4) | Digital Output | Status indicator |
| **OLED Display** | `I2C` (0x3C)| I2C | 128x64 Status Screen |

---

## 🧩 Software Architecture

The system is built on a scheduled task architecture using `java.util.Timer`.

### State Machine Transition
The `Secondary` class implements the hydration logic:

1.  **Read Sensor**: Converts Analog (0-1023) to Voltage (0-5V).
2.  **Determine State**:
    *   **DRY** (`>= 3.4V`): Activate Pump for **3.5s**.
    *   **DAMP** (`3.2V - 3.4V`): Activate Pump for **2.5s**.
    *   **WET** (`< 3.2V`): Standby mode.
3.  **Update UI**: Refresh OLED text and update the `StdDraw` line graph.

---

## 📦 Installation & Usage

### Prerequisites
*   **Java 17** or higher
*   **Maven** (for dependency management)
*   **Grove Board** connected to `COM3` (Update `USB_PORT` in `Main.java` if different)

### Build
This project uses Maven. Run the following command to download dependencies:
```bash
mvn clean install
```

### Run
To start the monitoring system:
```bash
mvn exec:java -Dexec.mainClass="eecs1021.Main"
```
*Note: Ensure the board is connected before running.*

### Run Tests
To verify the logic (without hardware):
```bash
mvn test
```

---

## 📂 Project Structure

```
├── eecs1021/
│   ├── Main.java         # Entry point, hardware initialization
│   ├── Secondary.java    # Logic, State Machine, and UI updates
│   └── UnitTesting.java  # JUnit tests for logic verification
├── pom.xml               # Maven dependencies (Firmata4j, StdLib, JUnit)
└── README.md             # Project documentation
```

---

## 🛡️ License

This project is part of the EECS 1021 coursework.
