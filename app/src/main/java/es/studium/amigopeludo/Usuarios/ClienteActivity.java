package es.studium.amigopeludo.Usuarios;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CalendarView;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import es.studium.amigopeludo.Citas.Cita;
import es.studium.amigopeludo.Citas.CitasAdapter;
import es.studium.amigopeludo.ConexionBaseDatos;
import es.studium.amigopeludo.ListadoProfesionales.ListadoProfesionalesActivity;
import es.studium.amigopeludo.MainActivity;
import es.studium.amigopeludo.R;
import es.studium.amigopeludo.RecyclerViewOnItemClickListener;
import es.studium.amigopeludo.Servicios.Servicio;

public class ClienteActivity extends AppCompatActivity implements RecyclerViewOnItemClickListener {

    private RecyclerView recyclerView;
    private CalendarView calendarView;
    private CitasAdapter citasAdapter;
    private List<Cita> citasList = new ArrayList<>();
    private List<Cita> todasLasCitas = new ArrayList<>();
    private List<Servicio> serviciosList = new ArrayList<>();
    private int idCliente;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cliente);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle("Mis Citas");
        }

        idCliente = getIntent().getIntExtra("idUsuario", -1);

        recyclerView = findViewById(R.id.recyclerViewCitas);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        citasAdapter = new CitasAdapter(citasList, this);
        recyclerView.setAdapter(citasAdapter);

        calendarView = findViewById(R.id.calendarView);
        calendarView.setOnDateChangeListener((view, year, month, dayOfMonth) -> {
            String fechaSeleccionada = String.format(Locale.getDefault(), "%04d-%02d-%02d", year, month + 1, dayOfMonth);
            filtrarCitasPorFecha(fechaSeleccionada);
        });

        Button btnNuevaCita = findViewById(R.id.btnNuevaCita);
        btnNuevaCita.setOnClickListener(v -> cargarServiciosYMostrarDialogo());

        Button btnVerProfesionales = findViewById(R.id.btnVerProfesionales);
        btnVerProfesionales.setOnClickListener(v -> {
            Intent intent = new Intent(this, ListadoProfesionalesActivity.class);
            startActivity(intent);
        });


        cargarCitasCliente();
    }

    private void cargarCitasCliente() {
        new Thread(() -> {
            ArrayList<Cita> citas = ConexionBaseDatos.consultarCitas(idCliente);
            runOnUiThread(() -> {
                todasLasCitas.clear();
                todasLasCitas.addAll(citas);
                filtrarCitasPorFecha(obtenerFechaActual());
            });
        }).start();
    }

    private void filtrarCitasPorFecha(String fecha) {
        List<Cita> filtradas = new ArrayList<>();
        for (Cita cita : todasLasCitas) {
            if (cita.getFecha().equals(fecha)) {
                filtradas.add(cita);
            }
        }
        filtradas.sort((c1, c2) -> c1.getHora().compareTo(c2.getHora()));
        citasList.clear();
        citasList.addAll(filtradas);
        citasAdapter.notifyDataSetChanged();
    }

    private String obtenerFechaActual() {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
        return sdf.format(new Date());
    }

    private void cargarServiciosYMostrarDialogo() {
        new Thread(() -> {
            List<Servicio> servicios = ConexionBaseDatos.consultarTodosLosServicios();
            runOnUiThread(() -> {
                if (servicios != null && !servicios.isEmpty()) {
                    serviciosList = servicios;
                    showNewCitaDialog();
                } else {
                    Toast.makeText(this, "No hay servicios disponibles", Toast.LENGTH_SHORT).show();
                }
            });
        }).start();
    }

    private void showNewCitaDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        View view = getLayoutInflater().inflate(R.layout.dialog_nueva_cita, null);
        builder.setView(view);

        Spinner spinnerProfesionales = view.findViewById(R.id.spinnerProfesionales);
        Spinner spinnerServicios = view.findViewById(R.id.spinnerServicios);
        EditText edtFecha = view.findViewById(R.id.edtFecha);
        EditText edtHora = view.findViewById(R.id.edtHora);

        edtFecha.setOnClickListener(v -> showDatePicker(edtFecha));

        edtHora.setOnClickListener(v -> {
            String fecha = edtFecha.getText().toString();
            int pos = spinnerServicios.getSelectedItemPosition();
            if (fecha.isEmpty()) {
                Toast.makeText(this, "Selecciona una fecha", Toast.LENGTH_SHORT).show();
                return;
            }
            if (pos <= 0) {
                Toast.makeText(this, "Selecciona un servicio", Toast.LENGTH_SHORT).show();
                return;
            }
            Servicio servicio = serviciosList.get(pos - 1); // offset por "elige un servicio"
            showFranjaHorariaDialog(edtHora, fecha, servicio.getIdServicio());
        });

        new Thread(() -> {
            List<String> nombresProfesionales = ConexionBaseDatos.obtenerNombresProfesionales();
            nombresProfesionales.add(0, "Elige un profesional");
            runOnUiThread(() -> {
                ArrayAdapter<String> adapterProfesionales = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_dropdown_item, nombresProfesionales) {
                    @Override
                    public boolean isEnabled(int position) { return position != 0; }

                    @Override
                    public View getDropDownView(int position, View convertView, ViewGroup parent) {
                        View view = super.getDropDownView(position, convertView, parent);
                        TextView tv = (TextView) view;
                        tv.setTextColor(position == 0 ? Color.GRAY : Color.BLACK);
                        return view;
                    }
                };
                spinnerProfesionales.setAdapter(adapterProfesionales);
            });
        }).start();

        // Cargar servicios por profesional
        spinnerProfesionales.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (position == 0) {
                    List<String> vacio = new ArrayList<>();
                    vacio.add("Elige un servicio");
                    spinnerServicios.setAdapter(new ArrayAdapter<>(ClienteActivity.this, android.R.layout.simple_spinner_dropdown_item, vacio));
                    return;
                }

                String nombreProfesional = parent.getItemAtPosition(position).toString();
                new Thread(() -> {
                    serviciosList = ConexionBaseDatos.obtenerServiciosPorNombreProfesional(nombreProfesional);
                    List<String> nombresServicios = obtenerNombresServicios(serviciosList);
                    nombresServicios.add(0, "Elige un servicio");

                    runOnUiThread(() -> {
                        ArrayAdapter<String> adapterServicios = new ArrayAdapter<String>(ClienteActivity.this, android.R.layout.simple_spinner_dropdown_item, nombresServicios) {
                            @Override
                            public boolean isEnabled(int pos) { return pos != 0; }

                            @Override
                            public View getDropDownView(int pos, View convertView, ViewGroup parent) {
                                View view = super.getDropDownView(pos, convertView, parent);
                                TextView tv = (TextView) view;
                                tv.setTextColor(pos == 0 ? Color.GRAY : Color.BLACK);
                                return view;
                            }
                        };
                        spinnerServicios.setAdapter(adapterServicios);
                    });
                }).start();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {}
        });

        builder.setTitle("Nueva Cita");
        builder.setNegativeButton("Cancelar", (dialog, which) -> dialog.dismiss());

        AlertDialog dialog = builder.create();
        dialog.setButton(AlertDialog.BUTTON_POSITIVE, "Agregar", (d, which) -> {});
        dialog.show();

        dialog.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener(v -> {
            String fecha = edtFecha.getText().toString().trim();
            String hora = edtHora.getText().toString().trim();
            int posProfesional = spinnerProfesionales.getSelectedItemPosition();
            int posServicio = spinnerServicios.getSelectedItemPosition();

            if (posProfesional == 0 || posServicio == 0 || fecha.isEmpty() || hora.isEmpty()) {
                Toast.makeText(this, "Completa todos los campos", Toast.LENGTH_SHORT).show();
                return;
            }

            Servicio servicioSeleccionado = serviciosList.get(posServicio - 1);
            Cita nuevaCita = new Cita(0, fecha, hora, "pendiente", idCliente, servicioSeleccionado.getIdServicio());

            new Thread(() -> {
                boolean disponible = ConexionBaseDatos.verificarDisponibilidad(fecha, hora, servicioSeleccionado.getIdServicio());
                runOnUiThread(() -> {
                    if (!disponible) {
                        Toast.makeText(this, "Franja no disponible", Toast.LENGTH_SHORT).show();
                    } else {
                        new Thread(() -> {
                            int resultado = ConexionBaseDatos.altaCita(nuevaCita);
                            runOnUiThread(() -> {
                                if (resultado == 201) {
                                    Toast.makeText(this, "Cita añadida correctamente", Toast.LENGTH_SHORT).show();
                                    cargarCitasCliente();
                                    dialog.dismiss();
                                } else {
                                    Toast.makeText(this, "Error al crear la cita", Toast.LENGTH_SHORT).show();
                                }
                            });
                        }).start();
                    }
                });
            }).start();
        });
    }

    private void showEditCitaDialog(Cita cita) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        View view = getLayoutInflater().inflate(R.layout.dialog_editar_cita, null);
        builder.setView(view);
        builder.setTitle("Editar Cita");

        EditText edtFecha = view.findViewById(R.id.edtFecha);
        EditText edtHora = view.findViewById(R.id.edtHora);
        Spinner spinnerServicios = view.findViewById(R.id.spinnerServicios);
        TextView txtNombreProfesional = view.findViewById(R.id.txtNombreProfesional);

        edtFecha.setText(cita.getFecha());
        edtHora.setText(cita.getHora());
        txtNombreProfesional.setText("Profesional: " + cita.getNombreProfesional());

        edtFecha.setOnClickListener(v -> showDatePicker(edtFecha));

        // ⏰ Abrir selector de franjas horarias disponibles
        edtHora.setOnClickListener(v -> {
            String fecha = edtFecha.getText().toString().trim();
            int pos = spinnerServicios.getSelectedItemPosition();
            if (fecha.isEmpty()) {
                Toast.makeText(this, "Selecciona una fecha", Toast.LENGTH_SHORT).show();
                return;
            }
            if (pos < 0 || pos >= serviciosList.size()) {
                Toast.makeText(this, "Selecciona un servicio", Toast.LENGTH_SHORT).show();
                return;
            }
            Servicio servicio = serviciosList.get(pos);
            showFranjaHorariaDialog(edtHora, fecha, servicio.getIdServicio());
        });

        // Cargar servicios
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, obtenerNombresServicios(serviciosList));
        spinnerServicios.setAdapter(adapter);

        for (int i = 0; i < serviciosList.size(); i++) {
            if (serviciosList.get(i).getIdServicio() == cita.getIdServicio()) {
                spinnerServicios.setSelection(i);
                break;
            }
        }

        builder.setPositiveButton("Guardar", (dialog, which) -> {
            cita.setFecha(edtFecha.getText().toString());
            cita.setHora(edtHora.getText().toString());

            int pos = spinnerServicios.getSelectedItemPosition();
            Servicio servicioSeleccionado = serviciosList.get(pos);
            cita.setIdServicio(servicioSeleccionado.getIdServicio());

            new Thread(() -> {
                boolean disponible = ConexionBaseDatos.verificarDisponibilidad(
                        cita.getFecha(), cita.getHora(), cita.getIdServicio(), cita.getIdCita()
                );

                runOnUiThread(() -> {
                    if (!disponible) {
                        Toast.makeText(this, "Ya hay una cita a esa hora", Toast.LENGTH_SHORT).show();
                    } else {
                        new Thread(() -> {
                            int resultado = ConexionBaseDatos.modificarCita(cita);
                            runOnUiThread(() -> {
                                if (resultado == 200) {
                                    Toast.makeText(this, "Cita actualizada", Toast.LENGTH_SHORT).show();
                                    cargarCitasCliente();
                                } else {
                                    Toast.makeText(this, "Error al actualizar cita", Toast.LENGTH_SHORT).show();
                                }
                            });
                        }).start();
                    }
                });
            }).start();
        });

        builder.setNegativeButton("Cancelar", (dialog, which) -> dialog.dismiss());
        builder.show();
    }

    private void showDatePicker(EditText edtFecha) {
        final Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int day = calendar.get(Calendar.DAY_OF_MONTH);

        DatePickerDialog datePickerDialog = new DatePickerDialog(this,
                (view, year1, monthOfYear, dayOfMonth) -> {
                    String selectedDate = String.format(Locale.getDefault(), "%04d-%02d-%02d", year1, monthOfYear + 1, dayOfMonth);
                    edtFecha.setText(selectedDate);
                }, year, month, day);
        datePickerDialog.show();
    }

    private void showFranjaHorariaDialog(EditText edtHora, String fecha, int idServicio) {
        List<String> franjas = new ArrayList<>();
        List<String> disponibles = new ArrayList<>();

        // Generar franjas de 09:00 a 21:00
        for (int hora = 9; hora < 21; hora++) {
            String inicio = String.format("%02d:00", hora);
            String fin = String.format("%02d:00", hora + 1);
            franjas.add(inicio + " - " + fin);
        }

        new Thread(() -> {
            List<String> ocupadas = ConexionBaseDatos.obtenerHorasOcupadas(fecha, idServicio); // Nueva función

            for (String franja : franjas) {
                String horaInicio = franja.split(" - ")[0];
                if (!ocupadas.contains(horaInicio)) {
                    disponibles.add(franja);
                }
            }

            runOnUiThread(() -> {
                if (disponibles.isEmpty()) {
                    Toast.makeText(this, "No hay franjas disponibles", Toast.LENGTH_SHORT).show();
                    return;
                }

                new AlertDialog.Builder(this)
                        .setTitle("Selecciona una franja horaria")
                        .setItems(disponibles.toArray(new String[0]), (dialog, which) -> {
                            String seleccion = disponibles.get(which);
                            String horaInicio = seleccion.split(" - ")[0];
                            edtHora.setText(horaInicio);
                        })
                        .show();
            });
        }).start();
    }


    private void cargarServiciosYMostrarDialogoEdit(Cita cita) {
        new Thread(() -> {
            List<Servicio> servicios = ConexionBaseDatos.consultarTodosLosServicios();
            runOnUiThread(() -> {
                if (servicios != null && !servicios.isEmpty()) {
                    serviciosList = servicios;
                    showEditCitaDialog(cita);
                } else {
                    Toast.makeText(this, "No se pueden cargar los servicios", Toast.LENGTH_SHORT).show();
                }
            });
        }).start();
    }

    private void showDeleteCitaDialog(Cita cita) {
        new AlertDialog.Builder(this)
                .setTitle("Eliminar Cita")
                .setMessage("¿Estás seguro de que deseas eliminar esta cita?")
                .setPositiveButton("Sí", (dialog, which) -> {
                    new Thread(() -> ConexionBaseDatos.eliminarCita(cita.getIdCita())).start();
                    citasList.remove(cita);
                    citasAdapter.notifyDataSetChanged();
                })
                .setNegativeButton("No", (dialog, which) -> dialog.dismiss())
                .show();
    }

    private List<String> obtenerNombresServicios(List<Servicio> servicios) {
        List<String> nombres = new ArrayList<>();
        for (Servicio s : servicios) {
            nombres.add(s.getNombreServicio());
        }
        return nombres;
    }

    @Override
    public void onClick(View v, int position) {
        Cita citaSeleccionada = citasList.get(position);
        cargarServiciosYMostrarDialogoEdit(citaSeleccionada);
    }

    @Override
    public void onLongClick(View v, int position) {
        showDeleteCitaDialog(citasList.get(position));
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_toolbar, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.action_logout) {
            new AlertDialog.Builder(this)
                    .setTitle("Cerrar sesión")
                    .setMessage("¿Estás seguro de que deseas cerrar sesión?")
                    .setPositiveButton("Sí", (dialog, which) -> {
                        getSharedPreferences("amigopeludo", MODE_PRIVATE).edit().clear().apply();
                        Intent intent = new Intent(this, MainActivity.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        startActivity(intent);
                        finish();
                    })
                    .setNegativeButton("No", (dialog, which) -> dialog.dismiss())
                    .show();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

}