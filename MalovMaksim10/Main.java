import java.util.Scanner;

public class Main {
    private static final int MAX_USERS = 15;
    private static String[] usernames = new String[MAX_USERS];
    private static String[] passwords = new String[MAX_USERS];
    private static String[] forbiddenPasswords = {"admin", "pass", "password", "qwerty", "ytrewq"};
    private static int userCount = 0;

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        
        while (true) {
            printMenu();
            String choice = scanner.nextLine();

            try {
                switch (choice) {
                    case "1":
                        addUser(scanner);
                        break;
                    case "2":
                        removeUser(scanner);
                        break;
                    case "3":
                        performUserAction(scanner);
                        break;
                    case "4":
                        System.out.println("Вихід з програми...");
                        return;
                    default:
                        System.out.println("Помилка: Невірний пункт меню.");
                }
            } catch (UserLimitExceededException | InvalidUsernameException | 
                     InvalidPasswordException | UserNotFoundException | 
                     AuthenticationException e) {
                System.out.println("КРИТИЧНА ПОМИЛКА: " + e.getMessage());
            }
        }
    }

    private static void printMenu() {
        System.out.println("\n--- Система Аутентифікації ---");
        System.out.println("1 - Додати користувача");
        System.out.println("2 - Видалити користувача за ім'ям");
        System.out.println("3 - Виконати дію (Аутентифікація)");
        System.out.println("4 - Вихід");
        System.out.print("Виберіть дію: ");
    }

    public static void addUser(Scanner sc) throws UserLimitExceededException, InvalidUsernameException, InvalidPasswordException {
        if (userCount >= MAX_USERS) {
            throw new UserLimitExceededException("Неможливо додати більше 15 користувачів.");
        }

        System.out.print("Введіть ім'я (мін. 5 симв., без пробілів): ");
        String name = sc.nextLine();
        checkUsername(name);

        System.out.print("Введіть пароль (мін. 10 симв., 3 цифри, 1 спецсимвол): ");
        String pass = sc.nextLine();
        checkPassword(pass);

        for (int i = 0; i < MAX_USERS; i++) {
            if (usernames[i] == null) {
                usernames[i] = name;
                passwords[i] = pass;
                userCount++;
                System.out.println("Користувача '" + name + "' успішно додано.");
                return;
            }
        }
    }

    public static void removeUser(Scanner sc) throws UserNotFoundException {
        System.out.print("Введіть ім'я користувача для видалення: ");
        String name = sc.nextLine();

        for (int i = 0; i < MAX_USERS; i++) {
            if (usernames[i] != null && usernames[i].equals(name)) {
                usernames[i] = null;
                passwords[i] = null;
                userCount--;
                System.out.println("Користувача '" + name + "' видалено.");
                return;
            }
        }
        throw new UserNotFoundException("Користувача з ім'ям '" + name + "' не існує.");
    }

    public static void performUserAction(Scanner sc) throws AuthenticationException {
        System.out.print("Введіть ім'я: ");
        String login = sc.nextLine();
        System.out.print("Введіть пароль: ");
        String pass = sc.nextLine();

        for (int i = 0; i < MAX_USERS; i++) {
            if (usernames[i] != null && usernames[i].equals(login)) {
                if (passwords[i].equals(pass)) {
                    System.out.println("Результат: Користувача аутентифіковано. Дія виконана.");
                    return;
                }
            }
        }
        throw new AuthenticationException("Помилка аутентифікації: Невірне ім'я або пароль.");
    }


    private static void checkUsername(String name) throws InvalidUsernameException {
        if (name.length() < 5) throw new InvalidUsernameException("Ім'я занадто коротке.");
        if (name.contains(" ")) throw new InvalidUsernameException("Ім'я не може містити пробіли.");
    }

    private static void checkPassword(String pass) throws InvalidPasswordException {
        if (pass.length() < 10) throw new InvalidPasswordException("Довжина паролю має бути від 10 символів.");
        if (pass.contains(" ")) throw new InvalidPasswordException("Пароль не може містити пробіли.");

        int digits = 0;
        boolean hasSpecial = false;
        String specials = "!@#$%^&*()-_=+[]{};:,.<>/?";

        for (int i = 0; i < pass.length(); i++) {
            char c = pass.charAt(i);
            
            boolean isLatin = (c >= 'a' && c <= 'z') || (c >= 'A' && c <= 'Z');
            boolean isDigit = (c >= '0' && c <= '9');
            boolean isSpecial = (specials.indexOf(c) != -1);

            if (!isLatin && !isDigit && !isSpecial) {
                throw new InvalidPasswordException("Дозволені тільки латинські літери, цифри та спецсимволи.");
            }

            if (isDigit) digits++;
            if (isSpecial) hasSpecial = true;
        }

        if (digits < 3) throw new InvalidPasswordException("Пароль має містити мінімум 3 цифри.");
        if (!hasSpecial) throw new InvalidPasswordException("Пароль має містити мінімум 1 спецсимвол.");

        String lowerPass = pass.toLowerCase();
        for (String word : forbiddenPasswords) {
            if (lowerPass.contains(word)) {
                throw new InvalidPasswordException("Пароль містить заборонене слово: " + word);
            }
        }
    }
}