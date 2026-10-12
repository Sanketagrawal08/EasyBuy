package in.divyansh.dto.response;


import lombok.*;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CartResponseDTO {

    private Long id;
    private Long userId;
    private String userName;
    private List<CartItemResponseDTO> items;
    private Integer totalItems;
    private double totalAmount;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}