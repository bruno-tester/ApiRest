package restTest;

import Forms.MovimentacaoForm;
import Utils.BuscasUtil;
import Utils.DataUtil;
import org.junit.Test;
import suporte.baseTest;

import static io.restassured.RestAssured.*;
import static org.hamcrest.Matchers.*;
import static org.hamcrest.Matchers.hasItems;

public class MovimentacaoTest extends baseTest {
    @Test
    public void incluirMovimentacaoComSucesso(){
        MovimentacaoForm movForm = getMovForm();
        given()
            .body(movForm)
        .when()
            .post("/transacoes")
        .then()
            .statusCode(201)
            .body("id", notNullValue())
        ;
    }

    @Test
    public void validarCamposObrigatoriosAoInserirMovimentacao(){
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
    public void incluirMovimentacaoComDataFutura(){
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
    public void naoDeveRemoverContaComMovimentacao(){
        Integer CONTA_ID = BuscasUtil.getIDContaPeloNome("Conta com movimentacao");
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
    public void deveRemoverMovimentacao(){
        Integer MOV_ID = BuscasUtil.getIDMovPelaDescricao("Movimentacao para exclusao");
        given()
            .pathParam("id", MOV_ID)
        .when()
            .delete("/transacoes/{id}")
        .then()
            .statusCode(204)
        ;
    }
    public MovimentacaoForm getMovForm() {
        MovimentacaoForm movForm = new MovimentacaoForm();
        movForm.setConta_id(BuscasUtil.getIDContaPeloNome("Conta para movimentacoes"));
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

