package restTest.suite;

import org.junit.BeforeClass;
import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import restTest.AuthTest;
import restTest.ContasTest;
import restTest.MovimentacaoTest;
import restTest.SaldoTest;
import suporte.baseTest;

import java.util.HashMap;
import java.util.Map;

import static io.restassured.RestAssured.*;

@RunWith(Suite.class)
@Suite.SuiteClasses({
        ContasTest.class,
        MovimentacaoTest.class,
        SaldoTest.class,
        AuthTest.class
})
public class SuiteTest extends baseTest {
    @BeforeClass
    public static void login(){
        Map<String,String> login = new HashMap<String, String>();
        login.put("email","bruno.maxado97@hotmail.com");
        login.put("senha","teste.123##");

        String TOKEN = given()
            .body(login)
        .when()
            .post("/signin")
        .then()
            .statusCode(200)
            .extract().path("token");

        requestSpecification.header("Authorization", "JWT "+ TOKEN);
        get("/reset").then().statusCode(200);
    }
}
