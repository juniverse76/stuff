package xyz.juniverse.stuff.list;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;

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
 */

abstract public class SimpleHolder<T> extends RecyclerView.ViewHolder
{
    public SimpleHolder(View itemView) {
        super(itemView);
    }

    /**
     * 데이터와 뷰를 바인딩 하는 함수.
     * @param data
     */
    abstract public void bind(RecyclerView.Adapter adapter, T data);

    public interface Factory<T> {
        /**
         * 실제 뷰를 생성하는 함수. LayoutInflator를 사용하면 되겠다. viewType에 맞늦 SimpleHolder를 생성하면 됨.
         * @param parent
         * @param viewType
         * @return
         */
        SimpleHolder onCreate(ViewGroup parent, int viewType);

        /**
         * data에 맞는 viewType을 반환.
         * @param data
         * @return
         */
        int getViewType(T data);
    }
}
