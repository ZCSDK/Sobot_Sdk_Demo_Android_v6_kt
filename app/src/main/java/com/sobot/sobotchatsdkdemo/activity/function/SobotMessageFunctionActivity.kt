package com.sobot.sobotchatsdkdemo.activity.function

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.ImageView
import android.widget.RelativeLayout
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.sobot.chat.ZCSobotApi
import com.sobot.chat.activity.WebViewActivity
import com.sobot.chat.api.model.Information
import com.sobot.chat.core.channel.Const
import com.sobot.chat.utils.SharedPreferencesUtil
import com.sobot.chat.utils.ToastUtil
import com.sobot.sobotchatsdkdemo.R
import com.sobot.sobotchatsdkdemo.util.SobotSPUtil.getObject

class SobotMessageFunctionActivity : AppCompatActivity(), View.OnClickListener {
    private var sobot_tv_left: RelativeLayout? = null
    private var sobot_rl_4_5_2: RelativeLayout? = null
    private var sobot_rl_4_5: RelativeLayout? = null
    private var sobot_rl_4_51: RelativeLayout? = null
    private var sobotImage452: ImageView? = null
    private var status452 = false
    private var tv_message_fun_4_5_2: TextView? = null
    private var tv_message_fun_4_5_3: TextView? = null
    private var tv_message_fun_4_5_4: TextView? = null
    private var tv_message_fun_4_5_6: TextView? = null
    private var sobot_tv_save: TextView? = null
    private var information: Information? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (supportActionBar != null) {
            supportActionBar!!.hide()
        }
        setContentView(R.layout.sobot_demo_message_func_activity)
        information = getObject(context, "sobot_demo_infomation") as Information?
        findvViews()
    }

    private fun findvViews() {
        sobot_tv_left = findViewById<View>(R.id.sobot_demo_tv_left) as RelativeLayout
        sobot_tv_left!!.setOnClickListener { finish() }
        val sobot_text_title = findViewById<View>(R.id.sobot_demo_tv_title) as TextView
        sobot_text_title.text = "消息相关"
        sobot_tv_save = findViewById(R.id.sobot_tv_save)
        sobot_tv_save!!.setOnClickListener(this)
        sobot_tv_save!!.setVisibility(View.VISIBLE)
        tv_message_fun_4_5_2 = findViewById(R.id.tv_message_fun_4_5_2)
        tv_message_fun_4_5_2!!.setText("https://www.sobot.com/developerdocs/app_sdk/android.html#_4-5-2-设置是否开启消息提醒")
        setOnClick(
            tv_message_fun_4_5_2,
            "https://www.sobot.com/developerdocs/app_sdk/android.html#_4-5-2-%E8%AE%BE%E7%BD%AE%E6%98%AF%E5%90%A6%E5%BC%80%E5%90%AF%E6%B6%88%E6%81%AF%E6%8F%90%E9%86%92"
        )
        tv_message_fun_4_5_3 = findViewById(R.id.tv_message_fun_4_5_3)
        tv_message_fun_4_5_3!!.setText("https://www.sobot.com/developerdocs/app_sdk/android.html#_4-5-3-设置离线消息")
        setOnClick(
            tv_message_fun_4_5_3,
            "https://www.sobot.com/developerdocs/app_sdk/android.html#_4-5-3-%E8%AE%BE%E7%BD%AE%E7%A6%BB%E7%BA%BF%E6%B6%88%E6%81%AF"
        )
        tv_message_fun_4_5_4 = findViewById(R.id.tv_message_fun_4_5_4)
        tv_message_fun_4_5_4!!.setText("https://www.sobot.com/developerdocs/app_sdk/android.html#_4-5-4-注册广播、获取新收到的信息和未读消息数")
        setOnClick(
            tv_message_fun_4_5_4,
            "https://www.sobot.com/developerdocs/app_sdk/android.html#_4-5-4-%E6%B3%A8%E5%86%8C%E5%B9%BF%E6%92%AD%E3%80%81%E8%8E%B7%E5%8F%96%E6%96%B0%E6%94%B6%E5%88%B0%E7%9A%84%E4%BF%A1%E6%81%AF%E5%92%8C%E6%9C%AA%E8%AF%BB%E6%B6%88%E6%81%AF%E6%95%B0"
        )
        tv_message_fun_4_5_6 = findViewById(R.id.tv_message_fun_4_5_6)
        tv_message_fun_4_5_6!!.setText("https://www.sobot.com/developerdocs/app_sdk/android.html#_4-5-6-自定义超链接的点击事件（拦截范围：帮助中心、留言、聊天、留言记录、商品卡片，订单卡片）")
        setOnClick(
            tv_message_fun_4_5_6,
            "https://www.sobot.com/developerdocs/app_sdk/android.html#_4-5-6-%E8%87%AA%E5%AE%9A%E4%B9%89%E8%B6%85%E9%93%BE%E6%8E%A5%E7%9A%84%E7%82%B9%E5%87%BB%E4%BA%8B%E4%BB%B6%EF%BC%88%E6%8B%A6%E6%88%AA%E8%8C%83%E5%9B%B4%EF%BC%9A%E5%B8%AE%E5%8A%A9%E4%B8%AD%E5%BF%83%E3%80%81%E7%95%99%E8%A8%80%E3%80%81%E8%81%8A%E5%A4%A9%E3%80%81%E7%95%99%E8%A8%80%E8%AE%B0%E5%BD%95%E3%80%81%E5%95%86%E5%93%81%E5%8D%A1%E7%89%87%EF%BC%8C%E8%AE%A2%E5%8D%95%E5%8D%A1%E7%89%87%EF%BC%89"
        )
        sobot_rl_4_5_2 = findViewById<View>(R.id.sobot_rl_4_5_2) as RelativeLayout
        sobot_rl_4_5_2!!.setOnClickListener(this)
        sobotImage452 = findViewById<View>(R.id.sobot_image_4_5_2) as ImageView
        sobot_rl_4_5 = findViewById<View>(R.id.sobot_rl_4_5) as RelativeLayout
        sobot_rl_4_5!!.setOnClickListener(this)
        sobot_rl_4_51 = findViewById<View>(R.id.sobot_rl_4_51) as RelativeLayout
        sobot_rl_4_51!!.setOnClickListener(this)
        if (information != null) {
            status452 =
                SharedPreferencesUtil.getBooleanData(context, Const.SOBOT_NOTIFICATION_FLAG, false)
            setImageShowStatus(status452, sobotImage452)
        }
    }

    fun setOnClick(view: TextView?, url: String?) {
        view!!.setOnClickListener(object : View.OnClickListener {
            override fun onClick(v: View) {
                val intent = Intent(context, WebViewActivity::class.java)
                intent.putExtra("url", url)
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                context.startActivity(intent)
            }
        })
    }

    override fun onClick(v: View) {
        when (v.id) {
            R.id.sobot_demo_tv_left -> finish()
            R.id.sobot_tv_save -> {
                //设置是否开启消息提醒
                ZCSobotApi.setNotificationFlag(
                    context,
                    status452,
                    com.sobot.chat.R.drawable.sobot_logo_small_icon,
                    com.sobot.chat.R.drawable.sobot_logo_icon
                )
                ToastUtil.showToast(context, "已保存")
                finish()
            }
            R.id.sobot_rl_4_5 -> if (information != null) {
                val num = ZCSobotApi.getUnReadMessage(context, information!!.partnerid)
                ToastUtil.showToast(context, "未读消息数=$num")
            } else {
                ZCSobotApi.getUnReadMessage(context, null)
            }
            R.id.sobot_rl_4_51 -> if (information != null) {
                ZCSobotApi.clearUnReadNumber(context, information!!.partnerid)
                ToastUtil.showToast(context, "已清除")
            } else {
                ZCSobotApi.clearUnReadNumber(context, null)
            }
            R.id.sobot_rl_4_5_2 -> {
                status452 = !status452
                setImageShowStatus(status452, sobotImage452)
            }
        }
    }

    private fun setImageShowStatus(status: Boolean, imageView: ImageView?) {
        if (status) {
            imageView!!.setBackgroundResource(R.drawable.sobot_demo_icon_open)
        } else {
            imageView!!.setBackgroundResource(R.drawable.sobot_demo_icon_close)
        }
    }

    val context: Context
        get() = this
}