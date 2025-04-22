package es.studium.amigopeludo.Citas;

import java.util.List;

public interface ObtenerCitasCallback {
    void onSuccess(List<Cita> citas);
    void onError(String mensaje);
}
