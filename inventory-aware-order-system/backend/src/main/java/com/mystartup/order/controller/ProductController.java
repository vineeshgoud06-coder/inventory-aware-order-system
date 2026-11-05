package com.mystartup.order.controller;
import com.mystartup.order.dto.CreateProductRequest;
import com.mystartup.order.entity.Product;
import com.mystartup.order.service.ProductService;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;
import java.util.List;
@RestController @RequestMapping("/api/products") @CrossOrigin(origins="*")
public class ProductController {
 private final ProductService service; public ProductController(ProductService service){this.service=service;}
 @GetMapping public List<Product> all(){return service.all();}
 @GetMapping("/{id}") public Product get(@PathVariable Long id){return service.get(id);}
 @PostMapping public Product create(@Valid @RequestBody CreateProductRequest req){return service.create(req);}
 @PostMapping("/{id}/restock") public Product restock(@PathVariable Long id,@RequestParam int quantity){return service.restock(id,quantity);}
}
