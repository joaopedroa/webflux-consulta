package com.estudo.operacoes.core.strategies;

import com.estudo.operacoes.core.models.ConsultaRequest;
import com.estudo.operacoes.core.models.Operacao;
import com.estudo.operacoes.core.models.OperacaoTable;
import com.estudo.operacoes.core.providers.OperacaoProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.util.List;

@Service("CONSULTA_CLIENTE")
@RequiredArgsConstructor
public class ConsultaClienteStrategy implements ConsultaOperacao{

    private final OperacaoProvider operacaoProvider;

    @Override
    public Mono<List<Operacao>> consultar(ConsultaRequest request) {
       return operacaoProvider.buscarOperacoesPorIdCliente(request);


    }
}
