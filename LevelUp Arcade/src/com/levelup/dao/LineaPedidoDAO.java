package com.levelup.dao;

import com.levelup.modelo.Categoria;
import com.levelup.modelo.LineaPedido;
import com.levelup.modelo.Producto;
import com.levelup.modelo.Proveedor;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Clase DAO para la gestión de líneas de pedido en la base de datos.
 */
public class LineaPedidoDAO {

    /**
     * Inserta una nueva línea de pedido en la base de datos.
     * @param lineaPedido objeto LineaPedido a insertar
     * @return true si la inserción fue exitosa
     */
    public boolean insertar(LineaPedido lineaPedido) {
        String sql = "INSERT INTO linea_pedido (id_pedido, id_producto, cantidad, precio_unitario) VALUES (?, ?, ?, ?)";
        try (Connection con = ConexionBD.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, lineaPedido.getIdPedido());
            ps.setInt(2, lineaPedido.getProducto().getId());
            ps.setInt(3, lineaPedido.getCantidad());
            ps.setDouble(4, lineaPedido.getPrecioUnitario());
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Error al insertar línea de pedido: " + e.getMessage());
            return false;
        }
    }

    /**
     * Obtiene todas las líneas de un pedido concreto.
     * @param idPedido identificador del pedido
     * @return lista de líneas de pedido
     */
    public List<LineaPedido> obtenerPorPedido(int idPedido) {
        List<LineaPedido> lista = new ArrayList<>();
        String sql = """
                SELECT lp.*, p.nombre, p.descripcion, p.precio, p.stock,
                       c.id_categoria, c.nombre AS nom_cat, c.descripcion AS desc_cat,
                       pv.id_proveedor, pv.nombre AS nom_prov, pv.email, pv.direccion, pv.telefono
                FROM linea_pedido lp
                JOIN productos p ON lp.id_producto = p.id_producto
                JOIN categorias c ON p.id_categoria = c.id_categoria
                JOIN proveedores pv ON p.id_proveedor = pv.id_proveedor
                WHERE lp.id_pedido = ? ORDER BY p.nombre ASC
                """;
        try (Connection con = ConexionBD.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, idPedido);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                lista.add(mapearLineaPedido(rs));
            }
        } catch (SQLException e) {
            System.err.println("Error al obtener líneas de pedido: " + e.getMessage());
        }
        return lista;
    }

    /**
     * Actualiza la cantidad y precio de una línea de pedido.
     * @param lineaPedido objeto LineaPedido con los datos actualizados
     * @return true si la actualización fue exitosa
     */
    public boolean actualizar(LineaPedido lineaPedido) {
        String sql = "UPDATE linea_pedido SET cantidad = ?, precio_unitario = ? WHERE id_pedido = ? AND id_producto = ?";
        try (Connection con = ConexionBD.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, lineaPedido.getCantidad());
            ps.setDouble(2, lineaPedido.getPrecioUnitario());
            ps.setInt(3, lineaPedido.getIdPedido());
            ps.setInt(4, lineaPedido.getProducto().getId());
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Error al actualizar línea de pedido: " + e.getMessage());
            return false;
        }
    }

    /**
     * Elimina una línea de pedido.
     * @param idPedido identificador del pedido
     * @param idProducto identificador del producto
     * @return true si la eliminación fue exitosa
     */
    public boolean eliminar(int idPedido, int idProducto) {
        String sql = "DELETE FROM linea_pedido WHERE id_pedido = ? AND id_producto = ?";
        try (Connection con = ConexionBD.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, idPedido);
            ps.setInt(2, idProducto);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Error al eliminar línea de pedido: " + e.getMessage());
            return false;
        }
    }

    /**
     * Mapea un ResultSet a un objeto LineaPedido.
     * @param rs ResultSet con los datos de la línea de pedido
     * @return objeto LineaPedido
     * @throws SQLException si hay error al leer el ResultSet
     */
    private LineaPedido mapearLineaPedido(ResultSet rs) throws SQLException {
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
        Producto producto = new Producto(
                rs.getInt("id_producto"),
                rs.getString("nombre"),
                rs.getString("descripcion"),
                rs.getDouble("precio"),
                rs.getInt("stock"),
                categoria,
                proveedor
        );
        return new LineaPedido(
                rs.getInt("id_pedido"),
                producto,
                rs.getInt("cantidad"),
                rs.getDouble("precio_unitario")
        );
    }
}