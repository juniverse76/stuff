package xyz.juniverse.stuff.list;

import java.util.List;

/**
 * Created by juniverse on 13/09/2017.
 */

public abstract class StickyHeaderSimpleAdapter extends SimpleAdapter implements HeaderItemDecoration.StickyHeaderInterface {
    public interface Header {}

    public StickyHeaderSimpleAdapter(List list, SimpleHolder.Factory factory) {
        super(list, factory);
    }

    @Override
    public int getHeaderPositionForItem(int itemPosition) {
        int headerPosition = 0;
        do {
            if (this.isHeader(itemPosition)) {
                headerPosition = itemPosition;
                break;
            }
            itemPosition -= 1;
        } while (itemPosition >= 0);
        return headerPosition;
    }

    @Override
    public boolean isHeader(int itemPosition) {
        Object item = getItem(itemPosition);
        return (item != null && item instanceof Header);
    }
}
