package com.slooze.SDE.DTO;

import com.slooze.SDE.model.Order;
import com.slooze.SDE.model.PaymentMethod;
import com.slooze.SDE.model.PaymentStatus;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.OneToOne;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PaymentDto {
    private Long id;
    private Order order;
    private PaymentMethod paymentMethod;
    private PaymentStatus status;
}
