import java.sql.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class DatabaseHelper {

    private static final String DB_URL = "jdbc:sqlite:users.db"; // Путь к базе данных
    private static Connection connection;

    // Инициализация соединения с базой данных
    public static void connect() {
        try {
            if (connection == null || connection.isClosed()) {
                connection = DriverManager.getConnection(DB_URL);
                System.out.println("Соединение с базой данных установлено.");
            }
        } catch (SQLException e) {
            System.out.println("Ошибка соединения с базой данных");
            e.printStackTrace();
        }
    }

    // Создание таблицы пользователей (если не существует)
    public static void createTable() {
        String sql = "CREATE TABLE IF NOT EXISTS users ("
                + "email TEXT PRIMARY KEY, "
                + "password TEXT NOT NULL);";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.execute();
            System.out.println("Таблица users успешно создана (если её не было).");
        } catch (SQLException e) {
            System.out.println("Ошибка при создании таблицы users");
            e.printStackTrace();
        }

        // Создание таблицы для лекарств
        String createMedicinesTable = "CREATE TABLE IF NOT EXISTS medicines ("
                + "id INTEGER PRIMARY KEY AUTOINCREMENT, "
                + "name TEXT NOT NULL, "
                + "price REAL NOT NULL);";
        try (PreparedStatement stmt = connection.prepareStatement(createMedicinesTable)) {
            stmt.execute();
            System.out.println("Таблица medicines успешно создана (если её не было).");
        } catch (SQLException e) {
            System.out.println("Ошибка при создании таблицы medicines");
            e.printStackTrace();
        }

        // Создание таблицы для связи пользователей и лекарств
        String createUserMedicinesTable = "CREATE TABLE IF NOT EXISTS user_medicines ("
                + "user_email TEXT, "
                + "medicine_id INTEGER, "
                + "FOREIGN KEY (user_email) REFERENCES users(email), "
                + "FOREIGN KEY (medicine_id) REFERENCES medicines(id), "
                + "PRIMARY KEY (user_email, medicine_id));";
        try (PreparedStatement stmt = connection.prepareStatement(createUserMedicinesTable)) {
            stmt.execute();
            System.out.println("Таблица user_medicines успешно создана (если её не было).");
        } catch (SQLException e) {
            System.out.println("Ошибка при создании таблицы user_medicines");
            e.printStackTrace();
        }
    }

    // Добавление лекарства в базу данных
    public static boolean addMedicine(String name, double price) {
        String sql = "INSERT INTO medicines (name, price) VALUES (?, ?)";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, name);
            stmt.setDouble(2, price);
            stmt.executeUpdate();
            System.out.println("Лекарство добавлено в базу данных.");
            return true;
        } catch (SQLException e) {
            System.out.println("Ошибка при добавлении лекарства");
            e.printStackTrace();
        }
        return false;
    }

    // Метод получения ID лекарства
    public static int getMedicineIdByName(String name) {
        String sql = "SELECT id FROM medicines WHERE name = ?";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, name);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return rs.getInt("id");
            }
        } catch (SQLException e) {
            System.out.println("Ошибка при получении ID лекарства");
            e.printStackTrace();
        }
        return -1; // Если не найдено
    }

    // Метод получения ID пользователя
    public static int getUserIdByEmail(String email) {
        String sql = "SELECT id FROM users WHERE email = ?";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, email);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return rs.getInt("id");
            }
        } catch (SQLException e) {
            System.out.println("Ошибка при получении ID пользователя");
            e.printStackTrace();
        }
        return -1; // Если не найдено
    }

    // Добавление выбранного лекарства для пользователя
    public static boolean addUserMedicine(String email, int medicineId) {
        String sql = "INSERT INTO user_medicines (user_email, medicine_id) VALUES (?, ?)";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, email);
            stmt.setInt(2, medicineId);
            stmt.executeUpdate();
            System.out.println("Лекарство добавлено для пользователя.");
            return true;
        } catch (SQLException e) {
            System.out.println("Ошибка при добавлении лекарства для пользователя");
            e.printStackTrace();
        }
        return false;
    }

    // Проверка, существует ли уже такой email в базе данных
    public static boolean emailExists(String email) {
        connect();  // Убедитесь, что соединение с базой данных установлено
        String sql = "SELECT * FROM users WHERE email = ?";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, email);
            ResultSet rs = stmt.executeQuery();
            return rs.next(); // Если запись найдена, значит email уже существует
        } catch (SQLException e) {
            System.out.println("Ошибка при проверке существования email");
            e.printStackTrace();
        }
        return false;
    }

    // Добавление пользователя в базу данных
    public static boolean addUser(String email, String password) {
        // Проверка на уникальность email
        if (emailExists(email)) {
            return false; // Email уже существует
        }

        String sql = "INSERT INTO users (email, password) VALUES (?, ?)";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, email);
            stmt.setString(2, password); // Пароль теперь строка
            stmt.executeUpdate();
            return true; // Пользователь успешно добавлен
        } catch (SQLException e) {
            System.out.println("Ошибка при добавлении пользователя");
            e.printStackTrace();
        }
        return false;
    }

    // Проверка пользователя
    public static boolean checkUser(String email, String password) {
        String sql = "SELECT * FROM users WHERE email = ? AND password = ?";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, email);
            stmt.setString(2, password); // Пароль теперь строка
            ResultSet rs = stmt.executeQuery();
            return rs.next(); // Если запись найдена, то пользователь существует
        } catch (SQLException e) {
            System.out.println("Ошибка при проверке пользователя");
            e.printStackTrace();
        }
        return false;
    }

    // Валидация пароля
    public static boolean isValidPassword(String password) {
        // Минимум 6 символов, наличие хотя бы одной цифры, одной буквы и одного спецсимвола
        String regex = "^(?=.*[a-zA-Z])(?=.*\\d)(?=.*[!\"№%:,.]).{6,}$";
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(password);
        return matcher.matches(); // Если пароль подходит под регулярное выражение
    }

    public static String getUserEmailFromDatabase(String email) {
        // Здесь пишем код для получения email из базы данных по логину
        String sql = "SELECT email FROM users WHERE email = ?";
        try (Connection connection = DriverManager.getConnection(DB_URL);
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, email);
            ResultSet resultSet = statement.executeQuery();
            if (resultSet.next()) {
                return resultSet.getString("email");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    // Добавление метода disconnect для закрытия соединения
    public static void disconnect() {
        try {
            if (connection != null && !connection.isClosed()) {
                connection.close();
                System.out.println("Соединение с базой данных закрыто.");
            }
        } catch (SQLException e) {
            System.out.println("Ошибка при закрытии соединения с базой данных");
            e.printStackTrace();
        }
    }

    // Обновление пароля пользователя по email
    public static boolean updatePassword(String email, String newPassword) {
        if (!emailExists(email)) {
            System.out.println("Email не найден в базе данных.");
            return false;
        }

        if (!isValidPassword(newPassword)) {
            System.out.println("Пароль не соответствует требованиям.");
            return false;
        }

        String sql = "UPDATE users SET password = ? WHERE email = ?";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, newPassword);
            stmt.setString(2, email);
            int rowsUpdated = stmt.executeUpdate();
            if (rowsUpdated > 0) {
                System.out.println("Пароль успешно обновлен.");
                return true;
            }
        } catch (SQLException e) {
            System.out.println("Ошибка при обновлении пароля");
            e.printStackTrace();
        }
        return false;
    }
}
