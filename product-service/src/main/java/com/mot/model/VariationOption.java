package com.mot.model;

import jakarta.persistence.*;

import java.util.HashSet;
import java.util.Set;


@Entity
public class VariationOption {
    @Id
    @GeneratedValue
    private Integer id;

    @ManyToOne
    @JoinColumn(name = "variation_id")
    private Variation variation;

    @OneToMany(mappedBy = "variationOption")
    private Set<ProductConfiguration> configurations = new HashSet<>();



    private String value;


}
