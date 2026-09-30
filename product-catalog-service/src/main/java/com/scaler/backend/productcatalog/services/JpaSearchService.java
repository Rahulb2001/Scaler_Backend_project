package com.scaler.backend.productcatalog.services;

import com.scaler.backend.productcatalog.dtos.SearchRequestDto;
import com.scaler.backend.productcatalog.dtos.SearchResponseDto;
import com.scaler.backend.productcatalog.dtos.SortParam;
import com.scaler.backend.productcatalog.models.Product;
import com.scaler.backend.productcatalog.repositories.ProductRepository;
import com.scaler.backend.productcatalog.utils.ProductDtoMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Set;

/**
 * Searches the products actually stored in our own database (see
 * StorageProductService) by name, with pagination and, unlike an earlier
 * version of this class, sorting that's actually driven by what the caller
 * asked for instead of a hardcoded "category then description" order.
 */
@Service
public class JpaSearchService implements ISearchService {

    private static final Set<String> SORTABLE_FIELDS = Set.of("name", "price");

    private final ProductRepository productRepository;

    public JpaSearchService(ProductRepository productRepository) {
        this.productRepository = productRepository;
    }

    @Override
    public SearchResponseDto search(SearchRequestDto request) {
        Sort sort = buildSort(request.getSortParams());
        PageRequest pageRequest = PageRequest.of(request.getPageNumber(), request.getPageSize(), sort);

        Page<Product> page = productRepository.findByNameContainingIgnoreCase(request.getQuery(), pageRequest);

        List<com.scaler.backend.productcatalog.dtos.ProductDto> products =
                page.getContent().stream().map(ProductDtoMapper::toDto).toList();

        return new SearchResponseDto(products, page.getNumber(), page.getTotalElements(), page.getTotalPages());
    }

    private Sort buildSort(List<SortParam> sortParams) {
        if (sortParams == null || sortParams.isEmpty()) {
            return Sort.by(Sort.Direction.ASC, "name");
        }

        List<Sort.Order> orders = sortParams.stream()
                .filter(param -> param.getField() != null && SORTABLE_FIELDS.contains(param.getField()))
                .map(param -> new Sort.Order(
                        param.getOrder() == com.scaler.backend.productcatalog.dtos.SortOrder.DESC
                                ? Sort.Direction.DESC
                                : Sort.Direction.ASC,
                        param.getField()
                ))
                .toList();

        return orders.isEmpty() ? Sort.by(Sort.Direction.ASC, "name") : Sort.by(orders);
    }
}
