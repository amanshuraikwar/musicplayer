package app.sonu.com.musicplayer.base.list;

/**
 * Created by sonu on 20/3/17.
 * represents items in a recycler view which can be visited/shown in the recyclerview
 * generally this class is inherited to make a new visitable type item
 */

public abstract class BaseVisitable
        <OnClickListener extends BaseListItemOnClickListener, TypeFactory extends BaseTypeFactory> {

    private OnClickListener onClickListener;

    public abstract int type(TypeFactory typeFactory);

    public OnClickListener getOnClickListener(){
        return this.onClickListener;
    }

    public void setOnClickListener(OnClickListener onClickListener){
        this.onClickListener = onClickListener;
    }
}
