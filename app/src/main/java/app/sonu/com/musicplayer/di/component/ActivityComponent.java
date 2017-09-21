package app.sonu.com.musicplayer.di.component;

import app.sonu.com.musicplayer.di.PerActivity;
import app.sonu.com.musicplayer.di.module.ActivityModule;
import app.sonu.com.musicplayer.ui.album.AlbumFragment;
import app.sonu.com.musicplayer.ui.main.MainActivity;
import app.sonu.com.musicplayer.ui.mediaitemdetail.MediaItemDetailActivity;
import app.sonu.com.musicplayer.ui.search.SearchActivity;
import dagger.Subcomponent;

/**
 * Created by sonu on 15/9/17.
 */

@PerActivity
@Subcomponent(modules = {ActivityModule.class})
public interface ActivityComponent {
    void inject(MainActivity activity);
    void inject(MediaItemDetailActivity activity);
    void inject(SearchActivity activity);

    @Subcomponent.Builder
    interface Builder {
        ActivityComponent.Builder activityModule(ActivityModule module);
        ActivityComponent build();
    }
}
