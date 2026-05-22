package com.levelup.vista;

import com.levelup.controlador.CategoriaController;
import com.levelup.modelo.Categoria;
import com.levelup.modelo.Usuario;
import java.util.List;
import java.util.Scanner;

/**
 * Clase que gestiona el menú de categorías.
 */
public class MenuCategorias {

    private final CategoriaController categoriaController;
    private final Scanner scanner;
    private final Usuario usuarioActivo;

    /**
     * Constructor. Inicializa el controlador de categorías y los parámetros de sesión.
     *
     * @param scanner       scanner para la lectura de datos por consola
     * @param usuarioActivo usuario que ha iniciado sesión en el sistema
     */
    public MenuCategorias(Scanner scanner, Usuario usuarioActivo) {
        this.categoriaController = new CategoriaController();
        this.scanner = scanner;
        this.usuarioActivo = usuarioActivo;
    }

    /**
     * Muestra el menú de gestión de categorías.
     */
    public void mostrar() {
        boolean salir = false;
        while (!salir) {
            System.out.println("\n╔══════════════════════════════════════════╗");
            System.out.println("║         GESTIÓN DE CATEGORÍAS            ║");
            System.out.println("╠══════════════════════════════════════════╣");
            System.out.println("║  1. Listar todas las categorías          ║");
            System.out.println("║  2. Buscar categoría por id              ║");
            if (usuarioActivo.getRol().equals("administrador")) {
                System.out.println("║  3. Añadir categoría                     ║");
                System.out.println("║  4. Actualizar categoría                 ║");
                System.out.println("║  5. Eliminar categoría                   ║");
            }
            System.out.println("║  0. Volver al menú principal             ║");
            System.out.println("╚══════════════════════════════════════════╝");
            System.out.print("Selecciona una opción: ");

            switch (scanner.nextLine()) {
                case "1" -> listarCategorias();
                case "2" -> buscarCategoria();
                case "3" -> {
                    if (esAdmin()) añadirCategoria();
                    else opcionNoValida();
                }
                case "4" -> {
                    if (esAdmin()) actualizarCategoria();
                    else opcionNoValida();
                }
                case "5" -> {
                    if (esAdmin()) eliminarCategoria();
                    else opcionNoValida();
                }
                case "0" -> salir = true;
                default -> opcionNoValida();
            }
        }
    }

    /**
     * Lista todas las categorías en formato tabla.
     */
    private void listarCategorias() {
        List<Categoria> categorias = categoriaController.obtenerTodas();
        if (categorias.isEmpty()) {
            System.out.println("No hay categorías registradas.");
            return;
        }
        System.out.println("\n" + String.format("| %-5s | %-30s | %-70s |",
                "ID", "NOMBRE", "DESCRIPCIÓN"));
        System.out.println("-".repeat(115));
        categorias.forEach(System.out::println);
    }

    /**
     * Busca y muestra una categoría por su id.
     */
    private void buscarCategoria() {
        System.out.print("Introduce el id de la categoría: ");
        try {
            int id = Integer.parseInt(scanner.nextLine());
            Categoria categoria = categoriaController.obtenerPorId(id);
            if (categoria != null) {
                System.out.println("\n" + String.format("| %-5s | %-30s | %-70s |",
                        "ID", "NOMBRE", "DESCRIPCIÓN"));
                System.out.println("-".repeat(115));
                System.out.println(categoria);
            } else {
                System.out.println("Categoría no encontrada.");
            }
        } catch (NumberFormatException e) {
            System.err.println("El id debe ser un número entero.");
        }
    }

    /**
     * Solicita datos y añade una nueva categoría.
     */
    private void añadirCategoria() {
        System.out.println("\n--- AÑADIR CATEGORÍA ---");
        System.out.print("Nombre: ");
        String nombre = scanner.nextLine();
        System.out.print("Descripción: ");
        String descripcion = scanner.nextLine();

        if (categoriaController.añadirCategoria(nombre, descripcion)) {
            System.out.println("Categoría añadida correctamente.");
        } else {
            System.out.println("Error al añadir la categoría.");
        }
    }

    /**
     * Solicita datos y actualiza una categoría existente.
     */
    private void actualizarCategoria() {
        System.out.println("\n--- ACTUALIZAR CATEGORÍA ---");
        try {
            System.out.print("Id de la categoría a actualizar: ");
            int id = Integer.parseInt(scanner.nextLine());
            Categoria categoria = categoriaController.obtenerPorId(id);
            if (categoria == null) {
                System.out.println("Categoría no encontrada.");
                return;
            }
            System.out.print("Nuevo nombre (" + categoria.getNombre() + "): ");
            String nombre = scanner.nextLine();
            System.out.print("Nueva descripción (" + categoria.getDescripcion() + "): ");
            String descripcion = scanner.nextLine();

            if (categoriaController.actualizarCategoria(id, nombre, descripcion)) {
                System.out.println("Categoría actualizada correctamente.");
            } else {
                System.out.println("Error al actualizar la categoría.");
            }
        } catch (NumberFormatException e) {
            System.err.println("El id debe ser un número entero.");
        }
    }

    /**
     * Solicita confirmación y elimina una categoría.
     */
    private void eliminarCategoria() {
        System.out.println("\n--- ELIMINAR CATEGORÍA ---");
        try {
            System.out.print("Id de la categoría a eliminar: ");
            int id = Integer.parseInt(scanner.nextLine());
            Categoria categoria = categoriaController.obtenerPorId(id);
            if (categoria == null) {
                System.out.println("Categoría no encontrada.");
                return;
            }
            System.out.print("¿Seguro que quieres eliminar '" + categoria.getNombre() + "'? (s/n): ");
            if (scanner.nextLine().equalsIgnoreCase("s")) {
                if (categoriaController.eliminarCategoria(id)) {
                    System.out.println("Categoría eliminada correctamente.");
                } else {
                    System.out.println("Error al eliminar la categoría.");
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