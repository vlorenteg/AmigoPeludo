package es.studium.amigopeludo.ListadoProfesionales;

import android.os.Bundle;
import android.util.Log;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import es.studium.amigopeludo.ConexionBaseDatos;
import es.studium.amigopeludo.R;

public class ListadoProfesionalesActivity extends AppCompatActivity {

    private RecyclerView recyclerViewProfesionales;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_listadoprofesionales);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle(getString(R.string.titulo_profesionales));
        }

        recyclerViewProfesionales = findViewById(R.id.recyclerViewProfesionales);
        recyclerViewProfesionales.setLayoutManager(new LinearLayoutManager(this));

        cargarListadoProfesionales();
    }

    private void cargarListadoProfesionales() {
        new Thread(() -> {
            List<Profesional> profesionales = ConexionBaseDatos.obtenerListadoProfesionales();
            runOnUiThread(() -> {
                if (profesionales != null && !profesionales.isEmpty()) {
                    ListadoProfesionalesAdapter adapter = new ListadoProfesionalesAdapter(profesionales);
                    recyclerViewProfesionales.setAdapter(adapter);
                } else {
                    Log.e("ListadoProfesionales", getString(R.string.log_no_profesionales));
                }
            });
        }).start();
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }
}
