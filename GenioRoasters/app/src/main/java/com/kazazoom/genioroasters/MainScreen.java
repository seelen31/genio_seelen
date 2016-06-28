package com.kazazoom.genioroasters;

import android.app.Activity;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.hardware.usb.UsbDevice;
import android.hardware.usb.UsbDeviceConnection;
import android.hardware.usb.UsbManager;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import java.util.HashMap;
import java.util.Map;

public class MainScreen extends Activity {


    ArduinoConnector arduinoConnector;
    UsbManager mUsbManager;
    Button RoastDatabutton;
    TextView receivedText;
    public static Context context;

    private Button offButton;
    private Button onButton;

    private Button settingsButton;

    BroadcastReceiver PermissionRequestReceiver = new BroadcastReceiver(){
        @Override
        public void onReceive(Context context, Intent intent) {
            try{
                switch(intent.getAction()) {

                    case ArduinoConnector.ACTION_USB_PERMISSION:
                        UsbDeviceConnection mUsbConnection = mUsbManager.openDevice(arduinoConnector.mUsbDevice);
                        arduinoConnector.setUpStreams(mUsbConnection);
                        break;
                    case ArduinoConnector.ACTION_NEW_DATA_RECEIVED:
                        UpdateScreen(
                                intent.getStringExtra(
                                        ArduinoConnector.
                                                ACTION_NEW_DATA_RECEIVED));
                }

            }catch(Exception ex){

                String text = ex.getMessage() + "\r\n";
                text += ex.getCause() + "\r\n";
                for(int i = 0; i < ex.getStackTrace().length; i++)
                    text += ex.getStackTrace()[i] + "\r\n";
                arduinoConnector.writeLogFile(text);

            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.testing_screen);

        MainScreen.context = getApplicationContext();
        setFilter();
        mUsbManager = (UsbManager) getSystemService(Context.USB_SERVICE);
        arduinoConnector = new ArduinoConnector();
        findSerialPortDevice();

        receivedText = (TextView) findViewById(R.id.receivedText);

        RoastDatabutton = (Button) findViewById(R.id.RoastDatabutton);
        RoastDatabutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                arduinoConnector.sendDataToArduino("request-info");
            }
        });

        offButton = (Button)findViewById(R.id.offButton);
        offButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                arduinoConnector.sendDataToArduino("0");
            }
        });

        onButton = (Button)findViewById(R.id.onButton);
        onButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                arduinoConnector.sendDataToArduino("1");
            }
        });
        
        settingsButton = (Button) findViewById(R.id.settingsButton);
        settingsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainScreen.this, SettingsActivity.class);
                startActivity(intent);
            }
        });
    }


    /**
     * Used to update the display with received data
     * @param data
     */
    public void UpdateScreen(String data){
        arduinoConnector.writeLogFile("we are here receiving data");
        try{
            //Toast.makeText(MainScreen.context, "New Data received", Toast.LENGTH_LONG);
            // String text = data;
            receivedText.append(data);
        }
        catch (Exception ex){

            String text = ex.getMessage() + "\r\n";
            text += ex.getCause() + "\r\n";
            for(int i = 0; i < ex.getStackTrace().length; i++)
                text += ex.getStackTrace()[i] + "\r\n";
            arduinoConnector.writeLogFile(text);
        }

    }

    /**
     * @Purpose: Used to find available devices and connected
     */
    public void findSerialPortDevice()
    {
        HashMap<String, UsbDevice> usbDevices = mUsbManager.getDeviceList();

        if(!usbDevices.isEmpty())
        {
            for (Map.Entry<String, UsbDevice> entry : usbDevices.entrySet()) {

                arduinoConnector.mUsbDevice = entry.getValue();
                ArduinoConnector.devicePID = arduinoConnector.mUsbDevice.getDeviceId();
                ArduinoConnector.deviceVID = arduinoConnector.mUsbDevice.getVendorId();
                requestUserPermission();
            }
        }
    }

    /**
     * @Purpose: Requests permission from the user through broadcastlistener
     */
    private void requestUserPermission() {
        PendingIntent mPendingIntent = PendingIntent.getBroadcast(this, 0, new Intent(ArduinoConnector.ACTION_USB_PERMISSION), 0);
        mUsbManager.requestPermission(arduinoConnector.mUsbDevice, mPendingIntent);
    }

    /**
     * @Purpose: Used to register android app events actions
     */
    private void setFilter() {
        IntentFilter filter = new IntentFilter();
        filter.addAction(ArduinoConnector.ACTION_USB_PERMISSION);
        registerReceiver(PermissionRequestReceiver, filter);
    }



}