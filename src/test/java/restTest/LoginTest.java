package restTest;

import Forms.MovimentacaoForm;
import Utils.DataUtil;
import io.restassured.RestAssured;
import io.restassured.specification.FilterableRequestSpecification;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runners.MethodSorters;
import suporte.baseTest;

import java.util.HashMap;
import java.util.Map;

import static io.restassured.RestAssured.*;
import static org.hamcrest.Matchers.*;

@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class LoginTest extends baseTest {
    private static String CONTA_NAME = "Conta " + System.nanoTime();
    private static Integer CONTA_ID;
    private static Integer MOV_ID;


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
    }

    @Test
    public void t02_IncluirContaComSucesso(){
        CONTA_ID = given()
            .body("{\"nome\": \""+CONTA_NAME+"\"}")
        .when()
            .post("/contas")
        .then()
            .statusCode(201)
            .body("nome", is(CONTA_NAME))
            .body("usuario_id", is(5217))
            .extract().path("id")
            ;
    }

    @Test
    public void t03_TentarIncluirContaComMesmoNome(){
        given()
           .body("{\"nome\": \""+CONTA_NAME+"\"}")
        .when()
            .post("/contas")
        .then()
            .statusCode(400)
            .body("error", is("Já existe uma conta com esse nome!"))
        ;
    }

    @Test
    public void t04_AlterarContaComSucesso(){
        given()
            .body("{\"nome\": \""+CONTA_NAME+" alterada\"}")
            .pathParam("id", CONTA_ID)
        .when()
            .put("/contas/{id}")
        .then()
            .statusCode(200)
            .body("nome", is(CONTA_NAME+" alterada"))
            .body("usuario_id", is(5217))
        ;
    }

    @Test
    public void t05_IncluirMovimentacaoComSucesso(){
        MovimentacaoForm movForm = getMovForm();
        MOV_ID = given()
            .body(movForm)
        .when()
            .post("/transacoes")
        .then()
            .statusCode(201)
            .body("id", notNullValue())
            .extract().path("id")
        ;
    }

    @Test
    public void t06_ValidarCamposObrigatoriosAoInserirMovimentacao(){
        given()
            .body("{}")
        .when()
            .post("/transacoes")
        .then()
            .statusCode(400)
            .body("$", hasSize(8))
            .body("msg", hasItems(
                    "Data da Movimentação é obrigatório",
                    "Data do pagamento é obrigatório",
                    "Descrição é obrigatório",
                    "Interessado é obrigatório",
                    "Valor é obrigatório",
                    "Valor deve ser um número",
                    "Conta é obrigatório",
                    "Situação é obrigatório"
            ))
        ;
    }
    @Test
    public void t07_IncluirMovimentacaoComDataFutura(){
        MovimentacaoForm movForm = getMovForm();
        movForm.setData_transacao(DataUtil.getDataDiferencaDias(2));
        given()
            .body(movForm)
        .when()
            .post("/transacoes")
        .then()
            .statusCode(400)
            .body("msg", hasSize(1))
            .body("msg", hasItem("Data da Movimentação deve ser menor ou igual à data atual"))
        ;
    }

    @Test
    public void t08_NaoDeveRemoverContaComMovimentacao(){

        given()
            .pathParam("id", CONTA_ID)
        .when()
            .delete("/contas/{id}")
        .then()
            .statusCode(500)
            .body("constraint", is("transacoes_conta_id_foreign"))
        ;
    }

    @Test
    public void t09_DeveCalcularSaldoContas(){

        given()
        .when()
            .get("/saldo")
        .then()
            .statusCode(200)
            .body("find{it.conta_id == "+CONTA_ID+"}.saldo", is("1509.99"))
        ;
    }
    @Test
    public void t10_DeveRemoverMovimentacao(){

        given()
            .pathParam("id", MOV_ID)
        .when()
            .delete("/transacoes/{id}")
        .then()
            .statusCode(204)
        ;
    }
    @Test
    public void t11_AcessarRecursoSemToken(){
        FilterableRequestSpecification req = (FilterableRequestSpecification) requestSpecification;
        req.removeHeader("Authorization");

        given()
        .when()
            .get("/contas")
        .then()
            .statusCode(401)
        ;
    }
    public MovimentacaoForm getMovForm() {
        MovimentacaoForm movForm = new MovimentacaoForm();
        movForm.setConta_id(CONTA_ID);
        movForm.setUsuario_id(5217);
        movForm.setDescricao("Descrição da Movimentação - Teste");
        movForm.setEnvolvido("Envolvido na Mov.");
        movForm.setTipo("REC");
        movForm.setData_transacao(DataUtil.getDataDiferencaDias(-1));
        movForm.setData_pagamento(DataUtil.getDataDiferencaDias(30));
        movForm.setValor(1509.99f);
        movForm.setStatus(true);
        return  movForm;
    }
}

