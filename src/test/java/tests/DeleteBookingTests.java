package tests;

import config.AuthConfig;
import io.restassured.response.Response;
import models.BookingRequest;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.BeforeTest;
import org.testng.annotations.Test;
import services.BookingService;
import utils.ApiAssertions;
import utils.TestDataGenerator;

public class DeleteBookingTests {

    private int bookingId;

    @BeforeMethod
    public void setupTestData(){
        //Create a new booking before each update test
        BookingRequest originalBooking = TestDataGenerator.generateBookingRequest();
        Response createResponse = BookingService.createBooking(originalBooking);
        bookingId = createResponse.jsonPath().getInt("bookingid");
    }

    @AfterMethod
    public void cleanup(){
        if (bookingId > 0){
            BookingService.deleteBooking(AuthConfig.USERNAME, AuthConfig.PASSWORD, bookingId);
        }
    }

    @Test(description = "Delete a booking with invalid auth credentials")
    public void deleteBookingWrongAuth(){
        Response response = BookingService.deleteBooking(
                TestDataGenerator.generateAuthUsername(), TestDataGenerator.generateAuthPassword(), bookingId);
        //Verify that status code is correct
        ApiAssertions.assertStatusCode(response, 403);
        //Verify that the booking still exists
        Response getResponse = BookingService.getBooking(bookingId);
        ApiAssertions.assertStatusCode(getResponse, 200);
    }

    @Test(description = "Delete a booking successfully")
    public void deleteBookingSuccessful(){
        Response response = BookingService.deleteBooking(AuthConfig.USERNAME, AuthConfig.PASSWORD, bookingId);
        //Verify that status code is correct
        ApiAssertions.assertStatusCode(response, 201);
        //Verify that the booking is deleted
        ApiAssertions.assertStatusCode(BookingService.getBooking(bookingId), 404);
    }

    @Test(description = "Delete a non-existent booking")
    public void deleteBooking_NoBookingId(){
        Response response = BookingService.deleteBooking(AuthConfig.USERNAME, AuthConfig.PASSWORD, 999999);
        //Verify that status code is correct
        ApiAssertions.assertStatusCode(response, 405);
    }

    @Test(description = "Delete an already deleted booking")
    public void deleteSameBookingTwice(){
        Response responseFirstDelete = BookingService.deleteBooking(AuthConfig.USERNAME, AuthConfig.PASSWORD, bookingId);
        Response responseSecondDelete = BookingService.deleteBooking(AuthConfig.USERNAME, AuthConfig.PASSWORD, bookingId);
        //Verify that status code is correct
        ApiAssertions.assertStatusCode(responseSecondDelete, 405);
        //Verify that the booking is gone
        Response getResponse = BookingService.getBooking(bookingId);
        ApiAssertions.assertStatusCode(getResponse, 404);
    }
}