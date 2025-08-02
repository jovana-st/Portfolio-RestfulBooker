package models;

import lombok.Data;
import java.time.LocalDate;

@Data
public class BookingDates {

    private String checkin;
    private String checkout;

    public BookingDates(String checkin, String checkout){
        this.checkin = checkin;
        this.checkout = checkout;
    }

}