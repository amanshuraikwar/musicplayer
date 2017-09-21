package app.sonu.com.musicplayer.di.component;

import android.app.Activity;

import javax.inject.Singleton;

import app.sonu.com.musicplayer.PerSlidingUpPanelBus;
import app.sonu.com.musicplayer.di.PerMusicPlayerHolder;
import app.sonu.com.musicplayer.di.module.ActivityModule;
import app.sonu.com.musicplayer.di.module.FragmentModule;
import app.sonu.com.musicplayer.di.module.MusicPlayerHolderModule;
import dagger.Component;

/**
 * Created by sonu on 15/9/17.
 */

@PerMusicPlayerHolder
@Component(modules = {MusicPlayerHolderModule.class},
        dependencies = {ApplicationComponent.class})
public interface MusicPlayerHolderComponent {
    PerSlidingUpPanelBus getPerSlidingUpPanelBus();

    ActivityComponent.Builder activityComponentBuilder();
    FragmentComponent.Builder fragmentComponentBuilder();
}
