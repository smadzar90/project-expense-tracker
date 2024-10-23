package org.example.service;

import org.example.model.Category;
import org.example.model.PaymentMethod;
import org.example.repository.CategoryRepository;
import org.example.repository.PaymentRepository;
import org.example.repository.base.EntityRepository;

import java.sql.Connection;
import java.util.List;
import java.util.Optional;

public class PaymentService extends CategoryService {
    private final EntityRepository<PaymentMethod> paymentRepository;

    public PaymentService(Connection connection) {
        super(connection);
        paymentRepository = new PaymentRepository(connection);
    }

    public List<PaymentMethod> findAllPaymentMethods() {
        return paymentRepository.findAll();
    }

    public Optional<PaymentMethod> getPaymentMethodByID(Long id) {
        return paymentRepository.findByID(id);
    }
}
