package tests;

import io.restassured.response.Response;
import models.BookingResponse;
import org.testng.Assert;
import org.testng.annotations.Test;
import services.BookingService;
import utils.ApiAssertions;
import utils.JsonHelper;
import utils.RetryAnalyzer;

import java.time.LocalDate;
import java.util.List;

public class GetBookingIdsTests {

    @Test(description = "Get all booking IDs successfully.", retryAnalyzer = RetryAnalyzer.class)
        public void getBookingIds(){
        Response response = BookingService.getBookingIds();
        //Status code is 200
        ApiAssertions.assertStatusCode(response, 200);
        //The response time is less than 2000ms
        ApiAssertions.assertResponseTimeLessThan(response, 2000);
        //The response structure is correct
        ApiAssertions.assertJsonSchema(response, "schemas/booking-ids-response.json");

        //The response contains the required fields
        List<BookingResponse> bookings = JsonHelper.fromJsonList(response.asString(), BookingResponse.class);
        if(!bookings.isEmpty()){
            bookings.forEach( b -> Assert.assertNotNull(b.getBookingid(), "Booking ID missing"));
        }
    }

    @Test(description = "Filter the booking IDs by name", retryAnalyzer = RetryAnalyzer.class)
    public void getBookingsByName_ReturnsFilteredResults(){
        Response response = BookingService.getBookingsByName("sally", "brown");
        //Status code is 200
        ApiAssertions.assertStatusCode(response, 200);
        //The response structure is checked
        ApiAssertions.assertJsonSchema(response, "schemas/booking-ids-response.json");
        //The response time is less than 2000ms
        ApiAssertions.assertResponseTimeLessThan(response, 2000);
        //The response contains the required fields
        List<BookingResponse> bookings = JsonHelper.fromJsonList(response.asString(), BookingResponse.class);
        if(!bookings.isEmpty()){
            bookings.forEach( b -> Assert.assertNotNull(b.getBookingid(), "Booking ID missing"));
        }

    }

    @Test(description = "Filter the booking IDs by a non-existent name", retryAnalyzer = RetryAnalyzer.class)
    public void getBookingsByName_ReturnsNoResults(){
        Response response = BookingService.getBookingsByName("nonexistent", "nonexistent");
        //Status code is 200
        ApiAssertions.assertStatusCode(response, 200);
        //The response is an empty array
        ApiAssertions.assertBodyEmptyArray(response);
    }

    @Test(description = "Filter the booking IDs by valid date range", retryAnalyzer = RetryAnalyzer.class)
    public void getBookingsByDate_ReturnsFilteredResults(){
        Response response = BookingService.getBookingsByCheckinAndCheckout(
                LocalDate.parse("2014-03-13"), LocalDate.parse("2014-05-21"));
        //Status code is 200
        ApiAssertions.assertStatusCode(response, 200);
        //The response structure is checked
        ApiAssertions.assertJsonSchema(response, "schemas/booking-ids-response.json");
        //The response time is under 2000ms
        ApiAssertions.assertResponseTimeLessThan(response, 2000);
        //The response contains the required fields
        List<BookingResponse> bookings = JsonHelper.fromJsonList(response.asString(), BookingResponse.class);
        if(!bookings.isEmpty()){
            bookings.forEach( b -> Assert.assertNotNull(b.getBookingid(), "Booking ID missing"));
        }
    }

    @Test(description = "Filter by nonexistent date range", retryAnalyzer = RetryAnalyzer.class)
    public void getBookingsByDate_NonexistentRange(){
        Response response = BookingService.getBookingsByCheckinAndCheckout(
                LocalDate.parse("1999-03-13"), LocalDate.parse("1999-05-21"));
        //Status code is 200
        ApiAssertions.assertStatusCode(response, 200);
        //The response is an empty array
        ApiAssertions.assertBodyEmptyArray(response);
    }

    @Test(description = "Validate logic for same day bookings", retryAnalyzer = RetryAnalyzer.class)
    public void getBookingsByDate_SameDayCheckInAndOut(){
        Response response = BookingService.getBookingsByCheckinAndCheckout(
                LocalDate.parse("2014-03-13"), LocalDate.parse("2014-03-13"));
        //Status code is 200
        ApiAssertions.assertStatusCode(response, 200);
        //The response is an empty array
        ApiAssertions.assertBodyEmptyArray(response);
    }

}