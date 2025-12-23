package com.example.whatsapp2.database.entities;

import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.PrimaryKey;

@Entity(tableName = "mensajes",
                foreignKeys = @ForeignKey(entity = Usuario.class,
                parentColumns = "id",
                childColumns = "usuarioId",
                onDelete = ForeignKey.CASCADE))
public class Mensaje {
    @PrimaryKey(autoGenerate = true)
    public int id;
    public int usuarioId;     // Remitente
    public int contactoId;  // Destinatario (Chat)
    public String contenido;
    public long HoraEnvio;
}
