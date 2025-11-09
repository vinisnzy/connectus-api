package com.mindp.connectus_api.domain.messaging.entity;

public enum TicketStatus {
    OPEN,           // Aberto, aguardando atendimento
    IN_PROGRESS,    // Em atendimento
    PENDING,        // Em espera (pausado temporariamente)
    RESOLVED,       // Resolvido (sucesso)
    LOST,           // Resolvido (perdido/não convertido)
    CLOSED          // Fechado definitivamente
}
