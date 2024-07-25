package users;

import io.qameta.allure.junit4.DisplayName;
import io.restassured.RestAssured;
import io.restassured.response.Response;
import org.apache.commons.lang3.RandomStringUtils;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import serializer.User;
import steps.UserSteps;

import static org.apache.http.HttpStatus.SC_OK;
import static org.apache.http.HttpStatus.SC_UNAUTHORIZED;
import static org.hamcrest.CoreMatchers.equalTo;
import static steps.BaseUrls.BASE_URL;

public class UpdateUserInfoTest {
    String token;
    User user;
    UserSteps userSteps;
    User newUser = new User();

    @Before
    public void setUp() {
        RestAssured.baseURI = BASE_URL;
        userSteps = new UserSteps();
        user = new User(RandomStringUtils.randomAlphanumeric(10) + "@yandex.ru",
                RandomStringUtils.randomAlphanumeric(10),
                RandomStringUtils.randomAlphabetic(20));
        userSteps.createUser(user);
        token = userSteps.loginUser(user).then().extract().path("accessToken");

    }

    @Test
    @DisplayName("Changing user email with authorization")
    public void updateUserEmailWithAuth() {
        newUser.setEmail("n123@setinbox.com");
        user.setEmail("n123@setinbox.com");
        Response response = userSteps.changeUserInfo(newUser, token);
        response
                .then()
                .assertThat()
                .statusCode(SC_OK)
                .and()
                .body("success", equalTo(true))
                .body("user.email", equalTo(newUser.getEmail()));
    }

    @Test
    @DisplayName("Changing user password with authorization")
    public void updateUserPasswordWithAuth() {
        newUser.setPassword("password");
        user.setPassword("password");
        Response response = userSteps.changeUserInfo(newUser, token);
        response
                .then()
                .assertThat()
                .statusCode(SC_OK)
                .and()
                .body("success", equalTo(true));
        userSteps.loginUser(newUser);

    }

    @Test
    @DisplayName("Changing user name with authorization")
    public void updateUserNameWithAuth() {
        newUser.setName("NewName");
        user.setName("NewName");
        Response response = userSteps.changeUserInfo(newUser, token);
        response
                .then()
                .assertThat()
                .statusCode(SC_OK)
                .and()
                .body("success", equalTo(true))
                .body("user.name", equalTo(newUser.getName()));
    }

    @Test
    @DisplayName("Changing user email without authorization")
    public void updateUserEmailWithoutAuth() {
        Response response = userSteps.changeUserInfo(user, "");
        response.then()
                .assertThat()
                .statusCode(SC_UNAUTHORIZED)
                .and()
                .body("success", equalTo(false))
                .body("message", equalTo("You should be authorised"));
    }

    @After
    public void teardown() {
        if (token != null) {
            userSteps.deleteUser(token);
        }
    }
}
