package users;

import io.qameta.allure.junit4.DisplayName;
import io.restassured.RestAssured;
import io.restassured.response.Response;
import org.apache.commons.lang3.RandomStringUtils;
import org.junit.Before;
import org.junit.Test;
import serializer.User;
import steps.UserSteps;

import static org.apache.http.HttpStatus.*;
import static org.hamcrest.CoreMatchers.equalTo;
import static steps.BaseUrls.BASE_URL;

public class LoginUserTest {
    User user;
    UserSteps userSteps;

    @Before
    public void setUp() {
        RestAssured.baseURI = BASE_URL;
        userSteps = new UserSteps();
        user = new User();
    }

    @Test
    @DisplayName("login under an existing user")
    public void checkUserAuthPositiveTest() {
        User user = new User(RandomStringUtils.randomAlphanumeric(10) + "@yandex.ru",
                RandomStringUtils.randomAlphanumeric(10),
                RandomStringUtils.randomAlphabetic(20));
        userSteps.createUser(user);
        Response response = userSteps.loginUser(user);
        response.then()
                .assertThat()
                .statusCode(SC_OK)
                .and()
                .body("success", equalTo(true));
    }

    @Test
    @DisplayName("login with an invalid username")
    public void checkUserLoginWithInvalidUsername() {
        User user = new User(RandomStringUtils.randomAlphanumeric(10) + "@yandex.ru",
                RandomStringUtils.randomAlphanumeric(10),
                RandomStringUtils.randomAlphabetic(20));
        userSteps.createUser(user);
        user.setEmail("random");
        Response response = userSteps.loginUser(user);
        response.then()
                .assertThat()
                .statusCode(SC_UNAUTHORIZED)
                .and()
                .body("message", equalTo("email or password are incorrect"));
    }

    @Test
    @DisplayName("login with an invalid password")
    public void checkUserLoginWithInvalidPassword() {
        User user = new User(RandomStringUtils.randomAlphanumeric(10) + "@yandex.ru",
                RandomStringUtils.randomAlphanumeric(10),
                RandomStringUtils.randomAlphabetic(20));
        userSteps.createUser(user);
        user.setPassword("random");
        Response response = userSteps.loginUser(user);
        response.then()
                .assertThat()
                .statusCode(SC_UNAUTHORIZED)
                .and()
                .body("message", equalTo("email or password are incorrect"));
    }
}
