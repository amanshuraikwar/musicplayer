package app.sonu.com.musicplayer.base.ui;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;

import javax.inject.Inject;

/**
 * Created by sonu on 29/6/17.
 * base fragment
 */

public abstract class BaseFragment<MvpPresenter extends BaseMvpPresenter> extends Fragment
        implements BaseMvpView {

    @Inject
    protected MvpPresenter mPresenter;

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        // there is a warning because the compiler does not already know if
        // BaseMvpView's child implemented by this class is same as BaseMvpView's
        // child given to Presenter
        mPresenter.onAttach(this);
    }

    @Override
    public void onStart() {
        super.onStart();
        mPresenter.onStart();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mPresenter.onDetach();
    }
}
