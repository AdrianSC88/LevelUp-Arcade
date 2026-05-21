package com.levelup.controlador;

import com.levelup.dao.ProveedorDAO;
import com.levelup.modelo.Proveedor;
import java.util.List;

/**
 * Controlador para la gestión de proveedores.
 */
public class ProveedorController {

    private final ProveedorDAO proveedorDAO;

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
            System.err.println("El nombre del proveedor no puede estar vacío.");
            return false;
        }
        if (email == null || !email.contains("@")) {
            System.err.println("El email del proveedor no es válido.");
            return false;
        }
        Proveedor proveedor = new Proveedor(nombre.trim(), email.trim(), direccion.trim(), telefono.trim());
        return proveedorDAO.insertar(proveedor);
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
            System.err.println("El nombre del proveedor no puede estar vacío.");
            return false;
        }
        if (email == null || !email.contains("@")) {
            System.err.println("El email del proveedor no es válido.");
            return false;
        }
        Proveedor proveedor = new Proveedor(id, nombre.trim(), email.trim(), direccion.trim(), telefono.trim());
        return proveedorDAO.actualizar(proveedor);
    }

    /**
     * Elimina un proveedor por su id.
     * @param id identificador del proveedor a eliminar
     * @return true si se eliminó correctamente
     */
    public boolean eliminarProveedor(int id) {
        if (id <= 0) {
            System.err.println("El id debe ser un número positivo.");
            return false;
        }
        return proveedorDAO.eliminar(id);
    }
}