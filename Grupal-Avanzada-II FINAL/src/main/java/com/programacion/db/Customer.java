package com.programacion.db;

import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;

@Getter
@Setter
@Table("customers")
public class Customer {
    @Id
    private Integer id;
    private String name;
    private String email;
    private Integer version;

    @Override
    public String toString() {
        return name + " (" + email + ")";
    }
}

