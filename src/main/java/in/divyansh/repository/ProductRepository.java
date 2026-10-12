package in.divyansh.repository;

import in.divyansh.entity.Product;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface ProductRepository extends JpaRepository<Product, Long> {

    Optional<Product> findBySku(String sku);

    boolean existsBySku(String sku);

    List<Product> findByIsAvailableTrue();

    List<Product> findByCategoryIgnoreCase(String category);

    List<Product> findByPriceBetween(
            double minPrice,
            double maxPrice
    );


    // =========================================================
    // SEARCH PRODUCTS
    // =========================================================

    @Query("""
        SELECT p
        FROM Product p
        WHERE (
            LOWER(p.name) LIKE LOWER(CONCAT('%', :keyword, '%'))
            OR LOWER(p.description) LIKE LOWER(CONCAT('%', :keyword, '%'))
        )
        AND p.isAvailable = true
        """)
    Page<Product> searchProducts(
            @Param("keyword") String keyword,
            Pageable pageable
    );


    // =========================================================
    // INCREASE STOCK
    // =========================================================

    @Modifying
    @Query("""
        UPDATE Product p
        SET p.stockQuantity = p.stockQuantity + :quantity
        WHERE p.id = :productId
        """)
    int increaseStock(
            @Param("productId") Long productId,
            @Param("quantity") Integer quantity
    );


    // =========================================================
    // DECREASE STOCK
    // =========================================================

    @Modifying
    @Query("""
        UPDATE Product p
        SET p.stockQuantity = p.stockQuantity - :quantity
        WHERE p.id = :productId
        AND p.stockQuantity >= :quantity
        """)
    int decreaseStock(
            @Param("productId") Long productId,
            @Param("quantity") Integer quantity
    );


    // =========================================================
    // LOW STOCK PRODUCTS
    // =========================================================

    @Query("""
        SELECT p
        FROM Product p
        WHERE p.stockQuantity <= :threshold
        AND p.isAvailable = true
        """)
    List<Product> findLowStockProducts(
            @Param("threshold") Integer threshold
    );
}