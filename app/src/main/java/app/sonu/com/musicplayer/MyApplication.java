package app.sonu.com.musicplayer;

import android.app.Application;

import app.sonu.com.musicplayer.di.component.ApplicationComponent;
import app.sonu.com.musicplayer.di.component.DaggerApplicationComponent;
import app.sonu.com.musicplayer.di.module.AppBusModule;
import app.sonu.com.musicplayer.di.module.ApplicationModule;
import app.sonu.com.musicplayer.di.module.DataModule;
import app.sonu.com.musicplayer.di.module.DbModule;
import app.sonu.com.musicplayer.di.module.LocalStorageModule;
import app.sonu.com.musicplayer.di.module.NetworkModule;
import app.sonu.com.musicplayer.di.module.PrefsModule;

/**
 * Created by sonu on 29/6/17.
 */

public class MyApplication extends Application {

    ApplicationComponent applicationComponent;

    @Override
    public void onCreate() {
        super.onCreate();

        applicationComponent = DaggerApplicationComponent.builder()
                .applicationModule(new ApplicationModule(this))
                .dataModule(new DataModule())
                .networkModule(new NetworkModule())
                .prefsModule(new PrefsModule())
                .localStorageModule(new LocalStorageModule())
                .dbModule(new DbModule())
                .appBusModule(new AppBusModule())
                .build();
    }

    public ApplicationComponent getApplicationComponent()  {
        return applicationComponent;
    }
}
