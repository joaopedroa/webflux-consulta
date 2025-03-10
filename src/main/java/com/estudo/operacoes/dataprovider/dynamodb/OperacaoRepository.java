package com.estudo.operacoes.dataprovider.dynamodb;

import com.estudo.operacoes.core.models.ConsultaRequest;
import com.estudo.operacoes.core.models.Operacao;
import com.estudo.operacoes.core.providers.OperacaoProvider;
import com.estudo.operacoes.dataprovider.dynamodb.mappers.OperacaoMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import org.springframework.util.CollectionUtils;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import software.amazon.awssdk.services.dynamodb.DynamoDbAsyncClient;
import software.amazon.awssdk.services.dynamodb.model.AttributeValue;
import software.amazon.awssdk.services.dynamodb.model.QueryRequest;
import software.amazon.awssdk.services.dynamodb.model.QueryResponse;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Repository
@RequiredArgsConstructor
public class OperacaoRepository implements OperacaoProvider {

    private final DynamoDbAsyncClient dynamoDbAsyncClient;
    private final OperacaoMapper operacaoMapper;

    public Mono<List<Operacao>> buscarOperacoesPorId(ConsultaRequest request, QueryRequest requestDynamo) {
        if (requestDynamo == null) {
            requestDynamo = QueryRequest.builder()
                    .tableName("tb_operacoes")
                    .expressionAttributeValues(Map.of(":op", AttributeValue.builder().s(request.getIdOperacao()).build()))
                    .keyConditionExpression("#id_operacao = :op")
                    .expressionAttributeNames(Map.of("#id_operacao", "id_operacao"))
                    .limit(15)
                    .build();
        }

        return getQueryResponse(request, requestDynamo, new ArrayList<>())
                .map(x -> operacaoMapper.fromMap(x, request));
    }

    private Mono<List<QueryResponse>> getQueryResponse(ConsultaRequest consultaRequest, QueryRequest requestDynamo, List<QueryResponse> responses) {

        return Mono.fromCompletionStage(dynamoDbAsyncClient.query(requestDynamo))
                .flatMap(x -> {
                    responses.add(x);
                    if (!CollectionUtils.isEmpty(x.lastEvaluatedKey())) {
                        var requestDynamoPage = QueryRequest.builder()
                                .tableName("tb_operacoes")
                                .expressionAttributeValues(Map.of(":op", AttributeValue.builder().s(consultaRequest.getIdOperacao()).build()))
                                .keyConditionExpression("#id_operacao = :op")
                                .expressionAttributeNames(Map.of("#id_operacao", "id_operacao"))
                                .exclusiveStartKey(x.lastEvaluatedKey())
                                .limit(15)
                                .build();
                        System.out.println("Paginação em andamento...");

                        return this.getQueryResponse(consultaRequest, requestDynamoPage, responses);
                    }
                    return Mono.just(responses);
                });
    }

    private Mono<QueryResponse> findPaginateOperacao(String idOperacao, Map<String, AttributeValue> exclusiveStartKey) {
        QueryRequest queryRequest = QueryRequest.builder()
                .tableName("tb_operacoes")
                .expressionAttributeValues(Map.of(":op", AttributeValue.builder().s(idOperacao).build()))
                .keyConditionExpression("#id_operacao = :op")
                .expressionAttributeNames(Map.of("#id_operacao", "id_operacao"))
                .exclusiveStartKey(exclusiveStartKey)
                .limit(5)
                .build();

        return Mono.fromCompletionStage(dynamoDbAsyncClient.query(queryRequest));
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

        return Mono.empty();

//        return Mono.fromCompletionStage(dynamoDbAsyncClient.query(queryRequest))
//                .expand(response -> {
//                    if (response.lastEvaluatedKey().size() > 0) {
//                        return findPaginateCliente(request.getIdCliente(), response.lastEvaluatedKey());
//                    }
//                    return Mono.empty();
//                })
//                .collectList()
//                .flatMapIterable(x -> operacaoMapper.fromMap(x, request))
//                .collectList();

    }

    private Mono<QueryResponse> findPaginateCliente(String idCliente, Map<String, AttributeValue> exclusiveStartKey) {
        QueryRequest queryRequest = QueryRequest.builder()
                .tableName("tb_operacoes")
                .indexName("id_cliente-dominio-index")
                .expressionAttributeValues(Map.of(":cliente", AttributeValue.builder().s(idCliente).build()))
                .keyConditionExpression("#id_cliente = :cliente")
                .expressionAttributeNames(Map.of("#id_cliente", "id_cliente"))
                .exclusiveStartKey(exclusiveStartKey)
                .build();

        return Mono.fromCompletionStage(dynamoDbAsyncClient.query(queryRequest));
    }
//
//    @Override
//    public Mono<List<Operacao>> buscarOperacoesPorIdConta(ConsultaRequest request) {
//        QueryRequest queryRequest = QueryRequest.builder()
//                .tableName("tb_operacoes")
//                .indexName("id_conta-dominio-index")
//                .expressionAttributeValues(Map.of(":conta", AttributeValue.builder().s(request.getIdConta()).build()))
//                .keyConditionExpression("#id_conta = :conta")
//                .expressionAttributeNames(Map.of("#id_conta", "id_conta"))
//                .build();
//
//        var response = dynamoDbAsyncClient.query(queryRequest);
//
//        return Mono.fromCompletionStage(response)
//                .map(x -> operacaoMapper.fromMap(x, request));
//    }


}
