package com.es.pharmacy.web.servlets;

import com.es.pharmacy.dao.ArrayListOrderDao;
import com.es.pharmacy.dao.OrderDao;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

public class OrderOverviewPageServlet extends HttpServlet {
    private OrderDao orderDao;
    private static final String ORDER_OVERVIEW_JSP="/WEB-INF/pages/orderOverview.jsp";

    @Override
    public void init(ServletConfig config) throws ServletException {
        super.init(config);
        orderDao = ArrayListOrderDao.getInstance();
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String secureOrderId=request.getPathInfo().substring(1);
        request.setAttribute("order", orderDao.getOrderBySecureId(secureOrderId));
        request.getRequestDispatcher(ORDER_OVERVIEW_JSP).forward(request, response);
    }
}

