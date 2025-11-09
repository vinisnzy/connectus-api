package com.mindp.connectus_api.domain.scheduling.entity;

public enum AppointmentStatus {
    SCHEDULED,      // Agendado
    CONFIRMED,      // Confirmado pelo cliente
    IN_PROGRESS,    // Em andamento
    COMPLETED,      // Concluído
    CANCELED,       // Cancelado
    NO_SHOW         // Cliente não compareceu
}
