package es.studium.amigopeludo.utils;

public class Validador {

    public static boolean emailValido(String email) {
        return email != null && email.matches("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$");
    }

    public static boolean telefonoValido(String telefono) {
        return telefono != null && telefono.matches("\\d{9,}");
    }

    public static boolean camposCompletos(String... campos) {
        for (String campo : campos) {
            if (campo == null || campo.trim().isEmpty()) return false;
        }
        return true;
    }

    public static boolean tipoUsuarioValido(String tipo) {
        return tipo.equalsIgnoreCase("cliente") || tipo.equalsIgnoreCase("profesional");
    }
}

