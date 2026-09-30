package com.scaler.backend.productcatalog.controllers;

import com.scaler.backend.productcatalog.dtos.ProductDto;
import com.scaler.backend.productcatalog.services.IProductService;
import com.scaler.backend.productcatalog.services.StorageProductService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/products")
public class ProductController {

    private final IProductService productService;
    private final StorageProductService storageProductService;

    public ProductController(IProductService productService, StorageProductService storageProductService) {
        this.productService = productService;
        this.storageProductService = storageProductService;
    }

    @GetMapping
    public ResponseEntity<List<ProductDto>> getAllProducts() {
        return ResponseEntity.ok(productService.getAllProducts());
    }

    @GetMapping("/{id}")
    public ResponseEntity<ProductDto> getProductById(@PathVariable Long id) {
        if (id <= 0) {
            throw new IllegalArgumentException("Product id must be a positive number");
        }
        return ResponseEntity.ok(productService.getProductById(id));
    }

    /**
     * Deliberately bypasses the @Primary FakeStoreProductService: this view
     * only makes sense for products we actually own in our database, scoped
     * by who's asking (see StorageProductService.getProductForUser).
     */
    @GetMapping("/{productId}/users/{userId}")
    public ResponseEntity<ProductDto> getProductForUser(@PathVariable Long productId, @PathVariable Long userId) {
        return ResponseEntity.ok(storageProductService.getProductForUser(productId, userId));
    }

    @PostMapping
    public ResponseEntity<ProductDto> createProduct(@RequestBody ProductDto productDto) {
        return new ResponseEntity<>(productService.createProduct(productDto), HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    public ResponseEntity<ProductDto> updateProduct(@PathVariable Long id, @RequestBody ProductDto productDto) {
        return ResponseEntity.ok(productService.updateProduct(id, productDto));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ProductDto> deleteProduct(@PathVariable Long id) {
        return ResponseEntity.ok(productService.deleteProduct(id));
    }
}
