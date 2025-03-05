package com.estudo.operacoes.entrypoint.resourcers;

import com.estudo.operacoes.core.enums.TipoConsulta;
import com.estudo.operacoes.core.models.ConsultaRequest;
import com.estudo.operacoes.core.models.Operacao;
import com.estudo.operacoes.core.models.OperacaoTable;
import com.estudo.operacoes.core.usecases.OperacaoUseCase;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

import java.util.List;

@RestController
@RequestMapping("/operacoes")
@RequiredArgsConstructor
public class OperacaoResource {

    private final OperacaoUseCase operacaoUseCase;

    @GetMapping()
    public Mono<List<Operacao>> buscarOperacoes(
            @RequestParam(value = "idOperacao", required = false) String idOperacao,
            @RequestParam(value = "idCliente", required = false) String idCliente,
            @RequestParam(value = "idConta", required = false) String idConta,
            @RequestParam(value = "expand", required = false) List<String> expand,
            @RequestHeader("tipoConsulta") TipoConsulta tipoConsulta){
        var request = new ConsultaRequest(tipoConsulta, idOperacao, idCliente, idConta, expand);
        return operacaoUseCase.buscarOperacao(request);
    }

}
