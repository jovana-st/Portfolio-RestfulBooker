package tests;

import config.AuthConfig;
import io.restassured.response.Response;
import models.BookingDates;
import models.BookingRequest;
import org.testng.Assert;
import org.testng.annotations.*;
import services.BookingService;
import utils.ApiAssertions;
import utils.TestDataGenerator;

import java.time.LocalDate;
import java.util.Map;

public class UpdateBookingTests {

    int bookingId = -1;

    @BeforeMethod
    public void setupTestData(){
        //Create a new booking before each update test
        BookingRequest originalBooking = TestDataGenerator.generateBookingRequest();
        Response createResponse = BookingService.createBooking(originalBooking);
        bookingId = createResponse.jsonPath().getInt("bookingid");
    }

    @AfterMethod
    public void cleanupTestData(){
        //Delete the test booking after each test
        if (bookingId >0){
            BookingService.deleteBooking(AuthConfig.USERNAME, AuthConfig.PASSWORD, bookingId);
        }
    }

    @Test(description = "Update booking with invalid auth credentials")
    public void updateBookingInvalidAuth(){

        BookingRequest updatedbooking = new BookingRequest();
        updatedbooking.setFirstname("UpdatedName");
        updatedbooking.setLastname("UpdatedLastName");
        updatedbooking.setTotalprice(999);
        updatedbooking.setDepositpaid(false);
        updatedbooking.setBookingdates(new BookingDates("2025-01-01", "2025-01-10"));
        updatedbooking.setAdditionalneeds("UpdatedAdditionalNeeds");

        Response updateResponse = BookingService.updateBooking(
                TestDataGenerator.generateAuthUsername(), TestDataGenerator.generateAuthPassword(),
                bookingId, updatedbooking);
        ApiAssertions.assertStatusCode(updateResponse, 403);
    }

    @Test(description = "Update a booking successfully.")
    public void updateBookingSuccess(){

        BookingRequest updatedbooking = new BookingRequest();
        updatedbooking.setFirstname("UpdatedName");
        updatedbooking.setLastname("UpdatedLastName");
        updatedbooking.setTotalprice(999);
        updatedbooking.setDepositpaid(false);
        updatedbooking.setBookingdates(new BookingDates("2025-01-01", "2025-01-10"));
        updatedbooking.setAdditionalneeds("UpdatedAdditionalNeeds");

        Response updateResponse = BookingService.updateBooking(
                AuthConfig.USERNAME, AuthConfig.PASSWORD, bookingId, updatedbooking);
        ApiAssertions.softAssertAll(updateResponse,
                200,
                Map.of(
                        "firstname", updatedbooking.getFirstname(),
                        "lastname", updatedbooking.getLastname(),
                        "totalprice", updatedbooking.getTotalprice(),
                        "bookingdates.checkin", updatedbooking.getBookingdates().getCheckin()
                ),
                Map.of("Content-Type", "application/json; charset=utf-8"),
                "UpdateBookingJsonSchema.json");
    }

    @DataProvider(name = "invalidUpdates")
    public Object[][] invalidUpdateData(){
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

    @Test(description = "Update booking with invalid data", dataProvider = "invalidUpdates", enabled = false)
    //Skipped - mocked API does not return the expected error code
    public void updateBooking_InvalidData_Returns400(
            String firstname,
            String lastname,
            int totalprice,
            boolean depositpaid,
            BookingDates bookingdates,
            String additionalneeds
    ) {
        BookingRequest updateRequest = new BookingRequest(
                firstname, lastname, totalprice, depositpaid, bookingdates, additionalneeds);
        Response response = BookingService.updateBooking(AuthConfig.USERNAME, AuthConfig.PASSWORD, bookingId, updateRequest);
        ApiAssertions.assertStatusCode(response, 400);
    }


    @DataProvider(name = "edgeCases")
    public Object[][] UpdateDataEdgeCases(){
        return new Object[][]{
                //Very long strings
                {"AbcDEfGhijKLmnOpQRstUVwxyZabcdefGHIJKlmnopqrSTUVwxyzABCDefghijklMNOPQRstuvWXYZ",
                        "AbcDEfGhijKLmnOpQRstUVwxyZabcdefGHIJKlmnopqrSTUVwxyzABCDefghijklMNOPQRstuvWXYZ",
                        100, true, new BookingDates("2024-01-01", "2024-01-02"),
                        "AbcDEfGhijKLmnOpQRstUVwxyZabcdefGHIJKlmnopqrSTUVwxyzABCDefghijklMNOPQRstuvWXYZ"},
                //Special characters
                {"!\"#$%&'()*+,-./:;<=>?@[\\]^_`{|}~",
                        "!\"#$%&'()*+,-./:;<=>?@[\\]^_`{|}~",
                        100, true, new BookingDates("invalid-date", "2024-01-02"),
                        "!\"#$%&'()*+,-./:;<=>?@[\\]^_`{|}~"},
                //Minimum price
                {"Valid", "Valid", 1, true, new BookingDates("2024-01-02", "2024-01-01"), "Valid"},
                //Maximum price
                {"Valid", "Valid", 10000, true, new BookingDates("2024-01-01", "2024-01-02"), "Valid"},
                //Same checkin and checkout date
                {"Valid", "Valid", 100, true, new BookingDates("2024-01-01", "2024-01-01"), "Valid"},
                //Current date checkin
                {"Valid", "Valid", 100, true, new BookingDates(
                        LocalDate.now().toString(), LocalDate.now().plusDays(2).toString()), "Valid"},
                //Distant future dates
                {"Valid", "Valid", 100, true, new BookingDates("2070-01-01", "2070-01-02"), "Valid"}
        };
    }

    @Test(description = "Updating a new booking with edge cases", dataProvider = "edgeCases")
    public void updateBooking_EdgeCases_Returns200(
            String firstname,
            String lastname,
            int totalprice,
            boolean depositpaid,
            BookingDates bookingdates,
            String additionalneeds
    ) {
        BookingRequest updateRequest = new BookingRequest(
                firstname, lastname, totalprice, depositpaid, bookingdates, additionalneeds);
        Response response = BookingService.updateBooking(AuthConfig.USERNAME, AuthConfig.PASSWORD, bookingId, updateRequest);
        ApiAssertions.assertStatusCode(response, 200);
    }

    @Test(description = "Updating the booking with the exact same data")
    public void updateBooking_multipleSameUpdates_returnsConsistentData(){
        BookingRequest updatedbooking = new BookingRequest();
        updatedbooking.setFirstname("UpdatedName");
        updatedbooking.setLastname("UpdatedLastName");
        updatedbooking.setTotalprice(999);
        updatedbooking.setDepositpaid(false);
        updatedbooking.setBookingdates(new BookingDates("2025-01-01", "2025-01-10"));
        updatedbooking.setAdditionalneeds("UpdatedAdditionalNeeds");

        Response firstUpdate = BookingService.updateBooking(
                AuthConfig.USERNAME, AuthConfig.PASSWORD, bookingId, updatedbooking);
        Response secondUpdate = BookingService.updateBooking(
                AuthConfig.USERNAME, AuthConfig.PASSWORD, bookingId, updatedbooking);

        ApiAssertions.assertStatusCode(firstUpdate, 200);
        ApiAssertions.assertStatusCode(secondUpdate, 200);
        Assert.assertEquals(firstUpdate.jsonPath().getString("firstname"),secondUpdate.jsonPath().getString("firstname"));
    }
}