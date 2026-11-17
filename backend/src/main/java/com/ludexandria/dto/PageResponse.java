package com.ludexandria.dto;

import java.util.List;

public record PageResponse<T>(
        List<T> content,
        int page,
        int size,
        long totalElements,
        long totalPages,
        boolean hasNext) {
    public static <T> PageResponse<T> of(List<T> content, int page, int size, long totalElements) {
        long totalPages = totalElements == 0 ? 0 : (totalElements - 1) / size + 1;
        return new PageResponse<>(content, page, size, totalElements, totalPages, page + 1L < totalPages);
    }
}
