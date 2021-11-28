package com.utn.infraestructura.api.administrador;

import com.utn.casodeuso.administrador.IniciarSesionAdmin;
import com.utn.dominio.Administradores;
import com.utn.dominio.Organizaciones;
import com.utn.dominio.excepcion.UsuarioNoEncontradoException;
import com.utn.dominio.organizacion.Administrador;
import com.utn.dominio.organizacion.Organizacion;
import com.utn.infraestructura.api.SesionManager;
import com.utn.infraestructura.api.usuario.LoginResponse;
import com.utn.infraestructura.api.usuario.SolicitudIniciarSesion;
import com.utn.infraestructura.persistencia.AdministradoresEnMySQL;
import com.utn.infraestructura.persistencia.OrganizacionesEnMySQL;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;


@RestController
@CrossOrigin
public class ControladorAdministrador {

    private final Administradores administradoresEnMySQL;
    private final IniciarSesionAdmin iniciarSesion;

    public ControladorAdministrador() {
        this.administradoresEnMySQL = new AdministradoresEnMySQL();
        this.iniciarSesion = new IniciarSesionAdmin(new AdministradoresEnMySQL());
    }

    @GetMapping("organizacion/panelAdministracion")
    public ResponseEntity acceder(@RequestHeader("Authorization") String idAdmin) {
        Administrador administrador = this.obtenerUsuarioSesionManager(idAdmin);
        Organizacion organizacion = administrador.getOrganizacion();

        RespuestaAcceso unaRespuesta = new RespuestaAcceso();
        List<String> usuarioAdmins = organizacion.getAdministradores().stream().map(Administrador::getUsuario).collect(Collectors.toList());
        List<String> caracteristicas = organizacion.getCaracteristicas();
        unaRespuesta.setUsuariosAdministradores(usuarioAdmins);
        unaRespuesta.setCalidadFoto(organizacion.calidadFoto());
        unaRespuesta.setTamanioFoto(organizacion.tamañoFoto());
        unaRespuesta.setCaracteristicas(caracteristicas);

        return ResponseEntity.status(200).body(unaRespuesta);
    }

    @PostMapping("organizacion/panelAdministracion/actualizarCaracteristicas")
    public ResponseEntity actualizarCaracteristicas(@RequestHeader("Authorization") String idAdmin, @RequestBody SolicitudActualizarCaracteristicas solicitud) {
        Administrador administradorRealTime = this.obtenerUsuarioSesionManager(idAdmin);

        Administrador administrador = new AdministradoresEnMySQL().obtenerPorNombreUsuario(administradorRealTime.getUsuario());
        administrador.añadirCaracteristica(solicitud.getNuevaCaracteristica());

        administradoresEnMySQL.guardar(administrador);

        return ResponseEntity.status(200).build();
    }

    @PostMapping("organizacion/panelAdministracion/actualizarDetalleFotos")
    public ResponseEntity actualizarDetalleFotos(@RequestHeader("Authorization") String idAdmin, @RequestBody SolicitudActualizarDetalleFotos solicitud) {
        Administrador administradorRealTime = this.obtenerUsuarioSesionManager(idAdmin);

        Administrador administrador = new AdministradoresEnMySQL().obtenerPorNombreUsuario(administradorRealTime.getUsuario());
        administrador.definirTamañoFoto(solicitud.getTamanioFoto());
        administrador.definirCalidadFoto(solicitud.getCalidadFoto());

        administradoresEnMySQL.guardar(administrador);
        return ResponseEntity.status(200).build();
    }

    @PostMapping("organizacion/panelAdministracion/actualizarAdministradores")
    public ResponseEntity actualizarAdministradores(@RequestHeader("Authorization") String idAdmin, @RequestBody SolicitudActualizarAdministradores solicitud) {
        Administrador administradorRealTime = this.obtenerUsuarioSesionManager(idAdmin);

        Administrador administrador = new AdministradoresEnMySQL().obtenerPorNombreUsuario(administradorRealTime.getUsuario());
        administrador.darAltaNuevoAdministrador(solicitud.getAdminNuevo(), solicitud.getContrasenia());

        administradoresEnMySQL.guardar(administrador);
        return ResponseEntity.status(200).build();
    }

    @PostMapping("administradores/autenticar")
    public LoginResponse iniciarSesion(@RequestBody SolicitudIniciarSesion solicitud, HttpServletResponse response) {
        try {
            Administrador administrador = iniciarSesion.ejecutar(solicitud.nombreUsuario(), solicitud.contrasenia());;
            SesionManager sesionManager =  SesionManager.getInstance();
            String idSesion = sesionManager.crear("administrador",administrador);
            System.out.println(idSesion);
            return new LoginResponse(idSesion);
        }
        catch(UsuarioNoEncontradoException e) {
            return new LoginResponse("-1");
        }
    }

    private Administrador obtenerUsuarioSesionManager(String idAdmin) {
        SesionManager sesionManager = SesionManager.getInstance();
        Map<String, Object> unosDatos = sesionManager.obtenerAtributos(idAdmin);
        return (Administrador) unosDatos.get("administrador");
    }
}
