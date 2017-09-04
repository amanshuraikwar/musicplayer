package app.sonu.com.musicplayer.base.ui;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentActivity;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import javax.inject.Inject;

/**
 * Created by sonu on 29/6/17.
 * base activity
 */

public abstract class BaseActivity<MvpPresenter extends BaseMvpPresenter> extends AppCompatActivity
        implements BaseMvpView {

    @Inject
    protected MvpPresenter mPresenter;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public void onStart() {
        super.onStart();

        // there is a warning because the compiler does not already know if
        // BaseMvpView's child implemented by this class is same as BaseMvpView's
        // child given to Presenter
        mPresenter.onAttach(this);

        mPresenter.onStart();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mPresenter.onDetach();
    }

    @Override
    protected void onStop() {
        super.onStop();
    }
}
