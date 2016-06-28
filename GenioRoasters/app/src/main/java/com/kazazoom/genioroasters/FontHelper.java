package com.kazazoom.genioroasters;

import android.graphics.Typeface;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

/**
 * Created by Given Mojapelo on 6/9/2016.
 */
public class FontHelper {

    private static Typeface typeBoldFont = Typeface.createFromAsset(DashBoardActivity.context.getAssets(), "fonts/Roboto-Bold.ttf");
    private static Typeface typeDefaultFont = Typeface.createFromAsset(DashBoardActivity.context.getAssets(),"fonts/Roboto-Thin.ttf");

    public static final int BOLD = 40;
    public static final int THIN = 60;

    /**
     * Sets the default app font
     * @param view
     */
    public static void setFont(View view){

        try {

            if (view instanceof TextView) {
                ((TextView) view).setTypeface(typeDefaultFont);
            } else if (view instanceof Button) {
                ((Button) view).setTypeface(typeDefaultFont);
            } else if (view instanceof EditText) {
                ((EditText) view).setTypeface(typeDefaultFont);
            }
        }
        catch (Exception ex){
            ArduinoConnector arduinoConnector = new ArduinoConnector();
            arduinoConnector.writeLogFile("ArduinoConnector", ex.getMessage());
        }
    }

    /**
     * Used by the activities to set custom fonts, be it bold, thin or any new font added
     * @param fontStyleEnum is the enums created at the top
     * @return
     */
    public static void setFont(View view, int fontStyleEnum){

        try {

            if (view instanceof TextView) {
                ((TextView) view).setTypeface
                        (setFontFromENUM(fontStyleEnum));
            } else if (view instanceof Button) {
                ((Button) view).setTypeface
                        (setFontFromENUM(fontStyleEnum));
            } else if (view instanceof EditText) {
                ((EditText) view).setTypeface
                        (setFontFromENUM(fontStyleEnum));
            }
        }
        catch (Exception ex){
            ArduinoConnector arduinoConnector = new ArduinoConnector();
            arduinoConnector.writeLogFile("ArduinoConnector", ex.getMessage());
        }
    }

    /**
     * Used by the activities to set custom fonts, be it bold, thin or any new font added
     * @param fontStyleEnum
     * @return
     */
    private static Typeface setFontFromENUM(int fontStyleEnum){

        Typeface typeface = null;

        try {
            if (fontStyleEnum == BOLD)
                typeface = typeBoldFont;
            else
                typeface =  typeDefaultFont;
        }
        catch (Exception ex){
            ArduinoConnector arduinoConnector = new ArduinoConnector();
            arduinoConnector.writeLogFile("ArduinoConnector", ex.getMessage());
        }
        return typeface;
    }


}
