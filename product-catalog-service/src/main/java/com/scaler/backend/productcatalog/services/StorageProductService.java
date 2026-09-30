package com.scaler.backend.productcatalog.services;

import com.scaler.backend.productcatalog.dtos.ProductDto;
import com.scaler.backend.productcatalog.dtos.UserDto;
import com.scaler.backend.productcatalog.exceptions.ProductNotFoundException;
import com.scaler.backend.productcatalog.exceptions.UserNotFoundException;
import com.scaler.backend.productcatalog.models.Category;
import com.scaler.backend.productcatalog.models.Product;
import com.scaler.backend.productcatalog.repositories.CategoryRepository;
import com.scaler.backend.productcatalog.repositories.ProductRepository;
import com.scaler.backend.productcatalog.utils.ProductDtoMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import java.util.List;

/**
 * The "real" catalog implementation - actual rows in our own database
 * rather than a proxy to fakestoreapi.com. Not @Primary, so it has to be
 * asked for by concrete type; ProductController does that specifically for
 * the user-scoped lookup below, since that's a feature FakeStoreProductService
 * has no reasonable way to support (fakestoreapi has no concept of our users).
 */
@Service
public class StorageProductService implements IProductService {

    private final ProductRepository productRepository;
    private final CategoryRepository categoryRepository;
    private final RestTemplate loadBalancedRestTemplate;

    public StorageProductService(ProductRepository productRepository,
                                  CategoryRepository categoryRepository,
                                  RestTemplate loadBalancedRestTemplate) {
        this.productRepository = productRepository;
        this.categoryRepository = categoryRepository;
        this.loadBalancedRestTemplate = loadBalancedRestTemplate;
    }

    @Override
    public ProductDto getProductById(Long id) {
        return ProductDtoMapper.toDto(findProductOrThrow(id));
    }

    @Override
    public List<ProductDto> getAllProducts() {
        return productRepository.findAll().stream().map(ProductDtoMapper::toDto).toList();
    }

    @Override
    @Transactional
    public ProductDto createProduct(ProductDto productDto) {
        Product product = new Product();
        ProductDtoMapper.copyToEntity(productDto, product, resolveCategory(productDto.getCategory()));
        return ProductDtoMapper.toDto(productRepository.save(product));
    }

    @Override
    @Transactional
    public ProductDto updateProduct(Long id, ProductDto productDto) {
        Product product = findProductOrThrow(id);
        ProductDtoMapper.copyToEntity(productDto, product, resolveCategory(productDto.getCategory()));
        return ProductDtoMapper.toDto(productRepository.save(product));
    }

    @Override
    @Transactional
    public ProductDto deleteProduct(Long id) {
        Product product = findProductOrThrow(id);
        productRepository.delete(product);
        return ProductDtoMapper.toDto(product);
    }

    /**
     * Looks the requesting user up in user-authentication-service (via
     * Eureka + client-side load balancing, hence "http://userservice/..."
     * rather than a hardcoded host:port) and tailors the response to their
     * role: admins see the full record including the platform's commission
     * on the item, everyone else gets the customer-facing view.
     */
    public ProductDto getProductForUser(Long productId, Long userId) {
        Product product = findProductOrThrow(productId);
        UserDto user = fetchUserOrThrow(userId);

        ProductDto dto = ProductDtoMapper.toDto(product);
        boolean isAdmin = user.getRoles() != null && user.getRoles().contains("ADMIN");
        if (!isAdmin) {
            dto.setCommission(0);
        }
        return dto;
    }

    private UserDto fetchUserOrThrow(Long userId) {
        try {
            UserDto user = loadBalancedRestTemplate.getForObject("http://userservice/users/{id}", UserDto.class, userId);
            if (user == null) {
                throw new UserNotFoundException("No user found with id " + userId);
            }
            return user;
        } catch (RestClientException e) {
            throw new UserNotFoundException("Could not look up user " + userId + ": " + e.getMessage());
        }
    }

    private Product findProductOrThrow(Long id) {
        return productRepository.findById(id)
                .orElseThrow(() -> new ProductNotFoundException("No product found with id " + id));
    }

    private Category resolveCategory(String categoryName) {
        if (categoryName == null || categoryName.isBlank()) {
            return null;
        }
        return categoryRepository.findByName(categoryName)
                .orElseGet(() -> {
                    Category category = new Category();
                    category.setName(categoryName);
                    return categoryRepository.save(category);
                });
    }
}
