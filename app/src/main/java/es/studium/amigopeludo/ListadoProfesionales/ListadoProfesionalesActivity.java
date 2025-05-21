package es.studium.amigopeludo.ListadoProfesionales;

import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
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
            getSupportActionBar().setTitle("Profesionales");
        }

        recyclerViewProfesionales = findViewById(R.id.recyclerViewProfesionales);
        recyclerViewProfesionales.setLayoutManager(new LinearLayoutManager(this));

    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }
}
