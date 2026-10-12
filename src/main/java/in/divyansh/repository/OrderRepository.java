package in.divyansh.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import in.divyansh.entity.Order;

public interface OrderRepository extends JpaRepository<Order, Long> {

    // =========================================================
    // 1. Get all orders of a specific user
    //    Newest orders first
    // =========================================================

    List<Order> findByUserIdOrderByOrderDateDesc(Long userId);


    // =========================================================
    // 2. Get orders of a specific user with pagination
    // =========================================================

    Page<Order> findByUserId(
            Long userId,
            Pageable pageable
    );


    // =========================================================
    // 3. Get orders by status
    // =========================================================

    List<Order> findByStatus(String status);


    // =========================================================
    // 4. Get order by ID with OrderItems + Products
    // =========================================================

    @Query("""
            SELECT o
            FROM Order o
            LEFT JOIN FETCH o.orderItems oi
            LEFT JOIN FETCH oi.product
            WHERE o.id = :orderId
            """)
    Optional<Order> findByIdWithItems(
            @Param("orderId") Long orderId
    );


    // =========================================================
    // 5. Get order by order number with OrderItems + Products
    // =========================================================

    @Query("""
            SELECT o
            FROM Order o
            LEFT JOIN FETCH o.orderItems oi
            LEFT JOIN FETCH oi.product
            WHERE o.orderNumber = :orderNumber
            """)
    Optional<Order> findByOrderNumberWithItems(
            @Param("orderNumber") String orderNumber
    );


    // =========================================================
    // 6. Search orders
    //    Searches:
    //    - order number
    //    - user's full name
    //    - user's email
    // =========================================================

    @Query("""
            SELECT o
            FROM Order o
            WHERE LOWER(o.orderNumber)
                  LIKE LOWER(CONCAT('%', :keyword, '%'))
               OR LOWER(o.user.fullName)
                  LIKE LOWER(CONCAT('%', :keyword, '%'))
               OR LOWER(o.user.email)
                  LIKE LOWER(CONCAT('%', :keyword, '%'))
            """)
    Page<Order> searchOrders(
            @Param("keyword") String keyword,
            Pageable pageable
    );
}