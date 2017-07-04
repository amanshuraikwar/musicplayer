package app.sonu.com.musicplayer.ui.fileview.list.visitables;

import java.io.File;

import app.sonu.com.musicplayer.base.list.BaseVisitable;
import app.sonu.com.musicplayer.ui.fileview.list.FileViewListTypeFactory;
import app.sonu.com.musicplayer.ui.fileview.list.onclicklisteners.FolderOnClickListener;

/**
 * Created by sonu on 30/6/17.
 */

public class FolderVisitable extends BaseVisitable<FolderOnClickListener, FileViewListTypeFactory> {

    private File file;

    public FolderVisitable(File file) {
        this.file = file;
    }

    public File getCoreFile() {
        return this.file;
    }

    @Override
    public int type(FileViewListTypeFactory typeFactory) {
        return typeFactory.type(this);
    }

    @Override
    public int getUniqueId() {
        return 2;
    }
}
