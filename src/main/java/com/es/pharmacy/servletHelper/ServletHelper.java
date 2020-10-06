package com.es.pharmacy.servletHelper;

import com.es.pharmacy.dao.ProductDao;
import com.es.pharmacy.exceptions.ItemNotFoundException;
import com.es.pharmacy.exceptions.OutOfStockException;
import com.es.pharmacy.model.cart.CartService;
import com.es.pharmacy.model.enums.PaymentMethod;
import com.es.pharmacy.model.order.Order;
import com.es.pharmacy.model.product.Product;
import com.es.pharmacy.model.recentlyviewedproducts.RecentlyViewedProducts;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.text.NumberFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.function.Consumer;

public class ServletHelper {
    public static void setRequiredParameters(HttpServletRequest request, String parameter, Map<String, String> errors, Consumer<String> consumer) {
        String value = request.getParameter(parameter);
        if (value == null || value.isEmpty()) {
            errors.put(parameter, "Value is required");
        } else {
            consumer.accept(value);
        }
    }

    public static void setPaymentMethod(HttpServletRequest request, Map<String, String> errors, Order order) {
        String value = request.getParameter("paymentMethod");
        if (value == null || value.isEmpty()) {
            errors.put("paymentMethod", "Value is required");
        } else {
            order.setPaymentMethod(PaymentMethod.valueOf(value));
        }
    }

    public static boolean isNotEmpty(String parameter, Map<String, String> errorAttributes, String value) {
        if (value.isEmpty()) {
            errorAttributes.put(parameter, "Value is required");
            return false;
        }
        return true;
    }

    public static void setRequiredDateParameter(HttpServletRequest request,
                                                Map<String, String> errorAttributes, Order order) {
        String value = request.getParameter("deliveryDate");
        if (isNotEmpty("deliveryDate", errorAttributes, value)) {
            SimpleDateFormat format = new SimpleDateFormat("dd-MM-yyyy");
            Date dateValue = null;
            try {
                dateValue = format.parse(value);
            } catch (ParseException e) {
                errorAttributes.put("deliveryDate", "Wrong format, should be: dd-MM-yyyy");
            }
            order.setDeliveryDate(dateValue);
        }
    }

    public static void setAllParameters(HttpServletRequest request, Map<String, String> errors, Order order) {
        setRequiredParameters(request, "firstName", errors, order::setFirstName);
        setRequiredParameters(request, "lastName", errors, order::setFirstName);
        setRequiredParameters(request, "phone", errors, order::setFirstName);
        setRequiredDateParameter(request, errors, order);
        setRequiredParameters(request, "deliveryAddress", errors, order::setFirstName);
        setPaymentMethod(request, errors, order);
    }

    public static void setParametersForProductDetails(HttpServletRequest request, RecentlyViewedProducts recentlyViewedProducts, ProductDao productDao,
                                                      CartService cartService, Long productId) {
        request.setAttribute("recentProducts", recentlyViewedProducts.getRecentlyViewedProducts());
        request.setAttribute("product", productDao.get(productId));
        request.setAttribute("cart", cartService.getCart(request.getSession()));
    }

    public static Long parseProductId(HttpServletRequest request) {
        return Long.valueOf(request.getPathInfo().substring(1));
    }

    public static Long getProductIdIfExist(HttpServletRequest request, HttpServletResponse response, String productId) throws IOException {
        Long id = null;
        try {
            id = Long.valueOf(productId);
        } catch (NumberFormatException ex) {
            request.setAttribute("message", "Product " + productId + " not found");
            response.sendError(404);
        }
        return id;
    }

    public static Product getProductIfExist(HttpServletRequest request, HttpServletResponse response, ProductDao productDao) throws IOException {
        String productInfo = "";
        Product product = null;
        try {
            productInfo = request.getPathInfo().substring(1);
            Long id = Long.valueOf(productInfo);
            product = productDao.get(id);
        } catch (ItemNotFoundException | NumberFormatException ex) {
            request.setAttribute("message", "Product " + productInfo + " not found");
            response.sendError(404);
        }
        return product;
    }

    public static int getQuantity(String quantityString, HttpServletRequest request) throws ParseException {
        NumberFormat numberFormat = getNumberFormat(request.getLocale());
        return numberFormat.parse(quantityString).intValue();
    }

    public static NumberFormat getNumberFormat(Locale locale) {
        return NumberFormat.getInstance(locale);
    }

    public static Map<Long, String> mapErrors(Long productId, Exception e) {
        Map<Long, String> errorAttributes = new HashMap<>();
        if (e.getClass().equals(ParseException.class)) {
            errorAttributes.put(productId, "Not a number");
        } else {
            if (((OutOfStockException) e).getStockRequested() <= 0) {
                errorAttributes.put(productId, "Can't be negative or zero");
            } else {
                errorAttributes.put(productId, "Out of stock, max available " + ((OutOfStockException) e).getStockAvailable());
            }
        }
        return errorAttributes;
    }
}
