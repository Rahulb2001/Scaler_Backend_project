package com.scaler.backend.productcatalog.models;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
public class Product extends BaseModel {

    @Column(nullable = false)
    private String name;

    @Column(length = 2000)
    private String description;

    private String imageUrl;

    @Column(nullable = false)
    private double price;

    // What cut the platform takes on this product, as a fraction (0.10 = 10%).
    // Only ever surfaced to admins - see ProductController's user-scoped lookup.
    private double commission;

    private boolean prime;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "category_id")
    private Category category;
}
