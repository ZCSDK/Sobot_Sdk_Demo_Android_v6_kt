package com.sobot.chat.activity.base;

import android.os.Bundle;

import com.sobot.chat.api.model.Information;
import com.sobot.chat.utils.SharedPreferencesUtil;
import com.sobot.chat.utils.ZhiChiConstant;

import java.io.Serializable;

/**
 * 帮助中心基类
 */
public abstract class SobotBaseHelpCenterActivity extends SobotChatBaseActivity {
    protected Bundle mInformationBundle;
    protected Information mInfo;

    @Override
    protected void initBundleData(Bundle savedInstanceState) {
        if (savedInstanceState == null) {
            mInformationBundle = getIntent().getBundleExtra(ZhiChiConstant.SOBOT_BUNDLE_INFORMATION);
        } else {
            mInformationBundle = savedInstanceState.getBundle(ZhiChiConstant.SOBOT_BUNDLE_INFORMATION);
        }
        if (mInformationBundle != null) {
            Serializable sobot_info = mInformationBundle.getSerializable(ZhiChiConstant.SOBOT_BUNDLE_INFO);
            if (sobot_info instanceof Information) {
                mInfo = (Information) sobot_info;
                SharedPreferencesUtil.saveObject(getSobotBaseContext(),
                        ZhiChiConstant.sobot_last_current_info, mInfo);
            }
        }
    }

    protected void onSaveInstanceState(Bundle outState) {
        //销毁前缓存数据
        outState.putBundle(ZhiChiConstant.SOBOT_BUNDLE_INFORMATION, mInformationBundle);
        super.onSaveInstanceState(outState);
    }
}