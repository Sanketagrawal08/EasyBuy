package in.divyansh.service;

import in.divyansh.dto.request.AddToCartRequestDTO;
import in.divyansh.dto.request.UpdateCartItemRequestDTO;
import in.divyansh.dto.response.CartItemResponseDTO;
import in.divyansh.dto.response.CartResponseDTO;

public interface CartService {

    CartResponseDTO getCartByUserId(Long userId);

    CartResponseDTO addItemToCart(
            Long userId,
            AddToCartRequestDTO addToCartRequestDTO
    );

    CartResponseDTO updateCartItem(
            Long userId,
            Long cartItemId,
            UpdateCartItemRequestDTO updateCartItemRequestDTO
    );

    CartResponseDTO removeItemFromCart(
            Long userId,
            Long cartItemId
    );

    CartResponseDTO clearCart(Long userId);

    CartItemResponseDTO getCartItem(
            Long userId,
            Long cartItemId
    );

    boolean isProductInCart(
            Long userId,
            Long productId
    );

    CartResponseDTO incrementItemQuantity(
            Long userId,
            Long productId,
            int quantity
    );

    CartResponseDTO decrementItemQuantity(
            Long userId,
            Long productId,
            int quantity
    );
}