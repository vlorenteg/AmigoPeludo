package es.studium.amigopeludo.Citas;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import es.studium.amigopeludo.R;
import es.studium.amigopeludo.RecyclerViewOnItemClickListener;

public class CitasAdapter extends RecyclerView.Adapter<CitaViewHolder> {

    private List<Cita> citasList;
    private RecyclerViewOnItemClickListener itemClickListener; // Usamos la interfaz genérica

    // Constructor
    public CitasAdapter(List<Cita> citasList, RecyclerViewOnItemClickListener itemClickListener) {
        this.citasList = citasList;
        this.itemClickListener = itemClickListener;
    }

    @Override
    public CitaViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_cita, parent, false);
        return new CitaViewHolder(view);
    }

    @Override
    public void onBindViewHolder(CitaViewHolder holder, int position) {
        Cita cita = citasList.get(position);
        holder.bind(cita, itemClickListener, position); // Pasamos el listener y la posición
    }

    @Override
    public int getItemCount() {
        return citasList.size();
    }
}
