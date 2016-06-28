package com.kazazoom.genioroasters;

import android.app.Application;
import android.test.ApplicationTestCase;

import junit.framework.Assert;

import org.junit.Test;

/**
 * Created by Given Mojapelo on 6/9/2016.
 */
public class RoastDataTest extends ApplicationTestCase<Application> {
    public RoastDataTest(Class<Application> applicationClass) {
        super(applicationClass);
    }

    RoastData roastData;

    @Test
    public void createRoastData(){

        roastData = RoastData.getInstance();
    }

    @Test
    public void populateRoastData(){

        String dataString = "ga1234|gb3568|ra124|rb331|rm1215|rn121|ro0133|rp0159|rq895|rr1233|rs1856|sa100|sb50|sc50";
        boolean checksum = roastData.validateTokenizedDataWithChecksum(dataString);

        Assert.assertEquals(true, checksum);

        if(checksum == true)
            ReadRoastData();

    }

    @Test
    public void ReadRoastData()
    {

/*        Bean Temp | Environment Temp | Bean Rate of Rise | Enviro Rate of Rise |
        Roast Time | Development Time | Development Time | Turn Point Time |
                Turn Point Temp | First Crack Time | First Crack Temp| full gas | fan speed | drum speed*/
        Assert.assertEquals("1234", roastData.getBeanTemp());
        Assert.assertEquals("3568", roastData.getBeanRateOfRise());
        Assert.assertEquals("124", roastData.getEnvironmentTemp());
        Assert.assertEquals("331", roastData.getEnvironmentRateOfRise());
        Assert.assertEquals("1215", roastData.getRoastTime());
        Assert.assertEquals("121", roastData.getDevelopmentTime());
        Assert.assertEquals("0133", roastData.getDevelopmentPercentage());
        Assert.assertEquals("0159", roastData.getTurnPointTime());
        Assert.assertEquals("895", roastData.getTurnPointTemp());
        Assert.assertEquals("1233", roastData.getFirstCrackTime());
        Assert.assertEquals("1856", roastData.getFirstCrackTemp());
        Assert.assertEquals("100", roastData.getFullGas());
        Assert.assertEquals("50", roastData.getFanSpeed());
        Assert.assertEquals("50", roastData.getDrumSpeed());
    }

}
