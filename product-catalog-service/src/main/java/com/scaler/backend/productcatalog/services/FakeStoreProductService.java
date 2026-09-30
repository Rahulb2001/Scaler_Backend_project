package com.scaler.backend.productcatalog.services;

import com.scaler.backend.productcatalog.dtos.FakeStoreProductDto;
import com.scaler.backend.productcatalog.dtos.ProductDto;
import com.scaler.backend.productcatalog.exceptions.ProductNotFoundException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.context.annotation.Primary;
import org.springframework.data.redis.core.HashOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import java.util.Arrays;
import java.util.List;

/**
 * Backs the product catalog with the public fakestoreapi.com instead of our
 * own database - handy for demos/interviews where standing up real product
 * data isn't the point. Responses are cached in Redis so we're not hitting
 * a third party on every request. This is the @Primary implementation of
 * IProductService; StorageProductService is the "real" JPA-backed one.
 */
@Primary
@Service
public class FakeStoreProductService implements IProductService {

    private static final String CACHE_KEY = "PRODUCTS";

    private final RestTemplate restTemplate;
    private final HashOperations<String, String, ProductDto> productCache;
    private final String baseUrl;

    public FakeStoreProductService(RestTemplateBuilder restTemplateBuilder,
                                    RedisTemplate<String, ProductDto> productRedisTemplate,
                                    @Value("${productcatalog.fakestore.base-url:https://fakestoreapi.com/products}") String baseUrl) {
        this.restTemplate = restTemplateBuilder.build();
        this.productCache = productRedisTemplate.opsForHash();
        this.baseUrl = baseUrl;
    }

    @Override
    public ProductDto getProductById(Long id) {
        ProductDto cached = productCache.get(CACHE_KEY, id.toString());
        if (cached != null) {
            return cached;
        }

        FakeStoreProductDto fakeStoreProduct = fetchOrThrow(id);
        ProductDto productDto = fakeStoreProduct.toProductDto();
        productCache.put(CACHE_KEY, id.toString(), productDto);
        return productDto;
    }

    @Override
    public List<ProductDto> getAllProducts() {
        FakeStoreProductDto[] products = restTemplate.getForObject(baseUrl, FakeStoreProductDto[].class);
        if (products == null) {
            return List.of();
        }
        return Arrays.stream(products).map(FakeStoreProductDto::toProductDto).toList();
    }

    @Override
    public ProductDto createProduct(ProductDto productDto) {
        FakeStoreProductDto requestBody = FakeStoreProductDto.fromProductDto(productDto);
        FakeStoreProductDto created = restTemplate.postForObject(baseUrl, requestBody, FakeStoreProductDto.class);
        if (created == null) {
            throw new ProductNotFoundException("fakestoreapi did not return the created product");
        }
        ProductDto createdDto = created.toProductDto();
        productCache.put(CACHE_KEY, createdDto.getId().toString(), createdDto);
        return createdDto;
    }

    @Override
    public ProductDto updateProduct(Long id, ProductDto productDto) {
        fetchOrThrow(id);

        FakeStoreProductDto requestBody = FakeStoreProductDto.fromProductDto(productDto);
        ResponseEntity<FakeStoreProductDto> response = restTemplate.exchange(
                baseUrl + "/{id}",
                HttpMethod.PUT,
                new HttpEntity<>(requestBody),
                FakeStoreProductDto.class,
                id
        );

        FakeStoreProductDto updated = response.getBody();
        if (updated == null) {
            throw new ProductNotFoundException("fakestoreapi did not return the updated product " + id);
        }
        updated.setId(id);
        ProductDto updatedDto = updated.toProductDto();
        productCache.put(CACHE_KEY, id.toString(), updatedDto);
        return updatedDto;
    }

    @Override
    public ProductDto deleteProduct(Long id) {
        ProductDto existing = getProductById(id);
        restTemplate.delete(baseUrl + "/{id}", id);
        productCache.delete(CACHE_KEY, id.toString());
        return existing;
    }

    private FakeStoreProductDto fetchOrThrow(Long id) {
        try {
            FakeStoreProductDto product = restTemplate.getForObject(baseUrl + "/{id}", FakeStoreProductDto.class, id);
            if (product == null || product.getId() == null) {
                throw new ProductNotFoundException("No product found with id " + id);
            }
            return product;
        } catch (HttpClientErrorException.NotFound e) {
            throw new ProductNotFoundException("No product found with id " + id);
        }
    }
}
