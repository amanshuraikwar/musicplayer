package app.sonu.com.musicplayer.mediaplayer.playback;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.PowerManager;
import android.support.annotation.NonNull;
import android.support.v4.media.MediaMetadataCompat;
import android.support.v4.media.session.MediaSessionCompat;
import android.util.Log;

import java.io.IOException;

import app.sonu.com.musicplayer.mediaplayer.MusicService;
import app.sonu.com.musicplayer.util.MediaIdHelper;
import app.sonu.com.musicplayer.mediaplayer.media.MediaProvider;
import app.sonu.com.musicplayer.util.MediaMetadataHelper;
import app.sonu.com.musicplayer.util.QueueHelper;

/**
 * Created by sonu on 29/7/17.
 * this class represents local playback of music
 * @author amanshu
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

    // The volume we set the media player to when we lose audio focus, but are
    // allowed to reduce the volume instead of stopping playback.
    private static final float VOLUME_DUCK = 0.2f;
    // The volume we set the media player when we have audio focus.
    private static final float VOLUME_NORMAL = 1.0f;

    // we don't have audio focus, and can't duck (play at a low volume)
    private static final int AUDIO_NO_FOCUS_NO_DUCK = 0;
    // we don't have focus, but can duck (play at a low volume)
    private static final int AUDIO_NO_FOCUS_CAN_DUCK = 1;
    // we have full audio focus
    private static final int AUDIO_FOCUSED = 2;

    private static final String focusStates[] = {"no focus no duck", "no focus can duck", "focus"};

    private final MediaProvider mMediaProvider;
    private final Context mContext;

    private Callback mCallback;
    private MediaPlayer mMediaPlayer;

    private volatile int mResumePosition;
    private volatile int mState;
    private volatile boolean mPlayOnFocusGain;
    private volatile boolean mAudioNoisyReceiverRegistered;
    private volatile int mCurrentAudioFocusState = AUDIO_NO_FOCUS_NO_DUCK;

    private final AudioManager mAudioManager;


    private final IntentFilter mAudioNoisyIntentFilter =
            new IntentFilter(AudioManager.ACTION_AUDIO_BECOMING_NOISY);

    private final BroadcastReceiver mAudioNoisyReceiver =
            new BroadcastReceiver() {
                @Override
                public void onReceive(Context context, Intent intent) {
                    if (AudioManager.ACTION_AUDIO_BECOMING_NOISY.equals(intent.getAction())) {
                        Log.d(TAG, "AudioNoisyReciever:Headphones disconnected.");
                        if (isPlaying()) {
                            Intent i = new Intent(context, MusicService.class);
                            i.setAction(MusicService.ACTION_CMD);
                            i.putExtra(MusicService.CMD_NAME, MusicService.CMD_PAUSE);
                            mContext.startService(i);
                        }
                    }
                }
            };

    public LocalPlayback(Context context, MediaProvider mediaProvider) {
        this.mContext = context.getApplicationContext();
        this.mMediaProvider = mediaProvider;

        this.mAudioManager =
                (AudioManager) mContext.getSystemService(Context.AUDIO_SERVICE);

        initMediaPlayer();
    }

    private void initMediaPlayer() {
        Log.d(TAG, "initMediaPlayer():called");
        mMediaPlayer = new MediaPlayer();

        //Set up MediaPlayer event listeners
        mMediaPlayer.setOnCompletionListener(this);
        mMediaPlayer.setOnErrorListener(this);
        mMediaPlayer.setOnPreparedListener(this);
        mMediaPlayer.setOnSeekCompleteListener(this);
        mMediaPlayer.setOnInfoListener(this);
        mMediaPlayer.setOnBufferingUpdateListener(this);
        mMediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
        mMediaPlayer.setWakeMode(mContext, PowerManager.PARTIAL_WAKE_LOCK);

        mResumePosition = 0;
        mState = Playback.CUSTOM_PLAYBACK_STATE_NONE;
    }

    @Override
    public boolean isPlaying() {
        return mState == Playback.CUSTOM_PLAYBACK_STATE_PLAYING;
    }

    @Override
    public boolean isStopped() {
        return mState == Playback.CUSTOM_PLAYBACK_STATE_STOPPED;
    }

    @Override
    public boolean isPaused() {
        return mState == Playback.CUSTOM_PLAYBACK_STATE_PAUSED;
    }

    @Override
    public boolean isIdle() {
        return mState == Playback.CUSTOM_PLAYBACK_STATE_IDLE;
    }

    @Override
    public boolean isPreparing() {
        return mState == Playback.CUSTOM_PLAYBACK_STATE_PREPARING;
    }

    @Override
    public int getState() {
        return mState;
    }

    @Override
    public int getCurrentPosition() {
        if (isPlaying() || isPaused()) {
            return mMediaPlayer.getCurrentPosition();
        }
        return -1;
    }

    @Override
    public void start() {
        //nothing to do
    }

    @Override
    public synchronized boolean play(@NonNull MediaSessionCompat.QueueItem item) {
        Log.d(TAG, "play:called");
        Log.i(TAG, "play:item="+item);
        Log.i(TAG, "play:state="+Playback.states[mState]);

        // if preparing we can't do anything
        if (isPreparing()) {
            Log.w(TAG, "play:player is preparing");
            return false;
        }

        // to play the player when configurePlayerState is called
        mPlayOnFocusGain = true;
        // try getting audio focus
        tryToGetAudioFocus();
        // register audio noisy receiver for headphone plug/unplug
        registerAudioNoisyReceiver();

        String mediaId = QueueHelper.getMediaIdOf(item);
        Log.i(TAG, "play:mediaId=" + mediaId);

        // check for mediaId
        if (mediaId == null) {
            Log.w(TAG, "play:mediaId is null");
            return false;
        }

        String songId = MediaIdHelper.getSongIdFromMediaId(mediaId);
        Log.i(TAG, "play:songId=" + songId);

        // check for musicId
        if (songId == null) {
            Log.w(TAG, "play:songId is null");
            return false;
        }

        MediaMetadataCompat track = mMediaProvider.getSongBySongId(songId);

        // check for track in library
        if (track == null) {
            Log.w(TAG, "play:track not in library");
            return false;
        }

        // stop player if playing
        stopPlayer();
        // reset player for a new source
        resetPlayer();

        String source = track.getString(MediaMetadataHelper.CUSTOM_METADATA_KEY_SOURCE);
        Log.i(TAG, "play:source=" + source);

        // set new data source
        if (setPlayerDataSource(source)) {
            Log.i(TAG, "play:set data resource successfully, go for preparing");
            // prepare player
            return preparePlayer();
        } else {
            Log.w(TAG, "play:cannot set data source");
            return false;
        }
    }

    @Override
    public synchronized boolean play() {
        Log.d(TAG, "play(no):called");
        Log.i(TAG, "play(no):state="+Playback.states[mState]);

        // if preparing we can't do anything
        if (isPreparing()) {
            Log.w(TAG, "play(no):player is preparing");
            return false;
        }

        // set flag to true to start playing when configurePlayerState is called
        mPlayOnFocusGain = true;
        // try to get audio focus
        tryToGetAudioFocus();
        // register audio noisy receiver for headphone plug/unplug
        registerAudioNoisyReceiver();

        return configurePlayerState();
    }

    @Override
    public synchronized boolean pause() {
        Log.d(TAG, "pause():called");
        Log.i(TAG, "pause:state="+Playback.states[mState]);

        // if preparing we can't do anything
        if (isPreparing()) {
            Log.w(TAG, "pause:player is preparing");
            return false;
        }

        if (pausePlayer()) {
            Log.i(TAG, "pause():paused successfully");
            // we don't want to listen to headphone plug/unplug while paused
            unregisterAudioNoisyReceiver();
            // tell playback manager for the state change
            mCallback.onPlaybackStatusChanged(mState);
            return true;
        }
        return false;
    }

    @Override
    public synchronized boolean stop() {
        Log.d(TAG, "stop():called");
        Log.i(TAG, "stop:state="+Playback.states[mState]);

        // if preparing we can't do anything
        if (isPreparing()) {
            Log.w(TAG, "stop:player is preparing");
            return false;
        }

        if (stopPlayer()) {
            Log.i(TAG, "stop:stopped successfully");
            // we don't want audio focus when the player stops
            giveUpAudioFocus();
            // we don't want to listen to headphone plug/unplug when player stops
            unregisterAudioNoisyReceiver();
            // tell playback manager about state change
            mCallback.onPlaybackStatusChanged(mState);
            return true;
        }
        return false;
    }

    @Override
    public synchronized void seekTo(int position) {
        Log.d(TAG, "seekTo():called");
        Log.i(TAG, "seekTo:state="+Playback.states[mState]);
        // if preparing we can't do anything
        if (isPreparing()) {
            Log.w(TAG, "seekTo:player is preparing");
            return;
        }

        seekPlayerTo(position);
    }

    @Override
    public synchronized void setCallback(Callback callback) {
        // if preparing we can't do anything
        if (isPreparing()) {
            Log.w(TAG, "setCallback:player is preparing");
            return;
        }

        this.mCallback = callback;
    }

    //media player implementations
    @Override
    public void onCompletion(MediaPlayer mp) {
        Log.d(TAG, "onCompletion():called");
        // set state to paused
        mState = Playback.CUSTOM_PLAYBACK_STATE_PAUSED;
        // seek player to zero to reset seekbar on ui
        mMediaPlayer.seekTo(0);
        // set resume position zero if the ui plays
        mResumePosition = 0;
        // tell playback manager that playback has completed
        mCallback.onCompletion();
    }

    @Override
    public boolean onError(MediaPlayer mp, int what, int extra) {
        mCallback.onError("what="+what+" extra="+extra);
        return true;
    }

    @Override
    public void onPrepared(MediaPlayer mp) {
        Log.d(TAG, "onPrepared:called");
        // set state to paused if player cannot play
        mState = Playback.CUSTOM_PLAYBACK_STATE_PAUSED;
        // set resume position zero because new song is being played
        mResumePosition = 0;
        configurePlayerState();
    }

    @Override
    public void onSeekComplete(MediaPlayer mp) {
        Log.d(TAG, "onSeekComplete:called");
        Log.d(TAG, "onSeekComplete:positionAfterSeek="+getCurrentPosition());

        // store the resume position
        mResumePosition = mMediaPlayer.getCurrentPosition();

        // tell playback manager about the state so that the
        // ui can be updated with new seekbar position
        if (isPlaying()) {
            mCallback.onPlaybackStatusChanged(mState);
        } else if (isPaused()) {
            mCallback.onPlaybackStatusChanged(mState);
        }

    }

    @Override
    public boolean onInfo(MediaPlayer mp, int what, int extra) {
        // nothing
        return false;
    }

    @Override
    public void onBufferingUpdate(MediaPlayer mp, int percent) {
        //nothing
    }

    private final AudioManager.OnAudioFocusChangeListener mOnAudioFocusChangeListener =
            new AudioManager.OnAudioFocusChangeListener() {
                @Override
                public void onAudioFocusChange(int focusChange) {
                    Log.d(TAG, "onAudioFocusChange:called");
                    Log.i(TAG, "onAudioFocusChange:focusChange="+focusChange);

                    switch (focusChange) {
                        case AudioManager.AUDIOFOCUS_GAIN:
                            mCurrentAudioFocusState = AUDIO_FOCUSED;
                            break;
                        case AudioManager.AUDIOFOCUS_LOSS_TRANSIENT_CAN_DUCK:
                            // Audio focus was lost, but it's possible to duck (i.e.: play quietly)
                            mCurrentAudioFocusState = AUDIO_NO_FOCUS_CAN_DUCK;
                            break;
                        case AudioManager.AUDIOFOCUS_LOSS_TRANSIENT:
                            // Lost audio focus, but will gain it back (shortly), so note whether
                            // playback should resume
                            mCurrentAudioFocusState = AUDIO_NO_FOCUS_NO_DUCK;
                            if (isPlaying()) {
                                // set the flag to true, to resume playing when we gain focus
                                mPlayOnFocusGain = true;
                            }
                            break;
                        case AudioManager.AUDIOFOCUS_LOSS:
                            // Lost audio focus, probably "permanently"
                            mCurrentAudioFocusState = AUDIO_NO_FOCUS_NO_DUCK;
                            break;
                    }

                    // update the player state based on the change
                    configurePlayerState();

                }
            };

    private void tryToGetAudioFocus() {
        Log.d(TAG, "tryToGetAudioFocus:called");
        int result =
                mAudioManager.requestAudioFocus(
                        mOnAudioFocusChangeListener,
                        AudioManager.STREAM_MUSIC,
                        AudioManager.AUDIOFOCUS_GAIN);
        if (result == AudioManager.AUDIOFOCUS_REQUEST_GRANTED) {
            mCurrentAudioFocusState = AUDIO_FOCUSED;
        } else {
            mCurrentAudioFocusState = AUDIO_NO_FOCUS_NO_DUCK;
        }
    }

    private void giveUpAudioFocus() {
        Log.d(TAG, "giveUpAudioFocus:called");
        if (mAudioManager.abandonAudioFocus(mOnAudioFocusChangeListener)
                == AudioManager.AUDIOFOCUS_REQUEST_GRANTED) {
            mCurrentAudioFocusState = AUDIO_NO_FOCUS_NO_DUCK;
        }
    }

    private boolean configurePlayerState() {
        Log.d(TAG, "configurePlayerState:called");
        Log.i(TAG,"mCurrentAudioFocusState="+focusStates[mCurrentAudioFocusState]);
        Log.i(TAG, "configurePlayerState:state="+Playback.states[mState]);

        boolean isPlaying = false;

        if (mCurrentAudioFocusState == AUDIO_NO_FOCUS_NO_DUCK) {
            // We don't have audio focus and can't duck, so we have to pause
            if (pausePlayer()) {
                // tell playback manager about state change
                mCallback.onPlaybackStatusChanged(mState);
                mCallback.onError("cannot gain audio focus");
            }
        } else {
            registerAudioNoisyReceiver();

            if (mCurrentAudioFocusState == AUDIO_NO_FOCUS_CAN_DUCK) {
                // We're permitted to play, but only if we 'duck', ie: play softly
                mMediaPlayer.setVolume(VOLUME_DUCK, VOLUME_DUCK);
            } else {
                mMediaPlayer.setVolume(VOLUME_NORMAL, VOLUME_NORMAL);
            }

            // If we were playing when we lost focus, we need to resume playing.
            if (mPlayOnFocusGain) {
                if (playPlayer()) {
                    isPlaying = true;
                    // tell playback manager about state change
                    mCallback.onPlaybackStatusChanged(mState);
                }
                // set the flag to false to handle any unexpected behaviors
                mPlayOnFocusGain = false;
            }
        }

        return isPlaying;
    }

    private void registerAudioNoisyReceiver() {
        Log.d(TAG, "registerAudioNoisyReceiver:called");
        Log.i(TAG, "registerAudioNoisyReceiver:state="+Playback.states[mState]);
        if (!mAudioNoisyReceiverRegistered) {
            Log.i(TAG, "registerAudioNoisyReceiver:registered");
            mContext.registerReceiver(mAudioNoisyReceiver, mAudioNoisyIntentFilter);
            mAudioNoisyReceiverRegistered = true;
        }
    }

    private void unregisterAudioNoisyReceiver() {
        Log.d(TAG, "unregisterAudioNoisyReceiver:called");
        Log.i(TAG, "unregisterAudioNoisyReceiver:state="+Playback.states[mState]);
        if (mAudioNoisyReceiverRegistered) {
            Log.i(TAG, "unregisterAudioNoisyReceiver:unregistered");
            mContext.unregisterReceiver(mAudioNoisyReceiver);
            mAudioNoisyReceiverRegistered = false;
        }
    }

    private boolean stopPlayer() {
        Log.d(TAG, "stopPlayer():called");
        Log.i(TAG, "stopPlayer:state="+Playback.states[mState]);

        // already stopped?
        if (isStopped()) {
            return true;
        }

        // is playing or paused then only we can stop
        if (isPlaying() || isPaused()) {
            mMediaPlayer.stop();
            mState = Playback.CUSTOM_PLAYBACK_STATE_STOPPED;
            return true;
        }

        return false;
    }

    private void resetPlayer() {
        Log.d(TAG, "resetPlayer():called");
        Log.i(TAG, "resetPlayer:state="+Playback.states[mState]);
        mMediaPlayer.reset();

        mState = Playback.CUSTOM_PLAYBACK_STATE_IDLE;
    }

    private boolean setPlayerDataSource(String source) {
        Log.d(TAG, "setPlayerDataSource():called");
        Log.i(TAG, "setPlayerDataSource:state="+Playback.states[mState]);

        // if idle then only we can set source
        if (!isIdle()) {
            return false;
        }

        try {
            mMediaPlayer.setDataSource(source);
        } catch (IOException e) {
            Log.e(TAG, "setPlayerDataSource:exception", e);
            e.printStackTrace();
            return false;
        }

        mState = Playback.CUSTOM_PLAYBACK_STATE_STOPPED;
        return true;
    }

    private boolean preparePlayer() {
        Log.d(TAG, "preparePlayer():called");
        Log.i(TAG, "preparePlayer:state="+Playback.states[mState]);
        // if stopped then only we can prepare player
        if (!isStopped()) {
            return false;
        }

        mState = Playback.CUSTOM_PLAYBACK_STATE_PREPARING;
        mMediaPlayer.prepareAsync();

        return true;
    }

    private void seekPlayerTo(int position) {
        Log.d(TAG, "seekPlayerTo():called");
        Log.i(TAG, "seekPlayerTo:state="+Playback.states[mState]);
        // if playing or paused then only we can seek player
        if (isPlaying() || isPaused()) {
            mMediaPlayer.seekTo(position);
        } else {
            if (isStopped()) {
                preparePlayer();
            } else {
                //do nothing
            }
        }
    }

    private boolean pausePlayer() {
        Log.d(TAG, "pausePlayer():called");
        Log.i(TAG, "pausePlayer:state="+Playback.states[mState]);

        // already paused?
        if (isPaused()) {
            return true;
        }

        // if playing then only we can pause
        if (isPlaying()) {
            mResumePosition = mMediaPlayer.getCurrentPosition();
            mMediaPlayer.pause();
            mState = Playback.CUSTOM_PLAYBACK_STATE_PAUSED;
            return true;
        }

        return false;
    }

    private boolean playPlayer() {
        Log.d(TAG, "playPlayer():called");
        Log.i(TAG, "playPlayer:state="+Playback.states[mState]);

        // if stopped then we will prepare player from scratch
        if (isStopped()) {
            return preparePlayer();
        } else if (isPaused() || isPlaying()) { // only play if it is paused or playing
            // seek to if the player is resuming from being paused
            if (mResumePosition != 0) {
                mMediaPlayer.seekTo(mResumePosition);
            }
            mMediaPlayer.start();
            mState = Playback.CUSTOM_PLAYBACK_STATE_PLAYING;
            return true;
        }
        return false;
    }
}

