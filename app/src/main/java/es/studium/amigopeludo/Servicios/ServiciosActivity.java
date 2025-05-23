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
            getSupportActionBar().setTitle(getString(R.string.titulo_mis_servicios));
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
        builder.setTitle(getString(R.string.titulo_nuevo_servicio));

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
                Toast.makeText(this, getString(R.string.mensaje_completa_campos), Toast.LENGTH_SHORT).show();
                return;
            }

            double importe;
            try {
                importe = Double.parseDouble(importeStr);
            } catch (NumberFormatException e) {
                Toast.makeText(this, getString(R.string.mensaje_importe_invalido), Toast.LENGTH_SHORT).show();
                return;
            }

            Servicio nuevoServicio = new Servicio(0, nombre, descripcion, importe);

            new Thread(() -> {
                int resultado = ConexionBaseDatos.altaServicio(nuevoServicio, idProfesional);
                runOnUiThread(() -> {
                    if (resultado == 201) {
                        Toast.makeText(this, getString(R.string.mensaje_servicio_agregado), Toast.LENGTH_SHORT).show();
                        cargarServicios();
                        dialog.dismiss();
                    } else {
                        Toast.makeText(this, getString(R.string.mensaje_error_agregar), Toast.LENGTH_SHORT).show();
                    }
                });
            }).start();
        });
    }

    private void showEditServicioDialog(Servicio servicio) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        View view = getLayoutInflater().inflate(R.layout.dialog_nuevo_servicio, null);
        builder.setView(view);
        builder.setTitle(getString(R.string.titulo_editar_servicio));

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
                Toast.makeText(this, getString(R.string.mensaje_completa_campos), Toast.LENGTH_SHORT).show();
                return;
            }

            double importe;
            try {
                importe = Double.parseDouble(importeStr);
            } catch (NumberFormatException e) {
                Toast.makeText(this, getString(R.string.mensaje_importe_invalido), Toast.LENGTH_SHORT).show();
                return;
            }

            servicio.setNombreServicio(nombre);
            servicio.setDescripcion(descripcion);
            servicio.setImporte(importe);

            new Thread(() -> {
                int resultado = ConexionBaseDatos.modificarServicio(servicio);
                runOnUiThread(() -> {
                    if (resultado == 200) {
                        Toast.makeText(this, getString(R.string.mensaje_servicio_actualizado), Toast.LENGTH_SHORT).show();
                        cargarServicios();
                        dialog.dismiss();
                    } else {
                        Toast.makeText(this, getString(R.string.mensaje_error_actualizar), Toast.LENGTH_SHORT).show();
                    }
                });
            }).start();
        });
    }

    private void showDeleteServicioDialog(Servicio servicio) {
        new AlertDialog.Builder(this)
                .setTitle(getString(R.string.titulo_eliminar_servicio))
                .setMessage(getString(R.string.mensaje_eliminar_servicio))
                .setPositiveButton(getString(R.string.accion_si), (dialog, which) -> {
                    new Thread(() -> {
                        int resultado = ConexionBaseDatos.eliminarServicio(servicio.getIdServicio());
                        runOnUiThread(() -> {
                            if (resultado == 200) {
                                Toast.makeText(this, getString(R.string.mensaje_servicio_eliminado), Toast.LENGTH_SHORT).show();
                                cargarServicios();
                            } else {
                                Toast.makeText(this, getString(R.string.mensaje_error_eliminar), Toast.LENGTH_SHORT).show();
                            }
                        });
                    }).start();
                })
                .setNegativeButton(getString(R.string.accion_no), (dialog, which) -> dialog.dismiss())
                .show();
    }
}
