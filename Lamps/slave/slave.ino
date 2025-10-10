// Della Matera Lorenzo 5E

#define FASTLED_INTERRUPT_RETRY_COUNT 0
#define FASTLED_ALLOW_INTERRUPTS 0
#include <FastLED.h>
#include <ESP8266WiFi.h>
#include <WiFiUDP.h>
#include "reactive_common.h"

#define LED_PIN D2
#define NUM_LEDS 144

#define MIC_LOW 0
#define MIC_HIGH 644

#define SAMPLE_SIZE 20
#define LONG_TERM_SAMPLES 250
#define BUFFER_DEVIATION 400
#define BUFFER_SIZE 3

#define LAMP_ID 1
WiFiUDP UDP;

const char *ssid = "LedLamps"; // The SSID (name) of the Wi-Fi network you want to connect to
const char *password = "123456789";  // The password of the Wi-Fi network

CRGB leds[NUM_LEDS];

struct averageCounter *samples;
struct averageCounter *longTermSamples;
struct averageCounter* sanityBuffer;

float globalHue;
int globalBrightness = 255;
int hueOffset = 120;
float fadeScale = 1.3;
float hueIncrement = 0.7;
int rainbow[] = {255000000, 255127000, 255255000, 255000, 255, 75000130, 148000211};

struct led_command {
  uint8_t opmode;
  uint32_t data;
  uint32_t data2;
};

unsigned long lastReceived = 0;
unsigned long lastHeartBeatSent;
const int heartBeatInterval = 100;

bool isconnected = true;
bool customfade = false;
bool firstTwinkle = true;
bool firstWipe = true;
bool chillfade = false;
bool newColor = true; int lastColor = 0;
bool isOn = true;

struct led_command cmd;
void connectToWifi();

void setup()
{
  globalHue = 0;
  samples = new averageCounter(SAMPLE_SIZE);
  longTermSamples = new averageCounter(LONG_TERM_SAMPLES);
  sanityBuffer    = new averageCounter(BUFFER_SIZE);

  while (sanityBuffer->setSample(250) == true) {}
  while (longTermSamples->setSample(200) == true) {}

  FastLED.addLeds<NEOPIXEL, LED_PIN>(leds, NUM_LEDS);

  Serial.begin(115200); // Start the Serial communication to send messages to the computer
  delay(10);
  Serial.println();
  Serial.println();
  Serial.println();
  Serial.println("  ██╗░░░░░███████╗██████╗░██╗░░░░░░█████╗░███╗░░░███╗██████╗░░██████╗  ");
  Serial.println("  ██║░░░░░██╔════╝██╔══██╗██║░░░░░██╔══██╗████╗░████║██╔══██╗██╔════╝  ");
  Serial.println("  ██║░░░░░█████╗░░██║░░██║██║░░░░░███████║██╔████╔██║██████╔╝╚█████╗░  ");
  Serial.println("  ██║░░░░░██╔══╝░░██║░░██║██║░░░░░██╔══██║██║╚██╔╝██║██╔═══╝░░╚═══██╗  ");
  Serial.println("  ███████╗███████╗██████╔╝███████╗██║░░██║██║░╚═╝░██║██║░░░░░██████╔╝  ");
  Serial.println("  ╚══════╝╚══════╝╚═════╝░╚══════╝╚═╝░░╚═╝╚═╝░░░░░╚═╝╚═╝░░░░░╚═════╝░  ");
  //Serial.println("  ▒█░░░ █▀▀ █▀▀▄ ▒█░░░ █▀▀█ █▀▄▀█ █▀▀█ █▀▀ ");
  //Serial.println("  ▒█░░░ █▀▀ █░░█ ▒█░░░ █▄▄█ █░▀░█ █░░█ ▀▀█ ");
  //Serial.println("  ▒█▄▄█ ▀▀▀ ▀▀▀░ ▒█▄▄█ ▀░░▀ ▀░░░▀ █▀▀▀ ▀▀▀ ");
  Serial.println();
  Serial.println();
  Serial.println();
  Serial.println();

  WiFi.begin(ssid, password); // Connect to the network
  Serial.print("Connecting to ");
  Serial.print(ssid);
  Serial.println(" ...");

  connectToWifi();
  sendHeartBeat();
  UDP.begin(7001);
}

void sendHeartBeat() {
  struct heartbeat_message hbm;
  hbm.client_id = LAMP_ID;
  hbm.chk = 77777;
  Serial.println("Sending heartbeat");
  IPAddress ip(192, 168, 4, 1);
  UDP.beginPacket(ip, 7171);
  int ret = UDP.write((char*)&hbm, sizeof(hbm));
  printf("Returned: %d, also sizeof hbm: %d \n", ret, sizeof(hbm));
  UDP.endPacket();
  lastHeartBeatSent = millis();
}

void loop()
{
  if (!isconnected) {
    chillFade();
    return;
  }

  if (millis() - lastHeartBeatSent > heartBeatInterval) {
    sendHeartBeat();
  }



  int packetSize = UDP.parsePacket();
  if (packetSize)
  {
    UDP.read((char *)&cmd, sizeof(struct led_command));
    lastReceived = millis();
  }

  if (millis() - lastReceived >= 5000)
  {
    connectToWifi();
  }

  int opMode = cmd.opmode;
  int data1 = cmd.data;
  int data2 = cmd.data2;

  if (opMode == 1)
    FastLED.setBrightness(255);
  else
    FastLED.setBrightness(globalBrightness);

  if (opMode != 2)
    chillfade = false;
  if (opMode != 45)
    customfade = false;
  if (opMode != 42 && opMode!=11)
    isOn = true;
  if (opMode != 13)
    firstTwinkle = true; 
  if (opMode != 3)
    firstWipe = true; 

  switch (opMode) {
    case 1:
      soundReactive(data1);
      break;

    case 2:
      chillFade();
      break;

    case 3:
      colorWipe(firstWipe);
      firstWipe = false;
      break;

    case 4:
      colorFlash();
      break;

    case 5:
      rainbowCycle();
      break;
      
    case 6:
      rainbowFade();
      break;

    case 7:
      rainbowChase();
      break;

    case 8:
      strobe(40);
      break;

    case 9:
      fire(55, 120);
      break;

    case 10:
      bouncingBalls();
      break;

    case 11:
      fillRandom();
      break;

    case 12:
      clapColor(data1);
      break;

    case 13:
      twinkle(firstTwinkle);
      firstTwinkle = false;
      break;

    case 14:
      sparkle();
      break;

    case 15:
      strobeMusicShot(data1);
      break;

    case 16:
      strobeMusicFade(data1);
      break;

    case 41:
      allWhite();
      break;

    case 42:
      off(isOn);
      isOn = false;
      break;

    case 43:
      switch (data1) {
        case 1:
          isOn = true;
          red();
          break;
        case 2:
          isOn = true;
          green();
          break;
        case 3:
          isOn = true;
          blue();
          break;
      }
      break;

    case 44:
      setColor(data1);
      break;
      
    case 45:
      customFade(data1);
      break;

    case 46:
      fadeColors(data1, data2);
      break;

    case 47:
      doubleColor(data1, data2);
      break;

    case 48:
      swappingColor(data1, data2, false);
      break;

    case 49:
      swappingColor(data1, data2, true);
      break;

    case 100:
      globalBrightness = data1;
      break;
  }
}

int off(bool isOn) {
  static uint8_t counterOff = 0;
  if (isOn)
    counterOff = 0;
  uint8_t brightness = counterOff * 10;
  for (int i = 0; i < NUM_LEDS; i++) {
    leds[i] = CRGB(250 - brightness, 250 - brightness, 250 - brightness);
  }
  delay(5);
  FastLED.show();

  if (counterOff < 25)
    counterOff++;

  return counterOff;
}
void red() {
  static uint16_t counterRed = 0;
  static bool reverseRed = false;
  for (int i = 0; i < NUM_LEDS; i++) {
    leds[i] = CRGB(counterRed, 0, 0);
  }
  delay(5);
  FastLED.show();
  if (!reverseRed)
    counterRed++;
  else
    counterRed--;
  if (counterRed%255 == 0)
    reverseRed = !reverseRed;
}
void blue() {
  static uint16_t counterBlue = 0;
  static bool reverseBlue = false;
  for (int i = 0; i < NUM_LEDS; i++) {
    leds[i] = CRGB(0, 0, counterBlue);
  }
  delay(5);
  FastLED.show();
  if (!reverseBlue)
    counterBlue++;
  else
    counterBlue--;
  if (counterBlue%255 == 0)
    reverseBlue = !reverseBlue;
}
void green() {
  static uint16_t counterGreen = 0;
  static bool reverseGreen = false;
  for (int i = 0; i < NUM_LEDS; i++) {
    leds[i] = CRGB(0, counterGreen, 0);
  }
  delay(5);
  FastLED.show();
  if (!reverseGreen)
    counterGreen++;
  else
    counterGreen--;
  if (counterGreen%255 == 0)
    reverseGreen = !reverseGreen;
}

void colorFlash(){
  static uint16_t stepsFlash = 0;
  static bool reverseFlash = false;
  static uint8_t rgb = 0;

  for (uint8_t i=0; i < NUM_LEDS; i++){
    switch(rgb){
      case 0:
        leds[i] = CRGB(stepsFlash, 0, 0);
        break;
      case 1:
        leds[i] = CRGB(0, stepsFlash, 0);
        break;
      case 2:
        leds[i] = CRGB(0, 0, stepsFlash);
        break;
    }
  }
  delay(5);
  FastLED.show();

  if (!reverseFlash)
    stepsFlash+=5;
  else
    stepsFlash-=5;
  if(stepsFlash%255 == 0)
    reverseFlash = !reverseFlash;
  if(stepsFlash == 0)
    rgb = (rgb+1)%3;
}

void colorWipe(bool firstTime){
  static uint8_t waitWipe = 0;
  static uint16_t stepsWipe = 0;

  if(firstTime){
    for (uint8_t i=0; i < NUM_LEDS; i++)
      leds[i] = CRGB(0,0,0);
    stepsWipe = 0;
  }

  if(stepsWipe < NUM_LEDS)
    leds[stepsWipe % NUM_LEDS] = CRGB(255, 0 , 0);
  else if(stepsWipe < NUM_LEDS*2)
    leds[stepsWipe % NUM_LEDS] = CRGB(0, 255 , 0);
  else
    leds[stepsWipe % NUM_LEDS] = CRGB(0, 0 , 255);

  delay(5);
  FastLED.show();

  if(waitWipe%5 == 0)
    stepsWipe = (stepsWipe+1) % (NUM_LEDS*3);
  waitWipe = (waitWipe+1)%5;
}

void rainbowFade() {
  static byte *c;
  uint16_t i;
  static uint16_t j = 0;
  static uint8_t waitFade = 0;

  for (i = 0; i < NUM_LEDS; i++) {
    c = Wheel(j & 255);
    leds[i] = CRGB(*c, *(c + 1), *(c + 2));
  }
  delay(5);
  FastLED.show();
  if(waitFade%7 == 0){
    j++;
  }
  waitFade = (waitFade+1)%7;
  if (j >= 256 * 2)
    j = 0;
}
void rainbowCycle() {
  static byte *c;
  uint16_t i;
  static uint16_t j = 0;

  for (i = 0; i < NUM_LEDS; i++) {
    c = Wheel(((i * 256 / NUM_LEDS) + j / 2) & 255);
    leds[i] = CRGB(*c, *(c + 1), *(c + 2));
  }
  delay(5);
  FastLED.show();
  j++;
  if (j >= 256 * 2)
    j = 0;
}
void rainbowChase() {
  static byte *c;
  static uint16_t j = 0;
  static uint8_t q = 0;
  static uint8_t waitChase = 0;
  
  for (uint16_t i=0; i < NUM_LEDS; i=i+3) {
    c = Wheel((i+j) % 255);
    leds[i+q] = CRGB(*c, *(c + 1), *(c + 2));
  }

  delay(5);
  FastLED.show();
  
  for (uint16_t i=0; i < NUM_LEDS; i=i+3) {
    leds[i+q] = CRGB(0,0,0);        //turn every third pixel off
  }

  if(waitChase%10 == 0){
    q++;
  }
  waitChase = (waitChase+1)%10;
  if (q >= 3){
    q = 0;
    j++;
  }
  if(j >= 256){
    j=0;
  }
}
byte * Wheel(byte WheelPos) {
  static byte c[3];

  if (WheelPos < 85) {
    c[0] = WheelPos * 3;
    c[1] = 255 - WheelPos * 3;
    c[2] = 0;
  } else if (WheelPos < 170) {
    WheelPos -= 85;
    c[0] = 255 - WheelPos * 3;
    c[1] = 0;
    c[2] = WheelPos * 3;
  } else {
    WheelPos -= 170;
    c[0] = 0;
    c[1] = WheelPos * 3;
    c[2] = 255 - WheelPos * 3;
  }

  return c;
}

void strobe(int FlashDelay) {
  int rainbowCol = 0;
  int steps = FlashDelay / 5;
  static uint16_t count = 0;
  if (count < steps) {
    rainbowCol = rainbow[0];
  }
  else if (count >= steps * 2 && count < steps * 3) {
    rainbowCol = rainbow[1];
  }
  else if (count >= steps * 4 && count < steps * 5) {
    rainbowCol = rainbow[2];
  }
  else if (count >= steps * 6 && count < steps * 7) {
    rainbowCol = rainbow[3];
  }
  else if (count >= steps * 8 && count < steps * 9) {
    rainbowCol = rainbow[4];
  }
  else if (count >= steps * 10 && count < steps * 11) {
    rainbowCol = rainbow[5];
  }
  else if (count >= steps * 12 && count < steps * 13) {
    rainbowCol = rainbow[6];
  }
  else {
    rainbowCol = 0;
  }
  for (int i = 0; i < NUM_LEDS; i++) {
    //leds[i] = CRGB((int)red/steps*(count%steps+1), (int)green/steps*(count%steps+1), (int)blue/10*(count%steps+1));
    leds[i] = CRGB((rainbowCol / 1000000) % 1000, (rainbowCol / 1000) % 1000, rainbowCol % 1000);
  }
  delay(5);
  FastLED.show();

  count++;
  if (count == steps * 14) {
    count = 0;
    printf("%d", steps);
  }

}

bool colorShot(int color) {
  static int counterShot = 0;
  static int myColor[3] = {0, 0, 0};
  static int colorShot = 0;
  static double s1Shot = 0, s2Shot = 0, s3Shot = 0, tmp1Shot = 0, tmp2Shot = 0, tmp3Shot = 0;
  static bool reverseShot = false;

  if (color != colorShot) {
    colorShot = color;
    myColor[2] = colorShot % 1000;
    myColor[1] = (colorShot / 1000) % 1000;
    myColor[0] = (colorShot / 1000000) % 1000;
    counterShot = 0;
    for (int i = 0; i < NUM_LEDS; i++) {
      leds[i] = CRGB(0, 0, 0);
    }
    s1Shot = double(myColor[0]) / 10.0;
    s2Shot = double(myColor[1]) / 10.0;
    s3Shot = double(myColor[2]) / 10.0;
    tmp1Shot = 0, tmp2Shot = 0, tmp3Shot = 0;
  }

  if (!reverseShot)
  {
    tmp1Shot += s1Shot;
    tmp2Shot += s2Shot;
    tmp3Shot += s3Shot;
  }
  else
  {
    tmp1Shot -= s1Shot;
    tmp2Shot -= s2Shot;
    tmp3Shot -= s3Shot;
  }

  for (int i = 0; i < NUM_LEDS; i++)
    leds[i] = CRGB((int)round(tmp1Shot), (int)round(tmp2Shot), (int)round(tmp3Shot));
  FastLED.show();
  //delay(5);

  counterShot++;
  if (counterShot == 10) {
    if (!reverseShot)
      tmp1Shot = myColor[0], tmp2Shot = myColor[1], tmp3Shot = myColor[2];
    else
      tmp1Shot = 0, tmp2Shot = 0, tmp3Shot = 0;
    reverseShot = !reverseShot;
    counterShot = 0;
  }
  if(counterShot == 0 && !reverseShot)
    return false;
  else
    return true;
}

void strobeMusicShot(int analogRaw){
  static bool shotStarted = false;
  static int i=0;
  if(analogRaw>200 || shotStarted){
    shotStarted = colorShot(rainbow[i]);
    if(!shotStarted)
      i = (i+1)%7;
  }
  else {
    for (int i = 0; i < NUM_LEDS; i++)
      leds[i] = CRGB(0,0,0);
    FastLED.show();
    shotStarted = false;
  }
  delay(5);
}

void strobeMusicFade(int analogRaw) {
  static int nextColor;
  static int i=0;
  static int counterStrobeFade = 0;
  static bool flagStrobeFade = true;
  nextColor = rainbow[i];
  if(nextColor!=lastColor)
    newColor=true;
  if(newColor==true && analogRaw<=200){
    colorTransition(nextColor);
    counterStrobeFade ++;
    if(counterStrobeFade == 10){
      flagStrobeFade = true;
      counterStrobeFade = 0;
    }
    return;
  }
  if(analogRaw<=200)
    i = (i+1)%7;
  if(analogRaw>200 && flagStrobeFade){
    flagStrobeFade = false;
    i = (i+1)%7;
    nextColor = rainbow[i];
    int rgb[3];
    rgb[2] = nextColor % 1000;
    rgb[1] = (nextColor / 1000) % 1000;
    rgb[0] = (nextColor / 1000000) % 1000;
    lastColor = nextColor;
    for (int i = 0; i < NUM_LEDS; i++)
      leds[i] = CRGB(rgb[0],rgb[1],rgb[2]);
    delay(5);
    FastLED.show();
  }
}

void fire(int Cooling, int Sparking) {
  static byte heat[NUM_LEDS];
  int cooldown;

  for ( int i = 0; i < NUM_LEDS; i++) {
    cooldown = random(0, ((Cooling * 10) / NUM_LEDS) + 2);

    if (cooldown > heat[i]) {
      heat[i] = 0;
    } else {
      heat[i] = heat[i] - cooldown;
    }
  }

  for ( int k = NUM_LEDS - 1; k >= 2; k--) {
    heat[k] = (heat[k - 1] + heat[k - 2] + heat[k - 2]) / 3;
  }

  if ( random(255) < Sparking ) {
    int y = random(7);
    heat[y] = heat[y] + random(160, 255);
    //heat[y] = random(160,255);
  }

  for ( int j = 0; j < NUM_LEDS; j++) {
    setPixelHeatColor(j, heat[j] );
  }

  delay(5);
  FastLED.show();
}
void setPixelHeatColor (int Pixel, byte temperature) {
  byte t192 = round((temperature / 255.0) * 191);

  byte heatramp = t192 & 0x3F; // 0..63
  heatramp <<= 2; // scale up to 0..252

  if ( t192 > 0x80) {                    // hottest
    leds[Pixel] = CRGB(255, 255, heatramp);
  } else if ( t192 > 0x40 ) {            // middle
    leds[Pixel] = CRGB(255, heatramp, 0);
  } else {                               // coolest
    leds[Pixel] = CRGB(heatramp, 0, 0);
  }
}

void setColor(int color) {
  if(color!=lastColor)
    newColor=true;
  if(newColor==true){
    colorTransition(color);
    return;
  }
  int rgb[3];
  rgb[2] = color % 1000;
  color /= 1000;
  rgb[1] = color % 1000;
  rgb[0] = color / 1000;
  for (int i = 0; i < NUM_LEDS; i++) {
    leds[i] = CRGB(rgb[0], rgb[1], rgb[2]);
  }
  delay(5);
  FastLED.show();
}
void colorTransition(int color){
  static int counterTrans=0;
  static int first[3] = {0, 0, 0};
  static int second[3]   = {0, 0, 0};
  static int secondColor = 0;
  static double dstepsTrans = 100.0;
  static double s1Trans = 0, s2Trans = 0, s3Trans = 0, tmp1Trans = 0, tmp2Trans = 0, tmp3Trans = 0;
  static bool reverseCustom = false;

  if (color != secondColor) {
    secondColor = color;
    second[2] = secondColor % 1000;
    second[1] = (secondColor / 1000) % 1000;
    second[0] = (secondColor / 1000000) % 1000;

    first[2] = lastColor % 1000;
    first[1] = (lastColor / 1000) % 1000;
    first[0] = (lastColor / 1000000) % 1000;

    lastColor = color;
    counterTrans = 0;
  }

  if (counterTrans == 0) {
    for (int i = 0; i < NUM_LEDS; i++) {
      leds[i] = CRGB(first[0], first[1], first[2]);
    }
    s1Trans = double((second[0] - first[0])) / dstepsTrans;
    s2Trans = double((second[1] - first[1])) / dstepsTrans;
    s3Trans = double((second[2] - first[2])) / dstepsTrans;
    tmp1Trans = first[0], tmp2Trans = first[1], tmp3Trans = first[2];
  }
  
  tmp1Trans += s1Trans;
  tmp2Trans += s2Trans;
  tmp3Trans += s3Trans;

  for (int i = 0; i < NUM_LEDS; i++)
    leds[i] = CRGB((int)round(tmp1Trans), (int)round(tmp2Trans), (int)round(tmp3Trans));
  FastLED.show();
  delay(5);

  counterTrans++;
  if(counterTrans == 100){
    newColor = false;
  }
}

void swappingColor(int colorA, int colorB, bool doswap) {
  static int firstColor=0, secondColor=0;
  static int one[3] = {0, 0, 0};
  static int two[3]   = {0, 0, 0};
  static int swapper = 50*LAMP_ID;
  static int limit = (50*LAMP_ID)+100;

  if(colorA != firstColor || colorB != secondColor){
    firstColor = colorA;
    one[2] = firstColor % 1000;
    one[1] = (firstColor / 1000) % 1000;
    one[0] = (firstColor / 1000000) % 1000;

    secondColor = colorB;
    two[2] = secondColor % 1000;
    two[1] = (secondColor / 1000) % 1000;
    two[0] = (secondColor / 1000000) % 1000;
  }
  
  if (swapper < 100 || swapper >= 150) {
    for (int i = 0; i < NUM_LEDS; i++) {
      leds[i] = CRGB(one[0], one[1], one[2]);
    }
  }
  else {
    for (int i = 0; i < NUM_LEDS; i++) {
      leds[i] = CRGB(two[0], two[1], two[2]);
    }
  }

  if (doswap && swapper<limit) {
    swapper++;
  }
  else {
    swapper = 50*LAMP_ID;
  }

  delay(5);
  FastLED.show();
}

void fadeColors(int colorA, int colorB){
  static int first[3] = {0, 0, 0};
  static int second[3]   = {0, 0, 0};
  static int counterFade = 0;
  static double s1Fade = 0, s2Fade = 0, s3Fade = 0, tmp1Fade = 0, tmp2Fade = 0, tmp3Fade = 0;
  static bool reverseFade = false;
  static int firstColor=0, secondColor=0;

  if(colorA != firstColor || colorB != secondColor){
    firstColor = colorA;
    first[2] = firstColor % 1000;
    first[1] = (firstColor / 1000) % 1000;
    first[0] = (firstColor / 1000000) % 1000;

    secondColor = colorB;
    second[2] = secondColor % 1000;
    second[1] = (secondColor / 1000) % 1000;
    second[0] = (secondColor / 1000000) % 1000;

    counterFade =0;
    for (int i = 0; i < NUM_LEDS; i++) {
      leds[i] = CRGB(first[0], first[1], first[2]);
    }
    s1Fade = double((second[0] - first[0])) / 500.0;
    s2Fade = double((second[1] - first[1])) / 500.0;
    s3Fade = double((second[2] - first[2])) / 500.0;
    tmp1Fade = first[0], tmp2Fade = first[1], tmp3Fade = first[2];
  }

  if (!reverseFade)
  {
    tmp1Fade += s1Fade;
    tmp2Fade += s2Fade;
    tmp3Fade += s3Fade;
  }
  else
  {
    tmp1Fade -= s1Fade;
    tmp2Fade -= s2Fade;
    tmp3Fade -= s3Fade;
  }

  for (int i = 0; i < NUM_LEDS; i++)
    leds[i] = CRGB((int)round(tmp1Fade), (int)round(tmp2Fade), (int)round(tmp3Fade));
  FastLED.show();
  delay(5);

  counterFade++;
  if (counterFade == 500) {
    if (!reverseFade)
      tmp1Fade = second[0], tmp2Fade = second[1], tmp3Fade = second[2];
    else
      tmp1Fade = first[0], tmp2Fade = first[1], tmp3Fade = first[2];
    reverseFade = !reverseFade;
    counterFade = 0;
  }
}

void doubleColor(int colorA, int colorB){
  static int first[3] = {0, 0, 0};
  static int second[3]   = {0, 0, 0};
  static int counterDouble = 0;
  static double s1Double = 0, s2Double = 0, s3Double = 0, tmp1Double = 0, tmp2Double = 0, tmp3Double = 0;
  static int firstColor=0, secondColor=0;
  
  if(colorA != firstColor || colorB != secondColor){
    firstColor = colorA;
    first[2] = firstColor % 1000;
    first[1] = (firstColor / 1000) % 1000;
    first[0] = (firstColor / 1000000) % 1000;

    secondColor = colorB;
    second[2] = secondColor % 1000;
    second[1] = (secondColor / 1000) % 1000;
    second[0] = (secondColor / 1000000) % 1000;
  }

  s1Double = double((second[0] - first[0])) / 40.0;
  s2Double = double((second[1] - first[1])) / 40.0;
  s3Double = double((second[2] - first[2])) / 40.0;
  tmp1Double = first[0], tmp2Double = first[1], tmp3Double = first[2];
  for (int i = 0; i < NUM_LEDS; i++){
    if(i<52)
      leds[i] = CRGB(first[0], first[1], first[2]);
    else if(i>=92)
      leds[i] = CRGB(second[0], second[1], second[2]);
    else{
      tmp1Double += s1Double;
      tmp2Double += s2Double;
      tmp3Double += s3Double;
      leds[i] = CRGB((int)round(tmp1Double), (int)round(tmp2Double), (int)round(tmp3Double));
    }
  }
  FastLED.show();
  delay(5);
}
  
void customFade(int color) {
  static int counterCustom = 0;
  static int first[3] = {0, 0, 0};
  static int second[3]   = {0, 0, 0};
  static int firstColor = 0;
  static double dstepsCustom = 255.0;
  static double s1Custom = 0, s2Custom = 0, s3Custom = 0, tmp1Custom = 0, tmp2Custom = 0, tmp3Custom = 0;
  static bool reverseCustom = false;

  if (chillfade)
    return;

  if (color != firstColor) {
    firstColor = color;
    first[2] = firstColor % 1000;
    first[1] = (firstColor / 1000) % 1000;
    first[0] = (firstColor / 1000000) % 1000;
   customfade = false;
  }

  if (customfade == false) {
    counterCustom = 0;
    for (int i = 0; i < NUM_LEDS; i++) {
      leds[i] = CRGB(first[0], first[1], first[2]);
    }
    s1Custom = double((second[0] - first[0])) / dstepsCustom;
    s2Custom = double((second[1] - first[1])) / dstepsCustom;
    s3Custom = double((second[2] - first[2])) / dstepsCustom;
    tmp1Custom = first[0], tmp2Custom = first[1], tmp3Custom = first[2];
   customfade = true;
  }

  if (!reverseCustom)
  {
    tmp1Custom += s1Custom;
    tmp2Custom += s2Custom;
    tmp3Custom += s3Custom;
  }
  else
  {
    tmp1Custom -= s1Custom;
    tmp2Custom -= s2Custom;
    tmp3Custom -= s3Custom;
  }

  for (int i = 0; i < NUM_LEDS; i++)
    leds[i] = CRGB((int)round(tmp1Custom), (int)round(tmp2Custom), (int)round(tmp3Custom));
  FastLED.show();
  delay(5);

  counterCustom++;
  if (counterCustom == (int)dstepsCustom) {
    if (!reverseCustom)
      tmp1Custom = second[0], tmp2Custom = second[1], tmp3Custom = second[2];
    else
      tmp1Custom = first[0], tmp2Custom = first[1], tmp3Custom = first[2];
    reverseCustom = !reverseCustom;
    counterCustom = 0;
  }
}

void clapColor(int analogRaw){
  static int red[7] = {255, 255, 255, 0, 0, 75, 148};
  static int green[7] = {0, 127, 255, 255, 0, 0, 0};
  static int blue[7] = {0, 0, 0, 0, 255, 130, 211};
  static int n=0;
  if(analogRaw >= 439)
    n = (n+1)%7;
  for (int i = 0; i < NUM_LEDS; i++) {
    leds[i] = CRGB(red[n], green[n], blue[n]);
  }
  delay(5);
  FastLED.show();
}

void bouncingBalls(){
  static byte colors[4][3] = { {0xff, 0,0},       // red
                        {0, 0xff, 0},      // green
                        {0, 0, 0xff},      // blue
                        {0xff, 0xff, 0} }; // yellow
  static int BallCount = 4;
  static float Gravity = -9.81;
  static int StartHeight = 1;
 
  static float Height[] = {StartHeight, StartHeight, StartHeight, StartHeight};
  static float ImpactVelocityStart = sqrt( -2 * Gravity * StartHeight );
  static float ImpactVelocity[] = {ImpactVelocityStart, ImpactVelocityStart, ImpactVelocityStart, ImpactVelocityStart};
  static float TimeSinceLastBounce[] = {0,0,0,0};
  static int   Position[] = {0,0,0,0};
  static long  ClockTimeSinceLastBounce[] = {millis(), millis(), millis(), millis()};
  static float Dampening[] = {0.90 - float(0)/pow(BallCount,2), 0.90 - float(1)/pow(BallCount,2), 0.90 - float(2)/pow(BallCount,2), 0.90 - float(3)/pow(BallCount,2)};

  for (int i = 0 ; i < BallCount ; i++) {
      TimeSinceLastBounce[i] =  millis() - ClockTimeSinceLastBounce[i];
      Height[i] = 0.5 * Gravity * pow( TimeSinceLastBounce[i]/1000 , 2.0 ) + ImpactVelocity[i] * TimeSinceLastBounce[i]/1000;
 
      if ( Height[i] < 0 ) {                      
        Height[i] = 0;
        ImpactVelocity[i] = Dampening[i] * ImpactVelocity[i];
        ClockTimeSinceLastBounce[i] = millis();
 
        if ( ImpactVelocity[i] < 0.01 ) {
          ImpactVelocity[i] = ImpactVelocityStart;
        }
      }
      Position[i] = round( Height[i] * (NUM_LEDS - 1) / StartHeight);
    }
 
    for (int i = 0 ; i < BallCount ; i++) {
      leds[Position[i]] = CRGB(colors[i][0], colors[i][1], colors[i][2]);
    }
   
    delay(5);
    FastLED.show();
    for (int i = 0; i < NUM_LEDS; i++) {
      leds[i] = CRGB(0, 0, 0);
    }
}

void fillRandom(){
  static uint16_t counterRandom = 0;
  static uint16_t iRandom = NUM_LEDS-1;
  static uint8_t r; static uint8_t g; static uint8_t b;

  if(iRandom==NUM_LEDS-1){
    if(counterRandom==0){
      if(off(isOn) < 25){
        isOn = false;
        return;
      }
      for (int i = 0; i < NUM_LEDS; i++) {
        leds[i] = CRGB(0, 0, 0);
      }
    }
    isOn = true;
    r = random(255);
    g = random(255);
    b = random(255);
  }
  else
    leds[iRandom+1] = CRGB(0, 0, 0); // turn previous LED off
    
  leds[iRandom] = CRGB(r, g, b); // turn current LED on
  delay(5);
  FastLED.show();
  
  if(iRandom > counterRandom){
    iRandom--;
  }
  else{
    iRandom = NUM_LEDS-1;
    counterRandom = (counterRandom+1)%NUM_LEDS;
  }
}

void twinkle(bool first){
  static uint16_t counterTwinkle = 0;
  static uint8_t pixel;
  static uint8_t r; static uint8_t g; static uint8_t b;
  
  if(first) counterTwinkle=0;

  if(counterTwinkle == 0){
    for (int i = 0; i < NUM_LEDS; i++) {
      leds[i] = CRGB(0, 0, 0);
    }
  }
  if(counterTwinkle%20 == 0){
    pixel = random(NUM_LEDS);
    r = random(255);
    g = random(255);
    b = random(255);
  }
  
  leds[pixel] = CRGB(r, g, b);
  delay(5);
  FastLED.show();

  counterTwinkle = (counterTwinkle+1)%400;
}

void sparkle(){
  for (int i = 0; i < NUM_LEDS; i++) {
    leds[i] = CRGB(0, 0, 0);
  }
  uint8_t pixel = random(NUM_LEDS);
  leds[pixel] = CRGB(random(255), random(255), random(255));
  delay(2);
  FastLED.show();
  leds[pixel] = CRGB(0, 0, 0);

  pixel = random(NUM_LEDS);
  leds[pixel] = CRGB(random(255), random(255), random(255));
  delay(3);
  FastLED.show();
}

void allWhite() {
  for (int i = 0; i < NUM_LEDS; i++) {
    leds[i] = CRGB(255, 255, 235);
  }
  delay(5);
  FastLED.show();
}

void chillFade() {
  static int fadeVal = 0;
  static int counter = 0;
  static int from[3] = {0, 234, 255};
  static int to[3]   = {255, 0, 214};
  static int i, j;
  static double dsteps = 500.0;
  static double s1 = 0, s2 = 0, s3 = 0, tmp1 = 0, tmp2 = 0, tmp3 = 0;
  static bool reverse = false;
  if (chillfade == false) {
    for (int i = 0; i < NUM_LEDS; i++) {
      leds[i] = CRGB(from[0], from[1], from[2]);
    }
    s1 = double((to[0] - from[0])) / dsteps;
    s2 = double((to[1] - from[1])) / dsteps;
    s3 = double((to[2] - from[2])) / dsteps;
    tmp1 = from[0], tmp2 = from[1], tmp3 = from[2];
    chillfade = true;
  }

  if (!reverse)
  {
    tmp1 += s1;
    tmp2 += s2;
    tmp3 += s3;
  }
  else
  {
    tmp1 -= s1;
    tmp2 -= s2;
    tmp3 -= s3;
  }

  for (j = 0; j < NUM_LEDS; j++)
    leds[j] = CRGB((int)round(tmp1), (int)round(tmp2), (int)round(tmp3));
  FastLED.show();
  delay(5);

  counter++;
  if (counter == (int)dsteps) {
    if (!reverse)
      tmp1 = to[0], tmp2 = to[1], tmp3 = to[2];
    else
      tmp1 = from[0], tmp2 = from[1], tmp3 = from[2];
    reverse = !reverse;
    counter = 0;
  }
}

void soundReactive(int analogRaw) {

  int sanityValue = sanityBuffer->computeAverage();
  if (!(abs(analogRaw - sanityValue) > BUFFER_DEVIATION)) {
    sanityBuffer->setSample(analogRaw);
  }
  analogRaw = fscale(MIC_LOW, MIC_HIGH, MIC_LOW, MIC_HIGH, analogRaw, 0.4);

  if (samples->setSample(analogRaw))
    return;

  uint16_t longTermAverage = longTermSamples->computeAverage();
  uint16_t useVal = samples->computeAverage();
  longTermSamples->setSample(useVal);

  int diff = (useVal - longTermAverage);
  if (diff > 5)
  {
    if (globalHue < 235)
    {
      globalHue += hueIncrement;
    }
  }
  else if (diff < -5)
  {
    if (globalHue > 2)
    {
      globalHue -= hueIncrement;
    }
  }


  int curshow = fscale(MIC_LOW, MIC_HIGH, 0.0, (float)NUM_LEDS, (float)useVal, 0);
  //int curshow = map(useVal, MIC_LOW, MIC_HIGH, 0, NUM_LEDS)

  for (int i = 0; i < NUM_LEDS; i++)
  {
    if (i < curshow)
    {
      leds[i] = CHSV(globalHue + hueOffset + (i * 2), 255, 255);
    }
    else
    {
      leds[i] = CRGB(leds[i].r / fadeScale, leds[i].g / fadeScale, leds[i].b / fadeScale);
    }

  }
  delay(5);
  FastLED.show();
}

void connectToWifi() {
  WiFi.setSleepMode(WIFI_NONE_SLEEP);
  WiFi.mode(WIFI_STA);
  for (int i = 0; i < NUM_LEDS; i++)
  {
    leds[i] = CHSV(0, 0, 0);
  }
  leds[0] = CRGB(0, 0, 255);
  FastLED.show();

  int i = 0;
  while (WiFi.status() != WL_CONNECTED)
  { // Wait for the Wi-Fi to connect
    delay(1000);
    Serial.print(++i);
    Serial.print(' ');
    if (i == 10) {
      isconnected = false;
      leds[0] = CRGB(255, 0, 0);
      FastLED.show();
      delay(2000);
      return;
    }
  }
  Serial.println('\n');
  Serial.println("Connection established!");
  Serial.print("IP address:\t");
  Serial.println(WiFi.localIP()); // Send the IP address of the ESP8266 to the computer
  leds[0] = CRGB(0, 255, 0);
  FastLED.show();
  lastReceived = millis();
}
float fscale(float originalMin, float originalMax, float newBegin, float newEnd, float inputValue, float curve)
{

  float OriginalRange = 0;
  float NewRange = 0;
  float zeroRefCurVal = 0;
  float normalizedCurVal = 0;
  float rangedValue = 0;
  boolean invFlag = 0;

  // condition curve parameter
  // limit range

  if (curve > 10)
    curve = 10;
  if (curve < -10)
    curve = -10;

  curve = (curve * -.1);  // - invert and scale - this seems more intuitive - postive numbers give more weight to high end on output
  curve = pow(10, curve); // convert linear scale into lograthimic exponent for other pow function

  // Check for out of range inputValues
  if (inputValue < originalMin)
  {
    inputValue = originalMin;
  }
  if (inputValue > originalMax)
  {
    inputValue = originalMax;
  }

  // Zero Refference the values
  OriginalRange = originalMax - originalMin;

  if (newEnd > newBegin)
  {
    NewRange = newEnd - newBegin;
  }
  else
  {
    NewRange = newBegin - newEnd;
    invFlag = 1;
  }

  zeroRefCurVal = inputValue - originalMin;
  normalizedCurVal = zeroRefCurVal / OriginalRange; // normalize to 0 - 1 float

  // Check for originalMin > originalMax  - the math for all other cases i.e. negative numbers seems to work out fine
  if (originalMin > originalMax)
  {
    return 0;
  }

  if (invFlag == 0)
  {
    rangedValue = (pow(normalizedCurVal, curve) * NewRange) + newBegin;
  }
  else // invert the ranges
  {
    rangedValue = newBegin - (pow(normalizedCurVal, curve) * NewRange);
  }

  return rangedValue;
}
