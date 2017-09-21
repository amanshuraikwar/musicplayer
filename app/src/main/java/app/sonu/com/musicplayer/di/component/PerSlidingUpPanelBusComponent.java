package app.sonu.com.musicplayer.di.component;

import app.sonu.com.musicplayer.PerSlidingUpPanelBus;
import app.sonu.com.musicplayer.di.PerMusicPlayerHolder;
import app.sonu.com.musicplayer.di.module.PerSlidingUpPanelBusModule;
import dagger.Component;

/**
 * Created by sonu on 15/9/17.
 */

@Component(modules = {PerSlidingUpPanelBusModule.class})
public interface PerSlidingUpPanelBusComponent {
    void inject(PerSlidingUpPanelBus perSlidingUpPanelBus);
}
