package com.slooze.SDE.service;

import com.slooze.SDE.DTO.PaymentDto;
import com.slooze.SDE.DTO.OrderDto;
import com.slooze.SDE.DTO.RestaurantDto;
import com.slooze.SDE.DTO.MenuItemDto;
import com.slooze.SDE.DTO.OrderItemDto;
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
import java.util.stream.Collectors;

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
        Order orderObject = paymentObject.getOrder();
        
        // Convert to OrderDto to avoid circular references
        OrderDto orderDto = convertToOrderDto(orderObject);
        
        return new PaymentDto(paymentObject.getId(),
                orderDto,
                paymentObject.getPaymentMethod(),
                paymentObject.getStatus());
    }

    public PaymentDto updatePayment(Long paymentId, PaymentMethod method, PaymentStatus status, String role) throws Exception {
        if (!"ADMIN".equals(role)) {
            throw new AccessDeniedException("only admins are allowed for payments");
        }

        Optional<Payment> payment = paymentRepository.findById(paymentId);

        if (payment.isPresent()) {
            Payment paymentObject = payment.get();
            
            // Log current state
            System.out.println("Updating payment ID: " + paymentId);
            System.out.println("Current method: " + paymentObject.getPaymentMethod());
            System.out.println("Current status: " + paymentObject.getStatus());
            System.out.println("New method: " + method);
            System.out.println("New status: " + status);
            
            if (method != null) {
                paymentObject.setPaymentMethod(method);
                System.out.println("Updated method to: " + method);
            }
            if (status != null) {
                paymentObject.setStatus(status);
                System.out.println("Updated status to: " + status);
            }

            paymentRepository.save(paymentObject);
            System.out.println("Payment saved successfully");
            
            // Convert to OrderDto to avoid circular references
            OrderDto orderDto = convertToOrderDto(paymentObject.getOrder());
            
            return new PaymentDto(paymentObject.getId(),
                    orderDto,
                    paymentObject.getPaymentMethod(),
                    paymentObject.getStatus());
        } else {
            throw new EntityNotFoundException("payment not found");
        }

    }
    
    private OrderDto convertToOrderDto(Order order) {
        // Convert Restaurant to RestaurantDto
        RestaurantDto restaurantDto = new RestaurantDto(
                order.getRestaurant().getId(),
                order.getRestaurant().getName(),
                order.getRestaurant().getCountry(),
                order.getRestaurant().getMenuItemList().stream()
                        .map(menuItem -> new MenuItemDto(
                                menuItem.getId(),
                                menuItem.getName(),
                                menuItem.getPrice(),
                                menuItem.getRestaurant().getId()
                        ))
                        .collect(Collectors.toList())
        );
        
        // Convert OrderItems to OrderItemDtos
        var orderItemDtos = order.getOrderItemList().stream()
                .map(orderItem -> new OrderItemDto(
                        orderItem.getId(),
                        orderItem.getOrder().getId(),
                        new MenuItemDto(
                                orderItem.getMenuItem().getId(),
                                orderItem.getMenuItem().getName(),
                                orderItem.getMenuItem().getPrice(),
                                orderItem.getMenuItem().getRestaurant().getId()
                        ),
                        orderItem.getQuantity(),
                        orderItem.getPrice()
                ))
                .collect(Collectors.toList());

        return new OrderDto(
                order.getId(),
                order.getUser() != null ? order.getUser().getId() : null,
                restaurantDto,
                orderItemDtos,
                order.getStatus(),
                order.getTotalAmount(),
                order.getCreatedAt()
        );
    }
}
