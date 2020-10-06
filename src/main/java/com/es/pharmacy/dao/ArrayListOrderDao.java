package com.es.pharmacy.dao;

import com.es.pharmacy.exceptions.ItemNotFoundException;
import com.es.pharmacy.model.order.Order;

import java.util.concurrent.locks.Lock;


public class ArrayListOrderDao  extends AbstractGenericDAO<Order> implements OrderDao {

    private static class SingletonHelper {
        private static final ArrayListOrderDao INSTANCE = new ArrayListOrderDao();
    }

    public static ArrayListOrderDao getInstance() {
        return SingletonHelper.INSTANCE;
    }


    @Override
    public Order getOrderBySecureId(String secureId) throws ItemNotFoundException {
        Lock readLock = rwLock.readLock();
        readLock.lock();
        try {
            return items.stream()
                    .filter((item) -> secureId.equals(item.getSecureId()))
                    .findAny()
                    .orElseThrow(ItemNotFoundException::new);
        } finally {
            readLock.unlock();
        }
    }
}
