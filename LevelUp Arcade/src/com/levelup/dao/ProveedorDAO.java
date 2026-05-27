package com.levelup.dao;

import com.levelup.modelo.Proveedor;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Clase DAO para la gestión de proveedores en la base de datos.
 */
public class ProveedorDAO {

    /**
     * Inserta un nuevo proveedor en la base de datos.
     * @param proveedor objeto Proveedor a insertar
     * @return true si la inserción fue exitosa
     */
    public boolean insertar(Proveedor proveedor) {
        String sql = "INSERT INTO proveedores (nombre, email, direccion, telefono) VALUES (?, ?, ?, ?)";
        try (Connection con = ConexionBD.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, proveedor.getNombre());
            ps.setString(2, proveedor.getEmail());
            ps.setString(3, proveedor.getDireccion());
            ps.setString(4, proveedor.getTelefono());
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Error al insertar proveedor: " + e.getMessage());
            return false;
        }
    }

    /**
     * Obtiene todos los proveedores de la base de datos.
     * @return lista de proveedores
     */
    public List<Proveedor> obtenerTodos() {
        List<Proveedor> lista = new ArrayList<>();
        String sql = "SELECT * FROM proveedores ORDER BY id_proveedor";
        try (Connection con = ConexionBD.getConnection();
             Statement st = con.createStatement();
             ResultSet rs = st.executeQuery(sql)) {
            while (rs.next()) {
                lista.add(new Proveedor(
                        rs.getInt("id_proveedor"),
                        rs.getString("nombre"),
                        rs.getString("email"),
                        rs.getString("direccion"),
                        rs.getString("telefono")
                ));
            }
        } catch (SQLException e) {
            System.err.println("Error al obtener proveedores: " + e.getMessage());
        }
        return lista;
    }

    /**
     * Obtiene un proveedor por su id.
     * @param id identificador del proveedor
     * @return objeto Proveedor o null si no existe
     */
    public Proveedor obtenerPorId(int id) {
        String sql = "SELECT * FROM proveedores WHERE id_proveedor = ?";
        try (Connection con = ConexionBD.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, id);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return new Proveedor(
                        rs.getInt("id_proveedor"),
                        rs.getString("nombre"),
                        rs.getString("email"),
                        rs.getString("direccion"),
                        rs.getString("telefono")
                );
            }
        } catch (SQLException e) {
            System.err.println("Error al obtener proveedor: " + e.getMessage());
        }
        return null;
    }

    /**
     * Actualiza un proveedor existente.
     * @param proveedor objeto Proveedor con los datos actualizados
     * @return true si la actualización fue exitosa
     */
    public boolean actualizar(Proveedor proveedor) {
        String sql = "UPDATE proveedores SET nombre = ?, email = ?, direccion = ?, telefono = ? WHERE id_proveedor = ?";
        try (Connection con = ConexionBD.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, proveedor.getNombre());
            ps.setString(2, proveedor.getEmail());
            ps.setString(3, proveedor.getDireccion());
            ps.setString(4, proveedor.getTelefono());
            ps.setInt(5, proveedor.getId());
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Error al actualizar proveedor: " + e.getMessage());
            return false;
        }
    }

    /**
     * Elimina un proveedor por su id.
     * @param id identificador del proveedor a eliminar
     * @return true si la eliminación fue exitosa
     */
    public boolean eliminar(int id) {
        String sql = "DELETE FROM proveedores WHERE id_proveedor = ?";
        try (Connection con = ConexionBD.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, id);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Error al eliminar proveedor: " + e.getMessage());
            return false;
        }
    }
}