package com.melo.bio;

import com.melo.j2ee.HttpServlet;
import com.melo.j2ee.HttpServletRequest;
import com.melo.j2ee.HttpServletResponse;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

public class Server {
    static int defaultPort = 8080;
    static Map<String, HttpServlet> servletMapping = new HashMap<>();
    Properties webxml = new Properties();
    private  void loadMapping(){
        System.out.println("tomcat loading mapping");
        //加载web.xml文件,同时初始化 ServletMapping对象
        try{
            String WEB_INF = this.getClass().getResource("/").getPath();
            FileInputStream fis = new FileInputStream(WEB_INF + "web.properties");


            webxml.load(fis);

            for (Object k : webxml.keySet()) {

                String key = k.toString();
                if(key.endsWith(".url")){
                    String servletName = key.replaceAll("\\.url$", "");
                    String url = webxml.getProperty(key);
                    String className = webxml.getProperty(servletName + ".className");
                    //单实例，多线程
                    HttpServlet obj = (HttpServlet)Class.forName(className).newInstance();
                    servletMapping.put(url, obj);
                }

            }


        }catch(Exception e){
            e.printStackTrace();
        }


    }
    /**
     * 启动tomcat
     */
    public  void start() throws IOException {
        loadMapping();
        ServerSocket serverSocket = new ServerSocket(defaultPort);
        while (true){
            Socket socket = serverSocket.accept();
            process(socket);
        }
    }

    public void process(Socket client) throws IOException {
        InputStream is = client.getInputStream();
        OutputStream os = client.getOutputStream();
        HttpServletRequest request = new HttpServletRequest(is);
        HttpServletResponse response = new HttpServletResponse(os);
        String url = request.getUrl();
        System.out.println(url);
        if(!servletMapping.containsKey(url)){
            response.write("404 - Not Found");
            return;
        }
        /**
         * 调用servlet 处理请求
         */
        servletMapping.get(url).service(request,response);
        os.flush();
        os.close();

        is.close();
        client.close();
    }
}
