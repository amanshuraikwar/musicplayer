package app.sonu.com.musicplayer.di.module;

import android.content.Context;

import javax.inject.Singleton;

import app.sonu.com.musicplayer.data.prefs.AppPrefsHelper;
import app.sonu.com.musicplayer.data.prefs.PrefsHelper;
import app.sonu.com.musicplayer.di.ApplicationContext;
import dagger.Module;
import dagger.Provides;

/**
 * Created by sonu on 30/6/17.
 */
@Module
public class PrefsModule {
    @Provides
    @Singleton
    PrefsHelper getPrefsHelper(AppPrefsHelper appPrefsHelper) {
        return appPrefsHelper;
    }

    @Provides
    @Singleton
    AppPrefsHelper getAppPrefsHelper(@ApplicationContext Context context) {
        return new AppPrefsHelper(context);
    }
}
