package com.levelup.controlador;

import com.levelup.dao.ClienteDAO;
import com.levelup.modelo.Cliente;
import java.util.List;

/**
 * Controlador para la gestión de clientes.
 */
public class ClienteController {

    private final ClienteDAO clienteDAO;

    public ClienteController() {
        this.clienteDAO = new ClienteDAO();
    }

    /**
     * Añade un nuevo cliente al sistema.
     * @param nombre nombre del cliente
     * @param email email del cliente
     * @param telefono teléfono del cliente
     * @param direccion dirección del cliente
     * @return true si se añadió correctamente
     */
    public boolean añadirCliente(String nombre, String email, String telefono, String direccion) {
        if (nombre == null || nombre.trim().isEmpty()) {
            System.err.println("El nombre del cliente no puede estar vacío.");
            return false;
        }
        if (email == null || !email.contains("@")) {
            System.err.println("El email del cliente no es válido.");
            return false;
        }
        if (telefono == null || telefono.trim().isEmpty()) {
            System.err.println("El teléfono del cliente no puede estar vacío.");
            return false;
        }
        Cliente cliente = new Cliente(nombre.trim(), email.trim(), telefono.trim(), direccion.trim());
        return clienteDAO.insertar(cliente);
    }

    /**
     * Obtiene todos los clientes del sistema.
     * @return lista de clientes
     */
    public List<Cliente> obtenerTodos() {
        return clienteDAO.obtenerTodos();
    }

    /**
     * Obtiene un cliente por su id.
     * @param id identificador del cliente
     * @return objeto Cliente o null si no existe
     */
    public Cliente obtenerPorId(int id) {
        if (id <= 0) {
            System.err.println("El id debe ser un número positivo.");
            return null;
        }
        return clienteDAO.obtenerPorId(id);
    }

    /**
     * Actualiza un cliente existente.
     * @param id identificador del cliente
     * @param nombre nuevo nombre
     * @param email nuevo email
     * @param telefono nuevo teléfono
     * @param direccion nueva dirección
     * @return true si se actualizó correctamente
     */
    public boolean actualizarCliente(int id, String nombre, String email, String telefono, String direccion) {
        if (nombre == null || nombre.trim().isEmpty()) {
            System.err.println("El nombre del cliente no puede estar vacío.");
            return false;
        }
        if (email == null || !email.contains("@")) {
            System.err.println("El email del cliente no es válido.");
            return false;
        }
        if (telefono == null || telefono.trim().isEmpty()) {
            System.err.println("El teléfono del cliente no puede estar vacío.");
            return false;
        }
        Cliente cliente = new Cliente(id, nombre.trim(), email.trim(), telefono.trim(), direccion.trim());
        return clienteDAO.actualizar(cliente);
    }

    /**
     * Elimina un cliente por su id.
     * @param id identificador del cliente a eliminar
     * @return true si se eliminó correctamente
     */
    public boolean eliminarCliente(int id) {
        if (id <= 0) {
            System.err.println("El id debe ser un número positivo.");
            return false;
        }
        return clienteDAO.eliminar(id);
    }
}