package com.sobot.chat.widget.dialog;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.graphics.Rect;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.sobot.chat.MarkConfig;
import com.sobot.chat.R;
import com.sobot.chat.ZCSobotApi;
import com.sobot.chat.utils.ScreenUtils;

/**
 * 左上角返回按钮弹窗
 */

public class SobotPermissionDialog extends Dialog implements View.OnClickListener {

    private Button sobot_btn_cancle_conversation, sobot_btn_temporary_leave;
    private LinearLayout coustom_pop_layout;
    private ClickViewListener viewListenern ;
    private final int screenHeight;
    private TextView titleView;
    private String title;
    private Context mContext;

    public SobotPermissionDialog(Activity context, ClickViewListener itemsOnClick) {
        super(context, R.style.sobot_noAnimDialogStyle);
        this.viewListenern=itemsOnClick;
        screenHeight = ScreenUtils.getScreenHeight(context);
        mContext = context;
        // 修改Dialog(Window)的弹出位置
        Window window = getWindow();
        if (window != null) {
            WindowManager.LayoutParams layoutParams = window.getAttributes();
            layoutParams.gravity = Gravity.CENTER;
            //横屏设置dialog全屏
            if (ZCSobotApi.getSwitchMarkStatus(MarkConfig.DISPLAY_INNOTCH) && ZCSobotApi.getSwitchMarkStatus(MarkConfig.LANDSCAPE_SCREEN)) {
                layoutParams.flags = WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE;
            }
            setParams(context, layoutParams);
            window.setAttributes(layoutParams);
        }
    }

    public SobotPermissionDialog(Activity activity, String title, ClickViewListener clickViewListener) {
        this(activity,  clickViewListener);
        this.title=title;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.sobot_permission_popup);
        initView();
    }

    public boolean onTouchEvent(MotionEvent event) {
        if (event.getAction() == MotionEvent.ACTION_DOWN) {
            if (!(event.getX() >= -10 && event.getY() >= -10)
                    || event.getY() <= (screenHeight - coustom_pop_layout.getHeight() - 20)) {//如果点击位置在当前View外部则销毁当前视图,其中10与20为微调距离
                dismiss();
            }
        }
        return true;
    }

    private void initView() {
        titleView=(TextView) findViewById(R.id.sobot_dialog_title);
        titleView.setText(R.string.sobot_no_permission_text);
        if (!TextUtils.isEmpty(title)) {
            titleView.setText(title);
        }
        sobot_btn_cancle_conversation = (Button) findViewById(R.id.sobot_btn_left);
        sobot_btn_cancle_conversation.setText(R.string.sobot_btn_cancle);
        sobot_btn_temporary_leave = (Button) findViewById(R.id.sobot_btn_right);
        sobot_btn_temporary_leave.setText(R.string.sobot_go_setting);
        coustom_pop_layout = (LinearLayout) findViewById(R.id.pop_layout);

        sobot_btn_cancle_conversation.setOnClickListener(this);
        sobot_btn_temporary_leave.setOnClickListener(this);
//        if(ThemeUtils.isChangedThemeColor(mContext)){
//            int themeColor = ThemeUtils.getThemeColor(mContext);
//            sobot_btn_cancle_conversation.setTextColor(themeColor);
//            sobot_btn_temporary_leave.setTextColor(themeColor);
//        }
    }

    private void setParams(Context context, WindowManager.LayoutParams lay) {
        DisplayMetrics dm = new DisplayMetrics();
        WindowManager wm = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        wm.getDefaultDisplay().getMetrics(dm);
        Rect rect = new Rect();
        View view = getWindow().getDecorView();
        view.getWindowVisibleDisplayFrame(rect);
        lay.width = dm.widthPixels;
    }

    @Override
    public void onClick(View v) {
        if (v==sobot_btn_cancle_conversation){
            if (viewListenern!=null){
                viewListenern.clickLeftView(getContext(),this);
            }
        }
        if (v==sobot_btn_temporary_leave){
            if (viewListenern!=null){
                viewListenern.clickRightView(getContext(),this);
            }
        }

    }

    public interface ClickViewListener{
        void clickRightView(Context context, SobotPermissionDialog dialog);
        void clickLeftView(Context context, SobotPermissionDialog dialog);
    }
}
