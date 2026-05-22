package com.levelup.vista;

import com.levelup.controlador.LlmController;
import com.levelup.controlador.ProductoController;
import com.levelup.modelo.Producto;

import java.util.Scanner;

/**
 * Menú de opciones para las funcionalidades de Inteligencia Artificial.
 */
public class MenuIA {

    private final LlmController llmController;
    private final Scanner scanner;

    public MenuIA(Scanner scanner) {
        this.llmController = new LlmController();
        this.scanner = scanner;
    }

    /**
     * Muestra el menú principal de IA y gestiona la navegación.
     */
    public void mostrarMenu() {
        int opcion;
        do {
        	System.out.println("\n╔════════════════════════════════════════════════════════════╗");
        	System.out.println("║                      ASISTENTE IA                          ║");
        	System.out.println("╠════════════════════════════════════════════════════════════╣");
        	System.out.println("║  1. Generar descripción de producto (solo consulta)        ║");
        	System.out.println("║  2. Sugerir categoría para producto (solo consulta)        ║");
        	System.out.println("║  0. Volver al menú principal                               ║");
        	System.out.println("╚════════════════════════════════════════════════════════════╝");
        	System.out.print("Selecciona una opción: ");

            opcion = scanner.nextInt();
            scanner.nextLine();

            switch (opcion) {
                case 1 -> generarDescripcion();
                case 2 -> sugerirCategoria();
                case 0 -> System.out.println("Volviendo al menú principal...");
                default -> System.out.println("Opción no válida.");
            }
        } while (opcion != 0);
    }

    private void generarDescripcion() {
        System.out.print("Introduce el ID del producto: ");
        try {
            int id = Integer.parseInt(scanner.nextLine());
            Producto producto = new ProductoController().obtenerPorId(id);
            if (producto == null) {
                System.out.println("Producto no encontrado.");
                return;
            }
            System.out.println("Generando descripción para: " + producto.getNombre() + ", espera...");
            String descripcion = llmController.generarDescripcion(producto.getNombre());
            System.out.println("\nDescripción generada (deberás añadirla o modificarla manualmente):\n" + descripcion);

        } catch (NumberFormatException e) {
            System.out.println("El ID debe ser un número entero.");
        }
    }

    private void sugerirCategoria() {
        System.out.print("Introduce el ID del producto: ");
        try {
            int id = Integer.parseInt(scanner.nextLine());
            Producto producto = new ProductoController().obtenerPorId(id);
            if (producto == null) {
                System.out.println("Producto no encontrado.");
                return;
            }
            System.out.println("Consultando categoría para: " + producto.getNombre() + ", espera...");
            String categoria = llmController.sugerirCategoria(producto.getNombre());
            System.out.println("\nCategoría sugerida (deberás asignarla manualmente al producto): " + categoria);
        } catch (NumberFormatException e) {
            System.out.println("El ID debe ser un número entero.");
        }
    }
}