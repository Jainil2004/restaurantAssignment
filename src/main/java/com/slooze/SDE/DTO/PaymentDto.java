package com.slooze.SDE.DTO;

import com.slooze.SDE.model.PaymentMethod;
import com.slooze.SDE.model.PaymentStatus;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PaymentDto {
    private Long id;
    private OrderDto order;
    private PaymentMethod paymentMethod;
    private PaymentStatus status;
}
