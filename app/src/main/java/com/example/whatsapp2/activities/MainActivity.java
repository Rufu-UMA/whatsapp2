package com.example.whatsapp2.activities;

import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.app.AlertDialog;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.fragment.app.Fragment;

import com.example.whatsapp2.R;
import com.example.whatsapp2.api.OperacionesSaldo;
import com.example.whatsapp2.database.AppBaseDeDatos;
import com.example.whatsapp2.database.entities.Usuario;
import com.example.whatsapp2.fragments.ContactsFragment;
import com.example.whatsapp2.fragments.PopupFragment;

import java.util.Locale;
import java.util.concurrent.Executors;

public class MainActivity extends AppCompatActivity implements PopupFragment.OnCoinUpdateListener {

    private static final int CURRENT_USER_ID = 1; // ID del usuario actual
    private TextView textCoin;
    private OperacionesSaldo operacionesSaldo;
    private ImageButton fabNewMessage;

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

        operacionesSaldo = new OperacionesSaldo(AppBaseDeDatos.getDatabase(this).chatDao());
        textCoin = findViewById(R.id.textCoin);
        
        fabNewMessage = findViewById(R.id.fabNewMessage);
        fabNewMessage.setOnClickListener(v -> showAddContactDialog());

        loadUserCoins();
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadUserCoins();
    }

    public void loadUserCoins() {
        Executors.newSingleThreadExecutor().execute(() -> {
            Double coins = operacionesSaldo.getCoins(CURRENT_USER_ID);
            if (coins != null) {
                runOnUiThread(() -> {
                    String coinsText = String.format(Locale.getDefault(), "%.2f $", coins);
                    textCoin.setText(coinsText);
                });
            }
        });
    }

    @Override
    public void onCoinUpdated() {
        loadUserCoins();
    }

    private void showAddContactDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        View view = getLayoutInflater().inflate(R.layout.dialog_add_contact, null);
        final EditText editName = view.findViewById(R.id.editName);
        final EditText editPhoto = view.findViewById(R.id.editPhotoUrl);

        builder.setView(view)
                .setPositiveButton("Añadir", (dialog, which) -> {
                    String name = editName.getText().toString();
                    String photo = editPhoto.getText().toString();
                    if (!name.isEmpty()) {
                        addNewContact(name, photo);
                    }
                })
                .setNegativeButton("Cancelar", null)
                .create()
                .show();
    }

    private void addNewContact(String name, String photoUrl) {
        Executors.newSingleThreadExecutor().execute(() -> {
            Usuario newUser = new Usuario();
            newUser.nombre = name;
            newUser.fotoPerfil = photoUrl;
            newUser.monedas = 0; // Monedas iniciales para contactos

            AppBaseDeDatos.getDatabase(this).chatDao().insertUsuario(newUser);

            runOnUiThread(() -> {
                Fragment fragment = getSupportFragmentManager().findFragmentById(R.id.fragmentContainerView);
                if (fragment instanceof ContactsFragment) {
                    ((ContactsFragment) fragment).loadContacts();
                }
            });
        });
    }
}
