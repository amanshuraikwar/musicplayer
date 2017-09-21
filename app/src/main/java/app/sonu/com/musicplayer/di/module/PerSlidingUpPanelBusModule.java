package app.sonu.com.musicplayer.di.module;

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
}
