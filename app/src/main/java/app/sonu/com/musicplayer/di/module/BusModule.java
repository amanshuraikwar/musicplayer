package app.sonu.com.musicplayer.di.module;

import javax.inject.Named;
import javax.inject.Singleton;

import app.sonu.com.musicplayer.data.db.model.Song;
import app.sonu.com.musicplayer.di.ApplicationContext;
import dagger.Module;
import dagger.Provides;
import io.reactivex.subjects.PublishSubject;

/**
 * Created by sonu on 4/7/17.
 */
@Module
public class BusModule {

    public static final String PROVIDER_SELECTED_SONG = "PROVIDER_SELECTED_SONG";

    @Provides
    @Singleton
    @Named(PROVIDER_SELECTED_SONG)
    PublishSubject<Song> getSelectedSongProvider() {
        return PublishSubject.create();
    }
}
