package org.example.controller;

import org.example.controller.base.EntityController;
import org.example.model.PaymentMethod;
import org.example.service.PaymentService;

import java.util.List;
import java.util.Optional;

public class PaymentController extends EntityController<PaymentMethod> {
    private final PaymentService service;

    public PaymentController(PaymentService service) {
        this.service = service;
    }

    @Override
    protected String getPath() {
        return "/api/payment/method";
    }

    @Override
    protected String getName() {
        return "Payment Method";
    }

    @Override
    protected List<PaymentMethod> findAll() {
        return service.findAllPaymentMethods();
    }

    @Override
    protected Optional<PaymentMethod> findByID(Long id) {
        return service.getPaymentMethodByID(id);
    }
}
