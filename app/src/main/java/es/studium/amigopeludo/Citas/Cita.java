package es.studium.amigopeludo.Citas;

public class Cita {
    private int idCita;
    private String fecha;
    private String hora;
    private String estado;
    private int idCliente;
    private int idServicio;
    private String nombreServicio;
    private String nombreProfesional;

    public Cita(String nombreServicio, String nombreProfesional, int idCita, String fecha, String hora,
                String estado, int idCliente, int idServicio) {
        this.idCita = idCita;
        this.fecha = fecha;
        this.hora = hora;
        this.estado = estado;
        this.idCliente = idCliente;
        this.idServicio = idServicio;
        this.nombreServicio = nombreServicio;
        this.nombreProfesional = nombreProfesional;
    }

    public Cita(int idCita, String fecha, String hora, String estado, int idCliente, int idServicio) {
        this.idCita = idCita;
        this.fecha = fecha;
        this.hora = hora;
        this.estado = estado;
        this.idCliente = idCliente;
        this.idServicio = idServicio;
    }

    public int getIdCita() { return idCita; }
    public void setIdCita(int idCita) { this.idCita = idCita; }

    public String getFecha() { return fecha; }
    public void setFecha(String fecha) { this.fecha = fecha; }

    public String getHora() { return hora; }
    public void setHora(String hora) { this.hora = hora; }

    public String getEstado() { return estado; }
    public void setEstado(String estado) { this.estado = estado; }

    public int getIdCliente() { return idCliente; }
    public void setIdCliente(int idCliente) { this.idCliente = idCliente; }

    public int getIdServicio() { return idServicio; }
    public void setIdServicio(int idServicio) { this.idServicio = idServicio; }

    public String getNombreServicio() { return nombreServicio; }
    public void setNombreServicio(String nombreServicio) { this.nombreServicio = nombreServicio; }

    public String getNombreProfesional() { return nombreProfesional; }
    public void setNombreProfesional(String nombreProfesional) { this.nombreProfesional = nombreProfesional; }

}
