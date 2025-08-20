package models;

import lombok.Data;

@Data
public class BookingDates {

    private String checkin;
    private String checkout;

    public BookingDates(String checkin, String checkout){
        this.checkin = checkin;
        this.checkout = checkout;
    }

}