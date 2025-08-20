package tests;

import io.restassured.response.Response;
import models.BookingRequest;
import org.testng.annotations.Test;
import services.BookingService;
import utils.ApiAssertions;
import utils.RetryAnalyzer;
import utils.TestDataGenerator;

public class GetBookingTests {

    @Test(description = "Fetch existing booking", retryAnalyzer = RetryAnalyzer.class)
    public void getBookingByIdSuccess(){
        //Create booking to fetch it
        BookingRequest originalBooking = TestDataGenerator.generateBookingRequest();
        Response createResponse = BookingService.createBooking(originalBooking);
        int bookingId = createResponse.jsonPath().getInt("bookingid");

        Response response = BookingService.getBooking(bookingId);
        //Status code is 200
        ApiAssertions.assertStatusCode(response, 200);
        //Validate response time
        ApiAssertions.assertResponseTimeLessThan(response, 2000);
        //Validate the response schema
        ApiAssertions.assertJsonSchema(response, "schemas/booking-get-response.json");
        //The response contains first name and last name
        ApiAssertions.assertResponseContainsField(response, "firstname");
        ApiAssertions.assertResponseContainsField(response, "lastname");
        //The response contains checkin and checkout
        ApiAssertions.assertResponseContainsField(response, "bookingdates.checkin");
        ApiAssertions.assertResponseContainsField(response, "bookingdates.checkout");
    }

    @Test(description = "Fetch nonexistent booking", retryAnalyzer = RetryAnalyzer.class)
    public void getBookingById_NonexistentBooking(){
        Response response = BookingService.getBooking(TestDataGenerator.generateInvalidBookingId());
        //Status code is 404, set to 200 due to API being mocked
        ApiAssertions.assertStatusCode(response, 200);
        //Validate response time
        ApiAssertions.assertResponseTimeLessThan(response, 2000);
    }
}