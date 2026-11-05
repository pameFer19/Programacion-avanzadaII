package com.programacion.repository;

import com.programacion.db.Book;
import com.programacion.db.Author;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.jdbc.repository.query.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface BookRepository extends CrudRepository<Book, String> {


    @Query("SELECT b.* FROM books b JOIN books_authors ba ON b.isbn = ba.books_isbn JOIN authors a ON ba.authors_id = a.id WHERE LOWER(a.name) LIKE LOWER(CONCAT('%', :authorName, '%'))")
    List<Book> findByAuthorNameContaining(@Param("authorName") String authorName);
    
    @Query("SELECT a.* FROM authors a JOIN books_authors ba ON a.id = ba.authors_id WHERE ba.books_isbn = :isbn")
    List<Author> findAuthorsByBookIsbn(@Param("isbn") String isbn);
    
    default void addAuthorToBook(String isbn, Integer authorId) {

    }
}

