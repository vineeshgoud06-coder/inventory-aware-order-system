package com.mystartup.order.dto;
import jakarta.validation.constraints.*;
import java.math.BigDecimal;
public record CreateProductRequest(@NotBlank String name,@NotBlank String sku,@NotNull @DecimalMin("0.01") BigDecimal price,@Min(0) int stock) {}
