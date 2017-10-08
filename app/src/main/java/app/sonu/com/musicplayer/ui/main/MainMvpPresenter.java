package app.sonu.com.musicplayer.ui.main;

import android.support.v4.app.FragmentActivity;

import app.sonu.com.musicplayer.ui.base.BaseMvpPresenter;

/**
 * Created by sonu on 15/9/17.
 */

public interface MainMvpPresenter extends BaseMvpPresenter<MainMvpView> {
    void onCreate(FragmentActivity activity);
    void onNavigationItemSelected(int itemId);
    void onNavigationItemReselected(int itemId);
    void disableFirstRunFlag();
}
