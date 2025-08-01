package models;

import lombok.Data;
import java.time.LocalDate;

@Data
public class BookingDates {

    LocalDate checkin;
    LocalDate checkout;

    public BookingDates(LocalDate checkin, LocalDate checkout){
        if (checkout.isBefore(checkin)){
            throw new IllegalArgumentException("Checkout must be after checkin.");
        }
        this.checkin = checkin;
        this.checkout = checkout;
    }

}