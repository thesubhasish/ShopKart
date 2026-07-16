package com.shopkart.product.repository;

import com.shopkart.product.entity.Product;
import org.springframework.data.jpa.domain.Specification;

import java.math.BigDecimal;

/**
 * Builds JPA Specifications on the fly based on which filters the caller actually provided.
 * Each method returns null if its filter wasn't requested, and Specification.where() safely
 * ignores null specs when combined with .and().
 */
public class ProductSpecifications {

    private ProductSpecifications() {}

    public static Specification<Product> hasCategorySlug(String categorySlug) {
        if (categorySlug == null || categorySlug.isBlank()) return null;
        return (root, query, cb) -> cb.equal(root.get("category").get("slug"), categorySlug);
    }

    public static Specification<Product> nameContains(String keyword) {
        if (keyword == null || keyword.isBlank()) return null;
        String pattern = "%" + keyword.toLowerCase() + "%";
        return (root, query, cb) -> cb.like(cb.lower(root.get("name")), pattern);
    }

    public static Specification<Product> priceGreaterThanOrEqual(BigDecimal minPrice) {
        if (minPrice == null) return null;
        return (root, query, cb) -> cb.greaterThanOrEqualTo(root.get("price"), minPrice);
    }

    public static Specification<Product> priceLessThanOrEqual(BigDecimal maxPrice) {
        if (maxPrice == null) return null;
        return (root, query, cb) -> cb.lessThanOrEqualTo(root.get("price"), maxPrice);
    }

    public static Specification<Product> isActive(Boolean active) {
        if (active == null) return null;
        return (root, query, cb) -> cb.equal(root.get("active"), active);
    }
}
