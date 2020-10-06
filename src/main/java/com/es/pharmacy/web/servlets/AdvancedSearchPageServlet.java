package com.es.pharmacy.web.servlets;

import com.es.pharmacy.dao.ArrayListProductDao;
import com.es.pharmacy.dao.ProductDao;
import com.es.pharmacy.model.product.Product;
import org.apache.commons.lang3.StringUtils;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.math.BigDecimal;
import java.text.NumberFormat;
import java.text.ParseException;
import java.util.*;

public class AdvancedSearchPageServlet extends HttpServlet {

    private ProductDao productDao;
    private static final String SEARCH_JSP = "/WEB-INF/pages/search.jsp";
    private String minPossibleOrStock = "0";
    private String maxPossiblePrice;


    @Override
    public void init(ServletConfig config) throws ServletException {
        super.init(config);
        productDao = ArrayListProductDao.getInstance();
        maxPossiblePrice = productDao.getMaxProductPrice().toString();
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        request.setAttribute("products", new ArrayList<>());
        request.getRequestDispatcher(SEARCH_JSP).forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
        Map<String, String> errors = new HashMap<>();

        BigDecimal minPrice = getPrice(request, "minPrice", errors, StringUtils.isBlank(request.getParameter("minPrice")) ? minPossibleOrStock : request.getParameter("minPrice"));
        BigDecimal maxPrice = getPrice(request, "maxPrice", errors, StringUtils.isBlank(request.getParameter("maxPrice")) ? maxPossiblePrice : request.getParameter("maxPrice"));
        int minStock = getStock(request, "minStock", errors, StringUtils.isBlank(request.getParameter("minStock")) ? minPossibleOrStock : request.getParameter("minStock"));

        if (!errors.isEmpty()) {
            request.setAttribute("errors", errors);
            request.setAttribute("message", "Error occurred while filling fields");
            doGet(request, response);
        }
        List<Product> foundProducts = productDao.advancedFindProduct(request.getParameter("productCode"), minPrice, maxPrice, minStock);
        request.setAttribute("products", foundProducts);
        request.setAttribute("message", "Found" + foundProducts.size() + "products");
        request.getRequestDispatcher(SEARCH_JSP).forward(request, response);
    }

    private BigDecimal getPrice(HttpServletRequest request, String requestParameter, Map<String, String> errors, String parameter) {
        try {
            return BigDecimal.valueOf(getPriceParse(parameter, request));
        } catch (ParseException e) {
            errors.put(requestParameter, "Not a number");
        } catch (IllegalArgumentException e) {
            errors.put(requestParameter, "Can't be negative");
        }
        return null;
    }

    private int getStock(HttpServletRequest request, String requestParameter, Map<String, String> errors, String parameter) {
        try {
            return getStockParse(parameter, request);
        } catch (ParseException e) {
            errors.put(requestParameter, "Not a number");
        } catch (IllegalArgumentException e) {
            errors.put(requestParameter, "Can't be negative");
        }
        return 0;
    }

    private int getStockParse(String quantityString, HttpServletRequest request) throws ParseException {
        NumberFormat numberFormat = getNumberFormat(request.getLocale());
        int stock = numberFormat.parse(quantityString).intValue();
        if (stock < 0) {
            throw new IllegalArgumentException();
        }
        return stock;
    }

    private double getPriceParse(String quantityString, HttpServletRequest request) throws ParseException {
        NumberFormat numberFormat = getNumberFormat(request.getLocale());
        double price = numberFormat.parse(quantityString).doubleValue();
        if (price < 0) {
            throw new IllegalArgumentException();
        }
        return price;
    }

    protected NumberFormat getNumberFormat(Locale locale) {
        return NumberFormat.getInstance(locale);
    }
}
