package com.programacion.repository;

import com.programacion.db.PurchaseOrder;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PurchaseOrderRepository extends CrudRepository<PurchaseOrder, Integer> {

    List<PurchaseOrder> findByCustomerIdOrderByPlacedOnDesc(Integer customerId);

    List<PurchaseOrder> findByStatusOrderByPlacedOnDesc(Short status);
    
    default List<PurchaseOrder> findByCustomerId(Integer customerId) {
        return findByCustomerIdOrderByPlacedOnDesc(customerId);
    }
    
    default List<PurchaseOrder> findByStatus(Short status) {
        return findByStatusOrderByPlacedOnDesc(status);
    }
    
    @Override
    List<PurchaseOrder> findAll();
}
