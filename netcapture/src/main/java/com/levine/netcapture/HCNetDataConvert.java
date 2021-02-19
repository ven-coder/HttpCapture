package com.levine.netcapture;

public interface HCNetDataConvert {
    String requestUrl(String url);

    CaptureBean.Data.RequestBean.HeadersBean requestHeaders(CaptureBean.Data.RequestBean.HeadersBean headersBean);

    CaptureBean.Data.RequestBean.ParameterBean requestParameters(CaptureBean.Data.RequestBean.ParameterBean parameterBean);

    CaptureBean.Data.RequestBean.UrlParameterBean requestUrlParameters(CaptureBean.Data.RequestBean.UrlParameterBean parameterBean);

    CaptureBean.Data.ResponseBean.HeadersBeanX responseHeaders(CaptureBean.Data.ResponseBean.HeadersBeanX headersBeanX);

    String responseBody(String body);

}
