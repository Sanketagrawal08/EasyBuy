package in.divyansh.service;

import java.util.List;

import in.divyansh.dto.request.ProductRequestDTO;
import in.divyansh.dto.request.UpdateProductRequestDTO;
import in.divyansh.dto.response.PageResponseDTO;
import in.divyansh.dto.response.ProductResponseDTO;

public interface ProductService {

    ProductResponseDTO createProduct(ProductRequestDTO productRequestDTO);

    ProductResponseDTO getProductById(Long id);

    ProductResponseDTO getProductBySku(String sku);

    List<ProductResponseDTO> getAllProducts();

    PageResponseDTO<ProductResponseDTO> getAllProductsPaginated(
            int page,
            int size,
            String sortBy,
            String sortDir
    );

    List<ProductResponseDTO> getAvailableProducts();

    List<ProductResponseDTO> getProductsByCategory(String category);

    List<ProductResponseDTO> getProductsByPriceRange(
            double minPrice,
            double maxPrice
    );

    PageResponseDTO<ProductResponseDTO> searchProducts(
            String keyword,
            int page,
            int size
    );

    ProductResponseDTO updateProduct(
            Long id,
            UpdateProductRequestDTO dto
    );

    void updateStock(Long productId, Integer quantity);

    List<ProductResponseDTO> getLowStockProducts(Integer threshold);

    boolean existsBySku(String sku);
}