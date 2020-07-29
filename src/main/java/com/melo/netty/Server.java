package com.melo.netty;

import com.melo.netty.j2ee.HttpServlet;
import com.melo.netty.j2ee.HttpServletRequest;
import com.melo.netty.j2ee.HttpServletResponse;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.http.HttpRequest;
import io.netty.handler.codec.http.HttpRequestDecoder;
import io.netty.handler.codec.http.HttpRequestEncoder;
import io.netty.handler.codec.http.HttpResponseEncoder;

import java.io.FileInputStream;
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
                    Class cls = Class.forName(className);
                    if(HttpServlet.class.isAssignableFrom(cls)) {
                        //单实例，多线程
                        HttpServlet obj = (HttpServlet) cls.newInstance();
                        servletMapping.put(url, obj);
                    }
                }

            }


        }catch(Exception e){
            e.printStackTrace();
        }


    }


    public void start() throws InterruptedException {
        loadMapping();

        EventLoopGroup bossGroup = new NioEventLoopGroup();
        EventLoopGroup workerGroup = new NioEventLoopGroup();
        try {
            ServerBootstrap bootstrap = new ServerBootstrap();
            bootstrap.group(bossGroup, workerGroup)
                    .channel(NioServerSocketChannel.class)
                    .childHandler(new ChannelInitializer<SocketChannel>() {
                        @Override
                        protected void initChannel(SocketChannel socketChannel) throws Exception {
                            socketChannel.pipeline().addLast(new HttpResponseEncoder());

                            socketChannel.pipeline().addLast(new HttpRequestDecoder());

                            socketChannel.pipeline().addLast(new TomcatHandler());

                        }
                    })
                    // 针对主线程的配置 分配线程最大数量 128
                    .option(ChannelOption.SO_BACKLOG, 128)
                    // 针对子线程的配置 保持长连接
                    .childOption(ChannelOption.SO_KEEPALIVE, true);
            ChannelFuture channelFuture = bootstrap.bind(defaultPort).sync();
            System.out.println("Server start successful");
            channelFuture.channel().closeFuture().sync();
        }finally {
            bossGroup.shutdownGracefully();
            workerGroup.shutdownGracefully();
        }
    }

    public class TomcatHandler extends ChannelInboundHandlerAdapter {

        @Override
        public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
            if(msg instanceof HttpRequest){
                HttpRequest req = (HttpRequest) msg;
                HttpServletRequest request = new HttpServletRequest(ctx, req);
                HttpServletResponse response = new HttpServletResponse(ctx,req);

                String url = request.getUrl();
                if(servletMapping.containsKey(url)){
                    servletMapping.get(url).service(request,response);
                } else {
                    response.write("404 Not Found Url "+ req.getUri());
                }
            }
        }
    }
}
