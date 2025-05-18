package es.studium.amigopeludo.Usuarios;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
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
import es.studium.amigopeludo.Servicios.Servicio;

public class ClienteActivity extends AppCompatActivity implements RecyclerViewOnItemClickListener {

    private RecyclerView recyclerView;
    private CitasAdapter citasAdapter;
    private List<Cita> citasList = new ArrayList<>();
    private List<Servicio> serviciosList = new ArrayList<>();
    private int idCliente;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cliente);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle("Mis Citas");
        }

        idCliente = getIntent().getIntExtra("idUsuario", -1);

        recyclerView = findViewById(R.id.recyclerViewCitas);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        citasAdapter = new CitasAdapter(citasList, this);
        recyclerView.setAdapter(citasAdapter);

        Button btnNuevaCita = findViewById(R.id.btnNuevaCita);
        btnNuevaCita.setOnClickListener(v -> showNewCitaDialog());

        cargarServicios();
        cargarCitasCliente();
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


    private void cargarCitasCliente() {
        new Thread(() -> {
            ArrayList<Cita> citas = ConexionBaseDatos.consultarCitas(idCliente);
            runOnUiThread(() -> {
                citasList.clear();
                citasList.addAll(citas);
                citasAdapter.notifyDataSetChanged();
            });
        }).start();
    }

    private void cargarServicios() {
        new Thread(() -> {
            serviciosList = ConexionBaseDatos.consultarServicios(idCliente);
        }).start();
    }

    private void showNewCitaDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        View view = getLayoutInflater().inflate(R.layout.dialog_nueva_cita, null);
        builder.setView(view);

        EditText edtFecha = view.findViewById(R.id.edtFecha);
        Spinner spinnerServicios = view.findViewById(R.id.spinnerServicios);

        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, obtenerNombresServicios(serviciosList));
        spinnerServicios.setAdapter(adapter);

        builder.setTitle("Nueva Cita");
        builder.setNegativeButton("Cancelar", (dialog, which) -> dialog.dismiss());

        AlertDialog dialog = builder.create();

        dialog.setButton(AlertDialog.BUTTON_POSITIVE, "Agregar", (d, which) -> {});
        dialog.show();

        dialog.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener(v -> {
            String fecha = edtFecha.getText().toString().trim();

            if (fecha.isEmpty() || !fecha.matches("^\\d{4}-\\d{2}-\\d{2}$")) {
                Toast.makeText(this, "Formato de fecha inválido (ej: 2025-04-20)", Toast.LENGTH_SHORT).show();
                return;
            }

            int posicion = spinnerServicios.getSelectedItemPosition();
            Servicio servicioSeleccionado = serviciosList.get(posicion);

            Cita nuevaCita = new Cita(0, fecha, "pendiente", idCliente, servicioSeleccionado.getIdServicio());

            new Thread(() -> {
                int resultado = ConexionBaseDatos.altaCita(nuevaCita);
                runOnUiThread(() -> {
                    if (resultado == 201) {
                        Toast.makeText(this, "Cita añadida correctamente", Toast.LENGTH_SHORT).show();
                        cargarCitasCliente();
                        dialog.dismiss();
                    } else {
                        Toast.makeText(this, "Error al crear la cita", Toast.LENGTH_SHORT).show();
                    }
                });
            }).start();
        });
    }

    private void showEditCitaDialog(Cita cita) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        View view = getLayoutInflater().inflate(R.layout.dialog_nueva_cita, null);
        builder.setView(view);

        EditText edtFecha = view.findViewById(R.id.edtFecha);
        Spinner spinnerServicios = view.findViewById(R.id.spinnerServicios);

        edtFecha.setText(cita.getFecha());

        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, obtenerNombresServicios(serviciosList));
        spinnerServicios.setAdapter(adapter);

        for (int i = 0; i < serviciosList.size(); i++) {
            if (serviciosList.get(i).getIdServicio() == cita.getIdServicio()) {
                spinnerServicios.setSelection(i);
                break;
            }
        }

        builder.setPositiveButton("Guardar", (dialog, which) -> {
            cita.setFecha(edtFecha.getText().toString());
            int pos = spinnerServicios.getSelectedItemPosition();
            Servicio s = serviciosList.get(pos);
            cita.setIdServicio(s.getIdServicio());

            new Thread(() -> ConexionBaseDatos.modificarCita(cita)).start();
            citasAdapter.notifyDataSetChanged();
        });

        builder.setNegativeButton("Cancelar", (dialog, which) -> dialog.dismiss());
        builder.show();
    }

    private void showDeleteCitaDialog(Cita cita) {
        new AlertDialog.Builder(this)
                .setTitle("Eliminar Cita")
                .setMessage("¿Estás seguro de que deseas eliminar esta cita?")
                .setPositiveButton("Sí", (dialog, which) -> {
                    new Thread(() -> ConexionBaseDatos.eliminarCita(cita.getIdCita())).start();
                    citasList.remove(cita);
                    citasAdapter.notifyDataSetChanged();
                })
                .setNegativeButton("No", (dialog, which) -> dialog.dismiss())
                .show();
    }

    private List<String> obtenerNombresServicios(List<Servicio> servicios) {
        List<String> nombres = new ArrayList<>();
        for (Servicio s : servicios) {
            nombres.add(s.getNombreServicio());
        }
        return nombres;
    }

    @Override
    public void onClick(View v, int position) {
        showEditCitaDialog(citasList.get(position));
    }

    @Override
    public void onLongClick(View v, int position) {
        showDeleteCitaDialog(citasList.get(position));
    }

}
