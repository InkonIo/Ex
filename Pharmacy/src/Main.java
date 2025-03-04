public static void main(String[] args) {
    DatabaseHelper.connect();
    DatabaseHelper.createTable(); // Создаем таблицу пользователей, если она не существует

    new ContactForm();
}
