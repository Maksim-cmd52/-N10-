import java.util.Scanner;

public class AuthenticationSystem {
    private static final int MAX_USERS = 15;
    private static String[] usernames = new String[MAX_USERS];
    private static String[] passwords = new String[MAX_USERS];
    private static String[] forbiddenPasswords = {"admin", "pass", "password", "qwerty", "ytrewq"};
    private static int userCount = 0;

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        while (true) {
            System.out.println("\n--- Меню ---");
            System.out.println("1. Реєстрація користувача");
            System.out.println("2. Видалити користувача");
            System.out.println("3. Аутентифікація (виконати дію)");
            System.out.println("4. Вихід");
            System.out.print("Виберіть дію: ");

            String choice = scanner.nextLine();

            try {
                switch (choice) {
                    case "1":
                        registerUser(scanner);
                        break;
                    case "2":
                        deleteUser(scanner);
                        break;
                    case "3":
                        authenticateUser(scanner);
                        break;
                    case "4":
                        return;
                    default:
                        System.out.println("Невірний вибір.");
                }
            } catch (UserLimitExceededException | InvalidUsernameException | 
                     InvalidPasswordException | UserNotFoundException | 
                     AuthenticationException e) {
                System.out.println("ПОМИЛКА: " + e.getMessage());
            }
        }
    }


    public static void registerUser(Scanner sc) throws UserLimitExceededException, InvalidUsernameException, InvalidPasswordException {
        if (userCount >= MAX_USERS) {
            throw new UserLimitExceededException("Максимальна кількість користувачів (15) досягнута.");
        }

        System.out.print("Введіть ім'я користувача: ");
        String name = sc.nextLine();
        validateUsername(name);

        System.out.print("Введіть пароль: ");
        String pass = sc.nextLine();
        validatePassword(pass);

        for (int i = 0; i < MAX_USERS; i++) {
            if (usernames[i] == null) {
                usernames[i] = name;
                passwords[i] = pass;
                userCount++;
                System.out.println("Користувача успішно зареєстровано.");
                break;
            }
        }
    }

    public static void deleteUser(Scanner sc) throws UserNotFoundException {
        System.out.print("Введіть ім'я для видалення: ");
        String name = sc.nextLine();

        for (int i = 0; i < MAX_USERS; i++) {
            if (usernames[i] != null && usernames[i].equals(name)) {
                usernames[i] = null;
                passwords[i] = null;
                userCount--;
                System.out.println("Користувача видалено.");
                return;
            }
        }
        throw new UserNotFoundException("Користувача з ім'ям '" + name + "' не знайдено.");
    }

    public static void authenticateUser(Scanner sc) throws AuthenticationException {
        System.out.print("Введіть ім'я: ");
        String name = sc.nextLine();
        System.out.print("Введіть пароль: ");
        String pass = sc.nextLine();

        for (int i = 0; i < MAX_USERS; i++) {
            if (usernames[i] != null && usernames[i].equals(name)) {
                if (passwords[i].equals(pass)) {
                    System.out.println("Успіх: Користувача [" + name + "] аутентифіковано.");
                    return;
                }
            }
        }
        throw new AuthenticationException("Невірне ім'я користувача або пароль.");
    }

    private static void validateUsername(String name) throws InvalidUsernameException {
        if (name.length() < 5) throw new InvalidUsernameException("Ім'я занадто коротке (мін. 5 символів).");
        if (name.contains(" ")) throw new InvalidUsernameException("Ім'я не повинно містити пробілів.");
    }

    private static void validatePassword(String pass) throws InvalidPasswordException {
        if (pass.length() < 10) throw new InvalidPasswordException("Пароль має бути не менше 10 символів.");
        if (pass.contains(" ")) throw new InvalidPasswordException("Пароль не може містити пробіли.");

        int digitCount = 0;
        boolean hasSpecial = false;
        String specialChars = "!@#$%^&*()-_=+[]{};:,.<>/?";

        for (int i = 0; i < pass.length(); i++) {
            char c = pass.charAt(i);
            
            if (Character.isDigit(c)) digitCount++;
            
            if (specialChars.indexOf(c) != -1) hasSpecial = true;

            if (!( (c >= 'a' && c <= 'z') || (c >= 'A' && c <= 'Z') || 
                   Character.isDigit(c) || specialChars.indexOf(c) != -1 )) {
                throw new InvalidPasswordException("Пароль містить недопустимі символи (дозволена лише латиниця, цифри та спецсимволи).");
            }
        }

        if (digitCount < 3) throw new InvalidPasswordException("Пароль має містити хоча б 3 цифри.");
        if (!hasSpecial) throw new InvalidPasswordException("Пароль має містити хоча б 1 спеціальний символ.");

        for (String forbidden : forbiddenPasswords) {
            if (pass.toLowerCase().contains(forbidden)) {
                throw new InvalidPasswordException("Пароль містить заборонене слово: " + forbidden);
            }
        }
    }
}