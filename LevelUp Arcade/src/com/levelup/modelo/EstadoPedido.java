package com.levelup.modelo;

/**
 * Enumerado que representa los posibles estados de un pedido.
 */
public enum EstadoPedido {
    /** Pedido registrado pendiente de ser procesado. */
    PENDIENTE,
    /** Pedido en proceso de preparación. */
    PROCESANDO,
    /** Pedido enviado al cliente. */
    ENVIADO,
    /** Pedido recibido por el cliente. */
    ENTREGADO,
    /** Pedido cancelado. */
    CANCELADO
}