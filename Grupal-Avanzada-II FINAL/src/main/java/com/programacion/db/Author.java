package com.programacion.db;

import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;


@Getter
@Setter
@Table("authors")
public class Author {
    @Id
    private Integer id;
    private String name;
    private Integer version;
    

}