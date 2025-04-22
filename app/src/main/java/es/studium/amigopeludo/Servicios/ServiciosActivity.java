package es.studium.amigopeludo.Servicios;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

import es.studium.amigopeludo.R;

public class ServiciosActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private ServiciosAdapter serviciosAdapter;
    private List<Servicio> serviciosList;
    private int nextIdServicio = 1;  // ID incremental para servicios

    // En el método onCreate
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_servicios);

        recyclerView = findViewById(R.id.recyclerViewServicios);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        serviciosList = new ArrayList<>();
        // Cambiar estas líneas para usar el idServicio
        serviciosList.add(new Servicio(nextIdServicio++, "Corte de Pelo", "Corte de pelo para perro", 15.0));
        serviciosList.add(new Servicio(nextIdServicio++, "Baño", "Baño completo para perros", 10.0));

        // Cambiar el constructor para usar el listener
        serviciosAdapter = new ServiciosAdapter(serviciosList, new ServiciosAdapter.OnServicioClickListener() {
            @Override
            public void onServicioClick(Servicio servicio) {
                showEditServicioDialog(servicio); // Modificar un servicio
            }

            @Override
            public void onServicioLongClick(Servicio servicio) {
                showDeleteServicioDialog(servicio); // Eliminar un servicio
            }
        });

        recyclerView.setAdapter(serviciosAdapter);

        Button btnNuevoServicio = findViewById(R.id.btnNuevoServicio);
        btnNuevoServicio.setOnClickListener(v -> showNewServicioDialog());
    }

    // Diálogo para agregar un nuevo servicio
    private void showNewServicioDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(ServiciosActivity.this);
        View view = getLayoutInflater().inflate(R.layout.dialog_nuevo_servicio, null);
        builder.setView(view);

        EditText edtNombre = view.findViewById(R.id.edtNombreServicio);
        EditText edtDescripcion = view.findViewById(R.id.edtDescripcionServicio);
        EditText edtImporte = view.findViewById(R.id.edtImporteServicio);

        builder.setPositiveButton("Aceptar", (dialog, which) -> {
            String nombre = edtNombre.getText().toString();
            String descripcion = edtDescripcion.getText().toString();
            double importe = Double.parseDouble(edtImporte.getText().toString());

            // Crear el servicio con el ID incremental
            Servicio nuevoServicio = new Servicio(nextIdServicio++, nombre, descripcion, importe);
            serviciosList.add(nuevoServicio);
            serviciosAdapter.notifyDataSetChanged();
            Toast.makeText(ServiciosActivity.this, "Servicio agregado", Toast.LENGTH_SHORT).show();
        });

        builder.setNegativeButton("Cancelar", (dialog, which) -> dialog.dismiss());

        builder.show();
    }

    // Diálogo para editar un servicio
    private void showEditServicioDialog(Servicio servicio) {
        AlertDialog.Builder builder = new AlertDialog.Builder(ServiciosActivity.this);
        View view = getLayoutInflater().inflate(R.layout.dialog_nuevo_servicio, null);
        builder.setView(view);

        EditText edtNombre = view.findViewById(R.id.edtNombreServicio);
        EditText edtDescripcion = view.findViewById(R.id.edtDescripcionServicio);
        EditText edtImporte = view.findViewById(R.id.edtImporteServicio);

        edtNombre.setText(servicio.getNombreServicio());
        edtDescripcion.setText(servicio.getDescripcion());
        edtImporte.setText(String.valueOf(servicio.getImporte()));

        builder.setPositiveButton("Guardar", (dialog, which) -> {
            servicio.setNombreServicio(edtNombre.getText().toString());
            servicio.setDescripcion(edtDescripcion.getText().toString());
            servicio.setImporte(Double.parseDouble(edtImporte.getText().toString()));
            serviciosAdapter.notifyDataSetChanged();
            Toast.makeText(ServiciosActivity.this, "Servicio modificado", Toast.LENGTH_SHORT).show();
        });

        builder.setNegativeButton("Cancelar", (dialog, which) -> dialog.dismiss());

        builder.show();
    }

    // Diálogo para eliminar un servicio
    private void showDeleteServicioDialog(Servicio servicio) {
        new AlertDialog.Builder(ServiciosActivity.this)
                .setTitle("Eliminar Servicio")
                .setMessage("¿Estás seguro de que deseas eliminar este servicio?")
                .setPositiveButton("Sí", (dialog, which) -> {
                    serviciosList.remove(servicio);
                    serviciosAdapter.notifyDataSetChanged();
                    Toast.makeText(ServiciosActivity.this, "Servicio eliminado", Toast.LENGTH_SHORT).show();
                })
                .setNegativeButton("No", (dialog, which) -> dialog.dismiss())
                .show();
    }
}
