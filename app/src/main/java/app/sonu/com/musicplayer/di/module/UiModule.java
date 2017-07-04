package app.sonu.com.musicplayer.di.module;

import android.app.Activity;
import android.content.Context;

import javax.inject.Named;

import app.sonu.com.musicplayer.data.DataManager;
import app.sonu.com.musicplayer.data.db.model.Song;
import app.sonu.com.musicplayer.di.ActivityContext;
import app.sonu.com.musicplayer.di.PerActivity;
import app.sonu.com.musicplayer.ui.allsongs.AllSongsMvpPresenter;
import app.sonu.com.musicplayer.ui.allsongs.AllSongsPresenter;
import app.sonu.com.musicplayer.ui.fileview.FileViewMvpPresenter;
import app.sonu.com.musicplayer.ui.fileview.FileViewPresenter;
import app.sonu.com.musicplayer.ui.main.MainMvpPresenter;
import app.sonu.com.musicplayer.ui.main.MainPresenter;

import app.sonu.com.musicplayer.ui.musicplayer.MusicPlayerFragment;
import app.sonu.com.musicplayer.ui.musicplayer.MusicPlayerMvpPresenter;
import app.sonu.com.musicplayer.ui.musicplayer.MusicPlayerPresenter;
import dagger.Module;
import dagger.Provides;
import io.reactivex.subjects.PublishSubject;

/**
 * Created by sonu on 29/6/17.
 */

@Module
public class UiModule {
    private Activity mActivity;

    public UiModule(Activity activity) {
        this.mActivity = activity;
    }

    @Provides
    @PerActivity
    @ActivityContext
    Context getContext() {
        return this.mActivity;
    }

    @Provides
    @PerActivity
    MainMvpPresenter getMainMvpPresenter(MainPresenter mainPresenter) {
        return mainPresenter;
    }

    @Provides
    @PerActivity
    MainPresenter getMainPresenter(DataManager dataManager) {
        return new MainPresenter(dataManager);
    }

    @Provides
    @PerActivity
    FileViewMvpPresenter getFileViewMvpPresenter(FileViewPresenter fileViewPresenter) {
        return fileViewPresenter;
    }

    @Provides
    @PerActivity
    FileViewPresenter getFileViewPresenter(DataManager dataManager) {
        return new FileViewPresenter(dataManager);
    }

    @Provides
    @PerActivity
    AllSongsMvpPresenter getAllSongsMvpPresenter(AllSongsPresenter allSongsPresenter) {
        return allSongsPresenter;
    }

    @Provides
    @PerActivity
    AllSongsPresenter getAllSongsPresenter(DataManager dataManager,
                                           @Named(BusModule.PROVIDER_SELECTED_SONG)
                                                   PublishSubject<Song> selectedSongSubject) {
        return new AllSongsPresenter(dataManager, selectedSongSubject);
    }

    @Provides
    @PerActivity
    MusicPlayerMvpPresenter getMusicPlayerMvpPresenter(MusicPlayerPresenter musicPlayerPresenter) {
        return musicPlayerPresenter;
    }

    @Provides
    @PerActivity
    MusicPlayerPresenter getMusicPlayerPresenter(DataManager dataManager,
                                                 @Named(BusModule.PROVIDER_SELECTED_SONG)
                                                         PublishSubject<Song> selectedSongSubject) {
        return new MusicPlayerPresenter(dataManager, selectedSongSubject);
    }
}
