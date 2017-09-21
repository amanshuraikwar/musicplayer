package app.sonu.com.musicplayer.di.component;

import javax.inject.Named;
import javax.inject.Singleton;

import app.sonu.com.musicplayer.AppBus;
import app.sonu.com.musicplayer.di.module.BusModule;
import dagger.Component;
import dagger.Provides;
import io.reactivex.subjects.PublishSubject;

import static app.sonu.com.musicplayer.di.module.BusModule.PROVIDER_MUSIC_PLAYER_PANEL_STATE;

/**
 * Created by sonu on 14/9/17.
 */

@Component(modules = {BusModule.class})
public interface BusComponent {
    void inject(AppBus appBus);
}
