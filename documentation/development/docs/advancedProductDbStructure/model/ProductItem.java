package com.mot.model;

import jakarta.persistence.*;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;

@Entity
public class ProductItem {
    @Id
    @GeneratedValue
    private UUID Id;

    @ManyToOne
    @JoinColumn(name = "product_id")
    private Product product;

    @OneToMany(mappedBy = "productItem")
    private Set<ProductConfiguration> configurations = new HashSet<>();


    private String SKU;

    private Integer QtyInStock;

    private String imagePath;

    private Float price;
}
