package app.sonu.com.musicplayer.di.module;

import android.support.v4.media.MediaBrowserCompat;
import android.support.v4.util.Pair;
import android.view.View;

import javax.inject.Named;
import javax.inject.Singleton;

import app.sonu.com.musicplayer.AppBus;
import dagger.Module;
import dagger.Provides;
import io.reactivex.subjects.PublishSubject;

/**
 * Created by sonu on 4/7/17.
 */
@Module
public class AppBusModule {

    @Singleton
    @Provides
    AppBus getAppBus() {
        return new AppBus();
    }
}
