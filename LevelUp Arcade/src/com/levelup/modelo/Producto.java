package com.levelup.modelo;

/**
 * Clase que representa un producto del inventario de LevelUp Arcade.
 */
public class Producto {

    private int id;
    private String nombre;
    private String descripcion;
    private double precio;
    private int stock;
    private Categoria categoria;
    private Proveedor proveedor;

    /**
     * Constructor vacío.
     */
    public Producto() {}

    /**
     * Constructor completo.
     */
    public Producto(int id, String nombre, String descripcion, double precio, int stock,
                    Categoria categoria, Proveedor proveedor) {
        this.id = id;
        this.nombre = nombre;
        this.descripcion = descripcion;
        this.precio = precio;
        this.stock = stock;
        this.categoria = categoria;
        this.proveedor = proveedor;
    }

    // Getters y Setters

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }

    public String getDescripcion() { return descripcion; }
    public void setDescripcion(String descripcion) { this.descripcion = descripcion; }

    public double getPrecio() { return precio; }
    public void setPrecio(double precio) { this.precio = precio; }

    public int getStock() { return stock; }
    public void setStock(int stock) { this.stock = stock; }

    public Categoria getCategoria() { return categoria; }
    public void setCategoria(Categoria categoria) { this.categoria = categoria; }

    public Proveedor getProveedor() { return proveedor; }
    public void setProveedor(Proveedor proveedor) { this.proveedor = proveedor; }

    /**
     * Representación en texto del producto.
     */
    @Override
    public String toString() {
        return String.format("| %-5d | %-25s | %-10.2f | %-8d | %-15s | %-15s |",
                id, nombre, precio, stock,
                categoria != null ? categoria.getNombre() : "-",
                proveedor != null ? proveedor.getNombre() : "-");
    }
    
}