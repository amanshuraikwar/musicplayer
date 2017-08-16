package app.sonu.com.musicplayer.ui.fileview.list.visitables;

import java.io.File;

import app.sonu.com.musicplayer.base.list.BaseVisitable;
import app.sonu.com.musicplayer.ui.fileview.list.FileViewListTypeFactory;
import app.sonu.com.musicplayer.ui.fileview.list.onclicklisteners.FileOnClickListener;

/**
 * Created by sonu on 30/6/17.
 */

public class FileVisitable extends BaseVisitable<FileOnClickListener, FileViewListTypeFactory> {

    private File file;
    private int playDuration;

    public FileVisitable(File file) {
        this.file = file;
    }

    public File getCoreFile() {
        return this.file;
    }

    @Override
    public int type(FileViewListTypeFactory typeFactory) {
        return typeFactory.type(this);
    }

    public int getPlayDuration() {
        return playDuration;
    }

    public void setPlayDuration(int playDuration) {
        this.playDuration = playDuration;
    }
}
