package com.scaler.backend.productcatalog.dtos;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
public class SearchResponseDto {
    private List<ProductDto> products;
    private int pageNumber;
    private long totalElements;
    private int totalPages;
}
