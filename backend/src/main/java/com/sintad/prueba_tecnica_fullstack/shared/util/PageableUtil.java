package com.sintad.prueba_tecnica_fullstack.shared.util;

import com.sintad.prueba_tecnica_fullstack.shared.dto.BaseListRequest;
import com.sintad.prueba_tecnica_fullstack.shared.dto.PageableRequest;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

public class PageableUtil {

    public static Pageable from(PageableRequest request) {
        int page = (request.getPage() != null && request.getPage() > 0) ? request.getPage() - 1 : 0;
        int size = (request.getPerPage() != null && request.getPerPage() > 0) ? request.getPerPage() : 10;

        String sortField = (request.getSort() != null && !request.getSort().isBlank()) ? request.getSort() : "id";
        String direction = (request.getDirection() != null && !request.getDirection().isBlank()) ? request.getDirection() : "asc";

        Sort sort = Sort.by(Sort.Direction.fromString(direction), sortField);
        return PageRequest.of(page, size, sort);
    }

    public static Pageable fromListRequest(BaseListRequest request) {
    int page = (request.getPage() != null && request.getPage() > 0) ? request.getPage() - 1 : 0;
    int size = (request.getPer_page() != null && request.getPer_page() > 0) ? request.getPer_page() : 10;

    String sortField = (request.getSort() != null && !request.getSort().isBlank()) ? request.getSort() : "id";
    String direction = (request.getDirection() != null && !request.getDirection().isBlank()) ? request.getDirection() : "asc";

    Sort sort = Sort.by(Sort.Direction.fromString(direction), sortField);
    return PageRequest.of(page, size, sort);
}

}
