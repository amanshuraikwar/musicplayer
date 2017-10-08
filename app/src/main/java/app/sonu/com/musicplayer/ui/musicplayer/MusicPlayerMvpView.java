package app.sonu.com.musicplayer.ui.musicplayer;

import android.support.v4.media.session.MediaSessionCompat;
import android.support.v7.widget.RecyclerView;

import java.util.List;

import app.sonu.com.musicplayer.ui.base.BaseMvpView;


/**
 * Created by sonu on 4/7/17.
 */

public interface MusicPlayerMvpView extends BaseMvpView {
    void displayToast(String message);
    void showFavButtonEnabled();
    void showFavButtonDisabled();
    void metadataChanged();
    void setUmanoScrollView(RecyclerView recyclerView);
}
