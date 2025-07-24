import java.awt.*;
import java.sql.*;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;

public class ProductManager extends JFrame {
    private JTable table;
    private DefaultTableModel model;

    private JTextField nameField, priceField, stockField;
    private JButton addBtn, updateBtn, deleteBtn;

    private Connection conn;

    public ProductManager() {
        setTitle("Product Manager");
        setSize(600, 400);
        setLayout(new BorderLayout());

        try {
            conn = DatabaseConnection.getConnection();
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Database connection error.");
            return;
        }

        setupUI();
        loadProducts();
    }

    private void setupUI() {
        model = new DefaultTableModel(new String[]{"ID", "Name", "Price", "Stock"}, 0);
        table = new JTable(model);
        JScrollPane tablePane = new JScrollPane(table);

        JPanel formPanel = new JPanel(new GridLayout(2, 4, 10, 10));
        nameField = new JTextField();
        priceField = new JTextField();
        stockField = new JTextField();

        formPanel.add(new JLabel("Name:"));
        formPanel.add(nameField);
        formPanel.add(new JLabel("Price:"));
        formPanel.add(priceField);
        formPanel.add(new JLabel("Stock:"));
        formPanel.add(stockField);

        addBtn = new JButton("Add");
        updateBtn = new JButton("Update");
        deleteBtn = new JButton("Delete");

        JPanel buttonPanel = new JPanel();
        buttonPanel.add(addBtn);
        buttonPanel.add(updateBtn);
        buttonPanel.add(deleteBtn);

        add(tablePane, BorderLayout.CENTER);
        add(formPanel, BorderLayout.NORTH);
        add(buttonPanel, BorderLayout.SOUTH);

        addBtn.addActionListener(e -> addProduct());
        updateBtn.addActionListener(e -> updateProduct());
        deleteBtn.addActionListener(e -> deleteProduct());

        table.getSelectionModel().addListSelectionListener(e -> {
            int row = table.getSelectedRow();
            if (row != -1) {
                nameField.setText(model.getValueAt(row, 1).toString());
                priceField.setText(model.getValueAt(row, 2).toString());
                stockField.setText(model.getValueAt(row, 3).toString());
            }
        });
    }

    private void loadProducts() {
        model.setRowCount(0);
        try (Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery("SELECT * FROM products")) {

            while (rs.next()) {
                model.addRow(new Object[]{
                        rs.getInt("product_id"),
                        rs.getString("name"),
                        rs.getDouble("price"),
                        rs.getInt("stock")
                });
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void addProduct() {
        String name = nameField.getText();
        String priceStr = priceField.getText();
        String stockStr = stockField.getText();

        if (name.isEmpty() || priceStr.isEmpty() || stockStr.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please fill all fields.");
            return;
        }

        try {
            double price = Double.parseDouble(priceStr);
            int stock = Integer.parseInt(stockStr);

            PreparedStatement ps = conn.prepareStatement(
                "INSERT INTO products (name, price, stock) VALUES (?, ?, ?)"
            );
            ps.setString(1, name);
            ps.setDouble(2, price);
            ps.setInt(3, stock);
            ps.executeUpdate();

            loadProducts();
            clearFields();
            JOptionPane.showMessageDialog(this, "✅ Product added!");

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void updateProduct() {
        int row = table.getSelectedRow();
        if (row == -1) {
            JOptionPane.showMessageDialog(this, "Please select a product to update.");
            return;
        }

        int id = Integer.parseInt(model.getValueAt(row, 0).toString());
        String name = nameField.getText();
        String priceStr = priceField.getText();
        String stockStr = stockField.getText();

        if (name.isEmpty() || priceStr.isEmpty() || stockStr.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please fill all fields.");
            return;
        }

        try {
            double price = Double.parseDouble(priceStr);
            int stock = Integer.parseInt(stockStr);

            PreparedStatement ps = conn.prepareStatement(
                "UPDATE products SET name = ?, price = ?, stock = ? WHERE product_id = ?"
            );
            ps.setString(1, name);
            ps.setDouble(2, price);
            ps.setInt(3, stock);
            ps.setInt(4, id);
            ps.executeUpdate();

            loadProducts();
            clearFields();
            JOptionPane.showMessageDialog(this, "✅ Product updated!");

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void deleteProduct() {
        int row = table.getSelectedRow();
        if (row == -1) {
            JOptionPane.showMessageDialog(this, "Please select a product to delete.");
            return;
        }

        int id = Integer.parseInt(model.getValueAt(row, 0).toString());

        try {
            PreparedStatement ps = conn.prepareStatement("DELETE FROM products WHERE product_id = ?");
            ps.setInt(1, id);
            ps.executeUpdate();

            loadProducts();
            clearFields();
            JOptionPane.showMessageDialog(this, "🗑️ Product deleted.");

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void clearFields() {
        nameField.setText("");
        priceField.setText("");
        stockField.setText("");
    }
}
