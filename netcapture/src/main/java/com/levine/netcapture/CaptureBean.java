package com.levine.netcapture;

import java.util.ArrayList;
import java.util.List;

/**
 * 标题：
 * 描述：
 * 作者：李启文
 * 创建时间：2020-06-10 18:24
 */
public class CaptureBean {

    private List<Data> data = new ArrayList<>();

    public List<Data> getData() {
        return data;
    }

    public void setData(List<Data> data) {
        this.data = data;
    }

    public static class Data {
        /**
         * request : {"url":"http://www.baidu.com","headers":[{"key":"","value":""}],"parameter":[{"key":"","value":""}]}
         * response : {"url":"http://www.baidu.com","headers":[{"key":"","value":""}],"body":""}
         */

        private RequestBean request = new RequestBean();
        private ResponseBean response = new ResponseBean();
        private boolean isExpansion = false;
        private int position = 0;

        public int getPosition() {
            return position;
        }

        public void setPosition(int position) {
            this.position = position;
        }

        public boolean isExpansion() {
            return isExpansion;
        }

        public void setExpansion(boolean expansion) {
            isExpansion = expansion;
        }

        public RequestBean getRequest() {
            return request;
        }

        public void setRequest(RequestBean request) {
            this.request = request;
        }

        public ResponseBean getResponse() {
            return response;
        }

        public void setResponse(ResponseBean response) {
            this.response = response;
        }

        public static class RequestBean {
            /**
             * url : http://www.baidu.com
             * headers : [{"key":"","value":""}]
             * parameter : [{"key":"","value":""}]
             */

            private String url;
            private String method;
            private String time;
            private String requestTag;
            private String raw = "";
            private String curl = "";

            public String getCurl() {
                return curl;
            }

            public void setCurl(String curl) {
                this.curl = curl;
            }

            public String getRaw() {
                return raw;
            }

            public void setRaw(String raw) {
                this.raw = raw;
            }

            public String getMethod() {
                return method;
            }

            public void setMethod(String method) {
                this.method = method;
            }

            private List<HeadersBean> headers = new ArrayList<>();
            private List<ParameterBean> parameter = new ArrayList<>();
            private List<UrlParameterBean> urlParameters = new ArrayList<>();

            public List<UrlParameterBean> getUrlParameters() {
                return urlParameters;
            }

            public void setUrlParameters(List<UrlParameterBean> urlParameters) {
                this.urlParameters = urlParameters;
            }

            public String getRequestTag() {
                return requestTag;
            }

            public void setRequestTag(String requestTag) {
                this.requestTag = requestTag;
            }

            public String getTime() {
                return time;
            }

            public void setTime(String time) {
                this.time = time;
            }

            public String getUrl() {
                return url;
            }

            public void setUrl(String url) {
                this.url = url;
            }

            public List<HeadersBean> getHeaders() {
                return headers;
            }

            public void setHeaders(List<HeadersBean> headers) {
                this.headers = headers;
            }

            public List<ParameterBean> getParameter() {
                return parameter;
            }

            public void setParameter(List<ParameterBean> parameter) {
                this.parameter = parameter;
            }

            public static class HeadersBean {
                /**
                 * key :
                 * value :
                 */

                private String key;
                private String value;

                public String getKey() {
                    return key;
                }

                public void setKey(String key) {
                    this.key = key;
                }

                public String getValue() {
                    return value;
                }

                public void setValue(String value) {
                    this.value = value;
                }
            }

            public static class ParameterBean {
                /**
                 * key :
                 * value :
                 */

                private String key;
                private String value;

                public String getKey() {
                    return key;
                }

                public void setKey(String key) {
                    this.key = key;
                }

                public String getValue() {
                    return value;
                }

                public void setValue(String value) {
                    this.value = value;
                }
            }

            public static class UrlParameterBean {
                /**
                 * key :
                 * value :
                 */

                private String key;
                private String value;

                public String getKey() {
                    return key;
                }

                public void setKey(String key) {
                    this.key = key;
                }

                public String getValue() {
                    return value;
                }

                public void setValue(String value) {
                    this.value = value;
                }
            }
        }

        public static class ResponseBean {
            /**
             * url : http://www.baidu.com
             * headers : [{"key":"","value":""}]
             * body :
             */

            private String url;
            private String time;
            private String body;
            private int status;

            public int getStatus() {
                return status;
            }

            public void setStatus(int status) {
                this.status = status;
            }

            private List<HeadersBeanX> headers = new ArrayList<>();

            public String getTime() {
                return time;
            }

            public void setTime(String time) {
                this.time = time;
            }

            public String getUrl() {
                return url;
            }

            public void setUrl(String url) {
                this.url = url;
            }

            public String getBody() {
                return body;
            }

            public void setBody(String body) {
                this.body = body;
            }

            public List<HeadersBeanX> getHeaders() {
                return headers;
            }

            public void setHeaders(List<HeadersBeanX> headers) {
                this.headers = headers;
            }

            public static class HeadersBeanX {
                /**
                 * key :
                 * value :
                 */

                private String key;
                private String value;

                public String getKey() {
                    return key;
                }

                public void setKey(String key) {
                    this.key = key;
                }

                public String getValue() {
                    return value;
                }

                public void setValue(String value) {
                    this.value = value;
                }
            }
        }
    }

}
