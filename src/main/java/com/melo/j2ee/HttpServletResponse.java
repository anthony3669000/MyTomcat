package com.melo.j2ee;

import java.io.IOException;
import java.io.OutputStream;
import java.rmi.server.ExportException;

public class HttpServletResponse {
    OutputStream os;
    public HttpServletResponse(OutputStream os) {
        this.os = os;
    }

    public void write(String s)  {
        try {
            StringBuilder sb = new StringBuilder();
            sb.append("HTTP/1.1 200 OK\n")
                    .append("Content-Type: text/html;\n")
                    .append("\r\n")
                    .append(s);
            os.write(sb.toString().getBytes());
        }catch (Exception e){
            e.printStackTrace();
        }
    }
}
