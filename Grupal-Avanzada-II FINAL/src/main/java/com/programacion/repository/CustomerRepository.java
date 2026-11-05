package com.programacion.repository;

import com.programacion.db.Customer;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CustomerRepository extends CrudRepository<Customer, Integer> {

    List<Customer> findAllByOrderByName();

    List<Customer> findByNameContainingIgnoreCase(String name);
    
}
