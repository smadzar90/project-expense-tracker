package org.example.service;

import org.example.model.Category;
import org.example.repository.CategoryRepository;

import java.sql.Connection;

public class CategoryService {
    private final CategoryRepository categoryRepository;

    public CategoryService(Connection connection) {
        categoryRepository =  new CategoryRepository(connection);
    }
}
