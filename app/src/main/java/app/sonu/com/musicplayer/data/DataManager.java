package app.sonu.com.musicplayer.data;

import app.sonu.com.musicplayer.data.db.DbHelper;
import app.sonu.com.musicplayer.data.local.LocalStorageHelper;
import app.sonu.com.musicplayer.data.network.ApiHelper;
import app.sonu.com.musicplayer.data.prefs.PrefsHelper;

/**
 * Created by sonu on 29/6/17.
 */

// facade to access data for the app
public interface DataManager extends ApiHelper, PrefsHelper, LocalStorageHelper, DbHelper {
}
