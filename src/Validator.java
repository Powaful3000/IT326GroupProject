import java.util.regex.Pattern;

public class Validator {

    // Validate if a string is not null and not empty
    public static boolean isValidString(String input) {
        if (input == null || input.trim().isEmpty()) {
            System.err.println("Invalid string: Input cannot be null or empty.");
            return false;
        }
        return true;
    }

    // Validate if an ID is positive
    public static boolean isValidId(int id) {
        if (id <= 0) {
            System.err.println("Invalid ID: ID must be a positive number.");
            return false;
        }
        return true;
    }

    // Validate if a year string is in a specific format (e.g., "Freshman",
    // "Sophomore", "Junior", "Senior")
    public static boolean isValidYear(String year) {
        String[] validYears = { "Freshman", "Sophomore", "Junior", "Senior" };
        for (String validYear : validYears) {
            if (validYear.equalsIgnoreCase(year)) {
                return true;
            }
        }
        System.err.println("Invalid year: Year must be one of " + String.join(", ", validYears) + ".");
        return false;
    }

    // Validate email format
    public static boolean isValidEmail(String email) {
        if (email == null || email.isEmpty()) {
            System.err.println("Invalid email: Email cannot be null or empty.");
            return false;
        }

        // Simple email regex pattern
        String emailRegex = "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$";
        if (!Pattern.matches(emailRegex, email)) {
            System.err.println("Invalid email: Email format is incorrect.");
            return false;
        }
        return true;
    }

    // Validate password strength (minimum 8 characters, at least one letter and one
    // number)
    public static boolean isValidPassword(String password) {
        if (password == null || password.isEmpty()) {
            System.err.println("Invalid password: Password cannot be null or empty.");
            return false;
        }

        // Password regex pattern
        String passwordRegex = "^(?=.*[A-Za-z])(?=.*\\d)[A-Za-z\\d]{8,}$";
        if (!Pattern.matches(passwordRegex, password)) {
            System.err.println(
                    "Invalid password: Password must be at least 8 characters long and include at least one letter and one number.");
            return false;
        }
        return true;
    }

    // Validate if a date is not null and is in the past
    public static boolean isValidDate(java.util.Date date) {
        if (date == null) {
            System.err.println("Invalid date: Date cannot be null.");
            return false;
        }
        if (date.after(new java.util.Date())) {
            System.err.println("Invalid date: Date cannot be in the future.");
            return false;
        }
        return true;
    }
}
