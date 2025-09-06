package com.example.store.dto;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/** The type Paged response test. */
@ExtendWith(MockitoExtension.class)
@DisplayName("PagedResponse Tests")
class PagedResponseTest {

    /** Should create paged response successfully. */
    @Test
    @DisplayName("Should create paged response successfully")
    void shouldCreatePagedResponseSuccessfully() {
        // Given
        List<String> content = Arrays.asList("item1", "item2", "item3");
        Page<String> page = new PageImpl<>(content, PageRequest.of(0, 10), 3);
        String sortBy = "name";
        String sortOrder = "asc";

        // When
        PagedResponse<String> result = PagedResponse.of(page, sortBy, sortOrder);

        // Then
        assertNotNull(result);
        assertEquals(content, result.getContent());
        assertEquals(0, result.getPage());
        assertEquals(10, result.getSize());
        assertEquals(3, result.getTotalElements());
        assertEquals(1, result.getTotalPages());
        assertTrue(result.isFirst());
        assertTrue(result.isLast());
        assertFalse(result.isHasNext());
        assertFalse(result.isHasPrevious());
        assertEquals(sortBy, result.getSortBy());
        assertEquals(sortOrder, result.getSortOrder());
    }

    /** Should create paged response with multiple pages. */
    @Test
    @DisplayName("Should create paged response with multiple pages")
    void shouldCreatePagedResponseWithMultiplePages() {
        // Given
        List<String> content = Arrays.asList("item1", "item2");
        Page<String> page = new PageImpl<>(content, PageRequest.of(1, 2), 5);
        String sortBy = "id";
        String sortOrder = "desc";

        // When
        PagedResponse<String> result = PagedResponse.of(page, sortBy, sortOrder);

        // Then
        assertNotNull(result);
        assertEquals(content, result.getContent());
        assertEquals(1, result.getPage());
        assertEquals(2, result.getSize());
        assertEquals(5, result.getTotalElements());
        assertEquals(3, result.getTotalPages());
        assertFalse(result.isFirst());
        assertFalse(result.isLast());
        assertTrue(result.isHasNext());
        assertTrue(result.isHasPrevious());
        assertEquals(sortBy, result.getSortBy());
        assertEquals(sortOrder, result.getSortOrder());
    }

    /** Should create paged response with empty content. */
    @Test
    @DisplayName("Should create paged response with empty content")
    void shouldCreatePagedResponseWithEmptyContent() {
        // Given
        List<String> content = Arrays.asList();
        Page<String> page = new PageImpl<>(content, PageRequest.of(0, 10), 0);
        String sortBy = "name";
        String sortOrder = "asc";

        // When
        PagedResponse<String> result = PagedResponse.of(page, sortBy, sortOrder);

        // Then
        assertNotNull(result);
        assertEquals(content, result.getContent());
        assertEquals(0, result.getPage());
        assertEquals(10, result.getSize());
        assertEquals(0, result.getTotalElements());
        assertEquals(0, result.getTotalPages());
        assertTrue(result.isFirst());
        assertTrue(result.isLast());
        assertFalse(result.isHasNext());
        assertFalse(result.isHasPrevious());
        assertEquals(sortBy, result.getSortBy());
        assertEquals(sortOrder, result.getSortOrder());
    }

    /** Should create paged response with null sort parameters. */
    @Test
    @DisplayName("Should create paged response with null sort parameters")
    void shouldCreatePagedResponseWithNullSortParameters() {
        // Given
        List<String> content = Arrays.asList("item1", "item2", "item3");
        Page<String> page = new PageImpl<>(content, PageRequest.of(0, 10), 3);
        String sortBy = null;
        String sortOrder = null;

        // When
        PagedResponse<String> result = PagedResponse.of(page, sortBy, sortOrder);

        // Then
        assertNotNull(result);
        assertEquals(content, result.getContent());
        assertEquals(0, result.getPage());
        assertEquals(10, result.getSize());
        assertEquals(3, result.getTotalElements());
        assertEquals(1, result.getTotalPages());
        assertTrue(result.isFirst());
        assertTrue(result.isLast());
        assertFalse(result.isHasNext());
        assertFalse(result.isHasPrevious());
        assertNull(result.getSortBy());
        assertNull(result.getSortOrder());
    }

    /** Should create paged response with large dataset. */
    @Test
    @DisplayName("Should create paged response with large dataset")
    void shouldCreatePagedResponseWithLargeDataset() {
        // Given
        List<String> content = Arrays.asList("item1", "item2", "item3", "item4", "item5");
        Page<String> page = new PageImpl<>(content, PageRequest.of(2, 5), 25);
        String sortBy = "createdDate";
        String sortOrder = "desc";

        // When
        PagedResponse<String> result = PagedResponse.of(page, sortBy, sortOrder);

        // Then
        assertNotNull(result);
        assertEquals(content, result.getContent());
        assertEquals(2, result.getPage());
        assertEquals(5, result.getSize());
        assertEquals(25, result.getTotalElements());
        assertEquals(5, result.getTotalPages());
        assertFalse(result.isFirst());
        assertFalse(result.isLast());
        assertTrue(result.isHasNext());
        assertTrue(result.isHasPrevious());
        assertEquals(sortBy, result.getSortBy());
        assertEquals(sortOrder, result.getSortOrder());
    }

    /** Should create paged response with single page. */
    @Test
    @DisplayName("Should create paged response with single page")
    void shouldCreatePagedResponseWithSinglePage() {
        // Given
        List<String> content = Arrays.asList("item1", "item2");
        Page<String> page = new PageImpl<>(content, PageRequest.of(0, 10), 2);
        String sortBy = "name";
        String sortOrder = "asc";

        // When
        PagedResponse<String> result = PagedResponse.of(page, sortBy, sortOrder);

        // Then
        assertNotNull(result);
        assertEquals(content, result.getContent());
        assertEquals(0, result.getPage());
        assertEquals(10, result.getSize());
        assertEquals(2, result.getTotalElements());
        assertEquals(1, result.getTotalPages());
        assertTrue(result.isFirst());
        assertTrue(result.isLast());
        assertFalse(result.isHasNext());
        assertFalse(result.isHasPrevious());
        assertEquals(sortBy, result.getSortBy());
        assertEquals(sortOrder, result.getSortOrder());
    }

    /** Should create paged response with last page. */
    @Test
    @DisplayName("Should create paged response with last page")
    void shouldCreatePagedResponseWithLastPage() {
        // Given
        List<String> content = Arrays.asList("item1");
        Page<String> page = new PageImpl<>(content, PageRequest.of(2, 2), 5);
        String sortBy = "id";
        String sortOrder = "desc";

        // When
        PagedResponse<String> result = PagedResponse.of(page, sortBy, sortOrder);

        // Then
        assertNotNull(result);
        assertEquals(content, result.getContent());
        assertEquals(2, result.getPage());
        assertEquals(2, result.getSize());
        assertEquals(5, result.getTotalElements());
        assertEquals(3, result.getTotalPages());
        assertFalse(result.isFirst());
        assertTrue(result.isLast());
        assertFalse(result.isHasNext());
        assertTrue(result.isHasPrevious());
        assertEquals(sortBy, result.getSortBy());
        assertEquals(sortOrder, result.getSortOrder());
    }

    /** Should create paged response with first page. */
    @Test
    @DisplayName("Should create paged response with first page")
    void shouldCreatePagedResponseWithFirstPage() {
        // Given
        List<String> content = Arrays.asList("item1", "item2");
        Page<String> page = new PageImpl<>(content, PageRequest.of(0, 2), 5);
        String sortBy = "name";
        String sortOrder = "asc";

        // When
        PagedResponse<String> result = PagedResponse.of(page, sortBy, sortOrder);

        // Then
        assertNotNull(result);
        assertEquals(content, result.getContent());
        assertEquals(0, result.getPage());
        assertEquals(2, result.getSize());
        assertEquals(5, result.getTotalElements());
        assertEquals(3, result.getTotalPages());
        assertTrue(result.isFirst());
        assertFalse(result.isLast());
        assertTrue(result.isHasNext());
        assertFalse(result.isHasPrevious());
        assertEquals(sortBy, result.getSortBy());
        assertEquals(sortOrder, result.getSortOrder());
    }
}
