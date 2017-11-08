package app.sonu.com.musicplayer.di.module;

import android.support.v4.util.Pair;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.sothree.slidinguppanel.SlidingUpPanelLayout;

import javax.inject.Named;

import app.sonu.com.musicplayer.di.PerFragment;
import dagger.Module;
import dagger.Provides;
import io.reactivex.subjects.PublishSubject;

/**
 * Created by sonu on 15/9/17.
 */

@Module
public class PerSlidingUpPanelBusModule {
    public static final String PROVIDER_SET_ANTIDRAG_VIEW = "SET_ANTIDRAG_VIEW";
    public static final String PROVIDER_SET_DRAG_VIEW = "SET_DRAG_VIEW";
    public static final String PROVIDER_SET_SUPL = "SET_SUPL";
    public static final String PROVIDER_SET_SCROLL_VIEW = "SET_SCROLL_VIEW";
    public static final String PROVIDER_DARK_COLOR_CHANGED = "DARK_COLOR_CHANGED";
    public static final String PROVIDER_LIGHT_COLOR_CHANGED = "LIGHT_COLOR_CHANGED";
    public static final String PROVIDER_BOTTOM_HALF_PANEL_STATE_CHANGED = "BOTTOM_HALF_PANEL_STATE_CHANGED";

    @Provides
    @Named(PROVIDER_SET_ANTIDRAG_VIEW)
    PublishSubject<View> getSetAntidragViewSubject() {
        return PublishSubject.create();
    }

    @Provides
    @Named(PROVIDER_SET_DRAG_VIEW)
    PublishSubject<View> getDragViewSubject() {
        return PublishSubject.create();
    }

    @Provides
    @Named(PROVIDER_SET_SUPL)
    PublishSubject<SlidingUpPanelLayout> getSetSuplSubject() {
        return PublishSubject.create();
    }

    @Provides
    @Named(PROVIDER_SET_SCROLL_VIEW)
    PublishSubject<RecyclerView> getScrollViewSubject() {
        return PublishSubject.create();
    }

    @Provides
    @Named(PROVIDER_DARK_COLOR_CHANGED)
    PublishSubject<Pair<Integer, Integer>> getDarkColorChangedSubject() {
        return PublishSubject.create();
    }

    @Provides
    @Named(PROVIDER_LIGHT_COLOR_CHANGED)
    PublishSubject<Pair<Integer, Integer>> getLightColorChangedSubject() {
        return PublishSubject.create();
    }

    @Provides
    @Named(PROVIDER_BOTTOM_HALF_PANEL_STATE_CHANGED)
    PublishSubject<SlidingUpPanelLayout.PanelState> getBottomHalfPanelStateChangedSubject() {
        return PublishSubject.create();
    }
}
