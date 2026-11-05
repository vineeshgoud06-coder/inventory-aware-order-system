package com.mystartup.order.config;
import com.mystartup.order.entity.Product;
import com.mystartup.order.repository.ProductRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import java.math.BigDecimal;
@Configuration public class SeedData {
 @Bean CommandLineRunner seedProducts(ProductRepository repo){return args->{if(repo.count()==0){repo.save(new Product("Wireless Mouse","MOU-100",new BigDecimal("599.00"),10));repo.save(new Product("Mechanical Keyboard","KEY-200",new BigDecimal("2499.00"),5));repo.save(new Product("USB-C Hub","HUB-300",new BigDecimal("1299.00"),8));}};}
}
