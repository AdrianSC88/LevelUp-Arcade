package com.levelup.controlador;

import com.levelup.modelo.Usuario;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Pruebas unitarias para UsuarioController.
 */
public class UsuarioControllerTest {

    private final UsuarioController controller = new UsuarioController();

    // ── LOGIN ──────────────────────────────────────────────

    @Test
    void loginCamposVaciosDevuelveNull() {
        assertNull(controller.login("", "123456"));
        assertNull(controller.login("admin", ""));
        assertNull(controller.login("", ""));
    }

    @Test
    void loginUsuarioInexistenteDevuelveNull() {
        assertNull(controller.login("usuarioquenoexiste", "123456"));
    }

    @Test
    void loginContrasenaIncorrectaDevuelveNull() {
        assertNull(controller.login("admin", "contrasenamal"));
    }

    @Test
    void loginCorrectoDevuelveUsuario() {
        Usuario u = controller.login("admin", "123456");
        assertNotNull(u);
        assertEquals("admin", u.getNombreUsuario());
        assertEquals("administrador", u.getRol());
    }

    // ── AÑADIR USUARIO ─────────────────────────────────────

    @Test
    void añadirUsuarioNombreVacioDevuelveFalse() {
        assertFalse(controller.añadirUsuario("", "Nombre Real", "123456", "empleado"));
    }

    @Test
    void añadirUsuarioContrasenacortaDevuelveFalse() {
        assertFalse(controller.añadirUsuario("nuevouser", "Nombre Real", "123", "empleado"));
    }

    @Test
    void añadirUsuarioRolInvalidoDevuelveFalse() {
        assertFalse(controller.añadirUsuario("nuevouser", "Nombre Real", "123456", "jefe"));
    }

    @Test
    void añadirUsuarioDuplicadoDevuelveFalse() {
        assertFalse(controller.añadirUsuario("admin", "Nombre Real", "123456", "administrador"));
    }

    // ── ELIMINAR USUARIO ───────────────────────────────────

    @Test
    void eliminarUsuarioIdInvalidoDevuelveFalse() {
        assertFalse(controller.eliminarUsuario(-1));
        assertFalse(controller.eliminarUsuario(0));
    }

    @Test
    void eliminarUsuarioInexistenteDevuelveFalse() {
        assertFalse(controller.eliminarUsuario(99999));
    }
}