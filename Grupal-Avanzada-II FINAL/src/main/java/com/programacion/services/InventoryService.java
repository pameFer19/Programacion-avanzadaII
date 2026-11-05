package com.programacion.services;

import com.programacion.db.Inventory;
import com.programacion.db.Book;
import com.programacion.repository.InventoryRepository;
import com.programacion.repository.BookRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
public class InventoryService {

    private final InventoryRepository inventoryRepository;
    private final BookRepository bookRepository;
    private final JdbcTemplate jdbcTemplate;

    @Autowired
    public InventoryService(InventoryRepository inventoryRepository, BookRepository bookRepository, JdbcTemplate jdbcTemplate) {
        this.inventoryRepository = inventoryRepository;
        this.bookRepository = bookRepository;
        this.jdbcTemplate = jdbcTemplate;
    }

    public List<Inventory> getAllInventory() {
        return (List<Inventory>) inventoryRepository.findAll();
    }

    public Optional<Inventory> getByIsbn(String isbn) {
        return inventoryRepository.findById(isbn);
    }

    public Optional<Book> getBook(String isbn) {
        return bookRepository.findById(isbn);
    }

    public List<Inventory> getLowStock(int threshold) {
        return inventoryRepository.findLowStock(threshold);
    }

    public List<Inventory> getByStockRange(int min, int max) {
        return inventoryRepository.findByStockRange(min, max);
    }

    public void save(Inventory inventory) {
        inventoryRepository.save(inventory);
    }

    public void deleteById(String isbn) {
        inventoryRepository.deleteById(isbn);
    }

    @Transactional
    public void addStock(String isbn, int quantity) {
        jdbcTemplate.update("UPDATE inventory SET supplied = supplied + ? WHERE isbn = ?", quantity, isbn);
    }

    @Transactional
    public void registerSale(String isbn, int quantity) {
        Integer available = inventoryRepository.getAvailableStock(isbn);
        if (available == null || quantity > available) {
            throw new RuntimeException("Stock insuficiente para realizar la venta.");
        }
        jdbcTemplate.update("UPDATE inventory SET sold = sold + ? WHERE isbn = ?", quantity, isbn);
    }

    public List<String> getAuthorsNames(String isbn) {
        return bookRepository.findAuthorsByBookIsbn(isbn).stream()
                .map(a -> a.getName())
                .toList();
    }
}

