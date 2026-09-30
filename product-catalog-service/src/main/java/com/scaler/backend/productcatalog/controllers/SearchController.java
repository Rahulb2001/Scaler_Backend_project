package com.scaler.backend.productcatalog.controllers;

import com.scaler.backend.productcatalog.dtos.SearchRequestDto;
import com.scaler.backend.productcatalog.dtos.SearchResponseDto;
import com.scaler.backend.productcatalog.services.ISearchService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/search")
public class SearchController {

    private final ISearchService searchService;

    public SearchController(ISearchService searchService) {
        this.searchService = searchService;
    }

    @PostMapping
    public ResponseEntity<SearchResponseDto> search(@RequestBody SearchRequestDto request) {
        return ResponseEntity.ok(searchService.search(request));
    }
}
