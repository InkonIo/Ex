import java.sql.*;

public class DatabaseHelper {
    private static final String DB_URL = "jdbc:sqlite:users.db";
    private static Connection connection;

    public static Connection connect() {
        try {
            if (connection == null || connection.isClosed()) {
                connection = DriverManager.getConnection(DB_URL);
                System.out.println("Соединение с базой данных установлено.");
            }
        } catch (SQLException e) {
            System.out.println("Ошибка соединения с базой данных");
            e.printStackTrace();
        }
        return connection;
    }

    public static void createTable() {
        connect();

        String createUsersTable = "CREATE TABLE IF NOT EXISTS users ("
                + "id INTEGER PRIMARY KEY AUTOINCREMENT, "
                + "email TEXT UNIQUE NOT NULL, "
                + "password TEXT NOT NULL);";

        String createMedicinesTable = "CREATE TABLE IF NOT EXISTS medicines ("
                + "id INTEGER PRIMARY KEY AUTOINCREMENT, "
                + "name TEXT NOT NULL, "
                + "price REAL NOT NULL);";

        String createUserMedicinesTable = "CREATE TABLE IF NOT EXISTS user_medicines ("
                + "user_id INTEGER, "
                + "medicine_id INTEGER, "
                + "FOREIGN KEY (user_id) REFERENCES users(id), "
                + "FOREIGN KEY (medicine_id) REFERENCES medicines(id), "
                + "PRIMARY KEY (user_id, medicine_id));";

        try (Statement stmt = connection.createStatement()) {
            stmt.execute(createUsersTable);
            stmt.execute(createMedicinesTable);
            stmt.execute(createUserMedicinesTable);
            System.out.println("Все таблицы успешно созданы.");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static boolean addUser(String email, String password) {
        String sql = "INSERT INTO users (email, password) VALUES (?, ?)";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, email);
            stmt.setString(2, password);
            stmt.executeUpdate();
            return true;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }


    public static int getUserIdByEmail(String email) {
        int userId = -1;
        String query = "SELECT id FROM users WHERE email = ? LIMIT 1";

        try (Connection conn = connect();
             PreparedStatement pstmt = conn.prepareStatement(query)) {

            System.out.println("Email перед запросом: " + email); // Проверяем email перед запросом
            pstmt.setString(1, email);
            System.out.println("SQL-запрос: " + pstmt.toString()); // Проверяем, что будет выполняться

            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                userId = rs.getInt("id");
                System.out.println("User ID найден: " + userId);
            } else {
                System.out.println("Пользователь не найден!");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return userId;
    }


    public static String getEmailByUserId(int userId) {
        String email = null;
        String query = "SELECT email FROM users WHERE id = ? LIMIT 1";

        try (Connection conn = connect();
             PreparedStatement pstmt = conn.prepareStatement(query)) {
            pstmt.setInt(1, userId);
            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                email = rs.getString("email");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return email;
    }



    public static boolean addMedicine(String name, double price) {
        String sql = "INSERT INTO medicines (name, price) VALUES (?, ?)";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, name);
            stmt.setDouble(2, price);
            stmt.executeUpdate();
            return true;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public static void printAllUsers() {
        int userId = getCurrentUserId();
        if (userId == -1) {
            System.out.println("Нет активного пользователя.");
            return;
        }

        String query = "SELECT id, email FROM users WHERE id = ?";
        try (Connection conn = connect();
             PreparedStatement pstmt = conn.prepareStatement(query)) {
            pstmt.setInt(1, userId);
            ResultSet rs = pstmt.executeQuery();

            while (rs.next()) {
                System.out.println("ID: " + rs.getInt("id") + ", Email: " + rs.getString("email"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }


    private static int currentUserId = -1;

    public static int getCurrentUserId() {
        return currentUserId;
    }

    private static void setCurrentUserId(int userId) {
        currentUserId = userId;
    }

    public static int getMedicineIdByName(String name) {
        String sql = "SELECT id FROM medicines WHERE name = ?";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, name);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return rs.getInt("id");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return -1;
    }

    public static boolean addUserMedicine(int userId, int medicineId) {
        String sql = "INSERT INTO user_medicines (user_id, medicine_id) VALUES (?, ?)";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, userId);
            stmt.setInt(2, medicineId);
            stmt.executeUpdate();
            return true;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public static boolean emailExists(String email) {
        String sql = "SELECT id FROM users WHERE email = ?";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, email);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                System.out.println("User found: " + email);
                return true;
            }
            return rs.next(); // Если есть результат, email существует
        } catch (SQLException e) {
            System.out.println("Ошибка при проверке email");
            e.printStackTrace();
        }
        return false;
    }

    public static boolean updatePassword(String email, String newPassword) {
        String sql = "UPDATE users SET password = ? WHERE email = ?";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, newPassword);
            stmt.setString(2, email);
            int rowsUpdated = stmt.executeUpdate();
            return rowsUpdated > 0; // Если обновлена хотя бы 1 строка, пароль изменен
        } catch (SQLException e) {
            System.out.println("Ошибка при обновлении пароля");
            e.printStackTrace();
        }
        return false;
    }

    public static boolean isValidPassword(String password) {
        if (password == null) return false;

        // Пароль должен быть минимум 6 символов, содержать цифру, заглавную букву и специальный символ
        String passwordPattern = "^(?=.*[A-Z])(?=.*\\d)(?=.*[@#$%^&+=!]).{6,}$";
        return password.matches(passwordPattern);
    }

    public static boolean checkUser(String email, String password) {
        String query = "SELECT id FROM users WHERE email = ? AND password = ?";
        try (Connection conn = connect();
             PreparedStatement pstmt = conn.prepareStatement(query)) {
            pstmt.setString(1, email);
            pstmt.setString(2, password);
            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                setCurrentUserId(rs.getInt("id")); // Сохраняем текущего пользователя
                return true;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public static void disconnect() {
        try {
            if (connection != null && !connection.isClosed()) {
                connection.close();
                System.out.println("Соединение с базой данных закрыто.");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
