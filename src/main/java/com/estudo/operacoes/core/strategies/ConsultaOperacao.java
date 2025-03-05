package com.estudo.operacoes.core.strategies;

import com.estudo.operacoes.core.models.ConsultaRequest;
import com.estudo.operacoes.core.models.Operacao;
import com.estudo.operacoes.core.models.OperacaoTable;
import reactor.core.publisher.Mono;

import java.util.List;

public interface ConsultaOperacao {

    Mono<List<Operacao>> consultar(ConsultaRequest request);
}
