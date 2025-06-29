package page;

import io.qameta.allure.Step;
import io.restassured.response.Response;

import java.util.List;

import static io.restassured.RestAssured.given;

public class OrderPage {

    @Step("Получение списка ингредиентов")
    public List<String> getIngredients() {
        return given()
                .get("/ingredients")
                .then()
                .statusCode(200)
                .extract()
                .jsonPath()
                .getList("data._id");
    }

    @Step("Создание заказа с ингредиентами")
    public Response createOrder(List<String> ingredients, String authToken) {
        String orderBody = String.format("{\"ingredients\": [\"%s\",\"%s\"]}",
                ingredients.get(0), ingredients.get(1));

        return given()
                .header("Content-type", "application/json")
                .header("Authorization", authToken)
                .body(orderBody)
                .when()
                .post("/orders");
    }

    @Step("Создание заказа")
    public Response createOrderWithoutAuth(List<String> ingredients) {
        String orderBody = String.format("{\"ingredients\": [\"%s\"]}", ingredients.get(0));
        return given()
                .header("Content-type", "application/json")
                .body(orderBody)
                .when()
                .post("/orders");
    }

    @Step("Создание заказа с неверным хешем ингредиента")
    public Response createOrderWithInvalidIngredient() {
        return given()
                .header("Content-type", "application/json")
                .body("{\"ingredients\": [\"invalid_hash\"]}")
                .when()
                .post("/orders");
    }
}