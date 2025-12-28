package com.example.whatsapp2.database;

import android.content.Context;
import androidx.annotation.NonNull;
import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.sqlite.db.SupportSQLiteDatabase;
import java.util.concurrent.Executors;
import com.example.whatsapp2.database.dao.ChatDao;
import com.example.whatsapp2.database.entities.Mensaje;
import com.example.whatsapp2.database.entities.Usuario;

@Database(entities = {Usuario.class, Mensaje.class}, version = 4)
public abstract class AppBaseDeDatos extends RoomDatabase {
    // volatile asegura que INSTANCE sea visible inmediatamente para todos los hilos
    private static volatile AppBaseDeDatos INSTANCE;
    public abstract ChatDao chatDao();

    public static AppBaseDeDatos getDatabase(final Context context) {
        if (INSTANCE == null) {
            synchronized (AppBaseDeDatos.class) {
                if (INSTANCE == null) {
                    INSTANCE = Room.databaseBuilder(context.getApplicationContext(),
                            AppBaseDeDatos.class, "whatsapp_database")
                            .fallbackToDestructiveMigration()
                            .addCallback(new RoomDatabase.Callback() {
                                @Override
                                public void onCreate(@NonNull SupportSQLiteDatabase db) {
                                    super.onCreate(db);

                                    // Usamos un hilo separado para poblar la BD inicial
                                    Executors.newSingleThreadExecutor().execute(() -> {
                                        // Usuario actual (ID 1)
                                        Usuario miUsuario = new Usuario();
                                        miUsuario.nombre = "Yo";
                                        miUsuario.monedas = 1.0;
                                        // Validar que no sea nulo antes de insertar (por seguridad en callbacks asíncronos)
                                        if (INSTANCE != null) {
                                            INSTANCE.chatDao().insertUsuario(miUsuario);
                                        }

                                        /* 
                                        // Contactos de prueba (Descomentar si se quieren datos iniciales)
                                        Usuario user1 = new Usuario();
                                        user1.nombre = "Agustín Valverde Ramos";
                                        user1.monedas = 5000;
                                        if (INSTANCE != null) INSTANCE.chatDao().insertUsuario(user1);

                                        Usuario user2 = new Usuario();
                                        user2.nombre = "Eduardo Medina Cano";
                                        user2.monedas = 5;
                                        if (INSTANCE != null) INSTANCE.chatDao().insertUsuario(user2);
                                        */
                                    });
                                }
                            })
                            .build();
                }
            }
        }
        return INSTANCE;
    }
}
