package simba.com.waterlootransit;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * Created by tmast_000 on 7/1/2015.
 */
public class Settings {

    private static SharedPreferences settings;
    // Constants
    private static final String SETTINGS = "settings";
    private static final String IS_FIRST_LAUNCH = "IS_FIRST_LAUNCH";

    public static boolean isFirstLaunch(Context context) {
        boolean isFirstLaunch = false;
        if (settings == null) {
            settings = context.getApplicationContext().getSharedPreferences(SETTINGS, Context.MODE_PRIVATE);
        }

        if (!settings.contains(IS_FIRST_LAUNCH)) {
            isFirstLaunch = true;
            SharedPreferences.Editor editor = settings.edit();
            editor.putBoolean(IS_FIRST_LAUNCH,false);
            editor.apply();
        }

        return isFirstLaunch;
    }
}
