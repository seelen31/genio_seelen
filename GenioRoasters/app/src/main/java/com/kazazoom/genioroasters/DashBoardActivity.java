package com.kazazoom.genioroasters;

import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.hardware.usb.UsbDevice;
import android.hardware.usb.UsbDeviceConnection;
import android.hardware.usb.UsbManager;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import android.support.v7.widget.Toolbar;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Timer;

public class DashBoardActivity extends AppCompatActivity{

    UsbManager usbManager;
    private ArduinoConnector arduinoConnector;
    public static Context context;
    private Button btnFirstCrack;
    private TextView lblBeanTemp;
    private TextView lblBeanRateOfRise;
    private TextView lblEnvironmentTemp;
    private TextView lblEnvironRateOfRise;
    private TextView lblRoastTime;
    private TextView lblTurnPointTime;
    private TextView lblTurnPointTemp;
    private TextView lblFirstCrackTime;
    private TextView lblFirstCrackTemp;
    private TextView lblDevelopmentTime;
    private TextView lblDevelopmentPercentage;
    private TextView lblFullGas;
    private TextView lblFanSpeed;
    private TextView lblDrumSpeed;
    private static int backButtonCountForExit = 0;
    private RoastData roastData;

    private SettingsData settingsData;

    private TextView lblBeanRateOfRiseUnit;
    private TextView lblEnvironRateOfRiseUnit;

    /*
    ** Define the eventRequestReceiver at this point to enable the stream to send data through using
    ** Android BroadcastReceiver
    */
    BroadcastReceiver eventRequestReceiver = new BroadcastReceiver()
    {
        @Override
        public void onReceive(Context context, Intent intent)
        {
            //Attempt to connect the device to the Arduino board
            try{
                switch(intent.getAction())
                {
                    //If the connecting device requires permissions to access application
                    case ArduinoConnector.ACTION_USB_PERMISSION:
                        UsbDeviceConnection mUsbConnection = usbManager.openDevice(arduinoConnector.mUsbDevice);
                        arduinoConnector.setUpStreams(mUsbConnection);
                        displayConnectionStatus();
                        try {
                            //once connection is made, open the screen updater service
                            Intent screenUpdaterIntent = new Intent(DashBoardActivity.context, ScreenUpdaterService.class);
                            startService(screenUpdaterIntent);

                            //send a request every 500ms
                            Timer timer = new Timer();
                            DataSendingHelper dataSendingHelper = new DataSendingHelper(arduinoConnector);
                            timer.schedule(dataSendingHelper, 0, 500);
                        }
                        catch(Exception ex)
                        {
                            ex.printStackTrace();
                        }
                        break;

                    //If data is received, update labels with new roast data
                    case ArduinoConnector.ACTION_NEW_DATA_RECEIVED:
                        try {
                            roastData = (RoastData) intent.getSerializableExtra("UPDATED_ROAST_DATA");

                            updateScreenWithNewRoastData();
                        }
                        catch(Exception ex)
                        {
                            arduinoConnector.writeLogFile("roast_data_error", "Failed to receive data");
                        }
                        break;
                    case ArduinoConnector.ACTION_USB_DEVICE_NOT_WORKING:
                        Toast.makeText(DashBoardActivity.this, "Communication Error!", Toast.LENGTH_LONG);
                        break;
                    case ArduinoConnector.ACTION_USB_DISCONNECTED:
                        destroyUsbConnection();
                        break;
                    case UsbManager.ACTION_USB_DEVICE_ATTACHED:
                        findSerialPortDevice();
                        displayConnectionStatus();
                        break;
                    case UsbManager.ACTION_USB_DEVICE_DETACHED:
                        destroyUsbConnection();
                }

            }catch(Exception ex)
            {
                //If connection is unsuccessful, write error to log file on device using the cause of the error
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
        setContentView(R.layout.activity_dash_board);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        DashBoardActivity.context = getApplicationContext();
        usbManager = (UsbManager) getSystemService(Context.USB_SERVICE);
        arduinoConnector = new ArduinoConnector();

        updateScreenWithZeros();

//        TabActionsListener.setTabNavigationActions(this);
        //set up the connection
        try {
            setFilter();
            findSerialPortDevice();
        }
        catch(Exception ex)
        {
            arduinoConnector.writeLogFile("ERROR", "The connection could not be set up due to " + ex.getMessage());
        }

        //set up screen
        try {
            initializeScreenLabels();
            setFontsOnTextViews();
        }
        catch(Exception ex)
        {
            arduinoConnector.writeLogFile("ERROR", "The screen could not be set up due to " + ex.getMessage());
        }

        //Set the temperature labels based on settings data

        onFirstCrackClicked();
    }

    private void updateScreenWithZeros()
    {
        //The bean temperature
        lblBeanTemp = (TextView) findViewById(R.id.beanTemperatureValue);
        lblBeanTemp.setText("000.00");
        //bean rate of rise
        lblBeanRateOfRise = (TextView) findViewById(R.id.beanTemperatureRorValue);
        lblBeanRateOfRise.setText("00.00");
        //environment temperature
        lblEnvironmentTemp = (TextView) findViewById(R.id.environmentTemperatureValue);
        lblEnvironmentTemp.setText("000.00");
        //environment rate of rise
        lblEnvironRateOfRise = (TextView) findViewById(R.id.environmentTemperatureRorValue);
        lblEnvironRateOfRise.setText("00.00");
        //roast time
        lblRoastTime = (TextView) findViewById(R.id.roastTimeValue);
        lblRoastTime.setText("00:00");
        //turn point time
        lblTurnPointTime = (TextView) findViewById(R.id.turnPointTimeValue);
        lblTurnPointTime.setText("00:00");
        //turn point temp
        lblTurnPointTemp = (TextView) findViewById(R.id.turnPointTemperatureValue);
        lblTurnPointTemp.setText("000.00" + "\u00b0");
        //first crack time
        lblFirstCrackTime = (TextView) findViewById(R.id.firstCrackTimeValue);
        lblFirstCrackTime.setText("00:00");
        //first crack temp
        lblFirstCrackTemp = (TextView) findViewById(R.id.firstCrackTemperatureValue);
        lblFirstCrackTemp.setText("000.00" + "\u00b0");
        //development time
        lblDevelopmentTime = (TextView) findViewById(R.id.developmentTimeValue);
        lblDevelopmentTime.setText("00:00");
        //development percent
        lblDevelopmentPercentage = (TextView) findViewById(R.id.developmentPercentageValue);
        lblDevelopmentPercentage.setText("00%");
        //full gas
        lblFullGas = (TextView) findViewById(R.id.gasVolumeValue);
        lblFullGas.setText( "00");
        //fan speed
        lblFanSpeed = (TextView) findViewById(R.id.fanSpeedValue);
        lblFanSpeed.setText("00");
        //drum speed
        lblDrumSpeed = (TextView) findViewById(R.id.drumSpeedValue);
        lblDrumSpeed.setText("00");

        try {
            //Get the settings, if any
            settingsData = new SettingsData();
            settingsData.fetchValuesFromAndroidPreferences();

            //Bean rate of rise unit
            lblBeanRateOfRiseUnit = (TextView) findViewById(R.id.beanRorUnit);
            lblBeanRateOfRiseUnit.setText(Integer.toString(settingsData.RATE_OF_RISE));

            //Environment rate of rise unit
            lblEnvironRateOfRiseUnit = (TextView) findViewById(R.id.environmentRorUnit);
            lblEnvironRateOfRiseUnit.setText(Integer.toString(settingsData.RATE_OF_RISE));

        }
        catch(Exception ex)
        {
            arduinoConnector.writeLogFile("ERROR", "Android preferences not loading due to " + ex.getMessage());
        }
    }

    /**
     * This exit the app with double click to the back button
     */
    @Override
    public void onBackPressed()
    {

        //if(TabActionsListener.currentActivityID != 0) {
        if (backButtonCountForExit >= 1) {
            finish();
        } else {
            Toast.makeText(this, "Press the back button once again to close the application.", Toast.LENGTH_SHORT).show();
            backButtonCountForExit++;
        }
        //}
    }

    /**
     * inflates the menu from the menu_main.xml
     * @param menu
     * @return
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    /**
     * Action listening for the icons toolbar
     * @param item
     * @return
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_settings:
                // User chose the "Settings" item, show the app settings UI...
                Intent intent = new Intent(DashBoardActivity.context, SettingsActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK); //Used to start new activity from non-activity class:
                DashBoardActivity.context.startActivity(intent);
                return true;


            default:
                // If we got here, the user's action was not recognized.
                // Invoke the superclass to handle it.
                return super.onOptionsItemSelected(item);
        }
    }

    /*
    ** Link the Activity attributes to screen layout UI controls
     */
    private void initializeScreenLabels()
    {
        //The bean temperature
        lblBeanTemp = (TextView) findViewById(R.id.beanTemperatureValue);
        //bean rate of rise
        lblBeanRateOfRise = (TextView) findViewById(R.id.beanTemperatureRorValue);
        //environment temperature
        lblEnvironmentTemp = (TextView) findViewById(R.id.environmentTemperatureValue);
        //environment rate of rise
        lblEnvironRateOfRise = (TextView) findViewById(R.id.environmentTemperatureRorValue);
        //roast time
        lblRoastTime = (TextView) findViewById(R.id.roastTimeValue);
        //turn point time
        lblTurnPointTime = (TextView) findViewById(R.id.turnPointTimeValue);
        //turn point temp
        lblTurnPointTemp = (TextView) findViewById(R.id.turnPointTemperatureValue);
        //first crack time
        lblFirstCrackTime = (TextView) findViewById(R.id.firstCrackTimeValue);
        //first crack temp
        lblFirstCrackTemp = (TextView) findViewById(R.id.firstCrackTemperatureValue);
        //development time
        lblDevelopmentTime = (TextView) findViewById(R.id.developmentTimeValue);
        //development percent
        lblDevelopmentPercentage = (TextView) findViewById(R.id.developmentPercentageValue);
        //full gas
        lblFullGas = (TextView) findViewById(R.id.gasVolumeValue);
        //fan speed
        lblFanSpeed = (TextView) findViewById(R.id.fanSpeedValue);
        //drum speed
        lblDrumSpeed = (TextView) findViewById(R.id.drumSpeedValue);
        //first crack button
        btnFirstCrack = (Button) findViewById(R.id.firstCrackButton);
    }

    private void updateScreenWithNewRoastData()
    {
        //The bean temperature
        lblBeanTemp = (TextView) findViewById(R.id.beanTemperatureValue);
        lblBeanTemp.setText(roastData.getBeanTemp());
        //bean rate of rise
        lblBeanRateOfRise = (TextView) findViewById(R.id.beanTemperatureRorValue);
        lblBeanRateOfRise.setText(roastData.getBeanRateOfRise());
        //environment temperature
        lblEnvironmentTemp = (TextView) findViewById(R.id.environmentTemperatureValue);
        lblEnvironmentTemp.setText(roastData.getEnvironmentTemp());
        //environment rate of rise
        lblEnvironRateOfRise = (TextView) findViewById(R.id.environmentTemperatureRorValue);
        lblEnvironRateOfRise.setText(roastData.getEnvironmentRateOfRise());
        //roast time
        lblRoastTime = (TextView) findViewById(R.id.roastTimeValue);
        lblRoastTime.setText(roastData.getRoastTime());
        //turn point time
        lblTurnPointTime = (TextView) findViewById(R.id.turnPointTimeValue);
        lblTurnPointTime.setText(roastData.getTurnPointTime());
        //turn point temp
        lblTurnPointTemp = (TextView) findViewById(R.id.turnPointTemperatureValue);
        lblTurnPointTemp.setText(roastData.getTurnPointTemp() + "\u00b0");
        //first crack time
        lblFirstCrackTime = (TextView) findViewById(R.id.firstCrackTimeValue);
        //first crack temp
        lblFirstCrackTemp = (TextView) findViewById(R.id.firstCrackTemperatureValue);
        //development time
        lblDevelopmentTime = (TextView) findViewById(R.id.developmentTimeValue);
        lblDevelopmentTime.setText(roastData.getDevelopmentTime()+"1");
        //development percent
        lblDevelopmentPercentage = (TextView) findViewById(R.id.developmentPercentageValue);
        lblDevelopmentPercentage.setText(roastData.getDevelopmentPercentage()+"%");
        //full gas
        lblFullGas = (TextView) findViewById(R.id.gasVolumeValue);
        lblFullGas.setText(roastData.getFullGas());
        //fan speed
        lblFanSpeed = (TextView) findViewById(R.id.fanSpeedValue);
        lblFanSpeed.setText(roastData.getFanSpeed());
        //drum speed
        lblDrumSpeed = (TextView) findViewById(R.id.drumSpeedValue);
        lblDrumSpeed.setText(roastData.getDrumSpeed());
    }

    /*
    ** When the First Crack button is clicked, execute the specified actions
     */
    private void onFirstCrackClicked()
    {
        btnFirstCrack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Calendar cal = Calendar.getInstance();
                SimpleDateFormat sdf = new SimpleDateFormat("HH:mm");
                lblFirstCrackTime.setText(sdf.format(cal.getTime()));
                lblFirstCrackTemp.setText(roastData.getFirstCrackTemp() + "\u00b0");
            }
        });
    }


    @Override
    public void onDestroy(){
        try {
            super.onDestroy();
            unregisterReceiver(eventRequestReceiver);
        }
        catch(Exception ex)
        {
            arduinoConnector.writeLogFile("ERROR", "Could not unregister receiver due to " + ex.getMessage());
        }
    }

    /**
     * This destroys only the connection and not the activity
     */
    public void destroyUsbConnection()
    {
        try {
            ArduinoConnector.isConnected = false;
            Intent intent = new Intent(DashBoardActivity.context, ScreenUpdaterService.class);
            stopService(intent);
            Toast.makeText(getBaseContext(), "Device disconnected", Toast.LENGTH_LONG);
            displayConnectionStatus();
        }
        catch (Exception ex)
        {}
    }

    /*
  ** Used to find available devices and connected
  */
    public void findSerialPortDevice()
    {
        HashMap<String, UsbDevice> usbDevices = usbManager.getDeviceList();

        if(!usbDevices.isEmpty())
        {
            for (Map.Entry<String, UsbDevice> entry : usbDevices.entrySet()) {

                arduinoConnector.mUsbDevice = entry.getValue();
                ArduinoConnector.devicePID = arduinoConnector.mUsbDevice.getDeviceId();
                ArduinoConnector.deviceVID = arduinoConnector.mUsbDevice.getVendorId();
                if(ArduinoConnector.deviceVID == AppSettingsData.ARDUINO_VENDOR_ID){
                    requestUserPermission();
                }
            }
        }
    }

    /**
     ** Requests permission from the user through broadcastlistener
     */
    private void requestUserPermission() {
        PendingIntent mPendingIntent = PendingIntent.getBroadcast(this, 0, new Intent(ArduinoConnector.ACTION_USB_PERMISSION), 0);
        usbManager.requestPermission(arduinoConnector.mUsbDevice, mPendingIntent);
    }

    /**
     ** Used to register android app events actions
     */
    private void setFilter() {
        IntentFilter filter = new IntentFilter();
        filter.addAction(ArduinoConnector.ACTION_USB_PERMISSION);
        filter.addAction(ArduinoConnector.ACTION_NEW_DATA_RECEIVED);
        filter.addAction(ArduinoConnector.ACTION_USB_DISCONNECTED);
        filter.addAction(ArduinoConnector.ACTION_USB_DEVICE_NOT_WORKING);
        filter.addAction(UsbManager.ACTION_USB_DEVICE_ATTACHED);
        filter.addAction(UsbManager.ACTION_USB_DEVICE_DETACHED);
        registerReceiver(eventRequestReceiver, filter);
    }

    /*
    ** Set the Roboto font on all TextView labels
     */
    private void setFontsOnTextViews()
    {
        FontHelper fontHelper = new FontHelper();
        fontHelper.setFont(lblBeanTemp);
        fontHelper.setFont(lblBeanRateOfRise, FontHelper.BOLD);
        fontHelper.setFont(lblEnvironmentTemp);
        fontHelper.setFont(lblEnvironRateOfRise, FontHelper.BOLD);
        fontHelper.setFont(lblRoastTime);
        fontHelper.setFont(lblTurnPointTemp);
        fontHelper.setFont(lblTurnPointTime, FontHelper.BOLD);
        fontHelper.setFont(lblFirstCrackTemp);
        fontHelper.setFont(lblFirstCrackTime, FontHelper.BOLD);
        fontHelper.setFont(lblDevelopmentPercentage);
        fontHelper.setFont(lblDevelopmentTime, FontHelper.BOLD);
        fontHelper.setFont(lblFullGas);
        fontHelper.setFont(lblDrumSpeed);
        fontHelper.setFont(lblFanSpeed);
        fontHelper.setFont(btnFirstCrack, FontHelper.BOLD);
    }

    /**
     * This deals with the connection status updating, placed in :
     * destroyUsbConnection() function
     * ACTION_USB_DEVICE_ATTACHED broadcast receiver action
     * ACTION_USB_PERMISSION broadcast receiver action
     */
    public void displayConnectionStatus(){
        if(ArduinoConnector.isConnected == true){

            TextView connectionView = (TextView)findViewById(R.id.appConnectionStatus);
            connectionView.setText("Connected");
            connectionView.setBackgroundColor(Color.rgb(0,100,0));
        }
        else{
            TextView connectionView = (TextView)findViewById(R.id.appConnectionStatus);
            connectionView.setText("Disconnected");
            connectionView.setBackgroundColor(Color.rgb(138, 0, 0));
        }

    }

}
