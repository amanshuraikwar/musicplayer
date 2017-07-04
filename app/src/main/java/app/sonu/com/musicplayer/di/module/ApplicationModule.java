package app.sonu.com.musicplayer.di.module;

import android.app.Application;
import android.content.Context;
import app.sonu.com.musicplayer.di.ApplicationContext;


import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;

/**
 * Created by sonu on 9/3/17.
 */
@Module
public class ApplicationModule {

    private Application mApplication;

    public ApplicationModule(Application application) {
        this.mApplication = application;
    }

    @Provides
    @Singleton
    @ApplicationContext
    Context getContext() {
        return this.mApplication;
    }

}
