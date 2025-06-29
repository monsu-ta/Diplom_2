package page;

import io.qameta.allure.Step;
import io.restassured.response.Response;

import static io.restassured.RestAssured.given;

public class AuthPage {

    @Step("Авторизация пользователя")
    public Response login(UserCredentials credentials) {
        return given()
                .header("Content-type", "application/json")
                .body(credentials)
                .when()
                .post("/auth/login");
    }

    @Step("Регистрация нового пользователя")
    public Response register(UserCredentials credentials) {
        return given()
                .header("Content-type", "application/json")
                .body(credentials)
                .when()
                .post("/auth/register");
    }

    @Step("Удаление пользователя")
    public Response deleteUser(String authToken) {
        return given()
                .header("Authorization", authToken)
                .when()
                .delete("/auth/user");
    }

    public static class UserCredentials {
        public String email;
        public String password;
        public String name;

        public UserCredentials(String email, String password, String name) {
            this.email = email;
            this.password = password;
            this.name = name;
        }
    }
}