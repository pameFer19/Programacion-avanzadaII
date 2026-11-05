package com.programacion.UI;

import com.programacion.repository.*;
import com.programacion.services.BookService;
import com.formdev.flatlaf.FlatIntelliJLaf;
import com.formdev.flatlaf.FlatDarculaLaf;
import com.programacion.services.InventoryService;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class LibraryUI extends JFrame {
    public LibraryUI(AuthorRepository authorRepository, BookRepository bookRepository, BookService bookService,
                     CustomerRepository customerRepository, InventoryService inventoryService,
                     PurchaseOrderRepository purchaseOrderRepository, LineItemRepository lineItemRepository) {
        setTitle("Gestor de libros");
        setSize(850, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        // Crear la barra de menús
        JMenuBar menuBar = new JMenuBar();


        JMenu menuTheme = new JMenu("Cambio de Tema");
        menuBar.add(menuTheme);

        // Agregar opciones de tema
        JMenuItem lightThemeItem = new JMenuItem("Tema Claro (FlatIntelliJLaf)");
        JMenuItem darkThemeItem = new JMenuItem("Tema Oscuro (FlatDarculaLaf)");

        menuTheme.add(lightThemeItem);
        menuTheme.add(darkThemeItem);

        lightThemeItem.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                setLookAndFeel(new FlatIntelliJLaf());
            }
        });

        darkThemeItem.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                setLookAndFeel(new FlatDarculaLaf());
            }
        });

        // Establecer la barra de menús
        setJMenuBar(menuBar);

        // Crear el panel de pestañas
        JTabbedPane tabbedPane = new JTabbedPane();
        tabbedPane.addTab("Authors", new AuthorPanel(authorRepository));
        tabbedPane.addTab("Books", new BookPanel(bookRepository, authorRepository, bookService));
        tabbedPane.addTab("Customers", new CustomerPanel(customerRepository, purchaseOrderRepository));
        tabbedPane.addTab("Inventario", new InventoryPanel(inventoryService));
        tabbedPane.addTab("Purchase Orders", new PurchaseOrderPanel(purchaseOrderRepository, customerRepository, lineItemRepository, bookRepository));

        add(tabbedPane);

        setVisible(true);
    }

    private void setLookAndFeel(javax.swing.LookAndFeel lookAndFeel) {
        try {
            UIManager.setLookAndFeel(lookAndFeel);
            SwingUtilities.updateComponentTreeUI(this);
        } catch (UnsupportedLookAndFeelException e) {
            e.printStackTrace();
        }
    }

}
