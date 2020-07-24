package com.melo.j2ee;

import java.io.InputStream;

public class HttpServletRequest {
    String url;
    String method;
    String version;

    public HttpServletRequest(InputStream inputStream) {
        try {
            //拿到HTTP协议内容
            String content = "";
            byte[] buff = new byte[1024];
            int len = 0;
            if ((len = inputStream.read(buff)) > 0) {
                content = new String(buff,0,len);
            }

            String line = content.split("\\n")[0];
            String [] arr = line.split("\\s");

            this.method = arr[0];
            this.url = arr[1].split("\\?")[0];
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getMethod() {
        return method;
    }

    public void setMethod(String method) {
        this.method = method;
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public String getUrl() {
        return url;
    }
}
