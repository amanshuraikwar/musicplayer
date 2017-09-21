package app.sonu.com.musicplayer.ui.medialists;

import android.support.v4.app.FragmentActivity;
import android.util.Log;

import app.sonu.com.musicplayer.AppBus;
import app.sonu.com.musicplayer.R;
import app.sonu.com.musicplayer.data.DataManager;
import app.sonu.com.musicplayer.ui.base.BasePresenter;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;

/**
 * Created by sonu on 15/9/17.
 */

public class MediaListsPresenter
        extends BasePresenter<MediaListsMvpView>
        implements MediaListsMvpPresenter {

    private AppBus mAppBus;

    private Disposable navigationItemSelectedDisposal;

    public MediaListsPresenter(DataManager dataManager,
                               AppBus appBus) {
        super(dataManager);
        mAppBus = appBus;
    }

    @Override
    public void onDetach() {
        navigationItemSelectedDisposal.dispose();
    }

    @Override
    public void onStart() {

    }

    @Override
    public void onPageSelected(int position) {
        mAppBus.medialistSelectedSubject.onNext(position);
    }

    @Override
    public void onCreate(FragmentActivity activity) {
        navigationItemSelectedDisposal =
                mAppBus.navigationItemSelectedSubject.subscribe(new Consumer<Integer>() {
                    @Override
                    public void accept(Integer integer) throws Exception {
                        Log.w("tyoyo", integer+"");
                        switch (integer) {
                            case R.id.menuItemSongs:
                                mMvpView.setMedialistSelected(0);
                                break;
                            case R.id.menuItemAlbums:
                                mMvpView.setMedialistSelected(1);
                                break;
                            case R.id.menuItemArtists:
                                mMvpView.setMedialistSelected(2);
                                break;
                            case R.id.menuItemPlaylists:
                                mMvpView.setMedialistSelected(3);
                                break;
                        }
                    }
                });
    }
}
