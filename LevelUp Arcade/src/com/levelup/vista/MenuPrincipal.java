package com.levelup.vista;

import com.levelup.controlador.UsuarioController;
import com.levelup.modelo.Usuario;
import java.util.Scanner;

/**
 * Clase que gestiona el menú principal de la aplicación.
 */
public class MenuPrincipal {

    private final UsuarioController usuarioController;
    private final Scanner scanner;
    private Usuario usuarioActivo;

    /**
     * Constructor. Inicializa el controlador de usuarios y el scanner de consola.
     */
    public MenuPrincipal() {
        this.usuarioController = new UsuarioController();
        this.scanner = new Scanner(System.in);
    }

    /**
     * Inicia la aplicación mostrando el login y el menú principal.
     */
    public void iniciar() {
        mostrarBienvenida();
        usuarioActivo = realizarLogin();
        if (usuarioActivo == null) {
            System.out.println("No se pudo autenticar. Cerrando aplicación.");
            return;
        }
        mostrarMenuPrincipal();
    }

    /**
     * Muestra el mensaje de bienvenida.
     */
    private void mostrarBienvenida() {
        System.out.println("╔══════════════════════════════════════════╗");
        System.out.println("║              LEVELUP ARCADE              ║");
        System.out.println("║     Sistema de Gestión de Inventario     ║");
        System.out.println("╚══════════════════════════════════════════╝");
    }

    /**
     * Gestiona el proceso de login del usuario.
     * @return objeto Usuario autenticado o null si falla
     */
    private Usuario realizarLogin() {
        int intentos = 0;
        while (intentos < 3) {
            System.out.println("\n--- INICIO DE SESIÓN ---");
            System.out.print("Usuario: ");
            String nombre = scanner.nextLine();
            System.out.print("Contraseña: ");
            String password = scanner.nextLine();

            Usuario usuario = usuarioController.login(nombre, password);
            if (usuario != null) {
                System.out.println("\nBienvenido, " + usuario.getNombre() +
                        " [" + usuario.getRol() + "]");
                return usuario;
            }
            intentos++;
            System.out.println("Credenciales incorrectas. Intentos restantes: " + (3 - intentos));
        }
        return null;
    }

    /**
     * Muestra el menú principal según el rol del usuario.
     */
    private void mostrarMenuPrincipal() {
        boolean salir = false;
        while (!salir) {
            System.out.println("\n╔══════════════════════════════════════════╗");
            System.out.println("║             MENÚ PRINCIPAL               ║");
            System.out.println("╠══════════════════════════════════════════╣");
            System.out.println("║  1. Gestión de Productos                 ║");
            System.out.println("║  2. Gestión de Categorías                ║");
            System.out.println("║  3. Gestión de Clientes                  ║");
            System.out.println("║  4. Gestión de Proveedores               ║");
            System.out.println("║  5. Gestión de Pedidos                   ║");
            if (usuarioActivo.getRol().equals("administrador")) {
                System.out.println("║  6. Gestión de Usuarios                  ║");
                System.out.println("║  7. Asistente IA                         ║");
            } else {
                System.out.println("║  6. Asistente IA                         ║");
            }
            System.out.println("║  0. Cerrar sesión                        ║");
            System.out.println("╚══════════════════════════════════════════╝");
            System.out.print("Selecciona una opción: ");

            String opcion = scanner.nextLine();
            salir = procesarOpcion(opcion);
        }
        System.out.println("\nSesión cerrada. ¡Hasta pronto!");
    }

    /**
     * Procesa la opción seleccionada en el menú principal.
     * @param opcion opción seleccionada
     * @return true si el usuario quiere salir
     */
    private boolean procesarOpcion(String opcion) {
        boolean esAdmin = usuarioActivo.getRol().equals("administrador");
        switch (opcion) {
            case "1" -> new MenuProductos(scanner, usuarioActivo).mostrar();
            case "2" -> new MenuCategorias(scanner, usuarioActivo).mostrar();
            case "3" -> new MenuClientes(scanner, usuarioActivo).mostrar();
            case "4" -> new MenuProveedores(scanner, usuarioActivo).mostrar();
            case "5" -> new MenuPedidos(scanner, usuarioActivo).mostrar();
            case "6" -> {
                if (esAdmin) new MenuUsuarios(scanner).mostrar();
                else new MenuIA(scanner).mostrarMenu();
            }
            case "7" -> {
               if (esAdmin) new MenuIA(scanner).mostrarMenu();
               else System.out.println("Opción no válida.");
            }
            case "0" -> { return true; }
            default -> System.out.println("Opción no válida, inténtalo de nuevo.");
        }
        return false;
    }
}