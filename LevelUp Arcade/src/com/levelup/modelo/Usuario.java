package com.levelup.modelo;

/**
 * Clase que representa un usuario del sistema.
 */
public class Usuario {

    private int id;
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
     * @param nombre nombre del usuario
     * @param passwordHash contraseña hasheada del usuario
     * @param rol rol del usuario (administrador/empleado)
     */
    public Usuario(int id, String nombre, String passwordHash, String rol) {
        this.id = id;
        this.nombre = nombre;
        this.passwordHash = passwordHash;
        this.rol = rol;
    }

    /**
     * Constructor sin id, para inserciones nuevas.
     * @param nombre nombre del usuario
     * @param passwordHash contraseña hasheada del usuario
     * @param rol rol del usuario (administrador/empleado)
     */
    public Usuario(String nombre, String passwordHash, String rol) {
        this.nombre = nombre;
        this.passwordHash = passwordHash;
        this.rol = rol;
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }

    public String getPasswordHash() { return passwordHash; }
    public void setPasswordHash(String passwordHash) { this.passwordHash = passwordHash; }

    public String getRol() { return rol; }
    public void setRol(String rol) { this.rol = rol; }

    @Override
    public String toString() {
        return String.format("| %-5d | %-25s | %-15s |", id, nombre, rol);
    }
}