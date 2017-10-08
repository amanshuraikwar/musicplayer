package app.sonu.com.musicplayer.ui.base.musicplayerholder;

import android.view.View;

import com.sothree.slidinguppanel.SlidingUpPanelLayout;

import app.sonu.com.musicplayer.ui.base.BaseMvpView;

/**
 * Created by sonu on 14/9/17.
 */

public interface MusicPlayerHolderMvpView extends BaseMvpView {
    void collapseSlidingUpPanelLayout();
    void expandSlidingUpPanelLayout();
    void hideSlidingUpPanelLayout();
    boolean isSlidingUpPaneHidden();
    void setAntiDragView(View view);
    void setSupl(SlidingUpPanelLayout supl);
    void displayToast(String message);
}
