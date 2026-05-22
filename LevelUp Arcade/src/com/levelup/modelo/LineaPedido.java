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
     *
     * @param idPedido       identificador del pedido al que pertenece la línea
     * @param producto       producto asociado a la línea de pedido
     * @param cantidad       cantidad de unidades del producto
     * @param precioUnitario precio unitario del producto en el momento del pedido
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
        return String.format("| %-50s | %-8d | %-10.2f | %-10.2f |",
                producto != null ? producto.getNombre() : "-",
                cantidad,
                precioUnitario,
                cantidad * precioUnitario);
    }
}