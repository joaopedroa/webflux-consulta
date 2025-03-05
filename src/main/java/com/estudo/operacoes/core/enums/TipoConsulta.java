package com.estudo.operacoes.core.enums;

public enum TipoConsulta {
    OPERACAO,
    CLIENTE,
    CONTA;

    public String getEstrategiaConsulta(){
        return "CONSULTA_".concat(this.name());
    }
}
