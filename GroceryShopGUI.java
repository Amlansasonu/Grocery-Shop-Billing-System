
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.sql.*;
import java.util.*;
import java.util.List;

public class GroceryShopGUI extends JFrame {
    private JComboBox<String> productBox;
    private JTextField quantityField;
    private JTextArea billArea;
    private JLabel totalLabel;
    private JButton generateReceiptBtn;

    private Connection conn;
    private double total = 0;
    private List<String> billLines = new ArrayList<>();

    public GroceryShopGUI() {
        setTitle("Grocery Shop Billing System");
        setSize(600, 500);
        setLayout(new BorderLayout());

        connectDatabase();
        setupUI();
        loadProducts();
    }

    private void connectDatabase() {
        try {
            conn = DatabaseConnection.getConnection();
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "❌ Database connection failed!");
            e.printStackTrace();
            System.exit(0);
        }
    }

    private void setupUI() {
        JMenuBar menuBar = new JMenuBar();
        JMenu menu = new JMenu("Options");
        JMenuItem manageProducts = new JMenuItem("Manage Products");
        manageProducts.addActionListener(e -> new ProductManager().setVisible(true));
        menu.add(manageProducts);
        menuBar.add(menu);
        setJMenuBar(menuBar);

        JPanel topPanel = new JPanel();
        productBox = new JComboBox<>();
        quantityField = new JTextField(5);
        JButton addButton = new JButton("Add to Bill");

        topPanel.add(new JLabel("Select Product:"));
        topPanel.add(productBox);
        topPanel.add(new JLabel("Qty:"));
        topPanel.add(quantityField);
        topPanel.add(addButton);

        billArea = new JTextArea();
        billArea.setEditable(false);
        JScrollPane scrollPane = new JScrollPane(billArea);

        JPanel bottomPanel = new JPanel(new BorderLayout());
        totalLabel = new JLabel("Total: ₹0.00");
        generateReceiptBtn = new JButton("Generate Receipt");
        generateReceiptBtn.addActionListener(e -> {
            ReceiptGenerator.generatePDF(billLines, total);
            JOptionPane.showMessageDialog(this, "Receipt saved as 'receipt.pdf'");
        });

        bottomPanel.add(totalLabel, BorderLayout.WEST);
        bottomPanel.add(generateReceiptBtn, BorderLayout.EAST);

        add(topPanel, BorderLayout.NORTH);
        add(scrollPane, BorderLayout.CENTER);
        add(bottomPanel, BorderLayout.SOUTH);

        addButton.addActionListener(e -> addToBill());
    }

    private void loadProducts() {
        try {
            Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery("SELECT name FROM products");
            while (rs.next()) {
                productBox.addItem(rs.getString("name"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void addToBill() {
        String productName = (String) productBox.getSelectedItem();
        String qtyStr = quantityField.getText();

        if (qtyStr.isEmpty() || !qtyStr.matches("\\d+")) {
            JOptionPane.showMessageDialog(this, "❗ Please enter a valid quantity.");
            return;
        }

        int quantity = Integer.parseInt(qtyStr);

        try {
            PreparedStatement ps = conn.prepareStatement("SELECT * FROM products WHERE name = ?");
            ps.setString(1, productName);
            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                double price = rs.getDouble("price");
                int stock = rs.getInt("stock");
                int id = rs.getInt("product_id");

                if (quantity > stock) {
                    JOptionPane.showMessageDialog(this, "⚠️ Not enough stock available!");
                    return;
                }

                if (stock - quantity < 10) {
                    JOptionPane.showMessageDialog(this, "⚠️ Low stock warning! Remaining stock will be < 10.");
                }

                double itemTotal = price * quantity;
                total += itemTotal;

                String line = String.format("%s x %d = ₹%.2f", productName, quantity, itemTotal);
                billArea.append(line + "\n");
                billLines.add(line);
                totalLabel.setText("Total: ₹" + total);

                PreparedStatement update = conn.prepareStatement("UPDATE products SET stock = stock - ? WHERE product_id = ?");
                update.setInt(1, quantity);
                update.setInt(2, id);
                update.executeUpdate();
            }

        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Error: " + ex.getMessage());
            ex.printStackTrace();
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            GroceryShopGUI app = new GroceryShopGUI();
            app.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            app.setVisible(true);
        });
    }
}
