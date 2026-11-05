package com.mystartup.order.entity;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.Instant;
import java.util.*;

@Entity @Table(name="customer_orders", uniqueConstraints=@UniqueConstraint(name="uk_order_idempotency_key", columnNames="idempotency_key"))
public class CustomerOrder {
    @Id @GeneratedValue(strategy=GenerationType.IDENTITY) private Long id;
    @Column(name="idempotency_key", nullable=false, unique=true, length=100) private String idempotencyKey;
    @Column(nullable=false) private String customerName;
    @Column(nullable=false, precision=12, scale=2) private BigDecimal total;
    @Column(nullable=false) private String status;
    @Column(nullable=false) private Instant createdAt;
    @OneToMany(mappedBy="order", cascade=CascadeType.ALL, orphanRemoval=true, fetch=FetchType.EAGER) private List<OrderItem> items=new ArrayList<>();
    protected CustomerOrder() {}
    public CustomerOrder(String key,String customer,BigDecimal total,String status){this.idempotencyKey=key;this.customerName=customer;this.total=total;this.status=status;this.createdAt=Instant.now();}
    public void addItem(OrderItem item){items.add(item);item.setOrder(this);}
    public Long getId(){return id;} public String getIdempotencyKey(){return idempotencyKey;} public String getCustomerName(){return customerName;} public BigDecimal getTotal(){return total;} public String getStatus(){return status;} public Instant getCreatedAt(){return createdAt;} public List<OrderItem> getItems(){return items;}
    public void setStatus(String v){status=v;}
}
