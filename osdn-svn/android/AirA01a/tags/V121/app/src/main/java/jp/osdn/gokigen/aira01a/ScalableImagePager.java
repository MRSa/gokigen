package jp.osdn.gokigen.aira01a;

import android.content.Context;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.view.View;

/**
 *   ScalableImagePager ... ImageViewerSampleから持ってくる
 *
 *
 */
public class ScalableImagePager extends ViewPager
{

    public ScalableImagePager(Context context)
    {
        super(context);
    }

    public ScalableImagePager(Context context, AttributeSet attrs)
    {
        super(context, attrs);
    }

    @Override
    protected boolean canScroll(View v, boolean checkV, int dx, int x, int y)
    {
        ScalableImageView imageView = (ScalableImageView)getCurrentView();
        return imageView.canHorizontalScroll();
    }

    protected View getCurrentView()
    {
        for (int position = 0; position < getChildCount(); position++)
        {
            View view = getChildAt(position);
            float viewportCenterX = getScrollX() + getWidth() / 2;
            float contentLeftX = view.getX();
            float contentRightX =  view.getX() + view.getWidth();
            if (contentLeftX < viewportCenterX && contentRightX > viewportCenterX)
            {
                return view;
            }
        }
        return getChildAt(0);
    }
}
