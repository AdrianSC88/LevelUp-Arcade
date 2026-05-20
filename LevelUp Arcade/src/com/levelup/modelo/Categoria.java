package com.levelup.modelo;

/**
 * Clase que representa una categoría de productos.
 */
public class Categoria {

    private int id;
    private String nombre;
    private String descripcion;

    /**
     * Constructor vacío.
     */
    public Categoria() {}

    /**
     * Constructor con parámetros.
     * @param id identificador de la categoría
     * @param nombre nombre de la categoría
     * @param descripcion descripción de la categoría
     */
    public Categoria(int id, String nombre, String descripcion) {
        this.id = id;
        this.nombre = nombre;
        this.descripcion = descripcion;
    }

    /**
     * Constructor sin id, para inserciones nuevas.
     * @param nombre nombre de la categoría
     * @param descripcion descripción de la categoría
     */
    public Categoria(String nombre, String descripcion) {
        this.nombre = nombre;
        this.descripcion = descripcion;
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }

    public String getDescripcion() { return descripcion; }
    public void setDescripcion(String descripcion) { this.descripcion = descripcion; }

    @Override
    public String toString() {
        return String.format("| %-5d | %-20s | %-50s |", id, nombre, descripcion);
    }
}