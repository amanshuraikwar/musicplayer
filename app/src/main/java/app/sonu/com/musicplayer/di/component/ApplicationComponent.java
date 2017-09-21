package app.sonu.com.musicplayer.di.component;

import app.sonu.com.musicplayer.AppBus;
import app.sonu.com.musicplayer.data.DataManager;
import app.sonu.com.musicplayer.di.module.AppBusModule;
import app.sonu.com.musicplayer.di.module.ApplicationModule;
import app.sonu.com.musicplayer.di.module.DataModule;
import app.sonu.com.musicplayer.di.module.DbModule;
import app.sonu.com.musicplayer.di.module.LocalStorageModule;
import app.sonu.com.musicplayer.di.module.NetworkModule;

import javax.inject.Singleton;

import app.sonu.com.musicplayer.di.module.PrefsModule;
import app.sonu.com.musicplayer.mediaplayer.MusicService;
import dagger.Component;

/**
 * Created by sonu on 9/3/17.
 */
@Singleton
@Component(modules = {
        ApplicationModule.class,
        DataModule.class,
        NetworkModule.class,
        PrefsModule.class,
        LocalStorageModule.class,
        DbModule.class,
        AppBusModule.class})
public interface ApplicationComponent {
    DataManager getDataManager();
    AppBus getAppBus();

    void inject(MusicService musicService);
}
