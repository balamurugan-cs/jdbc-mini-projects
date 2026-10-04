package hospital;

import java.time.LocalDate;

public class Validator {

    public static boolean isNotEmpty(String s) {
        return s != null && !s.trim().isEmpty();
    }

    public static boolean isValidName(String name) {
        return isNotEmpty(name) && name.matches("[A-Za-z ]+");
    }

    public static boolean isValidPhone(String phone) {
        return isNotEmpty(phone) && phone.matches("[0-9]{10}");
    }

    public static boolean isNumber(String s) {
        return isNotEmpty(s) && s.matches("[0-9]{1,9}");
    }

    public static boolean isValidAge(String s) {
        if (!isNumber(s)) {
            return false;
        }
        int age = Integer.parseInt(s);
        return age >= 1 && age <= 120;
    }

    public static boolean isValidGender(String s) {
        return isNotEmpty(s) && s.matches("(?i)male|female|other");
    }

    public static boolean isValidDate(String s) {
        try {
            LocalDate d = LocalDate.parse(s);
            return !d.isBefore(LocalDate.now());
        } catch (Exception e) {
            return false;
        }
    }
}