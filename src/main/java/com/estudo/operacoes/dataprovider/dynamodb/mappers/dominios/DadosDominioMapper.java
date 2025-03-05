package com.estudo.operacoes.dataprovider.dynamodb.mappers.dominios;

import com.estudo.operacoes.core.models.Operacao;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class DadosDominioMapper {


    private final ObjectMapper objectMapper;

    public Operacao.OperacaoData converterOperacaoData(String input) {
        try {
            return objectMapper.readValue(input, new TypeReference<>() {
            });
        } catch (Exception ex) {
            ex.printStackTrace();
            return null;
        }
    }

    public List<Operacao.ParcelaData> converterParcelaData(String input) {
        try {
            return objectMapper.readValue(input, new TypeReference<>() {
            });
        } catch (Exception ex) {
            ex.printStackTrace();
            return null;
        }
    }

}
