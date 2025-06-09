package api.kun.uz.util;


import java.util.regex.Pattern;

public class ValidityUtil {

    public static boolean isValidEmail(String username) {
        String emailRegex = "^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}$";
        return username!=null && username.matches(emailRegex);
    }

    public static boolean isValidPhone(String username) {
        String phoneRegex = "^\\+998\\s?\\d{2}\\s?\\d{3}\\s?\\d{2}\\s?\\d{2}$";
         return username!=null && username.matches(phoneRegex);
    }
}

