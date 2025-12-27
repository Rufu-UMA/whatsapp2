package com.example.whatsapp2.database.entities;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "usuarios")
public class Usuario {
    @PrimaryKey(autoGenerate = true)
    public int id;
    public String nombre;
    public double monedas;
    public String fotoPerfil; // URI de la imagen
}
