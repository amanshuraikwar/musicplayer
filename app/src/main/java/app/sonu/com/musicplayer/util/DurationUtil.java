package app.sonu.com.musicplayer.util;

/**
 * Created by sonu on 1/10/17.
 */

public class DurationUtil {
    public static String getFormattedDuration(long duration) {
        duration = duration/1000;
        String formattedDuration = "";

        formattedDuration += duration/60;
        formattedDuration += ":";
        formattedDuration += String.format("%02d", duration%60);

        return formattedDuration;
    }
}
