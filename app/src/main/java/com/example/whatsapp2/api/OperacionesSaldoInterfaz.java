package com.example.whatsapp2.api;

public interface OperacionesSaldoInterfaz {

    /**
     * Obtiene el saldo actual
     * @param usuarioId ID del usuario (Pon 1 si quieres usar el usuario actual)
     * @return saldo actual o null si no existe
     */
    Double getCoins(int usuarioId);

    /**
     * Añade saldo a un usuario
     * @param usuarioId ID del usuario (Pon 1 si quieres usar el usuario actual)
     * @param cantidad Cantidad a añadir
     * @return true si se añadió correctamente
     */
    
    boolean addCoins(int usuarioId, double cantidad);
    
    /**
     * Elimina saldo de un usuario
     * @param usuarioId ID del usuario (Pon 1 si quieres usar el usuario actual)
     * @param cantidad Cantidad a eliminar
     * @return true si se eliminó correctamente
     */
    boolean removeCoins(int usuarioId, double cantidad);

}
