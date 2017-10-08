package app.sonu.com.musicplayer.ui.base.medialist;

import android.support.v4.app.FragmentActivity;
import android.support.v4.media.MediaBrowserCompat;

import app.sonu.com.musicplayer.ui.base.BaseMvpPresenter;

/**
 * Created by sonu on 4/10/17.
 */

public interface MediaListMvpPresenter<MvpView extends MediaListMvpView>
        extends BaseMvpPresenter<MvpView> {
    void onCreateView();
    void onCreate(FragmentActivity activity, String rootMediaId);
    String getRootMediaId();
}
