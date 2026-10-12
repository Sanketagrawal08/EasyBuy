package in.divyansh.service.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import in.divyansh.dto.request.ProductRequestDTO;
import in.divyansh.dto.request.UpdateProductRequestDTO;
import in.divyansh.dto.response.PageResponseDTO;
import in.divyansh.dto.response.ProductResponseDTO;
import in.divyansh.entity.Product;
import in.divyansh.exception.DuplicateResourceException;
import in.divyansh.exception.ResourceNotFoundException;
import in.divyansh.repository.ProductRepository;
import in.divyansh.service.ProductService;
import jakarta.validation.ReportAsSingleViolation;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional
public class ProductServiceImpl implements ProductService {

    private final ProductRepository productRepository;


    // =========================================================
    // CREATE PRODUCT
    // =========================================================

    @Override
    public ProductResponseDTO createProduct(
            ProductRequestDTO productRequestDTO) {

        // Check duplicate SKU
        if (productRequestDTO.getSku() != null
                && productRepository.existsBySku(productRequestDTO.getSku())) {

            throw new DuplicateResourceException(
                    "Product",
                    "SKU",
                    productRequestDTO.getSku()
            );
        }

        Product product = Product.builder()
                .name(productRequestDTO.getName())
                .description(productRequestDTO.getDescription())
                .price(productRequestDTO.getPrice() != null
                        ? productRequestDTO.getPrice()
                        : 0.0)
                .stockQuantity(productRequestDTO.getStockQuantity())
                .category(productRequestDTO.getCategory())
                .brand(productRequestDTO.getBrand())
                .imageUrl(productRequestDTO.getImageUrl())
                .sku(productRequestDTO.getSku())
                .isAvailable(productRequestDTO.getIsAvailable() != null
                        ? productRequestDTO.getIsAvailable()
                        : true)
                .build();

        Product savedProduct = productRepository.save(product);

        return mapToResponseDTO(savedProduct);
    }


    // =========================================================
    // GET PRODUCT BY ID
    // =========================================================

    @Override
    public ProductResponseDTO getProductById(Long id) {

        Product product = findProductById(id);

        return mapToResponseDTO(product);
    }


    // =========================================================
    // GET PRODUCT BY SKU
    // =========================================================

    @Override
    public ProductResponseDTO getProductBySku(String sku) {

        Optional<Product> productOptional =
                productRepository.findBySku(sku);

        if (productOptional.isPresent()) {

            return mapToResponseDTO(productOptional.get());

        } else {

            throw new ResourceNotFoundException(
                    "Product",
                    "SKU",
                    sku
            );
        }
    }


    // =========================================================
    // GET ALL PRODUCTS
    // =========================================================

    @Override
    public List<ProductResponseDTO> getAllProducts() {

        List<Product> products =
                productRepository.findAll();

        List<ProductResponseDTO> responseList =
                new ArrayList<>();

        for (Product product : products) {

            responseList.add(
                    mapToResponseDTO(product)
            );
        }

        return responseList;
    }


    // =========================================================
    // GET ALL PRODUCTS - PAGINATED
    // =========================================================

    @Override
    public PageResponseDTO<ProductResponseDTO> getAllProductsPaginated(
            int page,
            int size,
            String sortBy,
            String sortDir) {

        Pageable pageable =
                createPageable(page, size, sortBy, sortDir);

        Page<Product> productPage =
                productRepository.findAll(pageable);

        return mapToPageResponse(productPage);
    }


    // =========================================================
    // GET AVAILABLE PRODUCTS
    // =========================================================

    @Override
    public List<ProductResponseDTO> getAvailableProducts() {

        List<Product> products =
                productRepository.findByIsAvailableTrue();

        List<ProductResponseDTO> responseList =
                new ArrayList<>();

        for (Product product : products) {

            responseList.add(
                    mapToResponseDTO(product)
            );
        }

        return responseList;
    }


    // =========================================================
    // GET PRODUCTS BY CATEGORY
    // =========================================================

    @Override
    public List<ProductResponseDTO> getProductsByCategory(
            String category) {

        List<Product> products =
                productRepository.findByCategoryIgnoreCase(category);

        List<ProductResponseDTO> responseList =
                new ArrayList<>();

        for (Product product : products) {

            responseList.add(
                    mapToResponseDTO(product)
            );
        }

        return responseList;
    }


    // =========================================================
    // GET PRODUCTS BY PRICE RANGE
    // =========================================================

    @Override
    public List<ProductResponseDTO> getProductsByPriceRange(
            double minPrice,
            double maxPrice) {

        List<Product> products =
                productRepository.findByPriceBetween(
                        minPrice,
                        maxPrice
                );

        List<ProductResponseDTO> responseList =
                new ArrayList<>();

        for (Product product : products) {

            responseList.add(
                    mapToResponseDTO(product)
            );
        }

        return responseList;
    }


    // =========================================================
    // SEARCH PRODUCTS
    // =========================================================

    @Override
    public PageResponseDTO<ProductResponseDTO> searchProducts(
            String keyword,
            int page,
            int size) {

        Pageable pageable =
                PageRequest.of(page, size);
    Page<Product> product=productRepository.searchProducts(keyword, pageable);
         return mapToPageResponse(product);
    }


    // =========================================================
    // UPDATE PRODUCT
    // =========================================================

    @Override
    public ProductResponseDTO updateProduct(
            Long id,
            UpdateProductRequestDTO dto) {

        Product product = findProductById(id);

        // At least one field must be provided
        if (dto.getName() == null
                && dto.getDescription() == null
                && dto.getPrice() == null
                && dto.getStockQuantity() == null
                && dto.getCategory() == null
                && dto.getBrand() == null
                && dto.getImageUrl() == null
                && dto.getSku() == null
                && dto.getIsAvailable() == null) {

            throw new IllegalArgumentException(
                    "At least one field must be provided for update"
            );
        }


        // -------------------------
        // Name
        // -------------------------

        if (dto.getName() != null) {

            if (dto.getName().isBlank()) {

                throw new IllegalArgumentException(
                        "Product name cannot be blank"
                );
            }

            product.setName(dto.getName().trim());
        }


        // -------------------------
        // Description
        // -------------------------

        if (dto.getDescription() != null) {

            product.setDescription(
                    dto.getDescription().trim()
            );
        }


        // -------------------------
        // Price
        // -------------------------

        if (dto.getPrice() != null) {

            if (dto.getPrice() < 0) {

                throw new IllegalArgumentException(
                        "Price cannot be negative"
                );
            }

            product.setPrice(dto.getPrice());
        }


        // -------------------------
        // Stock
        // -------------------------

        if (dto.getStockQuantity() != null) {

            if (dto.getStockQuantity() < 0) {

                throw new IllegalArgumentException(
                        "Stock quantity cannot be negative"
                );
            }

            product.setStockQuantity(
                    dto.getStockQuantity()
            );
        }


        // -------------------------
        // Category
        // -------------------------

        if (dto.getCategory() != null) {

            product.setCategory(
                    dto.getCategory().trim()
            );
        }


        // -------------------------
        // Brand
        // -------------------------

        if (dto.getBrand() != null) {

            product.setBrand(
                    dto.getBrand().trim()
            );
        }


        // -------------------------
        // Image URL
        // -------------------------

        if (dto.getImageUrl() != null) {

            product.setImageUrl(
                    dto.getImageUrl().trim()
            );
        }


        // -------------------------
        // SKU
        // -------------------------

        if (dto.getSku() != null) {

            if (dto.getSku().isBlank()) {

                throw new IllegalArgumentException(
                        "SKU cannot be blank"
                );
            }

            String newSku = dto.getSku().trim();

            if (!newSku.equals(product.getSku())
                    && productRepository.existsBySku(newSku)) {

                throw new DuplicateResourceException(
                        "Product",
                        "SKU",
                        newSku
                );
            }

            product.setSku(newSku);
        }


        // -------------------------
        // Availability
        // -------------------------

        if (dto.getIsAvailable() != null) {

            product.setIsAvailable(
                    dto.getIsAvailable()
            );
        }


        Product updatedProduct =
                productRepository.save(product);

        return mapToResponseDTO(updatedProduct);
    }


    // =========================================================
    // UPDATE STOCK
    // =========================================================

    @Override
    public void updateStock(
            Long productId,
            Integer quantity) {

        Product product =
                findProductById(productId);

        int newStock =
                product.getStockQuantity() + quantity;

        if (newStock < 0) {

            throw new IllegalArgumentException(
                    "Stock cannot be negative. Current stock: "
                    + product.getStockQuantity()
            );
        }

        product.setStockQuantity(newStock);

        productRepository.save(product);
    }


    // =========================================================
    // GET LOW STOCK PRODUCTS
    // =========================================================

    @Override
    public List<ProductResponseDTO> getLowStockProducts(
            Integer threshold) {

        List<Product> products =
                productRepository.findLowStockProducts(
                        threshold
                );

        List<ProductResponseDTO> responseList =
                new ArrayList<>();

        for (Product product : products) {

            responseList.add(
                    mapToResponseDTO(product)
            );
        }

        return responseList;
    }


    // =========================================================
    // CHECK SKU
    // =========================================================

    @Override
    public boolean existsBySku(String sku) {

        return productRepository.existsBySku(sku);
    }


    // =========================================================
    // HELPER: FIND PRODUCT
    // =========================================================

    private Product findProductById(Long id) {

        Optional<Product> productOptional =
                productRepository.findById(id);

        if (productOptional.isPresent()) {

            return productOptional.get();

        } else {

            throw new ResourceNotFoundException(
                    "Product",
                    "id",
                    id
            );
        }
    }


    // =========================================================
    // HELPER: CREATE PAGEABLE
    // =========================================================

    private Pageable createPageable(
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

        return PageRequest.of(
                page,
                size,
                sort
        );
    }


    // =========================================================
    // HELPER: PRODUCT → RESPONSE DTO
    // =========================================================

    private ProductResponseDTO mapToResponseDTO(
            Product product) {

        return ProductResponseDTO.builder()
                .id(product.getId())
                .name(product.getName())
                .description(product.getDescription())
                .price(product.getPrice())
                .stockQuantity(product.getStockQuantity())
                .category(product.getCategory())
                .brand(product.getBrand())
                .imageUrl(product.getImageUrl())
                .sku(product.getSku())
                .isAvailable(product.getIsAvailable())
                .inStock(product.getStockQuantity() > 0)
                .build();
    }


    // =========================================================
    // HELPER: PAGE → PAGE RESPONSE DTO
    // =========================================================

    private PageResponseDTO<ProductResponseDTO> mapToPageResponse(
            Page<Product> productPage) {

        List<ProductResponseDTO> products =
                new ArrayList<>();

        for (Product product : productPage.getContent()) {

            products.add(
                    mapToResponseDTO(product)
            );
        }

        return PageResponseDTO.<ProductResponseDTO>builder()
                .content(products)
                .pageNumber(productPage.getNumber())
                .pageSize(productPage.getSize())
                .totalElements(productPage.getTotalElements())
                .totalPages(productPage.getTotalPages())
                .first(productPage.isFirst())
                .last(productPage.isLast())
                .hasNext(productPage.hasNext())
                .hasPrevious(productPage.hasPrevious())
                .build();
    }


	
}