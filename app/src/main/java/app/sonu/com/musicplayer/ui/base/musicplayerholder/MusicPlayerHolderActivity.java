package app.sonu.com.musicplayer.ui.base.musicplayerholder;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.View;
import android.widget.FrameLayout;

import com.sothree.slidinguppanel.SlidingUpPanelLayout;

import app.sonu.com.musicplayer.R;
import app.sonu.com.musicplayer.di.component.MusicPlayerHolderComponent;
import app.sonu.com.musicplayer.ui.base.BaseActivity;

/**
 * Created by sonu on 14/9/17.
 */

public class MusicPlayerHolderActivity<MvpPresenter extends MusicPlayerHolderMvpPresenter>
        extends BaseActivity<MvpPresenter>
        implements MusicPlayerHolderMvpView {

    protected MusicPlayerHolderFragment musicPlayerHolderFragment;

    // for child classes to load their required fragments
    protected FrameLayout childFl;

    // for injecting dependencies in fragments inside direct child classes
    // do not forget to initialize the variable in onCreate of direct child class
    protected MusicPlayerHolderComponent mMusicPlayerHolderComponent;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_music_player_holder);

        musicPlayerHolderFragment =
                (MusicPlayerHolderFragment)
                        getSupportFragmentManager()
                                .findFragmentById(R.id.emptyMusicPlayerHolderFragment);

        childFl = musicPlayerHolderFragment.childFl;
        mMusicPlayerHolderComponent = musicPlayerHolderFragment.mMusicPlayerHolderComponent;
    }

    @Override
    public void collapseSlidingUpPanelLayout() {
        musicPlayerHolderFragment.collapseSlidingUpPanelLayout();
    }

    @Override
    public void expandSlidingUpPanelLayout() {
        musicPlayerHolderFragment.expandSlidingUpPanelLayout();
    }

    @Override
    public void hideSlidingUpPanelLayout() {
        musicPlayerHolderFragment.hideSlidingUpPanelLayout();
    }

    @Override
    public boolean isSlidingUpPaneHidden() {
        return musicPlayerHolderFragment.isSlidingUpPaneHidden();
    }

    @Override
    public void setAntiDragView(View view) {
        musicPlayerHolderFragment.setAntiDragView(view);
    }

    @Override
    public void setSupl(SlidingUpPanelLayout supl) {
        musicPlayerHolderFragment.setSupl(supl);
    }

    protected void loadChild(Fragment fragment) {
        musicPlayerHolderFragment.loadChild(fragment);
    }

    public MusicPlayerHolderComponent getMusicPlayerHolderComponent() {
        return mMusicPlayerHolderComponent;
    }

    protected SlidingUpPanelLayout getParentSupl() {
        return musicPlayerHolderFragment.getParentSupl();
    }
}
