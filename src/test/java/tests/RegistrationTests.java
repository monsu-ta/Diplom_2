package tests;

import io.qameta.allure.junit4.DisplayName;
import org.junit.After;
import org.junit.Test;
import page.AuthPage;

import static org.hamcrest.Matchers.equalTo;

public class RegistrationTests extends BaseTest {
    private final AuthPage authPage = new AuthPage();
    private String authToken;
    private String testEmail;

    @Test
    @DisplayName("Регистрация нового пользователя")
    public void testSuccessfulRegistration() {
        testEmail = "test" + System.currentTimeMillis() + "@example.com";
        authToken = authPage.register(new AuthPage.UserCredentials(
                        testEmail,
                        "password123",
                        "Test User"))
                .then()
                .statusCode(200)
                .body("success", equalTo(true))
                .extract()
                .path("accessToken");
    }

    @Test
    @DisplayName("Регистрация уже существующего пользователя")
    public void testRegisterExistingUser() {
        authPage.register(new AuthPage.UserCredentials(
                        "test-data@yandex.ru",
                        "password",
                        "Existing User"))
                .then()
                .statusCode(403)
                .body("message", equalTo("User already exists"));
    }

    @Test
    @DisplayName("Регистрация без обязательного поля")
    public void testRegisterWithoutRequiredField() {
        authPage.register(new AuthPage.UserCredentials(
                        "test@example.com",
                        "password",
                        null))
                .then()
                .statusCode(403)
                .body("message", equalTo("Email, password and name are required fields"));
    }

    @After
    public void tearDown() {
        if (authToken != null) {
            authPage.deleteUser(authToken)
                    .then()
                    .statusCode(202);
        }
    }
}