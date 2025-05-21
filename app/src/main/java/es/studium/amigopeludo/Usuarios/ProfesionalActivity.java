package es.studium.amigopeludo.Usuarios;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

import es.studium.amigopeludo.Citas.Cita;
import es.studium.amigopeludo.Citas.CitasAdapter;
import es.studium.amigopeludo.ConexionBaseDatos;
import es.studium.amigopeludo.MainActivity;
import es.studium.amigopeludo.R;
import es.studium.amigopeludo.RecyclerViewOnItemClickListener;
import es.studium.amigopeludo.Servicios.ServiciosActivity;

public class ProfesionalActivity extends AppCompatActivity implements RecyclerViewOnItemClickListener {

    private RecyclerView recyclerView;
    private CitasAdapter citasAdapter;
    private List<Cita> citasList = new ArrayList<>();
    private int idProfesional;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profesional);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle("Citas Profesionales");
        }

        idProfesional = getIntent().getIntExtra("idUsuario", -1);

        recyclerView = findViewById(R.id.recyclerViewCitasProfesional);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        citasAdapter = new CitasAdapter(citasList, this);
        recyclerView.setAdapter(citasAdapter);

        Button btnServicios = findViewById(R.id.btnServicios);
        btnServicios.setOnClickListener(v -> {
            Intent intent = new Intent(this, ServiciosActivity.class);
            intent.putExtra("idUsuario", idProfesional);
            startActivity(intent);
        });

        cargarCitasDelProfesional();
    }

    private void cargarCitasDelProfesional() {
        new Thread(() -> {
            ArrayList<Cita> citas = ConexionBaseDatos.consultarCitasPorProfesional(idProfesional);
            runOnUiThread(() -> {
                citasList.clear();
                citasList.addAll(citas);
                citasAdapter.notifyDataSetChanged();
            });
        }).start();
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_toolbar, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.action_logout) {
            new AlertDialog.Builder(this)
                    .setTitle("Cerrar sesión")
                    .setMessage("¿Estás seguro de que deseas cerrar sesión?")
                    .setPositiveButton("Sí", (dialog, which) -> {
                        getSharedPreferences("amigopeludo", MODE_PRIVATE).edit().clear().apply();
                        Intent intent = new Intent(this, MainActivity.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        startActivity(intent);
                        finish();
                    })
                    .setNegativeButton("No", (dialog, which) -> dialog.dismiss())
                    .show();
            return true;
        } else if (item.getItemId() == android.R.id.home) {
            onBackPressed();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        Toast.makeText(this, "Usa el botón de cerrar sesión", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onClick(View v, int position) {
        Cita cita = citasList.get(position);
        Toast.makeText(this, "Cita: " + cita.getFecha() + " " + cita.getHora(), Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onLongClick(View v, int position) {
        Cita cita = citasList.get(position);

        new AlertDialog.Builder(this)
                .setTitle("Cancelar Cita")
                .setMessage("¿Deseas cancelar esta cita?")
                .setPositiveButton("Sí", (dialog, which) -> {
                    cita.setEstado("cancelada");

                    new Thread(() -> {
                        int resultado = ConexionBaseDatos.modificarCita(cita);
                        runOnUiThread(() -> {
                            if (resultado == 200) {
                                Toast.makeText(this, "Cita cancelada correctamente", Toast.LENGTH_SHORT).show();
                                cargarCitasDelProfesional();
                            } else {
                                Toast.makeText(this, "Error al cancelar cita", Toast.LENGTH_SHORT).show();
                            }
                        });
                    }).start();
                })
                .setNegativeButton("No", (dialog, which) -> dialog.dismiss())
                .show();
    }
}
