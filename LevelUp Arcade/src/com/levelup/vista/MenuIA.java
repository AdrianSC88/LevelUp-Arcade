package com.levelup.vista;

import com.levelup.controlador.LlmController;

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
        	System.out.println("\n╔══════════════════════════════════════╗");
        	System.out.println("║           ASISTENTE IA               ║");
        	System.out.println("╠══════════════════════════════════════╣");
        	System.out.println("║  1. Generar descripción de producto  ║");
        	System.out.println("║  2. Sugerir categoría para producto  ║");
        	System.out.println("║  0. Volver al menú principal         ║");
        	System.out.println("╚══════════════════════════════════════╝");
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
        System.out.print("Introduce el nombre del producto: ");
        String nombre = scanner.nextLine();
        System.out.println("\nGenerando descripción, espera...");
        String descripcion = llmController.generarDescripcion(nombre);
        System.out.println("\nDescripción generada:\n" + descripcion);
    }

    private void sugerirCategoria() {
        System.out.print("Introduce el nombre del producto: ");
        String nombre = scanner.nextLine();
        System.out.println("\nConsultando categoría, espera...");
        String categoria = llmController.sugerirCategoria(nombre);
        System.out.println("\nCategoría sugerida: " + categoria);
    }
}