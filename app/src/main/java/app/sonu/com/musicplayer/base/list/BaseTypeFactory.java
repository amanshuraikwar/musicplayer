package app.sonu.com.musicplayer.base.list;

import android.view.View;

/**
 * Created by sonu on 20/3/17.
 */

/**
 * How to Use -
 * [1] inherit from this class and implement
 *     [a] type method returning a unique id for your different Visitables
 *     [b] createViewHolder returning appropriate ViewHolder according to the id above
 */

public abstract class BaseTypeFactory {
//    int type(CommentVisitable commentVisitable);

    public abstract BaseViewHolder createViewHolder(View parent, int type);
}
