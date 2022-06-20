package com.loginregister.logincodejavanet.orderItems;

import com.loginregister.logincodejavanet.orders.Order;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OrderItemRepository extends JpaRepository<OrderItem, Long> {

}
