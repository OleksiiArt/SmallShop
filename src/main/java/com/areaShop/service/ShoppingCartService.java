package com.areaShop.service;

import com.areaShop.exception.NotEnoughProductsInStockException;
import com.areaShop.model.Product;
import com.areaShop.model.dto.ProductDto;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

public interface ShoppingCartService {

    void addProduct(Product product);

    void removeProduct(Product product);

    Map<Product, Integer> getProductsInCart();

    void checkout() throws NotEnoughProductsInStockException ;

    BigDecimal getTotal();

    List<ProductDto> getProductList();
}

