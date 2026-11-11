package com.uvg.pooproyect.dto;
public final class CuentaRequests {
    private CuentaRequests() {}
    public record CuentaRequest(Integer carnet, String clave) {}
    public record ClaveRequest(String clave) {}
}
