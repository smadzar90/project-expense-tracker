package org.example.service;

import org.example.model.Category;
import org.example.model.PaymentMethod;
import org.example.repository.CategoryRepository;
import org.example.repository.PaymentRepository;

import java.sql.Connection;

public class PaymentService {
    private final PaymentRepository paymentRepository;

    public PaymentService(Connection connection) {
        paymentRepository =  new PaymentRepository(connection);
    }
}
