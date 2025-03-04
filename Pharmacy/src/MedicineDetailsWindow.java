import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;

public class MedicineDetailsWindow extends JFrame {
    private String medicineName;
    private String userEmail;
    private ArrayList<String> selectedMedicines;

    public MedicineDetailsWindow() {
        this.medicineName = medicineName;
        this.userEmail = userEmail;
        this.selectedMedicines = selectedMedicines;

        // Настройка окна
        setTitle(medicineName);
        setBounds(100, 100, 400, 300);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        // Панель с информацией
        JPanel infoPanel = new JPanel(new BorderLayout());

        // Заголовок
        JLabel nameLabel = new JLabel(medicineName, JLabel.CENTER);
        nameLabel.setFont(new Font("Tahoma", Font.BOLD, 20));
        infoPanel.add(nameLabel, BorderLayout.NORTH);

        // Описание лекарства
        JTextArea descriptionArea = new JTextArea("Описание лекарства: ..."); // Здесь можно добавить описание
        descriptionArea.setEditable(false);
        infoPanel.add(descriptionArea, BorderLayout.CENTER);

        // Кнопки
        JPanel buttonPanel = new JPanel();
        JButton addToBasketButton = new JButton("Добавить в корзину");
        addToBasketButton.addActionListener(e -> addToBasket());
        buttonPanel.add(addToBasketButton);

        infoPanel.add(buttonPanel, BorderLayout.SOUTH);

        Container container = getContentPane();
        container.add(infoPanel);

        setVisible(true);
    }

    private void addToBasket() {
        selectedMedicines.add(medicineName);
        JOptionPane.showMessageDialog(this, medicineName + " добавлено в корзину!", "Успех", JOptionPane.INFORMATION_MESSAGE);
    }
}
