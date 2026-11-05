package com.programacion.UI;

import com.programacion.db.Author;
import com.programacion.db.Book;
import com.programacion.repository.*;
import com.programacion.services.BookService;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

public class BookPanel extends JPanel {
    private final BookRepository bookRepository;
    private final AuthorRepository authorRepository;
    private final BookService bookService;
    private JTable table;
    private DefaultTableModel tableModel;

    public BookPanel(BookRepository bookRepository, AuthorRepository authorRepository, BookService bookService) {
        this.bookRepository = bookRepository;
        this.authorRepository = authorRepository;
        this.bookService = bookService;

        setLayout(new BorderLayout());

        // Configurar la tabla
        tableModel = new DefaultTableModel(new String[]{"ISBN", "Título", "Precio", "Versión", "Autores"}, 0);
        table = new JTable(tableModel);
        loadBooks();

        // Panel de búsqueda
        JPanel searchPanel = new JPanel(new FlowLayout());
        JTextField searchField = new JTextField(20);
        JButton searchByNameButton = new JButton("Buscar por Author");
        JButton showAllButton = new JButton("Mostrar Todos");

        searchByNameButton.addActionListener(e -> searchByName(searchField.getText().trim()));
        showAllButton.addActionListener(e -> loadBooks());

        searchPanel.add(new JLabel("Buscar:"));
        searchPanel.add(searchField);
        searchPanel.add(searchByNameButton);
        searchPanel.add(showAllButton);


        // Botones CRUD
        JPanel buttonPanel = new JPanel();
        JButton addButton = new JButton("Agregar");
        JButton updateButton = new JButton("Actualizar");
        JButton deleteButton = new JButton("Eliminar");

        addButton.addActionListener(e -> addBook());
        updateButton.addActionListener(e -> updateBook());
        deleteButton.addActionListener(e -> deleteBook());

        buttonPanel.add(addButton);
        buttonPanel.add(updateButton);
        buttonPanel.add(deleteButton);

        add(searchPanel, BorderLayout.NORTH);
        add(new JScrollPane(table), BorderLayout.CENTER);
        add(buttonPanel, BorderLayout.SOUTH);
    }

    private void loadBooks() {
        tableModel.setRowCount(0);
        Iterable<Book> books = bookRepository.findAll();
        for (Book book : books) {
            // Obtener los autores de este libro
            List<Author> authors = bookRepository.findAuthorsByBookIsbn(book.getIsbn());
            String authorsStr = authors.stream()
                    .map(Author::getName)
                    .reduce((a, b) -> a + ", " + b)
                    .orElse("Sin autor");

            tableModel.addRow(new Object[]{
                    book.getIsbn(),
                    book.getTitle(),
                    book.getPrice(),
                    book.getVersion(),
                    authorsStr
            });
        }
    }

    private void addBook() {
        JTextField titleField = new JTextField();
        JTextField isbnField = new JTextField();
        JTextField priceField = new JTextField();
        JTextField versionField = new JTextField("0");
        JTextField authorIdField = new JTextField();

        JPanel panel = new JPanel(new GridLayout(5, 2));
        panel.add(new JLabel("Título:"));
        panel.add(titleField);
        panel.add(new JLabel("ISBN:"));
        panel.add(isbnField);
        panel.add(new JLabel("Precio:"));
        panel.add(priceField);
        panel.add(new JLabel("Versión:"));
        panel.add(versionField);
        panel.add(new JLabel("ID del Autor:"));
        panel.add(authorIdField);

        int result = JOptionPane.showConfirmDialog(this, panel, "Agregar Libro", JOptionPane.OK_CANCEL_OPTION);
        if (result == JOptionPane.OK_OPTION) {
            try {
                String title = titleField.getText().trim();
                String isbn = isbnField.getText().trim();
                double price = Double.parseDouble(priceField.getText().trim());
                int version = Integer.parseInt(versionField.getText().trim());
                int authorId = Integer.parseInt(authorIdField.getText().trim());

                // Validar campos
                if (title.isEmpty() || isbn.isEmpty() || price <= 0 || version < 0 || authorId <= 0) {
                    JOptionPane.showMessageDialog(this, "Todos los campos son obligatorios, el precio debe ser mayor a cero y la versión mayor o igual a cero.", "Error", JOptionPane.ERROR_MESSAGE);
                    return;
                }

                // Verificar que el autor existe
                Optional<Author> authorOpt = authorRepository.findById(authorId);
                if (!authorOpt.isPresent()) {
                    JOptionPane.showMessageDialog(this, "Autor no encontrado", "Error", JOptionPane.ERROR_MESSAGE);
                    return;
                }

                // Crear el libro
                Book book = new Book();
                book.setTitle(title);
                book.setIsbn(isbn);
                book.setPrice(BigDecimal.valueOf(price));
                book.setVersion(version);

                // Guardar el libro
                bookRepository.save(book);

                // Asociar el autor al libro
                bookRepository.addAuthorToBook(isbn, authorId);

                loadBooks();
                JOptionPane.showMessageDialog(this, "Libro agregado exitosamente.", "Éxito", JOptionPane.INFORMATION_MESSAGE);

            } catch (NumberFormatException e) {
                JOptionPane.showMessageDialog(this, "Formato de número inválido para el precio, versión o el ID del autor", "Error", JOptionPane.ERROR_MESSAGE);
            } catch (Exception e) {
                JOptionPane.showMessageDialog(this, "Error al agregar libro: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void updateBook() {
        int selectedRow = table.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Seleccione un libro para actualizar", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        String isbn = (String) tableModel.getValueAt(selectedRow, 0);
        Optional<Book> bookOpt = bookRepository.findById(isbn);

        if (!bookOpt.isPresent()) {
            JOptionPane.showMessageDialog(this, "Libro no encontrado.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        Book book = bookOpt.get();
        JTextField titleField = new JTextField(book.getTitle());
        JTextField isbnField = new JTextField(book.getIsbn());
        JTextField priceField = new JTextField(book.getPrice().toString());
        JTextField versionField = new JTextField(String.valueOf(book.getVersion()));

        JPanel panel = new JPanel(new GridLayout(4, 2));
        panel.add(new JLabel("Titulo:"));
        panel.add(titleField);
        panel.add(new JLabel("ISBN:"));
        panel.add(isbnField);
        panel.add(new JLabel("Precio:"));
        panel.add(priceField);
        panel.add(new JLabel("Versión:"));
        panel.add(versionField);

        int result = JOptionPane.showConfirmDialog(this, panel, "Actualizar Libro", JOptionPane.OK_CANCEL_OPTION);
        if (result == JOptionPane.OK_OPTION) {
            String newTitle = titleField.getText().trim();
            String newIsbn = isbnField.getText().trim();
            String newPrice = priceField.getText().trim();
            String newVersion = versionField.getText().trim();

            // Validación de campos
            if (newTitle.isEmpty() || newIsbn.isEmpty() || newPrice.isEmpty() || newVersion.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Todos los campos son obligatorios.", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            try {
                BigDecimal newPriceValue = new BigDecimal(newPrice.trim());
                int versionValue = Integer.parseInt(newVersion.trim());

                if (newPriceValue.compareTo(BigDecimal.ZERO) <= 0) {
                    JOptionPane.showMessageDialog(this, "El precio debe ser mayor a cero.", "Error", JOptionPane.ERROR_MESSAGE);
                    return;
                }

                if (versionValue < 0) {
                    JOptionPane.showMessageDialog(this, "La versión debe ser mayor o igual a cero.", "Error", JOptionPane.ERROR_MESSAGE);
                    return;
                }

                book.setTitle(newTitle);
                book.setIsbn(newIsbn);
                book.setPrice(newPriceValue);
                book.setVersion(versionValue);

                bookRepository.save(book);
                loadBooks();
                JOptionPane.showMessageDialog(this, "Libro actualizado exitosamente", "Éxito", JOptionPane.INFORMATION_MESSAGE);

            } catch (NumberFormatException e) {
                JOptionPane.showMessageDialog(this, "Error en el formato del precio o la versión.", "Error", JOptionPane.ERROR_MESSAGE);
            } catch (Exception e) {
                JOptionPane.showMessageDialog(this, "Error al actualizar libro: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void deleteBook() {
        int selectedRow = table.getSelectedRow();
        if (selectedRow >= 0) {
            String isbn = (String) tableModel.getValueAt(selectedRow, 0);
            int confirm = JOptionPane.showConfirmDialog(
                    this,
                    "¿Estás seguro de eliminar este libro?",
                    "Confirmar eliminación",
                    JOptionPane.YES_NO_OPTION
            );

            if (confirm == JOptionPane.YES_OPTION) {
                try {
                    bookService.deleteBook(isbn);
                    loadBooks();
                    JOptionPane.showMessageDialog(this, "Libro eliminado exitosamente", "Éxito", JOptionPane.INFORMATION_MESSAGE);
                } catch (Exception e) {
                    JOptionPane.showMessageDialog(this, "Error al eliminar libro: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
                }
            }
        } else {
            JOptionPane.showMessageDialog(this, "Seleccione un libro para eliminar.", "Error", JOptionPane.ERROR_MESSAGE);
        }

    }
    private void searchByName(String name) {
        if (name.isEmpty()) {
            loadBooks();
            return;
        }

        tableModel.setRowCount(0);
        var books = bookService.getBooksByAuthor(name);
        for (Book book : books) {
            List<Author> authors = bookRepository.findAuthorsByBookIsbn(book.getIsbn());
            String authorsStr = authors.stream()
                    .map(Author::getName)
                    .reduce((a, b) -> a + ", " + b)
                    .orElse("Sin autor");

            tableModel.addRow(new Object[]{
                    book.getIsbn(),
                    book.getTitle(),
                    book.getPrice(),
                    book.getVersion(),
                    authorsStr
            });
        }
    }





}
