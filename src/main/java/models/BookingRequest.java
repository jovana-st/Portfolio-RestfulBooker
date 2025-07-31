package models;
import lombok.Data;

@Data
public class BookingRequest {

    String firstname;
    String lastname;
    int totalprice;
    boolean depositpaid;
    BookingDates bookingDates;
    String additionalneeds;

}
