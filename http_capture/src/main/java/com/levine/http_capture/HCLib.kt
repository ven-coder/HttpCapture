package com.levine.http_capture

import android.app.Activity
import android.app.Application
import android.os.Bundle
import android.view.MotionEvent
import android.view.View
import android.widget.FrameLayout
import android.widget.TextView
import com.blankj.utilcode.util.PathUtils
import com.blankj.utilcode.util.ScreenUtils
import com.blankj.utilcode.util.SizeUtils
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors

/**
 * Levine
 * 创建时间：2020-06-10 18:28
 */
object HCLib {
    private var mThreadPool: ExecutorService? = null
    private var y: Float = 400f
    private var x: Float = 0f
    private var isEnableActivityFloatView = true //是否允许显示浮窗，默认允许

    @Synchronized
    fun init(application: Application) {
        //创建单线程池专门用于处理数据的保存读取
        if (mThreadPool != null) return
        mThreadPool = Executors.newFixedThreadPool(1)
        Constant.SAVE_PATH = PathUtils.getExternalAppDataPath() + "/" + Constant.FILE_NAME
        application.registerActivityLifecycleCallbacks(object :
            Application.ActivityLifecycleCallbacks {
            override fun onActivityPaused(activity: Activity) {
            }

            override fun onActivityStarted(activity: Activity) {
                showActivityFloatView(activity)
            }

            override fun onActivityDestroyed(activity: Activity) {
            }

            override fun onActivitySaveInstanceState(activity: Activity, outState: Bundle) {
            }

            override fun onActivityStopped(activity: Activity) {
            }

            override fun onActivityCreated(activity: Activity, savedInstanceState: Bundle?) {
            }

            override fun onActivityResumed(activity: Activity) {
            }
        })
    }

    fun getThreadPool(): ExecutorService? {
        return mThreadPool
    }

    fun isEnableActivityFloatView(isEnableActivityFloatView: Boolean) {
        this.isEnableActivityFloatView = isEnableActivityFloatView
    }

    /**
     * 显示网络日志入口浮窗
     */
    fun showActivityFloatView(activity: Activity) {
        if (!isEnableActivityFloatView) {
            hideActivityFloatView(activity)
            return
        }
        if (activity.javaClass.canonicalName == NetRequestRecordActivity.javaClass.canonicalName!!.replace(
                ".Companion",
                ""
            )
        ) return
        val floatView = activity.findViewById<FrameLayout>(R.id.fl_com_levine_hc_activity_float)
        if (floatView == null) {
            val content = activity.findViewById<FrameLayout>(android.R.id.content)
            content.addView(createActivityFloatView(activity))
        } else {
            val tvLog = floatView.findViewById<TextView>(R.id.tv_com_levine_hc_http_log)
            tvLog.x = x
            tvLog.y = y
        }
    }

    /**
     * 隐藏网络日志入口浮窗
     */
    fun hideActivityFloatView(activity: Activity) {
        if (activity.javaClass.canonicalName == NetRequestRecordActivity.javaClass.canonicalName!!.replace(
                ".Companion",
                ""
            )
        ) return
        val floatView = activity.findViewById<FrameLayout>(R.id.fl_com_levine_hc_activity_float)
        if (floatView != null) {
            val content = activity.findViewById<FrameLayout>(android.R.id.content)
            content.removeView(floatView)
        }
    }

    private fun createActivityFloatView(activity: Activity): View {
        val view = View.inflate(activity, R.layout.view_activity_float, null)
        val tvLog = view.findViewById<TextView>(R.id.tv_com_levine_hc_http_log)
        val layoutParams = FrameLayout.LayoutParams(SizeUtils.dp2px(50f), SizeUtils.dp2px(50f))
        tvLog.y = y
        tvLog.x = x
        tvLog.layoutParams = layoutParams
        view.setOnTouchListener(object : View.OnTouchListener {
            var downTime = 0L
            override fun onTouch(v: View, event: MotionEvent): Boolean {
                when (event.action) {
                    MotionEvent.ACTION_DOWN -> {
                        if (event.x > tvLog.x && event.x < tvLog.x + SizeUtils.dp2px(50f) && event.y > tvLog.y && event.y < tvLog.y + SizeUtils.dp2px(
                                50f
                            )
                        ) {
                            downTime = System.currentTimeMillis()
                            return true
                        }
                    }
                    MotionEvent.ACTION_MOVE -> {
                        y = event.y - SizeUtils.dp2px(50f) / 2
                        x = event.x - SizeUtils.dp2px(50f) / 2
                        tvLog.y = y
                        tvLog.x = x
                        return true
                    }
                    MotionEvent.ACTION_UP -> {
                        if (x + SizeUtils.dp2px(50f) / 2 >= ScreenUtils.getScreenWidth() / 2) {
                            x = ScreenUtils.getScreenWidth().toFloat() - SizeUtils.dp2px(50f)
                        }
                        if (x + SizeUtils.dp2px(50f) / 2 < ScreenUtils.getScreenWidth() / 2) {
                            x = 0f
                        }
                        tvLog.x = x
                        if (System.currentTimeMillis() - downTime < 250) {
                            NetRequestRecordActivity.launch(activity)
                        }
                        return true
                    }
                }
                return false
            }
        })
        return view
    }
}