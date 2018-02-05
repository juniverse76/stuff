package xyz.juniverse.stuff.list;

import android.support.v7.widget.RecyclerView;
import android.view.ViewGroup;

import java.util.List;

/**
 * Created by juniverse on 13/04/2017.
 *
 * 사용법
 * private class MainViewHolder extends SimpleHolder<CustomModel>
 * {
 *     CustomModel data;
 *     public MainViewHolder(View itemView) {
 *         super(itemView);
 *     }
 *
 *     @Override
 *     public void bind(CustomModel data) {
 *         this.data = data;
 *         ((TextView)itemView.findViewById(R.id.number)).setText("" + data.toString());
 *         itemView.setOnClickListener(listener);
 *     }
 *
 *     View.OnClickListener listener = new View.OnClickListener() {
 *         @Override
 *         public void onClick(View view) {
 *             // todo do something
 *         }
 *     };
 * }
 *
 * private SimpleHolder.Factory factory = new SimpleHolder.Factory<CustomModel>() {
 *     @Override
 *     public SimpleHolder onCreate(ViewGroup parent, int viewType) {
 *         View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.custom_layout, parent, false);
 *         return new MainViewHolder(view);
 *     }
 *
 *     @Override
 *     public int getViewType(CustomModel data) {
 *         return 0;
 *     }
 * };
 *
 * new SimpleAdapter(new ArrayList()<CustomModel>(), factory);
 */

public class SimpleAdapter extends RecyclerView.Adapter<SimpleHolder>
{
    SimpleHolder.Factory factory;
    List list;

    public SimpleAdapter(List list, SimpleHolder.Factory factory)
    {
        this.list = list;
        this.factory = factory;
    }

    @Override
    public SimpleHolder onCreateViewHolder(ViewGroup parent, int viewType)
    {
        return factory.onCreate(parent, viewType);
    }

    @Override
    public void onBindViewHolder(SimpleHolder holder, int position)
    {
        holder.bind(this, list.get(position));
    }

    @Override
    public int getItemCount() {
        if (list == null) return 0;
        return list.size();
    }

    @Override
    public int getItemViewType(int position) {
        return factory.getViewType(list.get(position));
    }

    public void setNewList(List list) {
        this.list = list;
        notifyDataSetChanged();
    }
}
