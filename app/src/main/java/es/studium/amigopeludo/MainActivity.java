package es.studium.amigopeludo;

import android.content.Intent;
import android.os.Bundle;
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

        btnLogin = findViewById(R.id.btnLogin);
        btnRegister = findViewById(R.id.btnRegister);

        btnLogin.setOnClickListener(v -> showLoginDialog());
        btnRegister.setOnClickListener(v -> showRegisterDialog());
    }

    private void showLoginDialog() {
        View dialogView = getLayoutInflater().inflate(R.layout.dialog_login, null);

        EditText etEmail = dialogView.findViewById(R.id.etEmailLogin);
        EditText etPassword = dialogView.findViewById(R.id.etPasswordLogin);

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Iniciar Sesión");
        builder.setView(dialogView);

        builder.setPositiveButton("Entrar", (dialog, which) -> {
            String email = etEmail.getText().toString().trim();
            String password = etPassword.getText().toString().trim();

            if (email.isEmpty() || password.isEmpty()) {
                Toast.makeText(MainActivity.this, "Rellena todos los campos", Toast.LENGTH_SHORT).show();
            } else {
                ConexionBaseDatos.login(email, password, new ConexionBaseDatos.LoginCallback() {
                    @Override
                    public void onSuccess(String tipoUsuario) {
                        if (tipoUsuario.equals("cliente")) {
                            startActivity(new Intent(MainActivity.this, ClienteActivity.class));
                        } else if (tipoUsuario.equals("profesional")) {
                            startActivity(new Intent(MainActivity.this, ProfesionalActivity.class));
                        }
                    }

                    @Override
                    public void onError(String mensaje) {
                        Toast.makeText(MainActivity.this, mensaje, Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });

        builder.setNegativeButton("Cancelar", (dialog, which) -> dialog.dismiss());

        builder.show();
    }

    private void showRegisterDialog() {
        View dialogView = getLayoutInflater().inflate(R.layout.dialog_register, null);

        EditText etNombre = dialogView.findViewById(R.id.etNombre);
        EditText etTelefono = dialogView.findViewById(R.id.etTelefono);
        EditText etEmail = dialogView.findViewById(R.id.etEmailRegister);
        EditText etPassword = dialogView.findViewById(R.id.etPasswordRegister);
        RadioGroup rgTipoUsuario = dialogView.findViewById(R.id.rgTipoUsuario);

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Registrarse");
        builder.setView(dialogView);

        builder.setPositiveButton("Registrarse", (dialog, which) -> {
            String nombre = etNombre.getText().toString().trim();
            String telefono = etTelefono.getText().toString().trim();
            String email = etEmail.getText().toString().trim();
            String password = etPassword.getText().toString().trim();

            int tipoId = rgTipoUsuario.getCheckedRadioButtonId();
            String tipo = (tipoId == R.id.rbCliente) ? "cliente" :
                    (tipoId == R.id.rbProfesional) ? "profesional" : "";

            if (nombre.isEmpty() || telefono.isEmpty() || email.isEmpty() || password.isEmpty() || tipo.isEmpty()) {
                Toast.makeText(MainActivity.this, "Rellena todos los campos", Toast.LENGTH_SHORT).show();
            } else {
                ConexionBaseDatos.registrar(nombre, telefono, email, password, tipo, new ConexionBaseDatos.RegistroCallback() {
                    @Override
                    public void onSuccess() {
                        Toast.makeText(MainActivity.this, "Registro exitoso como " + tipo, Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onError(String mensaje) {
                        Toast.makeText(MainActivity.this, mensaje, Toast.LENGTH_LONG).show();
                    }
                });
            }
        });

        builder.setNegativeButton("Cancelar", (dialog, which) -> dialog.dismiss());

        builder.show();
    }
}
