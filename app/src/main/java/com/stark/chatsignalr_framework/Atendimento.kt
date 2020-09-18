package com.stark.chatsignalr_framework

class Atendimento {
    var protocolo: String? = null
    var mensagens: MutableList<Mensagem> = mutableListOf()
    var suporte : Supervisor?= null
    var cliente: Cliente? = null


    enum class StatusEnvio(private val value: Int) {
        ENVIANDO(0),
        ENVIADO(1),
        ERRO(2);

        fun toInt() = value
    }

    class Mensagem {
        var mensagem: String? = null
        var dataHora: String? = null
        var enviadoPeloSuporte: Boolean = false
        var statusEnvio: StatusEnvio = StatusEnvio.ERRO
    }

    class Cliente(
        var idCliente: Int? = null,
        var cpf_cnpj: String? = null,
        var email: String? = null,
        var telefone: String? = null,
        var nome: String? = null,
        var idConexaoChat: String? = null
    )
}