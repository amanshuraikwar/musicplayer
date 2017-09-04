package app.sonu.com.musicplayer.base.list;

import android.view.View;

/**
 * Created by sonu on 20/3/17.
 * typefactory for recyclerview which creates viewholder instances according to R.layout._id
 */

public abstract class BaseTypeFactory {
    public abstract BaseViewHolder createViewHolder(View parent, int type);
}
