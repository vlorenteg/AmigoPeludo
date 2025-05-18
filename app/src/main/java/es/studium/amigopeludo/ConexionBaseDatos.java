package es.studium.amigopeludo;

import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import es.studium.amigopeludo.Citas.Cita;
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
        JSONArray result;

        OkHttpClient client = new OkHttpClient();
        Request request = new Request.Builder()
                .url("http://192.168.1.12/ApiGestionAmigoPeludo/citas.php?idCliente=" + idCliente)
                .build();

        try {
            Response response = client.newCall(request).execute();

            if (response.isSuccessful()) {
                result = new JSONArray(response.body().string());

                for (int i = 0; i < result.length(); i++) {
                    JSONObject citaJson = result.getJSONObject(i);

                    int idCita = citaJson.getInt("idCita");
                    String fechaCita = citaJson.getString("fechaCita");
                    String estadoCita = citaJson.getString("estadoCita");
                    int idClienteFK = citaJson.getInt("idClienteFK");
                    int idServicioFK = citaJson.getInt("idServicioFK");

                    Cita cita = new Cita(idCita, fechaCita, estadoCita, idClienteFK, idServicioFK);
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
        JSONArray result;

        OkHttpClient client = new OkHttpClient();
        Request request = new Request.Builder()
                .url("http://192.168.1.12/ApiGestionAmigoPeludo/citas.php?idProfesional=" + idProfesional)
                .build();

        try {
            Response response = client.newCall(request).execute();

            if (response.isSuccessful()) {
                result = new JSONArray(response.body().string());

                for (int i = 0; i < result.length(); i++) {
                    JSONObject citaJson = result.getJSONObject(i);

                    int idCita = citaJson.getInt("idCita");
                    String fechaCita = citaJson.getString("fechaCita");
                    String estadoCita = citaJson.getString("estadoCita");
                    int idClienteFK = citaJson.getInt("idClienteFK");
                    int idServicioFK = citaJson.getInt("idServicioFK");

                    Cita cita = new Cita(idCita, fechaCita, estadoCita, idClienteFK, idServicioFK);
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
                .add("estadoCita", cita.getEstado())
                .add("idClienteFK", String.valueOf(cita.getIdCliente()))
                .add("idServicioFK", String.valueOf(cita.getIdServicio()))
                .build();

        Request request = new Request.Builder()
                .url("http://192.168.1.12/ApiGestionAmigoPeludo/citas.php")
                .post(formBody)
                .build();

        try {
            Response response = client.newCall(request).execute();
            resultado = response.code(); // 201 Created esperado
        } catch (IOException e) {
            Log.e("ConexionBD", e.getMessage());
        }

        return resultado;
    }

    public static int modificarCita(Cita cita) {
        int resultado = 0;
        OkHttpClient client = new OkHttpClient();

        HttpUrl.Builder urlBuilder = HttpUrl.parse("http://192.168.1.12/ApiGestionAmigoPeludo/citas.php").newBuilder();
        urlBuilder
                .addQueryParameter("idCita", String.valueOf(cita.getIdCita()))
                .addQueryParameter("fechaCita", cita.getFecha())
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
            resultado = response.code(); // 200 OK esperado
        } catch (IOException e) {
            Log.e("ConexionBD", e.getMessage());
        }

        return resultado;
    }

    public static int eliminarCita(int idCita) {
        int resultado = 0;
        OkHttpClient client = new OkHttpClient();

        Request request = new Request.Builder()
                .url("http://192.168.1.12/ApiGestionAmigoPeludo/citas.php?idCita=" + idCita)
                .delete()
                .build();

        try {
            Response response = client.newCall(request).execute();
            resultado = response.code(); // 200 OK esperado
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
            resultado = response.code(); // Esperado: 200 OK
        } catch (IOException e) {
            Log.e("ConexionBD", e.getMessage());
        }

        return resultado;
    }



}