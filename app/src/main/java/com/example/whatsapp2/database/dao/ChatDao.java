package com.example.whatsapp2.database.dao;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Transaction;
import com.example.whatsapp2.database.entities.Mensaje;
import com.example.whatsapp2.database.entities.Usuario;
import java.util.List;
@Dao
public abstract class ChatDao {
    // Obtener todos los usuarios
    @Query("SELECT * FROM usuarios")
    public abstract List<Usuario> getAllUsers();

    @Insert
    public abstract void insertUsuario(Usuario usuario);

    // Obtener saldo
    @Query("SELECT monedas FROM usuarios WHERE id = :usuarioId")
    public abstract double getUserBalance(int usuarioId);

    // Actualizar saldo
    @Query("UPDATE usuarios SET monedas = :nuevoSaldo WHERE id = :usuarioId")
    public abstract void updateUserBalance(int usuarioId, double nuevoSaldo);

    // Insertar mensaje
    @Insert
    public abstract long insertMensaje(Mensaje mensaje);

    // Obtener mensajes de un chat
    @Query("SELECT * FROM mensajes WHERE (usuarioId = :userId AND contactoId = :contactId) OR (usuarioId = :contactId AND contactoId = :userId) ORDER BY HoraEnvio ASC")
    public abstract List<Mensaje> getMessages(int userId, int contactId);

    // Enviar mensaje con saldo
    @Transaction
    public boolean enviarMensajeConSaldo(Mensaje mensaje, double costoMensaje) {
        double saldoActual = getUserBalance(mensaje.usuarioId);
        if (saldoActual >= costoMensaje) {
            // Actualizar saldo
            double nuevoSaldo = saldoActual - costoMensaje;
            updateUserBalance(mensaje.usuarioId, nuevoSaldo);
            // Insertar mensaje
            insertMensaje(mensaje);
            return true; // Mensaje enviado exitosamente
        } else {
            return false; // Saldo insuficiente
        }
    }
}
