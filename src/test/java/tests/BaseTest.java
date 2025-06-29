package tests;

import io.restassured.RestAssured;
import org.junit.Assume;
import org.junit.Before;
import page.AuthPage;
import page.OrderPage;

import java.util.List;

import static io.restassured.RestAssured.given;

public class BaseTest {
    @Before
    public void setUp() {
        RestAssured.baseURI = "https://stellarburgers.nomoreparties.site/api";
        RestAssured.enableLoggingOfRequestAndResponseIfValidationFails();
    }

    protected String getAuthToken() {
        return new AuthPage().login(new AuthPage.UserCredentials(
                        "test-data@yandex.ru",
                        "password",
                        null))
                .then()
                .extract()
                .path("accessToken");
    }

    protected List<String> getAvailableIngredients() {
        List<String> ingredients = new OrderPage().getIngredients();
        Assume.assumeFalse("Нет доступных ингредиентов", ingredients.isEmpty());
        return ingredients;
    }
}