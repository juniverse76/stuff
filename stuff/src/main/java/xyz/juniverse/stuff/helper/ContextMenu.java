package xyz.juniverse.stuff.helper;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.support.annotation.AttrRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;

import xyz.juniverse.stuff.R;


/**
 * Created by juniverse on 22/09/2017.
 *
 * 나중에 컴포넌트로 뺄 수 있겠다.
 */

public class ContextMenu extends FrameLayout implements View.OnClickListener {
    public ContextMenu(@NonNull Context context) {
        super(context);
        init(context, null);
    }

    public ContextMenu(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs);
    }

    public ContextMenu(@NonNull Context context, @Nullable AttributeSet attrs, @AttrRes int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context, attrs);
    }

    private int item_padding = 0;
    private float item_textSize = 0f;
    private int item_textColor = Color.DKGRAY;
    private void init(Context context, AttributeSet attrs) {
        item_padding = context.getResources().getDimensionPixelSize(R.dimen.cm_def_padding);
        item_textSize = context.getResources().getDimension(R.dimen.cm_def_textSize);
        item_textColor = context.getResources().getColor(R.color.cm_def_textColor);
        if (attrs != null) {
            TypedArray a = context.getTheme().obtainStyledAttributes(
                    attrs,
                    R.styleable.ContextMenu,
                    0, 0);

            try {
                item_padding = a.getDimensionPixelSize(R.styleable.ContextMenu_item_padding, item_padding);
                item_textSize = a.getFloat(R.styleable.ContextMenu_item_textSize, item_textSize);
                item_textColor = a.getColor(R.styleable.ContextMenu_item_textColor, item_textColor);
            } finally {
                a.recycle();
            }
        }
    }

    private ViewGroup rootView = null;
    private LinearLayout menu;
    public void setRootView(ViewGroup rootView) {
        this.rootView = rootView;
        rootView.addView(this, new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));

        setBackgroundColor(0x01000000);
        setOnClickListener(this);
        hideMenu();     // 일단 초기에는 숨기고 시작.

        menu = new LinearLayout(getContext());
        // todo styling
        menu.setOrientation(LinearLayout.VERTICAL);
        menu.setBackgroundColor(0xffffffff);
        addView(menu, new ViewGroup.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT));
    }

    public void setItems(int[] strResIds) {
        String[] labels = new String[strResIds.length];
        for (int i = 0; i < strResIds.length; i++)
            labels[i] = getResources().getString(strResIds[i]);
        setItems(labels);
    }

    public void setItems(String[] labels) {
        menu.removeAllViews();
        int index = 0;
//        int padding = getResources().getDimensionPixelSize(R.dimen.dp_8);
        TypedValue outValue = new TypedValue();
        getContext().getTheme().resolveAttribute(android.R.attr.selectableItemBackground, outValue, true);
        for (String label : labels) {
            TextView menuItem = new TextView(getContext());

            menuItem.setTextSize(item_textSize);
            menuItem.setTextColor(item_textColor);
            menuItem.setPadding(item_padding, item_padding, item_padding, item_padding);

            menuItem.setText(label);
            menuItem.setId(index);
            menuItem.setBackgroundResource(outValue.resourceId);

            menuItem.setOnClickListener(menuClickListener);
            menu.addView(menuItem, new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
            index++;
        }
    }

    public void showMenu(int[] strResIds, View guide, OnMenuSelectedListener listener) {
        setItems(strResIds);
        showMenu(guide, listener);
    }

    public void showMenu(final View guide, OnMenuSelectedListener listener) {
        this.listener = listener;
        setVisibility(View.VISIBLE);

        // todo guideView가 어디있느냐에 따라 메뉴가 보여지는 위치가 달라져야 하겠지만.... 일단 지금은 하단, 오른쪽 정렬로 가자.
        final int[] guideLocation = new int[2];
        guide.getLocationOnScreen(guideLocation);

//        FrameLayout.LayoutParams flp = (LayoutParams) menu.getLayoutParams();
//        flp.topMargin = guideLocation[1] + guide.getHeight();
//        flp.leftMargin = guideLocation[0] - menu.getWidth() + guide.getWidth();
//        menu.setLayoutParams(flp);

        // dependancy!!!
        Animation aa = new AlphaAnimation(0f, 1f);
        aa.setDuration(300);
        aa.setAnimationListener(new Animation.AnimationListener() {
            @Override public void onAnimationStart(Animation animation) {
                LayoutParams flp = (LayoutParams) menu.getLayoutParams();
                flp.topMargin = guideLocation[1] + guide.getHeight();
                flp.leftMargin = guideLocation[0] - menu.getWidth() + guide.getWidth();
                menu.setLayoutParams(flp);
            }
            @Override public void onAnimationEnd(Animation animation) { }
            @Override public void onAnimationRepeat(Animation animation) { }
        });
        menu.startAnimation(aa);
    }

    public void hideMenu() {
        setVisibility(View.GONE);
    }

    @Override
    public void onClick(View view) {
        hideMenu();
    }

    public interface OnMenuSelectedListener {
        void onMenuSelected(int index);
    }
    private OnMenuSelectedListener listener;

    private OnClickListener menuClickListener = new OnClickListener() {
        @Override
        public void onClick(View view) {
            if (listener != null)
                listener.onMenuSelected(view.getId());
            hideMenu();
        }
    };
}
