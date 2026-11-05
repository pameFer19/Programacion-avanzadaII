package com.programacion.repository;

import com.programacion.db.Inventory;
import org.springframework.data.jdbc.repository.query.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface InventoryRepository extends CrudRepository<Inventory, String> {

    @Query("SELECT * FROM inventory WHERE (supplied - sold) <= :threshold")
    List<Inventory> findLowStock(@Param("threshold") Integer threshold);
    
    @Query("SELECT * FROM inventory WHERE (supplied - sold) BETWEEN :min AND :max")
    List<Inventory> findByStockRange(@Param("min") Integer min, @Param("max") Integer max);
    
    @Query("SELECT (supplied - sold) FROM inventory WHERE isbn = :isbn")
    Integer getAvailableStock(@Param("isbn") String isbn);
}
