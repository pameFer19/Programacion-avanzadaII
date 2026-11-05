package com.programacion.db;

import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;

@Getter
@Setter
@Table("inventory")
public class Inventory {
    @Id
    private String isbn;
    private Integer sold;
    private Integer supplied;

}
