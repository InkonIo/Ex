import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;

public class Fervex extends JFrame {
    private String userEmail;
    private ArrayList<String> selectedMedicines;

    public Fervex(String userEmail, ArrayList<String> selectedMedicines) {
        this.userEmail = userEmail;
        this.selectedMedicines = selectedMedicines;

        setTitle("Фервекс");
        setBounds(100, 100, 400, 300);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        // Панель с информацией о парацетамоле
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.add(new JLabel("Фервекс - 90 тг."));

        // Кнопка добавления в корзину
        // Кнопка добавления в корзину
        JButton addToBasketButton = new JButton("Добавить в корзину");
        addToBasketButton.addActionListener(e -> {
            selectedMedicines.add("Фервекс"); // Просто добавляем в список без проверки
            JOptionPane.showMessageDialog(this, "Фервекс добавлен в корзину");
        });
        panel.add(addToBasketButton);


        // Кнопка "Назад"
        JButton backButton = new JButton("Назад");
        backButton.addActionListener(e -> {
            dispose(); // Закрыть текущее окно
            new SimpleWindow(userEmail, selectedMedicines); // Вернуться к окну выбора лекарств
        });
        panel.add(backButton);

        // Добавляем панель в окно
        getContentPane().add(panel);

        setVisible(true);
    }
}
