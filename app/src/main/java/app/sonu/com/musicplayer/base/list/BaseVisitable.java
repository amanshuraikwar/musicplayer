package app.sonu.com.musicplayer.base.list;

/**
 * Created by sonu on 20/3/17.
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
