package app.sonu.com.musicplayer.ui.base.musicplayerholder;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.sothree.slidinguppanel.SlidingUpPanelLayout;

import app.sonu.com.musicplayer.R;
import app.sonu.com.musicplayer.di.component.MusicPlayerHolderComponent;
import app.sonu.com.musicplayer.ui.base.BaseFragment;
import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by sonu on 14/9/17.
 */

public class MusicPlayerHolderFragment<MvpPresenter extends MusicPlayerHolderMvpPresenter>
        extends BaseFragment<MvpPresenter>
        implements MusicPlayerHolderMvpView {

    @BindView(R.id.childFl)
    FrameLayout childFlTemp;

    @BindView(R.id.miniPlayerParentRl)
    RelativeLayout miniPlayerParentRl;

    @BindView(R.id.parentSupl)
    SlidingUpPanelLayout parentSupl;

    // for child classes to load their required fragments
    protected FrameLayout childFl;

    // for injecting dependencies in fragments inside direct child classes
    // do not forget to initialize the variable in onCreate of direct child class
    protected MusicPlayerHolderComponent mMusicPlayerHolderComponent;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_music_player_holder, container, false);
        ButterKnife.bind(this, view);

        childFl = childFlTemp;

        return view;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        parentSupl.addPanelSlideListener(new SlidingUpPanelLayout.PanelSlideListener() {
            @Override
            public void onPanelSlide(View view, float v) {
                if (v >= 0.9) {
                    hideMiniPlayer();
                } else if (v <= 0.1) {
                    showMiniPlayer();
                }
            }

            @Override
            public void onPanelStateChanged(View view,
                                            SlidingUpPanelLayout.PanelState panelState,
                                            SlidingUpPanelLayout.PanelState panelState1) {
                mPresenter.onMusicPlayerPanelStateChanged(panelState1);
                onSlidingUpPanelStateChanged(panelState1);
            }
        });
    }

    void showMiniPlayer() {
        miniPlayerParentRl.setVisibility(View.VISIBLE);
    }

    void hideMiniPlayer() {
        miniPlayerParentRl.setVisibility(View.GONE);
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public void collapseSlidingUpPanelLayout() {
        parentSupl.setPanelState(SlidingUpPanelLayout.PanelState.COLLAPSED);
    }

    @Override
    public void expandSlidingUpPanelLayout() {
        parentSupl.setPanelState(SlidingUpPanelLayout.PanelState.EXPANDED);
    }

    @Override
    public void hideSlidingUpPanelLayout() {
        parentSupl.setPanelState(SlidingUpPanelLayout.PanelState.HIDDEN);
    }

    @Override
    public boolean isSlidingUpPaneHidden() {
        return parentSupl.getPanelState() == SlidingUpPanelLayout.PanelState.HIDDEN;
    }

    @Override
    public void setAntiDragView(View view) {
        parentSupl.setAntiDragView(view);
    }

    @Override
    public void setSupl(SlidingUpPanelLayout supl) {
        // todo implement
    }

    @Override
    public void displayToast(String message) {
        Toast.makeText(getActivity(), message, Toast.LENGTH_SHORT).show();
    }

    protected void loadChild(Fragment fragment) {
        FragmentManager fragmentManager = getChildFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.childFl, fragment);
        fragmentTransaction.commit();
    }

    public MusicPlayerHolderComponent getMusicPlayerHolderComponent() {
        return mMusicPlayerHolderComponent;
    }

    protected SlidingUpPanelLayout getParentSupl() {
        return parentSupl;
    }

    protected void onSlidingUpPanelStateChanged(SlidingUpPanelLayout.PanelState newState) {
        // override to get notified when state is changed
    }
}
