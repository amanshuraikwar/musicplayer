package app.sonu.com.musicplayer.ui.fileview.list.viewholders;

import app.sonu.com.musicplayer.R;
import app.sonu.com.musicplayer.base.list.BaseViewHolder;
import app.sonu.com.musicplayer.ui.fileview.list.onclicklisteners.FolderOnClickListener;
import app.sonu.com.musicplayer.ui.fileview.list.visitables.FolderVisitable;
import butterknife.BindView;

import android.support.annotation.LayoutRes;
import android.view.View;
import android.widget.TextView;

/**
 * Created by sonu on 30/6/17.
 */

public class FolderViewHolder extends BaseViewHolder<FolderVisitable, FolderOnClickListener> {

    @LayoutRes
    public static final int LAYOUT = R.layout.item_folder;

    @BindView(R.id.folderNameTv)
    TextView folderNameTv;

    @BindView(R.id.parentRl)
    View parentView;

    public FolderViewHolder(View itemView) {
        super(itemView);
    }

    @Override
    public void bind(final FolderVisitable visitable, final FolderOnClickListener onClickListener) {
        folderNameTv.setText(visitable.getCoreFile().getName());
        parentView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onClickListener.onFolderClick(visitable.getCoreFile().getAbsolutePath());
            }
        });
    }
}
