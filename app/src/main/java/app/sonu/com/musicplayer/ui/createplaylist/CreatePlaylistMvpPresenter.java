package app.sonu.com.musicplayer.ui.createplaylist;

import android.support.v4.app.FragmentActivity;

import app.sonu.com.musicplayer.ui.base.BaseMvpPresenter;

/**
 * Created by sonu on 23/9/17.
 */

public interface CreatePlaylistMvpPresenter extends BaseMvpPresenter<CreatePlaylistMvpView> {
    void onCreate(FragmentActivity activity);
    void onCreateView();
    void onCreateBtnClick(String playlistTitle);
    void onCancelBtnClick();
}
