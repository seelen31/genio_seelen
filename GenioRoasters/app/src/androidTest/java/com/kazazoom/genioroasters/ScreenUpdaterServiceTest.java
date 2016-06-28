package com.kazazoom.genioroasters;

import android.app.Service;
import android.content.Intent;
import android.test.ServiceTestCase;
import android.test.mock.MockContext;

/**
 * Created by Given Mojapelo on 6/9/2016.
 */
public class ScreenUpdaterServiceTest extends ServiceTestCase<Service> {

    /**
     * Constructor
     *
     * @param serviceClass The type of the service under test.
     */
    public ScreenUpdaterServiceTest(Class<Service> serviceClass) {
        super(serviceClass);
    }

    ScreenUpdaterService updaterService;

    public void CreateAndStartService()
    {
        MockContext context = new MockContext();     //might need t use real context
        Intent intent = new Intent(context, ScreenUpdaterService.class);
        context.startService(intent);
    }

    //Need to know more about running and testing a service...

}
