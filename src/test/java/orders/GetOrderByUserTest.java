package orders;

import io.restassured.RestAssured;
import io.restassured.response.Response;
import org.apache.commons.lang3.RandomStringUtils;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import io.qameta.allure.junit4.DisplayName;
import serializer.User;
import steps.BaseUrls;
import steps.UserSteps;

import static io.restassured.RestAssured.given;
import static org.hamcrest.CoreMatchers.equalTo;
import static steps.BaseUrls.BASE_URL;

public class GetOrderByUserTest {
    User user;
    UserSteps userSteps;
    String token;

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
    @DisplayName("Get order list by authorized user")
    public void getOrderListByAuthUserTest() {
        Response response = given().log().all()
                .header("Content-Type", "application/json")
                .header("authorization", token)
                .when()
                .get(BaseUrls.ORDERS);
        response.then().log().all()
                .assertThat()
                .statusCode(200)
                .and()
                .body("success", equalTo(true));

    }

    @Test
    @DisplayName("Get order list by user without auth")
    public void getOrderListWithoutAuth() {
        Response response = given().log().all()
                .header("Content-Type", "application/json")
                .when()
                .get(BaseUrls.ORDERS);
        response.then().log().all()
                .assertThat()
                .statusCode(401)
                .and()
                .body("success", equalTo(false))
                .and().body("message", equalTo("You should be authorised"));
    }

    @After
    public void tearDown() {
        if (token != null) {
            userSteps.deleteUser(token);
        }
    }
}
