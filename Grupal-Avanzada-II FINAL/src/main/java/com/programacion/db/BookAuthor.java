package com.programacion.db;

import lombok.Getter;
import lombok.Setter;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

@Getter
@Setter
@Table("books_authors")
public class BookAuthor {
    @Column("books_isbn")
    private String booksIsbn;

    @Column("authors_id")
    private Integer authorsId;

}
