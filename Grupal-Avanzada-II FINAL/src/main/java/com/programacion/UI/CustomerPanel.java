package com.programacion.UI;

import com.programacion.db.Customer;
import com.programacion.db.PurchaseOrder;
import com.programacion.repository.CustomerRepository;
import com.programacion.repository.PurchaseOrderRepository;
import org.springframework.stereotype.Component;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Optional;

@Component
public class CustomerPanel extends JPanel {
    private CustomerRepository customerRepository;
    private PurchaseOrderRepository purchaseOrderRepository;
    private JTable table;
    private DefaultTableModel tableModel;

    public CustomerPanel(CustomerRepository customerRepository, PurchaseOrderRepository purchaseOrderRepository) {
        this.customerRepository = customerRepository;
        this.purchaseOrderRepository = purchaseOrderRepository;
        setupUI();
        loadCustomers();
    }

    private void setupUI() {
        setLayout(new BorderLayout());

        // Configurar la tabla
        tableModel = new DefaultTableModel(new String[]{"ID", "Nombre", "Email", "Versión"}, 0);
        table = new JTable(tableModel);
        table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        loadCustomers();

        // Panel de búsqueda
        JPanel searchPanel = new JPanel(new FlowLayout());
        JTextField searchField = new JTextField(20);
        JButton searchByNameButton = new JButton("Buscar por Nombre");
        JButton showAllButton = new JButton("Mostrar Todos");

        searchByNameButton.addActionListener(e -> searchByName(searchField.getText().trim()));
        showAllButton.addActionListener(e -> loadCustomers());

        searchPanel.add(new JLabel("Buscar:"));
        searchPanel.add(searchField);
        searchPanel.add(searchByNameButton);
        searchPanel.add(showAllButton);

        // Botones CRUD
        JPanel buttonPanel = new JPanel();
        JButton addButton = new JButton("Agregar Cliente");
        JButton updateButton = new JButton("Actualizar Cliente");
        JButton deleteButton = new JButton("Eliminar Cliente");
        JButton viewOrdersButton = new JButton("Ver Órdenes");

        addButton.addActionListener(e -> addCustomer());
        updateButton.addActionListener(e -> updateCustomer());
        deleteButton.addActionListener(e -> deleteCustomer());
        viewOrdersButton.addActionListener(e -> viewCustomerOrders());

        buttonPanel.add(addButton);
        buttonPanel.add(updateButton);
        buttonPanel.add(deleteButton);
        buttonPanel.add(viewOrdersButton);

        add(searchPanel, BorderLayout.NORTH);
        add(new JScrollPane(table), BorderLayout.CENTER);
        add(buttonPanel, BorderLayout.SOUTH);
    }

    private void loadCustomers() {
        tableModel.setRowCount(0);
        Iterable<Customer> customers = customerRepository.findAll();
        for (Customer customer : customers) {
            tableModel.addRow(new Object[]{
                customer.getId(),
                customer.getName(),
                customer.getEmail(),
                customer.getVersion()
            });
        }
    }

    private void searchByName(String name) {
        if (name.isEmpty()) {
            loadCustomers();
            return;
        }
        
        tableModel.setRowCount(0);
        var customers = customerRepository.findByNameContainingIgnoreCase(name);
        for (Customer customer : customers) {
            tableModel.addRow(new Object[]{
                customer.getId(),
                customer.getName(),
                customer.getEmail(),
                customer.getVersion()
            });
        }
    }

    private void addCustomer() {
        JTextField nameField = new JTextField();
        JTextField emailField = new JTextField();
        JTextField versionField = new JTextField("0");

        JPanel panel = new JPanel(new GridLayout(3, 2));
        panel.add(new JLabel("Nombre:"));
        panel.add(nameField);
        panel.add(new JLabel("Email:"));
        panel.add(emailField);
        panel.add(new JLabel("Versión:"));
        panel.add(versionField);

        int result = JOptionPane.showConfirmDialog(this, panel, "Agregar Cliente", JOptionPane.OK_CANCEL_OPTION);
        if (result == JOptionPane.OK_OPTION) {
            try {
                String name = nameField.getText().trim();
                String email = emailField.getText().trim();
                int version = Integer.parseInt(versionField.getText().trim());

                if (name.isEmpty() || email.isEmpty()) {
                    JOptionPane.showMessageDialog(this, "Nombre y email son obligatorios.", "Error", JOptionPane.ERROR_MESSAGE);
                    return;
                }

                if (version < 0) {
                    JOptionPane.showMessageDialog(this, "La versión debe ser mayor o igual a 0.", "Error", JOptionPane.ERROR_MESSAGE);
                    return;
                }

                Customer customer = new Customer();
                customer.setName(name);
                customer.setEmail(email);
                customer.setVersion(version);

                customerRepository.save(customer);
                loadCustomers();
                JOptionPane.showMessageDialog(this, "Cliente agregado exitosamente.", "Éxito", JOptionPane.INFORMATION_MESSAGE);
            } catch (NumberFormatException e) {
                JOptionPane.showMessageDialog(this, "La versión debe ser un número válido.", "Error", JOptionPane.ERROR_MESSAGE);
            } catch (Exception e) {
                JOptionPane.showMessageDialog(this, "Error al agregar cliente: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void updateCustomer() {
        int selectedRow = table.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Seleccione un cliente para actualizar", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        int id = (int) tableModel.getValueAt(selectedRow, 0);
        Optional<Customer> customerOpt = customerRepository.findById(id);

        if (!customerOpt.isPresent()) {
            JOptionPane.showMessageDialog(this, "Cliente no encontrado.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        Customer customer = customerOpt.get();
        JTextField nameField = new JTextField(customer.getName());
        JTextField emailField = new JTextField(customer.getEmail());
        JTextField versionField = new JTextField(String.valueOf(customer.getVersion()));

        JPanel panel = new JPanel(new GridLayout(3, 2));
        panel.add(new JLabel("Nombre:"));
        panel.add(nameField);
        panel.add(new JLabel("Email:"));
        panel.add(emailField);
        panel.add(new JLabel("Versión:"));
        panel.add(versionField);

        int result = JOptionPane.showConfirmDialog(this, panel, "Actualizar Cliente", JOptionPane.OK_CANCEL_OPTION);
        if (result == JOptionPane.OK_OPTION) {
            try {
                String name = nameField.getText().trim();
                String email = emailField.getText().trim();
                int version = Integer.parseInt(versionField.getText().trim());

                if (name.isEmpty() || email.isEmpty()) {
                    JOptionPane.showMessageDialog(this, "Nombre y email son obligatorios.", "Error", JOptionPane.ERROR_MESSAGE);
                    return;
                }

                if (version < 0) {
                    JOptionPane.showMessageDialog(this, "La versión debe ser mayor o igual a 0.", "Error", JOptionPane.ERROR_MESSAGE);
                    return;
                }

                customer.setName(name);
                customer.setEmail(email);
                customer.setVersion(version);

                customerRepository.save(customer);
                loadCustomers();
                JOptionPane.showMessageDialog(this, "Cliente actualizado exitosamente.", "Éxito", JOptionPane.INFORMATION_MESSAGE);
            } catch (NumberFormatException e) {
                JOptionPane.showMessageDialog(this, "La versión debe ser un número válido.", "Error", JOptionPane.ERROR_MESSAGE);
            } catch (Exception e) {
                JOptionPane.showMessageDialog(this, "Error al actualizar cliente: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
                loadCustomers(); // Recargar en caso de error
            }
        }
    }

    private void deleteCustomer() {
        int selectedRow = table.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Seleccione un cliente para eliminar", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        int id = (int) tableModel.getValueAt(selectedRow, 0);
        String name = (String) tableModel.getValueAt(selectedRow, 1);

        int confirm = JOptionPane.showConfirmDialog(
            this,
            "¿Está seguro de eliminar el cliente: " + name + "?\nEsto eliminará también todas sus órdenes de compra.",
            "Confirmar eliminación",
            JOptionPane.YES_NO_OPTION
        );

        if (confirm == JOptionPane.YES_OPTION) {
            try {
                customerRepository.deleteById(id);
                loadCustomers();
                JOptionPane.showMessageDialog(this, "Cliente eliminado exitosamente", "Éxito", JOptionPane.INFORMATION_MESSAGE);
            } catch (Exception e) {
                JOptionPane.showMessageDialog(this, "Error al eliminar cliente: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void viewCustomerOrders() {
        int selectedRow = table.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Por favor selecciona un cliente");
            return;
        }

        Integer customerId = (Integer) tableModel.getValueAt(selectedRow, 0);
        List<PurchaseOrder> orders = purchaseOrderRepository.findByCustomerId(customerId);
        
        if (orders.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Este cliente no tiene órdenes de compra");
            return;
        }

        // Crear diálogo para mostrar las órdenes
        JDialog dialog = new JDialog((Frame) SwingUtilities.getWindowAncestor(this), "Órdenes del Cliente", true);
        dialog.setSize(800, 400);
        dialog.setLocationRelativeTo(this);

        // Modelo de tabla para las órdenes
        String[] orderColumns = {"ID", "Total", "Estado", "Fecha Entrega", "Fecha Pedido"};
        DefaultTableModel orderModel = new DefaultTableModel(orderColumns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");
        
        for (PurchaseOrder order : orders) {
            String placedOnStr = order.getPlacedOn() != null ? order.getPlacedOn().format(formatter) : "N/A";
            String deliveredOnStr = order.getDeliveredOn() != null ? order.getDeliveredOn().format(formatter) : "N/A";
            
            orderModel.addRow(new Object[]{
                order.getId(),
                String.format("$%.2f", order.getTotal() / 100.0), // Convertir centavos a pesos
                order.getStatus(),
                deliveredOnStr,
                placedOnStr
            });
        }

        JTable orderTable = new JTable(orderModel);
        orderTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        JScrollPane scrollPane = new JScrollPane(orderTable);

        JPanel panel = new JPanel(new BorderLayout());
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        panel.add(scrollPane, BorderLayout.CENTER);

        JButton closeButton = new JButton("Cerrar");
        closeButton.addActionListener(e -> dialog.dispose());
        
        JPanel buttonPanel = new JPanel();
        buttonPanel.add(closeButton);
        panel.add(buttonPanel, BorderLayout.SOUTH);

        dialog.add(panel);
        dialog.setVisible(true);
    }
}
