package tests;

import io.restassured.http.ContentType;
import io.restassured.response.Response;
import org.testng.Assert;
import org.testng.annotations.Test;

import static io.restassured.RestAssured.given;

public class ApiTest {

    private static final String BASE_URL =
            "https://jsonplaceholder.typicode.com";

    @Test
    public void verifyGetUser() {
        Response response = given()
                .when()
                .get(BASE_URL + "/users/1")
                .then()
                .extract()
                .response();

        Assert.assertEquals(response.statusCode(), 200);
        Assert.assertEquals(response.jsonPath().getString("id"), "1");
        Assert.assertEquals(response.jsonPath().getString("username"), "Bret");
    }

    @Test
    public void verifyCreateUser() {
        String requestBody = """
                {
                    "name": "Apurva QA",
                    "username": "apurvaqa",
                    "email": "apurva@example.com"
                }
                """;

        Response response = given()
                .contentType(ContentType.JSON)
                .body(requestBody)
                .when()
                .post(BASE_URL + "/users")
                .then()
                .extract()
                .response();

        Assert.assertEquals(response.statusCode(), 201);
        Assert.assertEquals(
                response.jsonPath().getString("name"),
                "Apurva QA"
        );
        Assert.assertEquals(
                response.jsonPath().getString("username"),
                "apurvaqa"
        );
    }

    @Test
    public void verifyInvalidUser() {
        Response response = given()
                .when()
                .get(BASE_URL + "/users/9999")
                .then()
                .extract()
                .response();

        Assert.assertEquals(response.statusCode(), 404);
    }

    @Test
    public void verifyMissingPost() {
        Response response = given()
                .when()
                .get(BASE_URL + "/posts/999999")
                .then()
                .extract()
                .response();

        Assert.assertEquals(response.statusCode(), 404);
    }
}
