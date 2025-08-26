package com.slooze.SDE.DTO;

import com.slooze.SDE.model.OrderStatus;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class OrderDto {
    private Long id;
    private Long userId;
    private RestaurantDto restaurant;
    private List<OrderItemDto> orderItemList;
    private OrderStatus status;
    private double totalAmount;
    private LocalDateTime createdAt;
}
