package com.kazazoom.genioroasters;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * Created by Alyssa Cloete on 5/19/2016.
 */
public class SettingsData {


    public static String PRIME_TEMPERATURE;
    public static String TEMPERATURE_UNIT;
    public static int RATE_OF_RISE;
    public static int DRUM_SPEED_MIN;
    public static int DRUM_SPEED_MAX;
    public static int FAN_SPEED_MIN;
    public static int FAN_SPEED_MAX;
    public static String ETHERNET_IP;
    public static String GATEWAY;
    public static boolean DHCP;

    //Configuration keys:
    private static final String PRIME_TEMPERATURE_KEY = "com.kazazoom.genioroasters.SettingsData.prime_temperature";
    private static final String TEMPERATURE_UNIT_KEY = "com.kazazoom.genioroasters.SettingsData.temperature_unit.";
    private static final String RATE_OF_RISE_KEY = "com.kazazoom.genioroasters.SettingsData.rate_of_rise";
    private static final String DRUM_SPEED_MIN_KEY = "com.kazazoom.genioroasters.SettingsData.drum_speed_min";
    private static final String DRUM_SPEED_MAX_KEY = "com.kazazoom.genioroasters.SettingsData.drum_speed_max";
    private static final String FAN_SPEED_MIN_KEY = "com.kazazoom.genioroasters.SettingsData.fan_speed_min";
    private static final String FAN_SPEED_MAX_KEY = "com.kazazoom.genioroasters.SettingsData.fan_speed_max";
    private static final String ETHERNET_IP_KEY = "com.kazazoom.genioroasters.SettingsData.ethernet_ip";
    private static final String GATEWAY_KEY = "com.kazazoom.genioroasters.SettingsData.gateway";
    private static final String DHCP_KEY = "com.kazazoom.genioroasters.SettingsData.dhcp";
    //This is the value used to store/access settings for this app in android config memory:
    private static final String APP_PREFERENCE_ID = "com.kazazoom.genioroasters.SettingsData.preferences";   //must match the app directory


    /**
     * @Purpose: Fetches the values stored in android shared preferences to the app memory
     */
    public void fetchValuesFromAndroidPreferences(){
        Context context = DashBoardActivity.context;
        SharedPreferences settings = context.getSharedPreferences(APP_PREFERENCE_ID, Context.MODE_PRIVATE);

        PRIME_TEMPERATURE = settings.getString(PRIME_TEMPERATURE_KEY, "");
        TEMPERATURE_UNIT  = settings.getString(TEMPERATURE_UNIT_KEY, "00");
        RATE_OF_RISE      = Integer.parseInt(settings.getString(RATE_OF_RISE_KEY, "00"));
        DRUM_SPEED_MIN    = Integer.parseInt(settings.getString(DRUM_SPEED_MIN_KEY, "00"));
        DRUM_SPEED_MAX    = Integer.parseInt(settings.getString(DRUM_SPEED_MAX_KEY, "00"));
        FAN_SPEED_MIN     = Integer.parseInt(settings.getString(FAN_SPEED_MIN_KEY, "00"));
        FAN_SPEED_MAX     = Integer.parseInt(settings.getString(FAN_SPEED_MAX_KEY, "00"));
        ETHERNET_IP       = settings.getString(ETHERNET_IP_KEY, "");
        GATEWAY           = settings.getString(GATEWAY_KEY, "");
        DHCP              = settings.getBoolean(DHCP_KEY, false);
    }

    /**
     * @Param: prime temperature value set from the settingsActivity
     * @Purpose: Updates this value to android shared preferences memory
     */
    public static void setPrimeTemperature(String primeTemperature)
    {
        try {
            Context context = DashBoardActivity.context;
            SharedPreferences settings = context.getSharedPreferences(APP_PREFERENCE_ID, Context.MODE_PRIVATE);

            if (settings != null) {
                SharedPreferences.Editor editor = settings.edit();
                editor.putString(PRIME_TEMPERATURE_KEY, primeTemperature);
                editor.commit();
            }
        }
        catch (Exception ex)
        {}
    }

    /**
     * @Param: temperature unit(C or F) set from the settingsActivity
     * @Purpose: Updates this value to the android shared preferences memory
     */
    public static void setTemperatureUnit(String temperatureUnit)
    {
        try {
            Context context = DashBoardActivity.context;
            SharedPreferences settings = context.getSharedPreferences(APP_PREFERENCE_ID, Context.MODE_PRIVATE);

            if (settings != null) {
                SharedPreferences.Editor editor = settings.edit();
                editor.putString(TEMPERATURE_UNIT_KEY, temperatureUnit);
                editor.commit();
            }
        }
        catch (Exception ex)
        {}
    }

    /**
     * @Param: Rate of rise value set from the settingsActivity
     * @Purpose: Updates this value to the android shared preferences memory
     */
    public static void setRateOfRise(int rateOfRise)
    {
        try {
            Context context = DashBoardActivity.context;
            SharedPreferences settings = context.getSharedPreferences(APP_PREFERENCE_ID, Context.MODE_PRIVATE);

            if (settings != null) {
                SharedPreferences.Editor editor = settings.edit();
                editor.putString(RATE_OF_RISE_KEY, Integer.toString(rateOfRise));
                editor.commit();
            }
        }
        catch(Exception ex)
        {}
    }

    /**
     * @param: drum speed minimum value set from the settingsActivity
     * @Purpose: Updates this value to the android shared preferences memory
     */
    public static void setDrumSpeedMin(int drumSpeedMin)
    {
        try {
            Context context = DashBoardActivity.context;
            SharedPreferences settings = context.getSharedPreferences(APP_PREFERENCE_ID, Context.MODE_PRIVATE);

            if (settings != null) {
                SharedPreferences.Editor editor = settings.edit();
                editor.putString(DRUM_SPEED_MIN_KEY, Integer.toString(drumSpeedMin));
                editor.commit();
            }
        }
        catch (Exception ex)
        {}
    }

    /**
     * @Param: drum speed max value set from the settingsActivity
     * @Purpose: Updates this value to the android shared preferences memory
     */
    public static void setDrumSpeedMax(int drumSpeedMax)
    {
        try {
            Context context = DashBoardActivity.context;
            SharedPreferences settings = context.getSharedPreferences(APP_PREFERENCE_ID, Context.MODE_PRIVATE);

            if (settings != null) {
                SharedPreferences.Editor editor = settings.edit();
                editor.putString(DRUM_SPEED_MAX_KEY, Integer.toString(drumSpeedMax));
                editor.commit();
            }
        }
        catch (Exception ex)
        {}
    }

    /**
     * @Param: fan speed minimum value set from the settingsActivity
     * @Purpose: Updates this value to the android shared preferences memory
     */
    public static void setFanSpeedMin(int fanSpeedMin)
    {
        try {
            Context context = DashBoardActivity.context;
            SharedPreferences settings = context.getSharedPreferences(APP_PREFERENCE_ID, Context.MODE_PRIVATE);

            if (settings != null) {
                SharedPreferences.Editor editor = settings.edit();
                editor.putString(FAN_SPEED_MIN_KEY, Integer.toString(fanSpeedMin));
                editor.commit();
            }
        }
        catch (Exception ex)
        {}
    }

    /**
     * @Param: fan speed max value set from the settingsActivity
     * @Purpose: Updates this value to the android shared preferences memory
     */
    public static void setFanSpeedMax(int fanSpeedMax)
    {
        try {
            Context context = DashBoardActivity.context;
            SharedPreferences settings = context.getSharedPreferences(APP_PREFERENCE_ID, Context.MODE_PRIVATE);

            if (settings != null) {
                SharedPreferences.Editor editor = settings.edit();
                editor.putString(FAN_SPEED_MAX_KEY, Integer.toString(fanSpeedMax));
                editor.commit();
            }
        }
        catch (Exception ex)
        {}
    }

    /**
     * @Param: ethernet String set from settingsActivity
     * @Purpose: Updates this value to the android shared preferences memory
     */
    public static void setEthernetIp(String ethernetIp)
    {
        try {
            Context context = DashBoardActivity.context;
            SharedPreferences settings = context.getSharedPreferences(APP_PREFERENCE_ID, Context.MODE_PRIVATE);

            if (settings != null) {
                SharedPreferences.Editor editor = settings.edit();
                editor.putString(ETHERNET_IP_KEY, ethernetIp);
                editor.commit();
            }
        }
        catch (Exception ex)
        {}
    }

    /**
     * @Param: gateway string set from the settingsActivity
     * @Purpose: Updates this value to the android shared preferences memory
     */
    public static void setGateWay(String gateway)
    {
        try {
            Context context = DashBoardActivity.context;
            SharedPreferences settings = context.getSharedPreferences(APP_PREFERENCE_ID, Context.MODE_PRIVATE);

            if (settings != null) {
                SharedPreferences.Editor editor = settings.edit();
                editor.putString(GATEWAY_KEY, gateway);
                editor.commit();
            }
        }
        catch (Exception ex)
        {}
    }

    /**
     * @Param: dhcp boolean set from the settingsActivity
     * @Purpose: Updates this value to the android shared preferences memory
     */
    public static void setDHCP(boolean dhcpState)
    {
        try {
            Context context = DashBoardActivity.context;
            SharedPreferences settings = context.getSharedPreferences(APP_PREFERENCE_ID, Context.MODE_PRIVATE);

            if (settings != null) {
                SharedPreferences.Editor editor = settings.edit();
                editor.putBoolean(DHCP_KEY, dhcpState);
                editor.commit();
            }
        }
        catch(Exception ex)
        {}
    }

}
