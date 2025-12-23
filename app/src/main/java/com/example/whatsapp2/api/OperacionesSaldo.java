package com.example.whatsapp2.api;

import com.example.whatsapp2.database.dao.ChatDao;

public class OperacionesSaldo implements OperacionesSaldoInterfaz{

    private final ChatDao chatDao;

    public OperacionesSaldo(ChatDao chatDao) {
        this.chatDao = chatDao;
    }

    public Double getCoins(int usuarioId) {
        Double saldo = chatDao.getUserBalance(usuarioId);
        return saldo;
    }
    
    @Override
    public boolean addCoins(int usuarioId, double cantidad) {
        if(cantidad <= 0) {
            return false;
        }
        Double saldoActual = chatDao.getUserBalance(usuarioId);
        if(saldoActual == null) {
            return false; // No se ha encontrado al usuario con usuarioId
        }
        double nuevoSaldo = saldoActual + cantidad;
        chatDao.updateUserBalance(usuarioId, nuevoSaldo);
        return true;
    }

    public boolean removeCoins(int usuarioId, double cantidad) {
        if(cantidad <= 0){
            return false;
        }
        Double saldoActual = chatDao.getUserBalance(usuarioId);
        if(saldoActual == null) {
            return false; // No se ha encontrado al usuario con usuarioId
        }
        double nuevoSaldo = saldoActual - cantidad;
        chatDao.updateUserBalance(usuarioId, nuevoSaldo);
        return true;
    }


}
