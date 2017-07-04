package app.sonu.com.musicplayer.base.ui;

import app.sonu.com.musicplayer.data.DataManager;

/**
 * Created by sonu on 29/6/17.
 */

public abstract class BasePresenter<MvpView extends BaseMvpView> implements BaseMvpPresenter<MvpView>{
    protected MvpView mMvpView;
    protected DataManager mDataManager;

    public BasePresenter(DataManager dataManager) {
        this.mDataManager = dataManager;
    }

    @Override
    public void onAttach(MvpView mvpView) {
        this.mMvpView = mvpView;
    }

    @Override
    public MvpView getMvpView() {
        return mMvpView;
    }
}
