public class SessionManager {
    private static int userId = -1;
    private static String userEmail = null;

    public static void setUser(int id, String email) {
        if (id <= 0 || email == null || email.isEmpty()) {
            System.err.println("Ошибка: Недопустимые данные для сессии!");
            return;
        }
        userId = id;
        userEmail = email;
        System.out.println("SessionManager: Установлен пользователь ID = " + userId + ", Email = " + userEmail);
    }

    public static int getUserId() {
        System.out.println("SessionManager.getUserId() = " + userId);
        return userId;
    }

    public static String getUserEmail() {
        System.out.println("SessionManager.getUserEmail() = " + userEmail);
        return userEmail;
    }
}
