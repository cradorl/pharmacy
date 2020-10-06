package com.es.pharmacy.dao;

import com.es.pharmacy.exceptions.ItemNotFoundException;
import com.es.pharmacy.model.order.Order;

public interface OrderDao extends GenericDao<Order> {
    Order getOrderBySecureId(String secureId) throws ItemNotFoundException;
}
