package com.estudo.operacoes.core.models;

import com.estudo.operacoes.core.enums.Dominio;
import com.estudo.operacoes.core.enums.TipoConsulta;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.*;
import java.util.stream.Collectors;

@Data
@AllArgsConstructor
public class ConsultaRequest {

    private TipoConsulta tipoConsulta;
    private String idOperacao;
    private String idCliente;
    private String idConta;
    private List<String> expand;

    public List<Dominio> getExpand() {
        Set<Dominio> dominiosBasicosObrigatorios = new HashSet<>(List.of(Dominio.OPERACAO));
        dominiosBasicosObrigatorios.addAll(this.buscarExand());
        return new ArrayList<>(dominiosBasicosObrigatorios);
    }

    private List<Dominio> buscarExand() {
        return Optional.ofNullable(this.expand)
                .map(x -> x.stream().map(Dominio::fromNome).collect(Collectors.toList()))
                .orElse(Collections.emptyList());
    }
}
