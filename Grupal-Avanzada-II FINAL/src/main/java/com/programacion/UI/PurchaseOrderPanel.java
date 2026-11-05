package com.programacion.UI;

import com.programacion.db.PurchaseOrder;
import com.programacion.db.Customer;
import com.programacion.db.LineItem;
import com.programacion.db.Book;
import com.programacion.repository.*;
import org.springframework.stereotype.Component;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Optional;

@Component
public class PurchaseOrderPanel extends JPanel {
    private PurchaseOrderRepository purchaseOrderRepository;
    private CustomerRepository customerRepository;
    private LineItemRepository lineItemRepository;
    private BookRepository bookRepository;
    
    private JTable ordersTable;
    private DefaultTableModel ordersTableModel;
    
    private JTable lineItemsTable;
    private DefaultTableModel lineItemsTableModel;
    
    private JComboBox<String> statusFilter;
    private JComboBox<Customer> customerFilter;

    public PurchaseOrderPanel(PurchaseOrderRepository purchaseOrderRepository,
                              CustomerRepository customerRepository,
                              LineItemRepository lineItemRepository,
                              BookRepository bookRepository) {
        this.purchaseOrderRepository = purchaseOrderRepository;
        this.customerRepository = customerRepository;
        this.lineItemRepository = lineItemRepository;
        this.bookRepository = bookRepository;
        setupUI();
        loadOrders();
    }

    private void setupUI() {
        setLayout(new BorderLayout());

        // Panel principal dividido
        JSplitPane splitPane = new JSplitPane(JSplitPane.VERTICAL_SPLIT);
        splitPane.setDividerLocation(300);

        // Panel superior: Órdenes de compra
        JPanel ordersPanel = createOrdersPanel();
        splitPane.setTopComponent(ordersPanel);

        // Panel inferior: Líneas de ítem
        JPanel lineItemsPanel = createLineItemsPanel();
        splitPane.setBottomComponent(lineItemsPanel);

        add(splitPane, BorderLayout.CENTER);
    }

    private JPanel createOrdersPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBorder(BorderFactory.createTitledBorder("Órdenes de Compra"));

        // filtros
        JPanel filterPanel = new JPanel(new FlowLayout());
        
        // Filtro por estado
        statusFilter = new JComboBox<>(new String[]{"Todos", "Pendiente (1)", "Entregado (2)"});
        statusFilter.addActionListener(e -> applyFilters());
        
        // Filtro por cliente
        customerFilter = new JComboBox<>();
        customerFilter.addItem(null); // Opción "Todos"
        loadCustomersIntoCombo();
        customerFilter.addActionListener(e -> applyFilters());
        
        JButton showAllButton = new JButton("Mostrar Todas");
        showAllButton.addActionListener(e -> {
            statusFilter.setSelectedIndex(0);
            customerFilter.setSelectedIndex(0);
            loadOrders();
        });

        filterPanel.add(new JLabel("Estado:"));
        filterPanel.add(statusFilter);
        filterPanel.add(new JLabel("Cliente:"));
        filterPanel.add(customerFilter);
        filterPanel.add(showAllButton);

        // Tabla de órdenes
        ordersTableModel = new DefaultTableModel(new String[]{
            "ID", "Cliente", "Total", "Estado", "Fecha Pedido", "Fecha Entrega"}, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        ordersTable = new JTable(ordersTableModel);
        ordersTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        ordersTable.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                loadLineItemsForSelectedOrder();
            }
        });

        // Botones de acciones
        JPanel buttonPanel = new JPanel(new FlowLayout());
        
        JButton addButton = new JButton("Nueva Orden");
        addButton.addActionListener(e -> addOrder());
        
        JButton editButton = new JButton("Editar Orden");
        editButton.addActionListener(e -> editOrder());
        
        JButton deleteButton = new JButton("Eliminar Orden");
        deleteButton.addActionListener(e -> deleteOrder());
        
        JButton viewCustomerButton = new JButton("Ver Cliente");
        viewCustomerButton.addActionListener(e -> viewCustomerDetails());

        buttonPanel.add(addButton);
        buttonPanel.add(editButton);
        buttonPanel.add(deleteButton);
        buttonPanel.add(new JSeparator(SwingConstants.VERTICAL));
        buttonPanel.add(viewCustomerButton);

        panel.add(filterPanel, BorderLayout.NORTH);
        panel.add(new JScrollPane(ordersTable), BorderLayout.CENTER);
        panel.add(buttonPanel, BorderLayout.SOUTH);

        return panel;
    }

    private JPanel createLineItemsPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBorder(BorderFactory.createTitledBorder("Productos en la Orden"));

        // Tabla de line items
        lineItemsTableModel = new DefaultTableModel(new String[]{
            "ID", "Libro (ISBN)", "Título", "Cantidad", "Precio Unit.", "Subtotal"}, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        lineItemsTable = new JTable(lineItemsTableModel);
        lineItemsTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        // Botones de acciones para line items
        JPanel buttonPanel = new JPanel(new FlowLayout());
        
        JButton addItemButton = new JButton("Agregar Producto");
        addItemButton.addActionListener(e -> addLineItem());
        
        JButton editItemButton = new JButton("Editar Producto");
        editItemButton.addActionListener(e -> editLineItem());
        
        JButton deleteItemButton = new JButton("Eliminar Producto");
        deleteItemButton.addActionListener(e -> deleteLineItem());
        
        JButton viewBookButton = new JButton("Ver Libro");
        viewBookButton.addActionListener(e -> viewBookDetails());

        buttonPanel.add(addItemButton);
        buttonPanel.add(editItemButton);
        buttonPanel.add(deleteItemButton);
        buttonPanel.add(new JSeparator(SwingConstants.VERTICAL));
        buttonPanel.add(viewBookButton);

        panel.add(new JScrollPane(lineItemsTable), BorderLayout.CENTER);
        panel.add(buttonPanel, BorderLayout.SOUTH);

        return panel;
    }

    private void loadCustomersIntoCombo() {
        try {
            List<Customer> customers = customerRepository.findAllByOrderByName();
            for (Customer customer : customers) {
                customerFilter.addItem(customer);
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Error al cargar clientes: " + e.getMessage());
        }
    }

    private void applyFilters() {
        try {
            List<PurchaseOrder> orders;
            
            // Filtrar por estado
            String selectedStatus = (String) statusFilter.getSelectedItem();
            Customer selectedCustomer = (Customer) customerFilter.getSelectedItem();
            
            if (selectedCustomer != null) {
                // Filtrar por cliente específico
                orders = purchaseOrderRepository.findByCustomerId(selectedCustomer.getId());
            } else if (!"Todos".equals(selectedStatus)) {
                // Filtrar por estado
                Short status = selectedStatus.contains("1") ? (short) 1 : (short) 2;
                orders = purchaseOrderRepository.findByStatus(status);
            } else {
                // Sin filtros
                orders = purchaseOrderRepository.findAll();
            }
            
            displayOrders(orders);
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Error al aplicar filtros: " + e.getMessage());
        }
    }

    private void loadOrders() {
        try {
            List<PurchaseOrder> orders = purchaseOrderRepository.findAll();
            displayOrders(orders);
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Error al cargar órdenes: " + e.getMessage());
        }
    }

    private void displayOrders(List<PurchaseOrder> orders) {
        ordersTableModel.setRowCount(0);
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
        
        for (PurchaseOrder order : orders) {
            try {
                // Obtener información del cliente
                Optional<Customer> customerOpt = customerRepository.findById(order.getCustomerId());
                String customerName = customerOpt.map(Customer::getName).orElse("Desconocido");
                
                String statusText = order.getStatus() == 1 ? "Pendiente" : "Entregado";
                String placedOn = order.getPlacedOn() != null ? order.getPlacedOn().format(formatter) : "";
                String deliveredOn = order.getDeliveredOn() != null ? order.getDeliveredOn().format(formatter) : "";
                
                ordersTableModel.addRow(new Object[]{
                    order.getId(),
                    customerName,
                    "$" + order.getTotal(),
                    statusText,
                    placedOn,
                    deliveredOn
                });
            } catch (Exception e) {
                System.err.println("Error al mostrar orden " + order.getId() + ": " + e.getMessage());
            }
        }
    }

    private void loadLineItemsForSelectedOrder() {
        int selectedRow = ordersTable.getSelectedRow();
        if (selectedRow == -1) {
            lineItemsTableModel.setRowCount(0);
            return;
        }
        
        try {
            Integer orderId = (Integer) ordersTableModel.getValueAt(selectedRow, 0);
            List<LineItem> lineItems = lineItemRepository.findByOrderIdOrderById(orderId);
            
            lineItemsTableModel.setRowCount(0);
            
            for (LineItem item : lineItems) {
                // Obtener información del libro
                Optional<Book> bookOpt = bookRepository.findById(item.getIsbn());
                String bookTitle = bookOpt.map(Book::getTitle).orElse("Libro no encontrado");
                Double bookPrice = bookOpt.map(book -> book.getPrice().doubleValue()).orElse(0.0);
                Double subtotal = bookPrice * item.getQuantity();
                
                lineItemsTableModel.addRow(new Object[]{
                    item.getId(),
                    item.getIsbn(),
                    bookTitle,
                    item.getQuantity(),
                    "$" + String.format("%.2f", bookPrice),
                    "$" + String.format("%.2f", subtotal)
                });
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Error al cargar productos: " + e.getMessage());
        }
    }

    private void addOrder() {
        try {
            // Diálogo para nueva orden
            JDialog dialog = new JDialog((JFrame) SwingUtilities.getWindowAncestor(this), "Nueva Orden de Compra", true);
            dialog.setLayout(new GridBagLayout());
            GridBagConstraints gbc = new GridBagConstraints();
            gbc.insets = new Insets(5, 5, 5, 5);

            // Selector de cliente
            JComboBox<Customer> customerCombo = new JComboBox<>();
            List<Customer> customers = customerRepository.findAllByOrderByName();
            for (Customer customer : customers) {
                customerCombo.addItem(customer);
            }

            // Campos de la orden
            JTextField totalField = new JTextField("0", 10);
            JComboBox<String> statusCombo = new JComboBox<>(new String[]{"Pendiente", "Entregado"});
            
            // Layout
            gbc.gridx = 0; gbc.gridy = 0; dialog.add(new JLabel("Cliente:"), gbc);
            gbc.gridx = 1; dialog.add(customerCombo, gbc);
            
            gbc.gridx = 0; gbc.gridy = 1; dialog.add(new JLabel("Total:"), gbc);
            gbc.gridx = 1; dialog.add(totalField, gbc);
            
            gbc.gridx = 0; gbc.gridy = 2; dialog.add(new JLabel("Estado:"), gbc);
            gbc.gridx = 1; dialog.add(statusCombo, gbc);

            // Botones
            JPanel buttonPanel = new JPanel();
            JButton saveButton = new JButton("Guardar");
            JButton cancelButton = new JButton("Cancelar");
            
            saveButton.addActionListener(e -> {
                try {
                    Customer selectedCustomer = (Customer) customerCombo.getSelectedItem();
                    if (selectedCustomer == null) {
                        JOptionPane.showMessageDialog(dialog, "Debe seleccionar un cliente.");
                        return;
                    }
                    
                    int total = Integer.parseInt(totalField.getText().trim());
                    short status = statusCombo.getSelectedIndex() == 0 ? (short) 1 : (short) 2;
                    
                    PurchaseOrder order = new PurchaseOrder();
                    order.setCustomerId(selectedCustomer.getId());
                    order.setTotal(total);
                    order.setStatus(status);
                    order.setPlacedOn(LocalDateTime.now());
                    if (status == 2) {
                        order.setDeliveredOn(LocalDateTime.now());
                    }
                    
                    purchaseOrderRepository.save(order);
                    dialog.dispose();
                    loadOrders();
                    JOptionPane.showMessageDialog(this, "Orden creada exitosamente.");
                } catch (NumberFormatException ex) {
                    JOptionPane.showMessageDialog(dialog, "El total debe ser un número válido.");
                } catch (Exception ex) {
                    JOptionPane.showMessageDialog(dialog, "Error al crear orden: " + ex.getMessage());
                }
            });
            
            cancelButton.addActionListener(e -> dialog.dispose());
            
            buttonPanel.add(saveButton);
            buttonPanel.add(cancelButton);
            
            gbc.gridx = 0; gbc.gridy = 3; gbc.gridwidth = 2;
            dialog.add(buttonPanel, gbc);
            
            dialog.pack();
            dialog.setLocationRelativeTo(this);
            dialog.setVisible(true);
            
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Error al crear nueva orden: " + e.getMessage());
        }
    }

    private void editOrder() {
        int selectedRow = ordersTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Por favor, seleccione una orden para editar.");
            return;
        }
        
        try {
            Integer orderId = (Integer) ordersTableModel.getValueAt(selectedRow, 0);
            Optional<PurchaseOrder> orderOpt = purchaseOrderRepository.findById(orderId);
            
            if (orderOpt.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Orden no encontrada.");
                return;
            }
            
            PurchaseOrder order = orderOpt.get();
            
            // Diálogo similar al de agregar pero pre-cargado
            // Por simplicidad, mostraremos solo los campos editables principales
            String totalStr = JOptionPane.showInputDialog(this, "Total:", order.getTotal());
            if (totalStr != null && !totalStr.trim().isEmpty()) {
                try {
                    int total = Integer.parseInt(totalStr.trim());
                    order.setTotal(total);
                    
                    String[] options = {"Pendiente", "Entregado"};
                    int statusChoice = JOptionPane.showOptionDialog(this, "Estado de la orden:",
                        "Editar Estado", JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE,
                        null, options, order.getStatus() == 1 ? options[0] : options[1]);
                    
                    if (statusChoice != -1) {
                        order.setStatus((short) (statusChoice + 1));
                        if (order.getStatus() == 2 && order.getDeliveredOn() == null) {
                            order.setDeliveredOn(LocalDateTime.now());
                        }
                        
                        purchaseOrderRepository.save(order);
                        loadOrders();
                        JOptionPane.showMessageDialog(this, "Orden actualizada exitosamente.");
                    }
                } catch (NumberFormatException e) {
                    JOptionPane.showMessageDialog(this, "El total debe ser un número válido.");
                }
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Error al editar orden: " + e.getMessage());
        }
    }

    private void deleteOrder() {
        int selectedRow = ordersTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Por favor, seleccione una orden para eliminar.");
            return;
        }
        
        try {
            Integer orderId = (Integer) ordersTableModel.getValueAt(selectedRow, 0);
            
            int confirm = JOptionPane.showConfirmDialog(this,
                "¿Está seguro de que desea eliminar esta orden? Esto también eliminará todos los productos asociados.",
                "Confirmar Eliminación", JOptionPane.YES_NO_OPTION);
                
            if (confirm == JOptionPane.YES_OPTION) {
                purchaseOrderRepository.deleteById(orderId);
                loadOrders();
                lineItemsTableModel.setRowCount(0);
                JOptionPane.showMessageDialog(this, "Orden eliminada exitosamente.");
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Error al eliminar orden: " + e.getMessage());
        }
    }

    private void addLineItem() {
        int selectedRow = ordersTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Por favor, seleccione una orden primero.");
            return;
        }
        
        try {
            Integer orderId = (Integer) ordersTableModel.getValueAt(selectedRow, 0);
            
            // Diálogo para agregar producto
            JDialog dialog = new JDialog((JFrame) SwingUtilities.getWindowAncestor(this), "Agregar Producto", true);
            dialog.setLayout(new GridBagLayout());
            GridBagConstraints gbc = new GridBagConstraints();
            gbc.insets = new Insets(5, 5, 5, 5);

            // Selector de libro
            JComboBox<Book> bookCombo = new JComboBox<>();
            Iterable<Book> booksIterable = bookRepository.findAll();
            for (Book book : booksIterable) {
                bookCombo.addItem(book);
            }
            
            JTextField quantityField = new JTextField("1", 10);

            // Layout
            gbc.gridx = 0; gbc.gridy = 0; dialog.add(new JLabel("Libro:"), gbc);
            gbc.gridx = 1; dialog.add(bookCombo, gbc);
            
            gbc.gridx = 0; gbc.gridy = 1; dialog.add(new JLabel("Cantidad:"), gbc);
            gbc.gridx = 1; dialog.add(quantityField, gbc);

            // Botones
            JPanel buttonPanel = new JPanel();
            JButton saveButton = new JButton("Agregar");
            JButton cancelButton = new JButton("Cancelar");
            
            saveButton.addActionListener(e -> {
                try {
                    Book selectedBook = (Book) bookCombo.getSelectedItem();
                    if (selectedBook == null) {
                        JOptionPane.showMessageDialog(dialog, "Debe seleccionar un libro.");
                        return;
                    }
                    
                    int quantity = Integer.parseInt(quantityField.getText().trim());
                    if (quantity <= 0) {
                        JOptionPane.showMessageDialog(dialog, "La cantidad debe ser mayor a 0.");
                        return;
                    }
                    
                    LineItem lineItem = new LineItem();
                    lineItem.setOrderId(orderId);
                    lineItem.setQuantity(quantity);
                    lineItem.setIsbn(selectedBook.getIsbn());
                    
                    lineItemRepository.save(lineItem);
                    dialog.dispose();
                    loadLineItemsForSelectedOrder();
                    JOptionPane.showMessageDialog(this, "Producto agregado exitosamente.");
                } catch (NumberFormatException ex) {
                    JOptionPane.showMessageDialog(dialog, "La cantidad debe ser un número válido.");
                } catch (Exception ex) {
                    JOptionPane.showMessageDialog(dialog, "Error al agregar producto: " + ex.getMessage());
                }
            });
            
            cancelButton.addActionListener(e -> dialog.dispose());
            
            buttonPanel.add(saveButton);
            buttonPanel.add(cancelButton);
            
            gbc.gridx = 0; gbc.gridy = 2; gbc.gridwidth = 2;
            dialog.add(buttonPanel, gbc);
            
            dialog.pack();
            dialog.setLocationRelativeTo(this);
            dialog.setVisible(true);
            
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Error al agregar producto: " + e.getMessage());
        }
    }

    private void editLineItem() {
        int selectedRow = lineItemsTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Por favor, seleccione un producto para editar.");
            return;
        }
        
        try {
            Integer lineItemId = (Integer) lineItemsTableModel.getValueAt(selectedRow, 0);
            Optional<LineItem> lineItemOpt = lineItemRepository.findById(lineItemId);
            
            if (lineItemOpt.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Producto no encontrado.");
                return;
            }
            
            LineItem lineItem = lineItemOpt.get();
            
            String quantityStr = JOptionPane.showInputDialog(this, "Cantidad:", lineItem.getQuantity());
            if (quantityStr != null && !quantityStr.trim().isEmpty()) {
                try {
                    int quantity = Integer.parseInt(quantityStr.trim());
                    if (quantity <= 0) {
                        JOptionPane.showMessageDialog(this, "La cantidad debe ser mayor a 0.");
                        return;
                    }
                    
                    lineItem.setQuantity(quantity);
                    lineItemRepository.save(lineItem);
                    loadLineItemsForSelectedOrder();
                    JOptionPane.showMessageDialog(this, "Producto actualizado exitosamente.");
                } catch (NumberFormatException e) {
                    JOptionPane.showMessageDialog(this, "La cantidad debe ser un número válido.");
                }
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Error al editar producto: " + e.getMessage());
        }
    }

    private void deleteLineItem() {
        int selectedRow = lineItemsTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Por favor, seleccione un producto para eliminar.");
            return;
        }
        
        try {
            Integer lineItemId = (Integer) lineItemsTableModel.getValueAt(selectedRow, 0);
            
            int confirm = JOptionPane.showConfirmDialog(this,
                "¿Está seguro de que desea eliminar este producto de la orden?",
                "Confirmar Eliminación", JOptionPane.YES_NO_OPTION);
                
            if (confirm == JOptionPane.YES_OPTION) {
                lineItemRepository.deleteById(lineItemId);
                loadLineItemsForSelectedOrder();
                JOptionPane.showMessageDialog(this, "Producto eliminado exitosamente.");
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Error al eliminar producto: " + e.getMessage());
        }
    }

    private void viewCustomerDetails() {
        int selectedRow = ordersTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Por favor, seleccione una orden primero.");
            return;
        }
        
        try {
            Integer orderId = (Integer) ordersTableModel.getValueAt(selectedRow, 0);
            Optional<PurchaseOrder> orderOpt = purchaseOrderRepository.findById(orderId);
            
            if (orderOpt.isPresent()) {
                PurchaseOrder order = orderOpt.get();
                Optional<Customer> customerOpt = customerRepository.findById(order.getCustomerId());
                
                if (customerOpt.isPresent()) {
                    Customer customer = customerOpt.get();
                    String message = String.format(
                        "Cliente: %s\nEmail: %s\nVersión: %d",
                        customer.getName(),
                        customer.getEmail(),
                        customer.getVersion()
                    );
                    JOptionPane.showMessageDialog(this, message, "Detalles del Cliente", JOptionPane.INFORMATION_MESSAGE);
                } else {
                    JOptionPane.showMessageDialog(this, "Cliente no encontrado.");
                }
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Error al obtener detalles del cliente: " + e.getMessage());
        }
    }

    private void viewBookDetails() {
        int selectedRow = lineItemsTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Por favor, seleccione un producto primero.");
            return;
        }
        
        try {
            String isbn = (String) lineItemsTableModel.getValueAt(selectedRow, 1);
            Optional<Book> bookOpt = bookRepository.findById(isbn);
            
            if (bookOpt.isPresent()) {
                Book book = bookOpt.get();
                String message = String.format(
                    "ISBN: %s\nTítulo: %s\nPrecio: $%.2f\nVersión: %d",
                    book.getIsbn(),
                    book.getTitle(),
                    book.getPrice(),
                    book.getVersion()
                );
                JOptionPane.showMessageDialog(this, message, "Detalles del Libro", JOptionPane.INFORMATION_MESSAGE);
            } else {
                JOptionPane.showMessageDialog(this, "Libro no encontrado.");
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Error al obtener detalles del libro: " + e.getMessage());
        }
    }
}
