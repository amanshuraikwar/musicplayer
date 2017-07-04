package app.sonu.com.musicplayer.di.module;

import android.content.Context;

import javax.inject.Singleton;

import app.sonu.com.musicplayer.data.db.AppDbHelper;
import app.sonu.com.musicplayer.data.db.DbHelper;
import app.sonu.com.musicplayer.data.prefs.AppPrefsHelper;
import app.sonu.com.musicplayer.data.prefs.PrefsHelper;
import app.sonu.com.musicplayer.di.ApplicationContext;
import dagger.Module;
import dagger.Provides;

/**
 * Created by sonu on 30/6/17.
 */
@Module
public class DbModule {
    @Provides
    @Singleton
    DbHelper getDbHelper(AppDbHelper appDbHelper) {
        return appDbHelper;
    }

    @Provides
    @Singleton
    AppDbHelper getAppDbHelper() {
        return new AppDbHelper();
    }
}
