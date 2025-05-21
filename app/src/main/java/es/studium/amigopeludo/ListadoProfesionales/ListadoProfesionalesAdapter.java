package es.studium.amigopeludo.ListadoProfesionales;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import es.studium.amigopeludo.R;
import es.studium.amigopeludo.ListadoProfesionales.Profesional;

public class ListadoProfesionalesAdapter extends RecyclerView.Adapter<ListadoProfesionalesAdapter.ViewHolder> {

    private final List<Profesional> profesionales;

    public ListadoProfesionalesAdapter(List<Profesional> profesionales) {
        this.profesionales = profesionales;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView txtNombre, txtTelefono, txtEmail;

        public ViewHolder(View itemView) {
            super(itemView);
            txtNombre = itemView.findViewById(R.id.txtNombreProfesional);
            txtTelefono = itemView.findViewById(R.id.txtTelefonoProfesional);
            txtEmail = itemView.findViewById(R.id.txtEmailProfesional);
        }

        public void bind(Profesional profesional) {
            txtNombre.setText(profesional.getNombre());
            txtTelefono.setText("Tel: " + profesional.getTelefono());
            txtEmail.setText("Email: " + profesional.getEmail());
        }
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_listadoprofesionales, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        holder.bind(profesionales.get(position));
    }

    @Override
    public int getItemCount() {
        return profesionales.size();
    }
}

