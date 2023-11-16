# Overview: FlowOS (Android app)   

Welcome to FlowOS, an Android application designed for efficient and secure management of bus transportation systems. This solution integrates various sensors and technologies to track bus location, monitor bus motion dynamics, and facilitate passenger count.

This Android app gathers data from NFC cards to identify bus drivers, GPS to locate the bus in the road, accelerometer, linear acceleromenter, and gyroscope to understand how the motion of the bus and BLE (Bluetooth Low Energy) sensor to detect how many passengers are in the bus at a given time.

Besides, this app takes over the device, locking it when the bus is in movement and unlocking it in no-movement timeframes. Also, the app sets up Kiosk Mode which enables the Android device to only run FlowOS. Other Android features (like dialer, messages or email app, among others) are disabled. 


# Features   

## Data Gathering   

### a. NFC Card Integration
The app enables drivers to log in through NFC cards, providing a reliable method for driver identification and authentication.

### b. GPS Tracking
Utilizing GPS sensor, the app determines the real-time location of the bus on the road, ensuring efficient route management.

### c. Motion Analysis
The app leverages a combination of accelerometer, linear accelerometer, and gyroscope sensors to gain insights into the motion dynamics of the bus. This information contributes to a deeper understanding of the vehicle's behavior during transit.

### d. Passenger Counting
Bluetooth Low Energy (BLE) sensor is employed to detect an approximation of the number of passengers on the bus at any given time.


## Device Control   

### a. Motion-Driven Device Locking
To prioritize safety and minimize distractions, the app automatically locks the device when the bus is in motion. This feature ensures the driver's focus remains on the road.

### b. Unlocking in No-Movement Timeframes
In instances of no movement, the app intelligently unlocks the device, allowing the driver to access necessary information during breaks or when the bus is stationary.

### c. Kiosk Mode Implementation
The app goes a step further by implementing Kiosk Mode, restricting the Android device to run exclusively on FlowOS. This focused environment eliminates distractions by disabling non-essential features such as the dialer, messaging apps, and email, ensuring optimal operational efficiency.

  
## App Foreground Flow   

<img src="/docs/1st_phase/app_flow.png" alt="app foreground flow">



## App Background Flow

<img src="/docs/1st_phase/background_tasks _specification.png" alt="app background flow">

## Others  
1. Project's CodeStyle can be found [here](docs/codestyle.md).  
2. Project utilities file can be found [here](docs/utilities.md).
3. CI/CD documentation can be found [here](docs/cicd.md).  
4. FlowOS 1st Phase documentation can be found [here](docs/1st_phase).
