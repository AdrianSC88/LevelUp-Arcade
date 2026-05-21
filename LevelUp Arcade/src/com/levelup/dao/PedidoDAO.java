package com.levelup.dao;

import com.levelup.modelo.Cliente;
import com.levelup.modelo.EstadoPedido;
import com.levelup.modelo.Pedido;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Clase DAO para la gestión de pedidos en la base de datos.
 */
public class PedidoDAO {

    /**
     * Inserta un nuevo pedido en la base de datos.
     * @param pedido objeto Pedido a insertar
     * @return id generado del pedido o -1 si falla
     */
    public int insertar(Pedido pedido) {
        String sql = "INSERT INTO pedidos (fecha, estado, id_cliente) VALUES (?, ?, ?)";
        try (Connection con = ConexionBD.getConnection();
             PreparedStatement ps = con.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setDate(1, Date.valueOf(pedido.getFecha()));
            ps.setString(2, pedido.getEstado().name());
            ps.setInt(3, pedido.getCliente().getId());
            ps.executeUpdate();
            ResultSet keys = ps.getGeneratedKeys();
            if (keys.next()) {
                return keys.getInt(1);
            }
        } catch (SQLException e) {
            System.err.println("Error al insertar pedido: " + e.getMessage());
        }
        return -1;
    }

    /**
     * Obtiene todos los pedidos de la base de datos con su cliente.
     * @return lista de pedidos
     */
    public List<Pedido> obtenerTodos() {
        List<Pedido> lista = new ArrayList<>();
        String sql = """
                SELECT p.*, c.nombre AS nom_cliente, c.email, c.telefono, c.direccion
                FROM pedidos p
                JOIN clientes c ON p.id_cliente = c.id_cliente ORDER BY p.id_pedido
                """;
        try (Connection con = ConexionBD.getConnection();
             Statement st = con.createStatement();
             ResultSet rs = st.executeQuery(sql)) {
            while (rs.next()) {
                lista.add(mapearPedido(rs));
            }
        } catch (SQLException e) {
            System.err.println("Error al obtener pedidos: " + e.getMessage());
        }
        return lista;
    }

    /**
     * Obtiene un pedido por su id.
     * @param id identificador del pedido
     * @return objeto Pedido o null si no existe
     */
    public Pedido obtenerPorId(int id) {
        String sql = """
                SELECT p.*, c.nombre AS nom_cliente, c.email, c.telefono, c.direccion
                FROM pedidos p
                JOIN clientes c ON p.id_cliente = c.id_cliente
                WHERE p.id_pedido = ?
                """;
        try (Connection con = ConexionBD.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, id);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return mapearPedido(rs);
            }
        } catch (SQLException e) {
            System.err.println("Error al obtener pedido: " + e.getMessage());
        }
        return null;
    }

    /**
     * Actualiza el estado de un pedido.
     * @param pedido objeto Pedido con el estado actualizado
     * @return true si la actualización fue exitosa
     */
    public boolean actualizar(Pedido pedido) {
        String sql = "UPDATE pedidos SET fecha = ?, estado = ?, id_cliente = ? WHERE id_pedido = ?";
        try (Connection con = ConexionBD.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setDate(1, Date.valueOf(pedido.getFecha()));
            ps.setString(2, pedido.getEstado().name());
            ps.setInt(3, pedido.getCliente().getId());
            ps.setInt(4, pedido.getId());
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Error al actualizar pedido: " + e.getMessage());
            return false;
        }
    }

    /**
     * Elimina un pedido por su id.
     * @param id identificador del pedido a eliminar
     * @return true si la eliminación fue exitosa
     */
    public boolean eliminar(int id) {
        String sql = "DELETE FROM pedidos WHERE id_pedido = ?";
        try (Connection con = ConexionBD.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, id);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Error al eliminar pedido: " + e.getMessage());
            return false;
        }
    }

    /**
     * Mapea un ResultSet a un objeto Pedido.
     * @param rs ResultSet con los datos del pedido
     * @return objeto Pedido
     * @throws SQLException si hay error al leer el ResultSet
     */
    private Pedido mapearPedido(ResultSet rs) throws SQLException {
        Cliente cliente = new Cliente(
                rs.getInt("id_cliente"),
                rs.getString("nom_cliente"),
                rs.getString("email"),
                rs.getString("telefono"),
                rs.getString("direccion")
        );
        return new Pedido(
                rs.getInt("id_pedido"),
                rs.getDate("fecha").toLocalDate(),
                EstadoPedido.valueOf(rs.getString("estado")),
                cliente
        );
    }
}