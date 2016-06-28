package com.kazazoom.genioroasters;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.hardware.usb.UsbDeviceConnection;
import android.hardware.usb.UsbManager;
import android.widget.Toast;

import com.felhr.usbserial.UsbSerialDevice;

import java.util.TimerTask;

import static java.lang.Thread.sleep;

/**
 * Created by Alyssa Cloete on 6/13/2016.
 * Description: This helper sends a request to the board every 500ms
 */
public class DataSendingHelper extends TimerTask {

    private ArduinoConnector arduinoConnector;

    public DataSendingHelper(ArduinoConnector arduinoConnectorFromDashboard)
    {
        this.arduinoConnector = arduinoConnectorFromDashboard;
    }

    /**
     * Starts executing the active part of the class' code. This method is
     * called when a thread is started that has been created with a class which
     * implements {@code Runnable}.
     */
    @Override
    public void run() {
        try {
            this.arduinoConnector.sendDataToArduino("riaa123ab456");
            sleep(500);

        } catch (Exception ex)
        {
            ex.printStackTrace();
            arduinoConnector.writeLogFile("ERROR", ex.getMessage());
        }
    }
}
