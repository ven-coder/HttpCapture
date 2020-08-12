package com.levine.http_capture;

import android.text.TextUtils;
import android.util.Log;

import com.blankj.utilcode.util.FileIOUtils;
import com.blankj.utilcode.util.FileUtils;
import com.blankj.utilcode.util.GsonUtils;
import com.blankj.utilcode.util.LogUtils;
import com.blankj.utilcode.util.TimeUtils;

import java.io.EOFException;
import java.net.URLDecoder;
import java.nio.charset.Charset;

import okhttp3.MediaType;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import okio.Buffer;

import static com.levine.http_capture.Constant.SAVE_PATH;
import static com.levine.http_capture.Constant.SAVE_RECORD_LENGTH;


public class LocalNetRecordIO {

    public static void saveRequest(Request request, HCNetDataConvert convert) {
        HCLib.INSTANCE.getThreadPool().submit(() -> {
            dispose(0, request, null, null, null, convert);
        });
    }

    public static void saveResponse(String body, Response response, HCNetDataConvert convert) {
        HCLib.INSTANCE.getThreadPool().submit(() -> {
            dispose(1, null, body, response, null, convert);
        });
    }

    public static void get(CallBack callBack) {
        HCLib.INSTANCE.getThreadPool().submit(() -> {
            dispose(2, null, null, null, callBack, null);
        });
    }

    private static synchronized void dispose(
            int type,
            Request request,
            String body,
            Response response,
            CallBack callBack,
            HCNetDataConvert convert
    ) {
        try {
            switch (type) {
                case 0:
                    syncSaveRequest(request, convert);
                    break;
                case 1:
                    syncSaveResponse(body, response, convert);
                    break;
                case 2:
                    syncGet(callBack, convert);
                    break;
            }
        } catch (Exception e) {
            LogUtils.e("capture保存失败", e.getMessage());
            LogUtils.e("报错", e.getMessage());
            StackTraceElement[] stackTrace = e.getStackTrace();
            for (StackTraceElement stackTraceElement : stackTrace) {
                Log.e(LogUtils.getConfig().getGlobalTag(), stackTraceElement.toString());
            }
        }
    }

    private static synchronized void syncGet(CallBack callBack, HCNetDataConvert convert) {
        if (callBack == null) return;
        //读取本地请求记录
        String localJson = FileIOUtils.readFile2String(SAVE_PATH);
        if (!TextUtils.isEmpty(localJson)) {
            CaptureBean captureBean = GsonUtils.fromJson(localJson, CaptureBean.class);
            callBack.onSuccess(captureBean);
        } else {
            callBack.fail("None");
        }
    }

    private static synchronized void syncSaveRequest(Request request, HCNetDataConvert convert) throws Exception {
        if (!FileUtils.createOrExistsFile(SAVE_PATH))
            return;
        if (!HCLib.INSTANCE.isEnableActivityFloatView()) FileUtils.delete(Constant.SAVE_PATH);
        CaptureBean.Data captureBean = new CaptureBean.Data();
        captureBean.getResponse().setStatus(-1);
        //保存请求数据
        captureBean.getRequest().setTime(TimeUtils.getNowString());
        captureBean.getRequest().setMethod(request.method());

        if (convert == null) {
            if (TextUtils.isEmpty(request.url().query())) {
                captureBean.getRequest().setUrl(request.url().toString());
            } else {
                captureBean.getRequest().setUrl(request.url().toString().replace("?" + request.url().query(), ""));
            }
        } else {
            captureBean.getRequest().setUrl(convert.requestUrl(request.url().toString()));
        }

        //请求头
        for (String key : request.headers().names()) {
            String value = request.header(key);
            CaptureBean.Data.RequestBean.HeadersBean headersBean = new CaptureBean.Data.RequestBean.HeadersBean();
            headersBean.setKey(key);
            headersBean.setValue(value);

            if (key.equals("REQUEST_TAG")) {
                captureBean.getRequest().setRequestTag(value);
            }

            if (convert == null) {
                captureBean.getRequest().getHeaders().add(headersBean);
            } else {
                captureBean.getRequest().getHeaders().add(convert.requestHeaders(headersBean));
            }

        }

        //url参数
        for (String item : request.url().queryParameterNames()) {
            CaptureBean.Data.RequestBean.UrlParameterBean parameterBean = new CaptureBean.Data.RequestBean.UrlParameterBean();
            parameterBean.setKey(item);
            parameterBean.setValue(request.url().queryParameter(item));
            if (convert == null) {
                captureBean.getRequest().getUrlParameters().add(parameterBean);
            } else {
                captureBean.getRequest().getUrlParameters().add(convert.requestUrlParameters(parameterBean));
            }
        }

        //请求参数
        RequestBody requestBody = request.body();
        if (requestBody != null) {
            Buffer buffer = new Buffer();
            requestBody.writeTo(buffer);
            Charset charset = Charset.forName("UTF-8");
            MediaType contentType = requestBody.contentType();
            if (contentType != null) {
                charset = Charset.forName("UTF-8");
            }
            if (isPlaintext(buffer)) {
                String paramsString = buffer.readString(charset);
                String[] params = paramsString.split("&");
                for (String item : params) {
                    String[] param = item.split("=");
                    if (param.length < 2) continue;
                    CaptureBean.Data.RequestBean.ParameterBean parameterBean = new CaptureBean.Data.RequestBean.ParameterBean();
                    parameterBean.setKey(param[0]);
                    parameterBean.setValue(URLDecoder.decode(param[1], "UTF-8"));
                    if (convert == null) {
                        captureBean.getRequest().getParameter().add(parameterBean);
                    } else {
                        captureBean.getRequest().getParameter().add(convert.requestParameters(parameterBean));
                    }
                }
            } else {
                CaptureBean.Data.RequestBean.ParameterBean parameterBean = new CaptureBean.Data.RequestBean.ParameterBean();
                parameterBean.setKey("None");
                parameterBean.setValue("参数为文件或其他类型数据");
                if (convert == null) {
                    captureBean.getRequest().getParameter().add(parameterBean);
                } else {
                    captureBean.getRequest().getParameter().add(convert.requestParameters(parameterBean));
                }
            }
        }

        String localJson = FileIOUtils.readFile2String(SAVE_PATH);
        CaptureBean localCaptureBean;
        if (TextUtils.isEmpty(localJson)) {
            localCaptureBean = new CaptureBean();
        } else {
            localCaptureBean = GsonUtils.fromJson(localJson, CaptureBean.class);
        }

        //保存的历史记录
        if (localCaptureBean.getData().size() > SAVE_RECORD_LENGTH) {
            localCaptureBean.getData().remove(localCaptureBean.getData().size() - 1);
        }
        localCaptureBean.getData().add(0, captureBean);
        FileIOUtils.writeFileFromString(SAVE_PATH, GsonUtils.toJson(localCaptureBean));
    }


    private static synchronized void syncSaveResponse(String body, Response response, HCNetDataConvert convert) {
        //读取本地请求记录
        String localJson = FileIOUtils.readFile2String(SAVE_PATH);
        if (TextUtils.isEmpty(localJson)) return;
        CaptureBean localCaptureBean = GsonUtils.fromJson(localJson, CaptureBean.class);
        CaptureBean.Data captureBean = null;
        Request request = response.request();
        for (CaptureBean.Data data : localCaptureBean.getData()) {
            //找到对应的本地请求记录
            for (String key : request.headers().names()) {
                String value = request.header(key);
                if (key.equals("REQUEST_TAG") && value != null && value.equals(data.getRequest().getRequestTag())) {
                    captureBean = data;
                }
            }
        }
        if (captureBean == null) return;
        //保存响应数据
        for (String key : response.headers().names()) {
            String value = response.header(key);
            CaptureBean.Data.ResponseBean.HeadersBeanX headersBeanX = new CaptureBean.Data.ResponseBean.HeadersBeanX();
            headersBeanX.setKey(key);
            headersBeanX.setValue(value);
            if (convert == null) {
                captureBean.getResponse().getHeaders().add(headersBeanX);
            } else {
                captureBean.getResponse().getHeaders().add(convert.responseHeaders(headersBeanX));
            }
        }
        if (convert == null) {
            captureBean.getResponse().setBody(body);
        } else {
            captureBean.getResponse().setBody(convert.responseBody(body));
        }
        //保存的历史记录
        if (localCaptureBean.getData().size() > SAVE_RECORD_LENGTH) {
            localCaptureBean.getData().remove(localCaptureBean.getData().size() - 1);
        }
        FileIOUtils.writeFileFromString(SAVE_PATH, GsonUtils.toJson(localCaptureBean));
    }

    private static boolean isPlaintext(Buffer buffer) {
        try {
            Buffer prefix = new Buffer();
            long byteCount = buffer.size() < 64 ? buffer.size() : 64;
            buffer.copyTo(prefix, 0, byteCount);
            for (int i = 0; i < 16; i++) {
                if (prefix.exhausted()) {
                    break;
                }
                int codePoint = prefix.readUtf8CodePoint();
                if (Character.isISOControl(codePoint) && !Character.isWhitespace(codePoint)) {
                    return false;
                }
            }
            return true;
        } catch (EOFException e) {
            return false; // Truncated UTF-8 sequence.
        }
    }

    interface CallBack {
        void onSuccess(CaptureBean captureBean);

        void fail(String message);
    }
}
