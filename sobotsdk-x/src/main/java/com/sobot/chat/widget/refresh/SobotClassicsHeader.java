package com.sobot.chat.widget.refresh;

import static android.view.ViewGroup.LayoutParams.WRAP_CONTENT;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.ColorInt;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentManager;

import com.sobot.widget.R;
import com.sobot.widget.refresh.layout.api.RefreshHeader;
import com.sobot.widget.refresh.layout.api.RefreshLayout;
import com.sobot.widget.refresh.layout.constant.RefreshState;
import com.sobot.widget.refresh.layout.constant.SpinnerStyle;
import com.sobot.widget.refresh.layout.drawable.ProgressDrawable;
import com.sobot.widget.refresh.layout.footer.ArrowDrawable;
import com.sobot.widget.refresh.layout.footer.ClassicsAbstract;
import com.sobot.widget.refresh.layout.util.SmartUtil;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

/**
 * 消息列表下拉头部
 */
public class SobotClassicsHeader extends ClassicsAbstract<SobotClassicsHeader> implements RefreshHeader {

    public static final int ID_TEXT_UPDATE = R.id.srl_classics_update;

    public static String REFRESH_HEADER_PULLING = null;//"下拉可以刷新";
    public static String REFRESH_HEADER_REFRESHING = null;//"正在刷新...";
    public static String REFRESH_HEADER_LOADING = null;//"正在加载...";
    public static String REFRESH_HEADER_RELEASE = null;//"释放立即刷新";
    public static String REFRESH_HEADER_FINISH = null;//"刷新完成";
    public static String REFRESH_HEADER_FAILED = null;//"刷新失败";
    public static String REFRESH_HEADER_UPDATE = null;//"上次更新 M-d HH:mm";
    public static String REFRESH_HEADER_SECONDARY = null;//"释放进入二楼";
//    public static String REFRESH_HEADER_UPDATE = "'Last update' M-d HH:mm";

    protected String KEY_LAST_UPDATE_TIME = "LAST_UPDATE_TIME";

    protected Date mLastTime;
    protected TextView mLastUpdateText;
    protected SharedPreferences mShared;
    protected DateFormat mLastUpdateFormat;
    protected boolean mEnableLastTime = true;

    protected String mTextPulling;//"下拉可以刷新";
    protected String mTextRefreshing;//"正在刷新...";
    protected String mTextLoading;//"正在加载...";
    protected String mTextRelease;//"释放立即刷新";
    protected String mTextFinish;//"刷新完成";
    protected String mTextFailed;//"刷新失败";
    protected String mTextUpdate;//"上次更新 M-d HH:mm";
    protected String mTextSecondary;//"释放进入二楼";


    //<editor-fold desc="RelativeLayout">
    public SobotClassicsHeader(Context context) {
        this(context, null);
    }

    public SobotClassicsHeader(Context context, AttributeSet attrs) {
        super(context, attrs, 0);

        View.inflate(context, R.layout.sobot_srl_classics_header, this);

        final View thisView = this;
        final View arrowView = mArrowView = thisView.findViewById(R.id.srl_classics_arrow);
        final View updateView = mLastUpdateText = thisView.findViewById(R.id.srl_classics_update);
        final View progressView = mProgressView = thisView.findViewById(R.id.srl_classics_progress);

        mTitleText = thisView.findViewById(R.id.srl_classics_title);

        TypedArray ta = context.obtainStyledAttributes(attrs, R.styleable.SobotClassicsHeader);

        LayoutParams lpArrow = (LayoutParams) arrowView.getLayoutParams();
        LayoutParams lpProgress = (LayoutParams) progressView.getLayoutParams();
        LinearLayout.LayoutParams lpUpdateText = new LinearLayout.LayoutParams(WRAP_CONTENT, WRAP_CONTENT);
        lpUpdateText.topMargin = ta.getDimensionPixelSize(R.styleable.SobotClassicsHeader_sobotSrlTextTimeMarginTop, SmartUtil.dp2px(0));
        lpProgress.rightMargin = ta.getDimensionPixelSize(R.styleable.SobotClassicsHeader_sobotSrlDrawableMarginRight, SmartUtil.dp2px(20));
        lpArrow.rightMargin = lpProgress.rightMargin;

        lpArrow.width = ta.getLayoutDimension(R.styleable.SobotClassicsHeader_sobotSrlDrawableArrowSize, lpArrow.width);
        lpArrow.height = ta.getLayoutDimension(R.styleable.SobotClassicsHeader_sobotSrlDrawableArrowSize, lpArrow.height);
        lpProgress.width = ta.getLayoutDimension(R.styleable.SobotClassicsHeader_sobotSrlDrawableProgressSize, lpProgress.width);
        lpProgress.height = ta.getLayoutDimension(R.styleable.SobotClassicsHeader_sobotSrlDrawableProgressSize, lpProgress.height);

        lpArrow.width = ta.getLayoutDimension(R.styleable.SobotClassicsHeader_sobotSrlDrawableSize, lpArrow.width);
        lpArrow.height = ta.getLayoutDimension(R.styleable.SobotClassicsHeader_sobotSrlDrawableSize, lpArrow.height);
        lpProgress.width = ta.getLayoutDimension(R.styleable.SobotClassicsHeader_sobotSrlDrawableSize, lpProgress.width);
        lpProgress.height = ta.getLayoutDimension(R.styleable.SobotClassicsHeader_sobotSrlDrawableSize, lpProgress.height);

        mFinishDuration = ta.getInt(R.styleable.SobotClassicsHeader_sobotSrlFinishDuration, mFinishDuration);
        mEnableLastTime = ta.getBoolean(R.styleable.SobotClassicsHeader_sobotSrlEnableLastTime, mEnableLastTime);
        mSpinnerStyle = SpinnerStyle.values[ta.getInt(R.styleable.SobotClassicsHeader_sobotSrlClassicsSpinnerStyle, mSpinnerStyle.ordinal)];

        if (ta.hasValue(R.styleable.SobotClassicsHeader_sobotSrlDrawableArrow)) {
            mArrowView.setImageDrawable(ta.getDrawable(R.styleable.SobotClassicsHeader_sobotSrlDrawableArrow));
        } else if (mArrowView.getDrawable() == null) {
            mArrowDrawable = new ArrowDrawable();
            if (ta.hasValue(R.styleable.SobotClassicsHeader_sobotSrlColorArrow)) {
                mArrowDrawable.setColor(ta.getColor(R.styleable.SobotClassicsHeader_sobotSrlColorArrow, 0));
            } else {
                mArrowDrawable.setColor(this.getResources().getColor(R.color.sobot_refresh_header_arrow_color));
            }
            mArrowView.setImageDrawable(mArrowDrawable);
        }

        if (ta.hasValue(R.styleable.SobotClassicsHeader_sobotSrlDrawableProgress)) {
            mProgressView.setImageDrawable(ta.getDrawable(R.styleable.SobotClassicsHeader_sobotSrlDrawableProgress));
        } else if (mProgressView.getDrawable() == null) {
            mProgressDrawable = new ProgressDrawable();
            if (ta.hasValue(R.styleable.SobotClassicsHeader_sobotSrlColorProgress)) {
                mProgressDrawable.setColor(ta.getColor(R.styleable.SobotClassicsHeader_sobotSrlColorProgress, 0));
            } else {
                mProgressDrawable.setColor(this.getResources().getColor(R.color.sobot_refresh_header_progress_color));
            }
            mProgressView.setImageDrawable(mProgressDrawable);
        }

        if (ta.hasValue(R.styleable.SobotClassicsHeader_sobotSrlTextSizeTitle)) {
            mTitleText.setTextSize(TypedValue.COMPLEX_UNIT_PX, ta.getDimensionPixelSize(R.styleable.SobotClassicsHeader_sobotSrlTextSizeTitle, SmartUtil.dp2px(16)));
//        } else {
//            mTitleText.setTextSize(16);
        }

        if (ta.hasValue(R.styleable.SobotClassicsHeader_sobotSrlTextSizeTime)) {
            mLastUpdateText.setTextSize(TypedValue.COMPLEX_UNIT_PX, ta.getDimensionPixelSize(R.styleable.SobotClassicsHeader_sobotSrlTextSizeTime, SmartUtil.dp2px(12)));
//        } else {
//            mLastUpdateText.setTextSize(12);
        }

        if (ta.hasValue(R.styleable.SobotClassicsHeader_sobotSrlPrimaryColor)) {
            super.setPrimaryColor(ta.getColor(R.styleable.SobotClassicsHeader_sobotSrlPrimaryColor, 0));
        }
        if (ta.hasValue(R.styleable.SobotClassicsHeader_sobotSrlAccentColor)) {
            setAccentColor(ta.getColor(R.styleable.SobotClassicsHeader_sobotSrlAccentColor, 0));
        }

        if (ta.hasValue(R.styleable.SobotClassicsHeader_sobotSrlTextPulling)) {
            mTextPulling = ta.getString(R.styleable.SobotClassicsHeader_sobotSrlTextPulling);
        } else if (REFRESH_HEADER_PULLING != null) {
            mTextPulling = REFRESH_HEADER_PULLING;
        } else {
            mTextPulling = context.getString(R.string.sobot_srl_header_pulling);
        }
        if (ta.hasValue(R.styleable.SobotClassicsHeader_sobotSrlTextLoading)) {
            mTextLoading = ta.getString(R.styleable.SobotClassicsHeader_sobotSrlTextLoading);
        } else if (REFRESH_HEADER_LOADING != null) {
            mTextLoading = REFRESH_HEADER_LOADING;
        } else {
            mTextLoading = context.getString(R.string.sobot_srl_header_loading);
        }
        if (ta.hasValue(R.styleable.SobotClassicsHeader_sobotSrlTextRelease)) {
            mTextRelease = ta.getString(R.styleable.SobotClassicsHeader_sobotSrlTextRelease);
        } else if (REFRESH_HEADER_RELEASE != null) {
            mTextRelease = REFRESH_HEADER_RELEASE;
        } else {
            mTextRelease = context.getString(R.string.sobot_srl_header_release);
        }
        if (ta.hasValue(R.styleable.SobotClassicsHeader_sobotSrlTextFinish)) {
            mTextFinish = ta.getString(R.styleable.SobotClassicsHeader_sobotSrlTextFinish);
        } else if (REFRESH_HEADER_FINISH != null) {
            mTextFinish = REFRESH_HEADER_FINISH;
        } else {
            mTextFinish = context.getString(R.string.sobot_srl_header_finish);
        }
        if (ta.hasValue(R.styleable.SobotClassicsHeader_sobotSrlTextFailed)) {
            mTextFailed = ta.getString(R.styleable.SobotClassicsHeader_sobotSrlTextFailed);
        } else if (REFRESH_HEADER_FAILED != null) {
            mTextFailed = REFRESH_HEADER_FAILED;
        } else {
            mTextFailed = context.getString(R.string.sobot_srl_header_failed);
        }
        if (ta.hasValue(R.styleable.SobotClassicsHeader_sobotSrlTextSecondary)) {
            mTextSecondary = ta.getString(R.styleable.SobotClassicsHeader_sobotSrlTextSecondary);
        } else if (REFRESH_HEADER_SECONDARY != null) {
            mTextSecondary = REFRESH_HEADER_SECONDARY;
        } else {
            mTextSecondary = context.getString(R.string.sobot_srl_header_secondary);
        }
        if (ta.hasValue(R.styleable.SobotClassicsHeader_sobotSrlTextRefreshing)) {
            mTextRefreshing = ta.getString(R.styleable.SobotClassicsHeader_sobotSrlTextRefreshing);
        } else if (REFRESH_HEADER_REFRESHING != null) {
            mTextRefreshing = REFRESH_HEADER_REFRESHING;
        } else {
            mTextRefreshing = context.getString(R.string.sobot_srl_header_refreshing);
        }
        if (ta.hasValue(R.styleable.SobotClassicsHeader_sobotSrlTextUpdate)) {
            mTextUpdate = ta.getString(R.styleable.SobotClassicsHeader_sobotSrlTextUpdate);
        } else if (REFRESH_HEADER_UPDATE != null) {
            mTextUpdate = REFRESH_HEADER_UPDATE;
        } else {
            mTextUpdate = context.getString(R.string.sobot_srl_header_update);
        }

        mLastUpdateFormat = new SimpleDateFormat("MM-dd HH:mm");

        ta.recycle();

        progressView.animate().setInterpolator(null);
        updateView.setVisibility(mEnableLastTime ? VISIBLE : GONE);
        mTitleText.setText(thisView.isInEditMode() ? mTextRefreshing : mTextPulling);

        if (thisView.isInEditMode()) {
            arrowView.setVisibility(GONE);
        } else {
            progressView.setVisibility(GONE);
        }

        try {//try 不能删除-否则会出现兼容性问题
            if (context instanceof FragmentActivity) {
                FragmentManager manager = ((FragmentActivity) context).getSupportFragmentManager();
                if (manager != null) {
                    @SuppressLint("RestrictedApi")
                    List<Fragment> fragments = manager.getFragments();
                    if (fragments.size() > 0) {
                        setLastUpdateTime(new Date());
                        return;
                    }
                }
            }
        } catch (Throwable e) {
            e.printStackTrace();
        }

        KEY_LAST_UPDATE_TIME += context.getClass().getName();
        mShared = context.getSharedPreferences("ClassicsHeader", Context.MODE_PRIVATE);
        setLastUpdateTime(new Date(mShared.getLong(KEY_LAST_UPDATE_TIME, System.currentTimeMillis())));

    }
    //</editor-fold>

    //<editor-fold desc="RefreshHeader">
    @Override
    public int onFinish(@NonNull RefreshLayout layout, boolean success) {
        if (success) {
            mTitleText.setText(mTextFinish);
            if (mLastTime != null) {
                setLastUpdateTime(new Date());
            }
        } else {
            mTitleText.setText(mTextFailed);
        }
        return super.onFinish(layout, success);//延迟500毫秒之后再弹回
    }

    @Override
    public void onStateChanged(@NonNull RefreshLayout refreshLayout, @NonNull RefreshState oldState, @NonNull RefreshState newState) {
        final View arrowView = mArrowView;
        final View updateView = mLastUpdateText;
        switch (newState) {
            case None:
                updateView.setVisibility(mEnableLastTime ? VISIBLE : GONE);
            case PullDownToRefresh:
                mTitleText.setText(mTextPulling);
                arrowView.setVisibility(VISIBLE);
//                arrowView.animate().rotation(0);
                break;
            case Refreshing:
            case RefreshReleased:
                mTitleText.setText(mTextRefreshing);
                arrowView.setVisibility(GONE);
                break;
            case ReleaseToRefresh:
                mTitleText.setText(mTextRelease);
//                arrowView.animate().rotation(180);
                break;
            case ReleaseToTwoLevel:
                mTitleText.setText(mTextSecondary);
//                arrowView.animate().rotation(0);
                break;
            case Loading:
                arrowView.setVisibility(GONE);
                updateView.setVisibility(mEnableLastTime ? INVISIBLE : GONE);
                mTitleText.setText(mTextLoading);
                break;
        }
    }
    //</editor-fold>

    //<editor-fold desc="API">
    public SobotClassicsHeader setLastUpdateTime(Date time) {
        final View thisView = this;
        mLastTime = time;
        mLastUpdateText.setText(String.format(mTextUpdate, mLastUpdateFormat.format(time)));
        if (mShared != null && !thisView.isInEditMode()) {
            mShared.edit().putLong(KEY_LAST_UPDATE_TIME, time.getTime()).apply();
        }
        return this;
    }

    public SobotClassicsHeader setTimeFormat(DateFormat format) {
        mLastUpdateFormat = format;
        if (mLastTime != null) {
            mLastUpdateText.setText(String.format(mTextUpdate, mLastUpdateFormat.format(mLastTime)));
        }
        return this;
    }

    public SobotClassicsHeader setLastUpdateText(CharSequence text) {
        mLastTime = null;
        mLastUpdateText.setText(text);
        return this;
    }

    public SobotClassicsHeader setAccentColor(@ColorInt int accentColor) {
        mLastUpdateText.setTextColor(accentColor & 0x00ffffff | 0xcc000000);
        return super.setAccentColor(accentColor);
    }

    public SobotClassicsHeader setEnableLastTime(boolean enable) {
        final View updateView = mLastUpdateText;
        mEnableLastTime = enable;
        updateView.setVisibility(enable ? VISIBLE : GONE);
        if (mRefreshKernel != null) {
            mRefreshKernel.requestRemeasureHeightFor(this);
        }
        return this;
    }

    public SobotClassicsHeader setTextSizeTime(float size) {
        mLastUpdateText.setTextSize(size);
        if (mRefreshKernel != null) {
            mRefreshKernel.requestRemeasureHeightFor(this);
        }
        return this;
    }

    public SobotClassicsHeader setTextSizeTime(int unit, float size) {
        mLastUpdateText.setTextSize(unit, size);
        if (mRefreshKernel != null) {
            mRefreshKernel.requestRemeasureHeightFor(this);
        }
        return this;
    }

    public SobotClassicsHeader setTextTimeMarginTop(float dp) {
        final View updateView = mLastUpdateText;
        MarginLayoutParams lp = (MarginLayoutParams) updateView.getLayoutParams();
        lp.topMargin = SmartUtil.dp2px(dp);
        updateView.setLayoutParams(lp);
        return this;
    }

    public SobotClassicsHeader setTextTimeMarginTopPx(int px) {
        MarginLayoutParams lp = (MarginLayoutParams) mLastUpdateText.getLayoutParams();
        lp.topMargin = px;
        mLastUpdateText.setLayoutParams(lp);
        return this;
    }

//    /**
//     * @deprecated 使用 findViewById(ID_TEXT_UPDATE) 代替
//     */
//    @Deprecated
//    public TextView getLastUpdateText() {
//        return mLastUpdateText;
//    }
    //</editor-fold>

}
