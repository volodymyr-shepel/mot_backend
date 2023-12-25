package com.mot.model;

import jakarta.persistence.*;

import java.util.List;
import java.util.UUID;

@Entity
public class ProductConfiguration {
    @Id
    @GeneratedValue
    private UUID id;

    @ManyToOne
    @JoinColumn(name = "product_item_id")
    private ProductItem productItem;

    @ManyToOne
    @JoinColumn(name = "variation_option_id")
    private VariationOption variationOption;

}
