package com.example.whatsapp2.activities;

import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;
import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.whatsapp2.R;
import com.example.whatsapp2.database.AppBaseDeDatos;
import com.example.whatsapp2.database.entities.Mensaje;
import com.example.whatsapp2.adapters.MessagesAdapter;
import com.example.whatsapp2.utils.Botkit;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ChatActivity extends AppCompatActivity {
    // En este codigo se maneja la pantalla de chat entre el usuario y un contacto seleccionado
    private int contactId;
    private String contactName;
    private EditText editTextMessage;
    private ImageButton buttonSend;
    private ImageButton backButton;
    private RecyclerView recyclerViewMessages;
    private MessagesAdapter adapter;
    private int currentUserId = 1; // Como no hay login, este será el id del usuario actual

    // ExecutorService para manejar hilos en segundo plano de forma eficiente y evitar cierres por exceso de hilos
    private final ExecutorService executorService = Executors.newSingleThreadExecutor();
    private final Handler mainHandler = new Handler(Looper.getMainLooper());

    /**
     * Método que se ejecuta al crear la actividad.
     * Inicializa la interfaz, recupera los datos del contacto y configura el RecyclerView.
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_chat);

        View mainView = findViewById(R.id.main_chat);
        if (mainView != null) {
            ViewCompat.setOnApplyWindowInsetsListener(mainView, (v, insets) -> {
                // We look for both System Bars AND the Keyboard (IME)
                Insets combinedInsets = insets.getInsets(WindowInsetsCompat.Type.systemBars() | WindowInsetsCompat.Type.ime());

                // Apply the padding. The bottom padding will now automatically
                // become the height of the keyboard when it opens.
                v.setPadding(combinedInsets.left, combinedInsets.top, combinedInsets.right, combinedInsets.bottom);

                return WindowInsetsCompat.CONSUMED;
            });
        }
        
        contactId = getIntent().getIntExtra("CONTACT_ID", -1);
        contactName = getIntent().getStringExtra("CONTACT_NAME");

        TextView nombre = findViewById(R.id.contactName);
        if (nombre != null) {
            nombre.setText(contactName);
        }

        editTextMessage = findViewById(R.id.editTextMessage);
        buttonSend = findViewById(R.id.buttonSend);
        backButton = findViewById(R.id.backButton);
        recyclerViewMessages = findViewById(R.id.recyclerViewMessages);
        if (recyclerViewMessages != null) {
            recyclerViewMessages.setLayoutManager(new LinearLayoutManager(this));
        }

        loadMessages();

        if (buttonSend != null) {
            buttonSend.setOnClickListener(v -> sendMessage());
        }

        if (backButton != null) {
            backButton.setOnClickListener(v -> finish());
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        // Liberar recursos del ExecutorService al cerrar la actividad
        if (executorService != null && !executorService.isShutdown()) {
            executorService.shutdown();
        }
    }

    /**
     * Carga los mensajes desde la base de datos en un hilo secundario
     * y actualiza la lista en el hilo principal.
     */
    private void loadMessages() {
        if (executorService.isShutdown()) return;

        executorService.execute(() -> {
            try {
                List<Mensaje> messages = AppBaseDeDatos.getDatabase(this).chatDao().getMessages(currentUserId, contactId);
                runOnUiThread(() -> {
                    if (recyclerViewMessages != null) {
                        adapter = new MessagesAdapter(messages, currentUserId);
                        recyclerViewMessages.setAdapter(adapter);
                        if (!messages.isEmpty()) {
                            recyclerViewMessages.scrollToPosition(messages.size() - 1);
                        }
                    }
                });
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
    }

    /**
     * Envía un mensaje si el campo de texto no está vacío.
     * Verifica el saldo (costo 0.80) y muestra un error si es insuficiente.
     */
    private void sendMessage() {
        if (editTextMessage == null) return;
        String content = editTextMessage.getText().toString();
        if (!content.isEmpty()) {
            Mensaje mensaje = new Mensaje();
            mensaje.usuarioId = currentUserId;
            mensaje.contactoId = contactId;
            mensaje.contenido = content;
            mensaje.HoraEnvio = System.currentTimeMillis();

            if (executorService.isShutdown()) return;

            executorService.execute(() -> {
                try {
                    // Verificar saldo antes de enviar (para depuración)
                    Double saldoAntes = AppBaseDeDatos.getDatabase(this).chatDao().getUserBalance(currentUserId);
                    Log.d("ChatActivity", "Saldo antes de enviar: " + saldoAntes + " para usuario ID: " + currentUserId);

                    // Intentamos enviar el mensaje con un costo de 0.80 monedas
                    boolean enviado = AppBaseDeDatos.getDatabase(this).chatDao().enviarMensajeConSaldo(mensaje, 0.80);

                    Double saldoDespues = AppBaseDeDatos.getDatabase(this).chatDao().getUserBalance(currentUserId);
                    Log.d("ChatActivity", "Saldo después de enviar: " + saldoDespues + " | Enviado: " + enviado);

                    runOnUiThread(() -> {
                        if (enviado) {
                            // Si se envió correctamente, limpiamos el campo y recargamos
                            editTextMessage.setText("");
                            loadMessages();
                            // Simular respuesta del contacto
                            simulateResponse(content);
                        } else {
                            // Si falló (saldo insuficiente), mostramos un mensaje de error
                            Toast.makeText(ChatActivity.this, "Error: Saldo insuficiente. Necesitas 0.80 monedas.", Toast.LENGTH_LONG).show();
                        }
                    });
                } catch (Exception e) {
                    e.printStackTrace();
                    runOnUiThread(() -> Toast.makeText(ChatActivity.this, "Error al enviar mensaje", Toast.LENGTH_SHORT).show());
                }
            });
        } else {
            Toast.makeText(this, "Por favor, escribe un mensaje.", Toast.LENGTH_SHORT).show(); // Se muestra una ventana de error
        }
    }

    private void simulateResponse(String userMessage) {
        if (mainHandler == null) return;
        mainHandler.postDelayed(() -> {
            if (executorService.isShutdown()) return;

            executorService.execute(() -> {
                try {
                    String botReply = Botkit.getResponse(userMessage);
                    if (botReply.contains("Sahi h")) {
                         botReply = "Interesante..."; 
                    }
                    
                    Mensaje response = new Mensaje();
                    response.usuarioId = contactId; // El remitente es el contacto
                    response.contactoId = currentUserId; // El destinatario es el usuario actual
                    response.contenido = botReply;
                    response.HoraEnvio = System.currentTimeMillis();
                    
                    // Insertar mensaje de respuesta (sin costo para el usuario)
                    AppBaseDeDatos.getDatabase(this).chatDao().insertMensaje(response);
                    
                    runOnUiThread(this::loadMessages);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            });
        }, 1000); // Retraso de 1 segundo para simular "escribiendo..."
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
