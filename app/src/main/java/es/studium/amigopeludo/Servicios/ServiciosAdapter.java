package es.studium.amigopeludo.Servicios;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import es.studium.amigopeludo.R;

public class ServiciosAdapter extends RecyclerView.Adapter<ServicioViewHolder> {

    private List<Servicio> serviciosList;
    private OnServicioClickListener onServicioClickListener;

    // Interfaz para manejar los clics de los servicios
    public interface OnServicioClickListener {
        void onServicioClick(Servicio servicio);
        void onServicioLongClick(Servicio servicio);
    }

    // Constructor
    public ServiciosAdapter(List<Servicio> serviciosList, OnServicioClickListener onServicioClickListener) {
        this.serviciosList = serviciosList;
        this.onServicioClickListener = onServicioClickListener;
    }

    @Override
    public ServicioViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_servicio, parent, false);
        return new ServicioViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ServicioViewHolder holder, int position) {
        Servicio servicio = serviciosList.get(position);
        holder.bind(servicio, onServicioClickListener); // Pasamos el listener a bind
    }

    @Override
    public int getItemCount() {
        return serviciosList.size();
    }
}
