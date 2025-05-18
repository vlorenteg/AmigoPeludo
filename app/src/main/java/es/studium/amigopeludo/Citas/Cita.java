package es.studium.amigopeludo.Citas;

public class Cita {
    private int idCita;
    private String fecha;
    private String estado;
    private int idCliente;
    private int idServicio;

    public Cita(int idCita, String fecha, String estado, int idCliente, int idServicio) {
        this.idCita = idCita;
        this.fecha = fecha;
        this.estado = estado;
        this.idCliente = idCliente;
        this.idServicio = idServicio;
    }

    // Getters y Setters
    public int getIdCita() { return idCita; }
    public void setIdCita(int idCita) { this.idCita = idCita; }

    public String getFecha() { return fecha; }
    public void setFecha(String fecha) { this.fecha = fecha; }

    public String getEstado() { return estado; }
    public void setEstado(String estado) { this.estado = estado; }

    public int getIdCliente() { return idCliente; }
    public void setIdCliente(int idCliente) { this.idCliente = idCliente; }

    public int getIdServicio() { return idServicio; }
    public void setIdServicio(int idServicio) { this.idServicio = idServicio; }
}


