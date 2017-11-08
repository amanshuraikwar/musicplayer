package app.sonu.com.musicplayer.ui.playbackcontrols;

import android.animation.Animator;
import android.animation.ArgbEvaluator;
import android.animation.ValueAnimator;
import android.content.res.ColorStateList;
import android.graphics.Bitmap;
import android.graphics.PorterDuff;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.support.v7.graphics.Palette;
import android.support.v7.widget.CardView;
import android.text.format.DateUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.load.resource.bitmap.BitmapTransitionOptions;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.request.target.Target;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

import app.sonu.com.musicplayer.R;
import app.sonu.com.musicplayer.di.module.FragmentModule;
import app.sonu.com.musicplayer.list.adapter.QueueRecyclerViewAdapter;
import app.sonu.com.musicplayer.mediaplayer.MusicService;
import app.sonu.com.musicplayer.ui.addsongstoplaylists.AddSongsToPlaylistsFragment;
import app.sonu.com.musicplayer.ui.base.BaseFragment;
import app.sonu.com.musicplayer.ui.musicplayer.MusicPlayerFragment;
import app.sonu.com.musicplayer.util.ColorUtil;
import app.sonu.com.musicplayer.util.LogHelper;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by sonu on 5/10/17.
 */

public class PlaybackControlsFragment extends BaseFragment<PlaybackControlsMvpPresenter>
        implements PlaybackControlsMvpView {

    private static final String TAG = LogHelper.getLogTag(PlaybackControlsFragment.class);

    private static final long PROGRESS_UPDATE_INTERNAL = 1000;
    private static final long PROGRESS_UPDATE_INITIAL_INTERVAL = 100;
    private final Handler mHandler = new Handler();
    private final Runnable mUpdateProgressTask = new Runnable() {
        @Override
        public void run() {
            mPresenter.updateProgress();
        }
    };
    private final ScheduledExecutorService mExecutorService =
            Executors.newSingleThreadScheduledExecutor();
    private ScheduledFuture<?> mScheduleFuture;

    private int lastDarkColor, lastLightColor;

    @BindView(R.id.playbackControlsParentLl)
    View playbackControlsParentLl;

    @BindView(R.id.playPauseIb)
    ImageButton playPauseIb;

    @BindView(R.id.songTitleMainTv)
    TextView songTitleMainTv;

    @BindView(R.id.songArtistMainTv)
    TextView songArtistMainTv;

    @BindView(R.id.songCurrentPositionSeekBar)
    SeekBar songCurrentPositionSeekBar;

    @BindView(R.id.elapsedTimeTv)
    TextView elapsedTimeTv;

    @BindView(R.id.totalTimeTv)
    TextView totalTimeTv;

    @BindView(R.id.shuffleIb)
    ImageButton shuffleIb;

    @BindView(R.id.repeatIb)
    ImageButton repeatIb;

    @BindView(R.id.addToPlaylistIv)
    ImageView addToPlaylistIv;

    @BindView(R.id.smallAlbumArtIv)
    ImageView smallAlbumArtIv;

    @BindView(R.id.smallAlbumArtCv)
    CardView smallAlbumArtCv;

    @BindView(R.id.songInfoLl)
    LinearLayout songInfoLl;

    @OnClick(R.id.playPauseIb)
    void onPlayPauseIbClick(){
        Log.d(TAG, "playPauseIb onClick:called");
        mPresenter.playPauseButtonOnClick();
    }

    @OnClick(R.id.skipNextIb)
    void onSkipNextIbClick(){
        Log.d(TAG, "skipNextIb onClick:called");
        mPresenter.skipNextButtonOnClick();
    }

    @OnClick(R.id.skipPreviousIb)
    void onSkipPreviousIbClick(){
        Log.d(TAG, "skipPreviousIb onClick:called");
        mPresenter.skipPreviousButtonOnClick();
    }

    @OnClick(R.id.shuffleIb)
    void onShuffleIbClick() {
        mPresenter.onShuffleButtonClick();
    }

    @OnClick(R.id.repeatIb)
    void onRepeatIbClick() {
        mPresenter.onRepeatButtonClick();
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        ((MusicPlayerFragment) getParentFragment())
                .getMusicPlayerHolderComponent()
                .fragmentComponentBuilder()
                .fragmentModule(new FragmentModule())
                .build()
                .inject(this);

        Log.d(TAG, "onCreate:is presenter null=" + (mPresenter == null));

        mPresenter.onCreate(getActivity());
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_playback_controls, container, false);
        ButterKnife.bind(this, view);

        songCurrentPositionSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                elapsedTimeTv.setText(DateUtils.formatElapsedTime(progress / 1000));
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
                stopSeekbarUpdate();
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                mPresenter.onSeekbarStopTrackingTouch(seekBar.getProgress());
            }
        });

        addToPlaylistIv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mPresenter.addToPlaylistIvClick();
            }
        });

        mPresenter.onCreateView();
        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        Log.d(TAG, "onResume:is presenter's view null="+(mPresenter.getMvpView()==null));
    }

    @Override
    public void displaySong(String songTitle,
                            String songSubtitle,
                            String songDuration,
                            String albumArtPath) {
        songTitleMainTv.setText(songTitle);
        songArtistMainTv.setText(songSubtitle);

        RequestOptions options = new RequestOptions();
        options.centerCrop();

        if (albumArtPath != null) {
            Glide.with(getActivity())
                    .asBitmap()
                    .load(albumArtPath)
                    .apply(options)
                    .listener(new RequestListener<Bitmap>() {
                        @Override
                        public boolean onLoadFailed(@Nullable GlideException e,
                                                    Object model,
                                                    Target<Bitmap> target,
                                                    boolean isFirstResource) {
                            return false;
                        }

                        @Override
                        public boolean onResourceReady(Bitmap resource,
                                                       Object model,
                                                       Target<Bitmap> target,
                                                       DataSource dataSource,
                                                       boolean isFirstResource) {

                            updateUiColor(resource);
                            return false;
                        }
                    })
                    .transition(BitmapTransitionOptions.withCrossFade())
                    .into(smallAlbumArtIv);
        } else {
            Glide.with(getActivity()).clear(smallAlbumArtIv);

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                smallAlbumArtIv.setImageDrawable(getActivity()
                        .getDrawable(R.drawable.default_song_art));
            } else {
                smallAlbumArtIv.setImageDrawable(
                        getActivity()
                                .getResources()
                                .getDrawable(R.drawable.default_song_art));
            }
            updateUiColor(null);
        }

        totalTimeTv.setText(songDuration);
    }

    private void updateUiColor(Bitmap resource) {

        if (resource == null) {
            setUiColorWithPalette(null);
        } else {
            ColorUtil.generatePalette(resource, new Palette.PaletteAsyncListener() {
                @Override
                public void onGenerated(Palette palette) {
                    setUiColorWithPalette(palette);
                }
            });
        }
    }

    private void setUiColorWithPalette(Palette palette) {

        final int darkColor = ColorUtil.getDarkColor(palette);
        final int lightColor = ColorUtil.getLightColor(palette);

        if (darkColor != lastDarkColor) {
            ValueAnimator colorAnimation = ValueAnimator
                    .ofObject(new ArgbEvaluator(), lastDarkColor, darkColor);
            colorAnimation.setDuration(200); // milliseconds
            colorAnimation.setRepeatCount(0);
            colorAnimation.setStartDelay(0);
            colorAnimation.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {

                @Override
                public void onAnimationUpdate(ValueAnimator animator) {
                    addToPlaylistIv.setColorFilter((Integer) animator.getAnimatedValue());
                    songTitleMainTv.setTextColor((Integer) animator.getAnimatedValue());
                    playPauseIb.setColorFilter((Integer) animator.getAnimatedValue());

                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {

                        songCurrentPositionSeekBar
                                .setProgressTintList(
                                        ColorStateList.valueOf(
                                                (Integer) animator.getAnimatedValue()));
                        songCurrentPositionSeekBar
                                .setThumbTintList(
                                        ColorStateList.valueOf(
                                                (Integer) animator.getAnimatedValue()));
                    }

                }
            });
            colorAnimation.start();

            mPresenter.onDarkColorChanged(lastDarkColor, darkColor);

            lastDarkColor = darkColor;
        }

        if (lastLightColor != lightColor) {
            mPresenter.onLightColorChanged(lastLightColor, lightColor);

            lastLightColor = lightColor;
        }
    }

    @Override
    public void showPlayIcon() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            playPauseIb.setImageDrawable(getResources()
                    .getDrawable(R.drawable.ic_play_circle_filled_grey_64dp, null));
        } else {
            playPauseIb.setImageDrawable(getResources()
                    .getDrawable(R.drawable.ic_play_circle_filled_grey_64dp));
        }
    }

    @Override
    public void showPauseIcon() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            playPauseIb.setImageDrawable(getResources()
                    .getDrawable(R.drawable.ic_pause_circle_filled_grey_64dp, null));
        } else {
            playPauseIb.setImageDrawable(getResources()
                    .getDrawable(R.drawable.ic_pause_circle_filled_grey_64dp));
        }
    }

    @Override
    public void setSeekBarPosition(int position) {
        songCurrentPositionSeekBar.setProgress(position);
    }

    @Override
    public void setElapsedTime(int position) {
        elapsedTimeTv.setText(DateUtils.formatElapsedTime(position/1000));
    }

    @Override
    public void updateDuration(long dur) {
        Log.d(TAG, "updateDuration:called");
        int duration = (int) dur;
        songCurrentPositionSeekBar.setMax(duration);
        totalTimeTv.setText(DateUtils.formatElapsedTime(duration/1000));

        Log.i(TAG, "updateDuration:total="+DateUtils.formatElapsedTime(duration/1000));
    }

    @Override
    public void scheduleSeekbarUpdate() {
        Log.d(TAG, "scheduleSeekbarUpdate:called");
        stopSeekbarUpdate();
        if (!mExecutorService.isShutdown()) {
            Log.d(TAG, "scheduleSeekbarUpdate:isnotshutdown");
            mScheduleFuture = mExecutorService.scheduleAtFixedRate(
                    new Runnable() {
                        @Override
                        public void run() {
                            mHandler.post(mUpdateProgressTask);
                        }
                    }, PROGRESS_UPDATE_INITIAL_INTERVAL,
                    PROGRESS_UPDATE_INTERNAL, TimeUnit.MILLISECONDS);
        }
    }

    @Override
    public void stopSeekbarUpdate() {
        if (mScheduleFuture != null) {
            mScheduleFuture.cancel(false);
        }
    }

    @Override
    public void setShuffleModeEnabled() {
        shuffleIb
                .setColorFilter(
                        ContextCompat
                                .getColor(getContext(),
                                        R.color.black), android.graphics.PorterDuff.Mode.SRC_IN);
    }

    @Override
    public void setShuffleModeDisabled() {
        shuffleIb
                .setColorFilter(
                        ContextCompat
                                .getColor(getContext(),
                                        R.color.grey), android.graphics.PorterDuff.Mode.SRC_IN);
    }

    @Override
    public void setRepeatModeNone() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            repeatIb
                    .setImageDrawable(
                            getActivity().getDrawable(R.drawable.ic_repeat_grey_24dp));
        } else {
            repeatIb.setImageDrawable(
                    getActivity()
                            .getResources()
                            .getDrawable(R.drawable.ic_repeat_grey_24dp));
        }

        repeatIb
                .setColorFilter(
                        ContextCompat
                                .getColor(getContext(),
                                        R.color.grey), android.graphics.PorterDuff.Mode.SRC_IN);
    }

    @Override
    public void setRepeatModeAll() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            repeatIb
                    .setImageDrawable(
                            getActivity().getDrawable(R.drawable.ic_repeat_grey_24dp));
        } else {
            repeatIb.setImageDrawable(
                    getActivity()
                            .getResources()
                            .getDrawable(R.drawable.ic_repeat_grey_24dp));
        }

        repeatIb
                .setColorFilter(
                        ContextCompat
                                .getColor(getContext(),
                                        R.color.black), android.graphics.PorterDuff.Mode.SRC_IN);
    }

    @Override
    public void setRepeatModeOne() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            repeatIb
                    .setImageDrawable(
                            getActivity().getDrawable(R.drawable.ic_repeat_one_grey_24dp));
        } else {
            repeatIb.setImageDrawable(
                    getActivity()
                            .getResources()
                            .getDrawable(R.drawable.ic_repeat_one_grey_24dp));
        }

        shuffleIb
                .setColorFilter(
                        ContextCompat
                                .getColor(getContext(),
                                        R.color.black), android.graphics.PorterDuff.Mode.SRC_IN);
    }

    @Override
    public void resetSeekbar() {
        songCurrentPositionSeekBar.setProgress(0);
    }

    @Override
    public void showAddToPlaylistsDialog(String songId) {
        AddSongsToPlaylistsFragment fragment = new AddSongsToPlaylistsFragment();
        Bundle b = new Bundle();
        b.putString(MusicService.KEY_SONG_ID, songId);
        fragment.setArguments(b);
        fragment.show(getChildFragmentManager(), "AddSongsToPlaylistsFragment");
    }

    @Override
    public void showMiniAlbumArt() {
        smallAlbumArtCv.setVisibility(View.VISIBLE);
    }

    @Override
    public void hideMiniAlbumArt() {
        smallAlbumArtCv.setVisibility(View.GONE);
    }

    @Override
    public void setMetadataGravityCenter() {
        songInfoLl.setGravity(Gravity.CENTER_HORIZONTAL);
    }

    @Override
    public void setMetadataGravityStart() {
        songInfoLl.setGravity(Gravity.START);
    }

    @Override
    public void displayToast(String message) {
        Toast.makeText(getContext(), message, Toast.LENGTH_SHORT).show();
    }
}
