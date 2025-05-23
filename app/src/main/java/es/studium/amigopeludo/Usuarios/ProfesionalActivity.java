package es.studium.amigopeludo.Usuarios;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.CalendarView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import es.studium.amigopeludo.Citas.Cita;
import es.studium.amigopeludo.Citas.CitasAdapter;
import es.studium.amigopeludo.ConexionBaseDatos;
import es.studium.amigopeludo.MainActivity;
import es.studium.amigopeludo.R;
import es.studium.amigopeludo.RecyclerViewOnItemClickListener;
import es.studium.amigopeludo.Servicios.ServiciosActivity;

public class ProfesionalActivity extends AppCompatActivity implements RecyclerViewOnItemClickListener {

    private RecyclerView recyclerView;
    private CalendarView calendarView;
    private CitasAdapter citasAdapter;
    private List<Cita> citasList = new ArrayList<>();
    private List<Cita> todasLasCitas = new ArrayList<>();
    private int idProfesional;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profesional);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle(getString(R.string.titulo_citas_profesionales));
        }

        idProfesional = getIntent().getIntExtra("idUsuario", -1);

        recyclerView = findViewById(R.id.recyclerViewCitasProfesional);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        citasAdapter = new CitasAdapter(citasList, this);
        recyclerView.setAdapter(citasAdapter);

        calendarView = findViewById(R.id.calendarView);
        calendarView.setOnDateChangeListener((view, year, month, dayOfMonth) -> {
            String fechaSeleccionada = String.format(Locale.getDefault(), "%04d-%02d-%02d", year, month + 1, dayOfMonth);
            filtrarCitasPorFecha(fechaSeleccionada);
        });

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
                todasLasCitas.clear();
                todasLasCitas.addAll(citas);
                filtrarCitasPorFecha(obtenerFechaActual());
            });
        }).start();
    }

    private void filtrarCitasPorFecha(String fecha) {
        List<Cita> filtradas = new ArrayList<>();
        for (Cita cita : todasLasCitas) {
            if (cita.getFecha().equals(fecha)) {
                filtradas.add(cita);
            }
        }
        filtradas.sort((c1, c2) -> c1.getHora().compareTo(c2.getHora()));
        citasList.clear();
        citasList.addAll(filtradas);
        citasAdapter.notifyDataSetChanged();
    }

    private String obtenerFechaActual() {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
        return sdf.format(new Date());
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
                    .setTitle(getString(R.string.titulo_cerrar_sesion))
                    .setMessage(getString(R.string.mensaje_cerrar_sesion))
                    .setPositiveButton(getString(R.string.accion_si), (dialog, which) -> {
                        getSharedPreferences("amigopeludo", MODE_PRIVATE).edit().clear().apply();
                        Intent intent = new Intent(this, MainActivity.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        startActivity(intent);
                        finish();
                    })
                    .setNegativeButton(getString(R.string.accion_no), (dialog, which) -> dialog.dismiss())
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
        Toast.makeText(this, getString(R.string.mensaje_back_prohibido), Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onClick(View v, int position) {
        Cita cita = citasList.get(position);
        Toast.makeText(this, getString(R.string.mensaje_cita_toast) + cita.getFecha() + " " + cita.getHora(), Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onLongClick(View v, int position) {
        Cita cita = citasList.get(position);

        new AlertDialog.Builder(this)
                .setTitle(getString(R.string.titulo_cancelar_cita))
                .setMessage(getString(R.string.mensaje_cancelar_cita))
                .setPositiveButton(getString(R.string.accion_si), (dialog, which) -> {
                    cita.setEstado("cancelada");

                    new Thread(() -> {
                        int resultado = ConexionBaseDatos.modificarCita(cita);
                        runOnUiThread(() -> {
                            if (resultado == 200) {
                                Toast.makeText(this, getString(R.string.cita_cancelada), Toast.LENGTH_SHORT).show();
                                cargarCitasDelProfesional();
                            } else {
                                Toast.makeText(this, getString(R.string.error_cancelar_cita), Toast.LENGTH_SHORT).show();
                            }
                        });
                    }).start();
                })
                .setNegativeButton(getString(R.string.accion_no), (dialog, which) -> dialog.dismiss())
                .show();
    }
}
