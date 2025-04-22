package es.studium.amigopeludo.Servicios;

import android.view.View;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import es.studium.amigopeludo.R;

public class ServicioViewHolder extends RecyclerView.ViewHolder {

    private TextView txtNombre, txtDescripcion, txtImporte;

    public ServicioViewHolder(View itemView) {
        super(itemView);
        txtNombre = itemView.findViewById(R.id.txtNombreServicio);
        txtDescripcion = itemView.findViewById(R.id.txtDescripcionServicio);
        txtImporte = itemView.findViewById(R.id.txtImporteServicio);
    }

    public void bind(final Servicio servicio, final ServiciosAdapter.OnServicioClickListener onServicioClickListener) {
        // Muestra el nombre, descripción y el importe del servicio
        txtNombre.setText(servicio.getNombreServicio());
        txtDescripcion.setText(servicio.getDescripcion());
        txtImporte.setText(String.format("%.2f €", servicio.getImporte()));

        itemView.setOnClickListener(v -> onServicioClickListener.onServicioClick(servicio));
        itemView.setOnLongClickListener(v -> {
            onServicioClickListener.onServicioLongClick(servicio);
            return true; // Indica que la acción se maneja
        });
    }
}

