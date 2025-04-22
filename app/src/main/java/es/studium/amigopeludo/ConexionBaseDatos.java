package es.studium.amigopeludo;

import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import es.studium.amigopeludo.Citas.Cita;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class ConexionBaseDatos {

    private static final String BASE_URL = "http://192.168.1.12/ApiGestionAmigoPeludo/usuarios.php";
    private static final String CITAS_URL = "http://192.168.1.12/ApiGestionAmigoPeludo/citas.php";

    public interface LoginCallback {
        void onSuccess(String tipoUsuario);
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

    // Método LOGIN
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
                    boolean encontrado = false;
                    String tipoUsuario = "";

                    for (int i = 0; i < jsonArray.length(); i++) {
                        JSONObject user = jsonArray.getJSONObject(i);
                        String userEmail = user.getString("emailUsuario");
                        String userPass = user.getString("contraseñaUsuario");

                        if (userEmail.equals(email) && userPass.equals(password)) {
                            encontrado = true;
                            tipoUsuario = user.getString("tipoUsuario");
                            break;
                        }
                    }

                    if (encontrado) {
                        callback.onSuccess(tipoUsuario);
                    } else {
                        callback.onError("Usuario o contraseña incorrectos");
                    }
                } else {
                    callback.onError("Error de conexión: " + response.message());
                }
            } catch (IOException | JSONException e) {
                callback.onError("Excepción: " + e.getMessage());
            }
        }).start();
    }

    // Método REGISTRO
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
                    boolean success = json.getBoolean("success");

                    if (success) {
                        callback.onSuccess();
                    } else {
                        callback.onError("Error al registrar: " + json.getString("error"));
                    }
                } else {
                    callback.onError("Error del servidor: " + response.message());
                }
            } catch (IOException | JSONException e) {
                callback.onError("Excepción: " + e.getMessage());
            }
        }).start();
    }

    // Método OBTENER CITAS
    public static void obtenerCitas(ObtenerCitasCallback callback) {
        OkHttpClient client = new OkHttpClient();
        Request request = new Request.Builder()
                .url(CITAS_URL)
                .build();

        new Thread(() -> {
            try {
                Response response = client.newCall(request).execute();
                if (response.isSuccessful()) {
                    JSONArray jsonArray = new JSONArray(response.body().string());
                    List<Cita> citas = new ArrayList<>();

                    for (int i = 0; i < jsonArray.length(); i++) {
                        JSONObject citaJson = jsonArray.getJSONObject(i);
                        String fecha = citaJson.getString("fecha");
                        String estado = citaJson.getString("estado");

                        Cita cita = new Cita(fecha, estado);
                        citas.add(cita);
                    }
                    callback.onSuccess(citas);
                } else {
                    callback.onError("Error al obtener citas: " + response.message());
                }
            } catch (IOException | JSONException e) {
                callback.onError("Excepción: " + e.getMessage());
            }
        }).start();
    }
}
