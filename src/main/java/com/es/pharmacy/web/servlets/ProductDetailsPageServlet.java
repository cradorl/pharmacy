package com.es.pharmacy.web.servlets;

import com.es.pharmacy.dao.ArrayListProductDao;
import com.es.pharmacy.dao.ProductDao;
import com.es.pharmacy.exceptions.OutOfStockException;
import com.es.pharmacy.model.cart.CartService;
import com.es.pharmacy.model.cart.DefaultCartService;
import com.es.pharmacy.model.recentlyviewedproducts.RecentlyViewedProducts;
import com.es.pharmacy.model.recentlyviewedproducts.service.DefaultRecentlyViewedProductsService;
import com.es.pharmacy.model.recentlyviewedproducts.service.RecentlyViewedProductsService;
import com.es.pharmacy.servletHelper.ServletHelper;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.text.ParseException;
import java.util.NoSuchElementException;

import static com.es.pharmacy.servletHelper.ServletHelper.parseProductId;
import static com.es.pharmacy.servletHelper.ServletHelper.setParametersForProductDetails;

public class ProductDetailsPageServlet extends HttpServlet {

    private ProductDao productDao;
    private CartService cartService;
    private RecentlyViewedProductsService recentlyViewedProductsService;
    private static final String PRODUCT_DETAILS_JSP = "/WEB-INF/pages/productDetails.jsp";

    @Override
    public void init(ServletConfig config) throws ServletException {
        super.init(config);
        productDao = ArrayListProductDao.getInstance();
        cartService = DefaultCartService.getInstance();
        recentlyViewedProductsService = DefaultRecentlyViewedProductsService.getInstance();
    }

    @Override
    public void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        try {
            Long productId = parseProductId(request);
            RecentlyViewedProducts recentlyViewedProducts =
                    recentlyViewedProductsService.getRecentlyViewedProducts(request);
            recentlyViewedProductsService.addProduct(productId, recentlyViewedProducts);
            ServletHelper.setParametersForProductDetails(request, recentlyViewedProducts, productDao, cartService, productId);
            request.getRequestDispatcher(PRODUCT_DETAILS_JSP).forward(request, response);
        } catch (NoSuchElementException | NumberFormatException e) {
            response.sendError(404);
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
        Long productId = ServletHelper.getProductIfExist(request, response, productDao).getId();
        try {
            int quantity = ServletHelper.getQuantity(request.getParameter("quantity"), request);
            cartService.add(cartService.getCart(request.getSession()), productId, quantity);
        } catch (ParseException | OutOfStockException e) {
            handleError(request, response, e);
            return;
        }
        response.sendRedirect(request.getContextPath() + "/products?message=Product " + productId + " added to cart");
    }

    private void handleError(HttpServletRequest request, HttpServletResponse response, Exception e) throws ServletException, IOException {
        if (e.getClass().equals(ParseException.class)) {
            request.setAttribute("error", "Not a number");
        } else {
            if (((OutOfStockException) e).getStockRequested() <= 0) {
                request.setAttribute("error", "Can't be negative or zero");
            } else {
                request.setAttribute("error", "Out of stock, max available " + ((OutOfStockException) e).getStockAvailable());
            }
        }
        doGet(request, response);
    }
}

