package com.scaler.backend.productcatalog.services;

import com.scaler.backend.productcatalog.dtos.SearchRequestDto;
import com.scaler.backend.productcatalog.dtos.SearchResponseDto;

public interface ISearchService {
    SearchResponseDto search(SearchRequestDto request);
}
