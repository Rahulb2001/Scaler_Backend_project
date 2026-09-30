package com.scaler.backend.productcatalog.dtos;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class SearchRequestDto {
    private String query = "";
    private int pageNumber = 0;
    private int pageSize = 10;
    private List<SortParam> sortParams = List.of();
}
