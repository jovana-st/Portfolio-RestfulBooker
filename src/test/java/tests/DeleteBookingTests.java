package tests;

import config.AuthConfig;
import io.restassured.response.Response;
import models.BookingRequest;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.BeforeTest;
import org.testng.annotations.Test;
import services.BookingService;
import utils.ApiAssertions;
import utils.TestDataGenerator;

public class DeleteBookingTests {

    int bookingId = -1;

    @BeforeMethod
    public void setupTestData(){
        //Create a new booking before each update test
        BookingRequest originalBooking = TestDataGenerator.generateBookingRequest();
        Response createResponse = BookingService.createBooking(originalBooking);
        bookingId = createResponse.jsonPath().getInt("bookingid");
    }

    @Test(description = "Delete a booking with invalid auth credentials")
    public void deleteBookingWrongAuth(){
        Response response = BookingService.deleteBooking(
                TestDataGenerator.generateAuthUsername(), TestDataGenerator.generateAuthPassword(), bookingId);
        ApiAssertions.assertStatusCode(response, 405);
    }

    @Test(description = "Delete a booking successfully")
    public void deleteBookingSuccessful(){
        Response response = BookingService.deleteBooking(AuthConfig.USERNAME, AuthConfig.PASSWORD, bookingId);
        ApiAssertions.assertStatusCode(response, 201);
        ApiAssertions.assertStatusCode(BookingService.getBooking(bookingId), 404);
    }

    @Test(description = "Delete a non-existent booking")
    public void deleteBooking_NoBookingId(){
        Response response = BookingService.deleteBooking(AuthConfig.USERNAME, AuthConfig.PASSWORD, 999999);
        ApiAssertions.assertStatusCode(response, 405);
    }

    @Test(description = "Delete an already deleted booking")
    public void deleteSameBookingTwice(){
        Response responseFirstDelete = BookingService.deleteBooking(AuthConfig.USERNAME, AuthConfig.PASSWORD, bookingId);
        Response responseSecondDelete = BookingService.deleteBooking(AuthConfig.USERNAME, AuthConfig.PASSWORD, bookingId);
        ApiAssertions.assertStatusCode(responseSecondDelete, 405);
    }
}