package com.slooze.SDE.service;

import com.slooze.SDE.DTO.OrderDto;
import com.slooze.SDE.DTO.RestaurantDto;
import com.slooze.SDE.DTO.MenuItemDto;
import com.slooze.SDE.DTO.OrderItemDto;
import com.slooze.SDE.model.*;
import com.slooze.SDE.repository.OrderRepository;
import com.slooze.SDE.repository.PaymentRepository;
import com.slooze.SDE.repository.RestaurantRepository;
import com.slooze.SDE.repository.OrderItemRepository;
import com.slooze.SDE.repository.UserRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.nio.file.AccessDeniedException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.ArrayList;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class OrderService {

    private final OrderRepository orderRepository;
    private final RestaurantRepository restaurantRepository;
    private final PaymentRepository paymentRepository;
    private final OrderItemRepository orderItemRepository;
    private final UserRepository userRepository;

    public Object getOrderById(Long orderId, String role, String country, String username) throws AccessDeniedException {
        Order order = orderRepository.findById(orderId).orElseThrow(() -> new EntityNotFoundException("Order not found"));
        
        // Convert string country to enum for comparison
        Country countryEnum;
        try {
            countryEnum = Country.valueOf(country.toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new RuntimeException("Invalid country: " + country);
        }

        // Check access permissions
        if (!"ADMIN".equals(role) && !order.getRestaurant().getCountry().equals(countryEnum)) {
            throw new AccessDeniedException("You are not authorized to view this order");
        }

        // Additional check: users can only view their own orders (except ADMIN)
        if (!"ADMIN".equals(role) && !order.getUser().getName().equals(username)) {
            throw new AccessDeniedException("You can only view your own orders");
        }

        // Convert to DTO inline
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
        
        List<OrderItemDto> orderItemDtos = order.getOrderItemList().stream()
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

    public Object createOrder(Long restaurantId, List<Long> menuItemIds, String role, String country, String username) throws Exception {
        Optional<Restaurant> restaurant = restaurantRepository.findById(restaurantId);

        if (restaurant.isEmpty()) {
            throw new EntityNotFoundException("restaurant not found");
        }

        // Convert string country to enum for comparison
        Country countryEnum;
        try {
            countryEnum = Country.valueOf(country.toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new RuntimeException("Invalid country: " + country);
        }

        if (!"ADMIN".equals(role) && !restaurant.get().getCountry().equals(countryEnum)) {
            throw new AccessDeniedException("couldn't create order: country doesnt match");
        }

        List<MenuItem> menuItems = restaurant.get().getMenuItemList().stream()
                .filter(item -> menuItemIds.contains(item.getId()))
                .toList();

        if (menuItems.isEmpty()) {
            throw new RuntimeException("Invalid menu items for this restaurant");
        }

        double totalAmount = menuItems.stream().mapToDouble(MenuItem::getPrice).sum();

        // Find the user by username
        Optional<User> user = userRepository.findUserByName(username);
        if (user.isEmpty()) {
            throw new EntityNotFoundException("user not found: " + username);
        }

        // Create the order first
        Order order = Order.builder()
                .user(user.get())
                .restaurant(restaurant.get())
                .totalAmount(totalAmount)
                .status(OrderStatus.CREATED)
                .createdAt(LocalDateTime.now())
                .build();
        
        Order savedOrder = orderRepository.save(order);

        // Create OrderItems for each menu item (assuming quantity 1 for each)
        List<OrderItem> orderItems = new ArrayList<>();
        for (MenuItem menuItem : menuItems) {
            OrderItem orderItem = OrderItem.builder()
                    .order(savedOrder)
                    .menuItem(menuItem)
                    .quantity(1)
                    .price(menuItem.getPrice())
                    .build();
            orderItems.add(orderItem);
        }
        
        // Save order items
        orderItemRepository.saveAll(orderItems);
        
        // Update the order with the order items
        savedOrder.setOrderItemList(orderItems);

        Payment payment = Payment.builder()
                .order(savedOrder)
                .paymentMethod(PaymentMethod.CASH)
                .status(PaymentStatus.PENDING)
                .build();

        paymentRepository.save(payment);
        orderRepository.save(order);
        
        // Convert to DTO inline
        RestaurantDto restaurantDto = new RestaurantDto(
                savedOrder.getRestaurant().getId(),
                savedOrder.getRestaurant().getName(),
                savedOrder.getRestaurant().getCountry(),
                savedOrder.getRestaurant().getMenuItemList().stream()
                        .map(menuItem -> new MenuItemDto(
                                menuItem.getId(),
                                menuItem.getName(),
                                menuItem.getPrice(),
                                menuItem.getRestaurant().getId()
                        ))
                        .collect(Collectors.toList())
        );
        
        List<OrderItemDto> orderItemDtos = savedOrder.getOrderItemList().stream()
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
                savedOrder.getId(),
                savedOrder.getUser() != null ? savedOrder.getUser().getId() : null,
                restaurantDto,
                orderItemDtos,
                savedOrder.getStatus(),
                savedOrder.getTotalAmount(),
                savedOrder.getCreatedAt()
        );
    }

    public Object checkoutOrder(Long orderId, String role, String country) throws AccessDeniedException {

        if ("MEMBER".equals(role)) {
            throw new AccessDeniedException("Members cannot checkout orders");
        }

        Order order = orderRepository.findById(orderId).orElseThrow(() -> new EntityNotFoundException("order not found"));

        if (!"ADMIN".equals(role) && !order.getRestaurant().getCountry().name().equals(country)) {
            throw new AccessDeniedException("you are not authorized to make this request");
        }

        // Find the payment associated with this order
        Optional<Payment> paymentOpt = paymentRepository.findByOrder(order);
        if (paymentOpt.isEmpty()) {
            throw new EntityNotFoundException("Payment not found for this order");
        }

        Payment payment = paymentOpt.get();
        order.setStatus(OrderStatus.PLACED);
        payment.setStatus(PaymentStatus.PAID);
        
        paymentRepository.save(payment);
        orderRepository.save(order);
        
        // Convert to DTO inline
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
        
        List<OrderItemDto> orderItemDtos = order.getOrderItemList().stream()
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

     public Object cancelOrder(Long orderId, String role, String country) throws AccessDeniedException {
         if ("MEMBER".equals(role)) {
             throw new AccessDeniedException("Members cannot cancel orders");
         }

         Order order = orderRepository.findById(orderId).orElseThrow(() -> new EntityNotFoundException("order not found"));

         if (!"ADMIN".equals(role) && !order.getRestaurant().getCountry().name().equals(country)) {
            throw new AccessDeniedException("you are not authorized to make this request");
         }

         // Find the payment associated with this order
         Optional<Payment> paymentOpt = paymentRepository.findByOrder(order);
         if (paymentOpt.isEmpty()) {
            throw new EntityNotFoundException("Payment not found for this order");
         }

         Payment payment = paymentOpt.get();
         order.setStatus(OrderStatus.CANCELLED);

         if (payment.getStatus() == PaymentStatus.PAID) {
            payment.setStatus(PaymentStatus.REFUNDED);
            paymentRepository.save(payment);
         }

         orderRepository.save(order);
         
         // Convert to DTO inline
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
         
         List<OrderItemDto> orderItemDtos = order.getOrderItemList().stream()
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
