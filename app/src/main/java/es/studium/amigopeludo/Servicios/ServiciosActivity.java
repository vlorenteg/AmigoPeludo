package es.studium.amigopeludo.Servicios;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

import es.studium.amigopeludo.ConexionBaseDatos;
import es.studium.amigopeludo.MainActivity;
import es.studium.amigopeludo.R;

public class ServiciosActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private ServiciosAdapter serviciosAdapter;
    private List<Servicio> serviciosList = new ArrayList<>();
    private int idProfesional;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_servicios);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle("Mis Servicios");
        }

        idProfesional = getIntent().getIntExtra("idUsuario", -1);

        recyclerView = findViewById(R.id.recyclerViewServicios);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        serviciosAdapter = new ServiciosAdapter(serviciosList, new ServiciosAdapter.OnServicioClickListener() {
            @Override
            public void onServicioClick(Servicio servicio) {
                showEditServicioDialog(servicio);
            }

            @Override
            public void onServicioLongClick(Servicio servicio) {
                showDeleteServicioDialog(servicio);
            }
        });

        recyclerView.setAdapter(serviciosAdapter);

        Button btnNuevoServicio = findViewById(R.id.btnNuevoServicio);
        btnNuevoServicio.setOnClickListener(v -> showNewServicioDialog());

        cargarServicios();
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

    private void cargarServicios() {
        new Thread(() -> {
            List<Servicio> servicios = ConexionBaseDatos.consultarServicios(idProfesional);
            runOnUiThread(() -> {
                serviciosList.clear();
                serviciosList.addAll(servicios);
                serviciosAdapter.notifyDataSetChanged();
            });
        }).start();
    }

    private void showNewServicioDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        View view = getLayoutInflater().inflate(R.layout.dialog_nuevo_servicio, null);
        builder.setView(view);
        builder.setTitle("Nuevo Servicio");

        AlertDialog dialog = builder.create();
        dialog.show();

        EditText edtNombre = view.findViewById(R.id.edtNombreServicio);
        EditText edtDescripcion = view.findViewById(R.id.edtDescripcionServicio);
        EditText edtImporte = view.findViewById(R.id.edtImporteServicio);
        Button btnAceptar = view.findViewById(R.id.btnAceptarServicio);
        Button btnCancelar = view.findViewById(R.id.btnCancelarServicio);

        btnCancelar.setOnClickListener(v -> dialog.dismiss());

        btnAceptar.setOnClickListener(v -> {
            String nombre = edtNombre.getText().toString().trim();
            String descripcion = edtDescripcion.getText().toString().trim();
            String importeStr = edtImporte.getText().toString().trim();

            if (nombre.isEmpty() || descripcion.isEmpty() || importeStr.isEmpty()) {
                Toast.makeText(this, "Completa todos los campos", Toast.LENGTH_SHORT).show();
                return;
            }

            double importe;
            try {
                importe = Double.parseDouble(importeStr);
            } catch (NumberFormatException e) {
                Toast.makeText(this, "Importe inválido", Toast.LENGTH_SHORT).show();
                return;
            }

            Servicio nuevoServicio = new Servicio(0, nombre, descripcion, importe);

            new Thread(() -> {
                int resultado = ConexionBaseDatos.altaServicio(nuevoServicio, idProfesional);
                runOnUiThread(() -> {
                    if (resultado == 201) {
                        Toast.makeText(this, "Servicio agregado", Toast.LENGTH_SHORT).show();
                        cargarServicios();
                        dialog.dismiss();
                    } else {
                        Toast.makeText(this, "Error al agregar servicio", Toast.LENGTH_SHORT).show();
                    }
                });
            }).start();
        });
    }

    private void showEditServicioDialog(Servicio servicio) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        View view = getLayoutInflater().inflate(R.layout.dialog_nuevo_servicio, null);
        builder.setView(view);
        builder.setTitle("Editar Servicio");

        EditText edtNombre = view.findViewById(R.id.edtNombreServicio);
        EditText edtDescripcion = view.findViewById(R.id.edtDescripcionServicio);
        EditText edtImporte = view.findViewById(R.id.edtImporteServicio);
        Button btnAceptar = view.findViewById(R.id.btnAceptarServicio);
        Button btnCancelar = view.findViewById(R.id.btnCancelarServicio);

        edtNombre.setText(servicio.getNombreServicio());
        edtDescripcion.setText(servicio.getDescripcion());
        edtImporte.setText(String.valueOf(servicio.getImporte()));

        AlertDialog dialog = builder.create();
        dialog.show();

        btnCancelar.setOnClickListener(v -> dialog.dismiss());

        btnAceptar.setOnClickListener(v -> {
            String nombre = edtNombre.getText().toString().trim();
            String descripcion = edtDescripcion.getText().toString().trim();
            String importeStr = edtImporte.getText().toString().trim();

            if (nombre.isEmpty() || descripcion.isEmpty() || importeStr.isEmpty()) {
                Toast.makeText(this, "Completa todos los campos", Toast.LENGTH_SHORT).show();
                return;
            }

            double importe;
            try {
                importe = Double.parseDouble(importeStr);
            } catch (NumberFormatException e) {
                Toast.makeText(this, "Importe inválido", Toast.LENGTH_SHORT).show();
                return;
            }

            servicio.setNombreServicio(nombre);
            servicio.setDescripcion(descripcion);
            servicio.setImporte(importe);

            new Thread(() -> {
                int resultado = ConexionBaseDatos.modificarServicio(servicio);
                runOnUiThread(() -> {
                    if (resultado == 200) {
                        Toast.makeText(this, "Servicio actualizado", Toast.LENGTH_SHORT).show();
                        cargarServicios();
                        dialog.dismiss();
                    } else {
                        Toast.makeText(this, "Error al actualizar servicio", Toast.LENGTH_SHORT).show();
                    }
                });
            }).start();
        });
    }


    private void showDeleteServicioDialog(Servicio servicio) {
        new AlertDialog.Builder(this)
                .setTitle("Eliminar Servicio")
                .setMessage("¿Estás seguro de que deseas eliminar este servicio?")
                .setPositiveButton("Sí", (dialog, which) -> {
                    new Thread(() -> {
                        int resultado = ConexionBaseDatos.eliminarServicio(servicio.getIdServicio());
                        runOnUiThread(() -> {
                            if (resultado == 200) {
                                Toast.makeText(this, "Servicio eliminado", Toast.LENGTH_SHORT).show();
                                cargarServicios();
                            } else {
                                Toast.makeText(this, "Error al eliminar servicio", Toast.LENGTH_SHORT).show();
                            }
                        });
                    }).start();
                })
                .setNegativeButton("No", (dialog, which) -> dialog.dismiss())
                .show();
    }
}
