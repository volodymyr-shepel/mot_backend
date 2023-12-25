package com.mot.dtos;

import java.time.LocalDateTime;
import java.util.UUID;

public record ProductDTO(
        UUID id,
        String sku,
        String name,
        String specification,
        Integer quantity,
        String imageUrl,
        LocalDateTime createdOn,
        LocalDateTime updatedOn,
        Integer categoryId
) {}
