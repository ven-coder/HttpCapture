package com.levine.http_capture;

import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class ListViewHolder extends RecyclerView.ViewHolder {
    TextView tvCopyHeader;
    TextView tvTime;
    TextView tvUrl;
    TextView tvStatus;
    TextView tvExpansionSwitch;
    LinearLayout llBody;
    TextView tvCopyRequestHeader;
    TextView tvRequestHeader;
    TextView tvCopyRequestParameter;
    TextView tvRequestParameter;
    TextView tvCopyResponseHeader;
    TextView tvResponseHeader;
    TextView tvCopyResponseBody;
    TextView tvResponseBody;
    TextView tvExpansionSwitchBottom;
    TextView tvPosition;
    TextView tvCopyRequestUrlParameter;
    TextView tvRequestUrlParameter;

    public ListViewHolder(@NonNull View itemView) {
        super(itemView);
        tvCopyHeader = (TextView) itemView.findViewById(R.id.tv_copy_header);
        tvTime = (TextView) itemView.findViewById(R.id.tv_time);
        tvUrl = (TextView) itemView.findViewById(R.id.tv_url);
        tvStatus = (TextView) itemView.findViewById(R.id.tv_status);
        tvExpansionSwitch = (TextView) itemView.findViewById(R.id.tv_expansion_switch);
        llBody = (LinearLayout) itemView.findViewById(R.id.ll_body);
        tvCopyRequestHeader = (TextView) itemView.findViewById(R.id.tv_copy_request_header);
        tvRequestHeader = (TextView) itemView.findViewById(R.id.tv_request_header);
        tvCopyRequestParameter = (TextView) itemView.findViewById(R.id.tv_copy_request_parameter);
        tvRequestParameter = (TextView) itemView.findViewById(R.id.tv_request_parameter);
        tvCopyResponseHeader = (TextView) itemView.findViewById(R.id.tv_copy_response_header);
        tvResponseHeader = (TextView) itemView.findViewById(R.id.tv_response_header);
        tvCopyResponseBody = (TextView) itemView.findViewById(R.id.tv_copy_response_body);
        tvResponseBody = (TextView) itemView.findViewById(R.id.tv_response_body);
        tvExpansionSwitchBottom = (TextView) itemView.findViewById(R.id.tv_expansion_switch_bottom);
        tvPosition = (TextView) itemView.findViewById(R.id.tv_position);
        tvCopyRequestUrlParameter = (TextView) itemView.findViewById(R.id.tv_copy_request_url_parameter);
        tvRequestUrlParameter = (TextView) itemView.findViewById(R.id.tv_request_url_parameter);

    }
}
