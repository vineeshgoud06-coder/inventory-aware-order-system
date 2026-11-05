package com.mystartup.order.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import java.math.BigDecimal;

@Entity @Table(name="products")
public class Product {
    @Id @GeneratedValue(strategy=GenerationType.IDENTITY) private Long id;
    @NotBlank @Column(nullable=false) private String name;
    @NotBlank @Column(nullable=false, unique=true) private String sku;
    @NotNull @DecimalMin("0.01") @Column(nullable=false, precision=12, scale=2) private BigDecimal price;
    @Min(0) @Column(nullable=false) private int stock;
    protected Product() {}
    public Product(String name, String sku, BigDecimal price, int stock) { this.name=name; this.sku=sku; this.price=price; this.stock=stock; }
    public Long getId(){return id;} public String getName(){return name;} public String getSku(){return sku;} public BigDecimal getPrice(){return price;} public int getStock(){return stock;}
    public void setName(String v){name=v;} public void setSku(String v){sku=v;} public void setPrice(BigDecimal v){price=v;} public void setStock(int v){stock=v;}
}
