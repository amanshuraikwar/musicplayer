package app.sonu.com.musicplayer.di.module;

import javax.inject.Singleton;

import app.sonu.com.musicplayer.PerSlidingUpPanelBus;
import app.sonu.com.musicplayer.di.PerMusicPlayerHolder;
import app.sonu.com.musicplayer.di.component.ActivityComponent;
import app.sonu.com.musicplayer.di.component.FragmentComponent;
import dagger.Module;
import dagger.Provides;

/**
 * Created by sonu on 15/9/17.
 */

@Module(subcomponents = {ActivityComponent.class, FragmentComponent.class})
public class MusicPlayerHolderModule {

    @Provides
    @PerMusicPlayerHolder
    PerSlidingUpPanelBus getPerSlidingUpPanelBus() {
        return new PerSlidingUpPanelBus();
    }
}
