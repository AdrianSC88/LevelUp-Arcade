package com.levelup.dao;

import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;

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
        try (InputStream input = ConexionBD.class
                .getClassLoader()
                .getResourceAsStream("config.properties")) {
            props.load(input);
            URL = props.getProperty("db.url");
            USUARIO = props.getProperty("db.usuario");
            PASSWORD = props.getProperty("db.password");
        } catch (IOException e) {
            throw new RuntimeException("No se pudo cargar config.properties", e);
        }
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


