package com.mot.model;

import jakarta.persistence.*;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Entity
public class Variation {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ManyToOne
    @JoinColumn(name = "category_id")
    private Category variationCategory; // specifies to which category this variation is assigned

    private String name;

    @OneToMany(mappedBy = "variation", cascade = CascadeType.ALL)
    private Set<VariationOption> variationOptionList = new HashSet<>();

}
