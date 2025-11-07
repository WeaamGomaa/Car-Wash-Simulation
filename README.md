# 🚗 Raghwa Wash Simulation

A Java Swing application demonstrating the classic **Producer-Consumer synchronization problem** using semaphores and bounded buffers, simulating a busy car wash station.

## Overview

This project simulates a car wash service station where:
- **Cars (Producers)** arrive and enter a waiting queue
- **Pumps (Consumers)** service cars from the queue
- **Semaphores** manage synchronization and prevent race conditions
- **Bounded Buffer** limits the waiting area capacity

## Features

-  Custom Semaphore implementation (wait/notify mechanism)
-  Thread-safe producer-consumer pattern
- Real-time GUI visualization with Java Swing
-  Configurable parameters (queue size, pumps, cars)
- Activity logging with timestamps
- Visual status indicators for pumps (Free/Busy)
- Live queue display

## How to Run

1. **Compile:**
   ```bash
   javac ServiceStation.java
   ```

2. **Run:**
   ```bash
   java ServiceStation
   ```

3. **Configure & Start:**
   - Enter waiting area capacity (1-10)
   - Enter number of service pumps
   - Enter number of cars to simulate
   - Click **Start Simulation**

##  GUI Components

| Component | Description |
|-----------|-------------|
| **Service Bays** | Visual representation of each pump (Green=Busy, Gray=Free) |
| **Waiting Queue** | Shows cars waiting for service |
| **Activity Log** | Real-time event logging with timestamps |
| **Statistics** | Tracks processed and arrived cars |

## Technical Implementation

### Classes

- **`Semaphore`**: Custom semaphore with acquire() and release() methods
- **`Car`**: Producer thread that adds itself to the queue
- **`Pump`**: Consumer thread that services cars from the queue
- **`CarWashGUI`**: Main GUI controller and visualization
- **`ServiceStation`**: Application entry point

### Synchronization

- **Empty Semaphore**: Tracks available queue slots
- **Full Semaphore**: Tracks cars in queue
- **Mutex Semaphore**: Ensures mutual exclusion for queue access

##  Sample Output

```
[14:23:15] === Simulation Started ===
[14:23:15] Waiting Capacity: 5
[14:23:15] Number of Pumps: 3
[14:23:15] Number of Cars: 10
[14:23:15] C1 arrived and entered queue
[14:23:16] Pump 1: C1 login
[14:23:16] Pump 1: C1 begins service at Bay 1
[14:23:17] C2 arrived and entered queue
[14:23:19] Pump 1: C1 finishes service
[14:23:19] Pump 1: Bay 1 is now free
...
```


## Authors
- Weaam Gomma
- Noura Yasser
- Rofida Fahd
- Nagat Mohammed
- Anoud Mohammed 






**Note:** Service time per car is set to 3 seconds. Cars arrive every 2 seconds. Modify `Thread.sleep()` values in `Pump.run()` and `startSimulation()` to adjust timing.
