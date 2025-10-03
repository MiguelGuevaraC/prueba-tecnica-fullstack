package com.sintad.prueba_tecnica_fullstack.shared.filter;

import jakarta.persistence.criteria.*;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Component;
import org.springframework.util.MultiValueMap;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;

@Component
public class FilterSpecBuilder<T> {

    public Specification<T> build(Class<T> type, Map<String, Operator> allowed, MultiValueMap<String, String> params) {
        return (root, query, cb) -> {
            List<Predicate> predicates = new ArrayList<>();

            allowed.forEach((field, operator) -> {
                String paramName = field.replace(".", "$"); 
                String value = params.getFirst(paramName);

                if (value == null) return;

                Path<?> path = getPath(root, field);

                switch (operator) {
                    case LIKE -> predicates.add(
                            cb.like(cb.lower(path.as(String.class)), "%" + value.toLowerCase() + "%"));

                    case EQ -> predicates.add(cb.equal(path, cast(path, value)));

                    case GT -> predicates.add(cb.greaterThan(
                            (Expression<? extends Comparable>) path,
                            (Comparable) cast(path, value)));

                    case GTE -> predicates.add(cb.greaterThanOrEqualTo(
                            (Expression<? extends Comparable>) path,
                            (Comparable) cast(path, value)));

                    case LT -> predicates.add(cb.lessThan(
                            (Expression<? extends Comparable>) path,
                            (Comparable) cast(path, value)));

                    case LTE -> predicates.add(cb.lessThanOrEqualTo(
                            (Expression<? extends Comparable>) path,
                            (Comparable) cast(path, value)));

                    case IN -> {
                        CriteriaBuilder.In<Object> in = cb.in(path);
                        Arrays.stream(value.split(","))
                                .map(v -> cast(path, v.trim()))
                                .forEach(in::value);
                        predicates.add(in);
                    }

                    case BETWEEN -> {
                        String from = params.getFirst(paramName + ".from");
                        String to = params.getFirst(paramName + ".to");

                        if (from != null && to != null) {
                            predicates.add(cb.between(
                                    (Expression<? extends Comparable>) path,
                                    (Comparable) cast(path, from),
                                    (Comparable) cast(path, to)
                            ));
                        } else if (from != null) {
                            predicates.add(cb.greaterThanOrEqualTo(
                                    (Expression<? extends Comparable>) path,
                                    (Comparable) cast(path, from)
                            ));
                        } else if (to != null) {
                            predicates.add(cb.lessThanOrEqualTo(
                                    (Expression<? extends Comparable>) path,
                                    (Comparable) cast(path, to)
                            ));
                        }
                    }

                    case DATE_EQ -> predicates.add(cb.equal(path.as(LocalDate.class), LocalDate.parse(value)));

                    default -> {}
                }
            });

            return cb.and(predicates.toArray(new Predicate[0]));
        };
    }

    private Path<?> getPath(Root<T> root, String field) {
        if (!field.contains(".")) return root.get(field);
        String[] parts = field.split("\\.");
        Path<?> path = root;
        for (String p : parts) path = path.get(p);
        return path;
    }

    private Object cast(Path<?> path, String value) {
        Class<?> type = path.getJavaType();
        try {
            if (type.equals(String.class)) return value;
            if (type.equals(Integer.class) || type.equals(int.class)) return Integer.valueOf(value);
            if (type.equals(Long.class) || type.equals(long.class)) return Long.valueOf(value);
            if (type.equals(Double.class) || type.equals(double.class)) return Double.valueOf(value);
            if (type.equals(BigDecimal.class)) return new BigDecimal(value);
            if (type.equals(Boolean.class) || type.equals(boolean.class)) return Boolean.valueOf(value);
            if (type.equals(LocalDate.class)) return LocalDate.parse(value);
            if (type.equals(LocalDateTime.class)) return LocalDateTime.parse(value);
            if (Enum.class.isAssignableFrom(type)) return Enum.valueOf((Class<Enum>) type, value);
        } catch (Exception e) {
            throw new IllegalArgumentException("No se pudo convertir el valor '" + value + "' a tipo " + type.getSimpleName());
        }
        return value;
    }
}
