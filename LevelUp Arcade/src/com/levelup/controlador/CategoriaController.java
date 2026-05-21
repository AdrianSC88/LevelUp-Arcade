package com.levelup.controlador;

import com.levelup.dao.CategoriaDAO;
import com.levelup.modelo.Categoria;
import java.util.List;

/**
 * Controlador para la gestión de categorías.
 */
public class CategoriaController {

    private final CategoriaDAO categoriaDAO;

    public CategoriaController() {
        this.categoriaDAO = new CategoriaDAO();
    }

    /**
     * Añade una nueva categoría al sistema.
     * @param nombre nombre de la categoría
     * @param descripcion descripción de la categoría
     * @return true si se añadió correctamente
     */
    public boolean añadirCategoria(String nombre, String descripcion) {
        if (nombre == null || nombre.trim().isEmpty()) {
            System.err.println("El nombre de la categoría no puede estar vacío.");
            return false;
        }
        Categoria categoria = new Categoria(nombre.trim(), descripcion.trim());
        return categoriaDAO.insertar(categoria);
    }

    /**
     * Obtiene todas las categorías del sistema.
     * @return lista de categorías
     */
    public List<Categoria> obtenerTodas() {
        return categoriaDAO.obtenerTodas();
    }

    /**
     * Obtiene una categoría por su id.
     * @param id identificador de la categoría
     * @return objeto Categoria o null si no existe
     */
    public Categoria obtenerPorId(int id) {
        if (id <= 0) {
            System.err.println("El id debe ser un número positivo.");
            return null;
        }
        return categoriaDAO.obtenerPorId(id);
    }

    /**
     * Actualiza una categoría existente.
     * @param id identificador de la categoría
     * @param nombre nuevo nombre
     * @param descripcion nueva descripción
     * @return true si se actualizó correctamente
     */
    public boolean actualizarCategoria(int id, String nombre, String descripcion) {
        if (nombre == null || nombre.trim().isEmpty()) {
            System.err.println("El nombre de la categoría no puede estar vacío.");
            return false;
        }
        Categoria categoria = new Categoria(id, nombre.trim(), descripcion.trim());
        return categoriaDAO.actualizar(categoria);
    }

    /**
     * Elimina una categoría por su id.
     * @param id identificador de la categoría a eliminar
     * @return true si se eliminó correctamente
     */
    public boolean eliminarCategoria(int id) {
        if (id <= 0) {
            System.err.println("El id debe ser un número positivo.");
            return false;
        }
        return categoriaDAO.eliminar(id);
    }
}