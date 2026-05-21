package com.levelup.vista;

import com.levelup.controlador.ClienteController;
import com.levelup.modelo.Cliente;
import com.levelup.modelo.Usuario;
import java.util.List;
import java.util.Scanner;

/**
 * Clase que gestiona el menú de clientes.
 */
public class MenuClientes {

    private final ClienteController clienteController;
    private final Scanner scanner;
    private final Usuario usuarioActivo;

    public MenuClientes(Scanner scanner, Usuario usuarioActivo) {
        this.clienteController = new ClienteController();
        this.scanner = scanner;
        this.usuarioActivo = usuarioActivo;
    }

    /**
     * Muestra el menú de gestión de clientes.
     */
    public void mostrar() {
        boolean salir = false;
        while (!salir) {
            System.out.println("\n╔══════════════════════════════════════════╗");
            System.out.println("║          GESTIÓN DE CLIENTES             ║");
            System.out.println("╠══════════════════════════════════════════╣");
            System.out.println("║  1. Listar todos los clientes            ║");
            System.out.println("║  2. Buscar cliente por id                ║");
            if (usuarioActivo.getRol().equals("administrador")) {
                System.out.println("║  3. Añadir cliente                       ║");
                System.out.println("║  4. Actualizar cliente                   ║");
                System.out.println("║  5. Eliminar cliente                     ║");
            }
            System.out.println("║  0. Volver al menú principal             ║");
            System.out.println("╚══════════════════════════════════════════╝");
            System.out.print("Selecciona una opción: ");

            switch (scanner.nextLine()) {
                case "1" -> listarClientes();
                case "2" -> buscarCliente();
                case "3" -> {
                    if (esAdmin()) añadirCliente();
                    else opcionNoValida();
                }
                case "4" -> {
                    if (esAdmin()) actualizarCliente();
                    else opcionNoValida();
                }
                case "5" -> {
                    if (esAdmin()) eliminarCliente();
                    else opcionNoValida();
                }
                case "0" -> salir = true;
                default -> opcionNoValida();
            }
        }
    }

    /**
     * Lista todos los clientes en formato tabla.
     */
    private void listarClientes() {
        List<Cliente> clientes = clienteController.obtenerTodos();
        if (clientes.isEmpty()) {
            System.out.println("No hay clientes registrados.");
            return;
        }
        System.out.println("\n" + String.format("| %-5s | %-30s | %-40s | %-15s | %-40s |",
                "ID", "NOMBRE", "EMAIL", "TELÉFONO", "DIRECCIÓN"));
        System.out.println("-".repeat(146));
        clientes.forEach(System.out::println);
    }

    /**
     * Busca y muestra un cliente por su id.
     */
    private void buscarCliente() {
        System.out.print("Introduce el id del cliente: ");
        try {
            int id = Integer.parseInt(scanner.nextLine());
            Cliente cliente = clienteController.obtenerPorId(id);
            if (cliente != null) {
                System.out.println("\n" + String.format("| %-5s | %-30s | %-40s | %-15s | %-40s |",
                        "ID", "NOMBRE", "EMAIL", "TELÉFONO", "DIRECCIÓN"));
                System.out.println("-".repeat(146));
                System.out.println(cliente);
            } else {
                System.out.println("Cliente no encontrado.");
            }
        } catch (NumberFormatException e) {
            System.err.println("El id debe ser un número entero.");
        }
    }

    /**
     * Solicita datos y añade un nuevo cliente.
     */
    private void añadirCliente() {
        System.out.println("\n--- AÑADIR CLIENTE ---");
        System.out.print("Nombre: ");
        String nombre = scanner.nextLine();
        System.out.print("Email: ");
        String email = scanner.nextLine();
        System.out.print("Teléfono: ");
        String telefono = scanner.nextLine();
        System.out.print("Dirección: ");
        String direccion = scanner.nextLine();

        if (clienteController.añadirCliente(nombre, email, telefono, direccion)) {
            System.out.println("Cliente añadido correctamente.");
        } else {
            System.out.println("Error al añadir el cliente.");
        }
    }

    /**
     * Solicita datos y actualiza un cliente existente.
     */
    private void actualizarCliente() {
        System.out.println("\n--- ACTUALIZAR CLIENTE ---");
        try {
            System.out.print("Id del cliente a actualizar: ");
            int id = Integer.parseInt(scanner.nextLine());
            Cliente cliente = clienteController.obtenerPorId(id);
            if (cliente == null) {
                System.out.println("Cliente no encontrado.");
                return;
            }
            System.out.print("Nuevo nombre (" + cliente.getNombre() + "): ");
            String nombre = scanner.nextLine();
            System.out.print("Nuevo email (" + cliente.getEmail() + "): ");
            String email = scanner.nextLine();
            System.out.print("Nuevo teléfono (" + cliente.getTelefono() + "): ");
            String telefono = scanner.nextLine();
            System.out.print("Nueva dirección (" + cliente.getDireccion() + "): ");
            String direccion = scanner.nextLine();

            if (clienteController.actualizarCliente(id, nombre, email, telefono, direccion)) {
                System.out.println("Cliente actualizado correctamente.");
            } else {
                System.out.println("Error al actualizar el cliente.");
            }
        } catch (NumberFormatException e) {
            System.err.println("El id debe ser un número entero.");
        }
    }

    /**
     * Solicita confirmación y elimina un cliente.
     */
    private void eliminarCliente() {
        System.out.println("\n--- ELIMINAR CLIENTE ---");
        try {
            System.out.print("Id del cliente a eliminar: ");
            int id = Integer.parseInt(scanner.nextLine());
            Cliente cliente = clienteController.obtenerPorId(id);
            if (cliente == null) {
                System.out.println("Cliente no encontrado.");
                return;
            }
            System.out.print("¿Seguro que quieres eliminar '" + cliente.getNombre() + "'? (s/n): ");
            if (scanner.nextLine().equalsIgnoreCase("s")) {
                if (clienteController.eliminarCliente(id)) {
                    System.out.println("Cliente eliminado correctamente.");
                } else {
                    System.out.println("Error al eliminar el cliente.");
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