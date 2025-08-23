package com.slooze.SDE.repository;

import com.slooze.SDE.model.Order;
import com.slooze.SDE.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface OrderRepository extends JpaRepository<Order, Long> {
    List<Order> findByUser(User user);
}
