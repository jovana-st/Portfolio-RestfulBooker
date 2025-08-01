package utils;
import com.github.javafaker.Faker;
import models.AuthRequest;
import models.BookingDates;
import models.BookingRequest;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

public class TestDataGenerator {

    private static final Faker faker = new Faker();
    private static final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");

    public static BookingRequest generateBookingRequest(){

        BookingRequest bookingRequest = new BookingRequest();

        bookingRequest.setFirstname(faker.name().firstName());
        bookingRequest.setLastname(faker.name().lastName());
        bookingRequest.setTotalprice(faker.number().numberBetween(100,1000));
        bookingRequest.setDepositpaid(faker.bool().bool());
        bookingRequest.setAdditionalneeds(faker.lorem().sentence(1));

        BookingDates bookingDates = new BookingDates(checkInGenerator(),checkOutGenerator());

        bookingRequest.setBookingDates(bookingDates);

        return bookingRequest;
}


    public static String generateAuthUsername(){
         String username = faker.name().username();
         return username;
    }

    public static String generateAuthPassword(){
        String password = faker.internet().password();
        return password;
    }

    public static LocalDate checkInGenerator(){
        return LocalDate.now().plusDays(faker.number().numberBetween(1, 15));
    }

    public static LocalDate checkOutGenerator(){
        return LocalDate.now().plusDays(faker.number().numberBetween(15, 30));
    }

    public static int generateInvalidBookingId(){
        int id = faker.number().numberBetween(10000, 1500);
        return id;
    }

}