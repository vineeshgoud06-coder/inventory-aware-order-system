package com.mystartup.order.repository;
import com.mystartup.order.entity.Product;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;
import java.util.Optional;
public interface ProductRepository extends JpaRepository<Product,Long> {
 @Lock(LockModeType.PESSIMISTIC_WRITE) @Query("select p from Product p where p.id=:id") Optional<Product> findByIdForUpdate(@Param("id") Long id);
 boolean existsBySku(String sku);
}
