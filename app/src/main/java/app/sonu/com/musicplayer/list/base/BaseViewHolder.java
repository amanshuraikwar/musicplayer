package app.sonu.com.musicplayer.list.base;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import butterknife.ButterKnife;

/**
 * Created by sonu on 30/6/17.
 */

public abstract class BaseViewHolder
        <Visitable extends BaseVisitable, OnClickListener extends BaseListItemOnClickListener>
        extends RecyclerView.ViewHolder {

    public BaseViewHolder(View itemView) {
        super(itemView);
        ButterKnife.bind(this, itemView);
    }

    /**
     * implement this method and do the initialization of your list item, use the OnClickListener
     * @param visitable visitable instance
     * @param onClickListener onclick listener attached
     */
    public abstract void bind(Visitable visitable, OnClickListener onClickListener, Context context);
}
