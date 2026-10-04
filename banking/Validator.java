package banking;

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

    public static boolean isValidEmail(String email) {
        return isNotEmpty(email)
                && email.matches("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$");
    }

    public static boolean isNumber(String s) {
        return isNotEmpty(s) && s.matches("[0-9]{1,9}");
    }

    public static boolean isValidAmount(String s) {
        return isNotEmpty(s)
                && s.matches("[0-9]{1,9}(\\.[0-9]{1,2})?")
                && Double.parseDouble(s) > 0;
    }
}