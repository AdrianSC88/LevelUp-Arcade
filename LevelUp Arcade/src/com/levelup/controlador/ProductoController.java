package com.levelup.controlador;

import com.levelup.dao.ProductoDAO;

import com.levelup.dao.CategoriaDAO;
import com.levelup.dao.ProveedorDAO;
import com.levelup.modelo.Producto;
import com.levelup.modelo.Categoria;
import com.levelup.modelo.Proveedor;
import java.util.List;
import com.levelup.util.Logger;

/**
 * Controlador para la gestión de productos.
 */
public class ProductoController {

    private final ProductoDAO productoDAO;
    private final CategoriaDAO categoriaDAO;
    private final ProveedorDAO proveedorDAO;

    /**
     * Constructor. Inicializa los DAOs de productos, categorías y proveedores.
     */
    public ProductoController() {
        this.productoDAO = new ProductoDAO();
        this.categoriaDAO = new CategoriaDAO();
        this.proveedorDAO = new ProveedorDAO();
    }

    /**
     * Añade un nuevo producto al sistema.
     * @param nombre nombre del producto
     * @param descripcion descripción del producto
     * @param precio precio del producto
     * @param stock stock del producto
     * @param idCategoria id de la categoría
     * @param idProveedor id del proveedor
     * @return true si se añadió correctamente
     */
    public boolean añadirProducto(String nombre, String descripcion, double precio,
    		int stock, int idCategoria, int idProveedor) {
    	if (nombre == null || nombre.trim().isEmpty()) {
    		Logger.error("Intento de añadir producto con nombre vacío");
    		System.err.println("El nombre del producto no puede estar vacío.");
    		return false;
    	}
    	if (precio <= 0) {
    		Logger.error("Intento de añadir producto con precio negativo");
    		System.err.println("El precio no puede ser negativo.");
    		return false;
    	}
    	if (stock < 0) {
    		Logger.error("Intento de añadir producto con stock negativo");
    		System.err.println("El stock no puede ser negativo.");
    		return false;
    	}
    	Categoria categoria = categoriaDAO.obtenerPorId(idCategoria);
    	if (categoria == null) {
    		Logger.error("Categoría con id " + idCategoria + " no encontrada");
    		System.err.println("La categoría indicada no existe.");
    		return false;
    	}
    	Proveedor proveedor = proveedorDAO.obtenerPorId(idProveedor);
    	if (proveedor == null) {
    		Logger.error("Proveedor con id " + idProveedor + " no encontrado");
    		System.err.println("El proveedor indicado no existe.");
    		return false;
    	}
    	Producto producto = new Producto(0, nombre.trim(), descripcion.trim(),
    			precio, stock, categoria, proveedor);

    	boolean resultado = productoDAO.insertar(producto);
    	if (resultado) {
    		Logger.info("Producto '" + nombre + "' añadido correctamente");
    	} else {
    		Logger.error("Error al añadir producto '" + nombre + "'");
    	}
    	return resultado;
    }

    /**
     * Obtiene todos los productos del sistema.
     * @return lista de productos
     */
    public List<Producto> obtenerTodos() {
        return productoDAO.obtenerTodos();
    }

    /**
     * Obtiene un producto por su id.
     * @param id identificador del producto
     * @return objeto Producto o null si no existe
     */
    public Producto obtenerPorId(int id) {
        if (id <= 0) {
            System.err.println("El id debe ser un número positivo.");
            return null;
        }
        return productoDAO.obtenerPorId(id);
    }

    /**
     * Actualiza un producto existente.
     * @param id identificador del producto
     * @param nombre nuevo nombre
     * @param descripcion nueva descripción
     * @param precio nuevo precio
     * @param stock nuevo stock
     * @param idCategoria nuevo id de categoría
     * @param idProveedor nuevo id de proveedor
     * @return true si se actualizó correctamente
     */
    public boolean actualizarProducto(int id, String nombre, String descripcion, double precio,
                                       int stock, int idCategoria, int idProveedor) {
        if (nombre == null || nombre.trim().isEmpty()) {
            System.err.println("El nombre del producto no puede estar vacío.");
            return false;
        }
        if (precio < 0) {
            System.err.println("El precio no puede ser negativo.");
            return false;
        }
        if (stock < 0) {
            System.err.println("El stock no puede ser negativo.");
            return false;
        }
        Categoria categoria = categoriaDAO.obtenerPorId(idCategoria);
        if (categoria == null) {
            System.err.println("La categoría indicada no existe.");
            return false;
        }
        Proveedor proveedor = proveedorDAO.obtenerPorId(idProveedor);
        if (proveedor == null) {
            System.err.println("El proveedor indicado no existe.");
            return false;
        }
        Producto producto = new Producto(id, nombre.trim(), descripcion.trim(),
                precio, stock, categoria, proveedor);
        return productoDAO.actualizar(producto);
    }

    /**
     * Elimina un producto por su id.
     * @param id identificador del producto a eliminar
     * @return true si se eliminó correctamente
     */
    public boolean eliminarProducto(int id) {
        if (id <= 0) {
            System.err.println("El id debe ser un número positivo.");
            return false;
        }
        return productoDAO.eliminar(id);
    }
    

}


