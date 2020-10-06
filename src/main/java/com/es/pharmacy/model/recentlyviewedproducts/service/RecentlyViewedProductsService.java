package com.es.pharmacy.model.recentlyviewedproducts.service;

import com.es.pharmacy.model.recentlyviewedproducts.RecentlyViewedProducts;

import javax.servlet.http.HttpServletRequest;

public interface RecentlyViewedProductsService {
    RecentlyViewedProducts getRecentlyViewedProducts(HttpServletRequest request);

    void addProduct(Long productId, RecentlyViewedProducts recentlyViewedProducts);
}
