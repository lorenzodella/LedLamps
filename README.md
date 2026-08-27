# LedLamps 💡

An IoT-based smart LED lighting system built around **ESP8266**, featuring an **Android mobile app**, a **Java backend server**, a **MySQL database**, and a **web interface** for direct control.

> **Educational project developed by Lorenzo Della Matera as a final school project for the 5E Informatics class at I.T.S.T. "Enea Mattei" – Sondrio, Italy (2020/2021).**

## 📋 Overview

**LedLamps** is a complete home automation project designed to remotely control **RGB LED strips** through a combination of embedded hardware, backend services, and mobile/web applications.

The system supports multiple lighting effects, custom colors, sound-reactive lighting, scheduled automations, and both local and remote control.

The project was developed with the goal of exploring different areas of software and hardware development within a single IoT ecosystem:

* Embedded programming with **ESP8266**
* Android application development
* Java backend development
* Database design and management
* Web development
* Network communication
* Data encryption and authentication

## 🏗️ System Architecture

The system is composed of several interconnected components:

* **Android App** — mobile interface for remote lamp control
* **ESP8266 Firmware** — controls the LED strips and provides local web access
* **Java Server** — handles communication between clients and the lamps
* **MySQL Database** — stores users, automations, and configuration data
* **Web Interface** — allows direct control through a browser

```text
┌─────────────────┐    ┌─────────────────┐    ┌─────────────────┐
│   Android App   │    │   Java Server   │    │   MySQL DB      │
│                 │◄──►│                 │◄──►│                 │
└─────────────────┘    └─────────────────┘    └─────────────────┘
                               │
                               ▼
                    ┌─────────────────┐
                    │    ESP8266      │
                    │     Master      │
                    └─────────────────┘
                               │
                               ▼
                    ┌─────────────────┐
                    │    ESP8266      │
                    │      Slave      │◄──► LED Strip
                    └─────────────────┘
```

## 🚀 Features

### 📱 Android App

* User registration and authentication
* Remote lamp control
* Customizable lighting modes
* Scheduled automations
* User profile management
* Color selection and customization
* Communication with the backend server

### 🔧 ESP8266 Firmware

The firmware is designed around a **Master/Slave architecture**, allowing multiple ESP8266 devices and LED strips to work together.

Features include:

* Automatic Wi-Fi connection
* Master/Slave communication
* Built-in web server for direct control
* UDP communication between devices
* RGB LED strip control
* Multiple lighting effects
* Sound-reactive lighting
* AES-based communication encryption

Available lighting modes include:

* **ON / OFF**
* **Sound Reactive**
* **Chill Fade**
* **Color Wipe**
* **Color Flash**
* **Rainbow Cycle**
* **Rainbow Fade**
* **Rainbow Chase**
* **Custom Colors**

### 🖥️ Java Server

The Java backend acts as the central communication layer between the application, database, and ESP8266 devices.

It provides:

* Socket-based communication
* Integrated web server
* MySQL database connectivity
* User authentication
* REST-style APIs for remote control
* Encrypted communications
* Server-side logging and error handling

### 🗄️ Database

The MySQL database is used to manage:

* User accounts
* Authentication data
* Scheduled automations
* Lamp configurations
* Operation history

## 📁 Project Structure

```text
LedLamps/
├── Android/                    # Android application
│   ├── app/
│   │   └── src/main/
│   │       ├── java/com/example/ledlamps/
│   │       │   ├── launcher/   # Startup and login
│   │       │   ├── main/       # Main application
│   │       │   ├── settings/   # Settings
│   │       │   ├── user/       # User management
│   │       │   └── utils/      # Utilities
│   │       └── res/            # Layouts, drawables, etc.
│   └── build.gradle
│
├── Java Server/                # Java backend
│   ├── JavaServer.java         # Main server
│   ├── LedLamps.java           # Entry point
│   ├── WebServer.java          # Web server
│   ├── DBConnector.java        # Database connection
│   ├── AES.java                # Encryption
│   └── *.jar                   # Dependencies
│
├── Lamps/                      # ESP8266 firmware
│   ├── master/                 # ESP8266 Master
│   │   ├── master.ino
│   │   ├── data/               # Web interface files
│   │   └── *.h, *.cpp          # AES and utility libraries
│   │
│   └── slave/                  # ESP8266 Slave
│       └── slave.ino
│
├── Database/                   # Database scripts and schema
│   ├── ledlamps.sql
│   ├── schema_*.png
│   └── ssl/
│
└── releases/                   # Release builds
    └── LedLamps.apk
```

## 🛠️ Installation & Setup

### Prerequisites

Depending on which components you want to run, you may need:

* **Arduino IDE** with ESP8266 board support
* **Android Studio**
* **Java JDK 8+**
* **MySQL Server**
* An **ESP8266-compatible board** such as a Wemos D1 Mini or NodeMCU
* RGB/NeoPixel-compatible LED strips

### Arduino Libraries

The ESP8266 firmware uses libraries including:

* [FastLED](https://fastled.io/)
* ESP8266WiFi
* ArduinoJson

### 🗄️ Database Setup

Import the provided SQL dump into MySQL:

```bash
mysql -u root -p < Database/ledlamps.sql
```

If SSL is required, configure the certificates according to your MySQL installation.

> **Note:** Database credentials and other environment-specific configuration should not be committed to the repository.

### 🖥️ Java Server Setup

Navigate to the server directory:

```bash
cd "Java Server"
```

Compile the Java sources:

```bash
javac -cp "mysql-connector-j-8.0.31.jar:json-20220924.jar:." *.java
```

Run the server:

```bash
java -cp "mysql-connector-j-8.0.31.jar:json-20220924.jar:." LedLamps
```

Depending on your operating system, the classpath separator may need to be changed from `:` to `;`.

### 🔧 ESP8266 Setup

1. Open `Lamps/master/master.ino` in Arduino IDE.
2. Configure the Wi-Fi credentials.
3. Select the correct ESP8266 board and serial port.
4. Upload the firmware to the Master device.
5. Configure and upload `Lamps/slave/slave.ino` to the Slave device.
6. Connect the LED strips according to the hardware configuration.

**Do not commit real Wi-Fi credentials or passwords to the repository.**

### 📱 Android App

1. Open the `Android/` directory with Android Studio.
2. Allow Gradle to synchronize the project.
3. Configure the server address if required.
4. Build the application.
5. Install the generated APK on an Android device.

## 🎮 Usage

### Android App

The typical workflow is:

1. Create an account or log in.
2. Connect the application to the LedLamps server.
3. Select the lamp or device to control.
4. Choose a lighting mode.
5. Customize colors or other available parameters.
6. Save automations if required.

### Web Interface

The ESP8266 Master provides a web interface for local control.

When operating in Access Point mode, connect to the ESP8266 network and open:

```text
http://192.168.4.1
```

The exact address may differ depending on the network configuration.

## 🌈 Lighting Modes

| Mode               | Description                           |
| ------------------ | ------------------------------------- |
| **OFF / ON**       | Turns the LEDs off or on              |
| **Sound Reactive** | Reacts to ambient sound               |
| **Chill Fade**     | Smooth and relaxing color transitions |
| **Color Wipe**     | Progressive color filling effect      |
| **Color Flash**    | Flashing color effect                 |
| **Rainbow Cycle**  | Continuous rainbow animation          |
| **Rainbow Fade**   | Smooth rainbow transitions            |
| **Rainbow Chase**  | Moving rainbow effect                 |
| **Custom**         | User-defined colors                   |

## 🌐 Network Architecture

LedLamps supports multiple network configurations:

### Wi-Fi Infrastructure

ESP8266 devices connect to an existing Wi-Fi network and communicate with other components on the network.

### Access Point

The ESP8266 can operate as an access point, allowing a device to connect directly to it and use the built-in web interface.

### Remote Server

The architecture also supports communication through a remotely hosted server, allowing the Android application to control the lamps from outside the local network when the required network configuration is available.

## 🔒 Security

The original project includes several security mechanisms:

* AES-based encryption for selected communications
* Password hashing
* User authentication
* Input validation
* HTTPS support where configured

> **Security note:** This project was originally developed as an educational project in 2020/2021. Some cryptographic choices and security practices may be outdated by current standards. In particular, **SHA-1 and MD5 should not be used for new password-storage implementations**. If deploying this project today, the security layer should be reviewed and modernized.

## 🧰 Technologies

| Area                  | Technologies                                             |
| --------------------- | -------------------------------------------------------- |
| **Hardware**          | ESP8266, Wemos D1 Mini, NodeMCU, RGB/NeoPixel LED strips |
| **Embedded**          | Arduino, C/C++                                           |
| **Backend**           | Java                                                     |
| **Mobile**            | Android, Java, Android Studio                            |
| **Web**               | HTML, CSS, JavaScript                                    |
| **Database**          | MySQL, SQL                                               |
| **Communication**     | UDP, TCP, HTTP/HTTPS, JSON                               |
| **Security**          | AES, SHA-1, MD5                                          |
| **External Services** | IFTTT / Google Assistant                                 |

## 🐛 Monitoring & Debugging

The different components provide several debugging mechanisms:

* Serial Monitor logs from the ESP8266
* Java server console logs
* Error handling across the main components
* Network communication debugging

## 📌 Project Status

**Archived educational project.**

LedLamps was developed during the **2020/2021 school year** as a final school project. The repository is primarily intended as a record of the original implementation and as a reference for learning purposes.

The project may require updates to dependencies, libraries, build tools, and security mechanisms to work correctly on modern systems.

## 🤝 Contributing

This repository contains an educational project originally developed by **Lorenzo Della Matera**.

Contributions, bug fixes, and improvements are welcome, especially if they help modernize the project or improve its documentation.

## 📄 License

This project was developed for **educational purposes**.

Unless otherwise specified in individual files or dependencies, the source code is provided for educational and reference purposes.

## 👤 Author

**Lorenzo Della Matera**

* Class: 5E Informatics
* School: I.T.S.T. "Enea Mattei" – Sondrio, Italy
* Academic year: 2020/2021

---

⭐ If you find this project useful or interesting, feel free to explore the code and experiment with the different components.
