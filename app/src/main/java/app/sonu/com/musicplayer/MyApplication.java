package app.sonu.com.musicplayer;

import android.app.Application;

import org.litepal.LitePal;

import app.sonu.com.musicplayer.di.component.ApplicationComponent;
import app.sonu.com.musicplayer.di.component.DaggerApplicationComponent;
import app.sonu.com.musicplayer.di.module.ApplicationModule;
import app.sonu.com.musicplayer.di.module.DataModule;
import app.sonu.com.musicplayer.di.module.NetworkModule;

/**
 * Created by sonu on 29/6/17.
 */

public class MyApplication extends Application {

    ApplicationComponent applicationComponent;

    @Override
    public void onCreate() {
        super.onCreate();
        LitePal.initialize(this);

        applicationComponent = DaggerApplicationComponent.builder()
                .applicationModule(new ApplicationModule(this))
                .dataModule(new DataModule())
                .networkModule(new NetworkModule())
                .build();
    }

    public ApplicationComponent getApplicationComponent()  {
        return applicationComponent;
    }
}
