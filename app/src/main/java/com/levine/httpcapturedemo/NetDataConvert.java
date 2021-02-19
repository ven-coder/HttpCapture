package com.levine.httpcapturedemo;

import com.blankj.utilcode.util.LogUtils;
import com.levine.netcapture.CaptureBean;
import com.levine.netcapture.HCNetDataConvert;

import org.jetbrains.annotations.NotNull;

/**
 * 标题：
 * 描述：抓包数据转换
 * 作者：李启文
 * 创建时间：2020-06-15 10:04
 */
public class NetDataConvert implements HCNetDataConvert {
    @NotNull
    @Override
    public CaptureBean.Data.RequestBean.HeadersBean requestHeaders(@NotNull CaptureBean.Data.RequestBean.HeadersBean headersBean) {
        return headersBean;
    }

    @NotNull
    @Override
    public CaptureBean.Data.RequestBean.ParameterBean requestParameters(@NotNull CaptureBean.Data.RequestBean.ParameterBean parameterBean) {

        LogUtils.d("requestParameters解密参数", parameterBean.getKey(), parameterBean.getValue());

        return parameterBean;
    }

    @NotNull
    @Override
    public String requestUrl(@NotNull String s) {
        return s;
    }

    @NotNull
    @Override
    public CaptureBean.Data.RequestBean.UrlParameterBean requestUrlParameters(@NotNull CaptureBean.Data.RequestBean.UrlParameterBean urlParameterBean) {

        return urlParameterBean;
    }

    @NotNull
    @Override
    public String responseBody(@NotNull String s) {
        return s;
    }

    @NotNull
    @Override
    public CaptureBean.Data.ResponseBean.HeadersBeanX responseHeaders(@NotNull CaptureBean.Data.ResponseBean.HeadersBeanX headersBeanX) {
        return headersBeanX;
    }
}
