package com.kazazoom.genioroasters;

/**
 * Created by Given Mojapelo on 6/8/2016.
 */
public class AppSettingsData {

    //screen update time in milli seconds, set by the developer, used in the ScreenUpdaterService
    public final static int SCREEN_UPDATE_TIME = 4000;
    //vendor id of Arduino board
    public final static int ARDUINO_VENDOR_ID = 9025;
    //Configuration keys:
    private static final String SCREEN_UPDATE_TIME_KEY = "com.kazazoom.genioroasters.AppSettingsData.screen_update_time";
    //This is the value used to store/access settings for this app in android config memory:
    private static final String APP_PREFERENCE_ID = "com.kazazoom.genioroasters.AppSettingsData.preferences";   //must match the app directory


}
