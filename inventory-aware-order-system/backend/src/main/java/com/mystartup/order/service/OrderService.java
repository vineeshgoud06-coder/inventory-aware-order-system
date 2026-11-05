package com.mystartup.order.service;

import com.mystartup.order.dto.OrderRequest;
import com.mystartup.order.entity.*;
import com.mystartup.order.repository.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.math.BigDecimal;
import java.util.*;

@Service
public class OrderService {
 private final ProductRepository products; private final CustomerOrderRepository orders;
 public OrderService(ProductRepository products,CustomerOrderRepository orders){this.products=products;this.orders=orders;}
 @Transactional
 public CustomerOrder checkout(OrderRequest req){
   Optional<CustomerOrder> previous=orders.findByIdempotencyKey(req.idempotencyKey());
   if(previous.isPresent()) return previous.get();
   // Merge repeated product lines to avoid locking/decrementing the same product twice.
   Map<Long,Integer> quantities=new TreeMap<>();
   for(OrderRequest.Line line:req.items()) quantities.merge(line.productId(),line.quantity(),Integer::sum);
   Map<Long,Product> locked=new LinkedHashMap<>();
   for(var entry:quantities.entrySet()){
     Product p=products.findByIdForUpdate(entry.getKey()).orElseThrow(()->new NoSuchElementException("Product not found: "+entry.getKey()));
     if(p.getStock()<entry.getValue()) throw new IllegalArgumentException("Insufficient stock for product: "+p.getName());
     locked.put(entry.getKey(),p);
   }
   BigDecimal total=BigDecimal.ZERO;
   for(var entry:quantities.entrySet()) total=total.add(locked.get(entry.getKey()).getPrice().multiply(BigDecimal.valueOf(entry.getValue())));
   // Stock reservation and order creation share a database transaction. Failed simulated payment restores stock.
   for(var entry:quantities.entrySet()) locked.get(entry.getKey()).setStock(locked.get(entry.getKey()).getStock()-entry.getValue());
   CustomerOrder order=new CustomerOrder(req.idempotencyKey(),req.customerName().trim(),total,req.simulatePaymentFailure()?"PAYMENT_FAILED":"PAID");
   for(var entry:quantities.entrySet()){Product p=locked.get(entry.getKey());order.addItem(new OrderItem(p.getId(),p.getName(),entry.getValue(),p.getPrice()));}
   if(req.simulatePaymentFailure()) for(var entry:quantities.entrySet()) locked.get(entry.getKey()).setStock(locked.get(entry.getKey()).getStock()+entry.getValue());
   return orders.save(order);
 }
 @Transactional(readOnly=true) public List<CustomerOrder> all(){return orders.findAll();}
 @Transactional(readOnly=true) public CustomerOrder get(Long id){return orders.findById(id).orElseThrow(()->new NoSuchElementException("Order not found: "+id));}
}
