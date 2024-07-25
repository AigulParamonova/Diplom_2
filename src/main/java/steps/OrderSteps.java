package steps;

import io.qameta.allure.Step;
import io.qameta.allure.restassured.AllureRestAssured;
import io.restassured.response.Response;
import org.hamcrest.Matchers;
import serializer.Ingredients;
import serializer.Order;

import static io.restassured.RestAssured.given;

public class OrderSteps {
    @Step("Get ingredients")
    public Ingredients getIngredient() {
        return given()
                .header("Content-Type", "application/json")
                .log().all()
                .get("/api/ingredients")
                .body()
                .as(Ingredients.class);
    }

    @Step("Create order with auth")
    public static Response createOrderWithAuth(Order order, String token) {
        return given().log().all().filter(new AllureRestAssured())
                .header("Content-Type", "application/json")
                .header("authorization", token)
                .body(order)
                .when()
                .post("/api/orders");
    }

    @Step("Create order without ingredients")
    public void createOrderWithoutIngredients(Response response) {
        response.then().log().all()
                .assertThat()
                .statusCode(400)
                .and().body("success", Matchers.is(false))
                .and().body("message", Matchers.is("Ingredient ids must be provided"));
    }
}
