package com.levelup.gui.paneles;

import com.levelup.controlador.ClienteController;
import com.levelup.controlador.PedidoController;
import com.levelup.controlador.ProductoController;
import com.levelup.gui.GUIUtils;
import com.levelup.modelo.Cliente;
import com.levelup.modelo.EstadoPedido;
import com.levelup.modelo.LineaPedido;
import com.levelup.modelo.Pedido;
import com.levelup.modelo.Producto;
import com.levelup.modelo.Usuario;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.*;
import java.awt.*;
import java.util.List;

/**
 * Panel de gestión de pedidos. Permite visualizar, buscar, crear,
 * cambiar estado y eliminar pedidos según el rol del usuario.
 */
public class PedidosPanel extends JPanel {

    private final PedidoController   pedidoController;
    private final ClienteController  clienteController;
    private final ProductoController productoController;
    private final boolean            esAdmin;
    private JTable                   tabla;
    private DefaultTableModel        modeloTabla;
    private TableRowSorter<DefaultTableModel> sorter;
    private JPanel                   cuerpo;

    /** Constructor. Recibe el usuario activo para controlar permisos. */
    public PedidosPanel(Usuario usuarioActivo) {
        this.esAdmin            = "administrador".equals(usuarioActivo.getRol());
        this.pedidoController   = new PedidoController();
        this.clienteController  = new ClienteController();
        this.productoController = new ProductoController();
        setLayout(new BorderLayout(0, 0));
        setBackground(GUIUtils.C_BG);
        construirUI();
        cargarDatos();
    }

    // ─── UI principal ───────────────────────────────────────────────────────

    private void construirUI() {
        JButton btnNuevo = null;
        if (esAdmin) {
            btnNuevo = GUIUtils.crearBotonTop("+ Añadir pedido", GUIUtils.C_ORANGE, GUIUtils.C_WHITE);
            btnNuevo.addActionListener(e -> dialogoCrearPedido());
        }
        add(GUIUtils.construirTopbar("Gestión de Pedidos", GUIUtils.panelAccionTopbar(btnNuevo)), BorderLayout.NORTH);
        cuerpo = new JPanel(new BorderLayout(0, 16));
        cuerpo.setBackground(GUIUtils.C_BG);
        cuerpo.setBorder(new EmptyBorder(20, 28, 20, 28));
        cuerpo.add(construirTarjetas(),   BorderLayout.NORTH);
        cuerpo.add(construirPanelTabla(), BorderLayout.CENTER);
        add(cuerpo, BorderLayout.CENTER);
    }

    private JPanel construirTarjetas() {
        List<Pedido> todos   = pedidoController.obtenerTodos();
        long pendientes      = todos.stream().filter(p -> p.getEstado() == EstadoPedido.PENDIENTE).count();
        long entregados      = todos.stream().filter(p -> p.getEstado() == EstadoPedido.ENTREGADO).count();

        JPanel panel = new JPanel(new GridLayout(1, 3, 12, 0));
        panel.setBackground(GUIUtils.C_BG);
        panel.setPreferredSize(new Dimension(0, 110));
        panel.add(GUIUtils.crearTarjeta("Total pedidos", String.valueOf(todos.size()), "registrados en el sistema", GUIUtils.C_PURPLE));
        panel.add(GUIUtils.crearTarjeta("Pendientes",    String.valueOf(pendientes),   "requieren atención",        GUIUtils.C_ORANGE));
        panel.add(GUIUtils.crearTarjeta("Entregados",    String.valueOf(entregados),   "completados",               GUIUtils.C_GREEN));
        return panel;
    }

    // ─── Panel tabla ────────────────────────────────────────────────────────

    private JPanel construirPanelTabla() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(GUIUtils.C_WHITE);
        panel.setBorder(BorderFactory.createLineBorder(GUIUtils.C_BORDER, 1));

        // Modelo y tabla
        String[] cols = {"ID", "Cliente", "Fecha", "Estado", ""};
        modeloTabla = new DefaultTableModel(cols, 0) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };
        tabla  = new JTable(modeloTabla);
        sorter = GUIUtils.estilizarTabla(tabla, modeloTabla);

        // Cabecera (sorter ya inicializado aquí)
        panel.add(construirCabeceraTabla(), BorderLayout.NORTH);

        // Renderer de estado coloreado
        tabla.getColumnModel().getColumn(3).setMinWidth(110);
        tabla.getColumnModel().getColumn(3).setMaxWidth(110);
        tabla.getColumnModel().getColumn(3).setCellRenderer(new DefaultTableCellRenderer() {
            @Override public Component getTableCellRendererComponent(JTable t, Object v,
                    boolean sel, boolean foc, int r, int c) {
                JLabel lbl = (JLabel) super.getTableCellRendererComponent(t, v, sel, foc, r, c);
                String val = v != null ? v.toString() : "";
                lbl.setHorizontalAlignment(SwingConstants.CENTER);
                lbl.setFont(new Font("Segoe UI", Font.BOLD, 11));
                lbl.setBackground(sel ? GUIUtils.C_SEL_BG : r % 2 == 0 ? GUIUtils.C_WHITE : GUIUtils.C_ROW_ALT);
                switch (val) {
                    case "PENDIENTE"  -> lbl.setForeground(GUIUtils.C_ORANGE);
                    case "PROCESANDO" -> lbl.setForeground(GUIUtils.C_BLUE);
                    case "ENVIADO"    -> lbl.setForeground(new Color(139, 92, 246));
                    case "ENTREGADO"  -> lbl.setForeground(GUIUtils.C_GREEN);
                    case "CANCELADO"  -> lbl.setForeground(GUIUtils.C_RED);
                    default           -> lbl.setForeground(GUIUtils.C_MUTED);
                }
                return lbl;
            }
        });

        tabla.getColumnModel().getColumn(0).setMaxWidth(48);
        tabla.getColumnModel().getColumn(4).setMinWidth(esAdmin ? 160 : 0);
        tabla.getColumnModel().getColumn(4).setMaxWidth(esAdmin ? 160 : 0);

        if (esAdmin) {
            tabla.getColumnModel().getColumn(4).setCellRenderer(new AccionesRenderer());
            tabla.addMouseListener(new java.awt.event.MouseAdapter() {
                @Override public void mouseClicked(java.awt.event.MouseEvent e) {
                    int fila = tabla.rowAtPoint(e.getPoint());
                    int col  = tabla.columnAtPoint(e.getPoint());
                    if (col != 4 || fila < 0) return;
                    int modelRow = tabla.convertRowIndexToModel(fila);
                    int x = e.getX() - tabla.getCellRect(fila, col, true).x;
                    if (x < 80) dialogoCambiarEstado(modelRow);
                    else        eliminarFila(modelRow);
                }
            });
            tabla.addMouseMotionListener(new java.awt.event.MouseMotionAdapter() {
                @Override public void mouseMoved(java.awt.event.MouseEvent e) { tabla.repaint(); }
            });
        }

        panel.add(GUIUtils.crearScrollTabla(tabla), BorderLayout.CENTER);
        return panel;
    }

    private JPanel construirCabeceraTabla() {
        return GUIUtils.construirCabeceraTabla("Listado de pedidos", "Buscar por ID, cliente, estado...", sorter, this::refrescar);
    }

    // ─── Diálogo crear pedido ───────────────────────────────────────────────

    private void dialogoCrearPedido() {
        List<Cliente>  clientes  = clienteController.obtenerTodos();
        List<Producto> productos = productoController.obtenerTodos();

        if (clientes.isEmpty())  { GUIUtils.error(this, "No hay clientes registrados. Añade un cliente primero.");   return; }
        if (productos.isEmpty()) { GUIUtils.error(this, "No hay productos registrados. Añade un producto primero."); return; }

        JDialog dialogo = new JDialog((Frame) SwingUtilities.getWindowAncestor(this), "Nuevo pedido", true);
        dialogo.setSize(620, 520);
        dialogo.setLocationRelativeTo(this);
        dialogo.setLayout(new BorderLayout());
        dialogo.getContentPane().setBackground(GUIUtils.C_BG);

        // Cabecera del diálogo
        JPanel cabecera = new JPanel(new FlowLayout(FlowLayout.LEFT));
        cabecera.setBackground(GUIUtils.C_PURPLE);
        cabecera.setBorder(new EmptyBorder(14, 20, 14, 20));
        JLabel lblTit = new JLabel("Nuevo pedido");
        lblTit.setFont(new Font("Segoe UI", Font.BOLD, 16));
        lblTit.setForeground(GUIUtils.C_WHITE);
        cabecera.add(lblTit);
        dialogo.add(cabecera, BorderLayout.NORTH);

        // Cuerpo
        JPanel cuerpoDialogo = new JPanel(new BorderLayout(0, 12));
        cuerpoDialogo.setBackground(GUIUtils.C_BG);
        cuerpoDialogo.setBorder(new EmptyBorder(16, 20, 12, 20));

        // Selector de cliente
        JPanel panelCliente = new JPanel(new BorderLayout(8, 0));
        panelCliente.setBackground(GUIUtils.C_BG);
        JLabel lblCliente = new JLabel("Cliente:");
        lblCliente.setFont(new Font("Segoe UI", Font.BOLD, 12));
        lblCliente.setForeground(GUIUtils.C_TEXT);
        lblCliente.setPreferredSize(new Dimension(80, 28));

        JComboBox<Cliente> comboCliente = new JComboBox<>();
        for (Cliente c : clientes) comboCliente.addItem(c);
        comboCliente.setRenderer(new DefaultListCellRenderer() {
            @Override public Component getListCellRendererComponent(JList<?> list, Object value,
                    int index, boolean isSelected, boolean cellHasFocus) {
                super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
                if (value instanceof Cliente c) setText(c.getNombre() + " (ID: " + c.getId() + ")");
                return this;
            }
        });
        comboCliente.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        panelCliente.add(lblCliente,   BorderLayout.WEST);
        panelCliente.add(comboCliente, BorderLayout.CENTER);
        cuerpoDialogo.add(panelCliente, BorderLayout.NORTH);

        // Tabla de líneas
        JPanel panelLineas = new JPanel(new BorderLayout(0, 6));
        panelLineas.setBackground(GUIUtils.C_BG);
        JLabel lblLineas = new JLabel("Líneas del pedido:");
        lblLineas.setFont(new Font("Segoe UI", Font.BOLD, 12));
        lblLineas.setForeground(GUIUtils.C_TEXT);
        panelLineas.add(lblLineas, BorderLayout.NORTH);

        String[] colsLineas = {"Producto", "Precio unit.", "Cantidad"};
        DefaultTableModel modeloLineas = new DefaultTableModel(colsLineas, 0) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };
        JTable tablaLineas = new JTable(modeloLineas);
        tablaLineas.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        tablaLineas.setRowHeight(30);
        tablaLineas.setGridColor(GUIUtils.C_BORDER);
        tablaLineas.setBackground(GUIUtils.C_WHITE);
        tablaLineas.getTableHeader().setBackground(GUIUtils.C_ROW_ALT);
        tablaLineas.getTableHeader().setForeground(GUIUtils.C_PURPLE);
        tablaLineas.getTableHeader().setFont(new Font("Consolas", Font.BOLD, 11));
        JScrollPane scrollLineas = new JScrollPane(tablaLineas);
        scrollLineas.setBorder(BorderFactory.createLineBorder(GUIUtils.C_BORDER, 1));
        scrollLineas.setPreferredSize(new Dimension(0, 160));
        panelLineas.add(scrollLineas, BorderLayout.CENTER);

        // Fila para añadir línea
        JPanel panelAnadir = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 4));
        panelAnadir.setBackground(GUIUtils.C_BG);

        JComboBox<Producto> comboProducto = new JComboBox<>();
        for (Producto p : productos) comboProducto.addItem(p);
        comboProducto.setRenderer(new DefaultListCellRenderer() {
            @Override public Component getListCellRendererComponent(JList<?> list, Object value,
                    int index, boolean isSelected, boolean cellHasFocus) {
                super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
                if (value instanceof Producto p)
                    setText(p.getNombre() + " (" + String.format("%.2f", p.getPrecio()) + " €)");
                return this;
            }
        });
        comboProducto.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        comboProducto.setPreferredSize(new Dimension(240, 30));

        JSpinner spinnerCantidad = new JSpinner(new SpinnerNumberModel(1, 1, 9999, 1));
        spinnerCantidad.setPreferredSize(new Dimension(70, 30));
        spinnerCantidad.setFont(new Font("Segoe UI", Font.PLAIN, 12));

        JButton btnAnadir = GUIUtils.crearBotonInline("+ Añadir", GUIUtils.C_PURPLE, 90);
        btnAnadir.addActionListener(e -> {
            Producto prod = (Producto) comboProducto.getSelectedItem();
            if (prod == null) return;
            for (int i = 0; i < modeloLineas.getRowCount(); i++) {
                if (modeloLineas.getValueAt(i, 0).toString().equals(prod.getNombre())) {
                    GUIUtils.error(dialogo, "Ese producto ya está en el pedido.");
                    return;
                }
            }
            modeloLineas.addRow(new Object[]{
                prod.getNombre(),
                String.format("%.2f €", prod.getPrecio()),
                (int) spinnerCantidad.getValue()
            });
        });

        JButton btnEliminarLinea = GUIUtils.crearBotonInline("Quitar", GUIUtils.C_RED, 70);
        btnEliminarLinea.addActionListener(e -> {
            int fila = tablaLineas.getSelectedRow();
            if (fila >= 0) modeloLineas.removeRow(fila);
        });

        panelAnadir.add(new JLabel("Producto:"));
        panelAnadir.add(comboProducto);
        panelAnadir.add(new JLabel("Cant:"));
        panelAnadir.add(spinnerCantidad);
        panelAnadir.add(btnAnadir);
        panelAnadir.add(btnEliminarLinea);
        panelLineas.add(panelAnadir, BorderLayout.SOUTH);

        cuerpoDialogo.add(panelLineas, BorderLayout.CENTER);
        dialogo.add(cuerpoDialogo, BorderLayout.CENTER);

        // Botones de acción
        JPanel panelBotones = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 10));
        panelBotones.setBackground(GUIUtils.C_BG);
        panelBotones.setBorder(BorderFactory.createMatteBorder(1, 0, 0, 0, GUIUtils.C_BORDER));

        JButton btnCancelar = GUIUtils.crearBotonInline("Cancelar", GUIUtils.C_MUTED, 100);
        btnCancelar.addActionListener(e -> dialogo.dispose());

        JButton btnCrear = GUIUtils.crearBotonTop("Crear pedido", GUIUtils.C_PURPLE, GUIUtils.C_WHITE);
        btnCrear.addActionListener(e -> {
            if (modeloLineas.getRowCount() == 0) {
                GUIUtils.error(dialogo, "Añade al menos un producto al pedido.");
                return;
            }
            Cliente clienteSeleccionado = (Cliente) comboCliente.getSelectedItem();
            if (clienteSeleccionado == null) return;

            int idPedido = pedidoController.crearPedido(clienteSeleccionado.getId());
            if (idPedido == -1) {
                GUIUtils.error(dialogo, "No se pudo crear el pedido. Comprueba la conexión.");
                return;
            }

            boolean todoBien = true;
            for (int i = 0; i < modeloLineas.getRowCount(); i++) {
                String nombreProducto = modeloLineas.getValueAt(i, 0).toString();
                int cantidad = (int) modeloLineas.getValueAt(i, 2);
                Producto prod = productos.stream()
                    .filter(p -> p.getNombre().equals(nombreProducto))
                    .findFirst().orElse(null);
                if (prod == null) continue;
                LineaPedido linea = new LineaPedido(idPedido, prod, cantidad, prod.getPrecio());
                if (!pedidoController.añadirLineaPedido(linea)) todoBien = false;
            }

            dialogo.dispose();
            if (todoBien) {
                GUIUtils.exito(this, "Pedido #" + idPedido + " creado correctamente.");
            } else {
                JOptionPane.showMessageDialog(this,
                    "Pedido creado (#" + idPedido + ") pero alguna línea no se pudo guardar.",
                    "Aviso", JOptionPane.WARNING_MESSAGE);
            }
            refrescar();
        });

        panelBotones.add(btnCancelar);
        panelBotones.add(btnCrear);
        dialogo.add(panelBotones, BorderLayout.SOUTH);
        dialogo.setVisible(true);
    }

    // ─── Acciones de tabla ──────────────────────────────────────────────────

    private void refrescar() {
        cargarDatos();
        cuerpo.remove(0);
        cuerpo.add(construirTarjetas(), BorderLayout.NORTH, 0);
        cuerpo.revalidate();
        cuerpo.repaint();
    }

    private void cargarDatos() {
        modeloTabla.setRowCount(0);
        for (Pedido p : pedidoController.obtenerTodos()) {
            modeloTabla.addRow(new Object[]{
                p.getId(),
                p.getCliente() != null ? p.getCliente().getNombre() : "-",
                p.getFecha()   != null ? p.getFecha().toString()    : "-",
                p.getEstado()  != null ? p.getEstado().toString()   : "-",
                ""
            });
        }
    }

    private void dialogoCambiarEstado(int fila) {
        int id = Integer.parseInt(modeloTabla.getValueAt(fila, 0).toString());
        EstadoPedido[] estados = EstadoPedido.values();
        String[] nombres = java.util.Arrays.stream(estados).map(Enum::toString).toArray(String[]::new);
        String seleccion = (String) JOptionPane.showInputDialog(this, "Selecciona el nuevo estado:",
            "Cambiar estado", JOptionPane.PLAIN_MESSAGE, null, nombres, nombres[0]);
        if (seleccion != null) {
            if (pedidoController.actualizarEstado(id, EstadoPedido.valueOf(seleccion))) {
                GUIUtils.exito(this, "Estado actualizado."); refrescar();
            } else GUIUtils.error(this, "No se pudo actualizar el estado.");
        }
    }

    private void eliminarFila(int fila) {
        int id = Integer.parseInt(modeloTabla.getValueAt(fila, 0).toString());
        if (JOptionPane.showConfirmDialog(this, "¿Eliminar el pedido #" + id + "?",
                "Confirmar", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE) == JOptionPane.YES_OPTION) {
            if (pedidoController.eliminarPedido(id)) { GUIUtils.exito(this, "Pedido eliminado."); refrescar(); }
            else GUIUtils.error(this, "No se pudo eliminar.");
        }
    }

    // ─── Renderer de acciones ───────────────────────────────────────────────

    private class AccionesRenderer extends JPanel implements TableCellRenderer {
        private final JButton btnEstado   = GUIUtils.crearBotonInline("Estado",   GUIUtils.C_BLUE);
        private final JButton btnEliminar = GUIUtils.crearBotonInline("Eliminar", GUIUtils.C_RED);
        AccionesRenderer() {
            setLayout(new FlowLayout(FlowLayout.CENTER, 4, 6)); setOpaque(true);
            add(btnEstado); add(btnEliminar);
        }
        @Override public Component getTableCellRendererComponent(JTable t, Object v,
                boolean sel, boolean foc, int r, int c) {
            boolean hover = false;
            try { Point mp = t.getMousePosition(); if (mp != null) hover = t.rowAtPoint(mp) == r; }
            catch (Exception ex) { /* ignorar */ }
            setBackground(sel ? GUIUtils.C_SEL_BG : r % 2 == 0 ? GUIUtils.C_WHITE : GUIUtils.C_ROW_ALT);
            btnEstado.setBackground(hover   ? GUIUtils.C_BLUE : GUIUtils.C_WHITE);
            btnEstado.setForeground(hover   ? GUIUtils.C_WHITE : GUIUtils.C_BLUE);
            btnEliminar.setBackground(hover ? GUIUtils.C_RED  : GUIUtils.C_WHITE);
            btnEliminar.setForeground(hover ? GUIUtils.C_WHITE : GUIUtils.C_RED);
            return this;
        }
    }
}
