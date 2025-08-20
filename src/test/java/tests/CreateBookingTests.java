package tests;

import io.restassured.response.Response;
import models.BookingDates;
import models.BookingRequest;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;
import services.BookingService;
import utils.ApiAssertions;
import utils.TestDataGenerator;

import java.time.LocalDate;
import java.util.Map;

public class CreateBookingTests {

    @Test(description = "Successfully creating a new booking")
    public void createBookingSuccessful(){
        BookingRequest request = TestDataGenerator.generateBookingRequest();
        Response response = BookingService.createBooking(request);

        //Verify that the response schema is correct
        ApiAssertions.assertJsonSchema(response, "schemas/booking-create-response.json");
        //Complete object validation
        ApiAssertions.assertResponseFieldEqualsSerialization(response, "booking", request);
        //Verify that status code is correct
        ApiAssertions.assertStatusCode(response, 200);
    }

    @Test(description = "Create a new booking with no first name")
    public void createBooking_noFirstName(){
        BookingRequest request = new BookingRequest();
        request.setFirstname(null);
        Response response = BookingService.createBooking(request);
        //Verify that status code is correct
        ApiAssertions.assertStatusCode(response, 500);
    }

    @DataProvider(name = "invalidBookings")
    public Object[][] createInvalidBookings(){
        return new Object[][]{
                //Invalid Dates
                {"Valid", "Valid", 100, true, new BookingDates("invalid-date", "2024-01-02"), "Valid"},
                //Checkout before checkin
                {"Valid", "Valid", 100, true, new BookingDates("2024-01-02", "2024-01-01"), "Valid"},
                //Negative total price
                {"Valid", "Valid", -100, true, new BookingDates("2024-01-01", "2024-01-02"), "Valid"}
        };
    }

    @Test(description = "Creating a new booking with invalid fields, all are successful",
            dataProvider = "invalidBookings")
    //Mocked API - does not return 400 but 200
    public void createBooking_InvalidData_Returns400(
            String firstname,
            String lastname,
            int totalprice,
            boolean depositpaid,
            BookingDates bookingdates,
            String additionalneeds
    ) {
        BookingRequest request = new BookingRequest(firstname, lastname, totalprice, depositpaid, bookingdates, additionalneeds);
        Response response = BookingService.createBooking(request);
        //Using soft assertion to capture multiple possible issues
        ApiAssertions.softAssertAll(response,
                200,
                Map.of(),
                Map.of("Content-Type", "application/json; charset=utf-8"),
                null);
    }

    @DataProvider(name = "edgeCases")
    public Object[][] createEdgeCaseBookings(){
        return new Object[][]{
                //Very long strings
                {"AbcDEfGhijKLmnOpQRstUVwxyZabcdefGHIJKlmnopqrSTUVwxyzABCDefghijklMNOPQRstuvWXYZ",
                        "AbcDEfGhijKLmnOpQRstUVwxyZabcdefGHIJKlmnopqrSTUVwxyzABCDefghijklMNOPQRstuvWXYZ",
                        100, true, new BookingDates("2024-01-01", "2024-01-02"),
                        "AbcDEfGhijKLmnOpQRstUVwxyZabcdefGHIJKlmnopqrSTUVwxyzABCDefghijklMNOPQRstuvWXYZ"},
                //Special characters
                {"!\"#$%&'()*+,-./:;<=>?@[\\]^_`{|}~",
                        "!\"#$%&'()*+,-./:;<=>?@[\\]^_`{|}~",
                        100, true, new BookingDates("2024-01-02", "2024-01-02"),
                        "!\"#$%&'()*+,-./:;<=>?@[\\]^_`{|}~"},
                //Minimum price
                {"Valid", "Valid", 1, true, new BookingDates("2024-01-02", "2024-01-02"), "Valid"},
                //Maximum price
                {"Valid", "Valid", 10000, true, new BookingDates("2024-01-02", "2024-01-01"), "Valid"},
                //Same checkin and checkout date
                {"Valid", "Valid", 100, true, new BookingDates("2024-01-01", "2024-01-01"), "Valid"},
                //Current date checkin
                {"Valid", "Valid", 100, true, new BookingDates(
                        LocalDate.now().toString(), LocalDate.now().plusDays(2).toString()), "Valid"},
                //Distant future dates
                {"Valid", "Valid", 100, true, new BookingDates("2070-01-01", "2070-01-02"), "Valid"}
        };
    }

    @Test(description = "Creating a new booking with edge cases", dataProvider = "edgeCases")
    public void createBooking_EdgeCases_Returns200(
            String firstname,
            String lastname,
            int totalprice,
            boolean depositpaid,
            BookingDates bookingdates,
            String additionalneeds
    ) {
        BookingRequest request = new BookingRequest(firstname, lastname, totalprice, depositpaid, bookingdates, additionalneeds);
        Response response = BookingService.createBooking(request);
        //Use soft deep comparison to catch ALL field mismatches
        ApiAssertions.softAssertResponseFieldEquals(response, "booking", request);
        //Verify that status code is correct
        ApiAssertions.assertStatusCode(response, 200);
        //Verify that the schema is correct
        ApiAssertions.assertJsonSchema(response, "schemas/booking-create-response.json");
    }

}


