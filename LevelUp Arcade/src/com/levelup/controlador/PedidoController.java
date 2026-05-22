package com.levelup.controlador;

import com.levelup.dao.ClienteDAO;
import com.levelup.dao.LineaPedidoDAO;
import com.levelup.dao.PedidoDAO;
import com.levelup.modelo.Cliente;
import com.levelup.modelo.EstadoPedido;
import com.levelup.modelo.LineaPedido;
import com.levelup.modelo.Pedido;
import com.levelup.util.Logger;
import java.time.LocalDate;
import java.util.List;

/**
 * Controlador para la gestión de pedidos.
 */
public class PedidoController {

    private final PedidoDAO pedidoDAO;
    private final ClienteDAO clienteDAO;
    private final LineaPedidoDAO lineaPedidoDAO;

    /**
     * Constructor. Inicializa los DAOs de pedidos, clientes y líneas de pedido.
     */
    public PedidoController() {
        this.pedidoDAO = new PedidoDAO();
        this.clienteDAO = new ClienteDAO();
        this.lineaPedidoDAO = new LineaPedidoDAO();
    }

    /**
     * Crea un nuevo pedido en el sistema.
     * @param idCliente id del cliente
     * @return id del pedido creado o -1 si falla
     */
    public int crearPedido(int idCliente) {
        if (idCliente <= 0) {
            Logger.error("Intento de crear pedido con id de cliente inválido: " + idCliente);
            System.err.println("El id del cliente debe ser un número positivo.");
            return -1;
        }
        Cliente cliente = clienteDAO.obtenerPorId(idCliente);
        if (cliente == null) {
            Logger.error("Intento de crear pedido con cliente inexistente, id: " + idCliente);
            System.err.println("El cliente indicado no existe.");
            return -1;
        }
        Pedido pedido = new Pedido(LocalDate.now(), EstadoPedido.PENDIENTE, cliente);
        int idPedido = pedidoDAO.insertar(pedido);
        if (idPedido != -1) {
            Logger.info("Pedido creado correctamente con id: " + idPedido + " para cliente: " + cliente.getNombre());
        } else {
            Logger.error("Error al crear pedido para cliente con id: " + idCliente);
        }
        return idPedido;
    }

    /**
     * Añade una línea de producto a un pedido existente.
     * @param lineaPedido objeto LineaPedido a añadir
     * @return true si se añadió correctamente
     */
    public boolean añadirLineaPedido(LineaPedido lineaPedido) {
        if (lineaPedido.getCantidad() <= 0) {
            Logger.error("Intento de añadir línea de pedido con cantidad inválida");
            System.err.println("La cantidad debe ser mayor que cero.");
            return false;
        }
        if (lineaPedido.getPrecioUnitario() < 0) {
            Logger.error("Intento de añadir línea de pedido con precio negativo");
            System.err.println("El precio unitario no puede ser negativo.");
            return false;
        }
        boolean resultado = lineaPedidoDAO.insertar(lineaPedido);
        if (resultado) {
            Logger.info("Producto '" + lineaPedido.getProducto().getNombre() +
                    "' añadido al pedido " + lineaPedido.getIdPedido());
        } else {
            Logger.error("Error al añadir producto al pedido " + lineaPedido.getIdPedido());
        }
        return resultado;
    }

    /**
     * Obtiene todos los pedidos del sistema.
     * @return lista de pedidos
     */
    public List<Pedido> obtenerTodos() {
        return pedidoDAO.obtenerTodos();
    }

    /**
     * Obtiene un pedido por su id.
     * @param id identificador del pedido
     * @return objeto Pedido o null si no existe
     */
    public Pedido obtenerPorId(int id) {
        if (id <= 0) {
            System.err.println("El id debe ser un número positivo.");
            return null;
        }
        return pedidoDAO.obtenerPorId(id);
    }

    /**
     * Obtiene las líneas de un pedido concreto.
     * @param idPedido identificador del pedido
     * @return lista de líneas de pedido
     */
    public List<LineaPedido> obtenerLineasPorPedido(int idPedido) {
        if (idPedido <= 0) {
            System.err.println("El id del pedido debe ser un número positivo.");
            return null;
        }
        return lineaPedidoDAO.obtenerPorPedido(idPedido);
    }

    /**
     * Actualiza el estado de un pedido.
     * @param id identificador del pedido
     * @param nuevoEstado nuevo estado del pedido
     * @return true si se actualizó correctamente
     */
    public boolean actualizarEstado(int id, EstadoPedido nuevoEstado) {
        if (id <= 0) {
            Logger.error("Intento de actualizar estado de pedido con id inválido: " + id);
            System.err.println("El id debe ser un número positivo.");
            return false;
        }
        Pedido pedido = pedidoDAO.obtenerPorId(id);
        if (pedido == null) {
            Logger.error("Intento de actualizar estado de pedido inexistente, id: " + id);
            System.err.println("El pedido indicado no existe.");
            return false;
        }
        pedido.setEstado(nuevoEstado);
        boolean resultado = pedidoDAO.actualizar(pedido);
        if (resultado) {
            Logger.info("Estado del pedido " + id + " actualizado a: " + nuevoEstado);
        } else {
            Logger.error("Error al actualizar estado del pedido con id: " + id);
        }
        return resultado;
    }

    /**
     * Elimina un pedido por su id.
     * @param id identificador del pedido a eliminar
     * @return true si se eliminó correctamente
     */
    public boolean eliminarPedido(int id) {
        if (id <= 0) {
            Logger.error("Intento de eliminar pedido con id inválido: " + id);
            System.err.println("El id debe ser un número positivo.");
            return false;
        }
        boolean resultado = pedidoDAO.eliminar(id);
        if (resultado) {
            Logger.info("Pedido con id " + id + " eliminado correctamente");
        } else {
            Logger.error("Error al eliminar pedido con id: " + id);
        }
        return resultado;
    }
}