package com.melo.servlet;

import com.melo.j2ee.HttpServlet;
import com.melo.j2ee.HttpServletRequest;
import com.melo.j2ee.HttpServletResponse;

public class FristServlet extends HttpServlet {
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) {
        doPost(request,response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) {
        response.write("First servlet");
    }
}
