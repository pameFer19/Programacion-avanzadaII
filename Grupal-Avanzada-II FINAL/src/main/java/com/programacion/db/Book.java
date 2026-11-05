package com.programacion.db;

import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;

import java.math.BigDecimal;

@Getter
@Setter
@Table("books")
public class Book {
    @Id
    private String isbn;
    private String title;
    private BigDecimal price;
    private Integer version;

    @Override
    public String toString() {
        return title + " (ISBN: " + isbn + ") - $" + price;
    }
}

