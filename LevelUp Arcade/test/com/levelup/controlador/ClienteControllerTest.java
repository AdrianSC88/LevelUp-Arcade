package com.levelup.controlador;

import com.levelup.modelo.Cliente;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Pruebas unitarias para ClienteController.
 */
public class ClienteControllerTest {

    private final ClienteController controller = new ClienteController();

    @Test
    void obtenerPorIdInvalidoDevuelveNull() {
        assertNull(controller.obtenerPorId(-1));
        assertNull(controller.obtenerPorId(0));
    }

    @Test
    void obtenerPorIdInexistenteDevuelveNull() {
        assertNull(controller.obtenerPorId(99999));
    }

    @Test
    void añadirClienteNombreVacioDevuelveFalse() {
        assertFalse(controller.añadirCliente("", "test@test.com", "600000000", "Calle Test"));
    }

    @Test
    void añadirClienteEmailInvalidoDevuelveFalse() {
        assertFalse(controller.añadirCliente("Test", "emailsinArroba", "600000000", "Calle Test"));
    }

    @Test
    void añadirClienteTelefonoVacioDevuelveFalse() {
        assertFalse(controller.añadirCliente("Test", "test@test.com", "", "Calle Test"));
    }

    @Test
    void eliminarClienteIdInvalidoDevuelveFalse() {
        assertFalse(controller.eliminarCliente(-1));
        assertFalse(controller.eliminarCliente(0));
    }

    @Test
    void eliminarClienteInexistenteDevuelveFalse() {
        assertFalse(controller.eliminarCliente(99999));
    }
}