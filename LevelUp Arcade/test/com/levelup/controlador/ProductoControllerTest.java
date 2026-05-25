package com.levelup.controlador;

import com.levelup.modelo.Producto;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Pruebas unitarias para ProductoController.
 */
public class ProductoControllerTest {

    private final ProductoController controller = new ProductoController();

    // ── OBTENER POR ID ─────────────────────────────────────

    @Test
    void obtenerPorIdInvalidoDevuelveNull() {
        assertNull(controller.obtenerPorId(-1));
        assertNull(controller.obtenerPorId(0));
    }

    @Test
    void obtenerPorIdInexistenteDevuelveNull() {
        assertNull(controller.obtenerPorId(99999));
    }

    // ── AÑADIR PRODUCTO ────────────────────────────────────

    @Test
    void añadirProductoNombreVacioDevuelveFalse() {
        assertFalse(controller.añadirProducto("", "Descripción", 10.0, 5, 1, 1));
    }

    @Test
    void añadirProductoPrecioNegativoDevuelveFalse() {
        assertFalse(controller.añadirProducto("Producto Test", "Descripción", -1.0, 5, 1, 1));
    }

    @Test
    void añadirProductoPrecioCeroDevuelveFalse() {
        assertFalse(controller.añadirProducto("Producto Test", "Descripción", 0.0, 5, 1, 1));
    }

    @Test
    void añadirProductoStockNegativoDevuelveFalse() {
        assertFalse(controller.añadirProducto("Producto Test", "Descripción", 10.0, -1, 1, 1));
    }

    @Test
    void añadirProductoCategoriaInexistenteDevuelveFalse() {
        assertFalse(controller.añadirProducto("Producto Test", "Descripción", 10.0, 5, 99999, 1));
    }

    @Test
    void añadirProductoProveedorInexistenteDevuelveFalse() {
        assertFalse(controller.añadirProducto("Producto Test", "Descripción", 10.0, 5, 1, 99999));
    }

    // ── ELIMINAR PRODUCTO ──────────────────────────────────

    @Test
    void eliminarProductoIdInvalidoDevuelveFalse() {
        assertFalse(controller.eliminarProducto(-1));
        assertFalse(controller.eliminarProducto(0));
    }

    @Test
    void eliminarProductoInexistenteDevuelveFalse() {
        assertFalse(controller.eliminarProducto(99999));
    }
}