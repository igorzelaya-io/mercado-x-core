package hn.alturaforge.mercadox.core.controller;


import hn.alturaforge.mercadox.library.entity.model.core.Category;
import hn.alturaforge.mercadox.library.entity.ports.incoming.CategoryUseCase;
import hn.alturaforge.mercadox.library.entity.response.BaseResponseDto;
import hn.alturaforge.mercadox.library.entity.response.Response;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/v1/categories")
@RequiredArgsConstructor
@PreAuthorize("permitAll()")
public class CategoryController {

    private final CategoryUseCase categoryService;

    @GetMapping
    public ResponseEntity<? extends Response<List<Category>>> getAllCategories() {
        BaseResponseDto<List<Category>> response = new BaseResponseDto<>();
        List<Category> categories = categoryService.findAll();
        return response.buildResponseEntity(HttpStatus.OK, "Categories retrieved successfully.", categories);
    }


}
