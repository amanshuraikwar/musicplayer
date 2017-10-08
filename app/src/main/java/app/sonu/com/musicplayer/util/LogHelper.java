package app.sonu.com.musicplayer.util;

import java.util.Objects;

/**
 * Created by sonu on 1/10/17.
 */

public class LogHelper {
    public static String getLogTag(Class cls) {
        return cls.getSimpleName();
    }
}
