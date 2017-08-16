package app.sonu.com.musicplayer.data.prefs;

import android.content.Context;
import android.content.SharedPreferences;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;

import app.sonu.com.musicplayer.R;
import app.sonu.com.musicplayer.data.db.model.Song;

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
        return sharedPref.getBoolean(this.mContext.getString(R.string.first_run_status), true);
    }

    @Override
    public boolean setFirstRun(Boolean flag) {
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putBoolean(this.mContext.getString(R.string.first_run_status), flag);
        editor.commit();
        return flag;
    }

    @Override
    public ArrayList<Song> getSongQueue() {
        Gson gson = new Gson();
        String json = sharedPref.getString(this.mContext.getString(R.string.song_queue), null);
        Type type = new TypeToken<ArrayList<Song>>() {
        }.getType();
        return gson.fromJson(json, type);
    }

    @Override
    public boolean setSongQueue(ArrayList<Song> songQueue) {
        SharedPreferences.Editor editor = sharedPref.edit();
        Gson gson = new Gson();
        String json = gson.toJson(songQueue);
        editor.putString(this.mContext.getString(R.string.song_queue), json);
        editor.commit();
        return true;
    }

    @Override
    public int getCurrentSongIndex() {
        return sharedPref.getInt(this.mContext.getString(R.string.current_song_index), -1);
    }

    @Override
    public boolean setCurrentSongIndex(int index) {
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putInt(this.mContext.getString(R.string.current_song_index), index);
        editor.commit();
        return true;
    }

}
