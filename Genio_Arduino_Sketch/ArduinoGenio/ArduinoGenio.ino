#if defined(LED_BUILTIN)
#define LED LED_BUILTIN // Use built in LED
#else
#define LED 9 // Pin 9
#endif

//Roast Data:
String beanTemperature;
String environmentTemp;
String beanRateRise;
String environmentRateRise;
String roastTime;
String developmentTime;
String developmentPercentage; 
String turnPointTime;
String turnPointTemp;
String firstCrackTime;
String firstCrackTemp;
String fullGas;
String fanSpeed;
String drumSpeed;
int counter = 1;
int timeCounter = 100;

//Settings Data:
String primeTemp;
String temperature;
String rateOfRise;
String drumSpeedMin;
String drumSpeedMax;
String fanSpeedMin;
String fanSpeedMax; 
String ethernetIP;
String ethernetGateway;
String firstCrack;
String DHCP;

int ledState;  //control the switch for built-in LED

uint32_t timer;
bool isConnected;

/*
 * Run when board is intialized (i.e reset) 
 */
void setup() 
{
  //Serial is used for communication between the Arduino board and a computer or other devices
  //Baud Rate set to standard for USB comms
  Serial.begin(19200);
  SerialUSB.begin(19200);

  pinMode(LED, OUTPUT);  //Initializes LED for output
  Serial.println(F("Program started"));  
  SerialUSB.println(F("Program started"));
  
}

void loop() 
{
    if( SerialUSB.available() )
    {
       String message = SerialUSB.readString();
       Serial.println(message);
       /*checking received data: This checks for data received from device and in our case, has to be data packet request 
       * value 1(HIGH) = led on
       * value 0(LOW) = led off
       * value compareReceivedRequestData(message) = true(request-info)
       */
      compareReceivedRequestData(message);

 }  
 
}

/*
 * Check whether the data received by the device is an actual 'request-info' string or settings data
 */
boolean compareReceivedRequestData(String message)
{
     if (message == "request-info")
     {
      Serial.println("The command sent, was request-info");
      sendGenioPacketToDevice();
     }
     else if(message == "0")
     {
      ledState = LOW;
      Serial.println("The command that was sent zero, 0"); 
      analogWrite(LED, ledState);
     }
     else if(message == "1")
     {
      ledState = HIGH;
      Serial.println("The command that was sent one, 1"); 
      analogWrite(LED, ledState);
     }
     else if ((message.length() > 3) &&  (message.substring(0,2) == "cs")){
        //check whether the command contains this setting
        //Blink twice
        Serial.println("The prime temp was set");
        //Please Note: prime temperature is to be extracted from the command sent
        setPrimeTemp(message);
        toggleLED();
        toggleLED();
     }
     else if ((message.length() > 2) &&  (message.substring(0,2) == "rt")){
        //Blink 3 times
        //check whether the command contains this setting
        Serial.println("The temperature was set");
        //Please Note: temperature is to be extracted from the command sent
        setTemperature(message);
        toggleLED();
        toggleLED();
        toggleLED();
     }
     else if ((message.length() > 2) &&  (message.substring(0,2) == "ru")){
        //Blink 4 times
        //check whether the command contains this setting
        Serial.println("The rate of rise was set");
        //Please Note: rate of rise is to be extracted from the command sent
        setRateOfRise(message);
        toggleLED();
        toggleLED();
        toggleLED();
        toggleLED();
     }
     else if ((message.length() > 2) &&  (message.substring(0,2) == "ae")){
        //Blink 5 times
        //check whether the command contains this setting
        Serial.println("The drum speed min was set");
        //Please Note: drum speed min is to be extracted from the command sent
        setDrumSpeedMin(message);
        toggleLED();
        toggleLED();
        toggleLED();
        toggleLED();
        toggleLED();
     }
     else if ((message.length() > 2) &&  (message.substring(0,2) == "ef")){
        //Blink 6 times
        //check whether the command contains this setting
        Serial.println("The drum speed max was set");
        //Please Note: drum speed max is to be extracted from the command sent
        setDrumSpeedMax(message);
        toggleLED();
        toggleLED();
        toggleLED();
        toggleLED();
        toggleLED();
        toggleLED();
     }
     else if ((message.length() > 2) &&  (message.substring(0,2) == "aj")){
        //Blink 7 times
        //check whether the command contains this setting
        Serial.println("The fan speed min was set");
        //Please Note: fan speed min is to be extracted from the command sent
        setFanSpeedMin(message);
        toggleLED();
        toggleLED();
        toggleLED();
        toggleLED();
        toggleLED();
        toggleLED();
        toggleLED();
     }
     else if ((message.length() > 2) &&  (message.substring(0,2) == "ak")){
        //Blink 8 times
        //check whether the command contains this setting
        Serial.println("The fan speed max was set");
        //Please Note: fan speed max is to be extracted from the command sent
        setFanSpeedMax(message);
        toggleLED();
        toggleLED();
        toggleLED();
        toggleLED();
        toggleLED();
        toggleLED();
        toggleLED();
        toggleLED();
     }
     else if ((message.length() > 2) &&  (message.substring(0,2) == "ry")){
        //Blink 9 times
        //check whether the command contains this setting
        Serial.println("The ethernet ip was set");
        //Please Note: ethernet ip is to be extracted from the command sent
        setEthernetIP(message);
        toggleLED();
        toggleLED();
        toggleLED();
        toggleLED();
        toggleLED();
        toggleLED();
        toggleLED();
        toggleLED();
        toggleLED();
     }
     else if ((message.length() > 2) &&  (message.substring(0,2) == "rz")){
        //Blink 10 times
        //check whether the command contains this setting
        Serial.println("The ethernet gateway was set");
        //Please Note: ethernet gateway is to be extracted from the command sent
        setEthernetGateway(message);
        toggleLED();
        toggleLED();
        toggleLED();
        toggleLED();
        toggleLED();
        toggleLED();
        toggleLED();
        toggleLED();
        toggleLED();
        toggleLED();
     }
     else if ((message.length() > 2) &&  (message.substring(0,2) == "rv")){
        //Blink 11 times
        //check whether the command contains this setting
        Serial.println("The first crack was set");
        //Please Note: first crack is to be extracted from the command sent
        setFirstCrack(message);
        toggleLED();
        toggleLED();
        toggleLED();
        toggleLED();
        toggleLED();
        toggleLED();
        toggleLED();
        toggleLED();
        toggleLED();
        toggleLED();
        toggleLED();
     }
     else if ((message.length() > 2) &&  (message.substring(0,2) == "dh")){
        //Blink 11 times
        //check whether the command contains this setting
        Serial.println("The DHCP was set/unset");
        //Please Note: first crack is to be extracted from the command sent
        setFirstCrack(message);
        toggleLED();
        toggleLED();
        toggleLED();
        toggleLED();
        toggleLED();
        toggleLED();
        toggleLED();
        toggleLED();
        toggleLED();
        toggleLED();
        toggleLED();
     }
     else
     {
      Serial.println("No command sent"); 
     }
}

//blink LED 
void toggleLED()
{
  analogWrite(LED, HIGH);
  analogWrite(LED, LOW);
  
  //To see Built-In LED Blinking
    /*for (int fadeValue = 0 ; fadeValue <= 255; fadeValue += 5) {
    // sets the value (range from 0 to 255):
    analogWrite(LED, fadeValue);
  }

  // fade out from max to min in increments of 5 points:
  for (int fadeValue = 255 ; fadeValue >= 0; fadeValue -= 5) {
    // sets the value (range from 0 to 255):
    analogWrite(LED, fadeValue);
  }*/
}

void sendGenioPacketToDevice()
{
    //prefix and suffix are used for checksum
    String prefix = "<|";  
    String suffix = "|>";
   
    String dataPacket = getBeanTemperature() + "|" + getEnvironmentTemp() + "|" + getBeanRateRise() + "|" + getEnvironmentRateRise() + "|" + getRoastTime() + "|" +
    getDevelopmentTime() + "|" + getDevelopmentPercentage() + "|" + getTurnPointTime() + "|" + getTurnPointTemp() + "|" + getFirstCrackTime() + "|" + getFirstCrackTemp() + "|" + getFullGas() + "|" + getFanSpeed() + "|" + getDrumSpeed();

    counter++;
    timeCounter = timeCounter + 100;
     
    dataPacket = prefix + dataPacket + suffix;
    dataPacket;
    uint16_t dataLength = 104; 
    //This is what will be sent to the device
    char *tokenizedDataPacket = new char[dataLength];
    dataPacket.toCharArray(tokenizedDataPacket, dataLength);
    Serial.println(tokenizedDataPacket);
    //Returns 0 for success
    SerialUSB.println(tokenizedDataPacket);
    toggleLED();
}

/**
 * The following methods are used to set information from the device
 */
void setPrimeTemp(String primetemp)
{
  primeTemp = primetemp;
  //Start
  //...Assign primeTemp to coffee machine board here
  //End   
}

void setTemperature(String temp)
{
  temperature = temp;
  //Start
  //...Assign temperature to coffee machine board here
  //End
}

void setRateOfRise(String rateofrise)
{
 rateOfRise = rateofrise;
 //Start
  //...Assign rateOfRise to coffee machine board here
  //End 
}

void setDrumSpeedMin(String drumspeedmin)
{
  drumSpeedMin = drumspeedmin;
  //Start
  //...Assign drumSpeedMin to coffee machine board here
  //End
}

void setDrumSpeedMax(String drumspeedmax)
{
  drumSpeedMax = drumspeedmax;
  //Start
  //...Assign drumSpeedMax to coffee machine board here
  //End
}

void setFanSpeedMin(String fanspeedmin)
{
  fanSpeedMin = fanspeedmin;
  //Start
  //...Assign fanSpeedMin to coffee machine board here
  //End
}

void setFanSpeedMax(String fanspeedmax)
{
  fanSpeedMax = fanspeedmax;
  //Start
  //...Assign fanSpeedMax to coffee machine board here
  //End
}

void setEthernetIP(String ethernetip)
{
  ethernetIP = ethernetip;
  //Start
  //...Assign ethernetIP to coffee machine board here
  //End
}

void setEthernetGateway(String ethernetgateway)
{
  ethernetGateway = ethernetgateway;
  //Start
  //...Assign ethernetGateway to coffee machine board here
  //End
}

void setFirstCrack(String firstcrack)
{
  firstCrack = firstcrack;
  //Start
  //...Assign firstCrack to coffee machine board here
  //End  
}

void setDHCP(String dhcp)
{
  DHCP = dhcp;
  //Start
  //...Assign firstCrack to coffee machine board here
  //End  
}
/**
 * The following methods are used to get information from the coffee machine board
 */
String getBeanTemperature()
{
  int intialBeanTemp = 1200 + counter*10;
  beanTemperature = "ga" + String(intialBeanTemp);
  //Start
  //...Please replace with code to get this parameters data.
  //End
  return beanTemperature;
}


String getEnvironmentTemp()
{
  int initialEnvironTemp = 100 + counter*10;
  environmentTemp = "gb" + String(initialEnvironTemp);
  //Start
  //...Please replace with code to get this parameters data.
  //End  
  return environmentTemp;  
}

String getBeanRateRise()
{
  int initialBeanRateOfRise = 120 + counter*10;
  beanRateRise = "ra" + String(initialBeanRateOfRise);
  //Start
  //...Please replace with code to get this parameters data.
  //End  
  return beanRateRise;
}

String getEnvironmentRateRise()
{
  int initialEnvironRateOfRise = 330 + counter*10;
  environmentRateRise = "rb" + String(initialEnvironRateOfRise);
  //Start
  //...Please replace with code to get this parameters data.
  //End  
  return environmentRateRise;
}

String getRoastTime()
{
  int initialRoastTime = 1201 + timeCounter;
  roastTime = "rm" + String(initialRoastTime);  
  //Start
  //...Please replace with code to get this parameters data.
  //End  
  return roastTime;
}

String getDevelopmentTime()
{
  int initialDevTime = 120 + timeCounter;
  developmentTime = "rn" + String(initialDevTime);  //This 2 variables will need crarity from the client or doc if any  
  //Start
  //...Please replace with code to get this parameters data.
  //End  
  return developmentTime;
}

String getDevelopmentPercentage()
{
  int initialDevPercent = 130 + counter*10;
  developmentPercentage = "ro0" + String(initialDevPercent);   
  //Start
  //...Please replace with code to get this parameters data.
  //End  
  return developmentPercentage;
}

String getTurnPointTime()
{
  int initialTurnPointTime = 151 + timeCounter;
  turnPointTime = "rp0" + String(initialTurnPointTime);  
  //Start
  //...Please replace with code to get this parameters data.
  //End  
  return turnPointTime;
}

String getTurnPointTemp()
{
  int initialTurnPointTemp = 890 + counter*10;
  turnPointTemp = "rq" + String(initialTurnPointTemp);  
  //Start
  //...Please replace with code to get this parameters data.
  //End  
  return turnPointTemp;
}

String getFirstCrackTime()
{
  int initialFirstCrackTime = 1201 + timeCounter;
  firstCrackTime = "rr" + String(initialFirstCrackTime); 
  //Start
  //...Please replace with code to get this parameters data.
  //End
  return firstCrackTime;
}

String getFirstCrackTemp()
{
  int initialFirstCrackTemp = 1850 + counter*10;
  firstCrackTemp = "rs" + String(initialFirstCrackTemp);    
  //Start
  //...Please replace with code to get this parameters data.
  //End  
  return firstCrackTemp;
}

String getFullGas()
{
  int initialFullGas = 020 + counter;
  fullGas = "sa" + String(initialFullGas);    
  //Start
  //...Please replace with code to get this parameters data.
  //End  
  return fullGas;
}

String getFanSpeed()
{
  int initialFanSpeed = 10 + counter;
  fanSpeed = "sb" + String(initialFanSpeed);
  //Start
  //...Please replace with code to get this parameters data.
  //End  
  return fanSpeed;    
}

String getDrumSpeed()
{
  int initialDrumSpeed = 10 + counter;
  drumSpeed = "sc" + String(initialDrumSpeed);
  //Start
  //...Please replace with code to get this parameters data.
  //End  
  return drumSpeed; 
}


