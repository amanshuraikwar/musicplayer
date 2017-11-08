package app.sonu.com.musicplayer.list.base;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v4.app.FragmentActivity;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.Collections;
import java.util.List;

/**
 * Created by sonu on 30/6/17.
 * recyclerview adapter which supports different type of viewholders efficiently
 * using visitable design pattern
 */

public class BaseRecyclerViewAdapter<TypeFactory extends BaseTypeFactory>
        extends RecyclerView.Adapter<BaseViewHolder> {

    private List<BaseVisitable> visitableList;
    private TypeFactory typeFactory;
    private FragmentActivity mActivity;

    public BaseRecyclerViewAdapter(@NonNull FragmentActivity activity,
                                   @NonNull TypeFactory typeFactory,
                                   @NonNull List<BaseVisitable> visitableList) {
        this.typeFactory = typeFactory;
        this.mActivity = activity;
        setVisitableList(visitableList);
    }

    public BaseRecyclerViewAdapter(@NonNull FragmentActivity mActivity,
                                   @NonNull TypeFactory typeFactory) {
        this.typeFactory = typeFactory;
        this.mActivity = mActivity;
        setVisitableList(Collections.<BaseVisitable>emptyList());
    }

    @Override
    public BaseViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View contactView = LayoutInflater.from(mActivity).inflate(viewType, parent, false);
        return typeFactory.createViewHolder(contactView, viewType);
    }

    @Override
    public void onBindViewHolder(BaseViewHolder holder, int position) {
        holder.bind(visitableList.get(position), visitableList.get(position).getOnClickListener(),
                mActivity);
    }

    @Override
    public int getItemCount() {
        return visitableList.size();
    }

    @Override
    public int getItemViewType(int position) {
        return visitableList.get(position).type(typeFactory);
    }

    public Context getmActivity() {
        return mActivity;
    }

    public List<BaseVisitable> getVisitableList() {
        return visitableList;
    }

    public void setVisitableList(List<BaseVisitable> visitableList) {
        this.visitableList = visitableList;
    }
}
