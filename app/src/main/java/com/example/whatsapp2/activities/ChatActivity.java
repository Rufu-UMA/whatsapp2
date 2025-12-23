package com.example.whatsapp2.activities;

import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.whatsapp2.R;
import com.example.whatsapp2.database.AppBaseDeDatos;
import com.example.whatsapp2.database.entities.Mensaje;
import com.example.whatsapp2.adapters.MessagesAdapter;
import java.util.List;
import java.util.concurrent.Executors;

public class ChatActivity extends AppCompatActivity {
    // En este codigo se maneja la pantalla de chat entre el usuario y un contacto seleccionado
    private int contactId;
    private String contactName;
    private EditText editTextMessage;
    private Button buttonSend;
    private RecyclerView recyclerViewMessages;
    private MessagesAdapter adapter;
    private int currentUserId = 1; // Como no hay login, este será el id del usuario actual

    /**
     * Método que se ejecuta al crear la actividad.
     * Inicializa la interfaz, recupera los datos del contacto y configura el RecyclerView.
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        contactId = getIntent().getIntExtra("CONTACT_ID", -1);
        contactName = getIntent().getStringExtra("CONTACT_NAME");

        Toolbar toolbar = findViewById(R.id.toolbarChat);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle(contactName);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        editTextMessage = findViewById(R.id.editTextMessage);
        buttonSend = findViewById(R.id.buttonSend);
        recyclerViewMessages = findViewById(R.id.recyclerViewMessages);
        recyclerViewMessages.setLayoutManager(new LinearLayoutManager(this));

        loadMessages();

        buttonSend.setOnClickListener(v -> sendMessage());
    }

    /**
     * Carga los mensajes desde la base de datos en un hilo secundario
     * y actualiza la lista en el hilo principal.
     */
    private void loadMessages() {
        Executors.newSingleThreadExecutor().execute(() -> {
            List<Mensaje> messages = AppBaseDeDatos.getDatabase(this).chatDao().getMessages(currentUserId, contactId);
            runOnUiThread(() -> {
                adapter = new MessagesAdapter(messages, currentUserId);
                recyclerViewMessages.setAdapter(adapter);
                if (!messages.isEmpty()) {
                    recyclerViewMessages.scrollToPosition(messages.size() - 1);
                }
            });
        });
    }

    /**
     * Envía un mensaje si el campo de texto no está vacío.
     * Verifica el saldo (costo 0.80) y muestra un error si es insuficiente.
     */
    private void sendMessage() {
        String content = editTextMessage.getText().toString();
        if (!content.isEmpty()) {
            Mensaje mensaje = new Mensaje();
            mensaje.usuarioId = currentUserId;
            mensaje.contactoId = contactId;
            mensaje.contenido = content;
            mensaje.HoraEnvio = System.currentTimeMillis();

            Executors.newSingleThreadExecutor().execute(() -> {
                // Intentamos enviar el mensaje con un costo de 0.80 monedas
                boolean enviado = AppBaseDeDatos.getDatabase(this).chatDao().enviarMensajeConSaldo(mensaje, 0.80);

                runOnUiThread(() -> {
                    if (enviado) {
                        // Si se envió correctamente, limpiamos el campo y recargamos
                        editTextMessage.setText("");
                        loadMessages();
                    } else {
                        // Si falló (saldo insuficiente), mostramos un mensaje de error
                        Toast.makeText(ChatActivity.this, "Error: Saldo insuficiente. Necesitas 0.80 monedas.", Toast.LENGTH_LONG).show();
                    }
                });
            });
        } else {
            Toast.makeText(this, "Por favor, escribe un mensaje.", Toast.LENGTH_SHORT).show(); // Se muestra una ventana de error
        }
    }

    /**
     * Maneja la acción de navegar hacia atrás (flecha en el toolbar).
     */
    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return true;
    }
}
