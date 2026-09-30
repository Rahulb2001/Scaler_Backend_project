package com.scaler.backend.productcatalog.dtos;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Getter;
import lombok.Setter;

/**
 * Shape of the JSON returned by https://fakestoreapi.com/products - kept
 * separate from ProductDto since the two are never going to stay identical
 * (fakestoreapi has its own "title"/"image"/"rating" fields), and mixing
 * them would leak an external API's shape into the rest of this service.
 */
@Getter
@Setter
@JsonIgnoreProperties(ignoreUnknown = true)
public class FakeStoreProductDto {
    private Long id;
    private String title;
    private double price;
    private String description;
    private String category;
    private String image;

    public ProductDto toProductDto() {
        ProductDto dto = new ProductDto();
        dto.setId(this.id);
        dto.setName(this.title);
        dto.setDescription(this.description);
        dto.setImageUrl(this.image);
        dto.setPrice(this.price);
        dto.setCategory(this.category);
        return dto;
    }

    public static FakeStoreProductDto fromProductDto(ProductDto productDto) {
        FakeStoreProductDto dto = new FakeStoreProductDto();
        dto.setId(productDto.getId());
        dto.setTitle(productDto.getName());
        dto.setDescription(productDto.getDescription());
        dto.setImage(productDto.getImageUrl());
        dto.setPrice(productDto.getPrice());
        dto.setCategory(productDto.getCategory());
        return dto;
    }
}
