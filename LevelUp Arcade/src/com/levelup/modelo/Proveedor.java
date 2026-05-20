package com.levelup.modelo;

/**
 * Clase que representa un proveedor de productos.
 */
public class Proveedor {

    private int id;
    private String nombre;
    private String email;
    private String direccion;
    private String telefono;

    /**
     * Constructor vacío.
     */
    public Proveedor() {}

    /**
     * Constructor con parámetros.
     * @param id identificador del proveedor
     * @param nombre nombre del proveedor
     * @param email email del proveedor
     * @param direccion dirección del proveedor
     * @param telefono teléfono del proveedor
     */
    public Proveedor(int id, String nombre, String email, String direccion, String telefono) {
        this.id = id;
        this.nombre = nombre;
        this.email = email;
        this.direccion = direccion;
        this.telefono = telefono;
    }

    /**
     * Constructor sin id, para inserciones nuevas.
     * @param nombre nombre del proveedor
     * @param email email del proveedor
     * @param direccion dirección del proveedor
     * @param telefono teléfono del proveedor
     */
    public Proveedor(String nombre, String email, String direccion, String telefono) {
        this.nombre = nombre;
        this.email = email;
        this.direccion = direccion;
        this.telefono = telefono;
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getDireccion() { return direccion; }
    public void setDireccion(String direccion) { this.direccion = direccion; }

    public String getTelefono() { return telefono; }
    public void setTelefono(String telefono) { this.telefono = telefono; }

    @Override
    public String toString() {
        return String.format("| %-5d | %-20s | %-30s | %-30s | %-15s |",
                id, nombre, email, direccion, telefono);
    }
}