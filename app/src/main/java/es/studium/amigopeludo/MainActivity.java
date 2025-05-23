package es.studium.amigopeludo;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioGroup;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import es.studium.amigopeludo.Usuarios.ClienteActivity;
import es.studium.amigopeludo.Usuarios.ProfesionalActivity;

public class MainActivity extends AppCompatActivity {

    Button btnLogin, btnRegister;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        SharedPreferences prefs = getSharedPreferences("amigopeludo", MODE_PRIVATE);
        String lastEmail = prefs.getString("lastEmail", "");
        String lastPassword = prefs.getString("lastPassword", "");

        btnLogin = findViewById(R.id.btnLogin);
        btnRegister = findViewById(R.id.btnRegister);

        btnLogin.setOnClickListener(v -> showLoginDialog());
        btnRegister.setOnClickListener(v -> showRegisterDialog());
    }

    private void showLoginDialog() {
        View dialogView = getLayoutInflater().inflate(R.layout.dialog_login, null);
        EditText etEmail = dialogView.findViewById(R.id.etEmailLogin);
        EditText etPassword = dialogView.findViewById(R.id.etPasswordLogin);

        // Cargar últimos datos guardados
        SharedPreferences prefs = getSharedPreferences("amigopeludo", MODE_PRIVATE);
        String lastEmail = prefs.getString("lastEmail", "");
        String lastPassword = prefs.getString("lastPassword", "");

        etEmail.setText(lastEmail);
        etPassword.setText(lastPassword);

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Iniciar Sesión").setView(dialogView);

        builder.setPositiveButton("Entrar", null);
        builder.setNegativeButton("Cancelar", (dialog, which) -> dialog.dismiss());

        AlertDialog dialog = builder.create();
        dialog.show();

        // Configurar el botón manualmente para evitar cierre automático
        Button positiveButton = dialog.getButton(AlertDialog.BUTTON_POSITIVE);
        positiveButton.setOnClickListener(v -> {
            String email = etEmail.getText().toString().trim();
            String password = etPassword.getText().toString().trim();

            if (email.isEmpty() || password.isEmpty()) {
                Toast.makeText(this, "Rellena todos los campos", Toast.LENGTH_SHORT).show();
            } else if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                Toast.makeText(this, "Email no válido", Toast.LENGTH_SHORT).show();
            } else {
                ConexionBaseDatos.login(email, password, new ConexionBaseDatos.LoginCallback() {
                    @Override
                    public void onSuccess(int idUsuario, String tipoUsuario) {
                        runOnUiThread(() -> {
                            // Guardar sesión
                            SharedPreferences.Editor editor = getSharedPreferences("amigopeludo", MODE_PRIVATE).edit();
                            editor.putInt("idUsuario", idUsuario);
                            editor.putString("tipoUsuario", tipoUsuario);

                            // También guardar último email y pass usados
                            editor.putString("lastEmail", email);
                            editor.putString("lastPassword", password);
                            editor.apply();

                            Intent intent = tipoUsuario.equalsIgnoreCase("cliente") ?
                                    new Intent(MainActivity.this, ClienteActivity.class) :
                                    new Intent(MainActivity.this, ProfesionalActivity.class);
                            intent.putExtra("idUsuario", idUsuario);
                            startActivity(intent);
                            finish();
                        });
                    }

                    @Override
                    public void onError(String mensaje) {
                        runOnUiThread(() -> Toast.makeText(MainActivity.this, mensaje, Toast.LENGTH_SHORT).show());
                    }
                });
            }
        });
    }


    private void showRegisterDialog() {
        View dialogView = getLayoutInflater().inflate(R.layout.dialog_register, null);
        EditText etNombre = dialogView.findViewById(R.id.etNombre);
        EditText etTelefono = dialogView.findViewById(R.id.etTelefono);
        EditText etEmail = dialogView.findViewById(R.id.etEmailRegister);
        EditText etPassword = dialogView.findViewById(R.id.etPasswordRegister);
        RadioGroup rgTipoUsuario = dialogView.findViewById(R.id.rgTipoUsuario);

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Registrarse").setView(dialogView);

        builder.setPositiveButton("Registrarse", (dialog, which) -> {
            String nombre = etNombre.getText().toString().trim();
            String telefono = etTelefono.getText().toString().trim();
            String email = etEmail.getText().toString().trim();
            String password = etPassword.getText().toString().trim();
            int tipoId = rgTipoUsuario.getCheckedRadioButtonId();
            String tipo = (tipoId == R.id.rbCliente) ? "cliente" :
                    (tipoId == R.id.rbProfesional) ? "profesional" : "";

            if (nombre.isEmpty() || telefono.isEmpty() || email.isEmpty() || password.isEmpty() || tipo.isEmpty()) {
                Toast.makeText(this, "Rellena todos los campos", Toast.LENGTH_SHORT).show();
            } else if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                Toast.makeText(this, "Email no válido", Toast.LENGTH_SHORT).show();
            } else if (!telefono.matches("\\d{9,}")) {
                Toast.makeText(this, "Teléfono inválido (mínimo 9 dígitos)", Toast.LENGTH_SHORT).show();
            } else {
                ConexionBaseDatos.registrar(nombre, telefono, email, password, tipo, new ConexionBaseDatos.RegistroCallback() {
                    @Override
                    public void onSuccess() {
                        runOnUiThread(() -> Toast.makeText(MainActivity.this, "Registro exitoso como " + tipo, Toast.LENGTH_SHORT).show());
                    }

                    @Override
                    public void onError(String mensaje) {
                        runOnUiThread(() -> Toast.makeText(MainActivity.this, mensaje, Toast.LENGTH_LONG).show());
                    }
                });
            }
        });

        builder.setNegativeButton("Cancelar", (dialog, which) -> dialog.dismiss());
        builder.show();
    }
}
