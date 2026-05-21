package com.levelup.modelo;

/**
 * Clase que representa una línea de pedido (producto dentro de un pedido).
 */
public class LineaPedido {

    private int idPedido;
    private Producto producto;
    private int cantidad;
    private double precioUnitario;

    /**
     * Constructor vacío.
     */
    public LineaPedido() {}

    /**
     * Constructor con parámetros.
     */
    public LineaPedido(int idPedido, Producto producto, int cantidad, double precioUnitario) {
        this.idPedido = idPedido;
        this.producto = producto;
        this.cantidad = cantidad;
        this.precioUnitario = precioUnitario;
    }

    public int getIdPedido() { return idPedido; }
    public void setIdPedido(int idPedido) { this.idPedido = idPedido; }

    public Producto getProducto() { return producto; }
    public void setProducto(Producto producto) { this.producto = producto; }

    public int getCantidad() { return cantidad; }
    public void setCantidad(int cantidad) { this.cantidad = cantidad; }

    public double getPrecioUnitario() { return precioUnitario; }
    public void setPrecioUnitario(double precioUnitario) { this.precioUnitario = precioUnitario; }

    @Override
    public String toString() {
        return String.format("| %-25s | %-8d | %-10.2f | %-10.2f |",
                producto != null ? producto.getNombre() : "-",
                cantidad,
                precioUnitario,
                cantidad * precioUnitario);
    }
}