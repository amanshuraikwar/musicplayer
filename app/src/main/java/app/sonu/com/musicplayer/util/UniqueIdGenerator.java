package app.sonu.com.musicplayer.util;

/**
 * Created by sonu on 9/9/17.
 */

public class UniqueIdGenerator {
    private static long id = System.currentTimeMillis();

    public static long getId() {
        return id--;
    }
}
