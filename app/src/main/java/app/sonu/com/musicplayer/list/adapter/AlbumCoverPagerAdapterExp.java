package app.sonu.com.musicplayer.list.adapter;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.media.session.MediaSessionCompat;
import android.support.v4.view.LayoutInflaterCompat;
import android.support.v4.view.PagerAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.List;

import app.sonu.com.musicplayer.R;
import app.sonu.com.musicplayer.ui.albumart.AlbumArtFragment;

/**
 * Created by sonu on 4/10/17.
 */

public class AlbumCoverPagerAdapterExp extends PagerAdapter {

    private List<MediaSessionCompat.QueueItem> queue;
    private Context mContext;
    private LayoutInflater layoutInflater;

    public AlbumCoverPagerAdapterExp(@NonNull Context context,
                                     @NonNull List<MediaSessionCompat.QueueItem> queue) {
        mContext = context;
        this.queue = queue;
        layoutInflater = LayoutInflater.from(context);
    }

    @Override
    public Object instantiateItem(ViewGroup container, int position) {

        ViewGroup layout = (ViewGroup) layoutInflater.inflate(R.layout.item_album_art,
                container, false);
        container.addView(layout);
        return layout;
    }

    @Override
    public void destroyItem(ViewGroup collection, int position, Object view) {
        collection.removeView((View) view);
    }

    @Override
    public int getCount() {
        return queue.size();
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return view == object;
    }

    public MediaSessionCompat.QueueItem getQueueItem(int position) {
        return queue.get(position);
    }

    private Fragment getAlbumArtFragment(MediaSessionCompat.QueueItem queueItem) {
        AlbumArtFragment fragment = new AlbumArtFragment();
        Bundle args = new Bundle();
        args.putParcelable("queueItem", queueItem);
        fragment.setArguments(args);
        return fragment;
    }
}

