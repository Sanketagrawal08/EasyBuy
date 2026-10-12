package in.divyansh.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import in.divyansh.dto.request.ProductRequestDTO;
import in.divyansh.dto.request.UpdateProductRequestDTO;
import in.divyansh.dto.response.ApiResponseDTO;
import in.divyansh.dto.response.PageResponseDTO;
import in.divyansh.dto.response.ProductResponseDTO;
import in.divyansh.service.ProductService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/products")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class ProductController {

    private final ProductService productService;


    // =========================================================
    // CREATE PRODUCT
    // =========================================================

    @PostMapping
    public ResponseEntity<ApiResponseDTO<ProductResponseDTO>> createProduct(
            @Valid @RequestBody ProductRequestDTO productRequestDTO) {

        ProductResponseDTO product =
                productService.createProduct(productRequestDTO);

        return new ResponseEntity<>(
                ApiResponseDTO.success(
                        "Product created successfully",
                        product
                ),
                HttpStatus.CREATED
        );
    }


    // =========================================================
    // GET ALL PRODUCTS
    // =========================================================

    @GetMapping
    public ResponseEntity<ApiResponseDTO<List<ProductResponseDTO>>> getAllProducts() {

        List<ProductResponseDTO> products =
                productService.getAllProducts();

        return ResponseEntity.ok(
                ApiResponseDTO.success(
                        "Products fetched successfully",
                        products
                )
        );
    }


    // =========================================================
    // GET ALL PRODUCTS - PAGINATED
    // =========================================================

    @GetMapping("/paginated")
    public ResponseEntity<ApiResponseDTO<PageResponseDTO<ProductResponseDTO>>>
            getAllProductsPaginated(

                    @RequestParam(defaultValue = "0")
                    int page,

                    @RequestParam(defaultValue = "10")
                    int size,

                    @RequestParam(defaultValue = "id")
                    String sortBy,

                    @RequestParam(defaultValue = "asc")
                    String sortDir) {

        PageResponseDTO<ProductResponseDTO> products =
                productService.getAllProductsPaginated(
                        page,
                        size,
                        sortBy,
                        sortDir
                );

        return ResponseEntity.ok(
                ApiResponseDTO.success(
                        "Products fetched successfully",
                        products
                )
        );
    }


    // =========================================================
    // GET PRODUCT BY ID
    // =========================================================

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponseDTO<ProductResponseDTO>> getProductById(
            @PathVariable Long id) {

        ProductResponseDTO product =
                productService.getProductById(id);

        return ResponseEntity.ok(
                ApiResponseDTO.success(product)
        );
    }


    // =========================================================
    // GET PRODUCT BY SKU
    // =========================================================

    @GetMapping("/sku/{sku}")
    public ResponseEntity<ApiResponseDTO<ProductResponseDTO>> getProductBySku(
            @PathVariable String sku) {

        ProductResponseDTO product =
                productService.getProductBySku(sku);

        return ResponseEntity.ok(
                ApiResponseDTO.success(product)
        );
    }


    // =========================================================
    // GET AVAILABLE PRODUCTS
    // =========================================================

    @GetMapping("/available")
    public ResponseEntity<ApiResponseDTO<List<ProductResponseDTO>>>
            getAvailableProducts() {

        List<ProductResponseDTO> products =
                productService.getAvailableProducts();

        return ResponseEntity.ok(
                ApiResponseDTO.success(
                        "Available products fetched successfully",
                        products
                )
        );
    }


    // =========================================================
    // GET PRODUCTS BY CATEGORY
    // =========================================================

    @GetMapping("/category/{category}")
    public ResponseEntity<ApiResponseDTO<List<ProductResponseDTO>>>
            getProductsByCategory(
                    @PathVariable String category) {

        List<ProductResponseDTO> products =
                productService.getProductsByCategory(category);

        return ResponseEntity.ok(
                ApiResponseDTO.success(
                        "Products fetched successfully",
                        products
                )
        );
    }


    // =========================================================
    // GET PRODUCTS BY PRICE RANGE
    // =========================================================

    @GetMapping("/price-range")
    public ResponseEntity<ApiResponseDTO<List<ProductResponseDTO>>>
            getProductsByPriceRange(

                    @RequestParam double minPrice,

                    @RequestParam double maxPrice) {

        List<ProductResponseDTO> products =
                productService.getProductsByPriceRange(
                        minPrice,
                        maxPrice
                );

        return ResponseEntity.ok(
                ApiResponseDTO.success(
                        "Products fetched successfully",
                        products
                )
        );
    }


    // =========================================================
    // SEARCH PRODUCTS
    // =========================================================

    @GetMapping("/search")
    public ResponseEntity<ApiResponseDTO<PageResponseDTO<ProductResponseDTO>>>
            searchProducts(

                    @RequestParam String keyword,

                    @RequestParam(defaultValue = "0")
                    int page,

                    @RequestParam(defaultValue = "10")
                    int size) {

        PageResponseDTO<ProductResponseDTO> products =
                productService.searchProducts(
                        keyword,
                        page,
                        size
                );

        return ResponseEntity.ok(
                ApiResponseDTO.success(
                        "Products searched successfully",
                        products
                )
        );
    }


    // =========================================================
    // GET LOW STOCK PRODUCTS
    // =========================================================

    @GetMapping("/low-stock")
    public ResponseEntity<ApiResponseDTO<List<ProductResponseDTO>>>
            getLowStockProducts(

                    @RequestParam(defaultValue = "10")
                    Integer threshold) {

        List<ProductResponseDTO> products =
                productService.getLowStockProducts(threshold);

        return ResponseEntity.ok(
                ApiResponseDTO.success(
                        "Low stock products fetched successfully",
                        products
                )
        );
    }


    // =========================================================
    // CHECK SKU
    // =========================================================

    @GetMapping("/exists/{sku}")
    public ResponseEntity<ApiResponseDTO<Boolean>> existsBySku(
            @PathVariable String sku) {

        boolean exists =
                productService.existsBySku(sku);

        return ResponseEntity.ok(
                ApiResponseDTO.success(
                        exists
                )
        );
    }


    // =========================================================
    // UPDATE PRODUCT
    // =========================================================

    @PatchMapping("/{id}")
    public ResponseEntity<ApiResponseDTO<ProductResponseDTO>>
            updateProduct(

                    @PathVariable Long id,

                    @Valid @RequestBody
                    UpdateProductRequestDTO dto) {

        ProductResponseDTO product =
                productService.updateProduct(id, dto);

        return ResponseEntity.ok(
                ApiResponseDTO.success(
                        "Product updated successfully",
                        product
                )
        );
    }


    // =========================================================
    // UPDATE STOCK
    // =========================================================

    @PatchMapping("/{id}/stock")
    public ResponseEntity<ApiResponseDTO<String>> updateStock(

            @PathVariable Long id,

            @RequestParam Integer quantity) {

        productService.updateStock(id, quantity);

        return ResponseEntity.ok(
                ApiResponseDTO.success(
                        "Product stock updated successfully",
                        null
                )
        );
    }
}