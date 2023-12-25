package com.mot.dtos;

import java.util.ArrayList;
import java.util.List;

public class CategoryDTO {
    private Integer id;
    private Integer parentCategoryId;
    private String name;
    private List<CategoryDTO> childCategories;

    public CategoryDTO() {
        this(null, null, null, new ArrayList<>());
    }

    public CategoryDTO(Integer id, Integer parentCategoryId, String name, List<CategoryDTO> childCategories) {
        this.id = id;
        this.parentCategoryId = parentCategoryId;
        this.name = name;
        this.childCategories = childCategories;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getParentCategoryId() {
        return parentCategoryId;
    }

    public void setParentCategoryId(Integer parentCategoryId) {
        this.parentCategoryId = parentCategoryId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<CategoryDTO> getChildCategories() {
        return childCategories;
    }

    public void setChildCategories(List<CategoryDTO> childCategories) {
        this.childCategories = childCategories;
    }
}
