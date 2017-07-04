package app.sonu.com.musicplayer.ui.main;

import app.sonu.com.musicplayer.base.ui.BasePresenter;
import app.sonu.com.musicplayer.data.DataManager;

/**
 * Created by sonu on 29/6/17.
 */

public class MainPresenter extends BasePresenter<MainMvpView> implements MainMvpPresenter{

    public MainPresenter(DataManager dataManager) {
        super(dataManager);
    }

    @Override
    public void onDetach() {

    }

    @Override
    public void onStart() {

    }
}
