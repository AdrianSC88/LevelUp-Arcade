package com.levelup.gui.paneles;

import com.levelup.controlador.PedidoController;
import com.levelup.modelo.EstadoPedido;
import com.levelup.modelo.Pedido;
import com.levelup.modelo.Usuario;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.table.TableRowSorter;
import java.awt.*;
import java.util.List;

public class PedidosPanel extends JPanel {

    private static final Color C_BG     = new Color(248, 247, 255);
    private static final Color C_PURPLE = new Color(92, 51, 181);
    private static final Color C_ORANGE = new Color(249, 115, 22);
    private static final Color C_BLUE   = new Color(59, 130, 246);
    private static final Color C_RED    = new Color(239, 68, 68);
    private static final Color C_GREEN  = new Color(34, 197, 94);
    private static final Color C_BORDER = new Color(229, 224, 248);
    private static final Color C_WHITE  = Color.WHITE;
    private static final Color C_TEXT   = new Color(26, 18, 37);
    private static final Color C_MUTED  = new Color(107, 114, 128);
    private static final Color C_HEADER = new Color(45, 21, 114);

    private final PedidoController pedidoController;
    private final Usuario usuarioActivo;
    private final boolean esAdmin;
    private JTable tabla;
    private DefaultTableModel modeloTabla;
    private TableRowSorter<DefaultTableModel> sorter;
    private JPanel cuerpo;

    public PedidosPanel(Usuario usuarioActivo) {
        this.usuarioActivo = usuarioActivo;
        this.esAdmin = "administrador".equals(usuarioActivo.getRol());
        this.pedidoController = new PedidoController();
        setLayout(new BorderLayout(0, 0));
        setBackground(C_BG);
        construirUI();
        cargarDatos();
    }

    private void construirUI() {
        add(construirTopbar(), BorderLayout.NORTH);
        cuerpo = new JPanel(new BorderLayout(0, 16));
        cuerpo.setBackground(C_BG);
        cuerpo.setBorder(new EmptyBorder(20, 28, 20, 28));
        cuerpo.add(construirTarjetas(), BorderLayout.NORTH);
        cuerpo.add(construirPanelTabla(), BorderLayout.CENTER);
        add(cuerpo, BorderLayout.CENTER);
    }

    private JPanel construirTopbar() {
        JPanel bar = new JPanel() {
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setPaint(new GradientPaint(0, 0, C_PURPLE, getWidth(), 0, C_HEADER));
                g2.fillRect(0, 0, getWidth(), getHeight());
                g2.setPaint(new GradientPaint(0, 0, C_ORANGE, getWidth(), 0, C_BLUE));
                g2.fillRect(0, getHeight() - 3, getWidth(), 3);
                g2.dispose();
            }
        };
        bar.setLayout(new BorderLayout());
        bar.setPreferredSize(new Dimension(0, 64));
        bar.setBorder(new EmptyBorder(0, 28, 0, 28));

        JPanel izquierda = new JPanel(new FlowLayout(FlowLayout.LEFT, 12, 10));
        izquierda.setOpaque(false);

        JLabel labelLogo = new JLabel();
        try {
            java.net.URL imgURL = getClass().getClassLoader().getResource("resources/logo2.png");
            if (imgURL != null) {
                Image scaled = new ImageIcon(imgURL).getImage().getScaledInstance(44, 44, Image.SCALE_SMOOTH);
                labelLogo.setIcon(new ImageIcon(scaled));
            }
        } catch (Exception ex) { /* sin logo */ }

        JLabel titulo = new JLabel("Gestión de Pedidos");
        titulo.setFont(new Font("Segoe UI", Font.BOLD, 20));
        titulo.setForeground(C_WHITE);

        izquierda.add(labelLogo);
        izquierda.add(titulo);
        bar.add(izquierda, BorderLayout.WEST);
        return bar;
    }

    private JPanel construirTarjetas() {
        JPanel panel = new JPanel(new GridLayout(1, 3, 12, 0));
        panel.setBackground(C_BG);
        panel.setPreferredSize(new Dimension(0, 110));

        List<Pedido> todos = pedidoController.obtenerTodos();
        long pendientes = todos.stream().filter(p -> p.getEstado() == EstadoPedido.PENDIENTE).count();
        long entregados = todos.stream().filter(p -> p.getEstado() == EstadoPedido.ENTREGADO).count();

        panel.add(crearTarjeta("Total pedidos", String.valueOf(todos.size()), "registrados en el sistema", C_PURPLE));
        panel.add(crearTarjeta("Pendientes", String.valueOf(pendientes), "requieren atención", C_ORANGE));
        panel.add(crearTarjeta("Entregados", String.valueOf(entregados), "completados", C_GREEN));
        return panel;
    }

    private JPanel crearTarjeta(String titulo, String valor, String sub, Color acento) {
        JPanel card = new JPanel(new BorderLayout());
        card.setBackground(C_WHITE);
        card.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(C_BORDER, 1), new EmptyBorder(14, 18, 14, 18)));
        JLabel lblTitulo = new JLabel(titulo);
        lblTitulo.setFont(new Font("Segoe UI", Font.PLAIN, 12)); lblTitulo.setForeground(C_MUTED);
        JLabel lblValor = new JLabel(valor);
        lblValor.setFont(new Font("Segoe UI", Font.BOLD, 26)); lblValor.setForeground(acento);
        JLabel lblSub = new JLabel(sub);
        lblSub.setFont(new Font("Segoe UI", Font.PLAIN, 11)); lblSub.setForeground(C_MUTED);
        JPanel centro = new JPanel();
        centro.setLayout(new BoxLayout(centro, BoxLayout.Y_AXIS)); centro.setBackground(C_WHITE);
        centro.add(lblValor); centro.add(Box.createVerticalStrut(2)); centro.add(lblSub);
        card.add(lblTitulo, BorderLayout.NORTH); card.add(centro, BorderLayout.CENTER);
        return card;
    }

    private JPanel construirPanelTabla() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(C_WHITE);
        panel.setBorder(BorderFactory.createLineBorder(C_BORDER, 1));

        JPanel cabecera = new JPanel(new BorderLayout(8, 0));
        cabecera.setBackground(C_WHITE);
        cabecera.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createMatteBorder(0, 0, 1, 0, C_BORDER), new EmptyBorder(10, 16, 10, 16)));

        JLabel lblTabla = new JLabel("Listado de pedidos");
        lblTabla.setFont(new Font("Segoe UI", Font.BOLD, 14)); lblTabla.setForeground(C_TEXT);

        JTextField campoBusqueda = crearCampoBusqueda("Buscar por ID, cliente, estado...");

        JButton btnRefrescar = crearBotonRefrescar();
        btnRefrescar.addActionListener(e -> { campoBusqueda.setText(""); refrescar(); });

        JPanel derechaBar = new JPanel(new FlowLayout(FlowLayout.RIGHT, 6, 0));
        derechaBar.setBackground(C_WHITE);
        derechaBar.add(btnRefrescar);

        cabecera.add(lblTabla, BorderLayout.WEST);
        cabecera.add(campoBusqueda, BorderLayout.CENTER);
        cabecera.add(derechaBar, BorderLayout.EAST);
        panel.add(cabecera, BorderLayout.NORTH);

        String[] cols = {"ID", "Cliente", "Fecha", "Estado", ""};
        modeloTabla = new DefaultTableModel(cols, 0) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };

        tabla = new JTable(modeloTabla);
        sorter = new TableRowSorter<>(modeloTabla);
        tabla.setRowSorter(sorter);
        tabla.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        tabla.setRowHeight(40); tabla.setGridColor(C_BORDER); tabla.setBackground(C_WHITE);
        tabla.setSelectionBackground(new Color(92, 51, 181, 30)); tabla.setSelectionForeground(C_TEXT);
        tabla.setShowVerticalLines(false); tabla.setFillsViewportHeight(true);

        JTableHeader header = tabla.getTableHeader();
        header.setFont(new Font("Consolas", Font.BOLD, 11));
        header.setBackground(new Color(245, 243, 255)); header.setForeground(C_PURPLE);
        header.setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, C_BORDER));
        header.setPreferredSize(new Dimension(0, 36));

        tabla.setDefaultRenderer(Object.class, new DefaultTableCellRenderer() {
            @Override public Component getTableCellRendererComponent(JTable t, Object v,
                    boolean sel, boolean foc, int r, int c) {
                super.getTableCellRendererComponent(t, v, sel, foc, r, c);
                if (sel) setBackground(new Color(92, 51, 181, 30));
                else if (r % 2 == 0) setBackground(C_WHITE);
                else setBackground(new Color(245, 243, 255));
                setBorder(new EmptyBorder(0, 8, 0, 8));
                return this;
            }
        });

        tabla.getColumnModel().getColumn(0).setMaxWidth(48);
        tabla.getColumnModel().getColumn(3).setMinWidth(110);
        tabla.getColumnModel().getColumn(3).setMaxWidth(110);

        tabla.getColumnModel().getColumn(3).setCellRenderer(new DefaultTableCellRenderer() {
            @Override public Component getTableCellRendererComponent(JTable t, Object v,
                    boolean sel, boolean foc, int r, int c) {
                JLabel lbl = (JLabel) super.getTableCellRendererComponent(t, v, sel, foc, r, c);
                String val = v != null ? v.toString() : "";
                lbl.setHorizontalAlignment(SwingConstants.CENTER);
                lbl.setFont(new Font("Segoe UI", Font.BOLD, 11));
                lbl.setBackground(sel ? new Color(92,51,181,30) : r % 2 == 0 ? C_WHITE : new Color(245,243,255));
                switch (val) {
                    case "PENDIENTE"  -> lbl.setForeground(C_ORANGE);
                    case "PROCESANDO" -> lbl.setForeground(C_BLUE);
                    case "ENVIADO"    -> lbl.setForeground(new Color(139, 92, 246));
                    case "ENTREGADO"  -> lbl.setForeground(C_GREEN);
                    case "CANCELADO"  -> lbl.setForeground(C_RED);
                    default           -> lbl.setForeground(C_MUTED);
                }
                return lbl;
            }
        });

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
                    else eliminarFila(modelRow);
                }
            });
            tabla.addMouseMotionListener(new java.awt.event.MouseMotionAdapter() {
                @Override public void mouseMoved(java.awt.event.MouseEvent e) { tabla.repaint(); }
            });
        }

        JScrollPane scroll = new JScrollPane(tabla);
        scroll.setBorder(BorderFactory.createEmptyBorder());
        scroll.getViewport().setBackground(C_WHITE);
        panel.add(scroll, BorderLayout.CENTER);
        return panel;
    }

    private void refrescar() {
        cargarDatos();
        cuerpo.remove(0);
        cuerpo.add(construirTarjetas(), BorderLayout.NORTH, 0);
        cuerpo.revalidate();
        cuerpo.repaint();
    }

    private class AccionesRenderer extends JPanel implements TableCellRenderer {
        private final JButton btnEstado   = crearBotonInline("Estado", C_BLUE);
        private final JButton btnEliminar = crearBotonInline("Eliminar", C_RED);
        AccionesRenderer() {
            setLayout(new FlowLayout(FlowLayout.CENTER, 4, 6)); setOpaque(true);
            add(btnEstado); add(btnEliminar);
        }
        @Override public Component getTableCellRendererComponent(JTable t, Object v,
                boolean sel, boolean foc, int r, int c) {
            boolean hover = false;
            try { Point mp = t.getMousePosition(); if (mp != null) hover = t.rowAtPoint(mp) == r; }
            catch (Exception ex) { /* ignorar */ }
            setBackground(sel ? new Color(92, 51, 181, 30) : r % 2 == 0 ? C_WHITE : new Color(245, 243, 255));
            btnEstado.setBackground(hover ? C_BLUE : C_WHITE); btnEstado.setForeground(hover ? C_WHITE : C_BLUE);
            btnEliminar.setBackground(hover ? C_RED : C_WHITE); btnEliminar.setForeground(hover ? C_WHITE : C_RED);
            return this;
        }
    }

    private void cargarDatos() {
        modeloTabla.setRowCount(0);
        for (Pedido p : pedidoController.obtenerTodos()) {
            modeloTabla.addRow(new Object[]{
                p.getId(),
                p.getCliente() != null ? p.getCliente().getNombre() : "-",
                p.getFecha() != null ? p.getFecha().toString() : "-",
                p.getEstado() != null ? p.getEstado().toString() : "-",
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
                exito("Estado actualizado."); refrescar();
            } else error("No se pudo actualizar el estado.");
        }
    }

    private void eliminarFila(int fila) {
        int id = Integer.parseInt(modeloTabla.getValueAt(fila, 0).toString());
        if (JOptionPane.showConfirmDialog(this, "¿Eliminar el pedido #" + id + "?",
                "Confirmar", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE) == JOptionPane.YES_OPTION) {
            if (pedidoController.eliminarPedido(id)) { exito("Pedido eliminado."); refrescar(); }
            else error("No se pudo eliminar.");
        }
    }

    private void filtrar(String texto) {
        if (texto == null || texto.trim().isEmpty()) {
            sorter.setRowFilter(null);
        } else {
            sorter.setRowFilter(RowFilter.regexFilter("(?i)" + texto.trim()));
        }
    }

    private JTextField crearCampoBusqueda(String placeholder) {
        JTextField campo = new JTextField() {
            @Override protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                if (getText().isEmpty() && !isFocusOwner()) {
                    Graphics2D g2 = (Graphics2D) g.create();
                    g2.setColor(C_MUTED);
                    g2.setFont(getFont().deriveFont(Font.ITALIC));
                    g2.drawString(placeholder, 10, getHeight() / 2 + 5);
                    g2.dispose();
                }
            }
        };
        campo.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        campo.setBackground(new Color(248, 247, 255));
        campo.setForeground(C_TEXT);
        campo.setCaretColor(C_PURPLE);
        campo.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(C_BORDER, 1), new EmptyBorder(4, 10, 4, 10)));
        campo.setPreferredSize(new Dimension(240, 32));
        campo.addFocusListener(new java.awt.event.FocusAdapter() {
            @Override public void focusGained(java.awt.event.FocusEvent e) {
                campo.setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createLineBorder(C_PURPLE, 1), new EmptyBorder(4, 10, 4, 10)));
            }
            @Override public void focusLost(java.awt.event.FocusEvent e) {
                campo.setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createLineBorder(C_BORDER, 1), new EmptyBorder(4, 10, 4, 10)));
            }
        });
        campo.getDocument().addDocumentListener(new DocumentListener() {
            @Override public void insertUpdate(DocumentEvent e) { filtrar(campo.getText()); }
            @Override public void removeUpdate(DocumentEvent e) { filtrar(campo.getText()); }
            @Override public void changedUpdate(DocumentEvent e) { filtrar(campo.getText()); }
        });
        return campo;
    }

    private JButton crearBotonInline(String texto, Color color) {
        JButton b = new JButton(texto);
        b.setFont(new Font("Segoe UI", Font.BOLD, 11)); b.setForeground(color);
        b.setBackground(C_WHITE); b.setBorder(BorderFactory.createLineBorder(color, 1));
        b.setFocusPainted(false); b.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        b.setPreferredSize(new Dimension(70, 26));
        return b;
    }

    private JButton crearBotonRefrescar() {
        JButton b = new JButton("↻ Refrescar");
        b.setFont(new Font("Segoe UI", Font.PLAIN, 11)); b.setForeground(C_PURPLE);
        b.setBackground(C_WHITE); b.setBorder(BorderFactory.createLineBorder(C_BORDER, 1));
        b.setFocusPainted(false); b.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        b.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override public void mouseEntered(java.awt.event.MouseEvent e) { b.setBackground(new Color(245, 243, 255)); }
            @Override public void mouseExited(java.awt.event.MouseEvent e) { b.setBackground(C_WHITE); }
        });
        return b;
    }

    private void exito(String msg) { JOptionPane.showMessageDialog(this, msg, "Éxito", JOptionPane.INFORMATION_MESSAGE); }
    private void error(String msg)  { JOptionPane.showMessageDialog(this, msg, "Error", JOptionPane.ERROR_MESSAGE); }
}
