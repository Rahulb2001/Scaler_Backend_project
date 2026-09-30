package com.scaler.backend.productcatalog.services;

import com.scaler.backend.productcatalog.dtos.ProductDto;

import java.util.List;

public interface IProductService {

    ProductDto getProductById(Long id);

    List<ProductDto> getAllProducts();

    ProductDto createProduct(ProductDto productDto);

    ProductDto updateProduct(Long id, ProductDto productDto);

    ProductDto deleteProduct(Long id);
}
