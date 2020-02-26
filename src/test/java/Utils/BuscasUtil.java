package Utils;

import io.restassured.RestAssured;

public class BuscasUtil {

    public static  Integer getIDContaPeloNome(String nome){
        return  RestAssured.get("/contas?nome="+nome).then().extract().path("id[0]");
    }
    public static Integer getIDMovPelaDescricao(String desc){
        return  RestAssured.get("/transacoes?descricao="+desc).then().extract().path("id[0]");
    }
}
