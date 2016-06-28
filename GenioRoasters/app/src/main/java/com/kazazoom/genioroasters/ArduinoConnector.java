package com.kazazoom.genioroasters;

import android.app.Service;
import android.content.Intent;
import android.hardware.usb.UsbDevice;
import android.hardware.usb.UsbDeviceConnection;
import android.os.IBinder;

import com.felhr.usbserial.UsbSerialDevice;
import com.felhr.usbserial.UsbSerialInterface;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Date;

public class ArduinoConnector extends Service{

    RoastData roastData;
    UsbDevice mUsbDevice;
    UsbDeviceConnection mUsbConnection;
    public static int devicePID; //arduino product id
    public static int deviceVID; //arduino vendor id
    public static int HandShakeFails;
    public static boolean isConnected;
    public StringBuilder tokenizedString = new StringBuilder();
    //the Arduino board
    public UsbSerialDevice serialPort;

    public static int ADK_BOARD_STRING_LENGTH = 104;  //set to change
    public static int BAUD_RATE = 19200;
    public static final String ACTION_USB_PERMISSION = "com.kazazoom.genioroasters.ArduinoConnector.USB_PERMISSION";
    public static final String ACTION_USB_READY = "com.kazazoom.genioroasters.ArduinoConnector.USB_READY";
    public static final String ACTION_USB_NOT_SUPPORTED = "com.kazazoom.genioroasters.ArduinoConnector.USB_NOT_SUPPORTED";
    public static final String ACTION_NO_USB = "com.kazazoom.genioroasters.ArduinoConnector.NO_USB";
    public static final String ACTION_USB_PERMISSION_GRANTED = "com.kazazoom.genioroasters.ArduinoConnector.USB_PERMISSION_GRANTED";
    public static final String ACTION_USB_PERMISSION_NOT_GRANTED = "com.kazazoom.genioroasters.ArduinoConnector.USB_PERMISSION_NOT_GRANTED";
    public static final String ACTION_USB_DISCONNECTED = "com.kazazoom.genioroasters.ArduinoConnector.USB_DISCONNECTED";
    public static final String ACTION_USB_DEVICE_NOT_WORKING = "com.kazazoom.genioroasters.ArduinoConnector.ACTION_USB_DEVICE_NOT_WORKING";
    public static final String ACTION_NEW_DATA_RECEIVED = "com.kazazoom.genioroasters.ArduinoConnector.ACTION_NEW_DATA_RECEIVED";

    public ArduinoConnector(){
        roastData = RoastData.getInstance();
    }

    /**
     * Set up device
     * @param UsbDevice
     */
    public ArduinoConnector(UsbDevice mUsbDevice) {

        this.mUsbDevice = mUsbDevice;
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }

    /**
     * Set up connector for input and output
     * @param UsbDeviceConnection
     */
    public void setUpStreams(UsbDeviceConnection mUsbConnection)
    {
        this.mUsbConnection = mUsbConnection;

        try {

            serialPort = UsbSerialDevice.createUsbSerialDevice(mUsbDevice, mUsbConnection);
            //set the settings for communication
            if (serialPort != null) {
                if (serialPort.open()) {
                    serialPort.setBaudRate(BAUD_RATE);
                    serialPort.setDataBits(UsbSerialInterface.DATA_BITS_8);
                    serialPort.setStopBits(UsbSerialInterface.STOP_BITS_1);
                    serialPort.setStopBits(UsbSerialInterface.STOP_BITS_1);
                    serialPort.setParity(UsbSerialInterface.PARITY_NONE);
                    serialPort.setFlowControl(UsbSerialInterface.FLOW_CONTROL_RTS_CTS);
                    serialPort.read(mCallback);
                    //set connection status:
                    ArduinoConnector.isConnected = true;
                }
            }

        } catch (Exception ex) {
            writeLogFile("arduinoconnector", ex.getMessage());
        }

    }

    /**
     * @Purpose: Sends data to the arduino board
     * @param:  textToSend
     */
    public void sendDataToArduino(String textToSend)
    {
       try{
           serialPort.write(
                   textToSend.getBytes());
        }
        catch (Exception ex){
            //exception handling action to take
            writeLogFile("ArduinoConnector", ex.getMessage());
        }
    }

    private UsbSerialInterface.UsbReadCallback mCallback = new UsbSerialInterface.UsbReadCallback() {
        @Override
        /*
         */
        public void onReceivedData(byte[] arg0) {
            /*
             *This receives data and send to the RoastData instance if it's valid
             * If no data is received then it broadcast an error message and increments handShake fails used by
             * ScreenUpdaterService
             */
            try{
                String data = new String(arg0, "UTF-8");
                if(data.length() > 0) {
                    //Assume that the tokenizedString is empty and re-assign the updated data to avoid data building on top of each other
                    tokenizedString = new StringBuilder(data);
                    writeLogFile("data", tokenizedString.toString());
                    if (roastData.validateTokenizedDataWithChecksum(tokenizedString.toString()) == true) {
                        tokenizedString.delete(0, tokenizedString.length()); //Buffer clean-up after use
                    }
                }
                else{
                    HandShakeFails ++;
                    Intent intent = new Intent(ArduinoConnector.ACTION_USB_DEVICE_NOT_WORKING);
                    DashBoardActivity.context.sendBroadcast(intent);
                }
            }
            catch(Exception ex){
              writeLogFile("ArduinoConnector", ex.getMessage());
            }
        }
    };


    /**
     * Used for writing data to files, for testing and seeing error messages
     * @param errorText
     */
    public void writeLogFile(String errorText)
    {
        /*
         *writes using this line getExternalCacheDir to android folder on the device
         * This creates everytime it runs
         */
        String fileName = "arduino" + new Date() + ".txt";
        File outputFile = new File(DashBoardActivity.context.getExternalCacheDir(), fileName);
        try {

            FileWriter w = new FileWriter(outputFile);
            w.append(errorText);
            w.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void writeLogFile(String filename, String errorText)
    {
        /*
         *writes using this line getExternalCacheDir to android folder on the device
         * This creates everytime it runs
         */
        String fileName = filename + ".txt";
        File outputFile = new File(DashBoardActivity.context.getExternalCacheDir(), fileName);
        try {

            FileWriter w = new FileWriter(outputFile);
            w.append(errorText);
            w.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

  }
