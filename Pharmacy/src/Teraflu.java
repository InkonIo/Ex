import javax.swing.*;
import java.util.ArrayList;

public class Teraflu extends JFrame {
    private String userEmail;
    private ArrayList<String> selectedMedicines;

    public Teraflu(String userEmail, ArrayList<String> selectedMedicines) {
        this.userEmail = userEmail;
        this.selectedMedicines = selectedMedicines;

        setTitle("Грипферон");
        setBounds(100, 100, 400, 300);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        // Панель с информацией о парацетамоле
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.add(new JLabel("Терафлю - 700 тг."));

        // Кнопка добавления в корзину
        // Кнопка добавления в корзину
        JButton addToBasketButton = new JButton("Добавить в корзину");
        addToBasketButton.addActionListener(e -> {
            selectedMedicines.add("Терафлю"); // Просто добавляем в список без проверки
            JOptionPane.showMessageDialog(this, "Терафлю добавлен в корзину");
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
