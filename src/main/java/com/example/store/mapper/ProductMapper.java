package com.example.store.mapper;

import com.example.store.dto.ProductDTO;
import com.example.store.entity.Product;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;


/**
 * The interface Product mapper.
 */
@Mapper(componentModel = "spring")
public interface ProductMapper {

    /**
     * Maps a Product entity to ProductDTO.
     *
     * @param product the product entity
     * @return the product DTO
     */
    @Mapping(
            target = "orderIds",
            expression =
                    "java(product.getOrders() != null ? product.getOrders().stream().map(order -> order.getId()).collect(java.util.stream.Collectors.toList()) : java.util.Collections.emptyList())")
    ProductDTO productToProductDTO(Product product);

    /**
     * Maps a list of Product entities to a list of ProductDTOs.
     *
     * @param products the list of product entities
     * @return the list of product DTOs
     */
    List<ProductDTO> productsToProductDTOs(List<Product> products);

    /**
     * Maps a ProductDTO to Product entity (without orders).
     *
     * @param productDTO the product DTO
     * @return the product entity
     */
    @Mapping(target = "orders", ignore = true)
    Product productDTOToProduct(ProductDTO productDTO);
}
