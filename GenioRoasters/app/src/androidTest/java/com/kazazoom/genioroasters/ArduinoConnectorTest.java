package com.kazazoom.genioroasters;

import android.app.Application;
import android.hardware.usb.UsbDevice;
import android.hardware.usb.UsbDeviceConnection;
import android.hardware.usb.UsbManager;
import android.test.ApplicationTestCase;
import android.test.mock.MockContext;

import com.felhr.usbserial.UsbSerialDevice;

import junit.framework.Assert;

import org.junit.Test;

/**
 * <a href="http://d.android.com/tools/testing/testing_android.html">Testing Fundamentals</a>
 */
public class ArduinoConnectorTest extends ApplicationTestCase<Application> {
    public ArduinoConnectorTest() {
        super(Application.class);
    }

    //This test has to be run with the MainActivity

    public void createUsbManager(){

        //If mock context is not working, the real context can be used
        MockContext context = new MockContext();
        UsbManager usbManager = (UsbManager) context.getSystemService(MockContext.USB_SERVICE);
        //Not complete
    }

    @Test
    public void createArduinoConnectionTest(){

        ArduinoConnector connector = new ArduinoConnector();
        UsbDeviceConnection usbConnection = connector.mUsbConnection;
        UsbDevice usbDevice = connector.mUsbDevice;
        UsbSerialDevice serialPort = connector.serialPort;

        Assert.assertEquals(true, (usbConnection != null) ? true : false );
        Assert.assertEquals(true, (usbDevice != null ) ? true : false );
        Assert.assertEquals(true, (serialPort != null) ? true : false);
    }

    @Test
    public void TestSendingAndReceivingData(){

        ArduinoConnector connector = new ArduinoConnector();
        UsbSerialDevice serialPort = connector.serialPort;
        serialPort.write("request-info".getBytes());

        String data = connector.tokenizedString.toString();
        String text = "ga1234|gb3568|ra124|rb331|rm1215|rn121|ro0133|rp0159|rp895|rr1233|rs1856";

        Assert.assertEquals(text, data);
        Assert.assertEquals(true, data.contains(text));
    }



}