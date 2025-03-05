package com.estudo.operacoes.dataprovider.dynamodb;

import com.estudo.operacoes.core.models.ConsultaRequest;
import com.estudo.operacoes.core.models.Operacao;
import com.estudo.operacoes.core.models.OperacaoTable;
import com.estudo.operacoes.core.providers.OperacaoProvider;
import com.estudo.operacoes.dataprovider.dynamodb.mappers.OperacaoMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Mono;
import software.amazon.awssdk.services.dynamodb.DynamoDbAsyncClient;
import software.amazon.awssdk.services.dynamodb.model.*;

import java.util.List;
import java.util.Map;

@Repository
@RequiredArgsConstructor
public class OperacaoRepository implements OperacaoProvider {

    private final DynamoDbAsyncClient dynamoDbAsyncClient;
    private final OperacaoMapper operacaoMapper;

    public Mono<List<Operacao>> buscarOperacoesPorId(ConsultaRequest request) {

        QueryRequest queryRequest = QueryRequest.builder()
                .tableName("tb_operacoes")
                .expressionAttributeValues(Map.of(":op", AttributeValue.builder().s(request.getIdOperacao()).build()))
                .keyConditionExpression("#id_operacao = :op")
                .expressionAttributeNames(Map.of("#id_operacao", "id_operacao"))
                .build();

        var response = dynamoDbAsyncClient.query(queryRequest);

        return Mono.fromCompletionStage(response)
                .map(x -> operacaoMapper.fromMap(x, request));
    }

    @Override
    public Mono<List<Operacao>> buscarOperacoesPorIdCliente(ConsultaRequest request) {
        QueryRequest queryRequest = QueryRequest.builder()
                .tableName("tb_operacoes")
                .indexName("id_cliente-dominio-index")
                .expressionAttributeValues(Map.of(":cliente", AttributeValue.builder().s(request.getIdCliente()).build()))
                .keyConditionExpression("#id_cliente = :cliente")
                .expressionAttributeNames(Map.of("#id_cliente", "id_cliente"))
                .build();

        var response = dynamoDbAsyncClient.query(queryRequest);

        return Mono.fromCompletionStage(response)
                .map(x -> operacaoMapper.fromMap(x, request));
    }

    @Override
    public Mono<List<Operacao>> buscarOperacoesPorIdConta(ConsultaRequest request) {
        QueryRequest queryRequest = QueryRequest.builder()
                .tableName("tb_operacoes")
                .indexName("id_conta-dominio-index")
                .expressionAttributeValues(Map.of(":conta", AttributeValue.builder().s(request.getIdConta()).build()))
                .keyConditionExpression("#id_conta = :conta")
                .expressionAttributeNames(Map.of("#id_conta", "id_conta"))
                .build();

        var response = dynamoDbAsyncClient.query(queryRequest);

        return Mono.fromCompletionStage(response)
                .map(x -> operacaoMapper.fromMap(x, request));
    }


}
