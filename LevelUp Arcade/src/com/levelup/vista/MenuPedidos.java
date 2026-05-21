package com.levelup.vista;

import com.levelup.controlador.PedidoController;
import com.levelup.modelo.EstadoPedido;
import com.levelup.modelo.LineaPedido;
import com.levelup.modelo.Pedido;
import com.levelup.modelo.Producto;
import com.levelup.modelo.Usuario;
import com.levelup.controlador.ProductoController;
import java.util.List;
import java.util.Scanner;

/**
 * Clase que gestiona el menú de pedidos.
 */
public class MenuPedidos {

    private final PedidoController pedidoController;
    private final ProductoController productoController;
    private final Scanner scanner;
    private final Usuario usuarioActivo;

    public MenuPedidos(Scanner scanner, Usuario usuarioActivo) {
        this.pedidoController = new PedidoController();
        this.productoController = new ProductoController();
        this.scanner = scanner;
        this.usuarioActivo = usuarioActivo;
    }

    /**
     * Muestra el menú de gestión de pedidos.
     */
    public void mostrar() {
        boolean salir = false;
        while (!salir) {
            System.out.println("\n╔══════════════════════════════════════════╗");
            System.out.println("║          GESTIÓN DE PEDIDOS              ║");
            System.out.println("╠══════════════════════════════════════════╣");
            System.out.println("║  1. Listar todos los pedidos             ║");
            System.out.println("║  2. Ver detalle de un pedido             ║");
            if (usuarioActivo.getRol().equals("administrador")) {
                System.out.println("║  3. Crear nuevo pedido                   ║");
                System.out.println("║  4. Actualizar estado de pedido          ║");
                System.out.println("║  5. Eliminar pedido                      ║");
            }
            System.out.println("║  0. Volver al menú principal             ║");
            System.out.println("╚══════════════════════════════════════════╝");
            System.out.print("Selecciona una opción: ");

            switch (scanner.nextLine()) {
                case "1" -> listarPedidos();
                case "2" -> verDetallePedido();
                case "3" -> {
                    if (esAdmin()) crearPedido();
                    else opcionNoValida();
                }
                case "4" -> {
                    if (esAdmin()) actualizarEstado();
                    else opcionNoValida();
                }
                case "5" -> {
                    if (esAdmin()) eliminarPedido();
                    else opcionNoValida();
                }
                case "0" -> salir = true;
                default -> opcionNoValida();
            }
        }
    }

    /**
     * Lista todos los pedidos en formato tabla.
     */
    private void listarPedidos() {
        List<Pedido> pedidos = pedidoController.obtenerTodos();
        if (pedidos.isEmpty()) {
            System.out.println("No hay pedidos registrados.");
            return;
        }
        System.out.println("\n" + String.format("| %-5s | %-12s | %-12s | %-25s |",
                "ID", "FECHA", "ESTADO", "CLIENTE"));
        System.out.println("-".repeat(65));
        pedidos.forEach(System.out::println);
    }

    /**
     * Muestra el detalle completo de un pedido con sus líneas.
     */
    private void verDetallePedido() {
        System.out.print("Introduce el id del pedido: ");
        try {
            int id = Integer.parseInt(scanner.nextLine());
            Pedido pedido = pedidoController.obtenerPorId(id);
            if (pedido == null) {
                System.out.println("Pedido no encontrado.");
                return;
            }
            System.out.println("\n--- DETALLE DEL PEDIDO ---");
            System.out.println("Id:      " + pedido.getId());
            System.out.println("Fecha:   " + pedido.getFecha());
            System.out.println("Estado:  " + pedido.getEstado());
            System.out.println("Cliente: " + pedido.getCliente().getNombre());

            List<LineaPedido> lineas = pedidoController.obtenerLineasPorPedido(id);
            if (lineas.isEmpty()) {
                System.out.println("\nEste pedido no tiene productos.");
            } else {
                System.out.println("\n" + String.format("| %-25s | %-8s | %-10s | %-10s |",
                        "PRODUCTO", "CANTIDAD", "PRECIO", "SUBTOTAL"));
                System.out.println("-".repeat(65));
                lineas.forEach(System.out::println);
                double total = lineas.stream()
                        .mapToDouble(l -> l.getCantidad() * l.getPrecioUnitario())
                        .sum();
                System.out.println("-".repeat(65));
                System.out.printf("TOTAL: %.2f €%n", total);
            }
        } catch (NumberFormatException e) {
            System.err.println("El id debe ser un número entero.");
        }
    }

    /**
     * Crea un nuevo pedido y permite añadir líneas de productos.
     */
    private void crearPedido() {
        System.out.println("\n--- CREAR PEDIDO ---");
        try {
            System.out.print("Id del cliente: ");
            int idCliente = Integer.parseInt(scanner.nextLine());

            int idPedido = pedidoController.crearPedido(idCliente);
            if (idPedido == -1) {
                System.out.println("Error al crear el pedido.");
                return;
            }
            System.out.println("Pedido creado con id: " + idPedido);

            boolean añadirMas = true;
            while (añadirMas) {
                System.out.println("\nProductos disponibles:");
                productoController.obtenerTodos().forEach(p ->
                        System.out.println("  " + p.getId() + " - " + p.getNombre() +
                                " (" + p.getPrecio() + " €)"));

                System.out.print("Id del producto (0 para terminar): ");
                int idProducto = Integer.parseInt(scanner.nextLine());
                if (idProducto == 0) {
                    añadirMas = false;
                    continue;
                }

                Producto producto = productoController.obtenerPorId(idProducto);
                if (producto == null) {
                    System.out.println("Producto no encontrado.");
                    continue;
                }

                System.out.print("Cantidad: ");
                int cantidad = Integer.parseInt(scanner.nextLine());

                LineaPedido linea = new LineaPedido(idPedido, producto, cantidad, producto.getPrecio());
                if (pedidoController.añadirLineaPedido(linea)) {
                    System.out.println("Producto añadido al pedido.");
                } else {
                    System.out.println("Error al añadir el producto.");
                }
            }
            System.out.println("Pedido finalizado correctamente.");
        } catch (NumberFormatException e) {
            System.err.println("Valor no válido.");
        }
    }

    /**
     * Actualiza el estado de un pedido existente.
     */
    private void actualizarEstado() {
        System.out.println("\n--- ACTUALIZAR ESTADO ---");
        try {
            System.out.print("Id del pedido: ");
            int id = Integer.parseInt(scanner.nextLine());

            System.out.println("Estados disponibles:");
            for (EstadoPedido estado : EstadoPedido.values()) {
                System.out.println("  " + estado.ordinal() + ". " + estado);
            }
            System.out.print("Selecciona el nuevo estado (número): ");
            int opcion = Integer.parseInt(scanner.nextLine());

            if (opcion < 0 || opcion >= EstadoPedido.values().length) {
                System.out.println("Estado no válido.");
                return;
            }

            EstadoPedido nuevoEstado = EstadoPedido.values()[opcion];
            if (pedidoController.actualizarEstado(id, nuevoEstado)) {
                System.out.println("Estado actualizado correctamente.");
            } else {
                System.out.println("Error al actualizar el estado.");
            }
        } catch (NumberFormatException e) {
            System.err.println("El id debe ser un número entero.");
        }
    }

    /**
     * Solicita confirmación y elimina un pedido.
     */
    private void eliminarPedido() {
        System.out.println("\n--- ELIMINAR PEDIDO ---");
        try {
            System.out.print("Id del pedido a eliminar: ");
            int id = Integer.parseInt(scanner.nextLine());
            Pedido pedido = pedidoController.obtenerPorId(id);
            if (pedido == null) {
                System.out.println("Pedido no encontrado.");
                return;
            }
            System.out.print("¿Seguro que quieres eliminar el pedido " + id + "'? (s/n): ");
            if (scanner.nextLine().equalsIgnoreCase("s")) {
                if (pedidoController.eliminarPedido(id)) {
                    System.out.println("Pedido eliminado correctamente.");
                } else {
                    System.out.println("Error al eliminar el pedido.");
                }
            } else {
                System.out.println("Operación cancelada.");
            }
        } catch (NumberFormatException e) {
            System.err.println("El id debe ser un número entero.");
        }
    }

    private boolean esAdmin() {
        return usuarioActivo.getRol().equals("administrador");
    }

    private void opcionNoValida() {
        System.out.println("Opción no válida o sin permisos suficientes.");
    }
}