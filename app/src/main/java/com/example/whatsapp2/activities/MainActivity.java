package com.example.whatsapp2.activities;

import android.content.res.Configuration;
import android.content.res.Resources;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.whatsapp2.R;
import com.example.whatsapp2.api.OperacionesSaldo;
import com.example.whatsapp2.database.AppBaseDeDatos;
import com.example.whatsapp2.fragments.ContactsFragment;
import com.example.whatsapp2.fragments.PopupFragment;

import java.util.Locale;
import java.util.concurrent.Executors;

public class MainActivity extends AppCompatActivity implements PopupFragment.OnCoinUpdateListener {

    private static final int CURRENT_USER_ID = 1; // ID del usuario actual
    private TextView textCoin;
    private OperacionesSaldo operacionesSaldo;

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

    public void setLocale(String lang) {
        Locale myLocale = new Locale(lang);
        Resources res = getResources();
        Configuration conf = res.getConfiguration();
        conf.setLocale(myLocale);
        res.updateConfiguration(conf, res.getDisplayMetrics());
        recreate();
    }
}
