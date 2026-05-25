package com.levelup.dao;

import com.levelup.modelo.Usuario;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Clase DAO para la gestión de usuarios en la base de datos.
 */
public class UsuarioDAO {

    /**
     * Inserta un nuevo usuario en la base de datos.
     * @param usuario objeto Usuario a insertar
     * @return true si la inserción fue exitosa
     */
    public boolean insertar(Usuario usuario) {
        String sql = "INSERT INTO usuarios (nombre_usuario, nombre, contrasena_hash, rol) VALUES (?, ?, ?, ?)";
        try (Connection con = ConexionBD.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, usuario.getNombreUsuario());
            ps.setString(2, usuario.getNombre());
            ps.setString(3, usuario.getPasswordHash());
            ps.setString(4, usuario.getRol());
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Error al insertar usuario: " + e.getMessage());
            return false;
        }
    }

    /**
     * Obtiene todos los usuarios de la base de datos.
     * @return lista de usuarios
     */
    public List<Usuario> obtenerTodos() {
        List<Usuario> lista = new ArrayList<>();
        String sql = "SELECT * FROM usuarios";
        try (Connection con = ConexionBD.getConnection();
             Statement st = con.createStatement();
             ResultSet rs = st.executeQuery(sql)) {
            while (rs.next()) {
                lista.add(new Usuario(
                        rs.getInt("id_usuario"),
                        rs.getString("nombre_usuario"),
                        rs.getString("nombre"),
                        rs.getString("contrasena_hash"),
                        rs.getString("rol")
                ));
            }
        } catch (SQLException e) {
            System.err.println("Error al obtener usuarios: " + e.getMessage());
        }
        return lista;
    }

    /**
     * Obtiene un usuario por su id.
     * @param id identificador del usuario
     * @return objeto Usuario o null si no existe
     */
    public Usuario obtenerPorId(int id) {
        String sql = "SELECT * FROM usuarios WHERE id_usuario = ?";
        try (Connection con = ConexionBD.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, id);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return new Usuario(
                        rs.getInt("id_usuario"),
                        rs.getString("nombre_usuario"),
                        rs.getString("nombre"),
                        rs.getString("contrasena_hash"),
                        rs.getString("rol")
                );
            }
        } catch (SQLException e) {
            System.err.println("Error al obtener usuario: " + e.getMessage());
        }
        return null;
    }

    /**
     * Obtiene un usuario por su nombre de usuario, usado para el login.
     * @param nombreUsuario nombre de login del usuario
     * @return objeto Usuario o null si no existe
     */
    public Usuario obtenerPorNombre(String nombreUsuario) {
        String sql = "SELECT * FROM usuarios WHERE nombre_usuario = ? ORDER BY id_usuario";
        try (Connection con = ConexionBD.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, nombreUsuario);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return new Usuario(
                        rs.getInt("id_usuario"),
                        rs.getString("nombre_usuario"),
                        rs.getString("nombre"),
                        rs.getString("contrasena_hash"),
                        rs.getString("rol")
                );
            }
        } catch (SQLException e) {
            System.err.println("Error al obtener usuario: " + e.getMessage());
        }
        return null;
    }

    /**
     * Actualiza un usuario existente.
     * @param usuario objeto Usuario con los datos actualizados
     * @return true si la actualización fue exitosa
     */
    public boolean actualizar(Usuario usuario) {
        String sql = "UPDATE usuarios SET nombre_usuario = ?, nombre = ?, contrasena_hash = ?, rol = ? WHERE id_usuario = ?";
        try (Connection con = ConexionBD.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, usuario.getNombreUsuario());
            ps.setString(2, usuario.getNombre());
            ps.setString(3, usuario.getPasswordHash());
            ps.setString(4, usuario.getRol());
            ps.setInt(5, usuario.getId());
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Error al actualizar usuario: " + e.getMessage());
            return false;
        }
    }

    /**
     * Elimina un usuario por su id.
     * @param id identificador del usuario a eliminar
     * @return true si la eliminación fue exitosa
     */
    public boolean eliminar(int id) {
        String sql = "DELETE FROM usuarios WHERE id_usuario = ?";
        try (Connection con = ConexionBD.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, id);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Error al eliminar usuario: " + e.getMessage());
            return false;
        }
    }
}