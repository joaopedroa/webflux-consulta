package com.estudo.operacoes.dataprovider.dynamodb;

import com.estudo.operacoes.core.models.ConsultaRequest;
import com.estudo.operacoes.core.models.Operacao;
import com.estudo.operacoes.core.providers.OperacaoProvider;
import com.estudo.operacoes.dataprovider.dynamodb.mappers.OperacaoMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Range;
import org.springframework.data.redis.core.ReactiveRedisTemplate;
import org.springframework.stereotype.Repository;
import org.springframework.util.CollectionUtils;
import reactor.core.publisher.Mono;
import software.amazon.awssdk.services.dynamodb.DynamoDbAsyncClient;
import software.amazon.awssdk.services.dynamodb.model.AttributeValue;
import software.amazon.awssdk.services.dynamodb.model.QueryRequest;
import software.amazon.awssdk.services.dynamodb.model.QueryResponse;

import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Repository
@RequiredArgsConstructor
public class OperacaoRepository implements OperacaoProvider {

    private final DynamoDbAsyncClient dynamoDbAsyncClient;
    private final OperacaoMapper operacaoMapper;
    private final ReactiveRedisTemplate<String, Operacao> reactiveRedisTemplate;

    public Mono<List<Operacao>> buscarOperacoesPorId(ConsultaRequest request, final QueryRequest requestDynamo) {
        var requestDynamoPage = QueryRequest.builder()
                .tableName("tb_operacoes")
                .expressionAttributeValues(Map.of(":op", AttributeValue.builder().s(request.getIdOperacao()).build()))
                .keyConditionExpression("#id_operacao = :op")
                .expressionAttributeNames(Map.of("#id_operacao", "id_operacao"))
                .limit(15)
                .build();

        return reactiveRedisTemplate.opsForList().range("operacoes:" + request.getIdOperacao(), 0L, -1L)
                .doOnNext(x -> System.out.println("achhhhou" + x.getOperacao().getIdOperacao()))
                .collectList()
                .filter(x -> !CollectionUtils.isEmpty(x))
                .switchIfEmpty(
                        this.getQueryResponse(request, requestDynamoPage, new ArrayList<>())
//                                .map(x -> new ArrayList<Operacao>())
                                .map(x -> operacaoMapper.fromMap(x, request))
                                .filter(x -> !CollectionUtils.isEmpty(x))
                                .map(x -> {
                                    reactiveRedisTemplate.opsForList().leftPushAll("operacoes:" + request.getIdOperacao(), x).doOnNext(z -> {
                                                System.out.println("salvo");
                                            }).then(reactiveRedisTemplate.expire("operacoes:" + request.getIdOperacao(), Duration.ofSeconds(3)))
                                            .subscribe();
                                    return x;
                                })
                );
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
