package xyz.juniverse.stuff.helper;

import android.app.Activity;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

/**
 * Created by juniverse on 10/04/2017.
 *
 */

public class ViewTreeDisplay
{
    private ViewGroup contentView;
    private TextView textView;
    public ViewTreeDisplay(Activity activity)
    {
        contentView = (ViewGroup) activity.findViewById(android.R.id.content);

        textView = new TextView(activity.getBaseContext());
        textView.setTextColor(0xffffffff);
        contentView.addView(textView);

        Handler handler = new Handler()
        {
            @Override
            public void handleMessage(Message msg) {
                String viewNames = printViews(contentView, "");
                textView.setText(viewNames);
                sendEmptyMessageDelayed(1, 1000);
            }
        };
        handler.sendEmptyMessageDelayed(1, 1000);
    }

    private String printViews(View view, String indent)
    {
        if (view == textView)
            return "";

        String name = indent + view.getClass().getSimpleName() + " " + view.getTag() + "\n";
        if (view instanceof ViewGroup)
        {
            indent += "  ";
            int children = ((ViewGroup)view).getChildCount();
            for (int i = 0; i < children; i++)
                name += printViews(((ViewGroup)view).getChildAt(i), indent);
        }
        return name;
    }

    public void setTextColor(int color)
    {
        textView.setTextColor(color);
    }
}
