package org.example.controller;

import org.example.controller.base.EntityController;
import org.example.model.Category;
import org.example.service.CategoryService;

import java.util.List;
import java.util.Optional;

public class CategoryController extends EntityController<Category> {
    private final CategoryService service;

    public CategoryController(CategoryService service) {
        this.service = service;
    }

    @Override
    protected String getPath() {
        return "/api/category";
    }

    @Override
    protected String getName() {
        return "Category";
    }

    @Override
    protected List<Category> findAll() {
        return service.findAllCategories();
    }

    @Override
    protected Optional<Category> findByID(Long id) {
        return service.getCategoryByID(id);
    }
}

