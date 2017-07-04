package app.sonu.com.musicplayer.ui.fileview.list.viewholders;

import app.sonu.com.musicplayer.R;
import app.sonu.com.musicplayer.base.list.BaseViewHolder;
import app.sonu.com.musicplayer.ui.fileview.list.onclicklisteners.FileOnClickListener;
import app.sonu.com.musicplayer.ui.fileview.list.visitables.FileVisitable;
import butterknife.BindView;

import android.support.annotation.LayoutRes;
import android.view.View;
import android.widget.TextView;

/**
 * Created by sonu on 30/6/17.
 */

public class FileViewHolder extends BaseViewHolder<FileVisitable, FileOnClickListener> {

    @LayoutRes
    public static final int LAYOUT = R.layout.item_file;

    @BindView(R.id.fileNameTv)
    TextView fileNameTv;

    public FileViewHolder(View itemView) {
        super(itemView);
    }

    @Override
    public void bind(FileVisitable visitable, FileOnClickListener onClickListener) {
        fileNameTv.setText(visitable.getCoreFile().getName());
    }
}
