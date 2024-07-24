package orders;


import io.restassured.RestAssured;
import io.restassured.response.Response;
import org.apache.commons.lang3.RandomStringUtils;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import io.qameta.allure.junit4.DisplayName;
import serializer.Ingredients;
import serializer.Order;
import serializer.User;
import steps.OrderSteps;
import steps.UserSteps;

import java.util.ArrayList;
import java.util.List;

import static org.hamcrest.CoreMatchers.equalTo;
import static steps.BaseUrls.BASE_URL;

public class CreateOrderTest {
    UserSteps userSteps;
    User user;
    String token;
    OrderSteps orderSteps;
    List<String> ingredient;
    Order order;


    @Before
    public void setUp() {
        RestAssured.baseURI = BASE_URL;
        userSteps = new UserSteps();
        orderSteps = new OrderSteps();
        user = new User(RandomStringUtils.randomAlphanumeric(10) + "@yandex.ru",
                RandomStringUtils.randomAlphanumeric(10),
                RandomStringUtils.randomAlphabetic(20));
        userSteps.createUser(user);
        token = userSteps.loginUser(user).then().extract().path("accessToken");
        ingredient = new ArrayList<>();
        Ingredients ingredients = orderSteps.getIngredient();
        ingredient.add(ingredients.getData().get(1).get_id());
        ingredient.add(ingredients.getData().get(2).get_id());
        ingredient.add(ingredients.getData().get(3).get_id());
        ingredient.add(ingredients.getData().get(4).get_id());
        ingredient.add(ingredients.getData().get(5).get_id());
        order = new Order(ingredient);
    }

    @Test
    @DisplayName("Create order with auth")
    public void createOrderWithAuthTest() {
        Response response = OrderSteps.createOrderWithAuth(order, token);
        response.then()
                .assertThat()
                .statusCode(200)
                .and()
                .body("success", equalTo(true));
    }

    @Test
    @DisplayName("Create order without auth")
    public void createOrderWithoutAuthTest() {
        Response response = OrderSteps.createOrderWithAuth(order, "");
        response.then()
                .assertThat()
                .body("success", equalTo(true))
                .and().statusCode(200);
    }

    @Test
    @DisplayName("Create order without ingredients")
    public void createOrderWithoutIngredients() {
        Order order = new Order();
        Response response = OrderSteps.createOrderWithAuth(order, token);
        orderSteps.createOrderWithoutIngredients(response);
    }

    @Test
    @DisplayName("Create order with invalid hash")
    public void createOrderWithInvalidHash() {
        Ingredients ingredients = orderSteps.getIngredient();
        ingredient.add(ingredients.getData().get(1).get_id() + "khilunjlknjkbyg9876");
        ingredient.add(ingredients.getData().get(2).get_id() + "op9iuojuigtyfjkuhh");
        Response response = orderSteps.createOrderWithAuth(order, token);
        response.then().log().all()
                .statusCode(500);
    }

    @After
    public void tearDown() {
        if (token != null) {
            userSteps.deleteUser(token);
        }
    }

}
