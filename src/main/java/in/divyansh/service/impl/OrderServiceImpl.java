package in.divyansh.service.impl;




import lombok.RequiredArgsConstructor;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.data.domain.Pageable;
import in.divyansh.dto.request.PlaceOrderRequestDTO;
import in.divyansh.dto.request.UpdateOrderStatusRequestDTO;
import in.divyansh.dto.response.OrderItemResponseDTO;
import in.divyansh.dto.response.OrderResponseDTO;
import in.divyansh.dto.response.PageResponseDTO;
import in.divyansh.entity.User;
import in.divyansh.repository.CartRepository;
import in.divyansh.repository.OrderItemRepository;
import in.divyansh.repository.OrderRepository;
import in.divyansh.repository.ProductRepository;
import in.divyansh.repository.UserRepository;
import in.divyansh.service.OrderService;
import in.divyansh.dto.request.UpdateUserRequestDTO;
import in.divyansh.dto.request.UserRequestDTO;
import in.divyansh.dto.response.UserResponseDTO;
import in.divyansh.entity.Cart;
import in.divyansh.entity.CartItem;
import in.divyansh.entity.Order;
import in.divyansh.entity.OrderItem;
import in.divyansh.entity.Product;
import in.divyansh.entity.User;
import in.divyansh.exception.DuplicateResourceException;
import in.divyansh.exception.EmptyCartException;
import in.divyansh.exception.InsufficientStockException;
import in.divyansh.exception.InvalidOperationException;
import in.divyansh.exception.ResourceNotFoundException;
import in.divyansh.repository.UserRepository;
import in.divyansh.service.UserService;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;


@Service
@RequiredArgsConstructor
@Transactional
public class OrderServiceImpl implements OrderService {

    private final OrderRepository orderRepository;
    private final OrderItemRepository orderItemRepository;
    private final CartRepository cartRepository;
    private final UserRepository userRepository;
    private final ProductRepository productRepository;


    // =========================================================
    // PLACE ORDER
    // =========================================================

    @Override
    public OrderResponseDTO placeOrder(
            PlaceOrderRequestDTO placeOrderRequestDTO) {

        // Fetch user
        Optional<User> userOptional =
                userRepository.findById(
                        placeOrderRequestDTO.getUserId());

        if (!userOptional.isPresent()) {
            throw new ResourceNotFoundException(
                    "User",
                    "id",
                    placeOrderRequestDTO.getUserId());
        }

        User user = userOptional.get();


        // Fetch cart with items
        Optional<Cart> cartOptional =
                cartRepository.findByUserIdWithItems(
                        placeOrderRequestDTO.getUserId());

        if (!cartOptional.isPresent()) {
            throw new ResourceNotFoundException(
                    "Cart",
                    "userId",
                    placeOrderRequestDTO.getUserId());
        }

        Cart cart = cartOptional.get();


        // Validate cart is not empty
        if (cart.getCartItems().isEmpty()) {
            throw new EmptyCartException(
                    "Cannot place order with an empty cart");
        }


        // Copy cart items to avoid concurrent modification issues
        List<CartItem> cartItemsCopy =
                new ArrayList<>(cart.getCartItems());


        // Validate stock availability for all items
        for (CartItem cartItem : cartItemsCopy) {

            Product product = cartItem.getProduct();

            if (!product.getIsAvailable()) {
                throw new InsufficientStockException(
                        product.getName()
                                + " is no longer available");
            }

            if (product.getStockQuantity()
                    < cartItem.getQuantity()) {

                throw new InsufficientStockException(
                        "Insufficient stock for "
                                + product.getName()
                                + ". Available: "
                                + product.getStockQuantity()
                                + ", Requested: "
                                + cartItem.getQuantity());
            }
        }


        // Calculate total amount
        // as the sum of all cart item subtotals
        double totalAmount = 0.0;

        for (CartItem cartItem : cartItemsCopy) {
            totalAmount =
                    totalAmount + cartItem.getSubTotal();
        }


        // Create order
        Order order = Order.builder()
                .user(user)
                .totalAmount(totalAmount)
                .notes(placeOrderRequestDTO.getNotes())
                .status(Order.STATUS_CONFIRMED)
                .build();

        Order savedOrder =
                orderRepository.save(order);


        // Create order items from cart items
        List<OrderItem> orderItems =
                new ArrayList<>();

        for (CartItem cartItem : cartItemsCopy) {

            OrderItem orderItem =
                    OrderItem.fromCartItem(cartItem);

            orderItem.setOrder(savedOrder);

            orderItems.add(orderItem);


            // Decrease product stock
            productRepository.decreaseStock(
                    cartItem.getProduct().getId(),
                    cartItem.getQuantity());
        }

        orderItemRepository.saveAll(orderItems);

        savedOrder.setOrderItems(orderItems);


        // Clear the cart after order is placed
        cart.getCartItems().clear();

        cart.setTotalItems(0);
        cart.setTotalAmount(0.0);

        cartRepository.save(cart);


        return mapToOrderResponseDTO(savedOrder);
    }


    // =========================================================
    // GET ORDER BY ID
    // =========================================================

    @Override
    public OrderResponseDTO getOrderById(Long orderId) {

        Optional<Order> orderOptional =
                orderRepository.findByIdWithItems(orderId);

        if (orderOptional.isPresent()) {
            return mapToOrderResponseDTO(
                    orderOptional.get());
        } else {
            throw new ResourceNotFoundException(
                    "Order",
                    "id",
                    orderId);
        }
    }


    // =========================================================
    // GET ORDER BY ORDER NUMBER
    // =========================================================

    @Override
    public OrderResponseDTO getOrderByOrderNumber(
            String orderNumber) {

        Optional<Order> orderOptional =
                orderRepository.findByOrderNumberWithItems(
                        orderNumber);

        if (orderOptional.isPresent()) {
            return mapToOrderResponseDTO(
                    orderOptional.get());
        } else {
            throw new ResourceNotFoundException(
                    "Order",
                    "orderNumber",
                    orderNumber);
        }
    }


    // =========================================================
    // GET ORDERS BY USER ID
    // =========================================================

    @Override
    public List<OrderResponseDTO> getOrdersByUserId(
            Long userId) {

        List<Order> orders =
                orderRepository.findByUserIdOrderByOrderDateDesc(
                        userId);

        List<OrderResponseDTO> responseList =
                new ArrayList<>();

        for (Order order : orders) {
            responseList.add(
                    mapToOrderResponseDTO(order));
        }

        return responseList;
    }


    // =========================================================
    // GET ALL ORDERS WITH PAGINATION
    // =========================================================

    @Override
    public PageResponseDTO<OrderResponseDTO>
    getAllOrdersPaginated(
            int page,
            int size,
            String sortBy,
            String sortDir) {

        Sort sort;

        if (sortDir.equalsIgnoreCase("desc")) {
            sort = Sort.by(sortBy).descending();
        } else {
            sort = Sort.by(sortBy).ascending();
        }

        Pageable pageable =
                PageRequest.of(page, size, sort);

        Page<Order> orderPage =
                orderRepository.findAll(pageable);

        return mapToPageResponse(orderPage);
    }


    // =========================================================
    // GET ORDERS BY STATUS
    // =========================================================

    @Override
    public List<OrderResponseDTO> getOrdersByStatus(
            String status) {

        List<Order> orders =
                orderRepository.findByStatus(status);

        List<OrderResponseDTO> responseList =
                new ArrayList<>();

        for (Order order : orders) {
            responseList.add(
                    mapToOrderResponseDTO(order));
        }

        return responseList;
    }


    // =========================================================
    // UPDATE ORDER STATUS
    // =========================================================

    @Override
    public OrderResponseDTO updateOrderStatus(
            Long orderId,
            UpdateOrderStatusRequestDTO
                    updateOrderStatusRequestDTO) {

        Order order = findOrderById(orderId);


        validateStatusTransition(
                order.getStatus(),
                updateOrderStatusRequestDTO.getOrderStatus());


        order.setStatus(
                updateOrderStatusRequestDTO.getOrderStatus());


        // If order is cancelled, restore stock
        if (Order.STATUS_CANCELLED.equals(
                updateOrderStatusRequestDTO.getOrderStatus())) {

            restoreStock(order);
        }


        // Add update notes
        if (updateOrderStatusRequestDTO.getNotes() != null
                && !updateOrderStatusRequestDTO
                        .getNotes()
                        .isEmpty()) {

            String existingNotes =
                    order.getNotes() != null
                            ? order.getNotes() + "\n"
                            : "";

            order.setNotes(
                    existingNotes
                            + "["
                            + LocalDateTime.now()
                            + "] "
                            + updateOrderStatusRequestDTO
                                    .getNotes());
        }


        Order updatedOrder =
                orderRepository.save(order);

        return mapToOrderResponseDTO(updatedOrder);
    }


    // =========================================================
    // CANCEL ORDER
    // =========================================================

    @Override
    public OrderResponseDTO cancelOrder(
            Long orderId,
            String reason) {

        Order order = findOrderById(orderId);


        if (!Order.STATUS_CONFIRMED.equals(
                order.getStatus())) {

            throw new InvalidOperationException(
                    "Cannot cancel order with status: "
                            + order.getStatus());
        }


        order.setStatus(
                Order.STATUS_CANCELLED);


        String notes =
                order.getNotes() != null
                        ? order.getNotes() + "\n"
                        : "";

        order.setNotes(
                notes
                        + "["
                        + LocalDateTime.now()
                        + "] Cancelled: "
                        + reason);


        // Restore stock
        restoreStock(order);


        Order cancelledOrder =
                orderRepository.save(order);

        return mapToOrderResponseDTO(
                cancelledOrder);
    }


    // =========================================================
    // SEARCH ORDERS
    // =========================================================

    @Override
    public PageResponseDTO<OrderResponseDTO> searchOrders(
            String keyword,
            int page,
            int size) {

        Pageable pageable = PageRequest.of(page, size);

        Page<Order> orderPage =
                orderRepository.searchOrders(keyword, pageable);

        return mapToPageResponse(orderPage);
    }


    // =========================================================
    // HELPER: FIND ORDER BY ID
    // =========================================================

    private Order findOrderById(Long orderId) {

        Optional<Order> orderOptional =
                orderRepository.findById(orderId);

        if (orderOptional.isPresent()) {
            return orderOptional.get();
        } else {
            throw new ResourceNotFoundException(
                    "Order",
                    "id",
                    orderId);
        }
    }


    // =========================================================
    // HELPER: VALIDATE STATUS TRANSITION
    // =========================================================

    private void validateStatusTransition(
            String currentStatus,
            String newStatus) {

        if (Order.STATUS_CONFIRMED.equals(
                currentStatus)) {

            if (!Order.STATUS_CANCELLED.equals(
                    newStatus)) {

                throw new InvalidOperationException(
                        "Cannot transition from CONFIRMED to "
                                + newStatus);
            }

        } else if (Order.STATUS_CANCELLED.equals(
                currentStatus)) {

            throw new InvalidOperationException(
                    "Cannot change status of "
                            + currentStatus
                            + " order");
        }
    }


    // =========================================================
    // HELPER: RESTORE STOCK
    // =========================================================

    private void restoreStock(Order order) {

        for (OrderItem orderItem :
                order.getOrderItems()) {

            productRepository.increaseStock(
                    orderItem.getProduct().getId(),
                    orderItem.getQuantity());
        }
    }


    // =========================================================
    // HELPER: MAP ORDER TO ORDER RESPONSE DTO
    // =========================================================

    private OrderResponseDTO mapToOrderResponseDTO(
            Order order) {

        List<OrderItemResponseDTO> items =
                new ArrayList<>();

        for (OrderItem orderItem :
                order.getOrderItems()) {

            items.add(
                    mapToOrderItemResponseDTO(
                            orderItem));
        }


        int totalItems = 0;

        for (OrderItem orderItem :
                order.getOrderItems()) {

            totalItems =
                    totalItems
                            + orderItem.getQuantity();
        }


        return OrderResponseDTO.builder()
                .id(order.getId())
                .orderNumber(order.getOrderNumber())
                .userId(order.getUser().getId())
                .userName(order.getUser().getFullName())
                .userEmail(order.getUser().getEmail())
                .items(items)
                .totalItems(totalItems)
                .totalAmount(order.getTotalAmount())
                .status(order.getStatus())
                .notes(order.getNotes())
                .orderDate(order.getOrderDate())
                .build();
    }


    // =========================================================
    // HELPER: MAP ORDER ITEM TO RESPONSE DTO
    // =========================================================

    private OrderItemResponseDTO
    mapToOrderItemResponseDTO(
            OrderItem orderItem) {

        return OrderItemResponseDTO.builder()
                .id(orderItem.getId())
                .productId(
                        orderItem.getProduct().getId())
                .productName(
                        orderItem.getProductName())
                .productSku(
                        orderItem.getProductSku())
                .productImage(
                        orderItem.getProduct().getImageUrl())
                .quantity(
                        orderItem.getQuantity())
                .unitPrice(
                        orderItem.getUnitPrice())
                .subtotal(
                        orderItem.getSubTotal())
                .build();
    }


    // =========================================================
    // HELPER: MAP PAGE TO PAGE RESPONSE DTO
    // =========================================================

    private PageResponseDTO<OrderResponseDTO>
    mapToPageResponse(
            Page<Order> orderPage) {

        List<OrderResponseDTO> orders =
                new ArrayList<>();

        for (Order order :
                orderPage.getContent()) {

            orders.add(
                    mapToOrderResponseDTO(order));
        }


        return PageResponseDTO                .<OrderResponseDTO>builder()
                .content(orders)
                .pageNumber(orderPage.getNumber())
                .pageSize(orderPage.getSize())
                .totalElements(
                        orderPage.getTotalElements())
                .totalPages(
                        orderPage.getTotalPages())
                .first(orderPage.isFirst())
                .last(orderPage.isLast())
                .hasNext(orderPage.hasNext())
                .hasPrevious(
                        orderPage.hasPrevious())
                .build();
    }
}