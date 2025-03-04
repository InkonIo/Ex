import com.formdev.flatlaf.FlatLightLaf;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

// hello

public class AdminPanel extends JFrame {
    private JTable productTable, userTable, orderTable;
    private DefaultTableModel productModel, userModel, orderModel;

    public AdminPanel() {
        try {
            UIManager.setLookAndFeel(new FlatLightLaf());
        } catch (UnsupportedLookAndFeelException e) {
            e.printStackTrace();
        }

        setTitle("Admin Panel - Pharmacy");
        setSize(800, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        JTabbedPane tabbedPane = new JTabbedPane();

        tabbedPane.add("Товары", createProductPanel());
        tabbedPane.add("Пользователи", createUserPanel());
        tabbedPane.add("Заказы", createOrderPanel());
        tabbedPane.add("Аналитика", createAnalyticsPanel());

        add(tabbedPane);
        setVisible(true);
    }

    private JPanel createProductPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        productModel = new DefaultTableModel(new String[]{"Название", "Цена", "Категория"}, 0);
        productTable = new JTable(productModel);

        JButton addButton = new JButton("Добавить");
        JButton saveButton = new JButton("Сохранить");
        JButton backButton = new JButton("Назад");

        addButton.addActionListener(e -> openAddProductDialog());
        saveButton.addActionListener(e -> saveChanges());
        backButton.addActionListener(e -> goBack());

        JPanel controlPanel = new JPanel();
        controlPanel.add(addButton);
        controlPanel.add(saveButton);
        controlPanel.add(backButton);

        panel.add(new JScrollPane(productTable), BorderLayout.CENTER);
        panel.add(controlPanel, BorderLayout.SOUTH);
        return panel;
    }

    private void openAddProductDialog() {
        String[] categories = {"Простуда", "Диабет", "Малыши и мамы", "Для кожи", "Витамины"};
        JComboBox<String> categoryBox = new JComboBox<>(categories);
        JComboBox<String> medicineBox = new JComboBox<>();
        JTextField priceField = new JTextField(10);

        categoryBox.addActionListener(e -> {
            String selectedCategory = (String) categoryBox.getSelectedItem();
            medicineBox.removeAllItems();
            switch (selectedCategory) {
                case "Простуда" -> medicineBox.setModel(new DefaultComboBoxModel<>(new String[]{"Цитрамон", "Парацетамол", "Ибупрофен"}));
                case "Диабет" -> medicineBox.setModel(new DefaultComboBoxModel<>(new String[]{"Глюкоза", "Метформин", "Глибенкламид"}));
                case "Малыши и мамы" -> medicineBox.setModel(new DefaultComboBoxModel<>(new String[]{"Парацетамол для детей", "Нурофен для детей"}));
                case "Для кожи" -> medicineBox.setModel(new DefaultComboBoxModel<>(new String[]{"Бепантен", "Левомеколь"}));
                case "Витамины" -> medicineBox.setModel(new DefaultComboBoxModel<>(new String[]{"Витамин С", "Кальций-Д3"}));
            }
        });
        categoryBox.setSelectedIndex(0);

        JPanel panel = new JPanel(new GridLayout(3, 2));
        panel.add(new JLabel("Категория:"));
        panel.add(categoryBox);
        panel.add(new JLabel("Препарат:"));
        panel.add(medicineBox);
        panel.add(new JLabel("Цена (₸):"));
        panel.add(priceField);

        int result = JOptionPane.showConfirmDialog(this, panel, "Добавить товар", JOptionPane.OK_CANCEL_OPTION);
        if (result == JOptionPane.OK_OPTION) {
            String category = (String) categoryBox.getSelectedItem();
            String medicine = (String) medicineBox.getSelectedItem();
            String price = priceField.getText().trim();
            if (!price.matches("\\d+")) {
                JOptionPane.showMessageDialog(this, "Введите корректную цену", "Ошибка", JOptionPane.ERROR_MESSAGE);
                return;
            }
            productModel.addRow(new Object[]{medicine, price + " ₸", category});
            MedicineDatabase.setPrice(medicine, Double.parseDouble(price)); // Сохраняем в базе
        }
    }

    private void saveChanges() {
        for (int i = 0; i < productModel.getRowCount(); i++) {
            String medicine = (String) productModel.getValueAt(i, 0);
            String priceStr = (String) productModel.getValueAt(i, 1);
            double price = Double.parseDouble(priceStr.replace(" ₸", "").trim());

            // Сохранение в базе данных
            MedicineDatabase.setPrice(medicine, price);
        }
        JOptionPane.showMessageDialog(this, "Изменения сохранены!", "Успех", JOptionPane.INFORMATION_MESSAGE);
    }

    private void goBack() {
        new ContactForm(); // Переход на ContactForm
        dispose();
    }

    private JPanel createUserPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        userModel = new DefaultTableModel(new String[]{"Email", "Статус"}, 0);
        userTable = new JTable(userModel);
        panel.add(new JScrollPane(userTable), BorderLayout.CENTER);
        return panel;
    }

    private JPanel createOrderPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        orderModel = new DefaultTableModel(new String[]{"Заказ", "Статус"}, 0);
        orderTable = new JTable(orderModel);
        panel.add(new JScrollPane(orderTable), BorderLayout.CENTER);
        return panel;
    }

    private JPanel createAnalyticsPanel() {
        JPanel panel = new JPanel();
        JLabel analyticsLabel = new JLabel("Аналитика в разработке");
        panel.add(analyticsLabel);
        return panel;
    }

    public static void main(String[] args) {
        new AdminPanel();
    }
}
