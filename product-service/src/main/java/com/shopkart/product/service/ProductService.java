package com.shopkart.product.service;

import com.shopkart.product.dto.ProductRequest;
import com.shopkart.product.dto.ProductResponse;
import com.shopkart.product.entity.Category;
import com.shopkart.product.entity.Product;
import com.shopkart.product.exception.DuplicateSkuException;
import com.shopkart.product.exception.ResourceNotFoundException;
import com.shopkart.product.repository.CategoryRepository;
import com.shopkart.product.repository.ProductRepository;
import com.shopkart.product.repository.ProductSpecifications;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;

@Service
@RequiredArgsConstructor
public class ProductService {

    private final ProductRepository productRepository;
    private final CategoryRepository categoryRepository;

    @Transactional
    public ProductResponse create(ProductRequest request) {
        if (productRepository.existsBySku(request.sku())) {
            throw new DuplicateSkuException(request.sku());
        }

        Category category = categoryRepository.findById(request.categoryId())
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Category not found with id: " + request.categoryId()));

        Product product = Product.builder()
                .sku(request.sku())
                .name(request.name())
                .description(request.description())
                .price(request.price())
                .imageUrl(request.imageUrl())
                .category(category)
                .active(true)
                .build();

        return ProductResponse.from(productRepository.save(product));
    }

    public ProductResponse getById(Long id) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Product not found with id: " + id));
        return ProductResponse.from(product);
    }

    /**
     * Search products with optional filters. Any parameter left null/blank is simply
     * not applied - see ProductSpecifications for how each filter degrades gracefully.
     */
    public Page<ProductResponse> search(
            String categorySlug,
            String keyword,
            BigDecimal minPrice,
            BigDecimal maxPrice,
            Boolean activeOnly,
            Pageable pageable
    ) {
        Specification<Product> spec = Specification
                .where(ProductSpecifications.hasCategorySlug(categorySlug))
                .and(ProductSpecifications.nameContains(keyword))
                .and(ProductSpecifications.priceGreaterThanOrEqual(minPrice))
                .and(ProductSpecifications.priceLessThanOrEqual(maxPrice))
                .and(ProductSpecifications.isActive(activeOnly));

        return productRepository.findAll(spec, pageable).map(ProductResponse::from);
    }

    @Transactional
    public ProductResponse update(Long id, ProductRequest request) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Product not found with id: " + id));

        if (!product.getSku().equals(request.sku()) && productRepository.existsBySku(request.sku())) {
            throw new DuplicateSkuException(request.sku());
        }

        Category category = categoryRepository.findById(request.categoryId())
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Category not found with id: " + request.categoryId()));

        product.setSku(request.sku());
        product.setName(request.name());
        product.setDescription(request.description());
        product.setPrice(request.price());
        product.setImageUrl(request.imageUrl());
        product.setCategory(category);

        return ProductResponse.from(productRepository.save(product));
    }

    @Transactional
    public void delete(Long id) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Product not found with id: " + id));
        // Soft delete - keeps order history intact for products that were once orderable
        product.setActive(false);
        productRepository.save(product);
    }
}
