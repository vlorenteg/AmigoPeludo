package es.studium.amigopeludo.Usuarios;

import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

import es.studium.amigopeludo.Citas.Cita;
import es.studium.amigopeludo.Citas.CitasAdapter;
import es.studium.amigopeludo.R;
import es.studium.amigopeludo.Servicios.Servicio;
import es.studium.amigopeludo.RecyclerViewOnItemClickListener;

public class ClienteActivity extends AppCompatActivity implements RecyclerViewOnItemClickListener {

    private RecyclerView recyclerView;
    private CitasAdapter citasAdapter;
    private List<Cita> citasList;
    private List<Servicio> serviciosList;
    private int nextIdCita = 1; // Simulador de ID incremental

    private final int idClienteSimulado = 1; // ID fijo para pruebas

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cliente);

        recyclerView = findViewById(R.id.recyclerViewCitas);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        citasList = new ArrayList<>();
        serviciosList = crearServiciosSimulados();

        citasAdapter = new CitasAdapter(citasList, this);

        recyclerView.setAdapter(citasAdapter);

        Button btnNuevaCita = findViewById(R.id.btnNuevaCita);
        btnNuevaCita.setOnClickListener(v -> showNewCitaDialog());
    }

    private List<Servicio> crearServiciosSimulados() {
        List<Servicio> lista = new ArrayList<>();
        lista.add(new Servicio(1, "Peluquería", "Corte de pelo para perros", 15.0));
        lista.add(new Servicio(2, "Consulta veterinaria", "Consulta médica para mascotas", 25.0));
        lista.add(new Servicio(3, "Vacunación", "Vacunación para perros y gatos", 30.0));
        return lista;
    }

    private void showNewCitaDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(ClienteActivity.this);
        View view = getLayoutInflater().inflate(R.layout.dialog_nueva_cita, null);
        builder.setView(view);

        EditText edtFecha = view.findViewById(R.id.edtFecha);
        Spinner spinnerServicios = view.findViewById(R.id.spinnerServicios);

        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, obtenerNombresServicios(serviciosList));
        spinnerServicios.setAdapter(adapter);

        builder.setPositiveButton("Agregar", (dialog, which) -> {
            String fecha = edtFecha.getText().toString();
            int posicion = spinnerServicios.getSelectedItemPosition();
            Servicio servicioSeleccionado = serviciosList.get(posicion);

            Cita nuevaCita = new Cita(
                    fecha,
                    "pendiente"
            );

            citasList.add(nuevaCita);
            citasAdapter.notifyDataSetChanged();
        });

        builder.setNegativeButton("Cancelar", (dialog, which) -> dialog.dismiss());
        builder.show();
    }

    private void showEditCitaDialog(Cita cita) {
        AlertDialog.Builder builder = new AlertDialog.Builder(ClienteActivity.this);
        View view = getLayoutInflater().inflate(R.layout.dialog_nueva_cita, null);
        builder.setView(view);

        EditText edtFecha = view.findViewById(R.id.edtFecha);
        Spinner spinnerServicios = view.findViewById(R.id.spinnerServicios);

        edtFecha.setText(cita.getFecha());

        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, obtenerNombresServicios(serviciosList));
        spinnerServicios.setAdapter(adapter);

        // Seleccionar el servicio actual
        for (int i = 0; i < serviciosList.size(); i++) {
            if (serviciosList.get(i).getIdServicio() == cita.getIdServicio()) {
                spinnerServicios.setSelection(i);
                break;
            }
        }

        builder.setPositiveButton("Guardar", (dialog, which) -> {
            cita.setFecha(edtFecha.getText().toString());

            int posicion = spinnerServicios.getSelectedItemPosition();
            Servicio servicioSeleccionado = serviciosList.get(posicion);
            cita.setIdServicio(servicioSeleccionado.getIdServicio());

            citasAdapter.notifyDataSetChanged();
        });

        builder.setNegativeButton("Cancelar", (dialog, which) -> dialog.dismiss());
        builder.show();
    }

    private void showDeleteCitaDialog(Cita cita) {
        new AlertDialog.Builder(ClienteActivity.this)
                .setTitle("Eliminar Cita")
                .setMessage("¿Estás seguro de que deseas eliminar esta cita?")
                .setPositiveButton("Sí", (dialog, which) -> {
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

    // Métodos de la interfaz RecyclerViewOnItemClickListener

    @Override
    public void onClick(View v, int position) {
        // Implementa la lógica de lo que quieres hacer cuando se haga clic en una cita
        Cita cita = citasList.get(position);
        showEditCitaDialog(cita);
    }

    @Override
    public void onLongClick(View v, int position) {
        // Implementa la lógica de lo que quieres hacer cuando se haga clic largo en una cita
        Cita cita = citasList.get(position);
        showDeleteCitaDialog(cita);
    }
}
