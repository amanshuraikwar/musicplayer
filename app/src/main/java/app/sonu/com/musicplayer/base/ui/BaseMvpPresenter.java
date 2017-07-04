package app.sonu.com.musicplayer.base.ui;

/**
 * Created by sonu on 29/6/17.
 */

public interface BaseMvpPresenter<MvpView extends BaseMvpView> {
    void onAttach(MvpView mvpView);
    void onDetach();
    MvpView getMvpView();
    void onStart();
}

