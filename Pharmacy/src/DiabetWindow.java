import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

public class DiabetWindow extends JFrame {
    private String userEmail;
    private int userId;
    private ArrayList<String> selectedMedicines;
    private JPanel medicinePanel;
    private JScrollPane medicineScrollPane;
    private JTextField searchField;
    private List<JPanel> medicineCards;

    public DiabetWindow(String userEmail, ArrayList<String> selectedMedicines) {
        this.userEmail = userEmail;
        this.selectedMedicines = selectedMedicines;
        this.medicineCards = new ArrayList<>();
        this.userId = DatabaseHelper.getUserIdByEmail(userEmail);

        setTitle("Диабет");
        setBounds(100, 100, 650, 450);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setResizable(false);
        setLocationRelativeTo(null);

        Container container = getContentPane();
        container.setLayout(new BorderLayout());
        container.setBackground(new Color(138, 209, 206));

        JPanel searchPanel = new JPanel(new BorderLayout());
        searchField = new JTextField("Поиск...");
        searchField.setForeground(Color.GRAY);

        searchField.getDocument().addDocumentListener(new DocumentListener() {
            @Override
            public void insertUpdate(DocumentEvent e) { filterMedicines(); }
            @Override
            public void removeUpdate(DocumentEvent e) { filterMedicines(); }
            @Override
            public void changedUpdate(DocumentEvent e) { filterMedicines(); }
        });

        searchField.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                if (searchField.getText().equals("Поиск...")) {
                    searchField.setText("");
                    searchField.setForeground(Color.BLACK);
                }
            }
            public void focusLost(java.awt.event.FocusEvent evt) {
                if (searchField.getText().isEmpty()) {
                    searchField.setText("Поиск...");
                    searchField.setForeground(Color.GRAY);
                }
            }
        });

        searchPanel.add(searchField, BorderLayout.CENTER);

        medicinePanel = new JPanel(new GridLayout(0, 1, 10, 10));
        medicinePanel.setBackground(new Color(138, 209, 206));
        medicinePanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        addMedicineButton("Глюкоза", "Важный источник энергии.", "/images/glukoza.png", Glukoza.class);

        medicineScrollPane = new JScrollPane(medicinePanel);
        medicineScrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
        medicineScrollPane.setBorder(BorderFactory.createEmptyBorder());

        JPanel buttonPanel = new JPanel();
        buttonPanel.setBackground(new Color(138, 209, 206));

        JButton backButton = createStyledButton("Назад", new Color(0, 123, 167), Color.WHITE);
        backButton.addActionListener(e -> {
            dispose();
            new MedicineSelectionWindow(userId, selectedMedicines);
        });

        JButton basketButton = createStyledButton("Корзина", new Color(0, 123, 167), Color.WHITE);
        basketButton.addActionListener(e -> {
            dispose();
            new Basket(selectedMedicines);
        });


        buttonPanel.add(backButton);
        buttonPanel.add(basketButton);

        container.add(searchPanel, BorderLayout.NORTH);
        container.add(medicineScrollPane, BorderLayout.CENTER);
        container.add(buttonPanel, BorderLayout.SOUTH);

        setVisible(true);
    }

    private void addMedicineButton(String medicineName, String description, String imagePath, Class<?> medicineWindowClass) {
        JPanel medicineCard = new JPanel(new BorderLayout());
        medicineCard.setBackground(new Color(200, 230, 229));
        medicineCard.setBorder(BorderFactory.createLineBorder(new Color(0, 90, 140), 2));
        medicineCard.setPreferredSize(new Dimension(300, 100));

        ImageIcon originalIcon = new ImageIcon(getClass().getResource(imagePath));
        Image scaledImage = originalIcon.getImage().getScaledInstance(100, 100, Image.SCALE_SMOOTH);
        JLabel imageLabel = new JLabel(new ImageIcon(scaledImage));

        JLabel nameLabel = new JLabel(medicineName);
        nameLabel.setFont(new Font("Arial", Font.BOLD, 14));

        double price = MedicineDatabase.getPrice(medicineName);
        JLabel priceLabel = new JLabel("Цена: " + price + " KZT");
        priceLabel.setFont(new Font("Arial", Font.BOLD, 12));

        JPanel textPanel = new JPanel(new BorderLayout());
        textPanel.setBackground(new Color(200, 230, 229));
        textPanel.add(nameLabel, BorderLayout.NORTH);
        textPanel.add(priceLabel, BorderLayout.SOUTH);

        JButton selectButton = createStyledButton("Выбрать", new Color(0, 123, 167), Color.WHITE);
        selectButton.addActionListener(e -> openMedicineWindow(medicineWindowClass));

        medicineCard.add(imageLabel, BorderLayout.WEST);
        medicineCard.add(textPanel, BorderLayout.CENTER);
        medicineCard.add(selectButton, BorderLayout.EAST);

        medicineCards.add(medicineCard);
        medicinePanel.add(medicineCard);
    }

    private JButton createStyledButton(String text, Color bgColor, Color fgColor) {
        JButton button = new JButton(text);
        button.setFont(new Font("Arial", Font.BOLD, 14));
        button.setBackground(bgColor);
        button.setForeground(fgColor);
        button.setFocusPainted(false);
        button.setBorder(BorderFactory.createLineBorder(Color.BLACK, 1));
        button.setPreferredSize(new Dimension(100, 30));
        return button;
    }

    private void filterMedicines() {
        String searchText = searchField.getText().trim().toLowerCase();
        medicinePanel.removeAll();

        for (JPanel card : medicineCards) {
            Component[] components = card.getComponents();
            if (components.length > 1 && components[1] instanceof JPanel textPanel) {
                Component[] textComponents = textPanel.getComponents();
                if (textComponents.length > 0 && textComponents[0] instanceof JLabel nameLabel) {
                    if (nameLabel.getText().toLowerCase().contains(searchText)) {
                        medicinePanel.add(card);
                    }
                }
            }
        }

        medicinePanel.revalidate();
        medicinePanel.repaint();
    }

    private void openMedicineWindow(Class<?> medicineWindowClass) {
        try {
            medicineWindowClass.getDeclaredConstructor(String.class).newInstance(userEmail);
            dispose();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }
}
