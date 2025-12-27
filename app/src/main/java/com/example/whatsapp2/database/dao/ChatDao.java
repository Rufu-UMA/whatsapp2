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

    // Obtener contactos (todos los usuarios excepto el usuario actual con ID 1)
    @Query("SELECT * FROM usuarios WHERE id != 1")
    public abstract List<Usuario> getContacts();

    // Obtener usuario por ID
    @Query("SELECT * FROM usuarios WHERE id = :usuarioId")
    public abstract Usuario getUserById(int usuarioId);

    @Insert
    public abstract void insertUsuario(Usuario usuario);

    // Obtener saldo (devuelve null si no existe el usuario)
    @Query("SELECT monedas FROM usuarios WHERE id = :usuarioId")
    public abstract Double getUserBalance(int usuarioId);

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
        Double saldoActual = getUserBalance(mensaje.usuarioId);
        // Verificar que el usuario existe y tiene saldo suficiente
        if (saldoActual != null && saldoActual >= costoMensaje) {
            // Actualizar saldo
            double nuevoSaldo = saldoActual - costoMensaje;
            updateUserBalance(mensaje.usuarioId, nuevoSaldo);
            // Insertar mensaje
            insertMensaje(mensaje);
            return true; // Mensaje enviado exitosamente
        } else {
            return false; // Saldo insuficiente o usuario no encontrado
        }
    }

    @Transaction
    public void addBalance(int userId, double amount) {
        Double currentBalance = getUserBalance(userId);
        if (currentBalance != null) {
            updateUserBalance(userId, currentBalance + amount);
        }
    }
}
