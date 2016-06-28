package com.kazazoom.genioroasters;

import android.app.Activity;
import android.content.Intent;
import android.view.View;
import android.widget.Button;

/**
 * Created by Given Mojapelo on 6/2/2016.
 * @Purpose: Used as a helper class,implementing navigation bar buttons listeners
 *         : The views are created in toolbar_view.xml layout file
 */
public class TabActionsListener{

    private static Button homeButton;
    private static Button reportsButton;
    private static Button settingsButton;
    public static int currentActivityID;

    public static void setTabNavigationActions(Activity activity){

        /**
         * @Purpose: contains all the actins for the navigation bar
         * @Param: activity calling this method
         */
        homeButton = (Button)activity.findViewById(R.id.homeNavigationButton);
        homeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(currentActivityID != R.id.dashBoardID) {
                    currentActivityID = R.id.dashBoardID;
                    Intent intent = new Intent(DashBoardActivity.context, DashBoardActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK); //Used to start new activity from non-activity class:
                    DashBoardActivity.context.startActivity(intent);
                }
            }
        });

        reportsButton = (Button)activity.findViewById(R.id.reportsButton);
        reportsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                  //Watch the space...
            }
        });

        settingsButton = (Button)activity.findViewById(R.id.settingsNavigationButton);
        settingsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(currentActivityID != R.id.mainLayout) {
                    currentActivityID = R.id.mainLayout;
                    Intent intent = new Intent(DashBoardActivity.context, SettingsActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK); //Used to start new activity from non-activity class:
                    DashBoardActivity.context.startActivity(intent);
                }
            }
        });

    }

}
