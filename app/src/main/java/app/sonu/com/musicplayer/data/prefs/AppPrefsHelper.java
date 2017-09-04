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
        return sharedPref.getBoolean(this.mContext.getString(R.string.first_run_status), true);
    }

    @Override
    public boolean setFirstRun(Boolean flag) {
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putBoolean(this.mContext.getString(R.string.first_run_status), flag);
        editor.commit();
        return flag;
    }
}
