package es.studium.amigopeludo.Servicios;

import android.view.View;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import es.studium.amigopeludo.R;

public class ServicioViewHolder extends RecyclerView.ViewHolder {
    TextView txtNombre, txtDescripcion, txtImporte;

    public ServicioViewHolder(View itemView) {
        super(itemView);
        txtNombre = itemView.findViewById(R.id.txtNombreServicio);
        txtDescripcion = itemView.findViewById(R.id.txtDescripcionServicio);
        txtImporte = itemView.findViewById(R.id.txtImporteServicio);
    }

    public void bind(Servicio servicio, ServiciosAdapter.OnServicioClickListener listener) {
        txtNombre.setText(servicio.getNombreServicio());
        txtDescripcion.setText(servicio.getDescripcion());
        txtImporte.setText(String.format("%.2f €", servicio.getImporte()));

        itemView.setOnClickListener(v -> {
            if (listener != null) {
                listener.onServicioClick(servicio);
            }
        });

        itemView.setOnLongClickListener(v -> {
            if (listener != null) {
                listener.onServicioLongClick(servicio);
            }
            return true;
        });
    }
}
