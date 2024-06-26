package com.sobot.chat.fragment;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.hardware.Camera;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.WindowManager;
import android.webkit.WebView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import com.sobot.chat.MarkConfig;
import com.sobot.chat.R;
import com.sobot.chat.ZCSobotApi;
import com.sobot.chat.activity.SobotCameraActivity;
import com.sobot.chat.activity.SobotSelectPicAndVideoActivity;
import com.sobot.chat.api.ZhiChiApi;
import com.sobot.chat.core.HttpUtils;
import com.sobot.chat.core.channel.SobotMsgManager;
import com.sobot.chat.listener.PermissionListener;
import com.sobot.chat.listener.PermissionListenerImpl;
import com.sobot.chat.notchlib.INotchScreen;
import com.sobot.chat.notchlib.NotchScreenManager;
import com.sobot.chat.utils.ChatUtils;
import com.sobot.chat.utils.CommonUtils;
import com.sobot.chat.utils.ToastUtil;
import com.sobot.chat.utils.ZhiChiConstant;
import com.sobot.chat.widget.dialog.SobotPermissionTipDialog;
import com.sobot.chat.widget.image.SobotRCImageView;
import com.sobot.pictureframe.SobotBitmapUtil;

import java.io.File;
import java.util.Arrays;

/**
 * @author Created by jinxl on 2018/2/1.
 */
public abstract class SobotBaseFragment extends Fragment {
    public static final int REQUEST_CODE_CAMERA = 108;

    public ZhiChiApi zhiChiApi;
    protected File cameraFile;

    private Activity activity;
    //权限回调
    public PermissionListener permissionListener;

    public SobotBaseFragment() {

    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (activity == null) {
            activity = (Activity) context;
        }
    }


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        zhiChiApi = SobotMsgManager.getInstance(getContext().getApplicationContext()).getZhiChiApi();
        if (ZCSobotApi.getSwitchMarkStatus(MarkConfig.DISPLAY_INNOTCH) && ZCSobotApi.getSwitchMarkStatus(MarkConfig.LANDSCAPE_SCREEN)) {
            // 支持显示到刘海区域
            NotchScreenManager.getInstance().setDisplayInNotch(getActivity());
            // 设置Activity全屏
            getActivity().getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        }
    }

    public void displayInNotch(final View view) {
        if (ZCSobotApi.getSwitchMarkStatus(MarkConfig.LANDSCAPE_SCREEN) && ZCSobotApi.getSwitchMarkStatus(MarkConfig.DISPLAY_INNOTCH) && view != null) {
            // 获取刘海屏信息
            NotchScreenManager.getInstance().getNotchInfo(getActivity(), new INotchScreen.NotchScreenCallback() {
                @Override
                public void onResult(INotchScreen.NotchScreenInfo notchScreenInfo) {
                    if (notchScreenInfo.hasNotch) {
                        for (Rect rect : notchScreenInfo.notchRects) {
                            if (view instanceof WebView && view.getParent() instanceof LinearLayout) {
                                LinearLayout.LayoutParams layoutParams = (LinearLayout.LayoutParams) view.getLayoutParams();
                                layoutParams.rightMargin = (rect.right > 110 ? 110 : rect.right) + 14;
                                layoutParams.leftMargin = (rect.right > 110 ? 110 : rect.right) + 14;
                                view.setLayoutParams(layoutParams);
                            } else if (view instanceof WebView && view.getParent() instanceof RelativeLayout) {
                                RelativeLayout.LayoutParams layoutParams = (RelativeLayout.LayoutParams) view.getLayoutParams();
                                layoutParams.rightMargin = (rect.right > 110 ? 110 : rect.right) + 14;
                                layoutParams.leftMargin = (rect.right > 110 ? 110 : rect.right) + 14;
                                view.setLayoutParams(layoutParams);
                            } else {
                                view.setPadding((rect.right > 110 ? 110 : rect.right) + view.getPaddingLeft(), view.getPaddingTop(), (rect.right > 110 ? 110 : rect.right) + view.getPaddingRight(), view.getPaddingBottom());
                            }
                        }
                    }
                }
            });

        }
    }


    @Override
    public void onDestroyView() {
        HttpUtils.getInstance().cancelTag(SobotBaseFragment.this);
        HttpUtils.getInstance().cancelTag(ZhiChiConstant.SOBOT_GLOBAL_REQUEST_CANCEL_TAG);
        super.onDestroyView();
    }


    public SobotBaseFragment getSobotBaseFragment() {
        return this;
    }

    //返回布局id
    /*protected abstract int getContentViewResId();

    protected void initBundleData(Bundle savedInstanceState) {
    }

    protected abstract void initView();

    protected abstract void initData();*/

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case ZhiChiConstant.SOBOT_PERMISSIONS_REQUEST_CODE:
                try {
                    //单独处理android 14 部分权限，如果允许是部分权限，跳转到回显界面
                    if (grantResults.length > 1 && permissions.length > 0) {
                        //是否有全部权限
                        boolean isAllGranted = true;
                        for (int i = 0; i < grantResults.length; i++) {
                            if (grantResults[i] != PackageManager.PERMISSION_GRANTED) {
                                isAllGranted = false;
                            }
                        }
                        if (!isAllGranted) {
                            for (int i = 0; i < grantResults.length; i++) {
                                if (permissions[i] != null && permissions[i].equals(Manifest.permission.READ_MEDIA_VISUAL_USER_SELECTED) && grantResults[i] == PackageManager.PERMISSION_GRANTED) {
                                    int selectType;
                                    if (Arrays.asList(permissions).contains(Manifest.permission.READ_MEDIA_IMAGES) && Arrays.asList(permissions).contains(Manifest.permission.READ_MEDIA_VIDEO)) {
                                        selectType = 3;//部分视频和图片
                                    } else if (Arrays.asList(permissions).contains(Manifest.permission.READ_MEDIA_VIDEO)) {
                                        selectType = 2;//部分视频
                                    } else {
                                        selectType = 1;//部分图片
                                    }
                                    openSelectPic(selectType);
                                    return;
                                }
                            }
                        }
                    }
                    for (int i = 0; i < grantResults.length; i++) {
                        //判断权限的结果，如果有被拒绝，就return
                        if (grantResults[i] != PackageManager.PERMISSION_GRANTED) {
                            String permissionTitle = getContext().getResources().getString(R.string.sobot_no_permission_text);
                            if (permissions[i] != null && permissions[i].equals(Manifest.permission.READ_EXTERNAL_STORAGE)) {
                                permissionTitle = getContext().getResources().getString(R.string.sobot_no_write_external_storage_permission);
                                if (shouldShowRequestPermissionRationale(Manifest.permission.READ_EXTERNAL_STORAGE) && !ZCSobotApi.getSwitchMarkStatus(MarkConfig.SHOW_PERMISSION_TIPS_POP)) {
                                    ToastUtil.showCustomLongToast(getSobotActivity(), CommonUtils.getAppName(getContext()) + getContext().getResources().getString(R.string.sobot_want_use_your) + getContext().getResources().getString(R.string.sobot_memory_card) + " , " + getContext().getResources().getString(R.string.sobot_memory_card_yongtu));
                                } else {
                                    //调用权限失败
                                    if (permissionListener != null) {
                                        permissionListener.onPermissionErrorListener(getSobotActivity(), permissionTitle);
                                    }
                                }
                            } else if (permissions[i] != null && permissions[i].equals(Manifest.permission.RECORD_AUDIO)) {
                                permissionTitle = getContext().getResources().getString(R.string.sobot_no_record_audio_permission);
                                if (shouldShowRequestPermissionRationale(Manifest.permission.RECORD_AUDIO) && !ZCSobotApi.getSwitchMarkStatus(MarkConfig.SHOW_PERMISSION_TIPS_POP)) {
                                    ToastUtil.showCustomLongToast(getSobotActivity(), CommonUtils.getAppName(getContext()) + getContext().getResources().getString(R.string.sobot_want_use_your) + getContext().getResources().getString(R.string.sobot_microphone) + " , " + getContext().getResources().getString(R.string.sobot_microphone_yongtu));
                                } else {
                                    //调用权限失败
                                    if (permissionListener != null) {
                                        permissionListener.onPermissionErrorListener(getSobotActivity(), permissionTitle);
                                    }
                                }
                            } else if (permissions[i] != null && permissions[i].equals(Manifest.permission.CAMERA)) {
                                permissionTitle = getContext().getResources().getString(R.string.sobot_no_camera_permission);
                                if (shouldShowRequestPermissionRationale(Manifest.permission.CAMERA) && !ZCSobotApi.getSwitchMarkStatus(MarkConfig.SHOW_PERMISSION_TIPS_POP)) {
                                    ToastUtil.showCustomLongToast(getSobotActivity(), CommonUtils.getAppName(getContext()) + getContext().getResources().getString(R.string.sobot_want_use_your) + getContext().getResources().getString(R.string.sobot_camera) + " , " + getContext().getResources().getString(R.string.sobot_camera_yongtu));
                                } else {
                                    //调用权限失败
                                    if (permissionListener != null) {
                                        permissionListener.onPermissionErrorListener(getSobotActivity(), permissionTitle);
                                    }
                                }
                            } else if (permissions[i] != null && permissions[i].equals(Manifest.permission.READ_MEDIA_IMAGES)) {
                                permissionTitle = getContext().getResources().getString(R.string.sobot_no_write_external_storage_permission);
                                if (shouldShowRequestPermissionRationale(Manifest.permission.READ_MEDIA_IMAGES) && !ZCSobotApi.getSwitchMarkStatus(MarkConfig.SHOW_PERMISSION_TIPS_POP)) {
                                    ToastUtil.showCustomLongToast(getSobotActivity(), CommonUtils.getAppName(getContext()) + getResources().getString(R.string.sobot_want_use_your) + getString(R.string.sobot_memory_card) + " , " + getString(R.string.sobot_memory_card_yongtu));
                                } else {
                                    //调用权限失败
                                    if (permissionListener != null) {
                                        permissionListener.onPermissionErrorListener(getSobotActivity(), permissionTitle);
                                    }
                                }
                            } else if (permissions[i] != null && permissions[i].equals(Manifest.permission.READ_MEDIA_VIDEO)) {
                                permissionTitle = getContext().getResources().getString(R.string.sobot_no_write_external_storage_permission);
                                if (shouldShowRequestPermissionRationale(Manifest.permission.READ_MEDIA_VIDEO) && !ZCSobotApi.getSwitchMarkStatus(MarkConfig.SHOW_PERMISSION_TIPS_POP)) {
                                    ToastUtil.showCustomLongToast(getSobotActivity(), CommonUtils.getAppName(getContext()) + getResources().getString(R.string.sobot_want_use_your) + getString(R.string.sobot_memory_card) + " , " + getString(R.string.sobot_memory_card_yongtu));
                                } else {
                                    //调用权限失败
                                    if (permissionListener != null) {
                                        permissionListener.onPermissionErrorListener(getSobotActivity(), permissionTitle);
                                    }
                                }
                            } else if (permissions[i] != null && permissions[i].equals(Manifest.permission.READ_MEDIA_AUDIO)) {
                                permissionTitle = getContext().getResources().getString(R.string.sobot_no_write_external_storage_permission);
                                if (shouldShowRequestPermissionRationale(Manifest.permission.READ_MEDIA_AUDIO) && !ZCSobotApi.getSwitchMarkStatus(MarkConfig.SHOW_PERMISSION_TIPS_POP)) {
                                    ToastUtil.showCustomLongToast(getSobotActivity(), CommonUtils.getAppName(getContext()) + getResources().getString(R.string.sobot_want_use_your) + getString(R.string.sobot_memory_card) + " , " + getString(R.string.sobot_memory_card_yongtu));
                                } else {
                                    //调用权限失败
                                    if (permissionListener != null) {
                                        permissionListener.onPermissionErrorListener(getSobotActivity(), permissionTitle);
                                    }
                                }
                            }
                            return;
                        }
                    }
                    if (permissionListener != null) {
                        permissionListener.onPermissionSuccessListener();
                    }
                } catch (Exception e) {
//                    e.printStackTrace();
                }
                break;
        }
    }


    /**
     * @param resourceId
     * @param textId
     */
    protected void showRightMenu(View tmpMenu, int resourceId, String textId) {
        if (tmpMenu == null || !(tmpMenu instanceof TextView)) {
            return;
        }
        TextView rightMenu = (TextView) tmpMenu;
        if (!TextUtils.isEmpty(textId)) {
            rightMenu.setText(textId);
        } else {
            rightMenu.setText("");
        }

        if (resourceId != 0) {
            Drawable img = getResources().getDrawable(resourceId);
            img.setBounds(0, 0, img.getMinimumWidth(), img.getMinimumHeight());
            rightMenu.setCompoundDrawables(null, null, img, null);
        } else {
            rightMenu.setCompoundDrawables(null, null, null, null);
        }

        rightMenu.setVisibility(View.VISIBLE);
    }


    /**
     * 显示头像
     *
     * @param avatarIV
     * @param avatarUrl
     * @param isShow
     */
    protected void showAvatar(SobotRCImageView avatarIV, String avatarUrl, boolean isShow) {
        if (!TextUtils.isEmpty(avatarUrl)) {
            SobotBitmapUtil.display(getContext(), avatarUrl, avatarIV);
        }
        if (isShow) {
            avatarIV.setVisibility(View.VISIBLE);
        } else {
            avatarIV.setVisibility(View.GONE);
        }
    }

    /**
     * @param resourceId
     * @param textId
     */
    protected void showLeftMenu(View tmpMenu, int resourceId, String textId) {
        if (tmpMenu == null || !(tmpMenu instanceof TextView)) {
            return;
        }
        TextView leftMenu = (TextView) tmpMenu;
        if (!TextUtils.isEmpty(textId)) {
            leftMenu.setText(textId);
        } else {
            leftMenu.setText("");
        }

        if (resourceId != 0) {
            Drawable img = getResources().getDrawable(resourceId);
            img.setBounds(0, 0, img.getMinimumWidth(), img.getMinimumHeight());
            leftMenu.setCompoundDrawables(img, null, null, null);
        } else {
            leftMenu.setCompoundDrawables(null, null, null, null);
        }
    }

    /**
     * 检查存储权限
     *
     * @param checkType 0：图片权限 1：视频权限，3，所有细分的权限， android 13 使用
     * @return true, 已经获取权限;false,没有权限,尝试获取
     */
    protected boolean checkStoragePermission(int checkType) {
        //如果是升级Android13之前就已经具有读写SDK的权限，那么升级到13之后，自己具有上述三个权限。
        //如果是升级Android13之后新装的应用，并且targetSDK小于33，则申请READ_EXTERNAL_STORAGE权限时，会自动转化为对上述三个权限的申请，权限申请框只一个
        //如果是升级Android13之后新装的应用，并且targetSDK大于等于33，则申请READ_EXTERNAL_STORAGE权限时会自动拒绝（同理WRITE_EXTERNAL_STORAGE也是一样）。
        //如果是android14 没有图片或者视频权限，还需要判断是否有部分权限，如果有部分权限，跳转允许图片视频界面，选择可以访问的上传，或者打开设置申请永久权限
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (checkType == 0) {
                //检测是否有图片权限
                if (ContextCompat.checkSelfPermission(getSobotActivity(), Manifest.permission.READ_MEDIA_IMAGES)
                        != PackageManager.PERMISSION_GRANTED) {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.UPSIDE_DOWN_CAKE) {
                        //android 14
                        if (ContextCompat.checkSelfPermission(getSobotActivity(), Manifest.permission.READ_MEDIA_VISUAL_USER_SELECTED)
                                == PackageManager.PERMISSION_GRANTED && ContextCompat.checkSelfPermission(getSobotActivity(), Manifest.permission.READ_MEDIA_VIDEO)
                                != PackageManager.PERMISSION_GRANTED) {
                            //有部分图片权限，同时（没有点击过视频，或者申请视频时，没有点击全部允许）直接打开允许访问图片视频列表界面
                            openSelectPic(1);//照片
                            return false;
                        } else {
                            this.requestPermissions(new String[]{Manifest.permission.READ_MEDIA_IMAGES, Manifest.permission.READ_MEDIA_VISUAL_USER_SELECTED}, ZhiChiConstant.SOBOT_PERMISSIONS_REQUEST_CODE);
                            return false;
                        }
                    } else {
                        //申请图片权限
                        this.requestPermissions(new String[]{Manifest.permission.READ_MEDIA_IMAGES}, ZhiChiConstant.SOBOT_PERMISSIONS_REQUEST_CODE);
                        return false;
                    }
                }
            } else if (checkType == 1) {
                if (ContextCompat.checkSelfPermission(getSobotActivity(), Manifest.permission.READ_MEDIA_VIDEO)
                        != PackageManager.PERMISSION_GRANTED) {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.UPSIDE_DOWN_CAKE) {
                        //android 14
                        if (ContextCompat.checkSelfPermission(getSobotActivity(), Manifest.permission.READ_MEDIA_VISUAL_USER_SELECTED)
                                == PackageManager.PERMISSION_GRANTED && ContextCompat.checkSelfPermission(getSobotActivity(), Manifest.permission.READ_MEDIA_IMAGES)
                                != PackageManager.PERMISSION_GRANTED) {
                            //有部分视频权限，同时（没有点击过图片，或者申请图片时，没有点击全部允许）直接打开允许访问图片视频列表界面
                            openSelectPic(2);//视频
                            return false;
                        } else {
                            this.requestPermissions(new String[]{Manifest.permission.READ_MEDIA_VIDEO, Manifest.permission.READ_MEDIA_VISUAL_USER_SELECTED}, ZhiChiConstant.SOBOT_PERMISSIONS_REQUEST_CODE);
                            return false;
                        }
                    } else {
                        //申请视频权限
                        this.requestPermissions(new String[]{Manifest.permission.READ_MEDIA_VIDEO}, ZhiChiConstant.SOBOT_PERMISSIONS_REQUEST_CODE);
                        return false;
                    }
                }
                return true;
            } else {
                if (ContextCompat.checkSelfPermission(getSobotActivity(), Manifest.permission.READ_MEDIA_IMAGES)
                        != PackageManager.PERMISSION_GRANTED || ContextCompat.checkSelfPermission(getSobotActivity(), Manifest.permission.READ_MEDIA_VIDEO)
                        != PackageManager.PERMISSION_GRANTED || ContextCompat.checkSelfPermission(getSobotActivity(), Manifest.permission.READ_MEDIA_AUDIO)
                        != PackageManager.PERMISSION_GRANTED) {
                    if (ContextCompat.checkSelfPermission(getSobotActivity(), Manifest.permission.READ_MEDIA_IMAGES) == PackageManager.PERMISSION_GRANTED && ContextCompat.checkSelfPermission(getSobotActivity(), Manifest.permission.READ_MEDIA_AUDIO)
                            != PackageManager.PERMISSION_GRANTED) {
                        //有图片和视频但是 没有音频权限
                        this.requestPermissions(new String[]{Manifest.permission.READ_MEDIA_AUDIO}, ZhiChiConstant.SOBOT_PERMISSIONS_REQUEST_CODE);
                        return false;
                    } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.UPSIDE_DOWN_CAKE) {
                        //android 14
                        if (ContextCompat.checkSelfPermission(getSobotActivity(), Manifest.permission.READ_MEDIA_VISUAL_USER_SELECTED)
                                == PackageManager.PERMISSION_GRANTED) {
                            //有部分视频权限，直接打开允许访问图片视频列表界面
                            openSelectPic(3);//视频
                            return false;
                        } else {
                            this.requestPermissions(new String[]{Manifest.permission.READ_MEDIA_IMAGES, Manifest.permission.READ_MEDIA_VIDEO, Manifest.permission.READ_MEDIA_AUDIO, Manifest.permission.READ_MEDIA_VISUAL_USER_SELECTED}, ZhiChiConstant.SOBOT_PERMISSIONS_REQUEST_CODE);
                            return false;
                        }
                    } else {
                        //申请图片、视频、语音三个权限
                        this.requestPermissions(new String[]{Manifest.permission.READ_MEDIA_IMAGES, Manifest.permission.READ_MEDIA_VIDEO, Manifest.permission.READ_MEDIA_AUDIO,}, ZhiChiConstant.SOBOT_PERMISSIONS_REQUEST_CODE);
                        return false;
                    }
                }
            }
        } else if (Build.VERSION.SDK_INT >= 23 && CommonUtils.getTargetSdkVersion(getSobotActivity().getApplicationContext()) >= 23) {
            if (ContextCompat.checkSelfPermission(getSobotActivity(), Manifest.permission.READ_EXTERNAL_STORAGE)
                    != PackageManager.PERMISSION_GRANTED) {
                //申请READ_EXTERNAL_STORAGE权限
                this.requestPermissions(new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, ZhiChiConstant.SOBOT_PERMISSIONS_REQUEST_CODE);
                return false;
            }
        }
        return true;
    }

    /**
     * android 14 部分权限情况下，回显照片或者视频
     *
     * @param selectType 1:部分图片 2:部分视频 3:部分视频和图片
     */
    private void openSelectPic(int selectType) {
        Intent intent = new Intent(getSobotActivity(), SobotSelectPicAndVideoActivity.class);
        intent.putExtra("selectType", selectType);
        startActivityForResult(intent, ZhiChiConstant.REQUEST_CODE_picture);
    }

    /**
     * 判断是否有存储卡权限,android 14 部分权限有的话也返回true
     *
     * @param checkPermissionType 0：图片权限 1：视频权限，2：音频权限，3，所有细分的权限， android 13 使用
     * @return true, 已经获取权限;false,没有权限
     */
    protected boolean isHasStoragePermission(int checkPermissionType) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (checkPermissionType == 0) {
                if (ContextCompat.checkSelfPermission(getSobotActivity(), Manifest.permission.READ_MEDIA_IMAGES)
                        != PackageManager.PERMISSION_GRANTED) {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.UPSIDE_DOWN_CAKE) {
                        //android 14 有部分权限就不弹权限提示框了
                        if (ContextCompat.checkSelfPermission(getSobotActivity(), Manifest.permission.READ_MEDIA_VISUAL_USER_SELECTED)
                                == PackageManager.PERMISSION_GRANTED) {
                            return true;
                        }
                    }
                    return false;
                }
            } else if (checkPermissionType == 1) {
                if (ContextCompat.checkSelfPermission(getSobotActivity(), Manifest.permission.READ_MEDIA_VIDEO)
                        != PackageManager.PERMISSION_GRANTED) {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.UPSIDE_DOWN_CAKE) {
                        //android 14 有部分权限就不弹权限提示框了
                        if (ContextCompat.checkSelfPermission(getSobotActivity(), Manifest.permission.READ_MEDIA_VISUAL_USER_SELECTED)
                                == PackageManager.PERMISSION_GRANTED) {
                            return true;
                        }
                    }
                    return false;
                }
            } else {
                if (ContextCompat.checkSelfPermission(getSobotActivity(), Manifest.permission.READ_MEDIA_IMAGES)
                        != PackageManager.PERMISSION_GRANTED || ContextCompat.checkSelfPermission(getSobotActivity(), Manifest.permission.READ_MEDIA_VIDEO)
                        != PackageManager.PERMISSION_GRANTED || ContextCompat.checkSelfPermission(getSobotActivity(), Manifest.permission.READ_MEDIA_AUDIO)
                        != PackageManager.PERMISSION_GRANTED) {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.UPSIDE_DOWN_CAKE) {
                        //android 14 有部分权限就不弹权限提示框了
                        if (ContextCompat.checkSelfPermission(getSobotActivity(), Manifest.permission.READ_MEDIA_VISUAL_USER_SELECTED)
                                == PackageManager.PERMISSION_GRANTED) {
                            return true;
                        }
                    }
                    return false;
                }
            }
        } else if (Build.VERSION.SDK_INT >= 23 && CommonUtils.getTargetSdkVersion(getSobotActivity().getApplicationContext()) >= 23) {
            if (ContextCompat.checkSelfPermission(getSobotActivity(), Manifest.permission.READ_EXTERNAL_STORAGE)
                    != PackageManager.PERMISSION_GRANTED) {
                return false;
            }
        }
        return true;
    }

    /**
     * 检查录音权限
     *
     * @return true, 已经获取权限;false,没有权限,尝试获取
     */
    protected boolean checkAudioPermission() {
        if (Build.VERSION.SDK_INT >= 23 && CommonUtils.getTargetSdkVersion(getSobotActivity().getApplicationContext()) >= 23) {
            if (ContextCompat.checkSelfPermission(getSobotActivity(), Manifest.permission.RECORD_AUDIO)
                    != PackageManager.PERMISSION_GRANTED) {
                this.requestPermissions(new String[]{Manifest.permission.RECORD_AUDIO},
                        ZhiChiConstant.SOBOT_PERMISSIONS_REQUEST_CODE);
                return false;
            }
        }
        return true;
    }

    /**
     * 判断是否有录音权限
     *
     * @return true, 已经获取权限;false,没有权限
     */
    protected boolean isHasAudioPermission() {
        if (Build.VERSION.SDK_INT >= 23 && CommonUtils.getTargetSdkVersion(getSobotActivity().getApplicationContext()) >= 23) {
            if (ContextCompat.checkSelfPermission(getSobotActivity(), Manifest.permission.RECORD_AUDIO)
                    != PackageManager.PERMISSION_GRANTED) {
                return false;
            }
        }
        return true;
    }

    /**
     * 检查相机权限
     *
     * @return true, 已经获取权限;false,没有权限,尝试获取
     */
    protected boolean checkCameraPermission() {
        if (Build.VERSION.SDK_INT >= 23 && CommonUtils.getTargetSdkVersion(getSobotActivity().getApplicationContext()) >= 23) {
            if (ContextCompat.checkSelfPermission(getSobotActivity(), Manifest.permission.CAMERA)
                    != PackageManager.PERMISSION_GRANTED) {
                this.requestPermissions(new String[]{Manifest.permission.CAMERA}
                        , ZhiChiConstant.SOBOT_PERMISSIONS_REQUEST_CODE);
                return false;
            }
        }
        return true;
    }

    /**
     * 判断是否有相机权限
     *
     * @return true, 已经获取权限;false,没有权限
     */
    protected boolean isHasCameraPermission() {
        if (Build.VERSION.SDK_INT >= 23 && CommonUtils.getTargetSdkVersion(getSobotActivity().getApplicationContext()) >= 23) {
            if (ContextCompat.checkSelfPermission(getSobotActivity(), Manifest.permission.CAMERA)
                    != PackageManager.PERMISSION_GRANTED) {
                return false;
            }
        }
        return true;
    }


    /**
     * 通过照相上传图片
     */
    public void selectPicFromCamera() {
        if (!CommonUtils.isExitsSdcard()) {
            ToastUtil.showCustomToast(getSobotActivity().getApplicationContext(), getString(R.string.sobot_sdcard_does_not_exist),
                    Toast.LENGTH_SHORT);
            return;
        }

        permissionListener = new PermissionListenerImpl() {
            @Override
            public void onPermissionSuccessListener() {
                //如果有拍照所需的权限，跳转到拍照界面
                startActivityForResult(SobotCameraActivity.newIntent(getSobotBaseFragment().getContext()), REQUEST_CODE_CAMERA);
            }
        };

        if (checkIsShowPermissionPop(getString(R.string.sobot_camera), getString(R.string.sobot_camera_yongtu), 3, 3)) {
            return;
        }

        if (!checkCameraPermission()) {
            return;
        }

        // 打开拍摄页面
        startActivityForResult(SobotCameraActivity.newIntent(getContext()), REQUEST_CODE_CAMERA);
    }

    /**
     * 从图库获取图片
     */
    public void selectPicFromLocal() {
        permissionListener = new PermissionListenerImpl() {
            @Override
            public void onPermissionSuccessListener() {
                ChatUtils.openSelectPic(getSobotActivity(), getSobotBaseFragment());
            }
        };
        if (checkIsShowPermissionPop(getString(R.string.sobot_memory_card), getString(R.string.sobot_memory_card_yongtu), 1, 0)) {
            return;
        }
        if (!checkStoragePermission(0)) {
            return;
        }
        ChatUtils.openSelectPic(getSobotActivity(), SobotBaseFragment.this);
    }

    /**
     * 判断是否有存储卡权限
     *
     * @param type                1 存储卡;2 麦克风;3 相机;
     * @param checkPermissionType 0：图片权限 1：视频权限，2：音频权限，3，所有细分的权限， android 13 使用
     * @return true, 已经获取权限;false,没有权限
     */
    protected boolean isHasPermission(int type, int checkPermissionType) {
        if (type == 1) {
            return isHasStoragePermission(checkPermissionType);
        } else if (type == 2) {
            return isHasAudioPermission();
        } else if (type == 3) {
            return isHasCameraPermission();
        }
        return true;
    }

    /**
     * 检测是否需要弹出权限用途提示框pop,如果弹出，则拦截接下来的处理逻辑，自己处理
     *
     * @param title
     * @param content
     * @param type
     * @param checkPermissionType 0：图片权限 1：视频权限，2：音频权限，3，所有细分的权限， android 13 使用
     * @return
     */
    public boolean checkIsShowPermissionPop(String title, String content, final int type, final int checkPermissionType) {
        if (ZCSobotApi.getSwitchMarkStatus(MarkConfig.SHOW_PERMISSION_TIPS_POP)) {
            if (!isHasPermission(type, checkPermissionType)) {
                SobotPermissionTipDialog dialog = new SobotPermissionTipDialog(getSobotActivity(), title, content, new SobotPermissionTipDialog.ClickViewListener() {
                    @Override
                    public void clickRightView(Context context, SobotPermissionTipDialog dialog) {
                        dialog.dismiss();
                        if (type == 1) {
                            if (!checkStoragePermission(checkPermissionType)) {
                                return;
                            }
                        } else if (type == 2) {
                            if (!checkAudioPermission()) {
                                return;
                            }
                        } else if (type == 3) {
                            if (!checkCameraPermission()) {
                                return;
                            }
                        }
                    }

                    @Override
                    public void clickLeftView(Context context, SobotPermissionTipDialog dialog) {
                        dialog.dismiss();
                    }
                });
                dialog.show();
                return true;
            }
        }
        return false;
    }

    /**
     * 从图库获取视频
     */
    public void selectVedioFromLocal() {
        permissionListener = new PermissionListenerImpl() {
            @Override
            public void onPermissionSuccessListener() {
                ChatUtils.openSelectVedio(getSobotActivity(), getSobotBaseFragment());
            }
        };
        if (checkIsShowPermissionPop(getString(R.string.sobot_memory_card), getString(R.string.sobot_memory_card_yongtu), 1, 1)) {
            return;
        }
        if (!checkStoragePermission(1)) {
            return;
        }
        ChatUtils.openSelectVedio(getSobotActivity(), SobotBaseFragment.this);
    }

    public static boolean isCameraCanUse() {

        boolean canUse = false;
        Camera mCamera = null;

        try {
            mCamera = Camera.open(0);
            Camera.Parameters mParameters = mCamera.getParameters();
            mCamera.setParameters(mParameters);
        } catch (Exception e) {
            canUse = false;
        }

        if (mCamera != null) {
            mCamera.release();
            canUse = true;
        }

        return canUse;
    }

    /**
     * 返回activity
     *
     * @return
     */
    public Activity getSobotActivity() {
        Activity activity = getActivity();
        if (activity == null) {
            return this.activity;
        }
        return activity;

    }
}