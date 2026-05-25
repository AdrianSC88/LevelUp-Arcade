package com.levelup.controlador;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Pruebas unitarias para ProveedorController.
 */
public class ProveedorControllerTest {

    private final ProveedorController controller = new ProveedorController();

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
    void añadirProveedorNombreVacioDevuelveFalse() {
        assertFalse(controller.añadirProveedor("", "test@test.com", "Calle Test", "600000000"));
    }

    @Test
    void añadirProveedorEmailInvalidoDevuelveFalse() {
        assertFalse(controller.añadirProveedor("Test", "emailsinArroba", "Calle Test", "600000000"));
    }

    @Test
    void eliminarProveedorIdInvalidoDevuelveFalse() {
        assertFalse(controller.eliminarProveedor(-1));
        assertFalse(controller.eliminarProveedor(0));
    }

    @Test
    void eliminarProveedorInexistenteDevuelveFalse() {
        assertFalse(controller.eliminarProveedor(99999));
    }
}