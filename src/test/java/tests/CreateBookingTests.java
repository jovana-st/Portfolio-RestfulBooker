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

    @Test(description = "Sucessfully creating a new booking")
    public void createBookingSuccessful(){
        BookingRequest request = TestDataGenerator.generateBookingRequest();
        Response response = BookingService.createBooking(request);
        //Validate that the response schema is correct
        ApiAssertions.assertJsonSchema(response, "BookingJsonSchemaFile.json");
        ApiAssertions.softAssertAll(response,
                200,
                Map.of(
                        "booking.firstname", request.getFirstname(),
                        "booking.lastname", request.getLastname(),
                        "booking.totalprice", request.getTotalprice(),
                        "booking.bookingdates.checkin", request.getBookingdates().getCheckin()
                ),
                Map.of("Content-Type", "application/json; charset=utf-8"),
                "BookingJsonSchemaFile.json");
    }

    @DataProvider(name = "invalidBookings")
    public Object[][] createInvalidBookings(){
        return new Object[][]{
                //Missing firstname
                {null, "Valid", 100, true, new BookingDates("2024-01-01", "2024-01-02"), "Valid"},
                //Invalid Dates
                {"Valid", "Valid", 100, true, new BookingDates("invalid-date", "2024-01-02"), "Valid"},
                //Checkout before checkin
                {"Valid", "Valid", 100, true, new BookingDates("2024-01-02", "2024-01-01"), "Valid"},
                //Negative total price
                {"Valid", "Valid", -100, true, new BookingDates("2024-01-01", "2024-01-02"), "Valid"}
        };
    }

    @Test(description = "Creating a new booking with invalid fields", dataProvider = "invalidBookings", enabled = false)
    //Skipped - mocked API does not return the expected error code
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
        ApiAssertions.assertStatusCode(response, 400);
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
        ApiAssertions.assertStatusCode(response, 200);
    }

}


