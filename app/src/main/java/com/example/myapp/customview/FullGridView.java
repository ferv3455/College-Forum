package com.example.myapp.customview;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.GridView;

public class FullGridView extends GridView {

    public FullGridView(Context context) {
        super(context);
    }

    public FullGridView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public FullGridView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int heightSpec;

        if (getLayoutParams().height == LayoutParams.WRAP_CONTENT) {

            // The two leftmost bits in the height measure spec have
            // a special meaning, hence we can't use them to describe height.
            heightSpec = MeasureSpec.makeMeasureSpec(
                    Integer.MAX_VALUE >>2, MeasureSpec.AT_MOST);
        }
        else {
            // Any other height should be respected as is.
            heightSpec = heightMeasureSpec;
        }

        super.onMeasure(widthMeasureSpec, heightSpec);
    }
}
