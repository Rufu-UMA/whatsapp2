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
@Database(entities = {Usuario.class, Mensaje.class}, version = 3)
public abstract class AppBaseDeDatos extends RoomDatabase {
    private static AppBaseDeDatos INSTANCE;
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
                                    Executors.newSingleThreadExecutor().execute(() -> {
                                        // Usuario actual (ID 1)
                                        Usuario miUsuario = new Usuario();
                                        miUsuario.nombre = "Yo";
                                        miUsuario.monedas = 1.0;
                                        INSTANCE.chatDao().insertUsuario(miUsuario);

                                        // Contactos
                                        Usuario user1 = new Usuario();
                                        user1.nombre = "Agustín Valverde Ramos";
                                        user1.monedas = 5000;
                                        INSTANCE.chatDao().insertUsuario(user1);

                                        Usuario user2 = new Usuario();
                                        user2.nombre = "Eduardo Medina Cano";
                                        user2.monedas = 5;
                                        INSTANCE.chatDao().insertUsuario(user2);
                                    }); // Estos usuarios iniciales se crean al instalar la app por primera vez
                                    // Para borrar la base de datos y que se vuelvan a crear, hay que desinstalar la app
                                }
                            })
                            .build();
                }
            }
        }
        return INSTANCE;
    }

}
