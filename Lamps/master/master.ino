// Della Matera Lorenzo 5E

#include <FastLED.h>
#include <ESP8266WiFi.h>
#include <WiFiUDP.h>
#include <WiFiClient.h>
#include <ESP8266WebServer.h>
#include "reactive_common.h"
#include "FS.h"
#include "AES.h"
#include "ebase64.h"
#include "crypto.h"
#include <ArduinoJson.h>

#define READ_PIN 0
#define BUTTON_PIN D1
#define LED_PIN D7

// Set WiFi credentials
#define WIFI_SSID "TIM-79053995"
#define WIFI_PASS "nonmelaricordolapassword"
//#define WIFI_SSID "ciaoemiao"
//#define WIFI_PASS "tivadivoltailcervello"
String ssid_now = "";
String pass_now = "";
#define KEY 1
 
// Set AP credentials
#define AP_SSID "LedLamps"
#define AP_PASS "123456789"
#define PORT 12345
String HOST = "lorenzodellamatera.northeurope.cloudapp.azure.com";

#define NUMBER_OF_CLIENTS 2
int number_of_clients = 2;
bool allLamps = true;
bool semaphore = false;
bool error_signal_flag = false;

const int checkDelay = 5000;
const int buttonDoubleTapDelay = 200;
const int numOpModes = 16;

bool randomEnabled = false;
unsigned long lastRandom;

unsigned long lastSync;
unsigned long lastChecked;
unsigned long buttonChecked;
bool buttonClicked = false;
bool queueDouble = false;
bool clickTrigger;
bool doubleTapped;
WiFiUDP UDP;

struct led_command {
  uint8_t opmode;
  uint32_t data;
  uint32_t data2;
};

bool heartbeats[NUMBER_OF_CLIENTS];

static int opMode = 1;
int lastMode = 1;

WiFiClient client;
ESP8266WebServer server(80);
int brightness_now_int = 255;
String brightness_now = "#ffffff";
String text_now = "black";
String color1 = "#ffffff";
String color2 = "#ffffff";
String color3 = "#ffffff";

String encrypt(String s, int shift){
  int i;
  String tmp = s;
  for(i=0; i<s.length(); i++){
    shift++;
    char c = s.charAt(i);
    if(s.charAt(i) >= 33 && s.charAt(i) <= 126)
      c = (c-33+(shift))%94+33;
    tmp.setCharAt(tmp.length()-1-i, c);
  }
  return tmp;
}

String decrypt(String s, int shift){
  int i;
  String tmp = s;
  shift += s.length()+1;
  for(i=0; i<s.length(); i++){
    shift--;
    char c = s.charAt(i);
    if(s.charAt(i) >= 33 && s.charAt(i) <= 126)
      c = (c-33+(94-shift))%94+33;
    tmp.setCharAt(tmp.length()-1-i, c);
  }
  return tmp;
}

void saveWifiSettings(){
  File file = SPIFFS.open("/wifi.txt", "w");
  if (!file) {
    Serial.println("Error opening file for writing");
    return;
  }
  int bytesWritten = file.print(ssid_now + ";" + encrypt(pass_now, KEY));
  file.close();
}
void loadWifiSettings(){
  String setting = readDataFromFile("/wifi.txt");
  if(setting!=""){
    ssid_now = setting.substring(0,setting.indexOf(";"));
    pass_now = decrypt(setting.substring(setting.indexOf(";")+1), KEY);
    Serial.println("wifi settings loaded");
  }
  else{
    ssid_now = WIFI_SSID;
    pass_now = WIFI_PASS;
  }
}

String readDataFromFile(String fileName){
  File file = SPIFFS.open(fileName, "r");
  if (!file) {
    Serial.println("Error opening file for reading");
    return "";
  }
  String dat="";
  while(file.available()){
    dat+=(char)file.read();
  }
  file.close();
  return dat;
}

String getJsonData(){
  String data = "{ \"fade1\": \"" + color1 + "\"" +
                ", \"fade2\": \"" + color2 + "\"" +
                ", \"custom\": \"" + color3 + "\"" +
                ", \"opMode\": \"" + opMode + "\"" +
                ", \"random\": \"" + randomEnabled + "\"" +
                ", \"brightness\": \"" + brightness_now_int + "\"" +
                ", \"ssid\": \"" + ssid_now + "\"" +
                ", \"connected\": \"" + client.connected() + "\"" + 
                ", \"ip\": \"" + WiFi.localIP().toString() + "\"" +
                ", \"host\": \"" + HOST + "\" }";
                   
  /*String data = "{ \"colors\" : {\
  \"fade1\": \"" + color1 + "\", \"fade2\": \"" + color2 + "\", \"custom\": \"" + color3 + "\"}, \
  \"modes\" : {\
  \"opMode\": \"" + opMode + "\", \"random\": \"" + randomEnabled + "\", \"brightness\": \"" + brightness_now_int + "\"}, \
  \"wifi\" : {\
  \"ssid\": \"" + ssid_now + "\", \"connected\": \"" + client.connected() + "\", \"ip\": \"" + WiFi.localIP().toString() + "\", \"host\": \"" + HOST + "\"}}";*/
  return data;
}
bool parseJsonData(String json){
  DynamicJsonDocument root(1024);
  DeserializationError error = deserializeJson(root, json);
  if (error) {
    Serial.println("deserializeJson() failed");
    Serial.println(error.f_str());
    return false;
  }
  semaphore = true;
  if(root["random"].as<int>() == 0)
    randomEnabled = false;
  else
    randomEnabled = true;
  handleBrightness(root["brightness"]);
  handleColor(root["fade1"], root["fade2"]);
  handleColor(root["custom"]);
  opMode = root["opMode"].as<int>();
  const char* h = root["host"];
  HOST = String(h);
  semaphore = false;

  Serial.println("->"+getJsonData());
  return true;
}

void sync(){
  server.send(200, "text/json", getJsonData());
}

void sendDataToServer(){
  // This will send a string to the server
  if(client.connected()) {
    Serial.println("sending data to server");
    client.println(AES_encrypt(getJsonData()));
    lastSync = millis();
  }
  else
    Serial.println("not connected to server");
}

void saveDataToFile(){
  File file = SPIFFS.open("/JSONdata.js", "w");
  if (!file) {
    Serial.println("Error opening file for writing");
    return;
  }
  int bytesWritten = file.print("data = '" + getJsonData() + "';");
  file.close();
}

void updateData(){
  saveDataToFile();
  sendDataToServer();
}

void handleRoot() {
  if(semaphore) return;

  if(opMode!=100) updateData();
  handleFileRead("/");
}

int colorCustom=0;
int colorFade1=0;
int colorFade2=0;
int subOption=0;

void handleMode(String aMode){
  if(aMode == "sound_reactive"){
    opMode = 1;
  } else
  if(aMode == "chill_fade"){
    opMode = 2;
  } else
  if(aMode == "color_wipe"){
    opMode = 3;
  } else
  if(aMode == "color_flash"){
    opMode = 4;
  } else
  if(aMode == "rainbow_cycle"){
    opMode = 5;
  } else
  if(aMode == "rainbow_fade"){
    opMode = 6;
  } else
  if(aMode == "rainbow_chase"){
    opMode = 7;
  } else
  if(aMode == "strobe"){
    opMode = 8;
  } else
  if(aMode == "fire"){
    opMode = 9;
  } else
  if(aMode == "balls"){
    opMode = 10;
  } else
  if(aMode == "fill_random"){
    opMode = 11;
  } else
  if(aMode == "sound_colors"){
    opMode = 12;
  } else
  if(aMode == "twinkle"){
    opMode = 13;
  } else
  if(aMode == "sparkle"){
    opMode = 14;
  } else
  if(aMode == "strobe_shot"){
    opMode = 15;
  } else
  if(aMode == "strobe_fade"){
    opMode = 16;
  } else
  if(aMode == "on"){
    opMode = 41;
  } else
  if(aMode == "off"){
    opMode = 42;
  } else
  if(aMode == "red"){
    opMode = 43;
    subOption = 1;
  } else
  if(aMode == "green"){
    opMode = 43;
    subOption = 2;
  } else
  if(aMode == "blue"){
    opMode = 43;
    subOption = 3;
  } else
  if(aMode == "custom_color"){
    opMode = 44;
  } else
  if(aMode == "custom_fade"){
    opMode = 45;
  } else
  if(aMode == "fade"){
    opMode = 46;
  } else
  if(aMode == "double"){
    opMode = 47;
  } else
  if(aMode == "fix"){
    opMode = 48;
  } else
  if(aMode == "swapping"){
    opMode = 49;
  }
  handleRoot();
}

void handleColor(String messageCustom){
  Serial.println(messageCustom);

  messageCustom.replace("%23", "#");
  colorCustom = colorHextToInt(messageCustom);
  color3 = messageCustom;
  
  handleRoot();
}

void handleColor(String messageFade1, String messageFade2){
  Serial.println(messageFade1 + messageFade2);

  messageFade1.replace("%23", "#");
  colorFade1 = colorHextToInt(messageFade1);
  color1 = messageFade1;

  messageFade2.replace("%23", "#");
  colorFade2 = colorHextToInt(messageFade2);
  color2 = messageFade2;
  
  handleRoot();
}

void handleVoice(String message){
  String voiceColor;
  message.toLowerCase();
  int n = message.toInt();

  //random command
  if(message.equals("random")){
    randomEnabled = true;
    opMode = rand()%numOpModes+1;
    handleRoot();
    return;
  }
  else if(message.equals("normale")){
    randomEnabled = false;
    handleRoot();
    return;
  }

  //brightness
  else if(message.indexOf("alta") != -1){
    handleBrightness("255");
    return;
  }
  else if(message.indexOf("media") != -1){
    handleBrightness("100");
    return;
  }
  else if(message.indexOf("bassa") != -1){
    handleBrightness("20");
    return;
  }

  //color
  else if(message.indexOf("ross") != -1)
    voiceColor = "#ff0000";
  else if(message.indexOf("blu") != -1)
    voiceColor = "#0000ff";
  else if(message.indexOf("verd") != -1)
    voiceColor = "#00ff00";
  else if(message.indexOf("giall") != -1)
    voiceColor = "#ffff00";
  else if(message.indexOf("arancio") != -1)
    voiceColor = "#ff7514";
  else if(message.indexOf("rosa") != -1)
    voiceColor = "#ffd1dc";
  else if(message.indexOf("viola") != -1)
    voiceColor = "#8f00ff";
  else if(message.indexOf("azzurr") != -1)
    voiceColor = "#007fff";

  //mode
  else if(n>0 && n<=numOpModes){
    opMode = n;
    handleRoot();
    return;
  }
  else if(n==0){
    if(message.indexOf("acc") != -1)
      opMode=41;
    else if(message.indexOf("spe") != -1)
      opMode=42;
    handleRoot();
    return;
  }
  
  //null command
  else{
    handleRoot();
    return;
  }

  colorCustom = colorHextToInt(voiceColor);
  color3 = voiceColor;
  opMode = 44;
  Serial.println(voiceColor);
  handleRoot();
}

void handleRandom(String message){
  if(message.indexOf("true") != -1){
    randomEnabled = true;
    opMode = rand()%numOpModes+1;
  }
  else {
    randomEnabled = false;
  }  
  handleRoot();
}

void handleBrightness(String messageBrightness) {
  int brightness = messageBrightness.toInt();
  if(brightness<=80)
    text_now = "white";
  else
    text_now = "black";
  String tmp = String(brightness, HEX);
  brightness_now = "#"+tmp+tmp+tmp;
  brightness_now_int = brightness;
  opMode=100;

  handleRoot();
}

void handleWifi(String ssid, String pass){
  ssid.replace("%20", " ");
  if(ssid.equals(ssid_now)){
    handleRoot();
    return;
  }
  else{
    server.send ( 202, "text/plain", "connecting...");
  }
  
  if(client.connected()) {
    Serial.println("client disconnected");
    client.println(AES_encrypt("#"));
  }
  client.stop();
  bool res = connectToWifi(ssid, pass);
  Serial.println(res);
}

String getContentType(String filename) { // convert the file extension to the MIME type
  if (filename.endsWith(".html")) return "text/html";
  else if (filename.endsWith(".css")) return "text/css";
  else if (filename.endsWith(".js")) return "application/javascript";
  else if (filename.endsWith(".ico")) return "image/x-icon";
  else if (filename.endsWith(".png")) return "image/png";
  return "text/plain";
}

bool handleFileRead(String path) { // send the right file to the client (if it exists)
  Serial.println("handleFileRead: " + path);
  if (path.endsWith("/")) path += "index.html";         // If a folder is requested, send the index file
  String contentType = getContentType(path);            // Get the MIME type
  if (SPIFFS.exists(path)) {                            // If the file exists
    File file = SPIFFS.open(path, "r");                 // Open it
    size_t sent = server.streamFile(file, contentType); // And send it to the client
    file.close();                                       // Then close the file again
    return true;
  }
  Serial.println("\tFile Not Found");
  return false;                                         // If the file doesn't exist, return false
}

void handleMessageFromServer(String received){
  String messages[2];
  if(received.indexOf("colorMode") != -1) {
    opMode = received.substring(received.lastIndexOf("=")+1).toInt();
    received = received.substring(0, received.lastIndexOf("&"));
  }
  if(received.indexOf("&") != -1) {
    messages[0] = received.substring(received.indexOf("=")+1, received.indexOf("&"));
    messages[1] = received.substring(received.lastIndexOf("=")+1);
  }
  else
    messages[0] = received.substring(received.indexOf("=")+1);

  Serial.println(messages[0] + messages[1]);
  
  if(received.indexOf("mode") != -1){
    Serial.println("--mode: " + messages[0]);
    handleMode(messages[0]);
  }
  else if(received.indexOf("set_color_hash") != -1){
    if(received.indexOf("&") != -1)
      handleColor(messages[0], messages[1]);
    else
      handleColor(messages[0]);
  }
  else if(received.indexOf("set_brightness_hash") != -1)
    handleBrightness(messages[0]);
  else if(received.indexOf("voice") != -1)
    handleVoice(messages[0]);
  else if(received.indexOf("random") != -1)
    handleRandom(messages[0]);
}

void setup()
{
  Serial.begin(115200);
  pinMode(READ_PIN, INPUT);
  pinMode(BUTTON_PIN, INPUT );
  pinMode(LED_PIN, OUTPUT);
  WiFi.mode(WIFI_AP_STA);
  //WiFi.setAutoConnect(false);
  //WiFi.setAutoReconnect(false);

  digitalWrite(LED_PIN, HIGH);
  Serial.println();
  Serial.println();
  Serial.println();
  Serial.println("  ██╗░░░░░███████╗██████╗░██╗░░░░░░█████╗░███╗░░░███╗██████╗░░██████╗  ");
  Serial.println("  ██║░░░░░██╔════╝██╔══██╗██║░░░░░██╔══██╗████╗░████║██╔══██╗██╔════╝  ");
  Serial.println("  ██║░░░░░█████╗░░██║░░██║██║░░░░░███████║██╔████╔██║██████╔╝╚█████╗░  ");
  Serial.println("  ██║░░░░░██╔══╝░░██║░░██║██║░░░░░██╔══██║██║╚██╔╝██║██╔═══╝░░╚═══██╗  ");
  Serial.println("  ███████╗███████╗██████╔╝███████╗██║░░██║██║░╚═╝░██║██║░░░░░██████╔╝  ");
  Serial.println("  ╚══════╝╚══════╝╚═════╝░╚══════╝╚═╝░░╚═╝╚═╝░░░░░╚═╝╚═╝░░░░░╚═════╝░  ");
  Serial.println();
  Serial.println();
  Serial.println();
  Serial.println();

  if(!SPIFFS.begin())
    Serial.println("An Error has occurred while mounting SPIFFS");

  String jdat=readDataFromFile("/JSONdata.js");
  if(jdat!=""){
    String json = jdat.substring(jdat.indexOf("'")+1, jdat.lastIndexOf("'"));
    Serial.println("data: "+json);
    parseJsonData(json);
  }

  loadWifiSettings();
    
  checkAllLamps();
  Serial.println("ok1");

  /* WiFi Part */
  Serial.print("Setting soft-AP ... ");
  WiFi.softAP(AP_SSID, AP_PASS);
  Serial.print("Soft-AP IP address = ");
  Serial.println(WiFi.softAPIP());

  
  
  UDP.begin(7171); 
  //server.on("/", handleRoot);
  server.on("/mode", []() {
    handleMode(server.arg(0));
  });
  server.on("/set_color_hash", []() {
    if(server.args()==2)
      handleColor(server.arg(0), server.arg(1));
    else
      handleColor(server.arg(0));
  });
  server.on("/set_brightness_hash", []() {
    handleBrightness(server.arg(0));
  });
  server.on("/voice", []() {
    handleVoice(server.arg(0));
  });
  server.on("/random", []() {
    handleRandom(server.arg(0));
  });
  server.on("/wifi", []() {
    handleWifi(server.arg(0), server.arg(1));
  });
  server.on("/sync", sync);
  server.on("/connect",  []() {
    server.send ( 202, "text/plain", "connecting...");
    connectToServer(server.arg(0));
  });

  server.onNotFound([]() {                              // If the client requests any URI
    if (!handleFileRead(server.uri()))                  // send it if it exists
      server.send(404, "text/plain", "404: Not Found"); // otherwise, respond with a 404 (Not Found) error
  });

  server.begin();
  Serial.println("HTTP server started");
  
  resetHeartBeats();
  Serial.println("ok2");
  
  //waitForConnections();
  Serial.println("ok3");
  
  digitalWrite(LED_PIN, LOW);
  delay(500);
  digitalWrite(LED_PIN, HIGH);

  connectToWifi(ssid_now, pass_now);

  lastChecked = millis();
  lastRandom = millis();
  lastSync = millis();
  buttonChecked = 0;
}

void loop()
{
  server.handleClient();
  if(WiFi.status() != WL_CONNECTED && error_signal_flag == false){
    error_signal_flag = true;
    Serial.println("disconnected!");
    digitalWrite(LED_PIN, LOW);
    delay(100);
    digitalWrite(LED_PIN, HIGH);
    delay(100);
    digitalWrite(LED_PIN, LOW);
    delay(100);
    digitalWrite(LED_PIN, HIGH);
    delay(100);
    digitalWrite(LED_PIN, LOW);
    delay(100);
    digitalWrite(LED_PIN, HIGH);
    ssid_now = "";
    pass_now = "";
    WiFi.setAutoReconnect(false);
    WiFi.setAutoConnect(false);
  }

  
  if(client.connected()){
    if(millis() - lastSync > 7000){
      client.println(sha1key_enc);
      lastSync = millis();
    }
    String received="";
    while(client.available()) {
      received += (char)client.read();
    }
    if(!received.equals("")){
      received = AES_decrypt(received);
      Serial.println("rec: "+received);
      handleMessageFromServer(received);
    }
  }

  uint32_t analogRaw;
  buttonCheck();
  if (millis() - lastChecked > checkDelay) {
    if (!checkHeartBeats()) {
      //waitForConnections();
    }
    if((millis() - lastChecked > 8000) && WiFi.status() == WL_CONNECTED) {
      connectToServer(HOST);
    }
      
    lastChecked = millis();
  }
  if (randomEnabled == true && millis() - lastRandom > 10000) {
    opMode = rand()%numOpModes+1;
    Serial.printf("Setting opmode %d \n", opMode);
    lastRandom = millis();
    updateData();
  }

  switch (opMode) {
    case 100:
      server.handleClient();
      sendLedData(brightness_now_int, opMode, 0);
      opMode = lastMode;
      updateData();
      delay(10);
      break;
    case 1:
      analogRaw = analogRead(READ_PIN);
      //Serial.println(analogRaw);
      /*if (analogRaw <= 3)
        break;*/
      sendLedData(analogRaw, opMode, 0);
      break;
    case 2:
      sendLedData(0, opMode, 0);
      delay(10);
      break;
    case 3:
      sendLedData(0, opMode, 0);
      delay(10);
      break;
    case 4:
      server.handleClient();
      sendLedData(0, opMode, 0);
      delay(10);
      break;
    case 5:
      server.handleClient();
      sendLedData(0, opMode, 0);
      delay(10);
      break;
    case 6:
      server.handleClient();
      sendLedData(0, opMode, 0);
      delay(10);
      break;
    case 7:
      server.handleClient();
      sendLedData(0, opMode, 0);
      delay(10);
      break;
    case 8:
      server.handleClient();
      sendLedData(0, opMode, 0);
      delay(10);
      break;
    case 9:
      server.handleClient();
      sendLedData(0, opMode, 0);
      delay(10);
      break;
    case 10:
      server.handleClient();
      sendLedData(0, opMode, 0);
      delay(10);
      break;
    case 11:
      server.handleClient();
      sendLedData(0, opMode, 0);
      delay(10);
      break;
    case 12:
      server.handleClient();
      analogRaw = analogRead(READ_PIN);
      sendLedData(analogRaw, opMode, 0);
      delay(10);
      break;
    case 13:
      server.handleClient();
      sendLedData(0, opMode, 0);
      delay(10);
      break;
    case 14:
      server.handleClient();
      sendLedData(0, opMode, 0);
      delay(10);
      break;
    case 15:
      server.handleClient();
      analogRaw = analogRead(READ_PIN);
      sendLedData(analogRaw, opMode, 0);
      delay(10);
      break;
    case 16:
      server.handleClient();
      analogRaw = analogRead(READ_PIN);
      sendLedData(analogRaw, opMode, 0);
      delay(10);
      break;
    case 41:
      server.handleClient();
      sendLedData(0, opMode, 0);
      delay(10);
      break;
    case 42:
      server.handleClient();
      sendLedData(0, opMode, 0);
      delay(10);
      break;
    case 43:
      server.handleClient();
      sendLedData(subOption, opMode, 0);
      delay(10);
      break;
    case 44:
      server.handleClient();
      sendLedData(colorCustom, opMode, 0);
      delay(10);
      break;
    case 45:
      server.handleClient();
      sendLedData(colorCustom, opMode, 0);
      delay(10);
      break;
    case 46:
      server.handleClient();
      sendLedData(colorFade1, opMode, colorFade2);
      delay(10);
      break;
    case 47:
      server.handleClient();
      sendLedData(colorFade1, opMode, colorFade2);
      delay(10);
      break;
    case 48:
      server.handleClient();
      sendLedData(colorFade1, opMode, colorFade2);
      delay(10);
      break;
    case 49:
      server.handleClient();
      sendLedData(colorFade1, opMode, colorFade2);
      delay(10);
      break;
  }

  lastMode = opMode;
  delay(5);
}

void handshake(){
    Serial.println("---------------------------HANDSHAKE----------------------------");
    memcpy(my_iv, default_iv, 16);
    client.println(AES_encrypt(sha1key));

    while(!client.available()) {
      yield();
    }
  
    String received="";
    while(client.available()) {
      received += (char)client.read();
    }
    if(!received.equals("")){
      Serial.println("rec: "+received);
      handleIV(received);

      sha1key_enc = AES_encrypt(sha1key);
      client.println(sha1key_enc);
    }
    Serial.println("---------------------------HANDSHAKE----------------------------");
}

void handleIV(String enc){
    Serial.println("----SET IV----");
    String dec = AES_decrypt(enc);

    byte plainiv[17];
    base64_decode((char *)plainiv, (char *)dec.c_str(), dec.length());
    Serial.print("plainiv: [");
    for(int i=0; i<16; i++){
      Serial.print((int)plainiv[i]);
      if(i!=15) Serial.print(", ");
    }
    Serial.println("]");

    memcpy(my_iv, plainiv, 16);
    Serial.println("----IV SET----");
}

void connectToServer(String new_host){
  if(!client.connected()){
    HOST = new_host;
    Serial.print("connecting to ");
    Serial.print(HOST);
    Serial.print(':');
    Serial.println(PORT);
  
    if (client.connect(HOST, PORT)){
      Serial.print("connected to the server ");
      Serial.println(HOST);
      handshake();
    }
    else
      Serial.println("connection failed");

    updateData();
  }
  else{
    client.println(sha1key_enc);
  }
}

bool connectToWifi(String ssid, String pass){
  WiFi.disconnect(true);
  WiFi.setAutoReconnect(true);
  WiFi.setAutoConnect(true);
  
  digitalWrite(LED_PIN, LOW);
  delay(500);
  digitalWrite(LED_PIN, HIGH);
  
  // Begin WiFi
  WiFi.begin(ssid, pass);
 
  // Connecting to WiFi...
  Serial.print("Connecting to ");
  Serial.print(ssid+"("+pass+")");

  int i=0;
  while (WiFi.status() != WL_CONNECTED && i<17)
  {
    delay(1000);
    i++;
    Serial.print(".");
  }
  if(WiFi.status() == WL_CONNECTED){
    error_signal_flag = false;
    Serial.println("Connected!");
    Serial.print("IP address for network ");
    Serial.print(ssid);
    Serial.print(" : ");
    Serial.println(WiFi.localIP());
    digitalWrite(LED_PIN, LOW);
    delay(300);
    digitalWrite(LED_PIN, HIGH);
    delay(300);
    digitalWrite(LED_PIN, LOW);
    delay(300);
    digitalWrite(LED_PIN, HIGH);

    ssid_now = ssid;
    pass_now = pass;
    saveWifiSettings();

    connectToServer(HOST);
    
    return true;
  }
  else{
    Serial.print("Error, can not connect to ");
    Serial.println(ssid);
    digitalWrite(LED_PIN, LOW);
    delay(100);
    digitalWrite(LED_PIN, HIGH);
    delay(100);
    digitalWrite(LED_PIN, LOW);
    delay(100);
    digitalWrite(LED_PIN, HIGH);
    delay(100);
    digitalWrite(LED_PIN, LOW);
    delay(100);
    digitalWrite(LED_PIN, HIGH);

    ssid_now = "";
    pass_now = "";
    error_signal_flag = true;
    //WiFi.disconnect(true);
    WiFi.setAutoReconnect(false);
    WiFi.setAutoConnect(false);
    
    return false;
  }
}

void sendLedData(uint32_t data, uint8_t op_mode, uint32_t data2) 
{
 struct led_command send_data;
 send_data.opmode = op_mode; 
 send_data.data = data; 
 send_data.data2 = data2;
 for (int i = 0; i < number_of_clients; i++) 
 {
    IPAddress ip(192,168,4,2 + i);
    UDP.beginPacket(ip, 7001); 
    UDP.write((char*)&send_data,sizeof(struct led_command));
    UDP.endPacket();
 }
}

void waitForConnections() {
  while(true) {
      readHeartBeat();
      if (checkHeartBeats()) {
        return;
      }
      delay(checkDelay);
      resetHeartBeats();
  }
}

void resetHeartBeats() {
  for (int i = 0; i < number_of_clients; i++) {
   heartbeats[i] = false;
  }
}

void readHeartBeat() {
  struct heartbeat_message hbm;
 while(true) {
  int packetSize = UDP.parsePacket();
  if (!packetSize) {
    break;
  }
  UDP.read((char *)&hbm, sizeof(struct heartbeat_message));
  if (hbm.client_id > number_of_clients) {
    Serial.println("Error: invalid client_id received");
    continue;
  }
  heartbeats[hbm.client_id - 1] = true;
 }
}

bool checkHeartBeats() {
  for (int i = 0; i < number_of_clients; i++) {
   if (!heartbeats[i]) {
    return false;
   }
  }
  resetHeartBeats();
  return true;
}


void buttonCheck()
{
  int but = digitalRead(BUTTON_PIN);
  //Serial.println(but);
  if (but == 0) {
    if (millis() - buttonChecked < buttonDoubleTapDelay && buttonClicked == false ) {
      doubleClicked();
      doubleTapped = true;
    }
    clickTrigger = true;
    buttonClicked = true; 
    buttonChecked = millis();
  }

  else if (but == 1) {
    if (millis() - buttonChecked > buttonDoubleTapDelay && clickTrigger) {
      if (!doubleTapped) {
        clicked();
      }
      clickTrigger = false;
      doubleTapped = false;
    }
    buttonClicked = false;
  }
}

void clicked() {
  if(semaphore) return;
  
  if (opMode >= numOpModes && opMode!=41 && opMode!=42)
    opMode = 41;
  else {
    opMode++;
    if(opMode == 43)
      opMode = 1;
  }
  Serial.printf("Setting opmode %d \n", opMode);
  
  handleRoot();
}

void doubleClicked() {
  if(!semaphore){
    if(opMode==42)
      opMode = 41;
    else
      opMode = 42;
    handleRoot();
    return;
  }
  allLamps = !allLamps;
  if(!allLamps)
    number_of_clients = 1;
  else
    number_of_clients = 2;
  Serial.println(number_of_clients);
}

void checkAllLamps(){
  semaphore=true;
  for(int i=0; i<5000; i++){
    buttonCheck();
    delay(1);
  }
  semaphore=false;
}
