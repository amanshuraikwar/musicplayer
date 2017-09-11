package app.sonu.com.musicplayer.data.prefs;

import android.content.Context;
import android.content.SharedPreferences;
import app.sonu.com.musicplayer.R;

/**
 * Created by sonu on 29/6/17.
 */

public class AppPrefsHelper implements PrefsHelper{

    private Context mContext;
    private SharedPreferences sharedPref;

    public AppPrefsHelper(Context context) {
        this.mContext = context;
        sharedPref = this.mContext
                .getSharedPreferences(
                        this.mContext.getString(app.sonu.com.musicplayer.R.string.preference_file_key),
                        Context.MODE_PRIVATE
                );
    }

    @Override
    public boolean isFirstRun() {
        return sharedPref.getBoolean(this.mContext.getString(R.string.first_run_status_pref_key), true);
    }

    @Override
    public boolean setFirstRun(Boolean flag) {
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putBoolean(this.mContext.getString(R.string.first_run_status_pref_key), flag);
        editor.commit();
        return flag;
    }

    @Override
    public String getPlaylistIds() {
        return sharedPref.getString(this.mContext.getString(R.string.playlist_id_list_pref_key), null);
    }

    @Override
    public void createPlaylistIdList(String json) {
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putString(this.mContext.getString(R.string.playlist_id_list_pref_key), json);
        editor.commit();
    }

    @Override
    public String getPlaylistById(String id) {
        return sharedPref.getString(id, null);
    }

    @Override
    public void putPlaylist(String id, String playlist) {
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putString(id, playlist);
        editor.commit();
    }
}
