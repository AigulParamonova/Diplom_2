package steps;

import io.qameta.allure.Step;
import io.restassured.response.Response;
import serializer.User;

import static io.restassured.RestAssured.given;

public class UserSteps {
    @Step("Creating a unique user")
    public Response createUser(User user) {
        return  given()
                .header("Content-type", "application/json")
                .and()
                .body(user)
                .when()
                .post(BaseUrls.REGISTER);
    }

    @Step("Login a user")
    public Response loginUser(User user) {
        return given()
                .header("Content-type", "application/json")
                .and()
                .body(user)
                .when()
                .post(BaseUrls.LOGIN);
    }

    @Step("Changing user data")
    public Response changeUserInfo(User user, String token) {
        return given()
                .header("Content-Type", "application/json")
                .header("authorization", token)
                .body(user)
                .when()
                .patch(BaseUrls.USER_INFO);
    }

    @Step("Delete user")
    public Response deleteUser(String accessToken) {
        return given()
                .header("Authorization", accessToken)
                .when()
                .delete(BaseUrls.USER_INFO);
    }
}
