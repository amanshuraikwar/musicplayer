package app.sonu.com.musicplayer.ui.base.musicplayerholder;

import android.os.Bundle;
import android.support.annotation.Nullable;

import app.sonu.com.musicplayer.MyApplication;
import app.sonu.com.musicplayer.di.component.DaggerMusicPlayerHolderComponent;

import app.sonu.com.musicplayer.di.module.FragmentModule;
import app.sonu.com.musicplayer.di.module.MusicPlayerHolderModule;

/**
 * Created by sonu on 14/9/17.
 */

public class EmptyMusicPlayerHolderFragment extends
        MusicPlayerHolderFragment<MusicPlayerHolderMvpPresenter> {

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mMusicPlayerHolderComponent =
                DaggerMusicPlayerHolderComponent
                        .builder()
                        .musicPlayerHolderModule(new MusicPlayerHolderModule())
                        .applicationComponent(
                                ((MyApplication)getActivity().getApplicationContext())
                                        .getApplicationComponent())
                        .build();

        mMusicPlayerHolderComponent
                .fragmentComponentBuilder()
                .fragmentModule(new FragmentModule())
                .build()
                .inject(this);

        mPresenter.onCreate(getActivity());
    }
}
