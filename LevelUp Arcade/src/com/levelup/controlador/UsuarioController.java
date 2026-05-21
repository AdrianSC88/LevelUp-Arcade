package com.levelup.controlador;

import com.levelup.dao.UsuarioDAO;
import com.levelup.modelo.Usuario;
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
            System.err.println("El nombre y la contraseña son obligatorios.");
            return null;
        }
        Usuario usuario = usuarioDAO.obtenerPorNombre(nombre.trim());
        if (usuario == null) {
            System.err.println("Usuario no encontrado.");
            return null;
        }
        if (!BCrypt.checkpw(password, usuario.getPasswordHash())) {
            System.err.println("Contraseña incorrecta.");
            return null;
        }
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
            System.err.println("El nombre del usuario no puede estar vacío.");
            return false;
        }
        if (password == null || password.length() < 6) {
            System.err.println("La contraseña debe tener al menos 6 caracteres.");
            return false;
        }
        if (!rol.equals("administrador") && !rol.equals("empleado")) {
            System.err.println("El rol debe ser 'administrador' o 'empleado'.");
            return false;
        }
        if (usuarioDAO.obtenerPorNombre(nombre.trim()) != null) {
            System.err.println("Ya existe un usuario con ese nombre.");
            return false;
        }
        String hash = BCrypt.hashpw(password, BCrypt.gensalt());
        Usuario usuario = new Usuario(nombre.trim(), hash, rol);
        return usuarioDAO.insertar(usuario);
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
            System.err.println("La contraseña debe tener al menos 6 caracteres.");
            return false;
        }
        Usuario usuario = usuarioDAO.obtenerPorId(id);
        if (usuario == null) {
            System.err.println("Usuario no encontrado.");
            return false;
        }
        usuario.setPasswordHash(BCrypt.hashpw(nuevaPassword, BCrypt.gensalt()));
        return usuarioDAO.actualizar(usuario);
    }

    /**
     * Elimina un usuario por su id.
     * @param id identificador del usuario a eliminar
     * @return true si se eliminó correctamente
     */
    public boolean eliminarUsuario(int id) {
        if (id <= 0) {
            System.err.println("El id debe ser un número positivo.");
            return false;
        }
        return usuarioDAO.eliminar(id);
    }
}