package tests;

import config.AuthConfig;
import io.restassured.response.Response;
import models.BookingDates;
import models.BookingRequest;
import org.testng.Assert;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;
import services.BookingService;
import utils.ApiAssertions;
import utils.TestDataGenerator;

import java.util.Map;

public class PartialUpdateBookingTests {

    private int bookingId;
    BookingRequest originalBooking = new BookingRequest();

    @BeforeMethod
    public void setupTestData(){
        //Create a new booking before each update test
        originalBooking = TestDataGenerator.generateBookingRequest();
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
    public void partialUpdateBookingInvalidAuth(){

        Map<String, Object> updates = Map.of(
                "firstname", "UpdatedName",
                "lastname", "UpdatedLastName"
        );

        Response updateResponse = BookingService.partialUpdateBooking(
                TestDataGenerator.generateAuthUsername(), TestDataGenerator.generateAuthPassword(),
                bookingId, updates);
        //Validate that the status code is 403
        ApiAssertions.assertStatusCode(updateResponse, 403);
        //Validate that the booking was not updated due to the failure - failing due to mocked API
        /*Response getResponse = BookingService.getBooking(bookingId);
        ApiAssertions.assertResponseFieldEquals(getResponse, "firstname", originalBooking.getFirstname());
        ApiAssertions.assertResponseFieldEquals(getResponse, "firstname", originalBooking.getLastname());*/
    }

    @Test(description = "Update a booking first name successfully.")
    public void partialUpdateBookingSuccess_firstName(){

        Map<String, Object> updates = Map.of(
                "firstname", "UpdatedName"
        );
        BookingRequest expectedBooking = originalBooking;
        expectedBooking.setFirstname("UpdatedName");


        Response updateResponse = BookingService.partialUpdateBooking(
                AuthConfig.USERNAME, AuthConfig.PASSWORD, bookingId, updates);

        //Verify that response code is 200
        ApiAssertions.assertStatusCode(updateResponse, 200);
        //Using deep comparison for complete validation
        ApiAssertions.assertResponseFieldEqualsSerialization(updateResponse, "$", expectedBooking);
        //Verify that the update persists
        Response getResponse = BookingService.getBooking(bookingId);
        ApiAssertions.assertResponseFieldEqualsSerialization(getResponse, "$", expectedBooking);
    }

    @Test(description = "Update a booking checkin and checkout dates successfully.")
    public void partialUpdateBookingSuccess_CheckinAndOutDates(){

        Map<String, String> bookingDatesMap = Map.of(
                "checkin", "2025-01-01",
                "checkout", "2025-01-10"
        );

        Map<String, Object> updates = Map.of(
                "bookingdates", bookingDatesMap
        );

        BookingDates expectedDates = new BookingDates("2025-01-01", "2025-01-10");

        Response updateResponse = BookingService.partialUpdateBooking(
                AuthConfig.USERNAME, AuthConfig.PASSWORD, bookingId, updates);

        //Verify that response code is 200
        ApiAssertions.assertStatusCode(updateResponse, 200);
        //Validate the dates are updated
        ApiAssertions.assertResponseFieldEqualsSerialization(updateResponse, "bookingdates", expectedDates);
        //Verify that other fields are not updated
        ApiAssertions.assertResponseFieldEquals(updateResponse, "firstname", originalBooking.getFirstname());
        ApiAssertions.assertResponseFieldEquals(updateResponse, "lastname", originalBooking.getLastname());
    }

    @Test(description = "Update a booking price and additional needs successfully.")
    public void partialUpdateBookingSuccess_additionalNeedsAndPrice(){

        Map<String, Object> updates = Map.of(
                "additionalneeds", "UpdatedNeeds",
                "totalprice", 1
        );


        Response updateResponse = BookingService.partialUpdateBooking(
                AuthConfig.USERNAME, AuthConfig.PASSWORD, bookingId, updates);
        //Verify that response code is 200
        ApiAssertions.assertStatusCode(updateResponse, 200);
        //Validate the additional needs and price are updated
        ApiAssertions.assertResponseFieldEquals(updateResponse, "additionalneeds", "UpdatedNeeds");
        ApiAssertions.assertResponseFieldEquals(updateResponse, "totalprice", 1);
        //Verify that other fields are not updated
        ApiAssertions.assertResponseFieldEquals(updateResponse, "firstname", originalBooking.getFirstname());
        ApiAssertions.assertResponseFieldEquals(updateResponse, "lastname", originalBooking.getLastname());
    }

    @Test(description = "Update a booking deposit status")
    public void partialUpdateBookingSuccess_toggleDepositPaid(){

        Map<String, Object> updates = Map.of(
                "depositpaid", !originalBooking.isDepositpaid()
        );


        Response updateResponse = BookingService.partialUpdateBooking(
                AuthConfig.USERNAME, AuthConfig.PASSWORD, bookingId, updates);

        //Verify that response code is 200
        ApiAssertions.assertStatusCode(updateResponse, 200);
        //Validate the deposit paid is updated
        ApiAssertions.assertResponseFieldEquals(updateResponse, "depositpaid", !originalBooking.isDepositpaid());
        //Verify that other fields are not updated
        ApiAssertions.assertResponseFieldEquals(updateResponse, "firstname", originalBooking.getFirstname());
        ApiAssertions.assertResponseFieldEquals(updateResponse, "lastname", originalBooking.getLastname());
    }

    @Test(description = "Update a booking with a minimal payload")
    public void partialUpdateBookingSuccess_minimalPayload(){

        Map<String, Object> updates = Map.of();

        Response updateResponse = BookingService.partialUpdateBooking(
                AuthConfig.USERNAME, AuthConfig.PASSWORD, bookingId, updates);
        //Verify that response code is 200
        ApiAssertions.assertStatusCode(updateResponse, 200);
        //Verify that nothing changed with an empty payload
        ApiAssertions.assertResponseFieldEqualsSerialization(updateResponse, "$", originalBooking);
    }

    @Test(description = "Update a booking with the same partial payload twice")
    public void partialUpdateBookingSuccess_updateTwice(){

        Map<String, Object> updates = Map.of(
                "firstname", "UpdatedName"
        );


        Response updateResponseFirst = BookingService.partialUpdateBooking(
                AuthConfig.USERNAME, AuthConfig.PASSWORD, bookingId, updates);
        Response updateResponseSecond = BookingService.partialUpdateBooking(
                AuthConfig.USERNAME, AuthConfig.PASSWORD, bookingId, updates);
        //Both calls have the same status code 200
        ApiAssertions.assertStatusCode(updateResponseFirst, 200);
        ApiAssertions.assertStatusCode(updateResponseSecond, 200);
        //Both calls return the same result
        Assert.assertEquals(updateResponseFirst.getBody().asString(), updateResponseSecond.getBody().asString(),
                "Duplicate updates should return the same response");
        //Verify the final state
        Response getResponse = BookingService.getBooking(bookingId);
        ApiAssertions.assertResponseFieldEquals(getResponse, "firstname", "UpdatedName");
    }

    @Test(description = "Update a booking partially, then fully.")
    public void partialUpdateBookingSuccess_partialThenFull(){

        Map<String, Object> updates = Map.of(
                "firstname", "UpdatedPartialName"
        );

        Response partialUpdateResponse = BookingService.partialUpdateBooking(
                AuthConfig.USERNAME, AuthConfig.PASSWORD, bookingId, updates);
        ApiAssertions.softAssertStatusCode(partialUpdateResponse, 200);

        BookingRequest updatedFullBooking = new BookingRequest();
        updatedFullBooking.setFirstname("UpdatedFullName");
        updatedFullBooking.setLastname("UpdatedLastName");
        updatedFullBooking.setTotalprice(999);
        updatedFullBooking.setDepositpaid(false);
        updatedFullBooking.setBookingdates(new BookingDates("2025-01-01", "2025-01-10"));
        updatedFullBooking.setAdditionalneeds("UpdatedAdditionalNeeds");

        Response fullUpdateResponse = BookingService.updateBooking(
                AuthConfig.USERNAME, AuthConfig.PASSWORD, bookingId, updatedFullBooking);
        ApiAssertions.softAssertStatusCode(fullUpdateResponse, 200);

        Response getBookingResponse = BookingService.getBooking(bookingId);
        ApiAssertions.softAssertStatusCode(getBookingResponse, 200);
        ApiAssertions.assertResponseFieldEqualsSerialization(getBookingResponse, "$", updatedFullBooking);
    }
}