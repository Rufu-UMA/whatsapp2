package com.example.whatsapp2.activities;

import android.content.Intent;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.app.AlertDialog;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
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
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class MainActivity extends AppCompatActivity implements PopupFragment.OnCoinUpdateListener {

    private static final int CURRENT_USER_ID = 1; // ID del usuario actual
    private TextView textCoin;
    private OperacionesSaldo operacionesSaldo;
    private ImageButton fabNewMessage;
    
    // Variables para selección de imagen
    private Uri selectedImageUri;
    private ImageView dialogImageView;
    private ActivityResultLauncher<Intent> imagePickerLauncher;

    // ExecutorService único para esta actividad
    private final ExecutorService executorService = Executors.newSingleThreadExecutor();

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

        // Configurar launcher para seleccionar imagen de galería
        imagePickerLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == RESULT_OK && result.getData() != null) {
                        selectedImageUri = result.getData().getData();
                        if (dialogImageView != null && selectedImageUri != null) {
                            // Persistir permiso de lectura para la URI
                            try {
                                getContentResolver().takePersistableUriPermission(selectedImageUri, Intent.FLAG_GRANT_READ_URI_PERMISSION);
                            } catch (SecurityException e) {
                                e.printStackTrace();
                            }
                            dialogImageView.setImageURI(selectedImageUri);
                        }
                    }
                }
        );

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

        ImageButton buttonSettings = findViewById(R.id.buttonSettings);
        buttonSettings.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PopupFragment popupFragment = new PopupFragment();
                Bundle args = new Bundle();
                args.putBoolean("isLanguagePopup", true);
                popupFragment.setArguments(args);
                popupFragment.show(getSupportFragmentManager(), "language_popup");
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadUserCoins();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (executorService != null && !executorService.isShutdown()) {
            executorService.shutdown();
        }
    }

    public void loadUserCoins() {
        if (textCoin == null) return;
        if (executorService.isShutdown()) return;

        executorService.execute(() -> {
            try {
                Double coins = operacionesSaldo.getCoins(CURRENT_USER_ID);
                if (coins != null) {
                    runOnUiThread(() -> {
                        String coinsText = String.format(Locale.getDefault(), "%.2f $", coins);
                        textCoin.setText(coinsText);
                    });
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
    }

    @Override
    public void onCoinUpdated() {
        loadUserCoins();
    }

    public void setLocale(String lang) {
        Locale myLocale = new Locale(lang);
        Locale.setDefault(myLocale);
        Resources res = getResources();
        Configuration conf = res.getConfiguration();
        conf.setLocale(myLocale);
        res.updateConfiguration(conf, res.getDisplayMetrics());
        recreate();
    }
    private void showAddContactDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        View view = getLayoutInflater().inflate(R.layout.dialog_add_contact, null);

        // Create the dialog
        AlertDialog dialog = builder.setView(view).create();

        // Transparent background for the dialog window so the Nine-Patch shows its shape
        if (dialog.getWindow() != null) {
            dialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        }

        final EditText editName = view.findViewById(R.id.editName);
        dialogImageView = view.findViewById(R.id.imagePreview);
        View selectImageButton = view.findViewById(R.id.buttonSelectImage);
        Button btnCancel = view.findViewById(R.id.btnCancel);
        Button btnAdd = view.findViewById(R.id.btnAdd);

        selectedImageUri = null;

        if (selectImageButton != null) {
            selectImageButton.setOnClickListener(v -> openGallery());
        }

        // Handle Cancel Button
        btnCancel.setOnClickListener(v -> dialog.dismiss());

        // Handle Add Button
        btnAdd.setOnClickListener(v -> {
            String name = editName.getText().toString();
            String photoUrl = (selectedImageUri != null) ? selectedImageUri.toString() : "";

            if (!name.isEmpty()) {
                addNewContact(name, photoUrl);
                dialog.dismiss();
            } else {
                Toast.makeText(this, R.string.hint_name, Toast.LENGTH_SHORT).show();
            }
        });

        dialog.show();
    }
    
    private void openGallery() {
        Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        intent.setType("image/*");
        imagePickerLauncher.launch(intent);
    }

    private void addNewContact(String name, String photoUrl) {
        if (executorService.isShutdown()) return;
        
        executorService.execute(() -> {
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
