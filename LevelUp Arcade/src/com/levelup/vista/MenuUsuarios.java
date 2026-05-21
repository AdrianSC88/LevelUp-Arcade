package com.levelup.vista;

import com.levelup.controlador.UsuarioController;
import com.levelup.modelo.Usuario;
import java.util.List;
import java.util.Scanner;

/**
 * Clase que gestiona el menú de usuarios, accesible solo para administradores.
 */
public class MenuUsuarios {

    private final UsuarioController usuarioController;
    private final Scanner scanner;

    public MenuUsuarios(Scanner scanner) {
        this.usuarioController = new UsuarioController();
        this.scanner = scanner;
    }

    /**
     * Muestra el menú de gestión de usuarios.
     */
    public void mostrar() {
        boolean salir = false;
        while (!salir) {
            System.out.println("\n╔══════════════════════════════════════════╗");
            System.out.println("║          GESTIÓN DE USUARIOS             ║");
            System.out.println("╠══════════════════════════════════════════╣");
            System.out.println("║  1. Listar todos los usuarios            ║");
            System.out.println("║  2. Añadir usuario                       ║");
            System.out.println("║  3. Actualizar contraseña                ║");
            System.out.println("║  4. Eliminar usuario                     ║");
            System.out.println("║  0. Volver al menú principal             ║");
            System.out.println("╚══════════════════════════════════════════╝");
            System.out.print("Selecciona una opción: ");

            switch (scanner.nextLine()) {
                case "1" -> listarUsuarios();
                case "2" -> añadirUsuario();
                case "3" -> actualizarPassword();
                case "4" -> eliminarUsuario();
                case "0" -> salir = true;
                default -> System.out.println("Opción no válida.");
            }
        }
    }

    /**
     * Lista todos los usuarios en formato tabla.
     */
    private void listarUsuarios() {
        List<Usuario> usuarios = usuarioController.obtenerTodos();
        if (usuarios.isEmpty()) {
            System.out.println("No hay usuarios registrados.");
            return;
        }
        System.out.println("\n" + String.format("| %-5s | %-20s | %-15s |",
                "ID", "NOMBRE", "ROL"));
        System.out.println("-".repeat(47));
        usuarios.forEach(System.out::println);
    }

    /**
     * Solicita datos y añade un nuevo usuario.
     */
    private void añadirUsuario() {
        System.out.println("\n--- AÑADIR USUARIO ---");
        System.out.print("Nombre: ");
        String nombre = scanner.nextLine();
        System.out.print("Contraseña: ");
        String password = scanner.nextLine();
        System.out.println("Roles disponibles: administrador / empleado");
        System.out.print("Rol: ");
        String rol = scanner.nextLine();

        if (usuarioController.añadirUsuario(nombre, password, rol)) {
            System.out.println("Usuario añadido correctamente.");
        } else {
            System.out.println("Error al añadir el usuario.");
        }
    }

    /**
     * Actualiza la contraseña de un usuario existente.
     */
    private void actualizarPassword() {
        System.out.println("\n--- ACTUALIZAR CONTRASEÑA ---");
        try {
            listarUsuarios();
            System.out.print("Id del usuario: ");
            int id = Integer.parseInt(scanner.nextLine());
            System.out.print("Nueva contraseña: ");
            String nuevaPassword = scanner.nextLine();
            System.out.print("Confirma la nueva contraseña: ");
            String confirmacion = scanner.nextLine();

            if (!nuevaPassword.equals(confirmacion)) {
                System.out.println("Las contraseñas no coinciden.");
                return;
            }

            if (usuarioController.actualizarPassword(id, nuevaPassword)) {
                System.out.println("Contraseña actualizada correctamente.");
            } else {
                System.out.println("Error al actualizar la contraseña.");
            }
        } catch (NumberFormatException e) {
            System.err.println("El id debe ser un número entero.");
        }
    }

    /**
     * Solicita confirmación y elimina un usuario.
     */
    private void eliminarUsuario() {
        System.out.println("\n--- ELIMINAR USUARIO ---");
        try {
            listarUsuarios();
            System.out.print("Id del usuario a eliminar: ");
            int id = Integer.parseInt(scanner.nextLine());
            System.out.print("¿Seguro que quieres eliminar este usuario? (s/n): ");
            if (scanner.nextLine().equalsIgnoreCase("s")) {
                if (usuarioController.eliminarUsuario(id)) {
                    System.out.println("Usuario eliminado correctamente.");
                } else {
                    System.out.println("Error al eliminar el usuario.");
                }
            } else {
                System.out.println("Operación cancelada.");
            }
        } catch (NumberFormatException e) {
            System.err.println("El id debe ser un número entero.");
        }
    }
}