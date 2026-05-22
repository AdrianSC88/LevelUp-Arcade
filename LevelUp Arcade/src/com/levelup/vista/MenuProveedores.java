package com.levelup.vista;

import com.levelup.controlador.ProveedorController;
import com.levelup.modelo.Proveedor;
import com.levelup.modelo.Usuario;
import java.util.List;
import java.util.Scanner;

/**
 * Clase que gestiona el menú de proveedores.
 */
public class MenuProveedores {

    private final ProveedorController proveedorController;
    private final Scanner scanner;
    private final Usuario usuarioActivo;

    /**
     * Constructor. Inicializa el controlador de proveedores y los parámetros de sesión.
     *
     * @param scanner       scanner para la lectura de datos por consola
     * @param usuarioActivo usuario que ha iniciado sesión en el sistema
     */
    public MenuProveedores(Scanner scanner, Usuario usuarioActivo) {
        this.proveedorController = new ProveedorController();
        this.scanner = scanner;
        this.usuarioActivo = usuarioActivo;
    }

    /**
     * Muestra el menú de gestión de proveedores.
     */
    public void mostrar() {
        boolean salir = false;
        while (!salir) {
            System.out.println("\n╔══════════════════════════════════════════╗");
            System.out.println("║         GESTIÓN DE PROVEEDORES           ║");
            System.out.println("╠══════════════════════════════════════════╣");
            System.out.println("║  1. Listar todos los proveedores         ║");
            System.out.println("║  2. Buscar proveedor por id              ║");
            if (usuarioActivo.getRol().equals("administrador")) {
                System.out.println("║  3. Añadir proveedor                     ║");
                System.out.println("║  4. Actualizar proveedor                 ║");
                System.out.println("║  5. Eliminar proveedor                   ║");
            }
            System.out.println("║  0. Volver al menú principal             ║");
            System.out.println("╚══════════════════════════════════════════╝");
            System.out.print("Selecciona una opción: ");

            switch (scanner.nextLine()) {
                case "1" -> listarProveedores();
                case "2" -> buscarProveedor();
                case "3" -> {
                    if (esAdmin()) añadirProveedor();
                    else opcionNoValida();
                }
                case "4" -> {
                    if (esAdmin()) actualizarProveedor();
                    else opcionNoValida();
                }
                case "5" -> {
                    if (esAdmin()) eliminarProveedor();
                    else opcionNoValida();
                }
                case "0" -> salir = true;
                default -> opcionNoValida();
            }
        }
    }

    /**
     * Lista todos los proveedores en formato tabla.
     */
    private void listarProveedores() {
        List<Proveedor> proveedores = proveedorController.obtenerTodos();
        if (proveedores.isEmpty()) {
            System.out.println("No hay proveedores registrados.");
            return;
        }
        System.out.println("\n" + String.format("| %-5s | %-30s | %-40s | %-40s | %-15s |",
                "ID", "NOMBRE", "EMAIL", "DIRECCIÓN", "TELÉFONO"));
        System.out.println("-".repeat(146));
        proveedores.forEach(System.out::println);
    }

    /**
     * Busca y muestra un proveedor por su id.
     */
    private void buscarProveedor() {
        System.out.print("Introduce el id del proveedor: ");
        try {
            int id = Integer.parseInt(scanner.nextLine());
            Proveedor proveedor = proveedorController.obtenerPorId(id);
            if (proveedor != null) {
                System.out.println("\n" + String.format("| %-5s | %-30s | %-40s | %-40s | %-15s |",
                        "ID", "NOMBRE", "EMAIL", "DIRECCIÓN", "TELÉFONO"));
                System.out.println("-".repeat(146));
                System.out.println(proveedor);
            } else {
                System.out.println("Proveedor no encontrado.");
            }
        } catch (NumberFormatException e) {
            System.err.println("El id debe ser un número entero.");
        }
    }

    /**
     * Solicita datos y añade un nuevo proveedor.
     */
    private void añadirProveedor() {
        System.out.println("\n--- AÑADIR PROVEEDOR ---");
        System.out.print("Nombre: ");
        String nombre = scanner.nextLine();
        System.out.print("Email: ");
        String email = scanner.nextLine();
        System.out.print("Dirección: ");
        String direccion = scanner.nextLine();
        System.out.print("Teléfono: ");
        String telefono = scanner.nextLine();

        if (proveedorController.añadirProveedor(nombre, email, direccion, telefono)) {
            System.out.println("Proveedor añadido correctamente.");
        } else {
            System.out.println("Error al añadir el proveedor.");
        }
    }

    /**
     * Solicita datos y actualiza un proveedor existente.
     */
    private void actualizarProveedor() {
        System.out.println("\n--- ACTUALIZAR PROVEEDOR ---");
        try {
            System.out.print("Id del proveedor a actualizar: ");
            int id = Integer.parseInt(scanner.nextLine());
            Proveedor proveedor = proveedorController.obtenerPorId(id);
            if (proveedor == null) {
                System.out.println("Proveedor no encontrado.");
                return;
            }
            System.out.print("Nuevo nombre (" + proveedor.getNombre() + "): ");
            String nombre = scanner.nextLine();
            System.out.print("Nuevo email (" + proveedor.getEmail() + "): ");
            String email = scanner.nextLine();
            System.out.print("Nueva dirección (" + proveedor.getDireccion() + "): ");
            String direccion = scanner.nextLine();
            System.out.print("Nuevo teléfono (" + proveedor.getTelefono() + "): ");
            String telefono = scanner.nextLine();

            if (proveedorController.actualizarProveedor(id, nombre, email, direccion, telefono)) {
                System.out.println("Proveedor actualizado correctamente.");
            } else {
                System.out.println("Error al actualizar el proveedor.");
            }
        } catch (NumberFormatException e) {
            System.err.println("El id debe ser un número entero.");
        }
    }

    /**
     * Solicita confirmación y elimina un proveedor.
     */
    private void eliminarProveedor() {
        System.out.println("\n--- ELIMINAR PROVEEDOR ---");
        try {
            System.out.print("Id del proveedor a eliminar: ");
            int id = Integer.parseInt(scanner.nextLine());
            Proveedor proveedor = proveedorController.obtenerPorId(id);
            if (proveedor == null) {
                System.out.println("Proveedor no encontrado.");
                return;
            }
            System.out.print("¿Seguro que quieres eliminar '" + proveedor.getNombre() + "'? (s/n): ");
            if (scanner.nextLine().equalsIgnoreCase("s")) {
                if (proveedorController.eliminarProveedor(id)) {
                    System.out.println("Proveedor eliminado correctamente.");
                } else {
                    System.out.println("Error al eliminar el proveedor.");
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