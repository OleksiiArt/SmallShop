package com.areaShop.service.impl;

import com.areaShop.exception.NotEnoughProductsInStockException;
import com.areaShop.model.Product;
import com.areaShop.model.dto.ProductDto;
import com.areaShop.repository.ProductRepository;
import com.areaShop.service.ShoppingCartService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.context.annotation.ScopedProxyMode;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.context.WebApplicationContext;

import java.math.BigDecimal;
import java.util.*;

@Service
@Scope(value = WebApplicationContext.SCOPE_SESSION, proxyMode = ScopedProxyMode.TARGET_CLASS)
@Transactional
public class ShoppingCartServiceImpl implements ShoppingCartService {

    private final ProductRepository productRepository;

    List<ProductDto> productDtoList;
    private Map<Product, Integer> products = new HashMap<>();

    @Autowired
    public ShoppingCartServiceImpl(ProductRepository productRepository) {
        this.productRepository = productRepository;
    }


    @Override
    public void addProduct(Product product) {
        if (products.containsKey(product)) {
            products.replace(product, products.get(product) + 1);
        } else {
            products.put(product, 1);
        }
    }


    @Override
    public void removeProduct(Product product) {
        if (products.containsKey(product)) {
            if (products.get(product) > 1)
                products.replace(product, products.get(product) - 1);
            else if (products.get(product) == 1) {
                products.remove(product);
            }
        }
    }


    @Override
    public Map<Product, Integer> getProductsInCart() {
        return Collections.unmodifiableMap(products);
    }


    @Override
    public void checkout() throws NotEnoughProductsInStockException {
        Product product;
        productDtoList = new ArrayList<>();
        for (Map.Entry<Product, Integer> entry : products.entrySet()) {
            ProductDto productDto = new ProductDto();
            // Refresh quantity for every product before checking
            product = productRepository.findOne(entry.getKey().getId());
            if (product.getQuantity() < entry.getValue())
                throw new NotEnoughProductsInStockException(product);
            entry.getKey().setQuantity(product.getQuantity() - entry.getValue());
            productDto.setName(entry.getKey().getName());
            productDto.setDescription(entry.getKey().getDescription());
            productDto.setPrice(entry.getKey().getPrice());
            productDto.setQuantity(entry.getValue());
            productDto.setItemTotal(productDto.getPrice().multiply(BigDecimal.valueOf(productDto.getQuantity())));
            productDtoList.add(productDto);
        }
        productRepository.save(products.keySet());
        productRepository.flush();
        products.clear();
    }

    @Override
    public BigDecimal getTotal() {
        return products.entrySet().stream()
                .map(entry -> entry.getKey().getPrice().multiply(BigDecimal.valueOf(entry.getValue())))
                .reduce(BigDecimal::add)
                .orElse(BigDecimal.ZERO);
    }

    @Override
    public List<ProductDto> getProductList() {
        return productDtoList;
    }
}
