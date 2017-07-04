package app.sonu.com.musicplayer.ui.fileview.list;

import app.sonu.com.musicplayer.base.list.BaseTypeFactory;
import app.sonu.com.musicplayer.base.list.BaseViewHolder;
import app.sonu.com.musicplayer.ui.fileview.list.viewholders.FileViewHolder;
import app.sonu.com.musicplayer.ui.fileview.list.viewholders.FolderViewHolder;
import app.sonu.com.musicplayer.ui.fileview.list.visitables.FileVisitable;
import app.sonu.com.musicplayer.ui.fileview.list.visitables.FolderVisitable;
import android.view.View;

/**
 * Created by sonu on 30/6/17.
 */

public class FileViewListTypeFactory extends BaseTypeFactory {
    public int type(FileVisitable fileVisitable) {
        return FileViewHolder.LAYOUT;
    }

    public int type(FolderVisitable folderVisitable){
        return FolderViewHolder.LAYOUT;
    }

    @Override
    public BaseViewHolder createViewHolder(View parent, int type) {
        BaseViewHolder abstractViewHolder = null;

        switch (type) {
            case FileViewHolder.LAYOUT:
                abstractViewHolder = new FileViewHolder(parent);
                break;
            case FolderViewHolder.LAYOUT:
                abstractViewHolder = new FolderViewHolder(parent);
                break;
        }

        return abstractViewHolder;
    }
}
