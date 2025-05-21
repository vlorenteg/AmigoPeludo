package es.studium.amigopeludo.Citas;

import android.view.View;
import android.widget.TextView;
import androidx.recyclerview.widget.RecyclerView;

import es.studium.amigopeludo.R;
import es.studium.amigopeludo.RecyclerViewOnItemClickListener;

public class CitaViewHolder extends RecyclerView.ViewHolder {

    private final TextView Fecha;
    private final TextView Hora;
    private final TextView Estado;
    private final TextView txtServicio;
    private final TextView txtProfesional;

    public CitaViewHolder(View itemView) {
        super(itemView);
        Fecha = itemView.findViewById(R.id.fecha);
        Hora = itemView.findViewById(R.id.hora);
        Estado = itemView.findViewById(R.id.estado);
        txtServicio = itemView.findViewById(R.id.txtServicio);
        txtProfesional = itemView.findViewById(R.id.txtProfesional);
    }

    public void bind(final Cita cita, final RecyclerViewOnItemClickListener itemClickListener, final int position) {
        Fecha.setText("Fecha: " + cita.getFecha());
        Hora.setText("Hora: " + cita.getHora());
        Estado.setText("Estado: " + cita.getEstado());
        txtServicio.setText("Servicio: " + cita.getNombreServicio());
        txtProfesional.setText("Con: " + cita.getNombreProfesional());

        itemView.setOnClickListener(v -> itemClickListener.onClick(v, position));
        itemView.setOnLongClickListener(v -> {
            itemClickListener.onLongClick(v, position);
            return true;
        });
    }
}
