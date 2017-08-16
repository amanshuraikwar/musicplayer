package app.sonu.com.musicplayer.ui.main;

import app.sonu.com.musicplayer.base.ui.BaseMvpPresenter;

/**
 * Created by sonu on 29/6/17.
 */

public interface MainMvpPresenter extends BaseMvpPresenter<MainMvpView> {
    void onSlidingUpPanelSlide(float slideOffset);
}
