public class SessionManager {
    private static int userId = -1;
    private static String userEmail = null;

    public static void setUser(int id, String email) {

        userId = id;
        userEmail = email;
    }

    public static int getUserId() {

        return userId;
    }

    public static String getUserEmail() {
        return userEmail;
    }
}
