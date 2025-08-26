package com.slooze.SDE.service;

import com.slooze.SDE.DTO.PaymentDto;
import com.slooze.SDE.model.Order;
import com.slooze.SDE.model.Payment;
import com.slooze.SDE.model.PaymentMethod;
import com.slooze.SDE.model.PaymentStatus;
import com.slooze.SDE.repository.PaymentRepository;
import com.slooze.SDE.repository.OrderRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.nio.file.AccessDeniedException;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class PaymentService {

    private final PaymentRepository paymentRepository;
    private final OrderRepository orderRepository;

    public PaymentDto getPaymentByOrderId(Long orderId, String role) throws Exception {
        if (!"ADMIN".equals(role)) {
            throw new AccessDeniedException("only admins are allowed to view payments");
        }

        Optional<Order> order = orderRepository.findById(orderId);
        if (order.isEmpty()) {
            throw new EntityNotFoundException("order not found");
        }

        Optional<Payment> payment = paymentRepository.findByOrder(order.get());
        if (payment.isEmpty()) {
            throw new EntityNotFoundException("payment not found for this order");
        }

        Payment paymentObject = payment.get();
        return new PaymentDto(paymentObject.getId(),
                paymentObject.getOrder(),
                paymentObject.getPaymentMethod(),
                paymentObject.getStatus());
    }

    public PaymentDto updatePayment(Long paymentId, PaymentMethod method, PaymentStatus status, String role) throws Exception {
        if (!"ADMIN".equals(role)) {
            throw new AccessDeniedException("only admins are allowed for payments");
        }

        Optional<Payment> payment = paymentRepository.findById(paymentId);

        if (payment.isPresent()) {
            if (method != null) payment.get().setPaymentMethod(method);
            if (status != null) payment.get().setStatus(status);

            Payment paymentObject = payment.get();
            paymentRepository.save(paymentObject);
            return new PaymentDto(paymentObject.getId(),
                    paymentObject.getOrder(),
                    paymentObject.getPaymentMethod(),
                    paymentObject.getStatus());
        } else {
            throw new EntityNotFoundException("payment not found");
        }

    }
}
