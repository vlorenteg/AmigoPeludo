package es.studium.amigopeludo.Servicios;

public class Servicio {
    private int idServicio;  // Añadimos el idServicio
    private String nombre;
    private String descripcion;
    private double importe;

    public Servicio(int idServicio, String nombre, String descripcion, double importe) {
        this.idServicio = idServicio;
        this.nombre = nombre;
        this.descripcion = descripcion;
        this.importe = importe;
    }

    // Getters y Setters
    public int getIdServicio() { return idServicio; }
    public void setIdServicio(int idServicio) { this.idServicio = idServicio; }

    public String getNombreServicio() { return nombre; }
    public void setNombreServicio(String nombre) { this.nombre = nombre; }

    public String getDescripcion() { return descripcion; }
    public void setDescripcion(String descripcion) { this.descripcion = descripcion; }

    public double getImporte() { return importe; }
    public void setImporte(double importe) { this.importe = importe; }
}
