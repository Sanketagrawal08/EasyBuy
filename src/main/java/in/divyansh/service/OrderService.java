package in.divyansh.service;

import java.util.List;

import in.divyansh.dto.request.PlaceOrderRequestDTO;
import in.divyansh.dto.request.UpdateOrderStatusRequestDTO;
import in.divyansh.dto.response.OrderResponseDTO;
import in.divyansh.dto.response.PageResponseDTO;
import org.springframework.data.domain.Pageable;
public interface OrderService {

    OrderResponseDTO placeOrder(PlaceOrderRequestDTO placeOrderRequestDTO);

    OrderResponseDTO getOrderById(Long orderId);

    OrderResponseDTO getOrderByOrderNumber(String orderNumber);

    List<OrderResponseDTO> getOrdersByUserId(Long userId);

    PageResponseDTO<OrderResponseDTO> getAllOrdersPaginated(
            int page,
            int size,
            String sortBy,
            String sortDir
    );

    List<OrderResponseDTO> getOrdersByStatus(String status);

    OrderResponseDTO updateOrderStatus(
            Long orderId,
            UpdateOrderStatusRequestDTO updateOrderStatusRequestDTO
    );

    OrderResponseDTO cancelOrder(Long orderId, String reason);

    PageResponseDTO<OrderResponseDTO> searchOrders(
            String keyword,
            int page,
            int size
    );
}
