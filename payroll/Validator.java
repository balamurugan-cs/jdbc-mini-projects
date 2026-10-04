package payroll;

import java.time.YearMonth;

public class Validator {

   
    public static boolean isNotEmpty(String s) {
        return s != null && !s.trim().isEmpty();
    }

   
    public static boolean isValidName(String name) {
        return isNotEmpty(name) && name.matches("[A-Za-z ]+");
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
                && s.matches("[0-9]{1,8}(\\.[0-9]{1,2})?")
                && Double.parseDouble(s) > 0;
    }


    public static boolean isValidAllowance(String s) {
        return isNotEmpty(s) && s.matches("[0-9]{1,8}(\\.[0-9]{1,2})?");
    }

    
    public static boolean isValidMonth(String s) {
        if (!isNotEmpty(s) || !s.matches("[0-9]{4}-[0-9]{2}")) {
            return false;
        }
        try {
            YearMonth.parse(s);
            return true;
        } catch (Exception e) {
            return false;
        }
    }
}