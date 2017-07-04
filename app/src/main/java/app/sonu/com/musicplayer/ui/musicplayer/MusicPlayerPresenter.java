package app.sonu.com.musicplayer.ui.musicplayer;

import app.sonu.com.musicplayer.base.ui.BasePresenter;
import app.sonu.com.musicplayer.data.DataManager;
import app.sonu.com.musicplayer.data.db.model.Song;
import io.reactivex.Observer;
import io.reactivex.disposables.Disposable;
import io.reactivex.subjects.PublishSubject;

/**
 * Created by sonu on 4/7/17.
 */

public class MusicPlayerPresenter extends BasePresenter<MusicPlayerMvpView>
        implements MusicPlayerMvpPresenter{

    private static final String TAG = MusicPlayerPresenter.class.getSimpleName();
    private PublishSubject<Song> selectedSongSubject;

    public MusicPlayerPresenter(DataManager dataManager,
                                PublishSubject<Song> selectedSongSubject) {
        super(dataManager);
        this.selectedSongSubject = selectedSongSubject;
    }

    @Override
    public void onDetach() {

    }

    @Override
    public void onStart() {
        selectedSongSubject.subscribe(new Observer<Song>() {
            @Override
            public void onSubscribe(Disposable d) {

            }

            @Override
            public void onNext(Song value) {
                mMvpView.displaySong(value);
            }

            @Override
            public void onError(Throwable e) {

            }

            @Override
            public void onComplete() {

            }
        });
    }
}
