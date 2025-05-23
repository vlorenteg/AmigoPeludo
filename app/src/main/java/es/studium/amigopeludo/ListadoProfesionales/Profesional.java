package es.studium.amigopeludo.ListadoProfesionales;

public class Profesional {
    private int id;
    private String nombre;
    private String telefono;
    private String email;
    private String servicios;

    public Profesional(int id, String nombre, String telefono, String email, String servicios) {
        this.id = id;
        this.nombre = nombre;
        this.telefono = telefono;
        this.email = email;
        this.servicios = servicios;
    }

    public int getId() { return id; }
    public String getNombre() { return nombre; }
    public String getTelefono() { return telefono; }
    public String getEmail() { return email; }
    public String getServicios() { return servicios; }
}


