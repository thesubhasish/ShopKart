package com.shopkart.product.controller;

import com.shopkart.product.dto.ProductRequest;
import com.shopkart.product.dto.ProductResponse;
import com.shopkart.product.service.ProductService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;

@RestController
@RequestMapping("/api/products")
@RequiredArgsConstructor
public class ProductController {

    private final ProductService productService;

    @PostMapping
    public ResponseEntity<ProductResponse> create(@Valid @RequestBody ProductRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(productService.create(request));
    }

    @GetMapping("/{id}")
    public ProductResponse getById(@PathVariable Long id) {
        return productService.getById(id);
    }

    /**
     * Example calls:
     *   GET /api/products?page=0&size=10
     *   GET /api/products?category=electronics
     *   GET /api/products?keyword=shoes&minPrice=500&maxPrice=3000
     *   GET /api/products?sort=price,asc
     */
    @GetMapping
    public Page<ProductResponse> search(
            @RequestParam(required = false) String category,
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) BigDecimal minPrice,
            @RequestParam(required = false) BigDecimal maxPrice,
            @RequestParam(defaultValue = "true") Boolean activeOnly,
            @PageableDefault(size = 20) Pageable pageable
    ) {
        return productService.search(category, keyword, minPrice, maxPrice, activeOnly, pageable);
    }

    @PutMapping("/{id}")
    public ProductResponse update(@PathVariable Long id, @Valid @RequestBody ProductRequest request) {
        return productService.update(id, request);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        productService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
