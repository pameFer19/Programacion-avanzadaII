package com.programacion.services;

import com.programacion.db.Book;
import com.programacion.repository.BookRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class BookService {

    @Autowired
    private BookRepository bookRepository;

   public List<Book> getBooksByAuthor(String authorName) {
        return bookRepository.findByAuthorNameContaining(authorName);
    }

    @Transactional
    public void deleteBook(String isbn) {
        bookRepository.deleteById(isbn);
    }
}
