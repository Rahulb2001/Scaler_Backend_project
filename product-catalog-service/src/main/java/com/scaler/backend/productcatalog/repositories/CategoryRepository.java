package com.scaler.backend.productcatalog.repositories;

import com.scaler.backend.productcatalog.models.Category;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface CategoryRepository extends JpaRepository<Category, Long> {
    Optional<Category> findByName(String name);
}
