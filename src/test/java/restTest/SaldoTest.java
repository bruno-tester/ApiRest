package restTest;

import Forms.MovimentacaoForm;
import Utils.BuscasUtil;
import Utils.DataUtil;
import io.restassured.RestAssured;
import org.junit.BeforeClass;
import org.junit.Test;
import suporte.baseTest;

import java.util.HashMap;
import java.util.Map;

import static io.restassured.RestAssured.*;
import static org.hamcrest.Matchers.*;

public class SaldoTest extends baseTest {
    @Test
    public void deveCalcularSaldoContas(){
        Integer CONTA_ID = BuscasUtil.getIDContaPeloNome("Conta para saldo");
        given()
        .when()
            .get("/saldo")
        .then()
            .statusCode(200)
            .body("find{it.conta_id == "+CONTA_ID+"}.saldo", is("534.00"))
        ;
    }

}

