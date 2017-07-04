package app.sonu.com.musicplayer.di.module;

import android.content.Context;
import app.sonu.com.musicplayer.data.AppDataManager;
import app.sonu.com.musicplayer.data.DataManager;
import app.sonu.com.musicplayer.data.db.DbHelper;
import app.sonu.com.musicplayer.data.local.LocalStorageHelper;
import app.sonu.com.musicplayer.data.network.ApiHelper;
import app.sonu.com.musicplayer.data.network.AppApiHelper;
import app.sonu.com.musicplayer.data.network.RequestHandler;
import app.sonu.com.musicplayer.data.prefs.AppPrefsHelper;
import app.sonu.com.musicplayer.data.prefs.PrefsHelper;
import app.sonu.com.musicplayer.di.ApplicationContext;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;

/**
 * Created by sonu on 12/3/17.
 */

@Module
public class DataModule {
    @Provides
    @Singleton
    DataManager getDataManager(AppDataManager appDataManager) {
        return appDataManager;
    }

    @Provides
    @Singleton
    AppDataManager getAppDataManager(ApiHelper apiHelper, PrefsHelper prefsHelper,
                                     LocalStorageHelper localStorageHelper, DbHelper dbHelper) {
        return new AppDataManager(apiHelper, prefsHelper, localStorageHelper, dbHelper);
    }
}
