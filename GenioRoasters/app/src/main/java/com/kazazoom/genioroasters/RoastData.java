package com.kazazoom.genioroasters;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Serializable;

/**
 * Created by Alyssa Cloete on 5/19/2016.
 * Representation of data to be displayed on dashboard screen
 */
public class RoastData implements Serializable{
    private static RoastData roastDataInstance = null;
    private String beanTemp;
    private String environmentTemp;
    private String beanRateOfRise;
    private String environmentRateOfRise;
    private String roastTime;
    private String developmentTime;
    private String developmentPercentage;
    private String turnPointTemp;
    private String turnPointTime;
    private String firstCrackTime;
    private String firstCrackTemp;
    private String fullGas;
    private String fanSpeed;
    private String drumSpeed;
    private String currentLoadedData;
    private boolean isNewRoastData;

    /*
    ** Maintain state of Singleton instance
    ** If an instance of RoastData exists in memory, preserve it by using current instance
     */
    public static RoastData getInstance()
    {
        if(roastDataInstance == null)
        {
            roastDataInstance = new RoastData();
        }
        return roastDataInstance;
    }

    private RoastData() {
    }

    /**
     * @Param: Data from the Arduino machine
     * @Purpose: Used to validate the supplied data by checking the checksum
     * @Returns: whether the supplied data is valid or not
     * @Checksum: checks whether first and last data characters are integers
     *            that cancel each other such that (last-first = 0)
     */
    public boolean validateTokenizedDataWithChecksum(String data)
    {
        boolean dataValid = false;

        try {
            //get the prefix and suffix of the data packet
            String prefix = data.substring(0, 1);
            String firstDataPacketLetter = data.substring(0, 3);
            String suffix = data.substring(data.length() - 1, data.length());

            char charPrefix = prefix.charAt(0);
            char charSuffix = suffix.charAt(0);

            //try to perform conversion (incomplete data packets can cause an app crash thus must remain in try-catch)
            try {
                //get the integer value of the prefix and suffix
                int asciiPrefix = Integer.valueOf(charPrefix);
                int asciiSuffix = Integer.valueOf(charSuffix);

                //validate the prefix and suffix, must result in 2
                if (asciiSuffix - asciiPrefix == 2)
                    dataValid = true;
                else
                    dataValid = false;

                if (dataValid == true) {
                    //frst layer of checking that only new data is updated to screen -> check that this data differs from what exists
                    if (data != currentLoadedData) {
                        currentLoadedData = data;
                        seperateAndSetDataFromTokenizedString(data);
                        //Let the other components know we have new data
                        setIsNewRoastData(true);
                    }
                }
            } catch (Exception ex) {
                writeRoastDataLogFile("INVALID DATA", "" + ex.getMessage());
            }
        }
        catch (Exception ex)
        {
            writeRoastDataLogFile("EXCEPTION","due to " + ex.getMessage());
        }

        return dataValid;
    }
    
    /*
    ** Seperates the roast data and validates a value before relating it to a class attribute
     */
    private void seperateAndSetDataFromTokenizedString(String data)
    {
        //check that there is data to set
        if (data.length() > 0)
        {
            //split the data based on the delimiter
            String[] seperatedTokenizedString = data.split("\\|");

            setBeanTemp(seperatedTokenizedString[1]);
            setEnvironmentTemp(seperatedTokenizedString[2]);
            setBeanRateOfRise(seperatedTokenizedString[3]);
            setEnvironmentRateOfRise(seperatedTokenizedString[4]);
            setRoastTime(seperatedTokenizedString[5]);
            setDevelopmentPercentage(seperatedTokenizedString[6]);
            setDevelopmentTime(seperatedTokenizedString[7]);
            setTurnPointTime(seperatedTokenizedString[8]);
            setTurnPointTemp(seperatedTokenizedString[9]);
            setFirstCrackTime(seperatedTokenizedString[10]);
            setFirstCrackTemp(seperatedTokenizedString[11]);
            setFullGas(seperatedTokenizedString[12]);
            setFanSpeed(seperatedTokenizedString[13]);
            setDrumSpeed(seperatedTokenizedString[14]);

        }
    }

    /*
    ** Publically accessible methods to access the RoastData
     */
    public String getBeanTemp()
    {
        return this.beanTemp;
    }

    public String getEnvironmentTemp()
    {
        return this.environmentTemp;
    }

    public String getBeanRateOfRise()
    {
        return this.beanRateOfRise;
    }

    public String getEnvironmentRateOfRise()
    {
        return this.environmentRateOfRise;
    }

    public String getRoastTime()
    {
        return this.roastTime;
    }

    public String getDevelopmentTime()
    {
        return this.developmentTime;
    }

    public String getDevelopmentPercentage()
    {
        return this.developmentPercentage;
    }

    public String getTurnPointTemp()
    {
        return this.turnPointTemp;
    }

    public String getTurnPointTime()
    {
        return this.turnPointTime;
    }

    public String getFirstCrackTime()
    {
        return this.firstCrackTime;
    }

    public String getFirstCrackTemp()
    {
        return this.firstCrackTemp;
    }

    public String getFullGas()
    {
        return this.fullGas;
    }

    public String getFanSpeed()
    {
        return this.fanSpeed;
    }

    public String getDrumSpeed()
    {
        return this.drumSpeed;
    }

    public boolean getIsNewDataStatus()
    {
        return this.isNewRoastData;
    }

    /*
    ** Accessed within class to dynamically set the Roast Data
     */
    private void setBeanTemp(String beanTemp)
    {
        //perform conversion for code to be changed to ux-friendly way
        String[] beanTempCode = beanTemp.split("ga");
        this.beanTemp = beanTempCode[1];
        double doubleBeanTemp = Double.parseDouble(this.beanTemp);
        double convertedBeanTemp = doubleBeanTemp/10;
        this.beanTemp = String.valueOf(convertedBeanTemp);
    }

    private void setEnvironmentTemp(String environmentTemp)
    {
        //perform conversion for code to be changed to ux-friendly way
        String[] environTempCode = environmentTemp.split("gb");
        this.environmentTemp = environTempCode[1];
        double doubleEnvironTemp = Double.parseDouble(this.environmentTemp);
        double convertedEnvironmentTemp = doubleEnvironTemp/10;
        this.environmentTemp = String.valueOf(convertedEnvironmentTemp);
    }

    private void setBeanRateOfRise(String beanRateOfRise)
    {
        //perform conversion for code to be changed to ux-friendly way
        String[] beanRoRCode = beanRateOfRise.split("ra");
        this.beanRateOfRise = beanRoRCode[1];
        double doubleBeanRoR = Double.parseDouble(this.beanRateOfRise);
        double convertedBeanRoR = doubleBeanRoR/10;
        this.beanRateOfRise = String.valueOf(convertedBeanRoR);
    }

    private void setEnvironmentRateOfRise(String environmentRateOfRise)
    {
        //perform conversion for code to be changed to ux-friendly way
        String[] environRoRCode = environmentRateOfRise.split("rb");
        this.environmentRateOfRise = environRoRCode[1];
        double doubleEnvironRoR = Double.parseDouble(this.environmentRateOfRise);
        double convertedEnvironmentRoR = doubleEnvironRoR/10;
        this.environmentRateOfRise = String.valueOf(convertedEnvironmentRoR);
    }

    private void setRoastTime(String roastTime)
    {
        //perform conversion for code to be changed to ux-friendly way
        String[] roastTimeCode = roastTime.split("rm");
        this.roastTime = roastTimeCode[1];
        double doubleRoastTime = Double.parseDouble(this.roastTime);
        double convertedRoastTime = doubleRoastTime/100;
        String roastTimeDecimal = String.valueOf(convertedRoastTime);
        //Split time on '.' -> eg.12.30 = roastTimeDecimalToTime[0] -> 12 ; roastTimeDecimalToTime[1] -> 30
        String[] roastTimeDecimalToTime = roastTimeDecimal.split("\\.");
        String minutes = roastTimeDecimalToTime[0];
        String seconds = roastTimeDecimalToTime[1];
        this.roastTime = minutes + ":" + seconds;
    }

    private void setDevelopmentTime(String developmentTime)
    {
        //perform conversion for code to be changed to ux-friendly way
        String[] developmentTimeCode = developmentTime.split("ro");
        this.developmentTime = developmentTimeCode[1];
        double doubleDevelopmentTime = Double.parseDouble(this.developmentTime);
        double convertedDevelopmentTime = doubleDevelopmentTime/100;
        String developmentTimeDecimal = String.valueOf(convertedDevelopmentTime);
        //Split time on '.' -> eg.12.30 = developmentTimeDecimalToTime[0] -> 12 ; developmentTimeDecimalToTime[1] -> 30
        String[] developmentTimeDecimalToTime = developmentTimeDecimal.split("\\.");
        String minutes = developmentTimeDecimalToTime[0];
        String seconds = developmentTimeDecimalToTime[1];
        this.developmentTime = minutes + ":" + seconds;

    }

    private void setDevelopmentPercentage(String developmentPercentage)
    {
        String[] developmentPercentageCode = developmentPercentage.split("rn");
        this.developmentPercentage = developmentPercentageCode[1];
        double doubleDevelopmentPercentage = Double.parseDouble(this.developmentPercentage);
        double convertedDevelopmentPercentage = doubleDevelopmentPercentage/10;
        this.developmentPercentage = String.valueOf(convertedDevelopmentPercentage);
    }

    private void setTurnPointTemp(String turnPointTemp)
    {
        String[] turnPointTempCode = turnPointTemp.split("rq");
        this.turnPointTemp = turnPointTempCode[1];
        double doubleTurnPointTemp = Double.parseDouble(this.turnPointTemp);
        double convertedTurnPointTemp = doubleTurnPointTemp/10;
        this.turnPointTemp = String.valueOf(convertedTurnPointTemp);
    }

    private void setTurnPointTime(String turnPointTime)
    {
        String[] turnPointTimeCode = turnPointTime.split("rp");
        this.turnPointTime = turnPointTimeCode[1];
        double doubleTurnPointTime = Double.parseDouble(this.turnPointTime);
        double convertedTurnPointTime = doubleTurnPointTime/100;
        String turnPointTimeDecimal = String.valueOf(convertedTurnPointTime);
        //Split time on '.' -> eg.12.30 = turnPointTimeDecimalToTime[0] -> 12 ; turnPointTimeDecimalToTime[1] -> 30
        String[] turnPointTimeDecimalToTime = turnPointTimeDecimal.split("\\.");
        String minutes = turnPointTimeDecimalToTime[0];
        String seconds = turnPointTimeDecimalToTime[1];
        this.turnPointTime = minutes + ":" + seconds;
    }

    private void setFirstCrackTime(String firstCrackTime)
    {
        String[] firstCrackTimeCode = firstCrackTime.split("rr");
        this.firstCrackTime = firstCrackTimeCode[1];
        double doubleFirstCrackTime = Double.parseDouble(this.firstCrackTime);
        double convertedFirstCrackTime = doubleFirstCrackTime/100;
        String firstCrackTimeDecimal = String.valueOf(convertedFirstCrackTime);
        //Split time on '.' -> eg.12.30 = firstCrackTimeDecimalToTime[0] -> 12 ; firstCrackTimeDecimalToTime[1] -> 30
        String[] firstCrackTimeDecimalToTime = firstCrackTimeDecimal.split("\\.");
        String minutes = firstCrackTimeDecimalToTime[0];
        String seconds = firstCrackTimeDecimalToTime[1];
        this.firstCrackTime = minutes + ":" + seconds;
    }

    private void setFirstCrackTemp(String firstCrackTemp)
    {
        String[] firstCrackTempCode = firstCrackTemp.split("rs");
        this.firstCrackTemp = firstCrackTempCode[1];
        double doubleFirstCrackTemp = Double.parseDouble(this.firstCrackTemp);
        double convertedFirstCrackTemp = doubleFirstCrackTemp/10;
        this.firstCrackTemp = String.valueOf(convertedFirstCrackTemp);
    }

    private void setFullGas(String fullGas)
    {
        String[] fullGasCode = fullGas.split("sa");
        this.fullGas = fullGasCode[1];
        int doubleFullGas = Integer.parseInt(this.fullGas);
        this.fullGas = String.valueOf(doubleFullGas);
    }

    private void setFanSpeed(String fanSpeed)
    {
        String[] fanSpeedCode = fanSpeed.split("sb");
        this.fanSpeed = fanSpeedCode[1];
        int doubleFanSpeed = Integer.parseInt(this.fanSpeed);
        this.fanSpeed = String.valueOf(doubleFanSpeed);
    }

    private void setDrumSpeed(String drumSpeed)
    {
        String[] drumSpeedCode = drumSpeed.split("sc");
        this.drumSpeed = drumSpeedCode[1];
        int doubleDrumSpeed = Integer.parseInt(this.drumSpeed);
        this.drumSpeed = String.valueOf(doubleDrumSpeed);
    }

    public void setIsNewRoastData(boolean isNew)
    {
        this.isNewRoastData = isNew;
    }

    public void writeRoastDataLogFile(String filename, String errorText)
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
