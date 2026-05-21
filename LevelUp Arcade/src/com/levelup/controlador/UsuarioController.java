package com.levelup.controlador;

import com.levelup.dao.UsuarioDAO;
import com.levelup.modelo.Usuario;
import com.levelup.util.Logger;
import org.mindrot.jbcrypt.BCrypt;
import java.util.List;

/**
 * Controlador para la gestión de usuarios y autenticación.
 */
public class UsuarioController {

    private final UsuarioDAO usuarioDAO;

    public UsuarioController() {
        this.usuarioDAO = new UsuarioDAO();
    }

    /**
     * Autentica un usuario comprobando nombre y contraseña.
     * @param nombre nombre del usuario
     * @param password contraseña en texto plano
     * @return objeto Usuario si las credenciales son correctas, null si no
     */
    public Usuario login(String nombre, String password) {
        if (nombre == null || nombre.trim().isEmpty() ||
            password == null || password.trim().isEmpty()) {
            Logger.error("Intento de login con campos vacíos");
            System.err.println("El nombre y la contraseña son obligatorios.");
            return null;
        }
        Usuario usuario = usuarioDAO.obtenerPorNombre(nombre.trim());
        if (usuario == null) {
            Logger.error("Intento de login con usuario inexistente: " + nombre);
            System.err.println("Usuario no encontrado.");
            return null;
        }
        if (!BCrypt.checkpw(password, usuario.getPasswordHash())) {
            Logger.error("Contraseña incorrecta para el usuario: " + nombre);
            System.err.println("Contraseña incorrecta.");
            return null;
        }
        Logger.info("Usuario '" + nombre + "' ha iniciado sesión correctamente");
        return usuario;
    }

    /**
     * Añade un nuevo usuario al sistema.
     * @param nombre nombre del usuario
     * @param password contraseña en texto plano
     * @param rol rol del usuario (administrador/empleado)
     * @return true si se añadió correctamente
     */
    public boolean añadirUsuario(String nombre, String password, String rol) {
        if (nombre == null || nombre.trim().isEmpty()) {
            Logger.error("Intento de añadir usuario con nombre vacío");
            System.err.println("El nombre del usuario no puede estar vacío.");
            return false;
        }
        if (password == null || password.length() < 6) {
            Logger.error("Intento de añadir usuario con contraseña demasiado corta");
            System.err.println("La contraseña debe tener al menos 6 caracteres.");
            return false;
        }
        if (!rol.equals("administrador") && !rol.equals("empleado")) {
            Logger.error("Intento de añadir usuario con rol inválido: " + rol);
            System.err.println("El rol debe ser 'administrador' o 'empleado'.");
            return false;
        }
        if (usuarioDAO.obtenerPorNombre(nombre.trim()) != null) {
            Logger.error("Intento de añadir usuario duplicado: " + nombre);
            System.err.println("Ya existe un usuario con ese nombre.");
            return false;
        }
        String hash = BCrypt.hashpw(password, BCrypt.gensalt());
        Usuario usuario = new Usuario(nombre.trim(), hash, rol);
        boolean resultado = usuarioDAO.insertar(usuario);
        if (resultado) {
            Logger.info("Usuario '" + nombre + "' añadido correctamente con rol: " + rol);
        } else {
            Logger.error("Error al añadir usuario '" + nombre + "'");
        }
        return resultado;
    }

    /**
     * Obtiene todos los usuarios del sistema.
     * @return lista de usuarios
     */
    public List<Usuario> obtenerTodos() {
        return usuarioDAO.obtenerTodos();
    }

    /**
     * Obtiene un usuario por su id.
     * @param id identificador del usuario
     * @return objeto Usuario o null si no existe
     */
    public Usuario obtenerPorId(int id) {
        if (id <= 0) {
            System.err.println("El id debe ser un número positivo.");
            return null;
        }
        return usuarioDAO.obtenerPorId(id);
    }

    /**
     * Actualiza la contraseña de un usuario.
     * @param id identificador del usuario
     * @param nuevaPassword nueva contraseña en texto plano
     * @return true si se actualizó correctamente
     */
    public boolean actualizarPassword(int id, String nuevaPassword) {
        if (nuevaPassword == null || nuevaPassword.length() < 6) {
            Logger.error("Intento de actualizar contraseña con menos de 6 caracteres para id: " + id);
            System.err.println("La contraseña debe tener al menos 6 caracteres.");
            return false;
        }
        Usuario usuario = usuarioDAO.obtenerPorId(id);
        if (usuario == null) {
            Logger.error("Intento de actualizar contraseña de usuario inexistente con id: " + id);
            System.err.println("Usuario no encontrado.");
            return false;
        }
        usuario.setPasswordHash(BCrypt.hashpw(nuevaPassword, BCrypt.gensalt()));
        boolean resultado = usuarioDAO.actualizar(usuario);
        if (resultado) {
            Logger.info("Contraseña actualizada correctamente para usuario: " + usuario.getNombre());
        } else {
            Logger.error("Error al actualizar contraseña para usuario con id: " + id);
        }
        return resultado;
    }

    /**
     * Elimina un usuario por su id.
     * @param id identificador del usuario a eliminar
     * @return true si se eliminó correctamente
     */
    public boolean eliminarUsuario(int id) {
        if (id <= 0) {
            Logger.error("Intento de eliminar usuario con id inválido: " + id);
            System.err.println("El id debe ser un número positivo.");
            return false;
        }
        boolean resultado = usuarioDAO.eliminar(id);
        if (resultado) {
            Logger.info("Usuario con id " + id + " eliminado correctamente");
        } else {
            Logger.error("Error al eliminar usuario con id: " + id);
        }
        return resultado;
    }
}