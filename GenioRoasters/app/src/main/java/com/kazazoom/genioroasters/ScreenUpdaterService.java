package com.kazazoom.genioroasters;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

public class ScreenUpdaterService extends Service implements Runnable {

    RoastData updatedRoastData;

    public ScreenUpdaterService() {
        updatedRoastData = RoastData.getInstance();
    }

    @Override
    public void onCreate()
    {
        startScreenUpdaterThread();
    }

    @Override
    public void onDestroy(){
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }

    /**
     * @Purpose: Starts the thread for this runnable extension
     */
    public void startScreenUpdaterThread(){

        try {
            Thread thread = new Thread(this);
            thread.start();
        }
        catch (Exception ex){
            ArduinoConnector arduinoConnector = new ArduinoConnector();
            arduinoConnector.writeLogFile("ArduinoConnector", ex.getMessage());
        }
    }

    /**
     * Starts executing the active part of the class' code. This method is
     * called when a thread is started that has been created with a class which
     * implements {@code Runnable}.
     */
    @Override
    public void run() {

        while(ArduinoConnector.isConnected){

            try {
                if (updatedRoastData.getIsNewDataStatus() == true) {
                    Intent intent = new Intent(ArduinoConnector.ACTION_NEW_DATA_RECEIVED);
                    intent.putExtra("UPDATED_ROAST_DATA", updatedRoastData);
                    DashBoardActivity.context.sendBroadcast(intent);
                    //resets the handshake fails to zero since we have connection
                    ArduinoConnector.HandShakeFails = 0;
                    updatedRoastData.setIsNewRoastData(false);
                }
                if(ArduinoConnector.HandShakeFails >= 10){
                    //If hand shake fails are greater that 10, then the app sends a disconnected status to the user:
                    Intent intent = new Intent(ArduinoConnector.ACTION_USB_DISCONNECTED);
                    DashBoardActivity.context.sendBroadcast(intent);
                }
            }
            catch (Exception ex){
               ArduinoConnector arduinoConnector = new ArduinoConnector();
                arduinoConnector.writeLogFile("ArduinoConnector", ex.getMessage());
            }

        }

    }
}
