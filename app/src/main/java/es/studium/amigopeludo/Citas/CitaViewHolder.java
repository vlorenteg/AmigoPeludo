package es.studium.amigopeludo.Citas;

import android.view.View;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import es.studium.amigopeludo.R;
import es.studium.amigopeludo.RecyclerViewOnItemClickListener;

public class CitaViewHolder extends RecyclerView.ViewHolder {

    private TextView Fecha;
    private TextView Estado;

    public CitaViewHolder(View itemView) {
        super(itemView);
        Fecha = itemView.findViewById(R.id.fecha);
        Estado = itemView.findViewById(R.id.estado);
    }

    public void bind(final Cita cita, final RecyclerViewOnItemClickListener itemClickListener, final int position) {
        Fecha.setText(cita.getFecha());
        Estado.setText(cita.getEstado());

        // Manejo de clic corto
        itemView.setOnClickListener(v -> itemClickListener.onClick(v, position));

        // Manejo de clic largo
        itemView.setOnLongClickListener(v -> {
            itemClickListener.onLongClick(v, position);
            return true; // Indica que el evento ha sido consumido
        });
    }
}
