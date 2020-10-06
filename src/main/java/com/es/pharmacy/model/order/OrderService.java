package com.es.pharmacy.model.order;

import com.es.pharmacy.model.cart.Cart;
import com.es.pharmacy.model.enums.PaymentMethod;

import java.util.List;

public interface OrderService {
    Order getOrder(Cart cart);

    List<PaymentMethod> getPaymentMethods();

    void placeOrder(Order order);
}
