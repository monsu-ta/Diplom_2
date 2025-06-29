package tests;

import io.qameta.allure.junit4.DisplayName;
import org.junit.After;
import org.junit.Test;
import page.AuthPage;

import static org.hamcrest.Matchers.equalTo;

public class LoginTests extends BaseTest {
    private final AuthPage authPage = new AuthPage();
    private AuthPage.UserCredentials testUserCredentials;
    private String testUserToken;

    @Test
    @DisplayName("Вход под существующим пользователем")
    public void testLoginWithValidCredentials() {
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
    }

    @Test
    @DisplayName("Вход с неверными учетными данными")
    public void testLoginWithInvalidCredentials() {
        authPage.login(new AuthPage.UserCredentials(
                        "wrong@example.com",
                        "wrongpassword",
                        null))
                .then()
                .statusCode(401)
                .body("message", equalTo("email or password are incorrect"));
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