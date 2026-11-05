package com.programacion.UI;

import com.programacion.db.Author;
import com.programacion.repository.AuthorRepository;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;


public class AuthorPanel extends JPanel {
    private final AuthorRepository authorRepository;
    private JTable table;
    private DefaultTableModel tableModel;

    public AuthorPanel(AuthorRepository authorRepository) {
        this.authorRepository = authorRepository;
        setLayout(new BorderLayout());

        // Configurar la tabla
        tableModel = new DefaultTableModel(new String[]{"ID", "Nombre", "Versión"}, 0);
        table = new JTable(tableModel);
        loadAuthors();

        // Panel de búsqueda
        JPanel searchPanel = new JPanel(new FlowLayout());
        JTextField searchField = new JTextField(20);
        JButton searchByNameButton = new JButton("Buscar por Nombre");
        JButton showAllButton = new JButton("Mostrar Todos");

        searchByNameButton.addActionListener(e -> searchByName(searchField.getText().trim()));
        showAllButton.addActionListener(e -> loadAuthors());

        searchPanel.add(new JLabel("Buscar:"));
        searchPanel.add(searchField);
        searchPanel.add(searchByNameButton);
        searchPanel.add(showAllButton);

        // Botones CRUD
        JPanel buttonPanel = new JPanel();
        JButton addButton = new JButton("Agregar");
        JButton updateButton = new JButton("Actualizar");
        JButton deleteButton = new JButton("Eliminar");

        addButton.addActionListener(e -> addAuthor());
        updateButton.addActionListener(e -> updateAuthor());
        deleteButton.addActionListener(e -> deleteAuthor());

        buttonPanel.add(addButton);
        buttonPanel.add(updateButton);
        buttonPanel.add(deleteButton);

        add(searchPanel, BorderLayout.NORTH);
        add(new JScrollPane(table), BorderLayout.CENTER);
        add(buttonPanel, BorderLayout.SOUTH);
    }

    private void loadAuthors() {
        tableModel.setRowCount(0);
        Iterable<Author> authors = authorRepository.findAll();
        for (Author author : authors) {
            tableModel.addRow(new Object[]{author.getId(), author.getName(), author.getVersion()});
        }
    }

    private void addAuthor() {
        JTextField nameField = new JTextField();
        JTextField versionField = new JTextField("0");

        JPanel panel = new JPanel(new GridLayout(2, 2));
        panel.add(new JLabel("Nombre completo del autor:"));
        panel.add(nameField);
        panel.add(new JLabel("Versión:"));
        panel.add(versionField);

        int result = JOptionPane.showConfirmDialog(this, panel, "Agregar Autor", JOptionPane.OK_CANCEL_OPTION);
        if (result == JOptionPane.OK_OPTION) {
            String name = nameField.getText().trim();
            String versionStr = versionField.getText().trim();

            if (name.isEmpty()) {
                JOptionPane.showMessageDialog(this, "El nombre es obligatorio.", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            try {
                int version = Integer.parseInt(versionStr);
                if (version < 0) {
                    JOptionPane.showMessageDialog(this, "La versión debe ser un número mayor o igual a 0.", "Error", JOptionPane.ERROR_MESSAGE);
                    return;
                }

                Author author = new Author();
                author.setName(name);
                author.setVersion(version);

                authorRepository.save(author);
                loadAuthors();
                JOptionPane.showMessageDialog(this, "Autor agregado exitosamente.", "Éxito", JOptionPane.INFORMATION_MESSAGE);
            } catch (NumberFormatException e) {
                JOptionPane.showMessageDialog(this, "La versión debe ser un número válido.", "Error", JOptionPane.ERROR_MESSAGE);
            } catch (Exception e) {
                JOptionPane.showMessageDialog(this, "Error al agregar autor: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void updateAuthor() {
        int selectedRow = table.getSelectedRow();
        if (selectedRow >= 0) {
            int id = (int) tableModel.getValueAt(selectedRow, 0);
            Author author = authorRepository.findById(id).orElse(null);

            if (author == null) {
                JOptionPane.showMessageDialog(this, "Autor no encontrado.", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            JTextField nameField = new JTextField(author.getName());
            JTextField versionField = new JTextField(String.valueOf(author.getVersion()));

            JPanel panel = new JPanel(new GridLayout(2, 2));
            panel.add(new JLabel("Nombre completo del autor:"));
            panel.add(nameField);
            panel.add(new JLabel("Versión:"));
            panel.add(versionField);

            int result = JOptionPane.showConfirmDialog(this, panel, "Actualizar Autor", JOptionPane.OK_CANCEL_OPTION);
            if (result == JOptionPane.OK_OPTION) {
                String name = nameField.getText().trim();
                String versionStr = versionField.getText().trim();

                if (name.isEmpty()) {
                    JOptionPane.showMessageDialog(this, "El nombre es obligatorio.", "Error", JOptionPane.ERROR_MESSAGE);
                    return;
                }

                try {
                    int version = Integer.parseInt(versionStr);
                    if (version < 0) {
                        JOptionPane.showMessageDialog(this, "La versión debe ser un número mayor o igual a 0.", "Error", JOptionPane.ERROR_MESSAGE);
                        return;
                    }

                    author.setName(name);
                    author.setVersion(version);

                    authorRepository.save(author);
                    loadAuthors();
                    JOptionPane.showMessageDialog(this, "Autor actualizado exitosamente.", "Éxito", JOptionPane.INFORMATION_MESSAGE);
                } catch (NumberFormatException e) {
                    JOptionPane.showMessageDialog(this, "La versión debe ser un número válido.", "Error", JOptionPane.ERROR_MESSAGE);
                } catch (Exception e) {
                    JOptionPane.showMessageDialog(this, "Error al actualizar autor: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
                    // Recargar para mostrar la versión actual
                    loadAuthors();
                }
            }
        } else {
            JOptionPane.showMessageDialog(this, "Seleccione un autor para actualizar.");
        }
    }

    private void deleteAuthor() {
        int selectedRow = table.getSelectedRow();
        if (selectedRow >= 0) {
            int id = (int) tableModel.getValueAt(selectedRow, 0);
            int confirm = JOptionPane.showConfirmDialog(this,
                    "¿Estás seguro de eliminar este autor?",
                    "Confirmar eliminación",
                    JOptionPane.YES_NO_OPTION);

            if (confirm == JOptionPane.YES_OPTION) {
                try {
                    authorRepository.deleteById(id);
                    loadAuthors();
                    JOptionPane.showMessageDialog(this,
                            "Autor eliminado con éxito.",
                            "Éxito",
                            JOptionPane.INFORMATION_MESSAGE);
                } catch (Exception ex) {
                    JOptionPane.showMessageDialog(this,
                            "No se puede eliminar el autor porque tiene libros asociados.\n" +
                                    "Elimine o reasigne sus libros antes de intentar eliminarlo.",
                            "Error de eliminación",
                            JOptionPane.ERROR_MESSAGE);
                }
            }
        } else {
            JOptionPane.showMessageDialog(this,
                    "Seleccione un autor para eliminar.",
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
        }
    }

    private void searchByName(String name) {
        if (name.isEmpty()) {
            loadAuthors();
            return;
        }

        tableModel.setRowCount(0);
        var authors = authorRepository.findByNameContainingIgnoreCase(name);
        for (Author author : authors) {
            List<Author> authors1 = authorRepository.findByNameContainingIgnoreCase(author.getName());
            String authorsStr = authors.stream()
                    .map(Author::getName)
                    .reduce((a, b) -> a + ", " + b)
                    .orElse("Sin autor");

            tableModel.addRow(new Object[]{
                    author.getId(),
                    author.getName(),
                    author.getVersion()
            });
        }
    }
}
