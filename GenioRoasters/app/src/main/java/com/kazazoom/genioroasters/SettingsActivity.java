package com.kazazoom.genioroasters;

import android.os.Bundle;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.hardware.usb.UsbDevice;
import android.hardware.usb.UsbDeviceConnection;
import android.hardware.usb.UsbManager;
import android.app.Activity;
import android.support.v7.app.AppCompatActivity;
import android.text.InputType;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;
import android.support.v7.widget.Toolbar;

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
/*
** @author: Alyssa Cloete
** @description: This activity controls the actions on the settings screen to
** fascilitate as many actions as possible
 */

public class SettingsActivity extends AppCompatActivity{

    /*
    All UI elements will be represented in below attributes
     */
    private EditText txtPrimeTemp;
    private Button btnSavePrimeTemp;

    private Button btnRateOfRise10s;
    private Button btnRateOfRise15s;
    private Button btnRateOfRise20s;
    private Button btnRateOfRise30s;
    private Button btnRateOfRise60s;

    private TextView lblDrumSpeedMin;
    private TextView lblDrumSpeedMax;
    private SeekBar skbDrumSpeedMin;
    private SeekBar skbDrumSpeedMax;
    private TextView lblFanSpeedMin;
    private TextView lblFanSpeedMax;
    private SeekBar skbFanSpeedMin;
    private SeekBar skbFanSpeedMax;

    private RadioGroup radTemperature;
    private RadioButton radCelcius;
    private RadioButton radFahrenheit;

    private CheckBox isDHCP;

    private EditText txtEthernetIPFirst;
    private EditText txtEthernetIPSecond;
    private EditText txtEthernetIPThird;
    private EditText txtEthernetIPFourth;

    private Button btnSaveEthernetIP;

    private EditText txtEthernetGatewayFirst;
    private EditText txtEthernetGatewaySecond;
    private EditText txtEthernetGatewayThird;
    private EditText txtEthernetGatewayFourth;

    private Button btnSaveEthernetGateway;

    private TextView txtBeanRORUnit;
    private TextView txtEnvironRORUnit;
    private TextView lblBeanRateOfRiseUnit;
    private TextView lblEnvironRateOfRiseUnit;

    UsbManager usbManager;
    private ArduinoConnector arduinoConnector;
    public static Context context;
    private static int backButtonCountForExit = 0;

    private SettingsData settingsData;

    /*
    ** Define the PermissionRequestReceiver at this point to enable the stream to send data through using
    ** Android BroadcastReceiver
     */
    BroadcastReceiver PermissionRequestReceiver = new BroadcastReceiver()
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
                    break;

                    //If data is received
                    case ArduinoConnector.ACTION_NEW_DATA_RECEIVED:
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

    /*
    ** Executes when screen is entered
     */
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        //link activity the activity_settings_screen
        setContentView(R.layout.activity_settings_screen);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        arduinoConnector = new ArduinoConnector();
        //To allow users to escape the soft keyboard by touching on the screen, we use the layout's id
        LinearLayout settingsScreenLayout = (LinearLayout) findViewById(R.id.mainLayout);
        settingsScreenLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    //hide the soft keyboard
                    InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);
                } catch (Exception ex) {
                    arduinoConnector.writeLogFile("ERROR", "Could not hide keyboard due to " + ex.getMessage());
                }
            }
        });

      //  TabActionsListener.setTabNavigationActions(this);

        //Assign attributes to layout ui controls
        assignLayoutToAttributes();

        try {
            //Get the settings, if any
            settingsData = new SettingsData();
            settingsData.fetchValuesFromAndroidPreferences();
            checkAndGetSettingsFromSharedPreferences(settingsData);
        }
        catch(Exception ex)
        {
            arduinoConnector.writeLogFile("ERROR", "Android preferences not loading due to " + ex.getMessage());
        }

        //Set up device for output using
        SettingsActivity.context = getApplicationContext();
        setFilter();
        usbManager = (UsbManager) getSystemService(Context.USB_SERVICE);
        findSerialPortDevice();

        //Set up action listeners
        onPrimeTempChanged();
        onRateOfRiseSelected();
        onDrumSpeedMinChanged();
        onDrumSpeedMaxChanged();
        onFanSpeedMinChanged();
        onFanSpeedMaxChanged();
        onTemperatureFormatSelected();
        onDHCPChecked();
        onEthernetIPSelected();
        onEthernetGatewaySelected();

    }

    /**
     * This exit the app with double click to the back button
     */
    @Override
    public void onBackPressed()
    {
        if(backButtonCountForExit >= 1)
        {
            finish();
        }
        else
        {
            Toast.makeText(this, "Press the back button once again to close the application.", Toast.LENGTH_SHORT).show();
            backButtonCountForExit++;
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_home:
                //Toast.makeText(this, "please work, please please please", Toast.LENGTH_LONG).show();
                Intent intent = new Intent(DashBoardActivity.context, DashBoardActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK); //Used to start new activity from non-activity class:
                DashBoardActivity.context.startActivity(intent);
                // User chose the "Favorite" action, mark the current item
                // as a favorite...
                return true;

            default:
                // If we got here, the user's action was not recognized.
                // Invoke the superclass to handle it.
                return super.onOptionsItemSelected(item);
        }
    }

    /*
    ** Used to assign values from layout to activity attributes
     */
    private void assignLayoutToAttributes()
    {
        txtPrimeTemp = (EditText)findViewById(R.id.primeTemperatureTextbox);
        btnSavePrimeTemp = (Button) findViewById(R.id.savePrimeTemperatureButton);

        btnRateOfRise10s = (Button) findViewById(R.id.s10);
        btnRateOfRise15s = (Button) findViewById(R.id.s15);
        btnRateOfRise20s = (Button) findViewById(R.id.s20);
        btnRateOfRise30s = (Button) findViewById(R.id.s30);
        btnRateOfRise60s = (Button) findViewById(R.id.s60);


        lblDrumSpeedMin = (TextView) findViewById(R.id.drumSpeedMinLabel);
        lblDrumSpeedMax = (TextView) findViewById(R.id.drumSpeedMaxLabel);
        lblFanSpeedMin = (TextView) findViewById(R.id.fanSpeedMinLabel);
        lblFanSpeedMax = (TextView) findViewById(R.id.fanSpeedMaxLabel);
        skbDrumSpeedMin = (SeekBar) findViewById(R.id.drumSpeedMinSeekbar);
        skbDrumSpeedMax = (SeekBar) findViewById(R.id.drumSpeedMaxSeekbar);
        skbFanSpeedMin = (SeekBar) findViewById(R.id.fanSpeedMinSeekbar);
        skbFanSpeedMax = (SeekBar) findViewById(R.id.fanSpeedMaxSeekbar);

        radTemperature = (RadioGroup) findViewById(R.id.temperatureRadioButtonsGroup);
        radCelcius = (RadioButton) findViewById(R.id.temperatureCelciusRadioButton);
        radFahrenheit = (RadioButton) findViewById(R.id.temperatureFahrenheitRadioButton);

        txtEthernetIPFirst = (EditText)findViewById(R.id.firstEthernetIpTextbox);
        txtEthernetIPSecond = (EditText)findViewById(R.id.secondEthernetIpTextbox);
        txtEthernetIPThird = (EditText)findViewById(R.id.thirdEthernetIpTextbox);
        txtEthernetIPFourth = (EditText)findViewById(R.id.fourthEthernetIpTextbox);
        btnSaveEthernetIP = (Button) findViewById(R.id.saveIpSettingsButton);

        txtEthernetGatewayFirst = (EditText)findViewById(R.id.firstEthernetGatewayTextbox);
        txtEthernetGatewaySecond = (EditText)findViewById(R.id.secondEthernetGatewayTextbox);
        txtEthernetGatewayThird = (EditText)findViewById(R.id.thirdEthernetGatewayTextbox);
        txtEthernetGatewayFourth = (EditText)findViewById(R.id.fourthEthernetGatewayTextbox);
        btnSaveEthernetGateway = (Button) findViewById(R.id.saveGatewaySettingsButton);

        isDHCP = (CheckBox)findViewById(R.id.ethernetIsDhcpCheckbox);

        txtBeanRORUnit = (TextView)findViewById(R.id.beanRorUnit);
        txtEnvironRORUnit = (TextView)findViewById(R.id.environmentRorUnit);
    }

    /*
    ** This method filters through all the stored Android preferences and pulls them to display on screen if necessary
     */
    private void checkAndGetSettingsFromSharedPreferences(SettingsData settingsToFetch)
    {
        if (settingsToFetch.PRIME_TEMPERATURE != "")
        {
            txtPrimeTemp.setText("" + settingsToFetch.PRIME_TEMPERATURE + "");
        }

        if (settingsToFetch.RATE_OF_RISE != 0)
        {
            if (settingsToFetch.RATE_OF_RISE == 10)
            {
                select10sRateOfRise();
            }
            if (settingsToFetch.RATE_OF_RISE == 15)
            {
                select15sRateOfRise();
            }
            if (settingsToFetch.RATE_OF_RISE == 20)
            {
                select20sRateOfRise();
            }
            if (settingsToFetch.RATE_OF_RISE == 30)
            {
                select30sRateOfRise();
            }
            if (settingsToFetch.RATE_OF_RISE == 60)
            {
                select60sRateOfRise();
            }
        }

        if (settingsToFetch.DRUM_SPEED_MIN != 0)
        {
            //add 10 so it reflects correctly on the ui control
            int progress = settingsToFetch.DRUM_SPEED_MIN;
            skbDrumSpeedMin.setProgress(progress + 10);
            lblDrumSpeedMin.setText("" + progress);
        }
        else
        {
            lblDrumSpeedMin.setText("-10");
            skbDrumSpeedMin.setProgress(0);
        }

        if (settingsToFetch.DRUM_SPEED_MAX != 0)
        {
            //add 10 so it reflects correctly on the ui control
            int progress = settingsToFetch.DRUM_SPEED_MAX;
            skbDrumSpeedMax.setProgress(progress + 10);
            lblDrumSpeedMax.setText("" + progress);
        }
        else
        {
            lblDrumSpeedMax.setText("70");
            skbDrumSpeedMax.setProgress(80);
        }

        if (settingsToFetch.FAN_SPEED_MIN != 0)
        {
            //add 10 so it reflects correctly on the ui control
            int progress = settingsToFetch.FAN_SPEED_MIN;
            skbFanSpeedMin.setProgress(progress + 10);
            lblFanSpeedMin.setText("" + progress);
        }
        else
        {
            lblFanSpeedMin.setText("-10");
            skbFanSpeedMin.setProgress(0);
        }

        if (settingsToFetch.FAN_SPEED_MAX != 0)
        {
            //add 10 so it reflects correctly on the ui control
            int progress = settingsToFetch.FAN_SPEED_MAX;
            skbFanSpeedMax.setProgress(progress + 10);
            lblFanSpeedMax.setText("" + progress);
        }
        else
        {
            lblFanSpeedMax.setText("70");
            skbFanSpeedMax.setProgress(80);
        }

        if (settingsToFetch.TEMPERATURE_UNIT != "")
        {
            radTemperature.check(Integer.parseInt(settingsToFetch.TEMPERATURE_UNIT));
        }

        if (settingsToFetch.ETHERNET_IP != "")
        {
            String[] ethernetIPSegments = settingsToFetch.ETHERNET_IP.split("\\.");

            txtEthernetIPFirst.setText(ethernetIPSegments[0]);
            txtEthernetIPSecond.setText(ethernetIPSegments[1]);
            txtEthernetIPThird.setText(ethernetIPSegments[2]);
            txtEthernetIPFourth.setText(ethernetIPSegments[3]);
        }

        if (settingsToFetch.GATEWAY != "")
        {
            String[] ethernetGatewaySegments = settingsToFetch.GATEWAY.split("\\.");

            txtEthernetGatewayFirst.setText(ethernetGatewaySegments[0]);
            txtEthernetGatewaySecond.setText(ethernetGatewaySegments[1]);
            txtEthernetGatewayThird.setText(ethernetGatewaySegments[2]);
            txtEthernetGatewayFourth.setText(ethernetGatewaySegments[3]);
        }

        if (settingsToFetch.DHCP == true)
        {
            isDHCP.setChecked(true);
            disableEthernetSettings();
        }

        if (settingsToFetch.DHCP == false)
        {
            isDHCP.setChecked(false);
            enableEthernetSettings();
        }


        try {
            //Get the settings, if any
            settingsData = new SettingsData();
            settingsData.fetchValuesFromAndroidPreferences();

            //Bean rate of rise unit
            lblBeanRateOfRiseUnit.setText(Integer.toString(settingsData.RATE_OF_RISE));

            //Environment rate of rise unit
            lblEnvironRateOfRiseUnit.setText(Integer.toString(settingsData.RATE_OF_RISE));

        }
        catch(Exception ex)
        {
            arduinoConnector.writeLogFile("ERROR", "Android preferences not loading due to " + ex.getMessage());
        }




    }

    /*
    ** Creates the command to send through to the board
    ** @param commandType: specifies which command the request is for
    ** @param value: specifies the what to attach to the prefix
     */
    private String constructCommandToSend(String commandType, String value)
    {
        String command = "";

        //check the command type passed through and concatenate the value with the relative prefix/command
        switch(commandType)
        {
            case "PRIME_TEMP":
            {
                command = "cs" + value;
                break;
            }
            case "TEMP":
            {
                command = "rt" + value;
                break;
            }
            case "RATE_OF_RISE":
            {
                command = "ru" + value;
                break;
            }
            case "DRUM_SPEED_MIN":
            {
                command = "ae" + value;
                break;
            }
            case "DRUM_SPEED_MAX":
            {
                command = "ef" + value;
                break;
            }
            case "FAN_SPEED_MIN":
            {
                command = "aj" + value;
                break;
            }
            case "FAN_SPEED_MAX":
            {
                command = "ak" + value;
                break;
            }
            case "ETHERNET_IP":
            {
                command = "ry" + value;
                break;
            }
            case "ETHERNET_GATEWAY":
            {
                command = "rz" + value;
                break;
            }
            case "DHCP":
            {
                command = "dh" + value;
                break;
            }
        }

        return command;
    }

    /*
    ** Event listener which is registered to a change to send prime temperature command
     */
    private void onPrimeTempChanged()
    {
        txtPrimeTemp.setImeOptions(EditorInfo.IME_ACTION_DONE);
        //When the prime temperature textbox is double-clicked, the textbox should be editable
        txtPrimeTemp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                txtPrimeTemp.setFocusableInTouchMode(true);
            }
        });

        //When the prime temperature save button is clicked, validate the input and send the command
        btnSavePrimeTemp.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                validateAndSendPrimeTemperature(txtPrimeTemp);
            }
        });

        //Specify actions for if built-in keyboard action is clicked instead of save button
        txtPrimeTemp.setOnEditorActionListener(new EditText.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event)
            {
                 if (actionId == EditorInfo.IME_ACTION_DONE || actionId == EditorInfo.IME_ACTION_NEXT)
                {
                    validateAndSendPrimeTemperature(txtPrimeTemp);
                }
                return false;
            }
        });

    }

    /*
    ** Check that the prime temperature entered value is valid
     */
    private void validateAndSendPrimeTemperature(EditText txtPrimeTempToValidate)
    {
        try {
            //remove cursor from textbox
            txtPrimeTempToValidate.setFocusable(false);
            //hide soft keyboard
            InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);

            //check text using this regex
            String ipRegex = "\\d{0,3}(\\.\\d{1,2})?";
            Pattern regexPattern = Pattern.compile(ipRegex);
            Matcher regexMatcher = regexPattern.matcher(txtPrimeTempToValidate.getText());

            if (regexMatcher.matches()) {
                String textToProcess = txtPrimeTempToValidate.getText().toString();
                //attach relative command and send through to Arduino
                String command = constructCommandToSend("PRIME_TEMP", textToProcess);
                arduinoConnector.sendDataToArduino(command);
                //Set the Android preference
                settingsData.setPrimeTemperature(textToProcess);
            } else {
                Toast.makeText(getApplicationContext(), "Please input correct format: e.g 360 or 360.0", Toast.LENGTH_LONG).show();
            }
        }
        catch(Exception ex)
        {
            arduinoConnector.writeLogFile("ERROR", "Could not validate and store Prime Temperature due to " + ex.getMessage());
        }
    }

    /*
    ** Event listener which is registered to respond to movements of the seekbars for drum min
     */
    private void onDrumSpeedMinChanged()
    {
        try {
            //Execute actions when a change is picked up on the seekbars
            skbDrumSpeedMin.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
                @Override
                public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                    //Action events when the seekbar is being moved
                    //Ensure that the label updates accordingly based on how the values have been adjusted form 0
                    int newMonitoredValue = applyRangeFormula((skbDrumSpeedMin.getProgress()));
                    lblDrumSpeedMin.setText("" + newMonitoredValue + "");
                    int drumSpeedMax = skbDrumSpeedMax.getProgress();
                    //Ensure that the minimum can never exceed the maximum
                    if (skbDrumSpeedMin.getProgress() <= skbDrumSpeedMax.getProgress()) {
                        skbDrumSpeedMin.setProgress(progress);
                    } else {
                        skbDrumSpeedMin.setProgress(skbDrumSpeedMax.getProgress());
                    }
                }

                @Override
                public void onStartTrackingTouch(SeekBar seekBar) {
                    //Action events when the seekbar starts moving/touched
                }

                @Override
                public void onStopTrackingTouch(SeekBar seekBar) {
                    //Action events when the seekbar stops moving
                    //Make the new minimum of the maximum the progress of the minimum
                    //Only reset max if the minimum is set to more (should never occurr)
                    if (skbDrumSpeedMax.getProgress() < skbDrumSpeedMin.getProgress()) {
                        skbDrumSpeedMax.setProgress(skbDrumSpeedMin.getProgress());
                    }

                    //applyRangeFormula should ensure that while the seekbar is on 0, th user sees -10 and -10 is sent through in the command for example
                    String progress = "" + applyRangeFormula(skbDrumSpeedMin.getProgress()) + "";
                    String command = constructCommandToSend("DRUM_SPEED_MIN", progress);
                    arduinoConnector.sendDataToArduino(command);
                    //Store to Android preferences
                    settingsData.setDrumSpeedMin(Integer.parseInt(progress));
                }
            });
        }
        catch (Exception ex)
        {
            arduinoConnector.writeLogFile("ERROR", "Could not set drum speed min due to " + ex.getMessage());
        }
    }

    /*
    ** Event listener which is registered to respond to movements of the seekbars for drum max
     */
    private void onDrumSpeedMaxChanged()
    {
        try {
            //Actions when various events are detected on seekbar
            skbDrumSpeedMax.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
                @Override
                public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                    //When the seekbar is moving, keep in mind that the label needs to be updated
                    //maximum cannot be less than the minimum
                    int newMonitoredValue = applyRangeFormula((skbDrumSpeedMax.getProgress()));
                    lblDrumSpeedMax.setText("" + newMonitoredValue + "");
                    int drumSpeedMin = skbDrumSpeedMin.getProgress();

                    if (skbDrumSpeedMax.getProgress() >= skbDrumSpeedMin.getProgress()) {
                        skbDrumSpeedMax.setProgress(progress);
                    } else {
                        skbDrumSpeedMax.setProgress(skbDrumSpeedMin.getProgress());
                    }
                }

                @Override
                public void onStartTrackingTouch(SeekBar seekBar) {
                    //While the seekbar is moving
                }

                @Override
                public void onStopTrackingTouch(SeekBar seekBar) {
                    //When the seekbar stops moving/user lifts finger from trackball, send command
                    String progress = "" + applyRangeFormula(skbDrumSpeedMax.getProgress()) + "";
                    String command = constructCommandToSend("DRUM_SPEED_MAX", progress);
                    arduinoConnector.sendDataToArduino(command);

                    //Set the Android preference
                    settingsData.setDrumSpeedMax(Integer.parseInt(progress));
                }
            });
        }
        catch (Exception ex)
        {
            arduinoConnector.writeLogFile("ERROR", "The drum speed max could not be set due to " + ex.getMessage());
        }
    }

    /*
    ** Event listener which is registered to respond to movements of the seekbars
     */
    private void onFanSpeedMinChanged()
    {
        try {
            //Register movements of seekbars
            skbFanSpeedMin.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
                @Override
                public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                    //While seekbar is moving, update the label
                    int newMonitoredValue = applyRangeFormula((skbFanSpeedMin.getProgress()));
                    lblFanSpeedMin.setText("" + newMonitoredValue + "");
                    int fanSpeedMax = skbFanSpeedMax.getProgress();

                    //Ensure that the minimum is never greater than the maximum
                    if (skbFanSpeedMin.getProgress() <= skbFanSpeedMax.getProgress()) {
                        skbFanSpeedMin.setProgress(progress);
                    } else {
                        skbFanSpeedMin.setProgress(skbFanSpeedMax.getProgress());
                    }
                }

                @Override
                public void onStartTrackingTouch(SeekBar seekBar) {
                    //ensure the min pointer cannot exceed the max
                }

                @Override
                public void onStopTrackingTouch(SeekBar seekBar) {
                    //Make the new minimum of the maximum the progress of the minimum
                    //only reset max if the minimum is set to more
                    if (skbFanSpeedMax.getProgress() < skbFanSpeedMin.getProgress()) {
                        skbFanSpeedMax.setProgress(skbFanSpeedMin.getProgress());
                    }
                    String progress = "" + applyRangeFormula(skbFanSpeedMin.getProgress()) + "";
                    String command = constructCommandToSend("FAN_SPEED_MIN", progress);
                    arduinoConnector.sendDataToArduino(command);

                    //Set the Android preference
                    settingsData.setFanSpeedMin(Integer.parseInt(progress));
                }
            });
        }
        catch(Exception ex)
        {
            arduinoConnector.writeLogFile("ERROR", "Could not set fan speed min due to " + ex.getMessage());
        }
    }

    /*
    ** Event listener which is registered to respond to movements of the seekbars
     */
    private void onFanSpeedMaxChanged()
    {
        try {
            //Actions registered to movement of seekbars
            skbFanSpeedMax.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
                @Override
                public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                    //Update labels as seekbars are dragged
                    int newMonitoredValue = applyRangeFormula((skbFanSpeedMax.getProgress()));
                    lblFanSpeedMax.setText("" + newMonitoredValue + "");
                    int fanMin = skbFanSpeedMin.getProgress();

                    //maximum should always be greater than minimum
                    if (skbFanSpeedMax.getProgress() >= skbFanSpeedMin.getProgress()) {
                        skbFanSpeedMax.setProgress(progress);
                    } else {
                        skbFanSpeedMax.setProgress(skbFanSpeedMin.getProgress());
                    }
                }

                @Override
                public void onStartTrackingTouch(SeekBar seekBar) {
                    //Actions when seekbar is first touched
                }

                @Override
                public void onStopTrackingTouch(SeekBar seekBar) {
                    //Apply the formula to translate the Android seekbar 0 to -10 (example) as the user sees on the screen and send the command
                    String progress = "" + applyRangeFormula(skbFanSpeedMax.getProgress()) + "";
                    String command = constructCommandToSend("FAN_SPEED_MAX", progress);
                    arduinoConnector.sendDataToArduino(command);

                    //Set the Android preference
                    settingsData.setFanSpeedMax(Integer.parseInt(progress));
                }
            });
        }
        catch (Exception ex)
        {
            arduinoConnector.writeLogFile("ERROR", "Could not set the fan max due to " + ex.getMessage());
        }
    }

    /*
    ** Android seekbars can only have a minimum of 0 by defaut
    ** The client requirement is that the range moves between -10 and 70
    ** All labels thus reflect a start value of -10 and a maximum of 70
    ** In reality, the seekbar moves from 0 to 80
    ** Likewise, all values selected have this formula applied to remain in sync with the labels/what the user sees
     */
    private int applyRangeFormula(int initialValue)
    {
        return initialValue - 10;
    }

    /*
    ** Event listener which is registered to the selection of the typeof temperature to display on the Roast Data
     */
    private void onTemperatureFormatSelected()
    {
        try {
            //When a option is checked within the radio group, get the id of the checked option
            radTemperature.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(RadioGroup group, int checkedId) {
                    String command = "";
                    String isCelcius = "0";
                    String isFahrenheit = "1";
                    //Check whether celcius or farenheit was checked and send the relative command
                    switch (checkedId) {
                        case R.id.temperatureCelciusRadioButton:
                            command = constructCommandToSend("TEMP", isCelcius);
                            arduinoConnector.sendDataToArduino(command);
                            //Store in Android preferences
                            settingsData.setTemperatureUnit("" + checkedId + "");
                            break;
                        case R.id.temperatureFahrenheitRadioButton:
                            command = constructCommandToSend("TEMP", isFahrenheit);
                            arduinoConnector.sendDataToArduino(command);
                            //Store in Android preferences
                            settingsData.setTemperatureUnit("" + checkedId + "");
                            break;
                    }

                }
            });
        }
        catch (Exception ex)
        {
            arduinoConnector.writeLogFile("ERROR", "Could not set the temperature due to " + ex.getMessage());
        }
    }

    /*
    ** Event listener which is registered to clicks on the Rate of Rise
     */
    private void onRateOfRiseSelected()
    {
        try {
            //filter through buttons to check if any are already selected
            //change background color of unchanged buttons
            btnRateOfRise10s.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    select10sRateOfRise();
                    String command1 = constructCommandToSend("RATE_OF_RISE", "10");
                    arduinoConnector.sendDataToArduino(command1);
                    //Set the Android preference
                    settingsData.setRateOfRise(10);
                }
            });

            btnRateOfRise15s.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    select15sRateOfRise();
                    String command2 = constructCommandToSend("RATE_OF_RISE", "15");
                    arduinoConnector.sendDataToArduino(command2);
                    //Set the Android preference
                    settingsData.setRateOfRise(15);
                }
            });

            btnRateOfRise20s.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    select20sRateOfRise();
                    String command3 = constructCommandToSend("RATE_OF_RISE", "20");
                    arduinoConnector.sendDataToArduino(command3);
                    //Set the Android preference
                    settingsData.setRateOfRise(20);
                }
            });

            btnRateOfRise30s.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    select30sRateOfRise();
                    String command4 = constructCommandToSend("RATE_OF_RISE", "30");
                    arduinoConnector.sendDataToArduino(command4);
                    //Set the Android preference
                    settingsData.setRateOfRise(30);
                }
            });

            btnRateOfRise60s.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    select60sRateOfRise();
                    String command5 = constructCommandToSend("RATE_OF_RISE", "60");
                    arduinoConnector.sendDataToArduino(command5);
                    //Set the Android preference
                    settingsData.setRateOfRise(60);
                }
            });
        }
        catch (Exception ex)
        {
            arduinoConnector.writeLogFile("ERROR", "Could not set the rate of rise due to " + ex.getMessage());
        }
    }

    /*
    ** Select the 10s button on Rate of Rise
     */
    private void select10sRateOfRise()
    {
        btnRateOfRise10s.setBackgroundResource(R.color.buttonSelectionColor);
        btnRateOfRise15s.setBackgroundResource(R.color.backgroundColor);
        btnRateOfRise20s.setBackgroundResource(R.color.backgroundColor);
        btnRateOfRise30s.setBackgroundResource(R.color.backgroundColor);
        btnRateOfRise60s.setBackgroundResource(R.color.backgroundColor);
    }

    /*
    ** Select the 15s button on Rate of Rise
     */
    private void select15sRateOfRise()
    {
        btnRateOfRise10s.setBackgroundResource(R.color.backgroundColor);
        btnRateOfRise15s.setBackgroundResource(R.color.buttonSelectionColor);
        btnRateOfRise20s.setBackgroundResource(R.color.backgroundColor);
        btnRateOfRise30s.setBackgroundResource(R.color.backgroundColor);
        btnRateOfRise60s.setBackgroundResource(R.color.backgroundColor);
    }

    /*
    ** Select the 20s on Rate of Rise
     */
    private void select20sRateOfRise()
    {
        btnRateOfRise10s.setBackgroundResource(R.color.backgroundColor);
        btnRateOfRise15s.setBackgroundResource(R.color.backgroundColor);
        btnRateOfRise20s.setBackgroundResource(R.color.buttonSelectionColor);
        btnRateOfRise30s.setBackgroundResource(R.color.backgroundColor);
        btnRateOfRise60s.setBackgroundResource(R.color.backgroundColor);
    }

    /*
    ** Select the 30s on Rate of Rise
     */
    private void select30sRateOfRise()
    {
        btnRateOfRise10s.setBackgroundResource(R.color.backgroundColor);
        btnRateOfRise15s.setBackgroundResource(R.color.backgroundColor);
        btnRateOfRise20s.setBackgroundResource(R.color.backgroundColor);
        btnRateOfRise30s.setBackgroundResource(R.color.buttonSelectionColor);
        btnRateOfRise60s.setBackgroundResource(R.color.backgroundColor);
    }

    /*
    ** Select the 60s button on Rate of Rise
     */
    private void select60sRateOfRise()
    {
        btnRateOfRise10s.setBackgroundResource(R.color.backgroundColor);
        btnRateOfRise15s.setBackgroundResource(R.color.backgroundColor);
        btnRateOfRise20s.setBackgroundResource(R.color.backgroundColor);
        btnRateOfRise30s.setBackgroundResource(R.color.backgroundColor);
        btnRateOfRise60s.setBackgroundResource(R.color.buttonSelectionColor);
    }

    /*
    ** Used when the IP and Gateway is input as a single text command
     */
    private String checkAndAddPaddingToAddress(String valueToCheck)
    {
        String valueToReturn = "";

        if (valueToCheck.length() == 1)
        {
            valueToReturn = "00" + valueToCheck;
        }
        else if (valueToCheck.length() == 2)
        {
            valueToReturn = "0" + valueToCheck;
        }
        else if (valueToCheck.length() == 3)
        {
            valueToReturn = valueToCheck;
        }

        return valueToReturn;
    }

    /*
    ** Event listener which is registered to a change in the layout element to send ethernet IP
     */
    private void onEthernetIPSelected()
    {
        try {
            txtEthernetIPFirst.setImeOptions(EditorInfo.IME_ACTION_NEXT);
            txtEthernetIPSecond.setImeOptions(EditorInfo.IME_ACTION_NEXT);
            txtEthernetIPThird.setImeOptions(EditorInfo.IME_ACTION_NEXT);
            txtEthernetIPFourth.setImeOptions(EditorInfo.IME_ACTION_DONE);

            //Set action when textboxes are clicked
            txtEthernetIPFirst.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    txtEthernetIPFirst.setFocusableInTouchMode(true);
                }
            });

            txtEthernetIPSecond.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    txtEthernetIPSecond.setFocusableInTouchMode(true);
                }
            });

            txtEthernetIPThird.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    txtEthernetIPThird.setFocusableInTouchMode(true);
                }
            });

            txtEthernetIPFourth.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    txtEthernetIPFourth.setCursorVisible(true);
                    txtEthernetIPFourth.setFocusableInTouchMode(true);
                }
            });


            //Actions related to Keyboard built-in functionality (IME Options)
            txtEthernetIPFirst.setOnEditorActionListener(new EditText.OnEditorActionListener() {
                @Override
                public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                    if (actionId == EditorInfo.IME_ACTION_NEXT) {
                        //move to next textbox as Android does not detect the correct textbox as "Next" by default
                        txtEthernetIPSecond.setFocusable(true);
                    }
                    return false;
                }
            });

            txtEthernetIPSecond.setOnEditorActionListener(new EditText.OnEditorActionListener() {
                @Override
                public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                    if (actionId == EditorInfo.IME_ACTION_NEXT) {
                        //move to next textbox as Android does not detect the correct textbox as "Next" by default
                        txtEthernetIPThird.setFocusable(true);
                    }
                    return false;
                }
            });

            txtEthernetIPThird.setOnEditorActionListener(new EditText.OnEditorActionListener() {
                @Override
                public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                    if (actionId == EditorInfo.IME_ACTION_NEXT) {
                        //move to next textbox as Android does not detect the correct textbox as "Next" by default
                        txtEthernetIPFourth.setCursorVisible(true);
                        txtEthernetIPFourth.setFocusable(true);
                    }
                    return false;
                }
            });

            txtEthernetIPFourth.setOnEditorActionListener(new EditText.OnEditorActionListener() {
                @Override
                public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                    if (actionId == EditorInfo.IME_ACTION_DONE) {

                        //check if all inputs are complete and then send commands
                        validateAndSendIP(txtEthernetIPFirst, txtEthernetIPSecond, txtEthernetIPThird, txtEthernetIPFourth);

                    }
                    return false;
                }
            });

            //Set the action when the save button for the IP is clicked
            btnSaveEthernetIP.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    //check if all inputs are complete and then send commands
                    validateAndSendIP(txtEthernetIPFirst, txtEthernetIPSecond, txtEthernetIPThird, txtEthernetIPFourth);
                }
            });
        }
        catch (Exception ex)
        {
            arduinoConnector.writeLogFile("ERROR", "Could not set EthernetIP due to " + ex.getMessage());
        }
    }

    /*
    ** Validate the IP address before sending to Arudino
     */
    private void validateAndSendIP(EditText firstIPToValidate, EditText secondIPToValidate, EditText thirdIPToValidate, EditText fourthIPToValidate)
    {
        try {
            //remove cursor
            fourthIPToValidate.setCursorVisible(false);

            //hide the soft keyboard
            InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);

            String firstIP = firstIPToValidate.getText().toString();
            String secondIP = secondIPToValidate.getText().toString();
            String thirdIP = thirdIPToValidate.getText().toString();
            String fourthIP = fourthIPToValidate.getText().toString();

            String firstIPSegment = checkAndAddPaddingToAddress(firstIP);
            String secondIPSegment = checkAndAddPaddingToAddress(secondIP);
            String thirdIPSegment = checkAndAddPaddingToAddress(thirdIP);
            String fourthIPSegment = checkAndAddPaddingToAddress(fourthIP);

            if ((firstIPSegment != "" || firstIPSegment != null) && (secondIPSegment != "" || secondIPSegment != null) && (thirdIPSegment != "" || thirdIPSegment != null) && (fourthIPSegment != "" || fourthIPSegment != null)) {
                try {
                    //cast values to integer and check that they are within range 0-255
                    if (((Integer.parseInt(firstIPSegment) >= 0) && (Integer.parseInt(firstIPSegment) <= 255)) && ((Integer.parseInt(secondIPSegment) >= 0) && (Integer.parseInt(secondIPSegment) <= 255)) && ((Integer.parseInt(thirdIPSegment) >= 0) && (Integer.parseInt(thirdIPSegment) <= 255)) && ((Integer.parseInt(fourthIPSegment) >= 0) && (Integer.parseInt(fourthIPSegment) <= 255))) {

                        String reconstructedIPWithPadding = firstIPSegment + "." + secondIPSegment + "." + thirdIPSegment + "." + fourthIPSegment;

                        String command = constructCommandToSend("ETHERNET_IP", reconstructedIPWithPadding);
                        arduinoConnector.sendDataToArduino(command);
                        settingsData.setEthernetIp(reconstructedIPWithPadding);
                    } else {
                        Toast.makeText(getApplicationContext(), "Please check all your values are between 0 and 255", Toast.LENGTH_LONG).show();
                    }
                } catch (Exception ex) {
                    Toast.makeText(getApplicationContext(), "Not set. Please check values and save again", Toast.LENGTH_LONG).show();
                }
            } else {
                Toast.makeText(getApplicationContext(), "Please ensure you have completed all fields", Toast.LENGTH_LONG).show();
            }
        }
        catch (Exception ex)
        {
            arduinoConnector.writeLogFile("ERROR", "Could not validate Ethernet IP due to " + ex.getMessage());
        }

    }
    /*
    ** Event listener which is registered to a change in the layout element to send ethernet Gateway
     */
    private void onEthernetGatewaySelected()
    {
        try {
            txtEthernetGatewayFirst.setImeOptions(EditorInfo.IME_ACTION_NEXT);
            txtEthernetGatewaySecond.setImeOptions(EditorInfo.IME_ACTION_NEXT);
            txtEthernetGatewayThird.setImeOptions(EditorInfo.IME_ACTION_NEXT);
            txtEthernetGatewayFourth.setImeOptions(EditorInfo.IME_ACTION_DONE);

            //Set action when textboxes are clicked
            txtEthernetGatewayFirst.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    txtEthernetGatewayFirst.setFocusableInTouchMode(true);
                }
            });

            txtEthernetGatewaySecond.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    txtEthernetGatewaySecond.setFocusableInTouchMode(true);
                }
            });

            txtEthernetGatewayThird.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    txtEthernetGatewayThird.setFocusableInTouchMode(true);
                }
            });

            txtEthernetGatewayFourth.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    txtEthernetGatewayFourth.setCursorVisible(true);
                    txtEthernetGatewayFourth.setFocusableInTouchMode(true);
                }
            });


            //Actions related to Keyboard built-in functionality (IME Options)
            txtEthernetGatewayFirst.setOnEditorActionListener(new EditText.OnEditorActionListener() {
                @Override
                public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                    if (actionId == EditorInfo.IME_ACTION_NEXT) {
                        //move to next textbox as Android does not detect the correct textbox as "Next" by default
                        txtEthernetGatewaySecond.setFocusable(true);
                    }
                    return false;
                }
            });

            txtEthernetGatewaySecond.setOnEditorActionListener(new EditText.OnEditorActionListener() {
                @Override
                public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                    if (actionId == EditorInfo.IME_ACTION_NEXT) {
                        //move to next textbox as Android does not detect the correct textbox as "Next" by default
                        txtEthernetGatewayThird.setFocusable(true);
                    }
                    return false;
                }
            });

            txtEthernetGatewayThird.setOnEditorActionListener(new EditText.OnEditorActionListener() {
                @Override
                public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                    if (actionId == EditorInfo.IME_ACTION_NEXT) {
                        //move to next textbox as Android does not detect the correct textbox as "Next" by default
                        txtEthernetGatewayFourth.setCursorVisible(true);
                        txtEthernetGatewayFourth.setFocusable(true);
                    }
                    return false;
                }
            });

            txtEthernetGatewayFourth.setOnEditorActionListener(new EditText.OnEditorActionListener() {
                @Override
                public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                    if (actionId == EditorInfo.IME_ACTION_DONE) {

                        //check if all inputs are complete and then send commands
                        validateAndSendGateway(txtEthernetGatewayFirst, txtEthernetGatewaySecond, txtEthernetGatewayThird, txtEthernetGatewayFourth);

                    }
                    return false;
                }
            });

            //Set the action when the save button for the IP is clicked
            btnSaveEthernetGateway.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    //check if all inputs are complete and then send commands
                    validateAndSendGateway(txtEthernetGatewayFirst, txtEthernetGatewaySecond, txtEthernetGatewayThird, txtEthernetGatewayFourth);
                }
            });
        }
        catch (Exception ex)
        {
            arduinoConnector.writeLogFile("ERROR", "Could not set the Ethernet Gateway due to " + ex.getMessage());
        }
    }

    /*
    ** Validate the ethernet gateway before sending to Arduino
     */
    private void validateAndSendGateway(EditText firstGatewayToValidate, EditText secondGatewayToValidate, EditText thirdGatewayToValidate, EditText fourthGatewayToValidate)
    {
        try {
            //remove the cursor
            fourthGatewayToValidate.setCursorVisible(false);

            //hide the soft keyboard
            InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);

            String firstGateway = firstGatewayToValidate.getText().toString();
            String secondGateway = secondGatewayToValidate.getText().toString();
            String thirdGateway = thirdGatewayToValidate.getText().toString();
            String fourthGateway = fourthGatewayToValidate.getText().toString();

            String firstGatewaySegment = checkAndAddPaddingToAddress(firstGateway);
            String secondGatewaySegment = checkAndAddPaddingToAddress(secondGateway);
            String thirdGatewaySegment = checkAndAddPaddingToAddress(thirdGateway);
            String fourthGatewaySegment = checkAndAddPaddingToAddress(fourthGateway);

            if ((firstGatewaySegment != "" || firstGatewaySegment != null) && (secondGatewaySegment != "" || secondGatewaySegment != null) && (thirdGatewaySegment != "" || thirdGatewaySegment != null) && (fourthGatewaySegment != "" || fourthGatewaySegment != null)) {
                try {
                    //cast values to integer and check that they are within range 0-255
                    if (((Integer.parseInt(firstGatewaySegment) >= 0) && (Integer.parseInt(firstGatewaySegment) <= 255)) && ((Integer.parseInt(secondGatewaySegment) >= 0) && (Integer.parseInt(secondGatewaySegment) <= 255)) && ((Integer.parseInt(thirdGatewaySegment) >= 0) && (Integer.parseInt(thirdGatewaySegment) <= 255)) && ((Integer.parseInt(fourthGatewaySegment) >= 0) && (Integer.parseInt(fourthGatewaySegment) <= 255))) {

                        String reconstructedGatewayWithPadding = firstGatewaySegment + "." + secondGatewaySegment + "." + thirdGatewaySegment + "." + fourthGatewaySegment;

                        String command = constructCommandToSend("ETHERNET_GATEWAY", reconstructedGatewayWithPadding);
                        arduinoConnector.sendDataToArduino(command);
                        settingsData.setGateWay(reconstructedGatewayWithPadding);
                    } else {
                        Toast.makeText(getApplicationContext(), "Please check all your values are between 0 and 255", Toast.LENGTH_LONG).show();
                    }
                } catch (Exception ex) {
                    Toast.makeText(getApplicationContext(), "Not set. Please check values and save again", Toast.LENGTH_LONG).show();
                }
            } else {
                Toast.makeText(getApplicationContext(), "Please ensure you have completed all fields", Toast.LENGTH_LONG).show();
            }
        }
        catch (Exception ex)
        {
            arduinoConnector.writeLogFile("ERROR", "Could not validate Ethernet gateway due to " + ex.getMessage());
        }
    }

    /*
    ** Event listener which is registered to the selection of the DHCP checkbox
     */
    private void onDHCPChecked()
    {
        try {
            //When the DHCP is selected, the Ethernet settings should be disabled and the relevant command should be sent based on the status of the check
            isDHCP.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    if (isChecked == true) {
                        String command = constructCommandToSend("DHCP", "1");
                        arduinoConnector.sendDataToArduino(command);

                        disableEthernetSettings();
                        settingsData.setDHCP(true);
                    } else {
                        String command = constructCommandToSend("DHCP", "0");
                        arduinoConnector.sendDataToArduino(command);

                        enableEthernetSettings();
                        settingsData.setDHCP(false);
                    }

                }
            });
        }
        catch(Exception ex)
        {
            arduinoConnector.writeLogFile("ERROR", "Could not set DHCP due to " + ex.getMessage());
        }
    }

    /*
    ** Used when selecting the DHCP
     */
    private void disableEthernetSettings()
    {
        try {
            //first disable IP Settings
            txtEthernetIPFirst.setEnabled(false);
            txtEthernetIPFirst.setInputType(InputType.TYPE_NULL);
            txtEthernetIPFirst.setFocusable(false);

            txtEthernetIPSecond.setEnabled(false);
            txtEthernetIPSecond.setInputType(InputType.TYPE_NULL);
            txtEthernetIPSecond.setFocusable(false);

            txtEthernetIPThird.setEnabled(false);
            txtEthernetIPThird.setInputType(InputType.TYPE_NULL);
            txtEthernetIPThird.setFocusable(false);

            txtEthernetIPFourth.setEnabled(false);
            txtEthernetIPFourth.setInputType(InputType.TYPE_NULL);
            txtEthernetIPFourth.setFocusable(false);

            btnSaveEthernetIP.setEnabled(false);
            btnSaveEthernetIP.setBackgroundResource(R.color.buttonSelectionColor);

            txtEthernetGatewayFirst.setEnabled(false);
            txtEthernetGatewayFirst.setInputType(InputType.TYPE_NULL);
            txtEthernetGatewayFirst.setFocusable(false);

            txtEthernetGatewaySecond.setEnabled(false);
            txtEthernetGatewaySecond.setInputType(InputType.TYPE_NULL);
            txtEthernetGatewaySecond.setFocusable(false);

            txtEthernetGatewayThird.setEnabled(false);
            txtEthernetGatewayThird.setInputType(InputType.TYPE_NULL);
            txtEthernetGatewayThird.setFocusable(false);

            txtEthernetGatewayFourth.setEnabled(false);
            txtEthernetGatewayFourth.setInputType(InputType.TYPE_NULL);
            txtEthernetGatewayFourth.setFocusable(false);

            btnSaveEthernetGateway.setEnabled(false);
            btnSaveEthernetGateway.setBackgroundResource(R.color.buttonSelectionColor);
        }
        catch (Exception ex)
        {
            arduinoConnector.writeLogFile("ERROR", "Could not disable Ethernet settings due to " + ex.getMessage());
        }
    }

    /*
    ** Used when deselecting the DHCP
     */
    private void enableEthernetSettings()
    {
        try {
            //first disable IP Settings
            txtEthernetIPFirst.setEnabled(true);
            txtEthernetIPFirst.setInputType(InputType.TYPE_CLASS_NUMBER);
            txtEthernetIPFirst.setFocusable(true);

            txtEthernetIPSecond.setEnabled(true);
            txtEthernetIPSecond.setInputType(InputType.TYPE_CLASS_NUMBER);
            txtEthernetIPSecond.setFocusable(true);

            txtEthernetIPThird.setEnabled(true);
            txtEthernetIPThird.setInputType(InputType.TYPE_CLASS_NUMBER);
            txtEthernetIPThird.setFocusable(true);

            txtEthernetIPFourth.setEnabled(true);
            txtEthernetIPFourth.setInputType(InputType.TYPE_CLASS_NUMBER);
            txtEthernetIPFourth.setFocusable(true);

            btnSaveEthernetIP.setEnabled(true);
            btnSaveEthernetIP.setBackgroundResource(R.color.sectionLabelsColor);

            txtEthernetGatewayFirst.setEnabled(true);
            txtEthernetGatewayFirst.setInputType(InputType.TYPE_CLASS_NUMBER);
            txtEthernetGatewayFirst.setFocusable(true);

            txtEthernetGatewaySecond.setEnabled(true);
            txtEthernetGatewaySecond.setInputType(InputType.TYPE_CLASS_NUMBER);
            txtEthernetGatewaySecond.setFocusable(true);

            txtEthernetGatewayThird.setEnabled(true);
            txtEthernetGatewayThird.setInputType(InputType.TYPE_CLASS_NUMBER);
            txtEthernetGatewayThird.setFocusable(true);

            txtEthernetGatewayFourth.setEnabled(true);
            txtEthernetGatewayFourth.setInputType(InputType.TYPE_CLASS_NUMBER);
            txtEthernetGatewayFourth.setFocusable(true);

            btnSaveEthernetGateway.setEnabled(true);
            btnSaveEthernetGateway.setBackgroundResource(R.color.sectionLabelsColor);
        }
        catch (Exception ex)
        {
            arduinoConnector.writeLogFile("ERROR", "Could not enable Ethernet settings due to " + ex.getMessage());
        }
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
                requestUserPermission();
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
        registerReceiver(PermissionRequestReceiver, filter);
    }

    @Override
    public void onDestroy(){
        super.onDestroy();
        unregisterReceiver(PermissionRequestReceiver);
    }

}
