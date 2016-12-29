package com.my.shoppingcart.view;

import android.content.Context;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.widget.ScrollView;

/**
 *
 * Created by YJH on 2016/12/22 18:05.
 */
public class MyScrollView extends ScrollView {

    private MyScrollView scrollViewListener = null;

    public MyScrollView(Context context) {
        super(context);
    }

    public MyScrollView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    public MyScrollView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public void setScrollViewListener(MyScrollView scrollViewListener) {
        this.scrollViewListener = scrollViewListener;
    }

    @Override
    protected int computeScrollDeltaToGetChildRectOnScreen(Rect rect) {

        return 0;
    }

}  