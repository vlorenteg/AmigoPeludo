package es.studium.amigopeludo.Citas;

import static org.junit.Assert.*;
import org.junit.Test;

public class CitaTest {

    @Test
    public void testCrearCita() {
        Cita cita = new Cita(1, "2025-06-02", "10:00", "pendiente", 3, 5);

        assertEquals(1, cita.getIdCita());
        assertEquals("2025-06-02", cita.getFecha());
        assertEquals("10:00", cita.getHora());
        assertEquals("pendiente", cita.getEstado());
        assertEquals(3, cita.getIdCliente());
        assertEquals(5, cita.getIdServicio());
    }

    @Test
    public void testModificarEstado() {
        Cita cita = new Cita(1, "2025-06-02", "10:00", "pendiente", 3, 5);
        cita.setEstado("cancelada");

        assertEquals("cancelada", cita.getEstado());
    }
}
