package com.levine.http_capture

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.text.TextUtils
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.blankj.utilcode.util.ToastUtils
import java.lang.StringBuilder

/**
 * 标题：
 * 描述：
 * 作者：李启文
 * 创建时间：2020-06-11 11:16
 */
class ListAdapter : RecyclerView.Adapter<ListViewHolder>() {

    private val data: ArrayList<CaptureBean.Data> = arrayListOf()
    private var mOnItemExpansionSwitchListener: OnItemExpansionSwitchListener? = null
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ListViewHolder {
        val view = View.inflate(parent.context, R.layout.item_net, null)
        return ListViewHolder(view)
    }

    override fun getItemCount(): Int {
        return data.size
    }

    override fun onBindViewHolder(view: ListViewHolder, position: Int) {
        val bean = data[position]
        view.tvPosition?.text = "" + (position + 1) + "."
        bean.position = position + 1
        view.tvTime?.text = bean.request.time
        view.tvUrl?.text = bean.request.method + "：" + bean.request.url
        view.tvStatus?.text = "code：" + bean.response.status
        var headerString =
            view.tvTime.text.toString() + "\n" + view.tvUrl?.text.toString() + "\n" + view.tvStatus?.text.toString()
        view.tvCopyHeader.setOnClickListener { copyString(it.context, headerString) }
        val stringBuilder = StringBuilder()
        for (item in bean.request.headers) {
            stringBuilder.append(item.key)
            stringBuilder.append("：")
            stringBuilder.append(item.value)
            stringBuilder.append("\n")
        }
        view.tvRequestHeader?.text =
            if (TextUtils.isEmpty(stringBuilder.toString())) "没有" else stringBuilder.toString()
        view.tvCopyRequestHeader.setOnClickListener {
            copyString(
                it.context,
                view.tvRequestHeader.text.toString()
            )
        }
        stringBuilder.clear()
        for (item in bean.request.urlParameters) {
            if (bean.request.urlParameters.indexOf(item) == 0) stringBuilder.append("?")
            stringBuilder.append(item.key)
            stringBuilder.append("=")
            stringBuilder.append(item.value)
            if (bean.request.urlParameters.indexOf(item) != bean.request.urlParameters.size - 1) {
                stringBuilder.append("&")
            }
        }
        view.tvRequestUrlParameter?.text =
            if (TextUtils.isEmpty(stringBuilder.toString())) "没有参" else stringBuilder.toString()
        view.tvCopyRequestUrlParameter.setOnClickListener {
            copyString(
                it.context,
                view.tvRequestUrlParameter.text.toString()
            )
        }

        stringBuilder.clear()
        for (item in bean.request.parameter) {
            stringBuilder.append(item.key)
            stringBuilder.append("：")
            stringBuilder.append(item.value)
            stringBuilder.append("\n")
        }
        view.tvRequestParameter?.text =
            if (TextUtils.isEmpty(stringBuilder.toString())) "什么鬼参都没有" else stringBuilder.toString()
        view.tvCopyRequestParameter.setOnClickListener {
            copyString(
                it.context,
                view.tvRequestParameter.text.toString()
            )
        }
        stringBuilder.clear()
        for (item in bean.response.headers) {
            stringBuilder.append(item.key)
            stringBuilder.append("：")
            stringBuilder.append(item.value)
            stringBuilder.append("\n")
        }
        view.tvResponseHeader?.text =
            if (TextUtils.isEmpty(stringBuilder.toString())) "服务端没头" else stringBuilder.toString()

        view.tvCopyResponseHeader.setOnClickListener {
            copyString(
                it.context,
                view.tvResponseHeader.text.toString()
            )
        }
        stringBuilder.clear()
        view.tvResponseBody?.text = bean.response.body
        view.tvCopyResponseBody.setOnClickListener {
            copyString(
                it.context,
                view.tvResponseBody.text.toString()
            )
        }
        view.tvExpansionSwitch?.setOnClickListener {
            mOnItemExpansionSwitchListener?.onItemExpansionSwitch(
                bean,
                position
            )
        }
        view.tvExpansionSwitchBottom?.setOnClickListener {
            mOnItemExpansionSwitchListener?.onItemExpansionSwitch(
                bean,
                position
            )
        }
        view.itemView.setOnClickListener {
            mOnItemExpansionSwitchListener?.onItemExpansionSwitch(
                bean,
                position
            )
        }
        if (bean.isExpansion) {
            view.llBody.visibility = View.VISIBLE
            view.tvExpansionSwitchBottom.text = "收起"
            view.tvExpansionSwitch.text = "收起"
        } else {
            view.llBody.visibility = View.GONE
            view.tvExpansionSwitchBottom.text = "点击查看"
            view.tvExpansionSwitch.text = "点击查看"
        }
    }

    private fun copyString(context: Context, string: String) {
        val cm = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
        val mClipData = ClipData.newPlainText("Label", string)
        cm.setPrimaryClip(mClipData)
        ToastUtils.showShort("复制成功")
    }

    fun setData(data: ArrayList<CaptureBean.Data>) {
        this.data.clear()
        this.data.addAll(data)
        notifyDataSetChanged()
    }

    fun getData(): ArrayList<CaptureBean.Data> {
        return data
    }

    fun setOnItemClickListener(onItemExpansionSwitchListener: OnItemExpansionSwitchListener) {
        mOnItemExpansionSwitchListener = onItemExpansionSwitchListener
    }

    interface OnItemExpansionSwitchListener {
        fun onItemExpansionSwitch(data: CaptureBean.Data, position: Int)
    }

}