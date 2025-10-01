package com.sintad.prueba_tecnica_fullstack.shared.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;
import org.springframework.data.domain.Page;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;
import org.springframework.web.util.UriComponentsBuilder;

import jakarta.servlet.http.HttpServletRequest;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Respuesta paginada estilo Laravel con filtros")
public class PageResponse<T> {

    private List<T> data;
    private Links links;
    private Meta meta;

    public static <T> PageResponse<T> from(Page<T> page, HttpServletRequest request) {
        int currentPage = page.getNumber() + 1;
        int lastPage = Math.max(page.getTotalPages(), 1);

        // Usa la URL actual con query params incluidos
        UriComponentsBuilder ub = ServletUriComponentsBuilder.fromRequest(request);

        String first = ub.replaceQueryParam("page", 1).toUriString();
        String last = ub.replaceQueryParam("page", lastPage).toUriString();
        String prev = currentPage > 1 ? ub.replaceQueryParam("page", currentPage - 1).toUriString() : null;
        String next = currentPage < lastPage ? ub.replaceQueryParam("page", currentPage + 1).toUriString() : null;

        // Path base sin query (para meta.path)
        String path = ServletUriComponentsBuilder.fromRequestUri(request).toUriString();

        Links links = Links.builder()
                .first(first)
                .last(last)
                .prev(prev)
                .next(next)
                .build();

        Meta meta = Meta.builder()
                .currentPage(currentPage)
                .from(page.getNumberOfElements() > 0 ? ((currentPage - 1) * page.getSize()) + 1 : 0)
                .lastPage(lastPage)
                .path(path)
                .perPage(page.getSize())
                .to(((currentPage - 1) * page.getSize()) + page.getNumberOfElements())
                .total(page.getTotalElements())
                .build();

        return PageResponse.<T>builder()
                .data(page.getContent())
                .links(links)
                .meta(meta)
                .build();
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Links {
        private String first;
        private String last;
        private String prev;
        private String next;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Meta {
        private int currentPage;
        private int from;
        private int lastPage;
        private String path;
        private int perPage;
        private int to;
        private long total;
    }
}
