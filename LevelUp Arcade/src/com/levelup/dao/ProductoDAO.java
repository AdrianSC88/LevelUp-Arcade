package com.levelup.dao;

import com.levelup.modelo.Categoria;
import com.levelup.modelo.Producto;
import com.levelup.modelo.Proveedor;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Clase DAO para la gestión de productos en la base de datos.
 */
public class ProductoDAO {

    /**
     * Inserta un nuevo producto en la base de datos.
     * @param producto objeto Producto a insertar
     * @return true si la inserción fue exitosa
     */
    public boolean insertar(Producto producto) {
        String sql = "INSERT INTO productos (nombre, descripcion, precio, stock, id_categoria, id_proveedor) VALUES (?, ?, ?, ?, ?, ?)";
        try (Connection con = ConexionBD.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, producto.getNombre());
            ps.setString(2, producto.getDescripcion());
            ps.setDouble(3, producto.getPrecio());
            ps.setInt(4, producto.getStock());
            ps.setInt(5, producto.getCategoria().getId());
            ps.setInt(6, producto.getProveedor().getId());
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Error al insertar producto: " + e.getMessage());
            return false;
        }
    }

    /**
     * Obtiene todos los productos de la base de datos con su categoría y proveedor.
     * @return lista de productos
     */
    public List<Producto> obtenerTodos() {
        List<Producto> lista = new ArrayList<>();
        String sql = """
                SELECT p.*, c.nombre AS nom_cat, c.descripcion AS desc_cat,
                       pv.nombre AS nom_prov, pv.email, pv.direccion, pv.telefono
                FROM productos p
                JOIN categorias c ON p.id_categoria = c.id_categoria
                JOIN proveedores pv ON p.id_proveedor = pv.id_proveedor ORDER BY p.id_producto
                """;
        try (Connection con = ConexionBD.getConnection();
             Statement st = con.createStatement();
             ResultSet rs = st.executeQuery(sql)) {
            while (rs.next()) {
                lista.add(mapearProducto(rs));
            }
        } catch (SQLException e) {
            System.err.println("Error al obtener productos: " + e.getMessage());
        }
        return lista;
    }

    /**
     * Obtiene un producto por su id.
     * @param id identificador del producto
     * @return objeto Producto o null si no existe
     */
    public Producto obtenerPorId(int id) {
        String sql = """
                SELECT p.*, c.nombre AS nom_cat, c.descripcion AS desc_cat,
                       pv.nombre AS nom_prov, pv.email, pv.direccion, pv.telefono
                FROM productos p
                JOIN categorias c ON p.id_categoria = c.id_categoria
                JOIN proveedores pv ON p.id_proveedor = pv.id_proveedor
                WHERE p.id_producto = ?
                """;
        try (Connection con = ConexionBD.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, id);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return mapearProducto(rs);
            }
        } catch (SQLException e) {
            System.err.println("Error al obtener producto: " + e.getMessage());
        }
        return null;
    }

    /**
     * Actualiza un producto existente.
     * @param producto objeto Producto con los datos actualizados
     * @return true si la actualización fue exitosa
     */
    public boolean actualizar(Producto producto) {
        String sql = "UPDATE productos SET nombre = ?, descripcion = ?, precio = ?, stock = ?, id_categoria = ?, id_proveedor = ? WHERE id_producto = ?";
        try (Connection con = ConexionBD.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, producto.getNombre());
            ps.setString(2, producto.getDescripcion());
            ps.setDouble(3, producto.getPrecio());
            ps.setInt(4, producto.getStock());
            ps.setInt(5, producto.getCategoria().getId());
            ps.setInt(6, producto.getProveedor().getId());
            ps.setInt(7, producto.getId());
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Error al actualizar producto: " + e.getMessage());
            return false;
        }
    }

    /**
     * Elimina un producto por su id.
     * @param id identificador del producto a eliminar
     * @return true si la eliminación fue exitosa
     */
    public boolean eliminar(int id) {
        String sql = "DELETE FROM productos WHERE id_producto = ?";
        try (Connection con = ConexionBD.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, id);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Error al eliminar producto: " + e.getMessage());
            return false;
        }
    }

    /**
     * Mapea un ResultSet a un objeto Producto.
     * @param rs ResultSet con los datos del producto
     * @return objeto Producto
     * @throws SQLException si hay error al leer el ResultSet
     */
    private Producto mapearProducto(ResultSet rs) throws SQLException {
        Categoria categoria = new Categoria(
                rs.getInt("id_categoria"),
                rs.getString("nom_cat"),
                rs.getString("desc_cat")
        );
        Proveedor proveedor = new Proveedor(
                rs.getInt("id_proveedor"),
                rs.getString("nom_prov"),
                rs.getString("email"),
                rs.getString("direccion"),
                rs.getString("telefono")
        );
        return new Producto(
                rs.getInt("id_producto"),
                rs.getString("nombre"),
                rs.getString("descripcion"),
                rs.getDouble("precio"),
                rs.getInt("stock"),
                categoria,
                proveedor
        );
    }
}