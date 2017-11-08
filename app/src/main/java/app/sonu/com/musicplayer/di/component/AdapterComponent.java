package app.sonu.com.musicplayer.di.component;

import app.sonu.com.musicplayer.di.PerAdapter;
import app.sonu.com.musicplayer.di.module.AdapterModule;
import app.sonu.com.musicplayer.ui.adapter.simple.SimpleMediaListAdapter;
import dagger.Component;

/**
 * Created by sonu on 8/11/17.
 */

@PerAdapter
@Component(modules = {AdapterModule.class}, dependencies = {ApplicationComponent.class})
public interface AdapterComponent {
    void inject(SimpleMediaListAdapter adapter);

    @Component.Builder
    interface Builder {
        AdapterComponent.Builder applicationComponent(ApplicationComponent applicationComponent);
        AdapterComponent.Builder adapterModule(AdapterModule module);
        AdapterComponent build();
    }
}
