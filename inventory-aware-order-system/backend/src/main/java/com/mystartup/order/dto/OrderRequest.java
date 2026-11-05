package com.mystartup.order.dto;
import jakarta.validation.Valid;
import jakarta.validation.constraints.*;
import java.util.List;
public record OrderRequest(@NotBlank @Size(max=120) String customerName,@NotEmpty @Valid List<Line> items,@NotBlank @Size(max=100) String idempotencyKey, boolean simulatePaymentFailure) {
 public record Line(@NotNull @Positive Long productId,@Min(1) @Max(1000) int quantity) {}
}
