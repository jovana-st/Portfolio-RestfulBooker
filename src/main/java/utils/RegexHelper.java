package utils;

import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.regex.Pattern;

public class RegexHelper {

    public static final Pattern DATE_PATTERN = Pattern.compile("\\d{4}-\\d{2}-\\d{2}");
    //This is just an example and is not being used in the framework
    public static final Pattern EMAIL_PATTERN = Pattern.compile("^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}$");

    public static boolean isValidDate(String dateString){
        if (dateString == null || !DATE_PATTERN.matcher(dateString).matches()){
            return false;
        }

        try{
            LocalDate.parse(dateString);
            return true;
        } catch (DateTimeParseException e){
            return false;
        }
    }

    //This is just an example and is not being used in the framework
    public static boolean isValidEmail(String email){
        return email != null && EMAIL_PATTERN.matcher(email).matches();
    }

    //This is just an example and is not being used in the framework
    public static boolean isValidUrl(String url){
        if (url == null) return false;
        try {
            new java.net.URL(url).toURI();
            return true;
        } catch (Exception e){
            return false;
        }
    }

}