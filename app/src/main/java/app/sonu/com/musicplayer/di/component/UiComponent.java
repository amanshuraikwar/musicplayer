package app.sonu.com.musicplayer.di.component;

import app.sonu.com.musicplayer.di.PerActivity;
import app.sonu.com.musicplayer.di.module.UiModule;
import app.sonu.com.musicplayer.ui.allsongs.AllSongsFragment;
import app.sonu.com.musicplayer.ui.fileview.FileViewFragment;
import app.sonu.com.musicplayer.ui.main.MainActivity;

import app.sonu.com.musicplayer.ui.musicplayer.MusicPlayerFragment;
import dagger.Component;

/**
 * Created by sonu on 29/6/17.
 */
@PerActivity
@Component(
        dependencies = {ApplicationComponent.class},
        modules = UiModule.class)

public interface UiComponent {
    void inject(MainActivity mainActivity);
    void inject(FileViewFragment fileViewFragment);
    void inject(AllSongsFragment allSongsFragment);
    void inject(MusicPlayerFragment musicPlayerFragment);
}
