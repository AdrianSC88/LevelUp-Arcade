package com.levelup.modelo;

/**
 * Clase que representa un cliente de la tienda.
 */
public class Cliente {

    private int id;
    private String nombre;
    private String email;
    private String telefono;
    private String direccion;

    /**
     * Constructor vacío.
     */
    public Cliente() {}

    /**
     * Constructor con parámetros.
     * @param id identificador del cliente
     * @param nombre nombre del cliente
     * @param email email del cliente
     * @param telefono teléfono del cliente
     * @param direccion dirección del cliente
     */
    public Cliente(int id, String nombre, String email, String telefono, String direccion) {
        this.id = id;
        this.nombre = nombre;
        this.email = email;
        this.telefono = telefono;
        this.direccion = direccion;
    }

    /**
     * Constructor sin id, para inserciones nuevas.
     * @param nombre nombre del cliente
     * @param email email del cliente
     * @param telefono teléfono del cliente
     * @param direccion dirección del cliente
     */
    public Cliente(String nombre, String email, String telefono, String direccion) {
        this.nombre = nombre;
        this.email = email;
        this.telefono = telefono;
        this.direccion = direccion;
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getTelefono() { return telefono; }
    public void setTelefono(String telefono) { this.telefono = telefono; }

    public String getDireccion() { return direccion; }
    public void setDireccion(String direccion) { this.direccion = direccion; }

    @Override
    public String toString() {
        return String.format("| %-5d | %-30s | %-40s | %-15s | %-40s |",
                id, nombre, email, telefono, direccion);
    }
}