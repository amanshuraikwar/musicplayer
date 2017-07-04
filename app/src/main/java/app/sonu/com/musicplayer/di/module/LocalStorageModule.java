package app.sonu.com.musicplayer.di.module;

import android.content.Context;

import javax.inject.Singleton;

import app.sonu.com.musicplayer.data.local.AppLocalStorageHelper;
import app.sonu.com.musicplayer.data.local.LocalStorageHelper;
import app.sonu.com.musicplayer.di.ApplicationContext;
import dagger.Module;
import dagger.Provides;

/**
 * Created by sonu on 30/6/17.
 */

@Module
public class LocalStorageModule {
    @Provides
    @Singleton
    LocalStorageHelper getLocalStorageHelper(AppLocalStorageHelper appLocalStorageHelper) {
        return appLocalStorageHelper;
    }

    @Provides
    @Singleton
    AppLocalStorageHelper getAppLocalStorageHelper(@ApplicationContext Context context) {
        return new AppLocalStorageHelper(context);
    }
}
