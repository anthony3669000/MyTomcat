package com.melo.netty.j2ee;


public abstract class HttpServlet {

    public void service(HttpServletRequest request, HttpServletResponse response) {
        if("GET".equalsIgnoreCase(request.getMethod())){
            doGet(request,response);
        }else{
            doPost(request,response);
        }
    }

    protected abstract void doGet(HttpServletRequest request, HttpServletResponse response);

    protected abstract void doPost(HttpServletRequest request, HttpServletResponse response);
}
