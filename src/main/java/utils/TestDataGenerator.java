package utils;
import com.github.javafaker.Faker;
import models.AuthRequest;
import models.BookingDates;
import models.BookingRequest;

import java.time.format.DateTimeFormatter;
import java.util.concurrent.TimeUnit;

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

        BookingDates bookingDates = new BookingDates();
        bookingDates.setCheckin(faker.date().future(1, 5, TimeUnit.DAYS));
        bookingDates.setCheckout(faker.date().future(6, 10, TimeUnit.DAYS));

        bookingRequest.setBookingDates(bookingDates);

        return bookingRequest;
}


    public static AuthRequest generateAuthRequest(){
        AuthRequest auth = new AuthRequest(faker.name().username(), faker.internet().password());
        return auth;
    }

}
