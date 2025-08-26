package com.slooze.SDE.repository;

import com.slooze.SDE.model.Order;
import com.slooze.SDE.model.Payment;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface PaymentRepository extends JpaRepository<Payment, Long> {
    Optional<Payment> findByOrder(Order order);
}
