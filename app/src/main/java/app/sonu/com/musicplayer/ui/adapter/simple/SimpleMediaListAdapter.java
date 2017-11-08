package app.sonu.com.musicplayer.ui.adapter.simple;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v4.app.FragmentActivity;

import app.sonu.com.musicplayer.MyApplication;
import app.sonu.com.musicplayer.di.component.DaggerAdapterComponent;
import app.sonu.com.musicplayer.di.module.AdapterModule;
import app.sonu.com.musicplayer.list.MediaListTypeFactory;
import app.sonu.com.musicplayer.ui.adapter.BaseMediaListAdapter;
import app.sonu.com.musicplayer.ui.adapter.MediaListBuilder;

/**
 * Created by sonu on 8/11/17.
 */

public class SimpleMediaListAdapter extends BaseMediaListAdapter<SimpleMediaListMvpPresenter>
        implements SimpleMediaListMvpView {

    public SimpleMediaListAdapter(@NonNull FragmentActivity activity,
                                  @NonNull MediaListTypeFactory typeFactory,
                                  @NonNull String mediaId,
                                  @NonNull MediaListBuilder listBuilder) {
        super(activity, typeFactory, mediaId, listBuilder);

        DaggerAdapterComponent
                .builder()
                .adapterModule(new AdapterModule(activity))
                .applicationComponent(
                        ((MyApplication)activity.getApplicationContext()).getApplicationComponent())
                .build()
                .inject(this);
    }
}
