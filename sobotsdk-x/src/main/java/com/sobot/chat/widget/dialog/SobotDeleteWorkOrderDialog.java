package com.sobot.chat.widget.dialog;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.graphics.Rect;
import android.os.Bundle;
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
 * Created by jinxl on 2017/4/10.
 */

public class SobotDeleteWorkOrderDialog extends Dialog {

    private Button btn_pick_photo, btn_cancel;
    private LinearLayout coustom_pop_layout;
    private TextView tv_photo_hint;
    private View.OnClickListener itemsOnClick;
    private final int screenHeight;
    private String title;

    private int position;

    private Context mContext;

    public int getPosition() {
        return position;
    }

    public void setPosition(int position) {
        this.position = position;
    }

    public SobotDeleteWorkOrderDialog(Activity context, String title, View.OnClickListener itemsOnClick) {
        super(context, R.style.sobot_noAnimDialogStyle);
        this.mContext = context;
        this.itemsOnClick = itemsOnClick;
        this.title = title;
        screenHeight = ScreenUtils.getScreenHeight(context);


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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.sobot_delete_picture_popup);
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

    private void initView(){
        tv_photo_hint = (TextView) findViewById(R.id.tv_photo_hint);
        tv_photo_hint.setText(R.string.sobot_title);
        btn_pick_photo = (Button) findViewById(R.id.btn_pick_photo);
        btn_pick_photo.setText(R.string.sobot_delete);
        btn_cancel = (Button) findViewById(R.id.btn_cancel);
        btn_cancel.setText(R.string.sobot_btn_cancle);
        coustom_pop_layout = (LinearLayout) findViewById(R.id.pop_layout);

        btn_pick_photo.setOnClickListener(itemsOnClick);
        btn_cancel.setOnClickListener(itemsOnClick);
        tv_photo_hint.setText(title);
//        if(ThemeUtils.isChangedThemeColor(mContext)){
//            int themeColor = ThemeUtils.getThemeColor(mContext);
//            btn_pick_photo.setTextColor(themeColor);
//            btn_cancel.setTextColor(themeColor);
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
}
