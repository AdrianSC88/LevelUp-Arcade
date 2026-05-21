package com.levelup.modelo;

import java.time.LocalDate;

/**
 * Clase que representa un pedido realizado por un cliente.
 */
public class Pedido {

    private int id;
    private LocalDate fecha;
    private EstadoPedido estado;
    private Cliente cliente;

    /**
     * Constructor vacío.
     */
    public Pedido() {}

    /**
     * Constructor con parámetros.
     */
    public Pedido(int id, LocalDate fecha, EstadoPedido estado, Cliente cliente) {
        this.id = id;
        this.fecha = fecha;
        this.estado = estado;
        this.cliente = cliente;
    }

    /**
     * Constructor sin id, para inserciones nuevas.
     */
    public Pedido(LocalDate fecha, EstadoPedido estado, Cliente cliente) {
        this.fecha = fecha;
        this.estado = estado;
        this.cliente = cliente;
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public LocalDate getFecha() { return fecha; }
    public void setFecha(LocalDate fecha) { this.fecha = fecha; }

    public EstadoPedido getEstado() { return estado; }
    public void setEstado(EstadoPedido estado) { this.estado = estado; }

    public Cliente getCliente() { return cliente; }
    public void setCliente(Cliente cliente) { this.cliente = cliente; }

    @Override
    public String toString() {
        return String.format("| %-5d | %-12s | %-12s | %-25s |",
                id, fecha, estado,
                cliente != null ? cliente.getNombre() : "-");
    }
}

