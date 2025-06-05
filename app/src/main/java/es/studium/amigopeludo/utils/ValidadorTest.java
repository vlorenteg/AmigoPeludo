package es.studium.amigopeludo.utils;

import static org.junit.Assert.*;
import org.junit.Test;

public class ValidadorTest {

    @Test
    public void testEmailCorrecto() {
        assertTrue(Validador.emailValido("usuario@correo.com"));
    }

    @Test
    public void testEmailIncorrecto() {
        assertFalse(Validador.emailValido("correoSinArroba"));
    }

    @Test
    public void testTelefonoCorrecto() {
        assertTrue(Validador.telefonoValido("612345678"));
    }

    @Test
    public void testTelefonoCorto() {
        assertFalse(Validador.telefonoValido("123"));
    }

    @Test
    public void testCamposCompletos() {
        assertTrue(Validador.camposCompletos("nombre", "correo", "pass"));
    }

    @Test
    public void testCamposVacios() {
        assertFalse(Validador.camposCompletos("nombre", "", "pass"));
    }

    @Test
    public void testTipoValido() {
        assertTrue(Validador.tipoUsuarioValido("cliente"));
        assertTrue(Validador.tipoUsuarioValido("profesional"));
    }

    @Test
    public void testTipoInvalido() {
        assertFalse(Validador.tipoUsuarioValido("admin"));
    }
}

