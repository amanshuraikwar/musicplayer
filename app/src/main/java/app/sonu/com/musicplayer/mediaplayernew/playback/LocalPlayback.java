package app.sonu.com.musicplayer.mediaplayernew.playback;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.support.annotation.NonNull;
import android.support.v4.media.MediaMetadataCompat;
import android.support.v4.media.session.MediaSessionCompat;
import android.support.v4.media.session.PlaybackStateCompat;
import android.util.Log;

import java.io.IOException;

import app.sonu.com.musicplayer.mediaplayernew.MusicService;
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

    private final MusicProvider mMusicProvider;
    private final Context mContext;

    private Callback mCallback;
    private MediaPlayer mMediaPlayer;
    private int mResumePosition;
    private int mState;

    private final AudioManager mAudioManager;
    private boolean mPlayOnFocusGain;
    private boolean mAudioNoisyReceiverRegistered;
    private int mCurrentAudioFocusState = AUDIO_NO_FOCUS_NO_DUCK;

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

    public LocalPlayback(Context context, MusicProvider musicProvider) {
        this.mContext = context.getApplicationContext();
        this.mMusicProvider = musicProvider;

        this.mAudioManager =
                (AudioManager) mContext.getSystemService(Context.AUDIO_SERVICE);

        initMediaPlayer();
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
    public boolean play(@NonNull MediaSessionCompat.QueueItem item) {
        Log.d(TAG, "play:called");
        Log.i(TAG, "play:item="+item);

        mPlayOnFocusGain = true;
        tryToGetAudioFocus();
        registerAudioNoisyReceiver();

        if (isPreparing()) {
            Log.w(TAG, "onPlay:player is preparing");
            return false;
        }

        stopPlayer();

        resetPlayer();

        Log.i(TAG, "play:id=" + MediaIdHelper.extractMusicIdFromMediaId(
                item.getDescription().getMediaId()));

        MediaMetadataCompat track =
                mMusicProvider.getMusic(
                        MediaIdHelper.extractMusicIdFromMediaId(
                                item.getDescription().getMediaId()));

        String source = track.getString(MusicProviderSource.CUSTOM_METADATA_KEY_TRACK_SOURCE);

        Log.d(TAG, "play:source=" + source);

        if (setPlayerDataSource(source)) {
//            configurePlayerState();
            return preparePlayer();
        } else {
            Log.w(TAG, "play:cannot set data source");
            return false;
        }
    }

    @Override
    public boolean play() {
        Log.d(TAG, "play(no):called");
        mPlayOnFocusGain = true;
        tryToGetAudioFocus();
        registerAudioNoisyReceiver();

        return configurePlayerState();
    }

    @Override
    public boolean pause() {
        Log.d(TAG, "pause():called");
        if (pausePlayer()) {
            unregisterAudioNoisyReceiver();
            mCallback.onPlaybackStatusChanged(PlaybackStateCompat.STATE_PAUSED);
            return true;
        }
        return false;
    }

    @Override
    public boolean stop() {
        Log.d(TAG, "stop():called");
        if (stopPlayer()) {
            giveUpAudioFocus();
            unregisterAudioNoisyReceiver();
            mCallback.onPlaybackStatusChanged(PlaybackStateCompat.STATE_STOPPED);
            return true;
        }
        return false;
    }

    @Override
    public void seekTo(int position) {
        Log.d(TAG, "seekTo():called");
        registerAudioNoisyReceiver();
        seekPlayerTo(position);
    }

    @Override
    public void setCallback(Callback callback) {
        this.mCallback = callback;
    }

    //media player implementations
    @Override
    public void onCompletion(MediaPlayer mp) {
        Log.d(TAG, "onCompletion():called");
        mState = Playback.CUSTOM_PLAYBACK_STATE_PAUSED;
        mMediaPlayer.seekTo(0);
        mResumePosition = 0;
        mCallback.onCompletion();
    }

    @Override
    public boolean onError(MediaPlayer mp, int what, int extra) {
        return false;
    }

    @Override
    public void onPrepared(MediaPlayer mp) {
        Log.d(TAG, "onPrepared:called");
        mState = Playback.CUSTOM_PLAYBACK_STATE_PAUSED;
        mResumePosition = 0;
        configurePlayerState();
    }

    @Override
    public void onSeekComplete(MediaPlayer mp) {
        Log.d(TAG, "onSeekComplete:called");
        Log.d(TAG, "onSeekComplete:positionAfterSeek="+getCurrentPosition());

        mResumePosition = mMediaPlayer.getCurrentPosition();
        if (isPlaying()) {
            mCallback.onPlaybackStatusChanged(PlaybackStateCompat.STATE_PLAYING);
        } else if (isPaused()) {
            mCallback.onPlaybackStatusChanged(PlaybackStateCompat.STATE_PAUSED);
        }

    }

    @Override
    public boolean onInfo(MediaPlayer mp, int what, int extra) {
        //todo figure out
        return false;
    }

    @Override
    public void onBufferingUpdate(MediaPlayer mp, int percent) {
        //todo figure out
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
                            //todo think
                            mPlayOnFocusGain = true;
                            break;
                        case AudioManager.AUDIOFOCUS_LOSS:
                            // Lost audio focus, probably "permanently"
                            mCurrentAudioFocusState = AUDIO_NO_FOCUS_NO_DUCK;
                            break;
                    }

                    // Update the player state based on the change
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
        Log.d(TAG, "configurePlayerState:called mCurrentAudioFocusState="+mCurrentAudioFocusState);

        boolean isPlaying = false;

        if (mCurrentAudioFocusState == AUDIO_NO_FOCUS_NO_DUCK) {
            // We don't have audio focus and can't duck, so we have to pause
            if (pausePlayer()) {
                mCallback.onPlaybackStatusChanged(PlaybackStateCompat.STATE_PAUSED);
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
                    mCallback.onPlaybackStatusChanged(PlaybackStateCompat.STATE_PLAYING);
                }
                mPlayOnFocusGain = false;
            }
        }

        return isPlaying;
    }

    private void registerAudioNoisyReceiver() {
        if (!mAudioNoisyReceiverRegistered) {
            mContext.registerReceiver(mAudioNoisyReceiver, mAudioNoisyIntentFilter);
            mAudioNoisyReceiverRegistered = true;
        }
    }

    private void unregisterAudioNoisyReceiver() {
        if (mAudioNoisyReceiverRegistered) {
            mContext.unregisterReceiver(mAudioNoisyReceiver);
            mAudioNoisyReceiverRegistered = false;
        }
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

        mResumePosition = 0;
        mState = Playback.CUSTOM_PLAYBACK_STATE_NONE;
    }

    private boolean stopPlayer() {
        Log.d(TAG, "stopPlayer():called");
        if (isStopped()) {
            return true;
        }

        if (isPlaying() || isPaused()) {
            mMediaPlayer.stop();
            mState = Playback.CUSTOM_PLAYBACK_STATE_STOPPED;
//            mCallback.onPlaybackStatusChanged(PlaybackStateCompat.STATE_STOPPED);
            return true;
        }

        return false;
    }

    private void resetPlayer() {
        Log.d(TAG, "resetPlayer():called");
        mMediaPlayer.reset();

        mState = Playback.CUSTOM_PLAYBACK_STATE_IDLE;
    }

    private boolean setPlayerDataSource(String source) {
        Log.d(TAG, "setPlayerDataSource():called");
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
        if (!isStopped()) {
            return false;
        }

        mMediaPlayer.prepareAsync();
        mState = Playback.CUSTOM_PLAYBACK_STATE_PREPARING;
        return true;
    }

    private void seekPlayerTo(int position) {
        Log.d(TAG, "seekPlayerTo():called");
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
        Log.i(TAG, "pausePlayer():state="+mState);
        if (isPlaying()) {
            mResumePosition = mMediaPlayer.getCurrentPosition();
            mMediaPlayer.pause();
            mState = Playback.CUSTOM_PLAYBACK_STATE_PAUSED;
//            mCallback.onPlaybackStatusChanged(PlaybackStateCompat.STATE_PAUSED);
            return true;
        } else {
            if (isPaused()) {
                return true;
            } else {
                return false;
            }
        }
    }

    private boolean playPlayer() {
        Log.d(TAG, "playPlayer():called");
        Log.d(TAG, "playPlayer():state="+mState);
        if (isStopped()) {
            preparePlayer();
            return true;
        } else {
            if (isPaused() || isPlaying()) {
                if (mResumePosition != 0) {
                    mMediaPlayer.seekTo(mResumePosition);
                }
                mMediaPlayer.start();
                mState = Playback.CUSTOM_PLAYBACK_STATE_PLAYING;
//                mCallback.onPlaybackStatusChanged(PlaybackStateCompat.STATE_PLAYING);
                return true;
            }
            return false;
        }
    }
}
