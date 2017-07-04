package app.sonu.com.musicplayer.base.list;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.List;

/**
 * Created by sonu on 30/6/17.
 */

public class BaseRecyclerViewAdapter<TypeFactory extends BaseTypeFactory> extends RecyclerView.Adapter<BaseViewHolder> {

    private List<BaseVisitable> elements;
    private TypeFactory typeFactory;

    public BaseRecyclerViewAdapter(List<BaseVisitable> elements, TypeFactory typeFactory) {
        this.elements = elements;
        this.typeFactory = typeFactory;
    }

    @Override
    public BaseViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        View contactView = LayoutInflater.from(context).inflate(viewType, parent, false);
        return typeFactory.createViewHolder(contactView, viewType);
    }

    @Override
    public void onBindViewHolder(BaseViewHolder holder, int position) {
        holder.bind(elements.get(position), elements.get(position).getOnClickListener());
    }

    @Override
    public int getItemCount() {
        return elements.size();
    }

    @Override
    public int getItemViewType(int position) {
        return elements.get(position).type(typeFactory);
    }
}
