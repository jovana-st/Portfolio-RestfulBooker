package services;

import config.Constants;
import config.Endpoints;
import specs.RequestSpecifications;
import io.restassured.response.Response;
import models.BookingRequest;

import java.time.LocalDate;
import java.util.Map;

import static specs.RequestSpecifications.*;
import static io.restassured.RestAssured.given;

public class BookingService {

    public static Response getBookingIds(){
        return given().spec(baseSpecGetBooking)
                .when().get(Endpoints.BOOKING)
                .then().extract().response();
    }

    public static Response getBookingsByName(String firstname, String lastname){
        return given().spec(baseSpecGetBooking)
                .when().get(Endpoints.BOOKING + "?firstname=" + firstname + "&lastname=" + lastname);
    }

    public static Response getBookingsByCheckinAndCheckout(LocalDate checkin, LocalDate checkout){
        return given().spec(baseSpecGetBooking)
                .when().get(Endpoints.BOOKING + "?checkin=" + checkin + "&checkout=" + checkout);
    }

    public static Response getBooking(int id){
        return given().spec(baseSpecGetBooking)
                .when().get(Endpoints.BOOKING + "/" + id)
                .then().extract().response();
    }

    public static Response createBooking(BookingRequest bookingBody){
        return given().spec(baseSpecCreateBooking)
                .body(bookingBody)
                .when().post(Endpoints.BOOKING)
                .then().extract().response();
    }

    public static Response updateBooking(String username, String password, int id, BookingRequest bookingBody){
        return given().spec(RequestSpecifications.baseSpecUpdateBooking(username, password))
                .body(bookingBody)
                .when().put(Endpoints.BOOKING + "/" + id)
                .then().extract().response();
    }

    public static Response partialUpdateBooking(String username, String password, int id, Map<String, Object> updates){
        return given().spec(RequestSpecifications.baseSpecUpdateBooking(username, password))
                .body(updates)
                .when().patch(Endpoints.BOOKING + "/" + id)
                .then().extract().response();
    }

    public static Response deleteBooking(String username, String password, int id){
        return given().spec(RequestSpecifications.baseSpecDeleteBooking(username, password))
                .when().delete(Endpoints.BOOKING + "/" + id)
                .then().extract().response();
    }


}