package com.scaler.backend.productcatalog.dtos;

import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

@Getter
@Setter
public class ProductDto implements Serializable {
    private Long id;
    private String name;
    private String description;
    private String imageUrl;
    private double price;
    private String category;
    private double commission;
    private boolean prime;
}
