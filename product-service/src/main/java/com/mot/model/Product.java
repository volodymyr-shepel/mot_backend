package com.mot.model;

import jakarta.persistence.*;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

@Entity
public class Product {
    @Id
    @GeneratedValue
    private UUID id;

    @ManyToOne
    @JoinColumn(name = "category_id")
    private Category category;

    private String name;

    private String description;

    @OneToMany(mappedBy = "product", cascade = CascadeType.ALL)
    private Set<ProductItem> productItemList = new HashSet<>();
}
