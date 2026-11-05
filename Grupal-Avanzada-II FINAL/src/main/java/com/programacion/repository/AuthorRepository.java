package com.programacion.repository;

import com.programacion.db.Author;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AuthorRepository extends CrudRepository<Author, Integer> {

    List<Author> findByNameContainingIgnoreCase(String name);
}

