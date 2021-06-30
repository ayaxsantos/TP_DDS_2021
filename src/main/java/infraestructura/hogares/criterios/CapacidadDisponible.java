package infraestructura.hogares.criterios;

import dominio.animal.Mascota;
import dominio.persona.Persona;
import infraestructura.hogares.Hogar;
import infraestructura.hogares.HogaresResponse;

import java.util.List;
import java.util.stream.Collectors;

public class CapacidadDisponible implements ValidacionHogar {

    @Override
    public boolean ejecutar(Hogar hogar, Persona personaRescatista, Mascota mascota) {
        return hogar.lugares_disponibles > 0;
    }

}