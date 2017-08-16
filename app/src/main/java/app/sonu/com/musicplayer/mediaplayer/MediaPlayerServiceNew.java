package app.sonu.com.musicplayer.mediaplayer;

import android.app.Service;
import android.content.Intent;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.TimedText;
import android.os.Binder;
import android.os.IBinder;
import android.support.annotation.IntDef;
import android.support.annotation.Nullable;
import android.util.Log;

import java.io.IOException;

import app.sonu.com.musicplayer.data.db.model.Song;
import app.sonu.com.musicplayer.data.prefs.AppPrefsHelper;

/**
 * Created by sonu on 19/7/17.
 */

public class MediaPlayerServiceNew extends Service
        implements MediaPlayerCallbacks,
        MediaPlayer.OnCompletionListener,
        MediaPlayer.OnPreparedListener,
        MediaPlayer.OnErrorListener,
        MediaPlayer.OnSeekCompleteListener,
        MediaPlayer.OnInfoListener,
        MediaPlayer.OnBufferingUpdateListener{

    private static final String TAG = MediaPlayerServiceNew.class.getSimpleName();

    // Binder given to clients
    private final IBinder mBinder = new LocalBinder();
    private MediaPlayer mediaPlayer;
    private Song activeSong;
    private int resumePosition;
    private boolean preparing = false;

    //media player callbacks
    @Override
    public void play() {

        AppPrefsHelper prefsHelper = new AppPrefsHelper(getApplicationContext());
        activeSong = prefsHelper.getSongQueue().get(prefsHelper.getCurrentSongIndex());

        if (isPlaying()) {
            mediaPlayer.stop();
        }

        //Reset so that the MediaPlayer is not pointing to another data source
        mediaPlayer.reset();

        try {
            // Set the data source to the mediaFile location
            mediaPlayer.setDataSource(activeSong.getPath());
        } catch (IOException | NullPointerException e) {
            e.printStackTrace();
            stopSelf();
        }
        preparing = true;

        mediaPlayer.prepareAsync();
    }

    @Override
    public void pause() {
        if (mediaPlayer == null) {
            return;
        }
        if (mediaPlayer.isPlaying()) {
            mediaPlayer.pause();
            resumePosition = mediaPlayer.getCurrentPosition();
        }
    }

    @Override
    public void resume() {
        if (mediaPlayer == null) {
            return;
        }
        if (!mediaPlayer.isPlaying()) {
            mediaPlayer.seekTo(resumePosition);
            mediaPlayer.start();
        }
    }

    @Override
    public void stop() {
        if (mediaPlayer == null) {
            return;
        }
        if (mediaPlayer.isPlaying()) {
            mediaPlayer.stop();
        }
    }

    @Override
    public void playNext() {

    }

    @Override
    public void playPrevious() {

    }

    @Override
    public boolean isPlaying() {
        if (mediaPlayer == null) {
            return false;
        }
        if (mediaPlayer.isPlaying()) {
            return true;
        } else {
            return false;
        }
    }

    @Override
    public int getCurrentPosition() {
        if (mediaPlayer == null) {
            return 0;
        } else {
            return mediaPlayer.getCurrentPosition();
        }
    }

    @Override
    public int getDuration() {
        if (mediaPlayer == null) {
            return 0;
        } else {
            return mediaPlayer.getDuration();
        }
    }

    //media player implementations
    @Override
    public void onCompletion(MediaPlayer mp) {

    }

    @Override
    public boolean onError(MediaPlayer mp, int what, int extra) {
        return false;
    }

    @Override
    public void onPrepared(MediaPlayer mp) {
        preparing = false;
        mediaPlayer.start();
    }

    @Override
    public boolean onInfo(MediaPlayer mp, int what, int extra) {
        return false;
    }

    @Override
    public void onSeekComplete(MediaPlayer mp) {

    }

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

        play();
    }

    @Override
    public void onBufferingUpdate(MediaPlayer mp, int percent) {
        Log.i(TAG, "percent : "+percent);
    }

    /**
     * Class used for the client Binder.  Because we know this service always
     * runs in the same process as its clients, we don't need to deal with IPC.
     */
    public class LocalBinder extends Binder {

        public MediaPlayerCallbacks getService() {
            // Return this instance of LocalService so clients can call public methods
            return MediaPlayerServiceNew.this;
        }
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        Log.d(TAG, "onBind:called");
        initMediaPlayer();
        return mBinder;
    }

    @Override
    public void onCreate() {
        Log.d(TAG, "onCreate:called");
        super.onCreate();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.d(TAG, "onStartCommand:called");
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void onDestroy() {
        Log.d(TAG, "onDestroy:called");
        super.onDestroy();
    }
}
