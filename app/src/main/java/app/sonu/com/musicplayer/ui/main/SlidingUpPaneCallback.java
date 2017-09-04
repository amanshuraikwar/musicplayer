package app.sonu.com.musicplayer.ui.main;

import android.view.View;

import com.sothree.slidinguppanel.SlidingUpPanelLayout;

/**
 * Created by sonu on 4/8/17.
 */

public interface SlidingUpPaneCallback {
    void setAntiDragViewNow(View view);
    void setPaneLayout(SlidingUpPanelLayout slidingUpPanelLayout);
}
