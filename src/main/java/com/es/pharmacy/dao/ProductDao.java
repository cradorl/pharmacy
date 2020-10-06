package com.es.pharmacy.dao;

import com.es.pharmacy.model.enums.SortField;
import com.es.pharmacy.model.enums.SortOrder;
import com.es.pharmacy.model.product.Product;

import java.math.BigDecimal;
import java.util.List;

public interface ProductDao extends GenericDao<Product> {
     List<Product> findProducts(String query, SortField sortField, SortOrder sortOrder);
    List<Product> advancedFindProduct(String productCode, BigDecimal minPrice, BigDecimal maxPrice, int minStock);
    BigDecimal getMaxProductPrice();
}
