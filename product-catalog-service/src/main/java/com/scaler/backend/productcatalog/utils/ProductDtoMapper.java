package com.scaler.backend.productcatalog.utils;

import com.scaler.backend.productcatalog.dtos.ProductDto;
import com.scaler.backend.productcatalog.models.Category;
import com.scaler.backend.productcatalog.models.Product;

public class ProductDtoMapper {

    private ProductDtoMapper() {
    }

    public static ProductDto toDto(Product product) {
        ProductDto dto = new ProductDto();
        dto.setId(product.getId());
        dto.setName(product.getName());
        dto.setDescription(product.getDescription());
        dto.setImageUrl(product.getImageUrl());
        dto.setPrice(product.getPrice());
        dto.setCommission(product.getCommission());
        dto.setPrime(product.isPrime());
        dto.setCategory(product.getCategory() != null ? product.getCategory().getName() : null);
        return dto;
    }

    public static void copyToEntity(ProductDto dto, Product product, Category category) {
        product.setName(dto.getName());
        product.setDescription(dto.getDescription());
        product.setImageUrl(dto.getImageUrl());
        product.setPrice(dto.getPrice());
        product.setCommission(dto.getCommission());
        product.setPrime(dto.isPrime());
        product.setCategory(category);
    }
}
