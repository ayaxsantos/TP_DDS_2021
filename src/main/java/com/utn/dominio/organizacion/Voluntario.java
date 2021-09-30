package com.utn.dominio.organizacion;

import java.util.List;
import java.util.ArrayList;
import com.utn.dominio.autenticacion.Usuario;
import com.utn.dominio.publicacion.Publicacion;

public class Voluntario {

    private final Usuario usuario;
    private final List<Organizacion> organizaciones = new ArrayList<>();

    public Voluntario(Usuario usuario, List<Organizacion> organizaciones) {
        this.usuario = usuario;
        this.organizaciones.addAll(organizaciones);
    }

    public void aprobarPublicacion(Publicacion publicacion){
        publicacion.estaVisible(true);
    }

    public void rechazarPublicacion(Publicacion publicacion) {
        publicacion.estaVisible(false);
    }

}