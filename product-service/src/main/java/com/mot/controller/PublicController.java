package com.mot.controller;

import com.mot.dtos.CategoryDTO;
import com.mot.dtos.ProductDTO;
import com.mot.model.Category;
import com.mot.model.Product;
import com.mot.service.PublicProductService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping(path = "/api/product/public")
public class PublicController {

    private final PublicProductService publicProductService;

    public PublicController(PublicProductService publicProductService) {
        this.publicProductService = publicProductService;
    }

    @GetMapping(path = "/p/{productId}")
    public ProductDTO getProductById(@PathVariable UUID productId) {
        return publicProductService.getProductById(productId);
    }

    @GetMapping(path = "/c/{categoryId}")
    public List<ProductDTO> getProductsByCategory(@PathVariable Integer categoryId){
        return publicProductService.getProductsByCategory(categoryId);
    }

    @GetMapping(path = "/categoryHierarchy")
    public List<CategoryDTO> getCategoryHierarchy(){
        return publicProductService.getCategoryHierarchy();
    }
    @GetMapping(path = "/test")
    public String test(){
        return "Bankai";
    }


}
