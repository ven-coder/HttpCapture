package com.levine.netcapture;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Adapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toolbar;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.blankj.utilcode.util.BarUtils;
import com.blankj.utilcode.util.FileIOUtils;
import com.blankj.utilcode.util.GsonUtils;
import com.blankj.utilcode.util.LogUtils;
import com.blankj.utilcode.util.ToastUtils;
import com.moczul.ok2curl.CurlInterceptor;
import com.moczul.ok2curl.logger.Loggable;

import java.util.ArrayList;
import java.util.List;

import static com.levine.netcapture.LocalNetRecordIO.SAVE_PATH;

public class NetCaptureRecordActivity extends Activity implements LocalNetRecordIO.CallBack {
    private ImageView mIvBlack;
    private SwipeRefreshLayout mRefreshView;
    private RecyclerView mRecyclerView;
    private Adapter mAdapter;
    private TextView mTvClean;

    public static void launch(Context context) {
        context.startActivity(new Intent(context, NetCaptureRecordActivity.class));
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_net_capture_record);

        mIvBlack = findViewById(R.id.iv_black);
        mRefreshView = findViewById(R.id.refresh_view);
        mRecyclerView = findViewById(R.id.recycler_view);
        mTvClean = findViewById(R.id.tv_clean);
        mIvBlack.setOnClickListener(v -> finish());
        mTvClean.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new AlertDialog.Builder(NetCaptureRecordActivity.this)
                        .setMessage("确定删除记录?")
                        .setPositiveButton("确定", (dialog, which) -> {
                            FileIOUtils.writeFileFromString(SAVE_PATH, GsonUtils.toJson(new CaptureBean()));
                            mAdapter.mData.clear();
                            mAdapter.notifyDataSetChanged();
                        }).setNegativeButton("取消", null).show();
            }
        });

        GridLayoutManager layoutManager = new GridLayoutManager(this, 1);
        mRecyclerView.setLayoutManager(layoutManager);
        mAdapter = new Adapter(null, mRecyclerView);
        mRecyclerView.setAdapter(mAdapter);
        LocalNetRecordIO.get(this);
        mRefreshView.setRefreshing(true);
        mRefreshView.setOnRefreshListener(() -> LocalNetRecordIO.get(NetCaptureRecordActivity.this));
    }

    @Override
    public void onSuccess(CaptureBean captureBean) {
        mRefreshView.setRefreshing(false);
        mAdapter.mData.clear();
        mAdapter.mData.addAll(captureBean.getData());
        mAdapter.notifyDataSetChanged();
    }

    @Override
    public void fail(String message) {
        mRefreshView.setRefreshing(false);
        ToastUtils.showShort(message);
    }


    private static class Adapter extends RecyclerView.Adapter<ItemView> {
        public List<CaptureBean.Data> mData = new ArrayList<>();
        private RecyclerView mRecyclerView;

        public Adapter(List<CaptureBean.Data> data, RecyclerView recyclerView) {
            if (data != null) {
                mData.addAll(data);
            }
            mRecyclerView = recyclerView;
        }

        @NonNull
        @Override
        public ItemView onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = View.inflate(parent.getContext(), R.layout.item_net, null);
            return new ItemView(view);
        }

        private void copyString(Context context, String string) {
            ClipboardManager cm = (ClipboardManager) context.getSystemService(Context.CLIPBOARD_SERVICE);
            ClipData mClipData = ClipData.newPlainText("Label", string);
            cm.setPrimaryClip(mClipData);
            ToastUtils.showShort("复制成功");
        }

        @Override
        public void onBindViewHolder(@NonNull ItemView holder, int position) {
            CaptureBean.Data data = mData.get(position);
            holder.tvPosition.setText((position + 1) + "." + (position == 0 ? "\t最新" : ""));
            holder.tvTime.setText(data.getRequest().getTime());
            holder.tvUrl.setText(data.getRequest().getUrl());
            holder.tvStatus.setText("状态：" + data.getResponse().getStatus());
            holder.tvRequestCurl.setText(TextUtils.isEmpty(data.getRequest().getCurl()) ? "无" : data.getRequest().getCurl());

            StringBuilder stringBuilder = new StringBuilder();
            for (CaptureBean.Data.RequestBean.HeadersBean headersBean : data.getRequest().getHeaders()) {
                if (stringBuilder.length() != 0) {
                    stringBuilder.append("\n");
                }
                stringBuilder.append(headersBean.getKey()).append("：").append(headersBean.getValue());
            }
            holder.tvRequestHeader.setText(TextUtils.isEmpty(stringBuilder.toString()) ? "无" : stringBuilder.toString());

            stringBuilder.delete(0, stringBuilder.length());
            for (CaptureBean.Data.RequestBean.UrlParameterBean parameterBean : data.getRequest().getUrlParameters()) {
                if (stringBuilder.length() != 0) {
                    stringBuilder.append("\n");
                }
                stringBuilder.append(parameterBean.getKey()).append("：").append(parameterBean.getValue());
            }
            holder.tvRequestUrlParameter.setText(TextUtils.isEmpty(stringBuilder.toString()) ? "无" : stringBuilder.toString());

            stringBuilder.delete(0, stringBuilder.length());
            for (CaptureBean.Data.RequestBean.ParameterBean parameterBean : data.getRequest().getParameter()) {
                if (stringBuilder.length() != 0) {
                    stringBuilder.append("\n");
                }
                stringBuilder.append(parameterBean.getKey()).append("：").append(parameterBean.getValue());
            }
            holder.tvRequestParameter.setText(TextUtils.isEmpty(stringBuilder.toString()) ? "无" : stringBuilder.toString());

            holder.tvRequestRaw.setText(TextUtils.isEmpty(data.getRequest().getRaw()) ? "无" : data.getRequest().getRaw());

            stringBuilder.delete(0, stringBuilder.length());
            for (CaptureBean.Data.ResponseBean.HeadersBeanX parameterBean : data.getResponse().getHeaders()) {
                if (stringBuilder.length() != 0) {
                    stringBuilder.append("\n");
                }
                stringBuilder.append(parameterBean.getKey()).append("：").append(parameterBean.getValue());
            }
            holder.tvResponseHeader.setText(TextUtils.isEmpty(stringBuilder.toString()) ? "无" : stringBuilder.toString());

            holder.tvResponseBody.setText(data.getResponse().getBody() + "");

            holder.llBody.setVisibility(data.isExpansion() ? View.VISIBLE : View.GONE);

            holder.tvExpansionSwitch.setText(data.isExpansion() ? "收起" : "点击查看");

            holder.itemView.setBackgroundColor(data.isExpansion() ? Color.parseColor("#FF4CAF50") : Color.parseColor("#f0f0f0"));

            holder.tvExpansionSwitch.setOnClickListener(v -> {
                data.setExpansion(!data.isExpansion());
                notifyItemChanged(position);
                mRecyclerView.scrollToPosition(position);
                mRecyclerView.postDelayed(() -> mRecyclerView.requestLayout(), 500);
            });
            holder.tvExpansionSwitchBottom.setOnClickListener(v -> {
                data.setExpansion(false);
                notifyItemChanged(position);
                mRecyclerView.scrollToPosition(position);
                mRecyclerView.postDelayed(() -> mRecyclerView.requestLayout(), 500);
            });

            holder.tvCopyHeader.setOnClickListener(v ->
                    copyString(v.getContext(), holder.tvTime.getText().toString() +
                            "\n" + holder.tvUrl.getText().toString() +
                            "\n" + holder.tvStatus.getText().toString()));
            holder.tvCopyRequestHeader.setOnClickListener(v -> copyString(v.getContext(), holder.tvRequestHeader.getText().toString()));
            holder.tvCopyRequestUrlParameter.setOnClickListener(v -> copyString(v.getContext(), holder.tvRequestUrlParameter.getText().toString()));
            holder.tvCopyRequestParameter.setOnClickListener(v -> copyString(v.getContext(), holder.tvRequestParameter.getText().toString()));
            holder.tvCopyRequestRaw.setOnClickListener(v -> copyString(v.getContext(), holder.tvRequestRaw.getText().toString()));
            holder.tvCopyResponseHeader.setOnClickListener(v -> copyString(v.getContext(), holder.tvResponseHeader.getText().toString()));
            holder.tvCopyResponseBody.setOnClickListener(v -> copyString(v.getContext(), holder.tvResponseBody.getText().toString()));
            holder.tvCopyRequestCurl.setOnClickListener(v -> copyString(v.getContext(), data.getRequest().getCurl() + ""));
        }

        @Override
        public int getItemCount() {
            return mData.size();
        }
    }

    private static class ItemView extends RecyclerView.ViewHolder {
        public TextView tvPosition;
        public TextView tvCopyHeader;
        public TextView tvTime;
        public TextView tvUrl;
        public TextView tvStatus;
        public TextView tvExpansionSwitch;
        public LinearLayout llBody;
        public TextView tvCopyRequestHeader;
        public TextView tvRequestHeader;
        public TextView tvCopyRequestUrlParameter;
        public TextView tvRequestUrlParameter;
        public TextView tvCopyRequestParameter;
        public TextView tvRequestParameter;
        public LinearLayout llResponse;
        public TextView tvCopyResponseHeader;
        public TextView tvResponseHeader;
        public TextView tvCopyResponseBody;
        public TextView tvResponseBody;
        public TextView tvExpansionSwitchBottom;
        public TextView tvCopyRequestRaw;
        public TextView tvRequestRaw;
        private TextView tvCopyRequestCurl;
        private TextView tvRequestCurl;

        public ItemView(View itemView) {
            super(itemView);
            tvCopyRequestCurl = itemView.findViewById(R.id.tv_copy_request_curl);
            tvRequestCurl = itemView.findViewById(R.id.tv_request_curl);
            tvCopyRequestRaw = itemView.findViewById(R.id.tv_copy_request_raw);
            tvRequestRaw = itemView.findViewById(R.id.tv_request_raw);
            tvPosition = itemView.findViewById(R.id.tv_position);
            tvCopyHeader = itemView.findViewById(R.id.tv_copy_header);
            tvTime = itemView.findViewById(R.id.tv_time);
            tvUrl = itemView.findViewById(R.id.tv_url);
            tvStatus = itemView.findViewById(R.id.tv_status);
            tvExpansionSwitch = itemView.findViewById(R.id.tv_expansion_switch);
            llBody = itemView.findViewById(R.id.ll_body);
            tvCopyRequestHeader = itemView.findViewById(R.id.tv_copy_request_header);
            tvRequestHeader = itemView.findViewById(R.id.tv_request_header);
            tvCopyRequestUrlParameter = itemView.findViewById(R.id.tv_copy_request_url_parameter);
            tvRequestUrlParameter = itemView.findViewById(R.id.tv_request_url_parameter);
            tvCopyRequestParameter = itemView.findViewById(R.id.tv_copy_request_parameter);
            tvRequestParameter = itemView.findViewById(R.id.tv_request_parameter);
            llResponse = itemView.findViewById(R.id.ll_response);
            tvCopyResponseHeader = itemView.findViewById(R.id.tv_copy_response_header);
            tvResponseHeader = itemView.findViewById(R.id.tv_response_header);
            tvCopyResponseBody = itemView.findViewById(R.id.tv_copy_response_body);
            tvResponseBody = itemView.findViewById(R.id.tv_response_body);
            tvExpansionSwitchBottom = itemView.findViewById(R.id.tv_expansion_switch_bottom);
        }
    }
}
