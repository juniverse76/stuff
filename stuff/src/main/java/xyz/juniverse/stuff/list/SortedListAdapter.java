package xyz.juniverse.stuff.list;

import android.support.v7.util.SortedList;
import android.support.v7.widget.RecyclerView;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 * Created by juniverse on 16/03/2017.
 */

@SuppressWarnings("unused")
abstract public class SortedListAdapter<T> extends RecyclerView.Adapter
{

    private Comparator<T> mComparator;
    private SortedList<T> mSortedList;
    public SortedListAdapter(Class<T> typeParameterClass, Comparator<T> comparator)
    {
        this.mComparator = comparator;
        this.mSortedList = new SortedList<>(typeParameterClass, new SortedList.Callback<T>() {
            @Override
            public int compare(T a, T b) {
                return mComparator.compare(a, b);
            }

            @Override
            public void onInserted(int position, int count) {
                notifyItemRangeInserted(position, count);
            }

            @Override
            public void onRemoved(int position, int count) {
                notifyItemRangeRemoved(position, count);
            }

            @Override
            public void onMoved(int fromPosition, int toPosition) {
                notifyItemMoved(fromPosition, toPosition);
            }

            @Override
            public void onChanged(int position, int count) {
                notifyItemRangeChanged(position, count);
            }

            @Override
            public boolean areContentsTheSame(T item1, T item2) {
                return SortedListAdapter.this.areContentsTheSame(item1, item2);
            }

            @Override
            public boolean areItemsTheSame(T item1, T item2) {
                return SortedListAdapter.this.areItemsTheSame(item1, item2);
            }
        });
    }

    @Override
    public int getItemCount() {
        return mSortedList.size();
    }

    protected T getItem(int position)
    {
        return mSortedList.get(position);
    }

    public void add(T model) {
        mSortedList.add(model);
    }

    public void remove(T model) {
        mSortedList.remove(model);
    }

    public void add(List<T> models) {
        mSortedList.addAll(models);
    }

    public void remove(List<T> models) {
        mSortedList.beginBatchedUpdates();
        for (T model : models) {
            mSortedList.remove(model);
        }
        mSortedList.endBatchedUpdates();
    }

    public void replaceAll(List<T> models) {
        mSortedList.beginBatchedUpdates();
        for (int i = mSortedList.size() - 1; i >= 0; i--) {
            final T model = mSortedList.get(i);
            if (!models.contains(model)) {
                mSortedList.remove(model);
            }
        }
        mSortedList.addAll(models);
        mSortedList.endBatchedUpdates();
    }

    public void setItems(T[] items)
    {
        List<T> baseList = new ArrayList<>();
        Collections.addAll(baseList, items);
        Collections.sort(baseList, mComparator);
        setSortedItems(baseList);
    }

    public void setItems(List<T> items)
    {
        List<T> baseList = new ArrayList<>();
        baseList.addAll(items);
        Collections.sort(baseList, mComparator);
        setSortedItems(baseList);
    }

    private void setSortedItems(List<T> sortedItems)
    {
        mSortedList.beginBatchedUpdates();
        mSortedList.addAll(sortedItems);
        mSortedList.endBatchedUpdates();
    }

    abstract protected boolean areItemsTheSame(T item1, T item2);
    abstract protected boolean areContentsTheSame(T item1, T item2);
}
