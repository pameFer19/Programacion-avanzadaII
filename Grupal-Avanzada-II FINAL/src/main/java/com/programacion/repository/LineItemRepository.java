package com.programacion.repository;

import com.programacion.db.LineItem;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface LineItemRepository extends CrudRepository<LineItem, Integer> {
    
    List<LineItem> findByOrderIdOrderById(@Param("orderId") Integer orderId);
}
