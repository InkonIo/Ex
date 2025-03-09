import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.HashMap;

public class Basket extends JFrame {
    private ArrayList<String> selectedMedicines;
    private String userEmail;
    private JPanel basketPanel;
    private JLabel totalLabel;

    public Basket(String userEmail, ArrayList<String> selectedMedicines) {
        this.userEmail = userEmail;
        this.selectedMedicines = selectedMedicines;

        setTitle("Корзина");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setBounds(100, 100, 600, 400);
        setResizable(false);
        setLocationRelativeTo(null);

        // Основная панель с бирюзовым фоном
        Container container = getContentPane();
        container.setLayout(new BorderLayout());
        container.setBackground(new Color(138, 209, 206)); // Основной фон

        basketPanel = new JPanel();
        basketPanel.setLayout(new BoxLayout(basketPanel, BoxLayout.Y_AXIS));
        basketPanel.setBackground(new Color(138, 209, 206));

        updateBasket();

        JScrollPane scrollPane = new JScrollPane(basketPanel);
        scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
        scrollPane.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // Кнопки управления
        JPanel buttonPanel = new JPanel();
        buttonPanel.setBackground(new Color(138, 209, 206));

        JButton backButton = createStyledButton("Назад", new Color(0, 123, 167), Color.WHITE);
        backButton.addActionListener(e -> {
            dispose();
            new MedicineSelectionWindow(userEmail, selectedMedicines);
        });

        JButton clearButton = createStyledButton("Очистить корзину", new Color(0, 123, 167), Color.WHITE);
        clearButton.addActionListener(e -> {
            selectedMedicines.clear();
            updateBasket();
        });

        JButton buyButton = createStyledButton("Купить", new Color(0, 180, 80), Color.WHITE);
        buyButton.addActionListener(e -> {
            if (selectedMedicines.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Корзина пуста!", "Ошибка", JOptionPane.WARNING_MESSAGE);
            } else {
                JOptionPane.showMessageDialog(this, "Покупка совершена!", "Успех", JOptionPane.INFORMATION_MESSAGE);
                selectedMedicines.clear();
                updateBasket();
            }
        });

        buttonPanel.add(backButton);
        buttonPanel.add(clearButton);
        buttonPanel.add(buyButton);

        container.add(scrollPane, BorderLayout.CENTER);
        container.add(buttonPanel, BorderLayout.SOUTH);

        setVisible(true);
    }

    // Метод для обновления корзины
    private void updateBasket() {
        basketPanel.removeAll();

        double totalPrice = 0.0;
        HashMap<String, Integer> medicineCount = new HashMap<>();

        for (String medicine : selectedMedicines) {
            medicineCount.put(medicine, medicineCount.getOrDefault(medicine, 0) + 1);
        }

        for (String medicine : medicineCount.keySet()) {
            int quantity = medicineCount.get(medicine);
            double price = MedicineDatabase.getPrice(medicine);

            JPanel itemPanel = new JPanel(new BorderLayout());
            itemPanel.setBackground(new Color(200, 230, 229)); // Светлее основного фона
            itemPanel.setBorder(BorderFactory.createLineBorder(new Color(0, 123, 167), 1));
            itemPanel.setPreferredSize(new Dimension(550, 40));

            JLabel medicineLabel = new JLabel(medicine + " - " + price + " тг. (" + quantity + " шт)");
            medicineLabel.setForeground(new Color(0, 90, 140));
            medicineLabel.setFont(new Font("Arial", Font.BOLD, 14));
            medicineLabel.setBorder(BorderFactory.createEmptyBorder(5, 10, 5, 5));

            JButton removeButton = createStyledButton("Удалить", new Color(255, 77, 77), Color.WHITE);
            removeButton.addActionListener(e -> {
                selectedMedicines.remove(medicine);
                updateBasket();
            });

            itemPanel.add(medicineLabel, BorderLayout.CENTER);
            itemPanel.add(removeButton, BorderLayout.EAST);
            basketPanel.add(itemPanel);

            totalPrice += price * quantity;
        }

        JLabel userLabel = new JLabel("Ваш email: " + userEmail);
        userLabel.setForeground(new Color(0, 90, 140));
        userLabel.setFont(new Font("Arial", Font.PLAIN, 14));

        totalLabel = new JLabel("Итого: " + totalPrice + " тг.");
        totalLabel.setForeground(new Color(0, 123, 167));
        totalLabel.setFont(new Font("Arial", Font.BOLD, 16));

        basketPanel.add(userLabel);
        basketPanel.add(totalLabel);

        basketPanel.revalidate();
        basketPanel.repaint();
    }

    // Метод для создания стилизованной кнопки
    private JButton createStyledButton(String text, Color bgColor, Color fgColor) {
        JButton button = new JButton(text);
        button.setFont(new Font("Arial", Font.BOLD, 14));
        button.setBackground(bgColor);
        button.setForeground(fgColor);
        button.setFocusPainted(false);
        button.setBorder(BorderFactory.createLineBorder(Color.BLACK, 1));
        button.setPreferredSize(new Dimension(140, 35));
        return button;
    }
}
