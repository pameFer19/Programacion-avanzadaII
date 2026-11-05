package com.programacion;

import com.formdev.flatlaf.FlatIntelliJLaf;
import com.programacion.UI.LibraryUI;
import com.programacion.config.SpringConfig;
import com.programacion.repository.*;
import com.programacion.services.BookService;
import com.programacion.services.InventoryService;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

import javax.swing.*;

public class MainLibraryProyect {
    public static void main(String[] args) {

        System.out.println("\n==================== INICIANDO CONTEXTO SPRING =====================");
        ApplicationContext context = new AnnotationConfigApplicationContext(SpringConfig.class);

        // Configurar FlatLaf
        try {
            UIManager.setLookAndFeel(new FlatIntelliJLaf());
        } catch (Exception e) {
            e.printStackTrace();
        }

        // Obtener los beans desde Spring
        AuthorRepository repoAuthor = context.getBean(AuthorRepository.class);
        BookRepository repoBook = context.getBean(BookRepository.class);
        BookService bookService = context.getBean(BookService.class);
        CustomerRepository repoCustomer = context.getBean(CustomerRepository.class);
        InventoryService repoInventory = context.getBean(InventoryService.class);
        PurchaseOrderRepository repoPurchaseOrder = context.getBean(PurchaseOrderRepository.class);
        LineItemRepository repoLineItem = context.getBean(LineItemRepository.class);

        // Iniciar la interfaz gráfica
        SwingUtilities.invokeLater(() -> new LibraryUI(repoAuthor, repoBook, bookService, repoCustomer, repoInventory, repoPurchaseOrder, repoLineItem));


    }
}
