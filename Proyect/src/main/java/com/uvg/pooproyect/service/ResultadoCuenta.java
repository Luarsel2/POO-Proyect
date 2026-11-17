package com.uvg.pooproyect.service;
public record ResultadoCuenta(Estado estado, String mensaje) {
    public enum Estado { OK, BAD_REQUEST, NOT_FOUND, CONFLICT }
}
