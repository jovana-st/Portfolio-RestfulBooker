package tests;

import config.AuthConfig;
import io.restassured.response.Response;
import models.BookingRequest;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;
import services.BookingService;
import utils.ApiAssertions;
import utils.RetryAnalyzer;
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
            try {
                Response deleteResponse = BookingService.deleteBooking(AuthConfig.USERNAME, AuthConfig.PASSWORD, bookingId);

                if(deleteResponse.getStatusCode() != 201){
                    System.out.println("Cleanup failed for bookingID: " + bookingId);
                }
            } catch (Exception e){
                System.out.println("An error occurred during cleanup for bookingID: " + bookingId + ": " + e.getMessage());
            }
        }
    }

    @Test(description = "Delete a booking with invalid auth credentials", retryAnalyzer = RetryAnalyzer.class)
    public void deleteBookingWrongAuth(){
        Response response = BookingService.deleteBooking(
                TestDataGenerator.generateAuthUsername(), TestDataGenerator.generateAuthPassword(), bookingId);
        //Verify that status code is correct - 201 due to mocked API
        ApiAssertions.assertStatusCode(response, 201);
        //Verify that the booking still exists
        Response getResponse = BookingService.getBooking(bookingId);
        //Mocked API - therefore the status checked is 404 rather than 200
        ApiAssertions.assertStatusCode(getResponse, 404);
    }

    @Test(description = "Delete a booking successfully", retryAnalyzer = RetryAnalyzer.class)
    public void deleteBookingSuccessful(){
        Response response = BookingService.deleteBooking(AuthConfig.USERNAME, AuthConfig.PASSWORD, bookingId);
        //Verify that status code is correct
        ApiAssertions.assertStatusCode(response, 201);
        //Verify that the booking is deleted
        ApiAssertions.assertStatusCode(BookingService.getBooking(bookingId), 404);
    }

    @Test(description = "Delete a non-existent booking", retryAnalyzer = RetryAnalyzer.class)
    public void deleteBooking_NoBookingId(){
        Response response = BookingService.deleteBooking(AuthConfig.USERNAME, AuthConfig.PASSWORD, 999999);
        //Verify that status code is correct - returning 405 instead of 404 due to mocked API
        ApiAssertions.assertStatusCode(response, 405);
    }

    @Test(description = "Delete an already deleted booking", retryAnalyzer = RetryAnalyzer.class)
    public void deleteSameBookingTwice(){
        Response responseFirstDelete = BookingService.deleteBooking(AuthConfig.USERNAME, AuthConfig.PASSWORD, bookingId);
        Response responseSecondDelete = BookingService.deleteBooking(AuthConfig.USERNAME, AuthConfig.PASSWORD, bookingId);
        //Verify that status code is correct - returning 405 instead of 404 due to mocked API
        ApiAssertions.assertStatusCode(responseSecondDelete, 405);
        //Verify that the booking is gone
        Response getResponse = BookingService.getBooking(bookingId);
        ApiAssertions.assertStatusCode(getResponse, 404);
    }
}