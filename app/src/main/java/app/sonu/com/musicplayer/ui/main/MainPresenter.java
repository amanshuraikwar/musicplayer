package app.sonu.com.musicplayer.ui.main;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.IBinder;
import android.support.v4.media.MediaBrowserCompat;
import android.util.Log;
import android.widget.Toast;

import app.sonu.com.musicplayer.base.ui.BasePresenter;
import app.sonu.com.musicplayer.data.DataManager;
import app.sonu.com.musicplayer.data.db.model.Song;
import app.sonu.com.musicplayer.mediaplayer.MediaPlayerService;
import io.reactivex.Observer;
import io.reactivex.disposables.Disposable;
import io.reactivex.subjects.PublishSubject;

/**
 * Created by sonu on 29/6/17.
 */

public class MainPresenter extends BasePresenter<MainMvpView> implements MainMvpPresenter{

    private static final String TAG = MainPresenter.class.getSimpleName();

    private PublishSubject<MediaBrowserCompat.MediaItem> mSelectedItemPublishSubject;

    public MainPresenter(DataManager dataManager,
                         PublishSubject<MediaBrowserCompat.MediaItem> selectedItemPublishSubject) {
        super(dataManager);
        mSelectedItemPublishSubject = selectedItemPublishSubject;
    }

    @Override
    public void onDetach() {
        //nothing
    }

    @Override
    public void onStart() {
        //nothing
        mSelectedItemPublishSubject.subscribe(new Observer<MediaBrowserCompat.MediaItem>() {
            @Override
            public void onSubscribe(Disposable d) {

            }

            @Override
            public void onNext(MediaBrowserCompat.MediaItem value) {
                if (mMvpView.isSlidingUpPaneHidden()) {
                    mMvpView.setSlidingUpPaneCollapsed();
                }
            }

            @Override
            public void onError(Throwable e) {

            }

            @Override
            public void onComplete() {

            }
        });
    }

    @Override
    public void onSlidingUpPanelSlide(float slideOffset) {
//        Log.i(TAG, "onSlidingUpPanelSlide:called slideOffset="+slideOffset);
        if (slideOffset >= 0.9) {
            mMvpView.hideMiniPlayer();
        } else if (slideOffset <= 0.1) {
            mMvpView.showMiniPlayer();
        }
    }
}
