package com.programacion.UI;

import com.programacion.db.Inventory;
import com.programacion.db.Book;
import com.programacion.services.InventoryService;
import org.springframework.stereotype.Component;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.Optional;

@Component
public class InventoryPanel extends JPanel {

    private final InventoryService inventoryService;
    private JTable table;
    private DefaultTableModel tableModel;

    public InventoryPanel(InventoryService inventoryService) {
        this.inventoryService = inventoryService;
        setupUI();
        loadInventory();
    }

    private void setupUI() {
        setLayout(new BorderLayout());

        tableModel = new DefaultTableModel(new String[]{
                "ISBN", "Título del Libro", "Vendidas", "Suministradas", "Stock Disponible"}, 0);
        table = new JTable(tableModel);
        table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        // Panel de filtros
        JPanel filterPanel = new JPanel(new FlowLayout());
        JButton showAllButton = new JButton("Mostrar Todo");
        JTextField minStockField = new JTextField(5);
        JTextField maxStockField = new JTextField(5);
        JButton rangeButton = new JButton("Filtrar por Rango");

        showAllButton.addActionListener(e -> loadInventory());

        rangeButton.addActionListener(e -> {
            try {
                int min = Integer.parseInt(minStockField.getText().trim());
                int max = Integer.parseInt(maxStockField.getText().trim());
                showStockRange(min, max);
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(this, "Ingrese números válidos para el rango", "Error", JOptionPane.ERROR_MESSAGE);
            }
        });

        filterPanel.add(new JLabel("Rango:"));
        filterPanel.add(minStockField);
        filterPanel.add(new JLabel("a"));
        filterPanel.add(maxStockField);
        filterPanel.add(rangeButton);
        filterPanel.add(showAllButton);

        // Panel de botones
        JPanel buttonPanel = new JPanel();
        JButton addButton = new JButton("Agregar Inventario");
        JButton updateButton = new JButton("Actualizar Inventario");
        JButton deleteButton = new JButton("Eliminar Inventario");
        JButton addStockButton = new JButton("Agregar Stock");
        JButton sellButton = new JButton("Registrar Venta");
        JButton viewBookButton = new JButton("Ver Libro");

        addButton.addActionListener(e -> addInventory());
        updateButton.addActionListener(e -> updateInventory());
        deleteButton.addActionListener(e -> deleteInventory());
        addStockButton.addActionListener(e -> addStock());
        sellButton.addActionListener(e -> registerSale());
        viewBookButton.addActionListener(e -> viewBookDetails());

        buttonPanel.add(addButton);
        buttonPanel.add(updateButton);
        buttonPanel.add(deleteButton);
        buttonPanel.add(addStockButton);
        buttonPanel.add(sellButton);
        buttonPanel.add(viewBookButton);

        add(filterPanel, BorderLayout.NORTH);
        add(new JScrollPane(table), BorderLayout.CENTER);
        add(buttonPanel, BorderLayout.SOUTH);
    }

    private void loadInventory() {
        tableModel.setRowCount(0);
        for (Inventory inventory : inventoryService.getAllInventory()) {
            String title = inventoryService.getBook(inventory.getIsbn())
                    .map(Book::getTitle)
                    .orElse("Libro no encontrado");
            int available = inventory.getSupplied() - inventory.getSold();
            tableModel.addRow(new Object[]{
                    inventory.getIsbn(), title, inventory.getSold(), inventory.getSupplied(), available
            });
        }
    }

    private void showStockRange(int min, int max) {
        tableModel.setRowCount(0);
        for (Inventory inventory : inventoryService.getByStockRange(min, max)) {
            String title = inventoryService.getBook(inventory.getIsbn())
                    .map(Book::getTitle)
                    .orElse("Libro no encontrado");
            int available = inventory.getSupplied() - inventory.getSold();
            tableModel.addRow(new Object[]{
                    inventory.getIsbn(), title, inventory.getSold(), inventory.getSupplied(), available
            });
        }
    }

    private void addInventory() {
        JTextField isbnField = new JTextField();
        JTextField soldField = new JTextField("0");
        JTextField suppliedField = new JTextField("0");

        JPanel panel = new JPanel(new GridLayout(3, 2));
        panel.add(new JLabel("ISBN:"));
        panel.add(isbnField);
        panel.add(new JLabel("Vendidas:"));
        panel.add(soldField);
        panel.add(new JLabel("Suministradas:"));
        panel.add(suppliedField);

        int result = JOptionPane.showConfirmDialog(this, panel, "Agregar Inventario", JOptionPane.OK_CANCEL_OPTION);
        if (result == JOptionPane.OK_OPTION) {
            try {
                String isbn = isbnField.getText().trim();
                int sold = Integer.parseInt(soldField.getText().trim());
                int supplied = Integer.parseInt(suppliedField.getText().trim());

                if (isbn.isEmpty()) {
                    JOptionPane.showMessageDialog(this, "El ISBN no puede estar vacío.", "Error", JOptionPane.ERROR_MESSAGE);
                    return;
                }

                if (inventoryService.getBook(isbn).isEmpty()) {
                    JOptionPane.showMessageDialog(this, "El libro no existe.", "Error", JOptionPane.ERROR_MESSAGE);
                    return;
                }

                Inventory inventory = new Inventory();
                inventory.setIsbn(isbn);
                inventory.setSold(sold);
                inventory.setSupplied(supplied);

                inventoryService.save(inventory);
                loadInventory();
                JOptionPane.showMessageDialog(this, "Inventario agregado.");
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Error: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void updateInventory() {
        int row = table.getSelectedRow();
        if (row == -1) return;

        String isbn = (String) tableModel.getValueAt(row, 0);
        Optional<Inventory> invOpt = inventoryService.getByIsbn(isbn);
        if (invOpt.isEmpty()) return;

        Inventory inventory = invOpt.get();

        JTextField soldField = new JTextField(String.valueOf(inventory.getSold()));
        JTextField suppliedField = new JTextField(String.valueOf(inventory.getSupplied()));

        JPanel panel = new JPanel(new GridLayout(2, 2));
        panel.add(new JLabel("Vendidas:"));
        panel.add(soldField);
        panel.add(new JLabel("Suministradas:"));
        panel.add(suppliedField);

        int result = JOptionPane.showConfirmDialog(this, panel, "Actualizar Inventario", JOptionPane.OK_CANCEL_OPTION);
        if (result == JOptionPane.OK_OPTION) {
            try {
                inventory.setSold(Integer.parseInt(soldField.getText()));
                inventory.setSupplied(Integer.parseInt(suppliedField.getText()));
                inventoryService.save(inventory);
                loadInventory();
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Error: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void deleteInventory() {
        int row = table.getSelectedRow();
        if (row == -1) return;
        String isbn = (String) tableModel.getValueAt(row, 0);

        int confirm = JOptionPane.showConfirmDialog(this,
                "¿Eliminar inventario de ISBN " + isbn + "?", "Confirmar", JOptionPane.YES_NO_OPTION);

        if (confirm == JOptionPane.YES_OPTION) {
            inventoryService.deleteById(isbn);
            loadInventory();
        }
    }

    private void addStock() {
        int row = table.getSelectedRow();
        if (row == -1) return;
        String isbn = (String) tableModel.getValueAt(row, 0);

        String input = JOptionPane.showInputDialog(this, "Cantidad a agregar:");
        if (input != null) {
            try {
                int cantidad = Integer.parseInt(input.trim());
                inventoryService.addStock(isbn, cantidad);
                loadInventory();
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Error: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void registerSale() {
        int row = table.getSelectedRow();
        if (row == -1) return;

        String isbn = (String) tableModel.getValueAt(row, 0);
        int disponible = (int) tableModel.getValueAt(row, 4);

        String input = JOptionPane.showInputDialog(this, "Cantidad vendida:");
        if (input != null) {
            try {
                int cantidad = Integer.parseInt(input.trim());
                if (cantidad > disponible) {
                    JOptionPane.showMessageDialog(this, "No hay suficiente stock.");
                    return;
                }
                inventoryService.registerSale(isbn, cantidad);
                loadInventory();
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Error: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void viewBookDetails() {
        int row = table.getSelectedRow();
        if (row == -1) return;

        String isbn = (String) tableModel.getValueAt(row, 0);
        Optional<Book> bookOpt = inventoryService.getBook(isbn);

        if (bookOpt.isPresent()) {
            Book book = bookOpt.get();
            String authors = String.join(", ", inventoryService.getAuthorsNames(isbn));
            String details = String.format("""
                ISBN: %s
                Título: %s
                Precio: $%.2f
                Versión: %d
                Autores: %s
            """, book.getIsbn(), book.getTitle(), book.getPrice(), book.getVersion(), authors);

            JOptionPane.showMessageDialog(this, details, "Detalles del Libro", JOptionPane.INFORMATION_MESSAGE);
        } else {
            JOptionPane.showMessageDialog(this, "Libro no encontrado.");
        }
    }
}
