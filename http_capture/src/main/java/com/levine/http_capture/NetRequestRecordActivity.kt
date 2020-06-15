package com.levine.http_capture

import android.content.*
import android.graphics.Color
import android.os.Bundle
import android.os.Looper
import android.text.TextUtils
import android.view.Menu
import android.view.MenuItem
import android.view.View
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.blankj.utilcode.util.*
import kotlinx.android.synthetic.main.view_refresh_list.*
import java.lang.Exception

/**
 * 标题：
 * 描述：
 * 创建时间：2020-06-11 10:52
 */
class NetRequestRecordActivity : AppCompatActivity(), LocalNetRecordIO.CallBack,
    ListAdapter.OnItemExpansionSwitchListener {
    var mListAdapter: ListAdapter? = null

    companion object {
        fun launch(context: Context) {
            context.startActivity(Intent(context, NetRequestRecordActivity::class.java))
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.view_refresh_list)
        val toolbar = findViewById<Toolbar>(R.id.toolbar)
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setHomeButtonEnabled(true)
        BarUtils.setStatusBarLightMode(this, false)
        BarUtils.setStatusBarColor(this, Color.BLACK)
        BarUtils.addMarginTopEqualStatusBarHeight(toolbar)
        ll_list_header.visibility = View.GONE
        val gridLayoutManager = GridLayoutManager(this, 1)
        recycler_view.layoutManager = gridLayoutManager
        mListAdapter = ListAdapter()
        mListAdapter?.setOnItemClickListener(this)
        recycler_view.adapter = mListAdapter
        LocalNetRecordIO.get(this)
        refresh_view.setOnRefreshListener { LocalNetRecordIO.get(this) }
        recycler_view.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                val position = gridLayoutManager.findFirstVisibleItemPosition()
                if (position == -1) return
                val data = mListAdapter?.getData()?.get(position)!!
                if (data.isExpansion) {
                    ll_list_header.visibility = View.VISIBLE
                } else {
                    ll_list_header.visibility = View.GONE
                }
                ll_list_header.requestLayout()
                fillHeaderData(data, position)
            }
        })
    }

    fun fillHeaderData(data: CaptureBean.Data, position: Int) {
        tv_list_position.text = data?.position.toString() + "."
        tv_list_time.text = data?.request?.time
        tv_list_url.text = data.request.method + "：" + data?.request?.url
        tv_list_url.requestLayout()
        tv_list_status.text = "code：" + data?.response?.status.toString()
        val headerString =
            tv_list_time.text.toString() + "\n" + tv_list_url.text.toString() + "\n" + tv_list_status.text.toString()
        tv_list_expansion_switch.text = if (data?.isExpansion!!) "收起" else "点击查看"
        tv_list_copy_header.setOnClickListener { copyString(it.context, headerString) }
        tv_list_expansion_switch.setOnClickListener { onItemExpansionSwitch(data, position) }
        ll_list_header.setOnClickListener { onItemExpansionSwitch(data, position) }
    }

    private fun copyString(context: Context, string: String) {
        val cm = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
        val mClipData = ClipData.newPlainText("Label", string)
        cm.setPrimaryClip(mClipData)
        ToastUtils.showShort("复制成功")
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.toolbar, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        when (item?.itemId) {
            R.id.delete_all -> {
                AlertDialog.Builder(this, R.style.Theme_AppCompat_Dialog_Alert)
                    .setMessage("确定删除所有？")
                    .setNegativeButton("取消", null)
                    .setPositiveButton("确定") { _, _ ->
                        FileUtils.delete(Constant.SAVE_PATH)
                        LocalNetRecordIO.get(this@NetRequestRecordActivity)
                    }.show()
            }
            android.R.id.home -> {
                finish()
            }
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onSuccess(captureBean: CaptureBean) {
        runOnUiThread {
            refresh_view.isRefreshing = false
            val data = arrayListOf<CaptureBean.Data>()
            for (item in captureBean.data) {
                item.isExpansion = false
                data.add(item)
                if (captureBean.data.indexOf(item) == 0) {
                    item.position = 1
                    fillHeaderData(item, 0)
                }
            }
            mListAdapter?.setData(data)
            tv_empty.visibility = View.GONE
        }
    }

    override fun fail(message: String) {
        runOnUiThread {
            refresh_view.isRefreshing = false
            mListAdapter?.setData(arrayListOf())
            tv_empty.visibility = View.VISIBLE
        }

    }

    override fun onItemExpansionSwitch(data: CaptureBean.Data, position: Int) {
        data.isExpansion = !data.isExpansion
        tv_list_expansion_switch.text = if (data.isExpansion) "收起" else "点击查看"
        mListAdapter?.notifyItemChanged(position)
        recycler_view.scrollToPosition(position)
    }

}