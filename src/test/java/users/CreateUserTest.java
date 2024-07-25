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

public class CreateUserTest {
    UserSteps userSteps;

    @Before
    public void setUp() {
        RestAssured.baseURI = BASE_URL;
        userSteps = new UserSteps();
    }

    @Test
    @DisplayName("check of creating a unique user")
    public void checkCreateUserPositiveTest() {
        User user = new User(RandomStringUtils.randomAlphanumeric(10) + "@yandex.ru",
                RandomStringUtils.randomAlphanumeric(10),
                RandomStringUtils.randomAlphabetic(20));
        Response response = userSteps.createUser(user);
        response.then()
                .assertThat()
                .body("success", equalTo(true))
                .and()
                .statusCode(SC_OK);
    }

    @Test
    @DisplayName("check of creating user already exist")
    public void checkCreateUserAlreadyExistTest() {
        User user = new User(RandomStringUtils.randomAlphanumeric(10) + "@yandex.ru",
                RandomStringUtils.randomAlphanumeric(10),
                RandomStringUtils.randomAlphabetic(20));
        //user.generateRandomUser();
        userSteps.createUser(user);
        Response response = userSteps.createUser(user);
        response.then()
                .assertThat()
                .body("success", equalTo(false))
                .and()
                .statusCode(SC_FORBIDDEN);
    }

    @Test
    @DisplayName("check of creating user without email")
    public void checkCreateUserWithoutEmailTest() {
        User user = new User(null,
                RandomStringUtils.randomAlphanumeric(10),
                RandomStringUtils.randomAlphabetic(20));
        Response response = userSteps.createUser(user);
        response.then()
                .assertThat()
                .body("message", equalTo("Email, password and name are required fields"))
                .and()
                .statusCode(SC_FORBIDDEN);
    }

    @Test
    @DisplayName("check of creating user without password")
    public void checkCreateUserWithoutPasswordTest() {
        User user = new User(RandomStringUtils.randomAlphanumeric(10) + "@yandex.ru",
                null, RandomStringUtils.randomAlphabetic(20));
        Response response = userSteps.createUser(user);
        response.then()
                .assertThat()
                .body("message", equalTo("Email, password and name are required fields"))
                .and()
                .statusCode(SC_FORBIDDEN);
    }

    @Test
    @DisplayName("check of creating user without name")
    public void checkCreateUserWithoutNameTest() {
        User user = new User(RandomStringUtils.randomAlphanumeric(10) + "@yandex.ru",
                RandomStringUtils.randomAlphanumeric(10), null);
        Response response = userSteps.createUser(user);
        response.then()
                .assertThat()
                .body("message", equalTo("Email, password and name are required fields"))
                .and()
                .statusCode(SC_FORBIDDEN);
    }
}
