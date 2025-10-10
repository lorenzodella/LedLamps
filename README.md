# LedLamps 💡

**Autore:** Della Matera Lorenzo  
**Classe:** 5E Informatica  
**Istituto:** I.T.S.T. “Enea Mattei” – Sondrio  
**Anno scolastico:** 2020/2021  

Un sistema completo per il controllo intelligente di lampade LED tramite ESP8266, con app mobile Android, server Java e interfaccia web.

## 📋 Panoramica del Progetto

LedLamps è un progetto di domotica che integra **hardware programmabile**, **server software** e **applicazioni mobili/web** per il controllo remoto di lampade a LED RGB.  
L’obiettivo è realizzare un sistema IoT completo, sostenibile e personalizzabile, in grado di gestire **illuminazioni multicolore, animazioni luminose e reazioni sonore**, sia localmente che via Internet.

## 🏗️ Architettura del Sistema
Il sistema include:
- **App Android** per il controllo remoto delle lampade
- **Firmware Arduino** per ESP8266 (Master e Slave)
- **Server Java** per la gestione delle comunicazioni
- **Database MySQL** per la persistenza dei dati
- **Interfaccia Web** per il controllo diretto delle lampade

```
┌─────────────────┐    ┌─────────────────┐    ┌─────────────────┐
│   App Android   │    │   Server Java   │    │   Database      │
│                 │◄──►│                 │◄──►│   MySQL         │
└─────────────────┘    └─────────────────┘    └─────────────────┘
                               │
                               ▼
                    ┌─────────────────┐
                    │   ESP8266       │
                    │   Master        │
                    └─────────────────┘
                               │
                               ▼
                    ┌─────────────────┐
                    │   ESP8266       │
                    │   Slave         │◄──► LED Strip
                    └─────────────────┘
```

## 🚀 Funzionalità Principali

### 📱 App Android
- **Autenticazione utente** (Login/Registrazione)
- **Controllo remoto** delle lampade
- **Modalità di illuminazione** personalizzabili
- **Automazioni** programmate
- **Interfaccia utente** intuitiva e moderna
- **Gestione profili** utente

### 🔧 ESP8266 Firmware
- **Modalità Master/Slave** per il controllo di più strip LED
- **Connessione WiFi** automatica
- **Server web integrato** per controllo diretto
- **Comunicazione UDP** tra dispositivi
- **Modalità operative**:
  - Sound Reactive (reattivo al suono)
  - Chill Fade
  - Color Wipe
  - Color Flash
  - Rainbow Cycle/Fade/Chase
  - Controllo colore personalizzato
- **Crittografia AES** per comunicazioni sicure

### 🖥️ Server Java
- **Server socket** per comunicazioni con l'app
- **Web server** integrato
- **Gestione database** MySQL
- **Sistema di autenticazione** con keychain
- **API RESTful** per il controllo remoto
- **Crittografia** per comunicazioni sicure

### 🗄️ Database
- **Gestione utenti** e autenticazione
- **Automazioni** programmate
- **Storico** delle operazioni
- **Configurazioni** personalizzate

## 📁 Struttura del Progetto

```
LedLamps/
├── Android/                    # App Android
│   ├── app/
│   │   └── src/main/
│   │       ├── java/com/example/ledlamps/
│   │       │   ├── launcher/   # Schermata di avvio e login
│   │       │   ├── main/       # Attività principale
│   │       │   ├── settings/   # Impostazioni
│   │       │   ├── user/       # Gestione utente
│   │       │   └── utils/      # Utilità
│   │       └── res/            # Risorse (layout, drawable, etc.)
│   └── build.gradle
├── Java Server/                # Server backend
│   ├── JavaServer.java         # Server principale
│   ├── LedLamps.java          # Entry point
│   ├── WebServer.java         # Server web
│   ├── DBConnector.java       # Connessione database
│   ├── AES.java               # Crittografia
│   └── *.jar                  # Dipendenze
├── Lamps/                     # Firmware Arduino
│   ├── master/                # ESP8266 Master
│   │   ├── master.ino
│   │   ├── data/              # File web server
│   │   └── *.h, *.cpp         # Librerie AES e utils
│   └── slave/                 # ESP8266 Slave
│       └── slave.ino
├── Database/                  # Script e schema database
│   ├── ledlamps.sql
│   ├── schema_*.png
│   └── ssl/
└── releases/                  # Build finali
    └── LedLamps.apk
```

## 🛠️ Installazione e Setup

### Prerequisiti
- **Arduino IDE** con supporto ESP8266
- **Android Studio** per l'app mobile
- **Java JDK 8+** per il server
- **MySQL Server** per il database
- **Librerie Arduino**:
  - FastLED
  - ESP8266WiFi
  - ArduinoJson

### Setup Database
```bash
# Importa il database
mysql -u root -p < Database/ledlamps.sql

# Configura la connessione SSL (opzionale)
cp Database/ssl/* /path/to/mysql/ssl/
```

### Configurazione Server Java
```bash
cd "Java Server"
# Compila ed esegui
javac -cp "mysql-connector-j-8.0.31.jar:json-20220924.jar:." *.java
java -cp "mysql-connector-j-8.0.31.jar:json-20220924.jar:." LedLamps
```

### Setup ESP8266
1. Apri `Lamps/master/master.ino` in Arduino IDE
2. Configura le credenziali WiFi:
   ```cpp
   #define WIFI_SSID "TuoSSID"
   #define WIFI_PASS "TuaPassword"
   ```
3. Carica il firmware sul dispositivo master
4. Ripeti per il firmware slave (`Lamps/slave/slave.ino`)

### Build App Android
1. Apri il progetto `Android/` in Android Studio
2. Sincronizza le dipendenze Gradle
3. Build e installa su dispositivo Android

## 🎮 Utilizzo

### Controllo via App Android
1. Registra un nuovo account o effettua il login
2. Connetti l'app al server
3. Seleziona le lampade da controllare
4. Scegli modalità di illuminazione e colori

### Controllo via Web Interface
1. Connettiti alla rete WiFi dell'ESP8266 o assicurati di essere sulla stessa rete
2. Naviga su `http://192.168.4.1`
3. Utilizza l'interfaccia web per il controllo diretto

### Modalità Disponibili
- **OFF/ON**: Spegni/Accendi
- **Sound Reactive**: Reattivo al suono ambientale
- **Chill Fade**: Dissolvenza rilassante
- **Color Wipe**: Riempimento colore progressivo
- **Color Flash**: Lampeggio colorato
- **Rainbow**: Effetti arcobaleno (Cycle/Fade/Chase)
- **Custom**: Colori personalizzati

## 🔒 Sicurezza

- **Crittografia AES** per le comunicazioni
- **Autenticazione utente** con hash delle password
- **Comunicazioni HTTPS** quando possibile
- **Validazione input** su tutti i livelli

## 🌐 Configurazione di Rete

Il sistema supporta diverse modalità di connessione:
- **WiFi Infrastructure**: Connessione alla rete domestica
- **Access Point**: ESP8266 come hotspot per controllo diretto
- **Cloud**: Connessione tramite server remoto Azure

## 🧰 Tecnologie utilizzate

| Area | Tecnologie |
|------|-------------|
| **Hardware** | ESP8266 (Wemos D1 Mini, NodeMCU), LED RGB NeoPixel |
| **Linguaggi** | C/C++ (Arduino), Java, PHP, JavaScript, SQL |
| **Database** | MySQL |
| **App Mobile** | Android Studio (Java) |
| **Comunicazioni** | UDP, TCP, HTTP/HTTPS, JSON |
| **Sicurezza** | AES, SHA1, MD5 |
| **Servizi esterni** | IFTTT (Google Assistant) |

## 📊 Monitoraggio e Debug

- **Log dettagliati** su serial monitor Arduino
- **Debug console** nel server Java
- **Error handling** robusto su tutti i componenti

## 🤝 Contributi

Questo è un progetto scolastico di **Della Matera Lorenzo** per la classe 5E. 

## 📄 Licenza

Progetto sviluppato per scopi didattici.

## 📞 Supporto

Per domande o problemi tecnici, contattare l'autore del progetto.

---

**Nota**: Questo sistema è stato progettato e sviluppato come progetto finale per il corso di studi. Include implementazioni avanzate di IoT, crittografia, sviluppo mobile e web development.