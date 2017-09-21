package app.sonu.com.musicplayer.ui.medialists;

import android.support.v4.app.FragmentActivity;

import app.sonu.com.musicplayer.ui.base.BaseMvpPresenter;

/**
 * Created by sonu on 15/9/17.
 */

public interface MediaListsMvpPresenter
        extends BaseMvpPresenter<MediaListsMvpView> {
    void onPageSelected(int position);
    void onCreate(FragmentActivity activity);
}
