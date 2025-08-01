package tests;

import io.restassured.response.Response;
import org.testng.annotations.Test;
import services.BookingService;
import utils.ApiAssertions;
import utils.TestDataGenerator;

public class GetSpecificBookingTests {

    @Test(description = "Fetch existing booking")
    public void getBookingByIdSuccess(){
        Response response = BookingService.getBooking(1);
        //Status code is 200
        ApiAssertions.assertStatusCode(response, 200);
        //The response contains first name and last name
        ApiAssertions.assertResponseContainsField(response, "firstname");
        ApiAssertions.assertResponseContainsField(response, "lastname");
        //Validate the booking schema
        ApiAssertions.assertJsonSchema(response, "BookingSpecificIdJsonSchemaFile.json");
        //Validate response time
        ApiAssertions.assertResponseTimeLessThan(response, 1500);
    }

    @Test(description = "Fetch nonexistent booking")
    public void getBookingById_NonexistentBooking(){
        Response response = BookingService.getBooking(TestDataGenerator.generateInvalidBookingId());
        //Status code is 200
        ApiAssertions.assertStatusCode(response, 404);
        //Validate response time
        ApiAssertions.assertResponseTimeLessThan(response, 1500);
    }
}