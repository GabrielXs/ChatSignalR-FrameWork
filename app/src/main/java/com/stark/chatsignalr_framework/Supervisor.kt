package com.stark.chatsignalr_framework

class Supervisor {

    var status: StatusAtendimento = StatusAtendimento.DISPONIVEL
    var idSuporte: Int? = null
    var idConexaoChat: String? = null

    enum class StatusAtendimento(value: Int) {
        DISPONIVEL(1),
        AGUARD_CONFIRMACAO(2),
        OCUPADO(3),
        EM_ATENDIMENTO(4),
        AUSENTE(5)

    }
}