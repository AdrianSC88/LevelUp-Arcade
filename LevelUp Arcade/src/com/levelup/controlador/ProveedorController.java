package com.levelup.controlador;

import com.levelup.dao.ProveedorDAO;
import com.levelup.modelo.Proveedor;
import com.levelup.util.Logger;
import java.util.List;

/**
 * Controlador para la gestión de proveedores.
 */
public class ProveedorController {

    private final ProveedorDAO proveedorDAO;

    /**
     * Constructor. Inicializa el DAO de proveedores.
     */
    public ProveedorController() {
        this.proveedorDAO = new ProveedorDAO();
    }

    /**
     * Añade un nuevo proveedor al sistema.
     * @param nombre nombre del proveedor
     * @param email email del proveedor
     * @param direccion dirección del proveedor
     * @param telefono teléfono del proveedor
     * @return true si se añadió correctamente
     */
    public boolean añadirProveedor(String nombre, String email, String direccion, String telefono) {
        if (nombre == null || nombre.trim().isEmpty()) {
            Logger.error("Intento de añadir proveedor con nombre vacío");
            System.err.println("El nombre del proveedor no puede estar vacío.");
            return false;
        }
        if (email == null || !email.contains("@")) {
            Logger.error("Intento de añadir proveedor con email inválido: " + email);
            System.err.println("El email del proveedor no es válido.");
            return false;
        }
        Proveedor proveedor = new Proveedor(nombre.trim(), email.trim(), direccion.trim(), telefono.trim());
        boolean resultado = proveedorDAO.insertar(proveedor);
        if (resultado) {
            Logger.info("Proveedor '" + nombre + "' añadido correctamente");
        } else {
            Logger.error("Error al añadir proveedor '" + nombre + "'");
        }
        return resultado;
    }

    /**
     * Obtiene todos los proveedores del sistema.
     * @return lista de proveedores
     */
    public List<Proveedor> obtenerTodos() {
        return proveedorDAO.obtenerTodos();
    }

    /**
     * Obtiene un proveedor por su id.
     * @param id identificador del proveedor
     * @return objeto Proveedor o null si no existe
     */
    public Proveedor obtenerPorId(int id) {
        if (id <= 0) {
            System.err.println("El id debe ser un número positivo.");
            return null;
        }
        return proveedorDAO.obtenerPorId(id);
    }

    /**
     * Actualiza un proveedor existente.
     * @param id identificador del proveedor
     * @param nombre nuevo nombre
     * @param email nuevo email
     * @param direccion nueva dirección
     * @param telefono nuevo teléfono
     * @return true si se actualizó correctamente
     */
    public boolean actualizarProveedor(int id, String nombre, String email, String direccion, String telefono) {
        if (nombre == null || nombre.trim().isEmpty()) {
            Logger.error("Intento de actualizar proveedor con nombre vacío, id: " + id);
            System.err.println("El nombre del proveedor no puede estar vacío.");
            return false;
        }
        if (email == null || !email.contains("@")) {
            Logger.error("Intento de actualizar proveedor con email inválido: " + email);
            System.err.println("El email del proveedor no es válido.");
            return false;
        }
        Proveedor proveedor = new Proveedor(id, nombre.trim(), email.trim(), direccion.trim(), telefono.trim());
        boolean resultado = proveedorDAO.actualizar(proveedor);
        if (resultado) {
            Logger.info("Proveedor con id " + id + " actualizado correctamente");
        } else {
            Logger.error("Error al actualizar proveedor con id: " + id);
        }
        return resultado;
    }

    /**
     * Elimina un proveedor por su id.
     * @param id identificador del proveedor a eliminar
     * @return true si se eliminó correctamente
     */
    public boolean eliminarProveedor(int id) {
        if (id <= 0) {
            Logger.error("Intento de eliminar proveedor con id inválido: " + id);
            System.err.println("El id debe ser un número positivo.");
            return false;
        }
        boolean resultado = proveedorDAO.eliminar(id);
        if (resultado) {
            Logger.info("Proveedor con id " + id + " eliminado correctamente");
        } else {
            Logger.error("Error al eliminar proveedor con id: " + id);
        }
        return resultado;
    }
}