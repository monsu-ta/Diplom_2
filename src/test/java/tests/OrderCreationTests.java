package tests;

import io.qameta.allure.junit4.DisplayName;
import org.junit.After;
import org.junit.Test;
import org.junit.Assume;
import page.AuthPage;
import page.OrderPage;

import java.util.List;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.equalTo;

public class OrderCreationTests extends BaseTest {
    private final AuthPage authPage = new AuthPage();
    private final OrderPage orderPage = new OrderPage();

    private AuthPage.UserCredentials testUserCredentials;
    private String testUserToken;

    @Test
    @DisplayName("Создание авторизированного заказа с ингредиентами")
    public void testCreateOrderWithAuthAndIngredients() {

        String email = "test-user-" + System.currentTimeMillis() + "@yandex.ru";
        String password = "password";
        String name = "Test User";

        testUserCredentials = new AuthPage.UserCredentials(email, password, name);

        authPage.register(testUserCredentials)
                .then()
                .statusCode(200)
                .body("success", equalTo(true));

        testUserToken = authPage.login(testUserCredentials)
                .then()
                .statusCode(200)
                .body("success", equalTo(true))
                .extract()
                .path("accessToken");

        List<String> ingredients = getAvailableIngredients();

        orderPage.createOrder(ingredients, testUserToken)
                .then()
                .statusCode(200)
                .body("success", equalTo(true));
    }

    @Test
    @DisplayName("Создание заказа без авторизации")
    public void testCreateOrderWithoutAuth() {
        List<String> ingredients = orderPage.getIngredients();
        Assume.assumeFalse("Нет доступных ингредиентов", ingredients.isEmpty());

        orderPage.createOrderWithoutAuth(ingredients)
                .then()
                .statusCode(401)
                .body("message", equalTo("You should be authorised"));
    }

    @Test
    @DisplayName("Создание заказа без ингредиентов")
    public void testCreateOrderWithoutIngredients() {
        given()
                .header("Content-type", "application/json")
                .body("{}")
                .when()
                .post("/orders")
                .then()
                .statusCode(400)
                .body("message", equalTo("Ingredient ids must be provided"));
    }

    @Test
    @DisplayName("Создание заказа с неверным хешем ингредиента")
    public void testCreateOrderWithInvalidIngredient() {
        orderPage.createOrderWithInvalidIngredient()
                .then()
                .statusCode(500);
    }

    @After
    public void tearDown() {
        if (testUserCredentials != null && testUserToken != null) {
            authPage.deleteUser(testUserToken)
                    .then()
                    .statusCode(202);
        }
    }
}