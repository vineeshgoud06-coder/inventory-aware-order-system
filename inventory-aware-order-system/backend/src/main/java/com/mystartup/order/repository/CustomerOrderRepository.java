package com.mystartup.order.repository;
import com.mystartup.order.entity.CustomerOrder;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;
public interface CustomerOrderRepository extends JpaRepository<CustomerOrder,Long> { Optional<CustomerOrder> findByIdempotencyKey(String key); }
