package com.slooze.SDE.controller;

import com.slooze.SDE.model.PaymentMethod;
import com.slooze.SDE.model.PaymentStatus;
import com.slooze.SDE.service.PaymentService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/payments")
@RequiredArgsConstructor
public class PaymentController {
    private final PaymentService paymentService;

    @GetMapping("/order/{orderId}")
    public ResponseEntity<?> getPaymentByOrderId(@PathVariable Long orderId, HttpServletRequest request) {
        String role = request.getAttribute("role").toString();

        try {
            return ResponseEntity.ok(paymentService.getPaymentByOrderId(orderId, role));
        } catch (AccessDeniedException e) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }
    }

    @PatchMapping("/{paymentId}")
    public ResponseEntity<?> updatePayment(@PathVariable Long paymentId, @RequestParam(required = false) PaymentMethod method, @RequestParam(required = false) PaymentStatus status, HttpServletRequest request) throws Exception {
        String role = request.getAttribute("role").toString();

        try {
            return ResponseEntity.ok(paymentService.updatePayment(paymentId, method, status, role));
        } catch (AccessDeniedException e) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }
    }
}
