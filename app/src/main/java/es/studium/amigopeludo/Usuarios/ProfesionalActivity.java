package es.studium.amigopeludo.Usuarios;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

import es.studium.amigopeludo.R;
import es.studium.amigopeludo.Citas.CitasAdapter;
import es.studium.amigopeludo.Citas.Cita;
import es.studium.amigopeludo.Servicios.ServiciosActivity;
import es.studium.amigopeludo.RecyclerViewOnItemClickListener;
import es.studium.amigopeludo.ConexionBaseDatos;


public class ProfesionalActivity extends AppCompatActivity implements RecyclerViewOnItemClickListener {

    private RecyclerView recyclerView;
    private CitasAdapter citasAdapter;
    private List<Cita> citasList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profesional);

        recyclerView = findViewById(R.id.recyclerViewCitasProfesional);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        // Obtener citas desde la API
        ConexionBaseDatos.obtenerCitas(new ConexionBaseDatos.ObtenerCitasCallback() {
            @Override
            public void onSuccess(List<Cita> citas) {
                citasList = citas; // Asignamos las citas obtenidas
                citasAdapter = new CitasAdapter(citasList, ProfesionalActivity.this);
                recyclerView.setAdapter(citasAdapter);
            }

            @Override
            public void onError(String mensaje) {
                Toast.makeText(ProfesionalActivity.this, mensaje, Toast.LENGTH_SHORT).show();
            }
        });

        Button btnServicios = findViewById(R.id.btnServicios);
        btnServicios.setOnClickListener(v -> {
            Intent intent = new Intent(ProfesionalActivity.this, ServiciosActivity.class);
            startActivity(intent);
        });
    }

    @Override
    public void onClick(View v, int position) {
        // Manejo del clic corto (por ejemplo, ver detalles de la cita)
        Cita cita = citasList.get(position);
        // Realiza la acción deseada con la cita
    }

    @Override
    public void onLongClick(View v, int position) {
        // Manejo del clic largo (por ejemplo, cancelar la cita)
        Cita cita = citasList.get(position);
        new AlertDialog.Builder(ProfesionalActivity.this)
                .setTitle("Cancelar Cita")
                .setMessage("¿Deseas cancelar esta cita?")
                .setPositiveButton("Sí", (dialog, which) -> {
                    cita.setEstado("Cancelada");
                    citasAdapter.notifyDataSetChanged();
                })
                .setNegativeButton("No", (dialog, which) -> dialog.dismiss())
                .show();
    }
}
