package com.levine.http_capture

import androidx.collection.ArrayMap

/**
 * 网络请求数据转换接口，用于数据的自定义处理
 * 比如如果数据加密了进行解密，显示的时候方便查看，否则显示的时候是自己加密的数据
 */
interface HCNetDataConvert {
    fun requestUrl(url: String): String
    fun requestHeaders(headersBean: CaptureBean.Data.RequestBean.HeadersBean): CaptureBean.Data.RequestBean.HeadersBean
    fun requestParameters(parameterBean: CaptureBean.Data.RequestBean.ParameterBean): CaptureBean.Data.RequestBean.ParameterBean
    fun requestUrlParameters(parameterBean: CaptureBean.Data.RequestBean.UrlParameterBean): CaptureBean.Data.RequestBean.UrlParameterBean
    fun responseHeaders(headersBeanX: CaptureBean.Data.ResponseBean.HeadersBeanX): CaptureBean.Data.ResponseBean.HeadersBeanX
    fun responseBody(body: String): String

}