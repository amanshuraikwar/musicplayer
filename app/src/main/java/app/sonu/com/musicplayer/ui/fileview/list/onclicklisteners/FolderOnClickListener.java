package app.sonu.com.musicplayer.ui.fileview.list.onclicklisteners;

import app.sonu.com.musicplayer.base.list.BaseListItemOnClickListener;

/**
 * Created by sonu on 30/6/17.
 */

public interface FolderOnClickListener extends BaseListItemOnClickListener {
    void onFolderClick(String pathOfFolder);
}
