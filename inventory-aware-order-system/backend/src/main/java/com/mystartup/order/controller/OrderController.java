package com.mystartup.order.controller;
import com.mystartup.order.dto.*;
import com.mystartup.order.service.OrderService;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;
import java.util.List;
@RestController @RequestMapping("/api/orders") @CrossOrigin(origins="*")
public class OrderController {
 private final OrderService service; public OrderController(OrderService service){this.service=service;}
 @PostMapping public OrderResponse checkout(@Valid @RequestBody OrderRequest req){return OrderResponse.from(service.checkout(req));}
 @GetMapping public List<OrderResponse> all(){return service.all().stream().map(OrderResponse::from).toList();}
 @GetMapping("/{id}") public OrderResponse get(@PathVariable Long id){return OrderResponse.from(service.get(id));}
}
