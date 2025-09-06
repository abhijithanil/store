package com.example.store.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * Generic paged response DTO following SOLID principles. Single Responsibility: Represents paginated data structure.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class PagedResponse<T> {
    private List<T> content;
    private int page;
    private int size;
    private long totalElements;
    private int totalPages;
    private boolean first;
    private boolean last;
    private boolean hasNext;
    private boolean hasPrevious;
    private String sortBy;
    private String sortOrder;

    /**
     * Creates a PagedResponse from Spring Data Page object.
     *
     * @param page the Spring Data Page object
     * @param sortBy the sort field
     * @param sortOrder the sort direction
     * @return PagedResponse with pagination metadata
     */
    public static <T> PagedResponse<T> of(
            org.springframework.data.domain.Page<T> page, String sortBy, String sortOrder) {
        return new PagedResponse<>(
                page.getContent(),
                page.getNumber(),
                page.getSize(),
                page.getTotalElements(),
                page.getTotalPages(),
                page.isFirst(),
                page.isLast(),
                page.hasNext(),
                page.hasPrevious(),
                sortBy,
                sortOrder);
    }
}
