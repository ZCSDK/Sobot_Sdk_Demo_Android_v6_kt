package com.sobot.chat.camera.util;

import android.content.Context;
import android.hardware.Camera;
import android.util.Log;

import com.sobot.chat.MarkConfig;
import com.sobot.chat.ZCSobotApi;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class CameraParamUtil {
    private static final String TAG = "JCameraView";
    private CameraSizeComparator sizeComparator = new CameraSizeComparator();
    private static CameraParamUtil cameraParamUtil = null;

    private CameraParamUtil() {

    }

    public static CameraParamUtil getInstance() {
        if (cameraParamUtil == null) {
            cameraParamUtil = new CameraParamUtil();
            return cameraParamUtil;
        } else {
            return cameraParamUtil;
        }
    }

    /**
     * 预览尺寸
     * @param list 所有尺寸
     * @param th
     * @param rate
     * @return
     */
    public Camera.Size getPreviewSize(List<Camera.Size> list, int th, float rate) {
//        for (int i = 0; i < list.size(); i++) {
//            SobotLogUtils.d(i+"===="+list.get(i).width+","+list.get(i).height);
//        }
        Collections.sort(list, sizeComparator);
        int i = 0;
        for (Camera.Size s : list) {
            if ((s.width > th) && equalRate(s, rate)) {
                Log.i(TAG, "MakeSure Preview :w = " + s.width + " h = " + s.height);
                break;
            }
            i++;
        }
        if (i == list.size()) {
            return getBestSize(list, rate);
        } else {
            return list.get(i);
        }
    }

    public Camera.Size getPictureSize(List<Camera.Size> list, int th, float rate) {
        Collections.sort(list, sizeComparator);
        int i = 0;
        for (Camera.Size s : list) {
            if ((s.width > th) && equalRate(s, rate)) {
                Log.i(TAG, "MakeSure Picture :w = " + s.width + " h = " + s.height);
                break;
            }
            i++;
        }
        if (i == list.size()) {
            return getBestSize(list, rate);
        } else {
            return list.get(i);
        }
    }

    public Camera.Size getSmallPictureSize(Camera camera){
        if(camera != null){
            List<Camera.Size> sizes = camera.getParameters().getSupportedPictureSizes();
            Camera.Size temp = sizes.get(0);
            for(int i = 1;i < sizes.size();i ++){
                float scale = (float)(sizes.get(i).height) / sizes.get(i).width;
                if(temp.width > sizes.get(i).width && scale < 0.6f && scale > 0.5f)
                    temp = sizes.get(i);
            }
            Log.i(TAG, "MakeSure Picture :w = " + temp.width + " h = " + temp.height);
            return temp;
        }
        return null;
    }

    private Camera.Size getBestSize(List<Camera.Size> list, float rate) {
        float previewDisparity = 100;
        int index = 0;
        for (int i = 0; i < list.size(); i++) {
            Camera.Size cur = list.get(i);
            float prop = (float) cur.width / (float) cur.height;
            if (Math.abs(rate - prop) < previewDisparity) {
                previewDisparity = Math.abs(rate - prop);
                index = i;
            }
        }
        return list.get(index);
    }


    private boolean equalRate(Camera.Size s, float rate) {
        float r = (float) (s.width) / (float) (s.height);
        return Math.abs(r - rate) <= 0.2;
    }

    public boolean isSupportedFocusMode(List<String> focusList, String focusMode) {
        for (int i = 0; i < focusList.size(); i++) {
            if (focusMode.equals(focusList.get(i))) {
                Log.i(TAG, "FocusMode supported " + focusMode);
                return true;
            }
        }
        Log.i(TAG, "FocusMode not supported " + focusMode);
        return false;
    }

    public boolean isSupportedPictureFormats(List<Integer> supportedPictureFormats, int jpeg) {
        for (int i = 0; i < supportedPictureFormats.size(); i++) {
            if (jpeg == supportedPictureFormats.get(i)) {
                Log.i(TAG, "Formats supported " + jpeg);
                return true;
            }
        }
        Log.i(TAG, "Formats not supported " + jpeg);
        return false;
    }

    private class CameraSizeComparator implements Comparator<Camera.Size> {
        public int compare(Camera.Size lhs, Camera.Size rhs) {
            if (lhs.width == rhs.width) {
                if (lhs.height == rhs.height) {
                    return 0;
                } else if (lhs.height > rhs.height) {
                    return 1;
                } else {
                    return -1;
                }
            } else if (lhs.width > rhs.width) {
                return 1;
            } else {
                return -1;
            }
        }

    }
    /**
     * 获取预览画面要校正的角度。
     */
    public int getCameraDisplayOrientation(Context context, int cameraId) {
        int result;
        //根据横竖屏开关来显示
        if (ZCSobotApi.getSwitchMarkStatus(MarkConfig.LANDSCAPE_SCREEN)) {
            //横屏
            result=0;
        }else{
            //竖屏
            result=90;
        }
        /*Camera.CameraInfo info = new Camera.CameraInfo();
        try {
            Camera.getCameraInfo(cameraId, info);
        } catch (Exception e) {
            //ignor
        }
        WindowManager wm = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        int rotation = 0;
        if (wm != null) {
            rotation = wm.getDefaultDisplay().getRotation();
        }
        int degrees = 0;
        switch (rotation) {
            case Surface.ROTATION_0:
                degrees = 0;
                break;
            case Surface.ROTATION_90:
                degrees = 90;
                break;
            case Surface.ROTATION_180:
                degrees = 180;
                break;
            case Surface.ROTATION_270:
                degrees = 270;
                break;
        }
//        SobotLogUtils.d("========info.orientation ========"+info.orientation );
//        SobotLogUtils.d("========degrees========"+degrees);
        int result;
        if (info.facing == Camera.CameraInfo.CAMERA_FACING_FRONT) {
            result = (info.orientation + degrees) % 360;
            result = (360 - result) % 360;   // compensate the mirror
        } else {
            // back-facing
            result = (info.orientation - degrees + 360) % 360;
        }*/
//        SobotLogUtils.d("========degrees========"+degrees);
        return result;
    }
}
