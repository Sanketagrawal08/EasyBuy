package in.divyansh.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import in.divyansh.dto.request.PlaceOrderRequestDTO;
import in.divyansh.dto.request.UpdateOrderStatusRequestDTO;
import in.divyansh.dto.response.ApiResponseDTO;
import in.divyansh.dto.response.OrderResponseDTO;
import in.divyansh.dto.response.PageResponseDTO;
import in.divyansh.service.OrderService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/orders")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class OrderController {

    private final OrderService orderService;


    // =========================================================
    // PLACE ORDER
    // POST /api/orders
    // =========================================================

    @PostMapping
    public ResponseEntity<ApiResponseDTO<OrderResponseDTO>> placeOrder(
            @Valid @RequestBody PlaceOrderRequestDTO placeOrderRequestDTO) {

        OrderResponseDTO order =
                orderService.placeOrder(placeOrderRequestDTO);

        return new ResponseEntity<>(
                ApiResponseDTO.success(
                        "Order placed successfully",
                        order
                ),
                HttpStatus.CREATED
        );
    }


    // =========================================================
    // GET ORDER BY ID
    // GET /api/orders/{orderId}
    // =========================================================

    @GetMapping("/{orderId}")
    public ResponseEntity<ApiResponseDTO<OrderResponseDTO>> getOrderById(
            @PathVariable Long orderId) {

        OrderResponseDTO order =
                orderService.getOrderById(orderId);

        return ResponseEntity.ok(
                ApiResponseDTO.success(order)
        );
    }


    // =========================================================
    // GET ORDER BY ORDER NUMBER
    // GET /api/orders/number/{orderNumber}
    // =========================================================

    @GetMapping("/number/{orderNumber}")
    public ResponseEntity<ApiResponseDTO<OrderResponseDTO>> getOrderByOrderNumber(
            @PathVariable String orderNumber) {

        OrderResponseDTO order =
                orderService.getOrderByOrderNumber(orderNumber);

        return ResponseEntity.ok(
                ApiResponseDTO.success(order)
        );
    }


    // =========================================================
    // GET ORDERS BY USER ID
    // GET /api/orders/user/{userId}
    // =========================================================

    @GetMapping("/user/{userId}")
    public ResponseEntity<ApiResponseDTO<List<OrderResponseDTO>>> getOrdersByUserId(
            @PathVariable Long userId) {

        List<OrderResponseDTO> orders =
                orderService.getOrdersByUserId(userId);

        return ResponseEntity.ok(
                ApiResponseDTO.success(
                        "Fetched " + orders.size() + " orders",
                        orders
                )
        );
    }


    // =========================================================
    // GET ALL ORDERS - PAGINATED
    // GET /api/orders?page=0&size=10&sortBy=orderDate&sortDir=desc
    // =========================================================

    @GetMapping
    public ResponseEntity<ApiResponseDTO<PageResponseDTO<OrderResponseDTO>>> getAllOrdersPaginated(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "orderDate") String sortBy,
            @RequestParam(defaultValue = "desc") String sortDir) {

        PageResponseDTO<OrderResponseDTO> orders =
                orderService.getAllOrdersPaginated(
                        page,
                        size,
                        sortBy,
                        sortDir
                );

        return ResponseEntity.ok(
                ApiResponseDTO.success(orders)
        );
    }


    // =========================================================
    // GET ORDERS BY STATUS
    // GET /api/orders/status/{status}
    // =========================================================

    @GetMapping("/status/{status}")
    public ResponseEntity<ApiResponseDTO<List<OrderResponseDTO>>> getOrdersByStatus(
            @PathVariable String status) {

        List<OrderResponseDTO> orders =
                orderService.getOrdersByStatus(status);

        return ResponseEntity.ok(
                ApiResponseDTO.success(orders)
        );
    }


    // =========================================================
    // UPDATE ORDER STATUS
    // PATCH /api/orders/{orderId}/status
    // =========================================================

    @PatchMapping("/{orderId}/status")
    public ResponseEntity<ApiResponseDTO<OrderResponseDTO>> updateOrderStatus(
            @PathVariable Long orderId,
            @Valid @RequestBody UpdateOrderStatusRequestDTO updateOrderStatusRequestDTO) {

        OrderResponseDTO order =
                orderService.updateOrderStatus(
                        orderId,
                        updateOrderStatusRequestDTO
                );

        return ResponseEntity.ok(
                ApiResponseDTO.success(
                        "Order status updated successfully",
                        order
                )
        );
    }


    // =========================================================
    // CANCEL ORDER
    // POST /api/orders/{orderId}/cancel
    // =========================================================

    @PostMapping("/{orderId}/cancel")
    public ResponseEntity<ApiResponseDTO<OrderResponseDTO>> cancelOrder(
            @PathVariable Long orderId,
            @RequestParam(
                    required = false,
                    defaultValue = "Customer requested cancellation"
            )
            String reason) {

        OrderResponseDTO order =
                orderService.cancelOrder(
                        orderId,
                        reason
                );

        return ResponseEntity.ok(
                ApiResponseDTO.success(
                        "Order cancelled successfully",
                        order
                )
        );
    }


    // =========================================================
    // SEARCH ORDERS
    // GET /api/orders/search?keyword=abc&page=0&size=10
    // =========================================================

    @GetMapping("/search")
    public ResponseEntity<ApiResponseDTO<PageResponseDTO<OrderResponseDTO>>> searchOrders(
            @RequestParam String keyword,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {

        PageResponseDTO<OrderResponseDTO> orders =
                orderService.searchOrders(
                        keyword,
                        page,
                        size
                );

        return ResponseEntity.ok(
                ApiResponseDTO.success(orders)
        );
    }
}