package com.estudo.operacoes.core.models;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class OperacaoTable {

    private Long idOperacao;
    private String idCliente;
    private String idConta;
    private String dominio;
    private String dadosDominio;

    public String getDominioConverter() {
        return "CONVERTE_" .concat(this.dominio);
    }


}
