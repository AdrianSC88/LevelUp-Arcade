package com.levelup.controlador;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Pruebas unitarias para CategoriaController.
 */
public class CategoriaControllerTest {

    private final CategoriaController controller = new CategoriaController();

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
    void añadirCategoriaNombreVacioDevuelveFalse() {
        assertFalse(controller.añadirCategoria("", "Descripción test"));
    }

    @Test
    void eliminarCategoriaIdInvalidoDevuelveFalse() {
        assertFalse(controller.eliminarCategoria(-1));
        assertFalse(controller.eliminarCategoria(0));
    }

    @Test
    void eliminarCategoriaInexistenteDevuelveFalse() {
        assertFalse(controller.eliminarCategoria(99999));
    }
}