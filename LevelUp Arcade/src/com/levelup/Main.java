package com.levelup;

import com.levelup.vista.MenuPrincipal;
import com.levelup.gui.LoginFrame;

import javax.swing.*;
import java.util.Scanner;

/**
 * Clase principal que inicia la aplicación LevelUp Arcade.
 */
public class Main {

    /**
     * Punto de entrada de la aplicación LevelUp Arcade.
     * Permite elegir entre modo consola o modo interfaz gráfica.
     *
     * @param args argumentos de línea de comandos (no utilizados)
     */
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        System.out.println("╔══════════════════════════════════════════╗");
        System.out.println("║            LEVELUP ARCADE                ║");
        System.out.println("║     Sistema de Gestión de Inventario     ║");
        System.out.println("╠══════════════════════════════════════════╣");
        System.out.println("║  Selecciona el modo de inicio:           ║");
        System.out.println("║  [1] Consola                             ║");
        System.out.println("║  [2] Interfaz gráfica (GUI)              ║");
        System.out.println("╚══════════════════════════════════════════╝");
        System.out.print("Selecciona una opción: ");

        String opcion = scanner.nextLine().trim();

        switch (opcion) {
            case "1" -> new MenuPrincipal().iniciar();
            case "2" -> {
                System.setProperty("gui.mode", "true");
                SwingUtilities.invokeLater(() -> {
                    LoginFrame login = new LoginFrame();
                    login.setVisible(true);
                    login.toFront();
                    login.requestFocus();
                });            
            }
            default  -> {
                System.out.println("Opción no válida. Iniciando consola por defecto.");
                new MenuPrincipal().iniciar();
            }
        }
    }
}
