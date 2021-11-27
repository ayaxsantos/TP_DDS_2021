package com.utn.infraestructura.api.administrador;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

public class SolicitudActualizarAdministradores
{
    @JsonProperty
    private String adminNuevo;

    @JsonProperty
    private String contrasenia;

    public String getAdminNuevo() {
        return adminNuevo;
    }

    public void setAdminNuevo(String adminNuevo) {
        this.adminNuevo = adminNuevo;
    }

    public String getContrasenia() {
        return contrasenia;
    }

    public void setContrasenia(String contrasenia) {
        this.contrasenia = contrasenia;
    }
}
