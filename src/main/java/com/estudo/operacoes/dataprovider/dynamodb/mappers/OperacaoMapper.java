package com.estudo.operacoes.dataprovider.dynamodb.mappers;

import com.estudo.operacoes.core.enums.Dominio;
import com.estudo.operacoes.core.models.ConsultaRequest;
import com.estudo.operacoes.core.models.Operacao;
import com.estudo.operacoes.core.models.OperacaoMetadata;
import com.estudo.operacoes.core.models.OperacaoTable;
import com.estudo.operacoes.dataprovider.dynamodb.mappers.dominios.DadosDominioMapper;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import software.amazon.awssdk.services.dynamodb.model.AttributeValue;
import software.amazon.awssdk.services.dynamodb.model.QueryResponse;

import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class OperacaoMapper {

    private final DadosDominioMapper dadosDominioMapper;

    public List<Operacao> fromMap(QueryResponse response, ConsultaRequest request) {
        return response.items().stream()
                .map(item -> new OperacaoTable(
                        Long.valueOf(item.get("id_operacao").s()),
                        Optional.ofNullable(item.get("id_cliente")).map(AttributeValue::s).orElse(""),
                        Optional.ofNullable(item.get("id_conta")).map(AttributeValue::s).orElse(""),
                        item.get("dominio").s(),
                        item.get("dados_dominio").s())
                ).collect(Collectors.groupingBy(OperacaoTable::getIdOperacao))
                .values().stream()
                .map(this::tratar)
                .map(operacao -> this.tratarErroDominioNaoEncontrado(operacao, request.getExpand()))
                .collect(Collectors.toList());
    }

    private Operacao tratarErroDominioNaoEncontrado(Operacao operacao, List<Dominio> dominiosSolicitados) {

        dominiosSolicitados.forEach(dominio -> {
            try {
                if (dominio.isObrigatorioPossuirDados()) {
                    var field = operacao.getClass().getDeclaredField(dominio.getNomeCampo());
                    field.setAccessible(true);
                    var value = field.get(operacao);
                    if (Objects.isNull(value)) {
                        operacao.getStatus().add(Operacao.StatusData.builder()
                                .mensagem("Domínio não encontrado no DynamoDB")
                                .path("/".concat(dominio.getNomeCampo()))
                                .statusCode(404L)
                                .build());
                    }
                }
            } catch (NoSuchFieldException | IllegalAccessException exception) {
                exception.printStackTrace();
            }
        });

        return operacao;
    }


    private Operacao tratar(List<OperacaoTable> operacoes) {
        final Operacao operacao = new Operacao();
        operacoes.forEach(operacaoTable -> {
            switch (operacaoTable.getDominio()) {
                case "OPERACAO" ->
                        operacao.setOperacao(dadosDominioMapper.converterOperacaoData(operacaoTable.getDadosDominio()));
                case "PARCELA" ->
                        operacao.setParcelas(dadosDominioMapper.converterParcelaData(operacaoTable.getDadosDominio()));
                default -> System.out.println("Domínio não encontrado");
            }
        });

        return operacao;
    }
}
