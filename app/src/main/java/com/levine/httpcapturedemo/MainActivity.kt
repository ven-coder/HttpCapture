package com.levine.httpcapturedemo

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.blankj.utilcode.util.*
import com.levine.netcapture.NCPInterceptor
import com.levine.netcapture.NetCaptureRecordActivity
import kotlinx.android.synthetic.main.activity_main.*
import okhttp3.*
//import okhttp3.logging.HttpLoggingInterceptor
import java.io.IOException

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        LogUtils.getConfig().globalTag = "123456"
        val request = Request.Builder()
            .url("https://api.douban.com/v2/book/1220562?aaaaa=bbbbbb")
            .addHeader("Header-1", "Header1")
            .addHeader("Header-2", "Header2")
            .post(
                FormBody.Builder()
                    .add("param_1", "param_value_1++dsafaad++--===")
                    .build()
            ).build()

        val okHttpClient = OkHttpClient().newBuilder()
        okHttpClient.addInterceptor(NCPInterceptor(NetDataConvert()))
        tv_test.setOnClickListener {
            val call = okHttpClient.build().newCall(request)
            call.enqueue(object : Callback {
                override fun onFailure(call: Call, e: IOException) {
                    NetCaptureRecordActivity.launch(this@MainActivity)
                }

                override fun onResponse(call: Call, response: Response) {
                    NetCaptureRecordActivity.launch(this@MainActivity)
                }
            })
        }
    }
}
