package tests;

import io.restassured.response.Response;
import org.testng.annotations.Test;
import services.BookingService;
import utils.ApiAssertions;
import utils.TestDataGenerator;

public class CreateBookingTests {

    @Test(description = "Sucessfully creating a new booking")
    public void createBookingSuccessful(){
        Response response = BookingService.createBooking(TestDataGenerator.generateBookingRequest());
        //Status code is 200
        ApiAssertions.assertStatusCode(response, 200);
        //Validate that the response contains a booking ID
        ApiAssertions.assertResponseContainsField(response, "bookingid");
        //Validate that the response schema is correct
        ApiAssertions.assertJsonSchema(response, "BookingSpecificIdJsonSchemaFile.json");
    }
}