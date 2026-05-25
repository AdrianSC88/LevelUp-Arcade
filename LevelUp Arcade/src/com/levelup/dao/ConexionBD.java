package com.levelup.dao;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;
import java.util.Scanner;

/**
 * Clase encargada de gestionar la conexión con la base de datos.
 */
public class ConexionBD {

    private static final String URL;
    private static final String USUARIO;
    private static final String PASSWORD;

    static {
        try {
            Class.forName("org.mariadb.jdbc.Driver");
        } catch (ClassNotFoundException e) {
            throw new RuntimeException("Driver MariaDB no encontrado", e);
        }

        Properties props = new Properties();
        File configFile = new File("config.properties");

        if (configFile.exists()) {
            try (InputStream input = new FileInputStream(configFile)) {
                props.load(input);
            } catch (IOException e) {
                throw new RuntimeException("Error al cargar config.properties", e);
            }
        } else {
            System.out.println("\n╔══════════════════════════════════════════╗");
            System.out.println("║       CONFIGURACIÓN INICIAL              ║");
            System.out.println("╚══════════════════════════════════════════╝");
            System.out.println("No se encontró config.properties.");
            System.out.println("Introduce los datos de conexión a la base de datos:\n");
            System.out.println("NOTA: El usuario y contraseña son los del servidor de base de datos,");
            System.out.println("no los de acceso a la aplicación.\n");
            
            Scanner scanner = new Scanner(System.in);

            System.out.print("IP del servidor: ");
            String host = scanner.nextLine().trim();
            System.out.print("Puerto (por defecto MySQL -> 3306, MariaDB -> 3308): ");
            String puerto = scanner.nextLine().trim();
            if (puerto.isEmpty()) puerto = "3306";
            System.out.print("Nombre de la base de datos: ");
            String bd = scanner.nextLine().trim();
            System.out.print("Usuario: ");
            String usuario = scanner.nextLine().trim();
            System.out.print("Contraseña: ");
            String password = scanner.nextLine().trim();

            String url = "jdbc:mariadb://" + host + ":" + puerto + "/" + bd;
            props.setProperty("db.url", url);
            props.setProperty("db.usuario", usuario);
            props.setProperty("db.password", password);

            try {
                props.store(new FileOutputStream(new File("config.properties")),
                        "Configuración generada automáticamente");
                System.out.println("\nconfig.properties creado correctamente.");
                System.out.println("La próxima vez no será necesario configurarlo.\n");
            } catch (IOException e) {
                System.err.println("No se pudo guardar config.properties: " + e.getMessage());
            }
        }

        URL = props.getProperty("db.url");
        USUARIO = props.getProperty("db.usuario");
        PASSWORD = props.getProperty("db.password");
    }

    /**
     * Obtiene una conexión con la base de datos.
     * @return objeto Connection
     * @throws SQLException si no se puede establecer la conexión
     */
    public static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(URL, USUARIO, PASSWORD);
    }
}