package com.areaShop.service;

import com.areaShop.model.Product;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;

public interface ProductService {

    Optional<Product> findById(Long id);

    Page<Product> findAllProductsPageable(Pageable pageable);

    List<Product> findAll();

    Product save(Product product);

    void delete(Product product);
}

