package com.mystartup.order.dto;
import com.mystartup.order.entity.CustomerOrder;
import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;
public record OrderResponse(Long id,String customerName,BigDecimal total,String status,String idempotencyKey,Instant createdAt,List<Item> items) {
 public record Item(Long productId,String productName,int quantity,BigDecimal unitPrice,BigDecimal lineTotal) {}
 public static OrderResponse from(CustomerOrder o){return new OrderResponse(o.getId(),o.getCustomerName(),o.getTotal(),o.getStatus(),o.getIdempotencyKey(),o.getCreatedAt(),o.getItems().stream().map(i->new Item(i.getProductId(),i.getProductName(),i.getQuantity(),i.getUnitPrice(),i.getUnitPrice().multiply(BigDecimal.valueOf(i.getQuantity())))).toList());}
}
