package xyz.juniverse.stuff.image;

import android.content.Context;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.view.Surface;
import android.view.TextureView;
import android.view.WindowManager;

import xyz.juniverse.stuff.console;

/**
 * Created by juniverse on 19/06/2017.
 *
 * 전체 화면인지, 아니면 뷰 안에 보여주는 것인지..
 * 보여줄 이미지의 비율에 따라 사이즈를 보정해주는 텍스쳐 뷰.
 */

public class FlexibleTextureView extends TextureView
{
    private boolean fitFullScreen;
    private int ratioWidth;
    private int ratioHeight;

    public FlexibleTextureView(Context context) {
        super(context);
        initialize();
    }

    public FlexibleTextureView(Context context, AttributeSet attrs) {
        super(context, attrs);
        initialize();
    }

    public FlexibleTextureView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initialize();
    }

    private void initialize()
    {
        DisplayMetrics displayMetrics = getResources().getDisplayMetrics();
        ratioWidth = displayMetrics.widthPixels;
        ratioHeight = displayMetrics.heightPixels;
        fitFullScreen = true;
    }

    public void setFitToFull(boolean fitFullScreen)
    {
        this.fitFullScreen = fitFullScreen;
        requestLayout();
    }

    public void setAspectRatio(int width, int height)
    {
        this.ratioWidth = width;
        this.ratioHeight = height;
        requestLayout();
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int width = MeasureSpec.getSize(widthMeasureSpec);
        int height = MeasureSpec.getSize(heightMeasureSpec);

        float textureRatio = (float)Math.max(ratioWidth, ratioHeight) / Math.min(ratioWidth, ratioHeight);
        float screenRatio = (float)Math.max(width, height) / Math.min(width, height);

        boolean fitLongest = (fitFullScreen) ^ (textureRatio > screenRatio);

        int longerMeasureSpec = MeasureSpec.makeMeasureSpec((int) (screenRatio * Math.max(width, height) / textureRatio), MeasureSpec.EXACTLY);
        int shorterMeasureSpec = MeasureSpec.makeMeasureSpec((int) (screenRatio * Math.min(width, height) / textureRatio), MeasureSpec.EXACTLY);
        if (fitLongest) {
            if (height > width)
                widthMeasureSpec = shorterMeasureSpec;
            else
                heightMeasureSpec = shorterMeasureSpec;
        }
        else {
            if (height > width)
                heightMeasureSpec = longerMeasureSpec;
            else
                widthMeasureSpec = longerMeasureSpec;
        }

        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    }
}
