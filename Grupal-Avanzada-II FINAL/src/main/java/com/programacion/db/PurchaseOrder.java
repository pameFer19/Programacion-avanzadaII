package com.programacion.db;

import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

import java.time.LocalDateTime;

@Getter
@Setter
@Table("purchase_orders")
public class PurchaseOrder {
    @Id
    private Integer id;

    @Column("customer_id")
    private Integer customerId;
    private Integer total;
    private Short status;

    @Column("placed_on")
    private LocalDateTime placedOn;

    @Column("delivered_on")
    private LocalDateTime deliveredOn;
}
