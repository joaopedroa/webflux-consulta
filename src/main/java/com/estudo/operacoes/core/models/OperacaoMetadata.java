package com.estudo.operacoes.core.models;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
@AllArgsConstructor
@NoArgsConstructor
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public class OperacaoMetadata {

    private Operacao operacaoCredito;
    private Metadata metadata;

    public void setDominioOperacao(Operacao.OperacaoData operacao){
        this.operacaoCredito.setOperacao(operacao);
        this.metadata.setDominioOperacaoValido(true);
    }

    public void setDominioParcela(List<Operacao.ParcelaData> parcelas){
        this.operacaoCredito.setParcelas(parcelas);
        this.metadata.setDominioParcelaValido(true);
    }

    @Data
    @JsonIgnoreProperties(ignoreUnknown = true)
    @AllArgsConstructor
    @NoArgsConstructor
    @JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
    public static class Metadata {
        private boolean dominioOperacaoValido;
        private boolean dominioParcelaValido;
    }
}
