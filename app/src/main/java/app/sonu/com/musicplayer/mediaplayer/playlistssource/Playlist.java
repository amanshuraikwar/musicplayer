package app.sonu.com.musicplayer.mediaplayer.playlistssource;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by sonu on 9/9/17.
 */

public class Playlist {
    private long mId;
    private String mTitle;
    private int mType;
    private long mTimeCreated;
    private List<String> mMusicMediaIds;
    private int mIconDrawableId;
    private int mColor;

    public Playlist(long id, String title, int type, int iconDrawableId, int color) {
        mId = id;
        mTitle = title;
        mType = type;
        mTimeCreated = System.currentTimeMillis();
        mMusicMediaIds = new ArrayList<>();
        mIconDrawableId = iconDrawableId;
        mColor = color;
    }

    public long getId() {
        return mId;
    }

    public void setId(long mId) {
        this.mId = mId;
    }

    public String getTitle() {
        return mTitle;
    }

    public void setTitle(String mTitle) {
        this.mTitle = mTitle;
    }

    public long getTimeCreated() {
        return mTimeCreated;
    }

    public void setTimeCreated(long mTimeCreated) {
        this.mTimeCreated = mTimeCreated;
    }

    public List<String> getMusicMediaIds() {
        return mMusicMediaIds;
    }

    public void setMusicMediaIds(List<String> mMusicMediaIds) {
        this.mMusicMediaIds = mMusicMediaIds;
    }

    public int getType() {
        return mType;
    }

    public void setType(int mType) {
        this.mType = mType;
    }

    public int getIconDrawableId() {
        return mIconDrawableId;
    }

    public void setIconDrawableId(int mIconDrawableId) {
        this.mIconDrawableId = mIconDrawableId;
    }

    public int getColor() {
        return mColor;
    }

    public void setColor(int mColor) {
        this.mColor = mColor;
    }

    public void addMusicMediaId(String id) {
        mMusicMediaIds.add(id);
    }

    public void removeMusicMediaId(String id) {
        if (mMusicMediaIds.contains(id)) {
            mMusicMediaIds.remove(mMusicMediaIds.lastIndexOf(id));
        }
    }
}
