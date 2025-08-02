package tests;

import config.AuthConfig;
import io.restassured.response.Response;
import models.BookingDates;
import models.BookingRequest;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;
import services.BookingService;
import utils.ApiAssertions;
import utils.TestDataGenerator;

import java.util.Map;

public class PartialUpdateBookingTests {

    int bookingId = -1;
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
        ApiAssertions.assertStatusCode(updateResponse, 403);
    }

    @Test(description = "Update a booking first name successfully.")
    public void partialUpdateBookingSuccess_firstName(){

        Map<String, Object> updates = Map.of(
                "firstname", "UpdatedName"
        );


        Response updateResponse = BookingService.partialUpdateBooking(
                AuthConfig.USERNAME, AuthConfig.PASSWORD, bookingId, updates);
        ApiAssertions.softAssertAll(updateResponse,
                200,
                Map.of(
                        "firstname", updates.get("firstname"),
                        "lastname", originalBooking.getLastname(),
                        "totalprice", originalBooking.getTotalprice(),
                        "bookingdates.checkin", originalBooking.getBookingdates().getCheckin(),
                        "bookingdates.checkout", originalBooking.getBookingdates().getCheckout(),
                        "depositpaid", originalBooking.isDepositpaid(),
                        "additionalneeds", originalBooking.getAdditionalneeds()
                ),
                Map.of("Content-Type", "application/json; charset=utf-8"),
                "UpdateBookingJsonSchema.json");
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


        Response updateResponse = BookingService.partialUpdateBooking(
                AuthConfig.USERNAME, AuthConfig.PASSWORD, bookingId, updates);
        ApiAssertions.softAssertAll(updateResponse,
                200,
                Map.of(
                        "firstname", originalBooking.getFirstname(),
                        "lastname", originalBooking.getLastname(),
                        "totalprice", originalBooking.getTotalprice(),
                        "bookingdates.checkin", bookingDatesMap.get("checkin"),
                        "bookingdates.checkout", bookingDatesMap.get("checkout"),
                        "depositpaid", originalBooking.isDepositpaid(),
                        "additionalneeds", originalBooking.getAdditionalneeds()
                ),
                Map.of("Content-Type", "application/json; charset=utf-8"),
                "UpdateBookingJsonSchema.json");
    }

    @Test(description = "Update a booking price and additional needs successfully.")
    public void partialUpdateBookingSuccess_additionalNeedsAndPrice(){

        Map<String, Object> updates = Map.of(
                "additionalneeds", "UpdatedNeeds",
                "totalprice", 1
        );


        Response updateResponse = BookingService.partialUpdateBooking(
                AuthConfig.USERNAME, AuthConfig.PASSWORD, bookingId, updates);
        ApiAssertions.softAssertAll(updateResponse,
                200,
                Map.of(
                        "firstname", originalBooking.getFirstname(),
                        "lastname", originalBooking.getLastname(),
                        "totalprice", updates.get("totalprice"),
                        "bookingdates.checkin", originalBooking.getBookingdates().getCheckin(),
                        "bookingdates.checkout", originalBooking.getBookingdates().getCheckout(),
                        "depositpaid", originalBooking.isDepositpaid(),
                        "additionalneeds", updates.get("additionalneeds")
                ),
                Map.of("Content-Type", "application/json; charset=utf-8"),
                "UpdateBookingJsonSchema.json");
    }

    @Test(description = "Update a booking deposit status")
    public void partialUpdateBookingSuccess_toggleDepositPaid(){

        Map<String, Object> updates = Map.of(
                "depositpaid", !originalBooking.isDepositpaid()
        );


        Response updateResponse = BookingService.partialUpdateBooking(
                AuthConfig.USERNAME, AuthConfig.PASSWORD, bookingId, updates);
        ApiAssertions.softAssertAll(updateResponse,
                200,
                Map.of(
                        "firstname", originalBooking.getFirstname(),
                        "lastname", originalBooking.getLastname(),
                        "totalprice", originalBooking.getTotalprice(),
                        "bookingdates.checkin", originalBooking.getBookingdates().getCheckin(),
                        "bookingdates.checkout", originalBooking.getBookingdates().getCheckout(),
                        "depositpaid", updates.get("depositpaid"),
                        "additionalneeds", originalBooking.getAdditionalneeds()
                ),
                Map.of("Content-Type", "application/json; charset=utf-8"),
                "UpdateBookingJsonSchema.json");
    }

    @Test(description = "Update a booking with a minimal payload")
    public void partialUpdateBookingSuccess_minimalPayload(){

        Map<String, Object> updates = Map.of(
        );


        Response updateResponse = BookingService.partialUpdateBooking(
                AuthConfig.USERNAME, AuthConfig.PASSWORD, bookingId, updates);
        ApiAssertions.softAssertAll(updateResponse,
                200,
                Map.of(
                        "firstname", originalBooking.getFirstname(),
                        "lastname", originalBooking.getLastname(),
                        "totalprice", originalBooking.getTotalprice(),
                        "bookingdates.checkin", originalBooking.getBookingdates().getCheckin(),
                        "bookingdates.checkout", originalBooking.getBookingdates().getCheckout(),
                        "depositpaid", originalBooking.isDepositpaid(),
                        "additionalneeds", originalBooking.getAdditionalneeds()
                ),
                Map.of("Content-Type", "application/json; charset=utf-8"),
                "UpdateBookingJsonSchema.json");
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
        ApiAssertions.softAssertAll(updateResponseFirst,
                200,
                Map.of(
                        "firstname", updates.get("firstname"),
                        "lastname", originalBooking.getLastname(),
                        "totalprice", originalBooking.getTotalprice(),
                        "bookingdates.checkin", originalBooking.getBookingdates().getCheckin(),
                        "bookingdates.checkout", originalBooking.getBookingdates().getCheckout(),
                        "depositpaid", originalBooking.isDepositpaid(),
                        "additionalneeds", originalBooking.getAdditionalneeds()
                ),
                Map.of("Content-Type", "application/json; charset=utf-8"),
                "UpdateBookingJsonSchema.json");
        ApiAssertions.softAssertAll(updateResponseSecond,
                200,
                Map.of(
                        "firstname", updates.get("firstname"),
                        "lastname", originalBooking.getLastname(),
                        "totalprice", originalBooking.getTotalprice(),
                        "bookingdates.checkin", originalBooking.getBookingdates().getCheckin(),
                        "bookingdates.checkout", originalBooking.getBookingdates().getCheckout(),
                        "depositpaid", originalBooking.isDepositpaid(),
                        "additionalneeds", originalBooking.getAdditionalneeds()
                ),
                Map.of("Content-Type", "application/json; charset=utf-8"),
                "UpdateBookingJsonSchema.json");
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
        ApiAssertions.assertResponseFieldEquals(getBookingResponse,"firstname" , "UpdatedFullName");
    }
}