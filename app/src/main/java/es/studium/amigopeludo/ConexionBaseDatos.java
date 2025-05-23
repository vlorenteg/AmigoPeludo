package es.studium.amigopeludo;

import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import es.studium.amigopeludo.Citas.Cita;
import es.studium.amigopeludo.ListadoProfesionales.Profesional;
import es.studium.amigopeludo.Servicios.Servicio;
import okhttp3.FormBody;
import okhttp3.HttpUrl;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class ConexionBaseDatos {

    private static final String BASE_URL = "http://192.168.1.12/ApiGestionAmigoPeludo/usuario.php";
    private static final String CITAS_URL = "http://192.168.1.12/ApiGestionAmigoPeludo/citas.php";

    public interface LoginCallback {
        void onSuccess(int idUsuario, String tipoUsuario);
        void onError(String mensaje);
    }


    public interface RegistroCallback {
        void onSuccess();
        void onError(String mensaje);
    }

    public interface ObtenerCitasCallback {
        void onSuccess(List<Cita> citas);
        void onError(String mensaje);
    }

    public static void login(String email, String password, LoginCallback callback) {
        OkHttpClient client = new OkHttpClient();

        Request request = new Request.Builder()
                .url(BASE_URL)
                .build();

        new Thread(() -> {
            try {
                Response response = client.newCall(request).execute();

                if (response.isSuccessful()) {
                    JSONArray jsonArray = new JSONArray(response.body().string());
                    for (int i = 0; i < jsonArray.length(); i++) {
                        JSONObject user = jsonArray.getJSONObject(i);
                        String userEmail = user.getString("emailUsuario");
                        String userPass = user.getString("contraseñaUsuario");

                        if (userEmail.equals(email) && userPass.equals(password)) {
                            int idUsuario = user.getInt("idUsuario");
                            String tipoUsuario = user.getString("tipoUsuario").trim();
                            callback.onSuccess(idUsuario, tipoUsuario);
                            return;
                        }
                    }
                    callback.onError("Usuario o contraseña incorrectos");
                } else {
                    callback.onError("Error de conexión: " + response.message());
                }
            } catch (IOException | JSONException e) {
                callback.onError("Excepción: " + e.getMessage());
            }
        }).start();
    }

    public static void registrar(String nombre, String telefono, String email, String password, String tipo, RegistroCallback callback) {
        OkHttpClient client = new OkHttpClient();

        RequestBody formBody = new FormBody.Builder()
                .add("nombreUsuario", nombre)
                .add("telefonoUsuario", telefono)
                .add("emailUsuario", email)
                .add("contraseñaUsuario", password)
                .add("tipoUsuario", tipo)
                .build();

        Request request = new Request.Builder()
                .url(BASE_URL)
                .post(formBody)
                .build();

        new Thread(() -> {
            try {
                Response response = client.newCall(request).execute();

                if (response.isSuccessful()) {
                    JSONObject json = new JSONObject(response.body().string());
                    if (json.optBoolean("success", false)) {
                        callback.onSuccess();
                    } else {
                        callback.onError("Error al registrar: " + json.optString("error", "desconocido"));
                    }
                } else {
                    callback.onError("Error del servidor: " + response.message());
                }
            } catch (IOException | JSONException e) {
                callback.onError("Excepción: " + e.getMessage());
            }
        }).start();
    }

    public static ArrayList<Cita> consultarCitas(int idCliente) {
        ArrayList<Cita> citas = new ArrayList<>();
        OkHttpClient client = new OkHttpClient();

        Request request = new Request.Builder()
                .url(CITAS_URL + "?idCliente=" + idCliente)
                .build();

        try {
            Response response = client.newCall(request).execute();
            if (response.isSuccessful()) {
                JSONArray result = new JSONArray(response.body().string());
                for (int i = 0; i < result.length(); i++) {
                    JSONObject citaJson = result.getJSONObject(i);
                    Cita cita = new Cita(
                            citaJson.optString("nombreServicio", ""),
                            citaJson.optString("nombreProfesional", ""),
                            citaJson.getInt("idCita"),
                            citaJson.getString("fechaCita"),
                            citaJson.getString("horaCita"),
                            citaJson.getString("estadoCita"),
                            citaJson.getInt("idClienteFK"),
                            citaJson.getInt("idServicioFK")
                    );
                    citas.add(cita);
                }
            } else {
                Log.e("ConexionBD", response.message());
            }
        } catch (IOException | JSONException e) {
            Log.e("ConexionBD", e.getMessage());
        }

        return citas;
    }


    public static ArrayList<Cita> consultarCitasPorProfesional(int idProfesional) {
        ArrayList<Cita> citas = new ArrayList<>();
        OkHttpClient client = new OkHttpClient();

        Request request = new Request.Builder()
                .url(CITAS_URL + "?idProfesional=" + idProfesional)
                .build();

        try {
            Response response = client.newCall(request).execute();
            if (response.isSuccessful()) {
                JSONArray result = new JSONArray(response.body().string());
                for (int i = 0; i < result.length(); i++) {
                    JSONObject citaJson = result.getJSONObject(i);
                    Cita cita = new Cita(
                            citaJson.optString("nombreServicio", ""),
                            citaJson.optString("nombreCliente", ""),
                            citaJson.getInt("idCita"),
                            citaJson.getString("fechaCita"),
                            citaJson.getString("horaCita"),
                            citaJson.getString("estadoCita"),
                            citaJson.getInt("idClienteFK"),
                            citaJson.getInt("idServicioFK")
                    );
                    citas.add(cita);
                }
            } else {
                Log.e("ConexionBD", response.message());
            }
        } catch (IOException | JSONException e) {
            Log.e("ConexionBD", e.getMessage());
        }

        return citas;
    }


    public static int altaCita(Cita cita) {
        int resultado = 0;
        OkHttpClient client = new OkHttpClient();

        RequestBody formBody = new FormBody.Builder()
                .add("fechaCita", cita.getFecha())
                .add("horaCita", cita.getHora())
                .add("estadoCita", cita.getEstado())
                .add("idClienteFK", String.valueOf(cita.getIdCliente()))
                .add("idServicioFK", String.valueOf(cita.getIdServicio()))
                .build();

        Request request = new Request.Builder()
                .url(CITAS_URL)
                .post(formBody)
                .build();

        try {
            Response response = client.newCall(request).execute();
            resultado = response.code();
        } catch (IOException e) {
            Log.e("ConexionBD", e.getMessage());
        }

        return resultado;
    }

    public static int modificarCita(Cita cita) {
        int resultado = 0;
        OkHttpClient client = new OkHttpClient();

        HttpUrl.Builder urlBuilder = HttpUrl.parse(CITAS_URL).newBuilder();
        urlBuilder
                .addQueryParameter("idCita", String.valueOf(cita.getIdCita()))
                .addQueryParameter("fechaCita", cita.getFecha())
                .addQueryParameter("horaCita", cita.getHora())
                .addQueryParameter("estadoCita", cita.getEstado())
                .addQueryParameter("idClienteFK", String.valueOf(cita.getIdCliente()))
                .addQueryParameter("idServicioFK", String.valueOf(cita.getIdServicio()));

        RequestBody emptyBody = new FormBody.Builder().build();

        Request request = new Request.Builder()
                .url(urlBuilder.build())
                .put(emptyBody)
                .build();

        try {
            Response response = client.newCall(request).execute();
            resultado = response.code();
        } catch (IOException e) {
            Log.e("ConexionBD", e.getMessage());
        }

        return resultado;
    }

    public static int eliminarCita(int idCita) {
        int resultado = 0;
        OkHttpClient client = new OkHttpClient();

        Request request = new Request.Builder()
                .url(CITAS_URL + "?idCita=" + idCita)
                .delete()
                .build();

        try {
            Response response = client.newCall(request).execute();
            resultado = response.code();
        } catch (IOException e) {
            Log.e("ConexionBD", e.getMessage());
        }

        return resultado;
    }

    public static ArrayList<Servicio> consultarServicios(int idProfesional) {
        ArrayList<Servicio> servicios = new ArrayList<>();
        JSONArray result;

        OkHttpClient client = new OkHttpClient();
        Request request = new Request.Builder()
                .url("http://192.168.1.12/ApiGestionAmigoPeludo/servicios.php?idProfesional=" + idProfesional)
                .build();

        try {
            Response response = client.newCall(request).execute();

            if (response.isSuccessful()) {
                result = new JSONArray(response.body().string());

                for (int i = 0; i < result.length(); i++) {
                    JSONObject json = result.getJSONObject(i);
                    int id = json.getInt("idServicio");
                    String nombre = json.getString("nombreServicio");
                    String descripcion = json.getString("descripcionServicio");
                    double importe = json.getDouble("importeServicio");

                    servicios.add(new Servicio(id, nombre, descripcion, importe));
                }
            } else {
                Log.e("ConexionBD", response.message());
            }
        } catch (IOException | JSONException e) {
            Log.e("ConexionBD", e.getMessage());
        }

        return servicios;
    }

    public static int altaServicio(Servicio servicio, int idProfesional) {
        int resultado = 0;
        OkHttpClient client = new OkHttpClient();

        RequestBody formBody = new FormBody.Builder()
                .add("nombreServicio", servicio.getNombreServicio())
                .add("descripcionServicio", servicio.getDescripcion())
                .add("importeServicio", String.valueOf(servicio.getImporte()))
                .add("idProfesionalFK", String.valueOf(idProfesional))
                .build();

        Request request = new Request.Builder()
                .url("http://192.168.1.12/ApiGestionAmigoPeludo/servicios.php")
                .post(formBody)
                .build();

        try {
            Response response = client.newCall(request).execute();
            resultado = response.code(); // Esperado: 201 Created
        } catch (IOException e) {
            Log.e("ConexionBD", e.getMessage());
        }

        return resultado;
    }

    public static int modificarServicio(Servicio servicio) {
        int resultado = 0;
        OkHttpClient client = new OkHttpClient();

        HttpUrl.Builder urlBuilder = HttpUrl.parse("http://192.168.1.12/ApiGestionAmigoPeludo/servicios.php").newBuilder();
        urlBuilder
                .addQueryParameter("idServicio", String.valueOf(servicio.getIdServicio()))
                .addQueryParameter("nombreServicio", servicio.getNombreServicio())
                .addQueryParameter("descripcionServicio", servicio.getDescripcion())
                .addQueryParameter("importeServicio", String.valueOf(servicio.getImporte()));

        RequestBody emptyBody = new FormBody.Builder().build();

        Request request = new Request.Builder()
                .url(urlBuilder.build())
                .put(emptyBody)
                .build();

        try {
            Response response = client.newCall(request).execute();
            resultado = response.code(); // Esperado: 200 OK
        } catch (IOException e) {
            Log.e("ConexionBD", e.getMessage());
        }

        return resultado;
    }

    public static int eliminarServicio(int idServicio) {
        int resultado = 0;
        OkHttpClient client = new OkHttpClient();

        Request request = new Request.Builder()
                .url("http://192.168.1.12/ApiGestionAmigoPeludo/servicios.php?idServicio=" + idServicio)
                .delete()
                .build();

        try {
            Response response = client.newCall(request).execute();
            resultado = response.code();
        } catch (IOException e) {
            Log.e("ConexionBD", e.getMessage());
        }

        return resultado;
    }

    public static ArrayList<Servicio> consultarTodosLosServicios() {
        ArrayList<Servicio> servicios = new ArrayList<>();
        JSONArray result;

        OkHttpClient client = new OkHttpClient();
        Request request = new Request.Builder()
                .url("http://192.168.1.12/ApiGestionAmigoPeludo/servicios.php")
                .build();

        try {
            Response response = client.newCall(request).execute();

            if (response.isSuccessful()) {
                result = new JSONArray(response.body().string());

                for (int i = 0; i < result.length(); i++) {
                    JSONObject json = result.getJSONObject(i);
                    int id = json.getInt("idServicio");
                    String nombre = json.getString("nombreServicio");
                    String descripcion = json.getString("descripcionServicio");
                    double importe = json.getDouble("importeServicio");

                    servicios.add(new Servicio(id, nombre, descripcion, importe));
                }
            }
        } catch (IOException | JSONException e) {
            Log.e("ConexionBD", e.getMessage());
        }

        return servicios;
    }

    public static boolean verificarDisponibilidad(String fecha, String hora, int idServicio) {
        return verificarDisponibilidad(fecha, hora, idServicio, null);
    }


    public static boolean verificarDisponibilidad(String fecha, String hora, int idServicio, Integer idCitaOmitir) {
        OkHttpClient client = new OkHttpClient();
        boolean disponible = false;

        HttpUrl.Builder builder = HttpUrl.parse("http://192.168.1.12/ApiGestionAmigoPeludo/verificar_cita.php").newBuilder()
                .addQueryParameter("fechaCita", fecha)
                .addQueryParameter("horaCita", hora)
                .addQueryParameter("idServicioFK", String.valueOf(idServicio));

        if (idCitaOmitir != null) {
            builder.addQueryParameter("idCita", String.valueOf(idCitaOmitir));
        }

        Request request = new Request.Builder().url(builder.build()).build();

        try {
            Response response = client.newCall(request).execute();
            if (response.isSuccessful()) {
                JSONObject json = new JSONObject(response.body().string());
                disponible = json.optBoolean("disponible", false);
            }
        } catch (IOException | JSONException e) {
            Log.e("ConexionBD", "Error al verificar disponibilidad: " + e.getMessage());
        }

        return disponible;
    }

    public static List<String> obtenerNombresProfesionales() {
        List<String> nombres = new ArrayList<>();
        OkHttpClient client = new OkHttpClient();
        Request request = new Request.Builder()
                .url("http://192.168.1.12/ApiGestionAmigoPeludo/usuario.php?tipo=profesional")
                .build();

        try {
            Response response = client.newCall(request).execute();
            if (response.isSuccessful()) {
                JSONArray json = new JSONArray(response.body().string());
                for (int i = 0; i < json.length(); i++) {
                    JSONObject obj = json.getJSONObject(i);
                    String nombre = obj.getString("nombreUsuario").trim();
                    if (!nombre.isEmpty()) {
                        nombres.add(nombre);
                    }
                }
            }
        } catch (Exception e) {
            Log.e("ConexionBD", "Error cargando profesionales: " + e.getMessage());
        }

        return nombres;
    }


    public static ArrayList<Servicio> obtenerServiciosPorNombreProfesional(String nombreProfesional) {
        ArrayList<Servicio> servicios = new ArrayList<>();
        OkHttpClient client = new OkHttpClient();

        Request request = new Request.Builder()
                .url("http://192.168.1.12/ApiGestionAmigoPeludo/servicios.php?nombreProfesional=" + nombreProfesional)
                .build();

        try {
            Response response = client.newCall(request).execute();
            if (response.isSuccessful()) {
                JSONArray jsonArray = new JSONArray(response.body().string());
                for (int i = 0; i < jsonArray.length(); i++) {
                    JSONObject json = jsonArray.getJSONObject(i);
                    servicios.add(new Servicio(
                            json.getInt("idServicio"),
                            json.getString("nombreServicio"),
                            json.getString("descripcionServicio"),
                            json.getDouble("importeServicio")
                    ));
                }
            }
        } catch (Exception e) {
            Log.e("ConexionBD", e.getMessage());
        }

        return servicios;
    }

    public static List<String> obtenerHorasOcupadas(String fecha, int idServicio) {
        List<String> ocupadas = new ArrayList<>();
        OkHttpClient client = new OkHttpClient();

        HttpUrl url = HttpUrl.parse("http://192.168.1.12/ApiGestionAmigoPeludo/franjas_ocupadas.php")
                .newBuilder()
                .addQueryParameter("fechaCita", fecha)
                .addQueryParameter("idServicioFK", String.valueOf(idServicio))
                .build();

        Request request = new Request.Builder().url(url).build();

        try {
            Response response = client.newCall(request).execute();
            if (response.isSuccessful()) {
                JSONObject json = new JSONObject(response.body().string());
                JSONArray array = json.getJSONArray("ocupadas");
                for (int i = 0; i < array.length(); i++) {
                    ocupadas.add(array.getString(i));
                }
            }
        } catch (IOException | JSONException e) {
            Log.e("ConexionBD", "Error al obtener horas ocupadas: " + e.getMessage());
        }

        return ocupadas;
    }

    public static List<Profesional> obtenerListadoProfesionales() {
        List<Profesional> lista = new ArrayList<>();
        OkHttpClient client = new OkHttpClient();

        Request request = new Request.Builder()
                .url("http://192.168.1.12/ApiGestionAmigoPeludo/usuario.php?tipo=profesional")
                .build();

        try {
            Response response = client.newCall(request).execute();
            if (response.isSuccessful()) {
                JSONArray jsonArray = new JSONArray(response.body().string());
                for (int i = 0; i < jsonArray.length(); i++) {
                    JSONObject json = jsonArray.getJSONObject(i);
                    lista.add(new Profesional(
                            json.getInt("idUsuario"),
                            json.getString("nombreUsuario"),
                            json.getString("telefonoUsuario"),
                            json.getString("emailUsuario"),
                            json.optString("servicios", "Sin servicios")
                    ));

                }
            }
        } catch (Exception e) {
            Log.e("ConexionBD", e.getMessage());
        }

        return lista;
    }


}