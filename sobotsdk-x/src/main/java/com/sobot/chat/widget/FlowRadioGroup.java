package com.sobot.chat.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.RadioGroup;

import com.sobot.chat.utils.LogUtils;

/**
 * 自动换行的RadioGroup
 */
public class FlowRadioGroup extends RadioGroup {
    public FlowRadioGroup(Context context) {
        super(context);
    }

    private boolean isOutSize = false;
    private int maxWidth;
    private int oneWidth;
    private int padding;//按钮之间的间隙

    public FlowRadioGroup(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public void setMaxWidth(int maxWidth,int padding) {
        this.maxWidth = maxWidth;
        this.padding = padding;
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        if(maxWidth==0) {
            maxWidth = MeasureSpec.getSize(widthMeasureSpec);
        }
        int childCount = getChildCount();
        int x = 0;
        int y = 0;
        int row = 0;

        for (int index = 0; index < childCount; index++) {
            final View child = getChildAt(index);
            if (child.getVisibility() != View.GONE) {
                child.measure(MeasureSpec.UNSPECIFIED, MeasureSpec.UNSPECIFIED);
                // 此处增加onlayout中的换行判断，用于计算所需的高度
                int width = child.getMeasuredWidth();

                if (oneWidth < width) {
                    oneWidth = width;
                }
                int height = child.getMeasuredHeight();
                x += width;
//                LogUtils.d(index + "===width=====" + width + ",===x==" + x + "======oneWidth===" + oneWidth);
                y = row * height + height;
                if (x +padding> maxWidth) {
                    x = width;
                    row++;
                    y = row * height + height;
                }
            }
        }
//        LogUtils.d(maxWidth + "===row=====" + row + ",===x==" + x);
        if (row >= 1) {
            isOutSize = true;
            // 设置容器所需的宽度和高度
        }
        setMeasuredDimension(maxWidth, y);

    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        LogUtils.d("==onLayout==" + changed + "===l=====" + l + ",===r==" + r);
        super.onLayout(changed, l, t, r, b);

        final int childCount = getChildCount();
//            int maxWidth = r - l;
        int x = 0;
        int y = 0;
        int row = 0;
        for (int i = 0; i < childCount; i++) {
            final View child = this.getChildAt(i);
            if (child.getVisibility() != View.GONE) {
                int width = child.getMeasuredWidth();
                int height = child.getMeasuredHeight();
                x += width;
                y = row * height + height;
                if (x +padding> maxWidth) {
                    x = width;
                    row++;
                    y = row * height + height;
                }
                if (isOutSize) {
                    if (oneWidth >= maxWidth) {
                        oneWidth = maxWidth - 100;
                    }
                    child.layout((maxWidth - oneWidth) / 2, y - height, (maxWidth - oneWidth) / 2 + oneWidth, y);
                } else {
                    int padding = (maxWidth / 2 - oneWidth) / 2;
//                        child.layout(i*maxWidth/2+padding, y - height, i*maxWidth/2+padding+oneWidth, y);
                    if (i == 0) {
                        child.layout(maxWidth / 2 - 50 - oneWidth, y - height, maxWidth / 2 - 50, y);
                    } else {
                        child.layout(maxWidth / 2 + 50, y - height, maxWidth / 2 + 50 + oneWidth, y);
                    }
                }
            }
        }
    }
}
