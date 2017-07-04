package app.sonu.com.musicplayer.data.network;

/**
 * Created by sonu on 29/6/17.
 */

public class AppApiHelper implements ApiHelper{
    private static final String TAG = AppApiHelper.class.getSimpleName();

    private RequestHandler mRequestHandler;

    public AppApiHelper(RequestHandler requestHandler) {
        this.mRequestHandler = requestHandler;
    }

}
