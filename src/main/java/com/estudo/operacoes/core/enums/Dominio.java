package com.estudo.operacoes.core.enums;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Arrays;

@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Getter
public enum Dominio {

    OPERACAO("operacao", "operacao", true),
    PARCELA("parcela", "parcelas", true);

    private final String nome;
    private final String nomeCampo;
    private final boolean obrigatorioPossuirDados;

    public static Dominio fromNome(String nome) {
        return Arrays.stream(values())
                .filter(x -> x.getNome().equals(nome))
                .findFirst()
                .orElseThrow(() -> new RuntimeException("Domínio não encontrado"));
    }
}
