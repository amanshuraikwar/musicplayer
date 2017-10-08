package app.sonu.com.musicplayer.list.adapter;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.media.session.MediaSessionCompat;

import java.util.ArrayList;
import java.util.List;

import app.sonu.com.musicplayer.ui.albumart.AlbumArtFragment;

/**
 * Created by sonu on 4/10/17.
 */

public class AlbumCoverPagerAdapter extends FragmentStatePagerAdapter{

    private List<MediaSessionCompat.QueueItem> queue;

    public AlbumCoverPagerAdapter(FragmentManager fm, @NonNull List<MediaSessionCompat.QueueItem> queue) {
        super(fm);
        this.queue = queue;
    }

    @Override
    public Fragment getItem(int position) {
        return getAlbumArtFragment(queue.get(position));
    }

    @Override
    public int getCount() {
        return queue.size();
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

