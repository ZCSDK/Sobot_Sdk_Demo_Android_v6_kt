package com.sobot.chat.widget.kpswitch.view.emoticon;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.sobot.chat.R;
import com.sobot.chat.utils.ScreenUtils;
import com.sobot.chat.utils.ThemeUtils;
import com.sobot.chat.widget.kpswitch.widget.data.PageSetEntity;

import java.util.ArrayList;

public class EmoticonsIndicatorView extends LinearLayout {

    private static final int MARGIN_LEFT = 8;
    protected Context mContext;
    protected ArrayList<ImageView> mImageViews;
    protected Drawable mDrawableSelect;
    protected Drawable mDrawableNomal;
    protected LayoutParams mLeftLayoutParams;

    public EmoticonsIndicatorView(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.mContext = context;
        this.setOrientation(HORIZONTAL);

        if (mDrawableNomal == null) {
            mDrawableNomal = getResources().getDrawable(R.drawable.sobot_indicator_point_nomal);
        }
        if (mDrawableSelect == null) {
            mDrawableSelect = getResources().getDrawable(R.drawable.sobot_indicator_point_select);
            ThemeUtils.applyColorToDrawable(mDrawableSelect, ThemeUtils.getThemeColor(getContext()));
        }

        mLeftLayoutParams = new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
        mLeftLayoutParams.height = ScreenUtils.dip2px(context, 6);
        mLeftLayoutParams.width = ScreenUtils.dip2px(context, 6);
        mLeftLayoutParams.leftMargin = ScreenUtils.dip2px(context, MARGIN_LEFT);
    }

    public void playTo(int position, PageSetEntity pageSetEntity) {
        if (!checkPageSetEntity(pageSetEntity)) {
            return;
        }

        updateIndicatorCount(pageSetEntity.getPageCount());

        if (mImageViews != null && mImageViews.size() > 0) {
            for (ImageView iv : mImageViews) {
                iv.setImageDrawable(mDrawableNomal);
            }
            if (mImageViews.get(position) != null) {
                mImageViews.get(position).setImageDrawable(mDrawableSelect);
            }
        }
    }

    public void playBy(int startPosition, int nextPosition, PageSetEntity pageSetEntity) {
        if (!checkPageSetEntity(pageSetEntity)) {
            return;
        }

        updateIndicatorCount(pageSetEntity.getPageCount());

        if (startPosition < 0 || nextPosition < 0 || nextPosition == startPosition) {
            startPosition = nextPosition = 0;
        }

        if (startPosition < 0) {
            startPosition = nextPosition = 0;
        }

        final ImageView imageViewStrat = mImageViews.get(startPosition);
        final ImageView imageViewNext = mImageViews.get(nextPosition);

        imageViewStrat.setImageDrawable(mDrawableNomal);
        imageViewNext.setImageDrawable(mDrawableSelect);
    }

    protected boolean checkPageSetEntity(PageSetEntity pageSetEntity) {
        if (pageSetEntity != null && pageSetEntity.isShowIndicator()) {
            setVisibility(VISIBLE);
            return true;
        } else {
            setVisibility(GONE);
            return false;
        }
    }

    protected void updateIndicatorCount(int count) {
        if (mImageViews == null) {
            mImageViews = new ArrayList<>();
        }
        if (count > mImageViews.size()) {
            for (int i = mImageViews.size(); i < count; i++) {
                ImageView imageView = new ImageView(mContext);
                imageView.setImageDrawable(i == 0 ? mDrawableSelect : mDrawableNomal);
                this.addView(imageView, mLeftLayoutParams);
                mImageViews.add(imageView);
            }
        }

        //如果只有一页不显示indicator
        if (count == 1) {
            for (int i = 0; i < mImageViews.size(); i++) {
                mImageViews.get(i).setVisibility(GONE);
            }
        } else {
            for (int i = 0; i < mImageViews.size(); i++) {
                if (i >= count) {
                    mImageViews.get(i).setVisibility(GONE);
                } else {
                    mImageViews.get(i).setVisibility(VISIBLE);
                }
            }
        }
        if (count>1){
            setVisibility(VISIBLE);
        }else {
            setVisibility(GONE);
        }
    }
}