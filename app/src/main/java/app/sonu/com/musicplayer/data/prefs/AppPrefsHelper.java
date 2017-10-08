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
        editor.apply();
        return flag;
    }

    @Override
    public String getPlaylistIdList() {
        return sharedPref.getString(this.mContext.getString(R.string.playlist_id_list_pref_key), null);
    }

    @Override
    public void createPlaylistIdList(String playlistJson) {
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putString(this.mContext.getString(R.string.playlist_id_list_pref_key), playlistJson);
        editor.apply();
    }

    @Override
    public String getPlaylistByPlaylistId(String playlistId) {
        return sharedPref.getString(playlistId, null);
    }

    @Override
    public void putPlaylist(String playlistId, String playlistJson) {
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putString(playlistId, playlistJson);
        editor.apply();
    }

    @Override
    public void removePlaylist(String playlistId) {
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.remove(playlistId);
        editor.apply();
    }
}
