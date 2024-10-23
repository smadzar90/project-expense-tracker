package org.example.service;

import org.example.model.Category;
import org.example.repository.CategoryRepository;
import org.example.repository.base.EntityRepository;

import java.sql.Connection;
import java.util.List;
import java.util.Optional;

public class CategoryService {
    private final EntityRepository<Category> categoryRepository;

    public CategoryService(Connection connection) {
        categoryRepository =  new CategoryRepository(connection);
    }

    public List<Category> findAllCategories() {
        return categoryRepository.findAll();
    }

    public Optional<Category> getCategoryByID(Long id) {
        return categoryRepository.findByID(id);
    }
}
