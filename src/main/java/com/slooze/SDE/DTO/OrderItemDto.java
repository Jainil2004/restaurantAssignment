package com.slooze.SDE.DTO;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class OrderItemDto {
    private Long id;
    private Long orderId;
    private MenuItemDto menuItem;
    private Integer quantity;
    private Double price;
}
