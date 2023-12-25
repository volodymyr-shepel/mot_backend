package com.mot.service;

import com.mot.dtos.CategoryDTO;
import com.mot.dtos.ProductDTO;
import com.mot.model.Category;
import com.mot.model.Product;
import com.mot.repository.CategoryRepository;
import com.mot.repository.ProductRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class PublicProductService {
    private final ProductRepository productRepository;
    private final CategoryRepository categoryRepository;

    public PublicProductService(ProductRepository productRepository, CategoryRepository categoryRepository) {
        this.productRepository = productRepository;
        this.categoryRepository = categoryRepository;
    }

    public ProductDTO getProductById(UUID productId) {
        Optional<Product> productOptional = productRepository.findById(productId);
        Product product = productOptional.orElse(null);
        return product != null ? convertToProductDTO(product) : null;
    }
    public List<ProductDTO> getProductsByCategory(int categoryId){
        Category category = categoryRepository.findById(categoryId)
                .orElseThrow(() -> new EntityNotFoundException("Category not found"));
        Set<Product> products = category.getProducts();
        return products.stream()
                .map(this::convertToProductDTO)
                .collect(Collectors.toList());

    }

    private ProductDTO convertToProductDTO(Product product) {

        return new ProductDTO(
                product.getId(),
                product.getSku(),
                product.getName(),
                product.getSpecification(),
                product.getQuantity(),
                product.getImageUrl(),
                product.getCreatedOn(),
                product.getUpdatedOn(),
                product.getCategory().getId()
        );
    }


    public List<CategoryDTO> getCategoryHierarchy() {
        List<Category> categories = categoryRepository.findByParentIsNull(); // Implement this method in your service

        return categories.stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }
    public List<Category> getAllCategories() {
        return categoryRepository.findAll();
    }


    private CategoryDTO convertToDTO(Category category) {
        CategoryDTO dto = new CategoryDTO();
        dto.setId(category.getId());
        dto.setParentCategoryId(category.getParent() != null ? category.getParent().getId() : null);
        dto.setName(category.getName());
        dto.setChildCategories(
                category.getSubCategories().stream()
                        .map(this::convertToDTO)
                        .collect(Collectors.toList())
        );
        return dto;
    }



}
