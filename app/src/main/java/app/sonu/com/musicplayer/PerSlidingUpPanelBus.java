package app.sonu.com.musicplayer;

import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.sothree.slidinguppanel.SlidingUpPanelLayout;

import javax.inject.Inject;
import javax.inject.Named;

import app.sonu.com.musicplayer.di.component.DaggerBusComponent;
import app.sonu.com.musicplayer.di.component.DaggerPerSlidingUpPanelBusComponent;
import app.sonu.com.musicplayer.di.module.BusModule;
import app.sonu.com.musicplayer.di.module.PerSlidingUpPanelBusModule;
import io.reactivex.subjects.PublishSubject;

/**
 * Created by sonu on 14/9/17.
 */

public class PerSlidingUpPanelBus {

    @Inject
    @Named(PerSlidingUpPanelBusModule.PROVIDER_SET_ANTIDRAG_VIEW)
    public PublishSubject<View> setAntidragViewSubject;

    @Inject
    @Named(PerSlidingUpPanelBusModule.PROVIDER_SET_DRAG_VIEW)
    public PublishSubject<View> setDragViewSubject;

    @Inject
    @Named(PerSlidingUpPanelBusModule.PROVIDER_SET_SUPL)
    public PublishSubject<SlidingUpPanelLayout> setSuplSubject;

    @Inject
    @Named(PerSlidingUpPanelBusModule.PROVIDER_SET_SCROLL_VIEW)
    public PublishSubject<RecyclerView> setScrollViewSubject;

    public PerSlidingUpPanelBus() {
        DaggerPerSlidingUpPanelBusComponent
                .builder()
                .perSlidingUpPanelBusModule(new PerSlidingUpPanelBusModule())
                .build()
                .inject(this);
    }
}
