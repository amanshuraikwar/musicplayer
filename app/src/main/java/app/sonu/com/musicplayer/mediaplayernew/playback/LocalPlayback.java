package app.sonu.com.musicplayer.mediaplayernew.playback;

import android.content.Context;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.support.v4.media.MediaMetadataCompat;
import android.support.v4.media.session.MediaSessionCompat;
import android.support.v4.media.session.PlaybackStateCompat;
import android.util.Log;

import java.io.IOException;

import app.sonu.com.musicplayer.mediaplayernew.util.MediaIdHelper;
import app.sonu.com.musicplayer.mediaplayernew.MusicProvider;
import app.sonu.com.musicplayer.mediaplayernew.musicsource.MusicProviderSource;

/**
 * Created by sonu on 29/7/17.
 */

public class LocalPlayback
        implements
        Playback,
        MediaPlayer.OnCompletionListener,
        MediaPlayer.OnErrorListener,
        MediaPlayer.OnPreparedListener,
        MediaPlayer.OnSeekCompleteListener,
        MediaPlayer.OnInfoListener,
        MediaPlayer.OnBufferingUpdateListener {

    private static final String TAG = LocalPlayback.class.getSimpleName();

    private final MusicProvider mMusicProvider;
    private final Context mContext;
    private Callback mCallback;

    private MediaPlayer mediaPlayer;
    private int resumePosition;
    private boolean paused;

    private void initMediaPlayer() {
        mediaPlayer = new MediaPlayer();

        //Set up MediaPlayer event listeners
        mediaPlayer.setOnCompletionListener(this);
        mediaPlayer.setOnErrorListener(this);
        mediaPlayer.setOnPreparedListener(this);
        mediaPlayer.setOnSeekCompleteListener(this);
        mediaPlayer.setOnInfoListener(this);
        mediaPlayer.setOnBufferingUpdateListener(this);
        mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
    }

    public LocalPlayback(Context context, MusicProvider musicProvider) {
        Context applicationContext = context.getApplicationContext();
        this.mContext = applicationContext;
        this.mMusicProvider = musicProvider;

        initMediaPlayer();
    }


    @Override
    public void start() {
        //nothing to do
    }

    @Override
    public void stop() {
        //todo release resources
    }

    @Override
    public void play() {
        if (!mediaPlayer.isPlaying()) {
            mediaPlayer.seekTo(resumePosition);
            mediaPlayer.start();
            mCallback.onPlaybackStatusChanged(getState());
            paused = false;
        }
    }

    @Override
    public void pause() {
        if (mediaPlayer.isPlaying()) {
            resumePosition = mediaPlayer.getCurrentPosition();
            mediaPlayer.pause();
            paused = true;
            mCallback.onPlaybackStatusChanged(getState());
        }
    }

    @Override
    public boolean isPlaying() {
        return mediaPlayer.isPlaying();
    }


    @Override
    public void play(MediaSessionCompat.QueueItem item) {
        Log.d(TAG, "play:called");
        Log.d(TAG, "play:item="+item);

        try {
            Log.i(TAG, "play:id=" + MediaIdHelper.extractMusicIdFromMediaId(
                    item.getDescription().getMediaId()));

            MediaMetadataCompat track =
                    mMusicProvider.getMusic(
                            MediaIdHelper.extractMusicIdFromMediaId(
                                    item.getDescription().getMediaId()));

            String source = track.getString(MusicProviderSource.CUSTOM_METADATA_KEY_TRACK_SOURCE);

            Log.d(TAG, "play:source=" + source);

            if (mediaPlayer.isPlaying()) {
                mediaPlayer.stop();
            }

            //Reset so that the MediaPlayer is not pointing to another data source
            mediaPlayer.reset();

            try {
                // Set the data source to the mediaFile location
                mediaPlayer.setDataSource(source);
            } catch (IOException | NullPointerException e) {
                e.printStackTrace();
            }
            mediaPlayer.prepareAsync();
        } catch (Exception e) {
            e.printStackTrace();
            Log.e(TAG, "play:exception:"+e);
        }
    }

    @Override
    public int getState() {
        if (mediaPlayer == null) {
            return PlaybackStateCompat.STATE_NONE;
        }

        if (mediaPlayer.isPlaying()) {
            return PlaybackStateCompat.STATE_PLAYING;
        } else if (paused) {
            return PlaybackStateCompat.STATE_PAUSED;
        } else {
            return PlaybackStateCompat.STATE_STOPPED;
        }
    }

    @Override
    public void setCallback(Callback callback) {
        this.mCallback = callback;
    }

    @Override
    public long getCurrentPosition() {
        if (mediaPlayer != null) {
            return mediaPlayer.getCurrentPosition();
        } else {
            return 0;
        }
    }

    @Override
    public void seekTo(long position) {
        if (mediaPlayer != null) {
            Log.d(TAG, "seekTo:positionBeforeSeek="+getCurrentPosition());
            mediaPlayer.seekTo((int) position);
        }
    }

    //media player implementations
    @Override
    public void onCompletion(MediaPlayer mp) {
        mCallback.onCompletion();
    }

    @Override
    public boolean onError(MediaPlayer mp, int what, int extra) {
        return false;
    }

    @Override
    public void onPrepared(MediaPlayer mp) {
        mediaPlayer.start();
        mCallback.onPlaybackStatusChanged(getState());
    }

    @Override
    public void onSeekComplete(MediaPlayer mp) {
        Log.d(TAG, "onSeekComplete:called");
        Log.d(TAG, "onSeekComplete:positionAfterSeek="+getCurrentPosition());
        mCallback.onPlaybackStatusChanged(getState());
        resumePosition = (int)getCurrentPosition();
    }

    @Override
    public boolean onInfo(MediaPlayer mp, int what, int extra) {
        return false;
    }

    @Override
    public void onBufferingUpdate(MediaPlayer mp, int percent) {

    }
}
