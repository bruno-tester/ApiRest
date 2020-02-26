package restTest;

import Utils.BuscasUtil;
import org.junit.Test;
import suporte.baseTest;

import static io.restassured.RestAssured.*;
import static org.hamcrest.Matchers.*;

public class ContasTest extends baseTest {
    @Test
    public void incluirContaComSucesso(){
        given()
            .body("{\"nome\": \"Conta Nova - Teste\"}")
        .when()
            .post("/contas")
        .then()
            .statusCode(201)
            .body("nome", is("Conta Nova - Teste"))
            .body("usuario_id", is(5217))
        ;
    }
    @Test
    public void alterarContaComSucesso(){
        Integer CONTA_ID = BuscasUtil.getIDContaPeloNome("Conta para alterar");
        given()
            .body("{\"nome\": \"Conta alterada -TESTE\"}")
            .pathParam("id", CONTA_ID)
        .when()
            .put("/contas/{id}")
        .then()
            .statusCode(200)
            .body("nome", is("Conta alterada -TESTE"))
            .body("usuario_id", is(5217))
        ;
    }

    @Test
    public void TentarIncluirContaComMesmoNome(){
        given()
            .body("{\"nome\": \"Conta mesmo nome\"}")
        .when()
            .post("/contas")
        .then()
            .statusCode(400)
            .body("error", is("Já existe uma conta com esse nome!"))
        ;
    }

}

