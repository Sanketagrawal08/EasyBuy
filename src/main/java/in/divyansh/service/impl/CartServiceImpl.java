package in.divyansh.service.impl;

import in.divyansh.dto.request.AddToCartRequestDTO;
import in.divyansh.dto.request.UpdateCartItemRequestDTO;

import in.divyansh.dto.response.CartItemResponseDTO;
import in.divyansh.dto.response.CartResponseDTO;

import in.divyansh.entity.Cart;
import in.divyansh.entity.CartItem;
import in.divyansh.entity.Product;
import in.divyansh.entity.User;

import in.divyansh.exception.InsufficientStockException;
import in.divyansh.exception.ResourceNotFoundException;

import in.divyansh.repository.CartItemRepository;
import in.divyansh.repository.CartRepository;
import in.divyansh.repository.ProductRepository;
import in.divyansh.repository.UserRepository;

import in.divyansh.service.CartService;

import lombok.RequiredArgsConstructor;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;


@Service
@RequiredArgsConstructor
@Transactional
public class CartServiceImpl implements CartService {

    private final CartRepository cartRepository;
    private final CartItemRepository cartItemRepository;
    private final UserRepository userRepository;
    private final ProductRepository productRepository;


    // =========================================================
    // GET CART
    // =========================================================

    @Override
    public CartResponseDTO getCartByUserId(Long userId) {

        Cart cart = getOrCreateCart(userId);

        return mapToCartResponseDTO(cart);
    }


    // =========================================================
    // ADD ITEM TO CART
    // =========================================================

    @Override
    public CartResponseDTO addItemToCart(
            Long userId,
            AddToCartRequestDTO addToCartRequestDTO) {

        Cart cart = getOrCreateCart(userId);

        Product product =
                findProductById(addToCartRequestDTO.getProductId());

        validateProductAvailability(
                product,
                addToCartRequestDTO.getQuantity()
        );

        Optional<CartItem> existingCartItem =
                cartItemRepository.findByCartIdAndProductId(
                        cart.getId(),
                        product.getId()
                );

        if (existingCartItem.isPresent()) {

            CartItem cartItem = existingCartItem.get();

            int newQuantity =
                    cartItem.getQuantity()
                            + addToCartRequestDTO.getQuantity();

            validateProductAvailability(
                    product,
                    newQuantity
            );

            cartItem.setQuantity(newQuantity);

            cartItem.calculateSubTotal();

            cartItemRepository.save(cartItem);

        } else {

            CartItem cartItem = CartItem.builder()
                    .cart(cart)
                    .product(product)
                    .quantity(addToCartRequestDTO.getQuantity())
                    .unitPrice(product.getPrice())
                    .build();

            cartItem.calculateSubTotal();

            cart.addCartItem(cartItem);

            cartItemRepository.save(cartItem);
        }

        cart.recalculateTotals();

        cartRepository.save(cart);

        return mapToCartResponseDTO(cart);
    }


    // =========================================================
    // UPDATE CART ITEM
    // =========================================================

    @Override
    public CartResponseDTO updateCartItem(
            Long userId,
            Long cartItemId,
            UpdateCartItemRequestDTO updateCartItemRequestDTO) {

        Cart cart = getOrCreateCart(userId);

        CartItem cartItem =
                findCartItemById(cartItemId);

        if (!cartItem.getCart()
                .getId()
                .equals(cart.getId())) {

            throw new ResourceNotFoundException(
                    "CartItem",
                    "id",
                    cartItemId
            );
        }

        validateProductAvailability(
                cartItem.getProduct(),
                updateCartItemRequestDTO.getQuantity()
        );

        cartItem.setQuantity(
                updateCartItemRequestDTO.getQuantity()
        );

        cartItem.calculateSubTotal();

        cartItemRepository.save(cartItem);

        cart.recalculateTotals();

        cartRepository.save(cart);

        return mapToCartResponseDTO(cart);
    }


    // =========================================================
    // REMOVE ITEM FROM CART
    // =========================================================

    @Override
    public CartResponseDTO removeItemFromCart(
            Long userId,
            Long cartItemId) {

        Cart cart = getOrCreateCart(userId);

        CartItem cartItem =
                findCartItemById(cartItemId);

        if (!cartItem.getCart()
                .getId()
                .equals(cart.getId())) {

            throw new ResourceNotFoundException(
                    "CartItem",
                    "id",
                    cartItemId
            );
        }

        cart.removeCartItem(cartItem);

        cartItemRepository.delete(cartItem);

        cart.recalculateTotals();

        cartRepository.save(cart);

        return mapToCartResponseDTO(cart);
    }


    // =========================================================
    // CLEAR CART
    // =========================================================

    @Override
    public CartResponseDTO clearCart(Long userId) {

        Cart cart = getOrCreateCart(userId);

        cart.clearCart();

        cartRepository.save(cart);

        return mapToCartResponseDTO(cart);
    }


    // =========================================================
    // GET CART ITEM
    // =========================================================

    @Override
    public CartItemResponseDTO getCartItem(
            Long userId,
            Long cartItemId) {

        Cart cart = getOrCreateCart(userId);

        CartItem cartItem =
                findCartItemById(cartItemId);

        if (!cartItem.getCart()
                .getId()
                .equals(cart.getId())) {

            throw new ResourceNotFoundException(
                    "CartItem",
                    "id",
                    cartItemId
            );
        }

        return mapToCartItemResponseDTO(cartItem);
    }


    // =========================================================
    // CHECK WHETHER PRODUCT IS IN CART
    // =========================================================

    @Override
    public boolean isProductInCart(
            Long userId,
            Long productId) {

        Optional<Cart> cartOptional =
                cartRepository.findByUserId(userId);

        if (!cartOptional.isPresent()) {
            return false;
        }

        Cart cart = cartOptional.get();

        return cartItemRepository
                .existsByCartIdAndProductId(
                        cart.getId(),
                        productId
                );
    }


    // =========================================================
    // INCREMENT ITEM QUANTITY
    // =========================================================

    @Override
    public CartResponseDTO incrementItemQuantity(
            Long userId,
            Long productId,
            int quantity) {

        Cart cart = getOrCreateCart(userId);

        Optional<CartItem> cartItemOptional =
                cartItemRepository.findByCartIdAndProductId(
                        cart.getId(),
                        productId
                );

        if (!cartItemOptional.isPresent()) {

            throw new ResourceNotFoundException(
                    "CartItem",
                    "productId",
                    productId
            );
        }

        CartItem cartItem =
                cartItemOptional.get();

        int newQuantity =
                cartItem.getQuantity() + quantity;

        validateProductAvailability(
                cartItem.getProduct(),
                newQuantity
        );

        cartItem.setQuantity(newQuantity);

        cartItem.calculateSubTotal();

        cartItemRepository.save(cartItem);

        cart.recalculateTotals();

        cartRepository.save(cart);

        return mapToCartResponseDTO(cart);
    }


    // =========================================================
    // DECREMENT ITEM QUANTITY
    // =========================================================

    @Override
    public CartResponseDTO decrementItemQuantity(
            Long userId,
            Long productId,
            int quantity) {

        Cart cart = getOrCreateCart(userId);

        Optional<CartItem> cartItemOptional =
                cartItemRepository.findByCartIdAndProductId(
                        cart.getId(),
                        productId
                );

        if (!cartItemOptional.isPresent()) {

            throw new ResourceNotFoundException(
                    "CartItem",
                    "productId",
                    productId
            );
        }

        CartItem cartItem =
                cartItemOptional.get();

        int newQuantity =
                cartItem.getQuantity() - quantity;

        if (newQuantity <= 0) {

            cart.removeCartItem(cartItem);

            cartItemRepository.delete(cartItem);

        } else {

            cartItem.setQuantity(newQuantity);

            cartItem.calculateSubTotal();

            cartItemRepository.save(cartItem);
        }

        cart.recalculateTotals();

        cartRepository.save(cart);

        return mapToCartResponseDTO(cart);
    }


    // =========================================================
    // HELPER: GET OR CREATE CART
    // =========================================================

    private Cart getOrCreateCart(Long userId) {

        Optional<Cart> cartOptional =
                cartRepository.findByUserIdWithItems(userId);

        if (cartOptional.isPresent()) {

            return cartOptional.get();

        } else {

            Optional<User> userOptional =
                    userRepository.findById(userId);

            if (userOptional.isPresent()) {

                User user = userOptional.get();

                Cart newCart = Cart.builder()
                        .user(user)
                        .build();

                return cartRepository.save(newCart);

            } else {

                throw new ResourceNotFoundException(
                        "User",
                        "id",
                        userId
                );
            }
        }
    }


    // =========================================================
    // HELPER: FIND PRODUCT BY ID
    // =========================================================

    private Product findProductById(Long productId) {

        Optional<Product> productOptional =
                productRepository.findById(productId);

        if (productOptional.isPresent()) {

            return productOptional.get();

        } else {

            throw new ResourceNotFoundException(
                    "Product",
                    "id",
                    productId
            );
        }
    }


    // =========================================================
    // HELPER: FIND CART ITEM BY ID
    // =========================================================

    private CartItem findCartItemById(Long cartItemId) {

        Optional<CartItem> cartItemOptional =
                cartItemRepository.findById(cartItemId);

        if (cartItemOptional.isPresent()) {

            return cartItemOptional.get();

        } else {

            throw new ResourceNotFoundException(
                    "CartItem",
                    "id",
                    cartItemId
            );
        }
    }


    // =========================================================
    // HELPER: VALIDATE PRODUCT AVAILABILITY
    // =========================================================

    private void validateProductAvailability(
            Product product,
            int requestedQuantity) {

        if (!product.getIsAvailable()) {

            throw new InsufficientStockException(
                    product.getName() + " is not available"
            );
        }

        if (product.getStockQuantity()
                < requestedQuantity) {

            throw new InsufficientStockException(
                    "Insufficient stock for "
                            + product.getName()
                            + ". Available: "
                            + product.getStockQuantity()
                            + ", Requested: "
                            + requestedQuantity
            );
        }
    }


    // =========================================================
    // HELPER: MAP CART TO CART RESPONSE DTO
    // =========================================================

    private CartResponseDTO mapToCartResponseDTO(
            Cart cart) {

        List<CartItemResponseDTO> items =
                new ArrayList<>();

        for (CartItem cartItem :
                cart.getCartItems()) {

            items.add(
                    mapToCartItemResponseDTO(cartItem)
            );
        }

        return CartResponseDTO.builder()
                .id(cart.getId())
                .userId(cart.getUser().getId())
                .userName(cart.getUser().getFullName())
                .items(items)
                .totalItems(cart.getTotalItems())
                .totalAmount(cart.getTotalAmount())
                .createdAt(cart.getCreatedAt())
                .updatedAt(cart.getUpdatedAt())
                .build();
    }


    // =========================================================
    // HELPER: MAP CART ITEM TO RESPONSE DTO
    // =========================================================

    private CartItemResponseDTO mapToCartItemResponseDTO(
            CartItem cartItem) {

        Product product =
                cartItem.getProduct();

        return CartItemResponseDTO.builder()
                .id(cartItem.getId())
                .productId(product.getId())
                .productName(product.getName())
                .productImage(product.getImageUrl())
                .productSku(product.getSku())
                .unitPrice(product.getPrice())
                .quantity(cartItem.getQuantity())
                .subtotal(cartItem.getSubTotal())
                .availableStock(product.getStockQuantity())
                .addedAt(cartItem.getAddedAt())
                .build();
    }
}