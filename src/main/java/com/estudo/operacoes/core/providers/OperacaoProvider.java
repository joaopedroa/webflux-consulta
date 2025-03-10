package com.estudo.operacoes.core.providers;

import com.estudo.operacoes.core.models.ConsultaRequest;
import com.estudo.operacoes.core.models.Operacao;
import com.estudo.operacoes.core.models.OperacaoTable;
import reactor.core.publisher.Mono;
import software.amazon.awssdk.services.dynamodb.model.QueryRequest;

import java.util.List;

public interface OperacaoProvider {

    Mono<List<Operacao>> buscarOperacoesPorId(ConsultaRequest request, QueryRequest requestDynamo);
    Mono<List<Operacao>> buscarOperacoesPorIdCliente(ConsultaRequest request);
//    Mono<List<Operacao>> buscarOperacoesPorIdConta(ConsultaRequest request);
}
