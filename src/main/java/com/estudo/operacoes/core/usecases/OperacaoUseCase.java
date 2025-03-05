package com.estudo.operacoes.core.usecases;

import com.estudo.operacoes.core.models.ConsultaRequest;
import com.estudo.operacoes.core.models.Operacao;
import com.estudo.operacoes.core.models.OperacaoTable;
import com.estudo.operacoes.core.providers.OperacaoProvider;
import com.estudo.operacoes.core.strategies.ConsultaOperacao;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.server.ServerRequest;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class OperacaoUseCase {

    private final Map<String, ConsultaOperacao> consultaOperacaoStrategies;

    public Mono<List<Operacao>> buscarOperacao(ConsultaRequest request) {
        return consultaOperacaoStrategies.get(request.getTipoConsulta().getEstrategiaConsulta()).consultar(request);
    }
}
