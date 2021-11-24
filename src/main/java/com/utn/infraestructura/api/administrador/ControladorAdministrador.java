package com.utn.infraestructura.api.administrador;

import com.utn.casodeuso.administrador.AccederAdministrador;
import com.utn.casodeuso.administrador.ActualizarCaracteristicas;
import com.utn.casodeuso.organizacion.ObtenerTodasLasOrganizaciones;
import com.utn.dominio.Administradores;
import com.utn.dominio.Organizaciones;
import com.utn.dominio.autenticacion.Usuario;
import com.utn.dominio.excepcion.NoEsAdministradorDeLaOrganizacionException;
import com.utn.dominio.excepcion.UsuarioNoEncontradoException;
import com.utn.dominio.foto.CalidadFoto;
import com.utn.dominio.foto.TamañoFoto;
import com.utn.dominio.organizacion.Organizacion;
import com.utn.infraestructura.api.SesionManager;
import com.utn.infraestructura.persistencia.AdministradoresEnMySQL;
import com.utn.infraestructura.persistencia.OrganizacionesEnMySQL;
import org.springframework.web.bind.annotation.*;

import org.springframework.http.ResponseEntity;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;


@RestController
@CrossOrigin
public class ControladorAdministrador
{
    private final AccederAdministrador accederAdministrador;
    private final ActualizarCaracteristicas actualizarCaracteristicas;
    private final ObtenerTodasLasOrganizaciones obtenerOrganizaciones;

    public ControladorAdministrador() {
        Organizaciones organizacionesEnMySQL = new OrganizacionesEnMySQL();
        Administradores administradoresEnMySQL = new AdministradoresEnMySQL();

        this.actualizarCaracteristicas = new ActualizarCaracteristicas(administradoresEnMySQL,organizacionesEnMySQL);
        this.obtenerOrganizaciones = new ObtenerTodasLasOrganizaciones(organizacionesEnMySQL);
        this.accederAdministrador = new AccederAdministrador(administradoresEnMySQL,organizacionesEnMySQL);
    }

    @GetMapping("organizacion/{nombreOrganizacion}/panelAdministracion")
    public ResponseEntity acceder(@PathVariable("nombreOrganizacion") String nombreOrganizacion,
                                  @RequestHeader("Authorization") String idSesion)
    {
        try {

            Usuario unUsuario = this.obtenerUsuarioSesionManager(idSesion);

            Organizacion unaOrganizacion = accederAdministrador.ejecutar(unUsuario.nombreUsuario(), nombreOrganizacion);

            CalidadFoto unaCalidad = unaOrganizacion.calidadFoto();
            TamañoFoto unTamaño = unaOrganizacion.tamañoFoto();

            List<String> unosUsuariosAdmins = unaOrganizacion.getAdministradores().stream().map(
                    unAdmin -> unAdmin.getUsuario().nombreUsuario()
            ).collect(Collectors.toList());

            List<String> unasCaracteristicas = unaOrganizacion.getCaracteristicas();

            RespuestaAcceso unaRespuesta = new RespuestaAcceso();
            unaRespuesta.setUsuariosAdministradores(unosUsuariosAdmins);
            unaRespuesta.setNombreUsuario(unUsuario.nombreUsuario());
            unaRespuesta.setCalidadFoto(unaCalidad);
            unaRespuesta.setTamañoFoto(unTamaño);
            unaRespuesta.setCaracteristicas(unasCaracteristicas);

            return ResponseEntity.status(200).body(unaRespuesta);
        }
        catch(UsuarioNoEncontradoException | NullPointerException e)
        {
            return ResponseEntity.status(404).build();
        }
        catch(NoEsAdministradorDeLaOrganizacionException e)
        {
            return ResponseEntity.status(402).build();
        }
    }

    @PostMapping("/organizacion/{nombreOrganizacion}/actualizarCaracteristicas")
    public ResponseEntity actualizarCaracteristicas(@PathVariable("nombreOrganizacion") String nombreOrganizacion,
                                                      @RequestHeader("Authorization") String idSesion,
                                                      @RequestBody SolicitudActualizarCaracteristicas solicitudActualizarCaracteristicas)
    {
        Usuario unUsuario = this.obtenerUsuarioSesionManager(idSesion);
        this.actualizarCaracteristicas.ejecutar(unUsuario.nombreUsuario(),nombreOrganizacion,
                solicitudActualizarCaracteristicas.getCaracteristicasActualizar());
        return ResponseEntity.status(200).build();
    }

    private Usuario obtenerUsuarioSesionManager(String idSesion)
    {
        SesionManager sesionManager = SesionManager.getInstance();

        Map<String, Object> unosDatos = sesionManager.obtenerAtributos(idSesion);
        return (Usuario) unosDatos.get("usuario");
    }
}
