package com.programacion.db;

import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

@Getter
@Setter
@Table("line_items")
public class LineItem {
    @Id
    private Integer id;
    @Column("order_id")
    private Integer orderId;
    private Integer quantity;
    private String isbn;

}
