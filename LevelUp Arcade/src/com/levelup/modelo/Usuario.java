package com.levelup.modelo;

/**
 * Clase que representa un usuario del sistema.
 */
public class Usuario {

    private int id;
    private String nombreUsuario;
    private String nombre;
    private String passwordHash;
    private String rol;

    /**
     * Constructor vacío.
     */
    public Usuario() {}

    /**
     * Constructor con parámetros.
     * @param id identificador del usuario
     * @param nombreUsuario nombre de usuario para login
     * @param nombre nombre real del usuario
     * @param passwordHash contraseña hasheada del usuario
     * @param rol rol del usuario (administrador/empleado)
     */
    public Usuario(int id, String nombreUsuario, String nombre, String passwordHash, String rol) {
        this.id = id;
        this.nombreUsuario = nombreUsuario;
        this.nombre = nombre;
        this.passwordHash = passwordHash;
        this.rol = rol;
    }

    /**
     * Constructor sin id, para inserciones nuevas.
     * @param nombreUsuario nombre de usuario para login
     * @param nombre nombre del usuario
     * @param passwordHash contraseña hasheada del usuario
     * @param rol rol del usuario (administrador/empleado)
     */
    public Usuario(String nombreUsuario, String nombre, String passwordHash, String rol) {
        this.nombreUsuario = nombreUsuario;
        this.nombre = nombre;
        this.passwordHash = passwordHash;
        this.rol = rol;
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getNombreUsuario() { return nombreUsuario; }
    public void setNombreUsuario(String nombreUsuario) { this.nombreUsuario = nombreUsuario; }
    
    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }

    public String getPasswordHash() { return passwordHash; }
    public void setPasswordHash(String passwordHash) { this.passwordHash = passwordHash; }

    public String getRol() { return rol; }
    public void setRol(String rol) { this.rol = rol; }

    @Override
    public String toString() {
    	return String.format("| %-5d | %-20s | %-30s | %-15s |", id, nombreUsuario, nombre, rol);    
    }
}