package com.es.pharmacy.exceptions;

import com.es.pharmacy.model.product.Product;

public class OutOfStockException extends Exception {
    private Product product;
    private int stockRequested;
    private int stockAvailable;

    public OutOfStockException(Product product, int stockRequested, int stockAvailable) {
        this.product = product;
        this.stockRequested = stockRequested;
        this.stockAvailable = stockAvailable;
    }

    public Product getProduct() {
        return product;
    }

    public int getStockRequested() {
        return stockRequested;
    }

    public int getStockAvailable() {
        return stockAvailable;
    }
}
