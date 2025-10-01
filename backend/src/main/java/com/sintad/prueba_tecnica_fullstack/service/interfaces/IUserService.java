package com.sintad.prueba_tecnica_fullstack.service.interfaces;

import com.sintad.prueba_tecnica_fullstack.dto.user.UserRequest;
import com.sintad.prueba_tecnica_fullstack.dto.user.UserResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;

public interface IUserService {

    UserResponse create(UserRequest request);

    UserResponse getById(Long id);

    UserResponse update(Long id, UserRequest request);

    void delete(Long id);

    Page<UserResponse> search(Specification<?> spec, Pageable pageable);
}
