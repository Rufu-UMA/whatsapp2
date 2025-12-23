package com.example.whatsapp2.activities;

import android.os.Bundle;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.whatsapp2.R;
import com.example.whatsapp2.database.AppBaseDeDatos;
import com.example.whatsapp2.database.entities.Usuario;
import com.example.whatsapp2.fragments.ContactsFragment;

import java.util.Locale;
import java.util.concurrent.Executors;

public class MainActivity extends AppCompatActivity {

    private static final int CURRENT_USER_ID = 1; // ID del usuario actual
    private TextView textCoin;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        if (savedInstanceState == null) { // Si no hay estado guardado, se carga el fragmento de contactos
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.fragmentContainerView, new ContactsFragment())
                    .commit();
        }

        // Inicializar el TextView de las monedas
        textCoin = findViewById(R.id.textCoin);

        // Cargar las monedas del usuario
        loadUserCoins();
    }

    @Override
    protected void onResume() {
        super.onResume();
        // Actualizar las monedas cada vez que se vuelve a la activity
        loadUserCoins();
    }

    /**
     * Carga las monedas del usuario actual desde la base de datos y actualiza el TextView
     */
    private void loadUserCoins() {
        Executors.newSingleThreadExecutor().execute(() -> {
            Usuario usuario = AppBaseDeDatos.getDatabase(this).chatDao().getUserById(CURRENT_USER_ID);
            if (usuario != null) {
                runOnUiThread(() -> {
                    // Formatear las monedas (sin decimales si es número entero)
                    String coinsText;
                    if (usuario.monedas == (long) usuario.monedas) {
                        coinsText = String.valueOf((long) usuario.monedas);
                    } else {
                        coinsText = String.format(Locale.getDefault(), "%.2f", usuario.monedas);
                    }
                    textCoin.setText(coinsText);
                });
            }
        });
    }
}