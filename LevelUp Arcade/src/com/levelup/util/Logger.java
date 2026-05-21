package com.levelup.util;

import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * Clase utilitaria para el registro de logs de la aplicación.
 */
public class Logger {

    private static final String RUTA_LOG = "logs/levelup.log";
    private static final DateTimeFormatter FORMATO = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    /**
     * Registra un mensaje informativo en el fichero de log.
     * @param mensaje mensaje a registrar
     */
    public static void info(String mensaje) {
        escribir("INFO", mensaje);
    }

    /**
     * Registra un mensaje de error en el fichero de log.
     * @param mensaje mensaje a registrar
     */
    public static void error(String mensaje) {
        escribir("ERROR", mensaje);
    }

    /**
     * Escribe una línea en el fichero de log.
     * @param nivel nivel del log (INFO/ERROR)
     * @param mensaje mensaje a registrar
     */
    private static void escribir(String nivel, String mensaje) {
        String linea = String.format("[%s] %s - %s",
                LocalDateTime.now().format(FORMATO), nivel, mensaje);

        System.out.println(linea);

        try {
            java.io.File directorio = new java.io.File("logs");
            if (!directorio.exists()) {
                directorio.mkdir();
            }
            try (PrintWriter pw = new PrintWriter(new FileWriter(RUTA_LOG, true))) {
                pw.println(linea);
            }
        } catch (IOException e) {
            System.err.println("Error al escribir en el log: " + e.getMessage());
        }
    }
}