package com.example.whatsapp2.database;

import android.content.Context;
import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import com.example.whatsapp2.database.dao.ChatDao;
import com.example.whatsapp2.database.entities.Mensaje;
import com.example.whatsapp2.database.entities.Usuario;
@Database(entities = {Usuario.class, Mensaje.class}, version = 1)
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
                            .build();
                }
            }
        }
        return INSTANCE;
    }

}
