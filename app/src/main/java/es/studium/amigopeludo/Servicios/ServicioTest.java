package es.studium.amigopeludo.Servicios;

import static org.junit.Assert.*;
import org.junit.Test;

public class ServicioTest {

    @Test
    public void testCrearServicio() {
        Servicio servicio = new Servicio(1, "Peluquería", "Baño y corte completo", 25.0);

        assertEquals(1, servicio.getIdServicio());
        assertEquals("Peluquería", servicio.getNombreServicio());
        assertEquals("Baño y corte completo", servicio.getDescripcion());
        assertEquals(25.0, servicio.getImporte(), 0.01);
    }

    @Test
    public void testModificarImporte() {
        Servicio servicio = new Servicio(2, "Adiestramiento", "Sesión de obediencia básica", 30.0);
        servicio.setImporte(40.5);

        assertEquals(40.5, servicio.getImporte(), 0.01);
    }

    @Test
    public void testModificarNombre() {
        Servicio servicio = new Servicio(3, "Veterinario", "Consulta general", 50.0);
        servicio.setNombreServicio("Consulta Veterinaria");

        assertEquals("Consulta Veterinaria", servicio.getNombreServicio());
    }
}
