package com.levelup.gui.paneles;

import com.levelup.controlador.CategoriaController;
import com.levelup.controlador.ProductoController;
import com.levelup.controlador.ProveedorController;
import com.levelup.modelo.Categoria;
import com.levelup.modelo.Producto;
import com.levelup.modelo.Proveedor;
import com.levelup.modelo.Usuario;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.*;
import java.awt.*;
import java.util.List;

public class ProductosPanel extends JPanel {

    private static final Color C_BG       = new Color(248, 247, 255);
    private static final Color C_PURPLE   = new Color(92, 51, 181);
    private static final Color C_PURPLE_L = new Color(196, 181, 253);
    private static final Color C_ORANGE   = new Color(249, 115, 22);
    private static final Color C_BLUE     = new Color(59, 130, 246);
    private static final Color C_RED      = new Color(239, 68, 68);
    private static final Color C_GREEN    = new Color(34, 197, 94);
    private static final Color C_BORDER   = new Color(229, 224, 248);
    private static final Color C_WHITE    = Color.WHITE;
    private static final Color C_TEXT     = new Color(26, 18, 37);
    private static final Color C_MUTED    = new Color(107, 114, 128);
    private static final Color C_HEADER   = new Color(45, 21, 114);

    private final ProductoController productoController;
    private final CategoriaController categoriaController;
    private final ProveedorController proveedorController;
    private final Usuario usuarioActivo;
    private final boolean esAdmin;

    private JTable tabla;
    private DefaultTableModel modeloTabla;
    private JPanel cuerpo;

    public ProductosPanel(Usuario usuarioActivo) {
        this.usuarioActivo = usuarioActivo;
        this.esAdmin = "administrador".equals(usuarioActivo.getRol());
        this.productoController = new ProductoController();
        this.categoriaController = new CategoriaController();
        this.proveedorController = new ProveedorController();
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
                g2.setPaint(new GradientPaint(0, 0, C_ORANGE, getWidth(), 0, new Color(59, 130, 246)));
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

        JLabel titulo = new JLabel("Gestión de Productos");
        titulo.setFont(new Font("Segoe UI", Font.BOLD, 20));
        titulo.setForeground(C_WHITE);

        izquierda.add(labelLogo);
        izquierda.add(titulo);

        JPanel derecha = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 14));
        derecha.setOpaque(false);
        if (esAdmin) {
            JButton btnAnadir = crearBotonTop("+ Añadir producto", C_ORANGE, C_WHITE);
            btnAnadir.addActionListener(e -> dialogoAnadir());
            derecha.add(btnAnadir);
        }

        bar.add(izquierda, BorderLayout.WEST);
        bar.add(derecha, BorderLayout.EAST);
        return bar;
    }

    private JPanel construirTarjetas() {
        JPanel panel = new JPanel(new GridLayout(1, 3, 12, 0));
        panel.setBackground(C_BG);
        panel.setPreferredSize(new Dimension(0, 110));

        List<Producto> todos = productoController.obtenerTodos();
        long sinStock = todos.stream().filter(p -> p.getStock() == 0).count();
        long stockBajo = todos.stream().filter(p -> p.getStock() > 0 && p.getStock() <= 10).count();
        long totalUnidades = todos.stream().mapToLong(p -> p.getStock()).sum();
        double valorInventario = todos.stream().mapToDouble(p -> p.getPrecio() * p.getStock()).sum();

        panel.add(crearTarjeta("Total productos", String.valueOf(todos.size()), "unidades en catálogo", C_PURPLE));
        panel.add(crearTarjeta("Stock bajo / sin stock", stockBajo + " / " + sinStock, "requieren atención", C_ORANGE));
        panel.add(crearTarjeta("Valor inventario", String.format("%.2f €", valorInventario), totalUnidades + " unidades totales en stock", C_BLUE));
        return panel;
    }

    private JPanel crearTarjeta(String titulo, String valor, String sub, Color acento) {
        JPanel card = new JPanel(new BorderLayout());
        card.setBackground(C_WHITE);
        card.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(C_BORDER, 1),
            new EmptyBorder(14, 18, 14, 18)
        ));
        JLabel lblTitulo = new JLabel(titulo);
        lblTitulo.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        lblTitulo.setForeground(C_MUTED);

        JLabel lblValor = new JLabel(valor);
        lblValor.setFont(new Font("Segoe UI", Font.BOLD, 26));
        lblValor.setForeground(acento);

        JLabel lblSub = new JLabel(sub);
        lblSub.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        lblSub.setForeground(C_MUTED);

        JPanel centro = new JPanel();
        centro.setLayout(new BoxLayout(centro, BoxLayout.Y_AXIS));
        centro.setBackground(C_WHITE);
        centro.add(lblValor);
        centro.add(Box.createVerticalStrut(2));
        centro.add(lblSub);

        card.add(lblTitulo, BorderLayout.NORTH);
        card.add(centro, BorderLayout.CENTER);
        return card;
    }

    private JPanel construirPanelTabla() {
        JPanel panel = new JPanel(new BorderLayout(0, 0));
        panel.setBackground(C_WHITE);
        panel.setBorder(BorderFactory.createLineBorder(C_BORDER, 1));

        JPanel cabecera = new JPanel(new BorderLayout());
        cabecera.setBackground(C_WHITE);
        cabecera.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createMatteBorder(0, 0, 1, 0, C_BORDER),
            new EmptyBorder(12, 16, 12, 16)
        ));

        JLabel lblTabla = new JLabel("Inventario");
        lblTabla.setFont(new Font("Segoe UI", Font.BOLD, 14));
        lblTabla.setForeground(C_TEXT);

        JButton btnRefrescar = crearBotonRefrescar();
        btnRefrescar.addActionListener(e -> refrescar());

        cabecera.add(lblTabla, BorderLayout.WEST);
        cabecera.add(btnRefrescar, BorderLayout.EAST);
        panel.add(cabecera, BorderLayout.NORTH);

        String[] cols = {"ID", "Nombre", "Categoría", "Proveedor", "Precio", "Stock", "Estado", ""};
        modeloTabla = new DefaultTableModel(cols, 0) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
            @Override public Class<?> getColumnClass(int c) { return String.class; }
        };

        tabla = new JTable(modeloTabla);
        tabla.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        tabla.setRowHeight(40);
        tabla.setGridColor(C_BORDER);
        tabla.setBackground(C_WHITE);
        tabla.setSelectionBackground(new Color(92, 51, 181, 30));
        tabla.setSelectionForeground(C_TEXT);
        tabla.setShowVerticalLines(false);
        tabla.setIntercellSpacing(new Dimension(0, 0));
        tabla.setFillsViewportHeight(true);

        JTableHeader header = tabla.getTableHeader();
        header.setFont(new Font("Consolas", Font.BOLD, 11));
        header.setBackground(new Color(245, 243, 255));
        header.setForeground(C_PURPLE);
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
        tabla.getColumnModel().getColumn(4).setMaxWidth(90);
        tabla.getColumnModel().getColumn(5).setMaxWidth(70);
        tabla.getColumnModel().getColumn(6).setMaxWidth(100);
        tabla.getColumnModel().getColumn(7).setMinWidth(esAdmin ? 140 : 0);
        tabla.getColumnModel().getColumn(7).setMaxWidth(esAdmin ? 140 : 0);

        tabla.getColumnModel().getColumn(6).setCellRenderer(new DefaultTableCellRenderer() {
            @Override public Component getTableCellRendererComponent(JTable t, Object v,
                    boolean sel, boolean foc, int r, int c) {
                JLabel lbl = (JLabel) super.getTableCellRendererComponent(t, v, sel, foc, r, c);
                String val = v != null ? v.toString() : "";
                lbl.setHorizontalAlignment(SwingConstants.CENTER);
                lbl.setFont(new Font("Segoe UI", Font.BOLD, 11));
                if (val.equals("Sin stock")) lbl.setForeground(C_RED);
                else if (val.equals("Stock bajo")) lbl.setForeground(C_ORANGE);
                else lbl.setForeground(C_GREEN);
                lbl.setBackground(sel ? new Color(92,51,181,30) : r % 2 == 0 ? C_WHITE : new Color(245, 243, 255));
                return lbl;
            }
        });

        if (esAdmin) {
            tabla.getColumnModel().getColumn(7).setCellRenderer(new AccionesRenderer());
            tabla.addMouseListener(new java.awt.event.MouseAdapter() {
                @Override public void mouseClicked(java.awt.event.MouseEvent e) {
                    int fila = tabla.rowAtPoint(e.getPoint());
                    int col  = tabla.columnAtPoint(e.getPoint());
                    if (col != 7 || fila < 0) return;
                    int x = e.getX() - tabla.getCellRect(fila, col, true).x;
                    if (x < 70) dialogoEditar(fila);
                    else eliminarFila(fila);
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
        private final JButton btnEditar   = crearBotonInline("Editar", C_BLUE);
        private final JButton btnEliminar = crearBotonInline("Eliminar", C_RED);

        AccionesRenderer() {
            setLayout(new FlowLayout(FlowLayout.CENTER, 4, 6));
            setOpaque(true);
            add(btnEditar);
            add(btnEliminar);
        }

        @Override public Component getTableCellRendererComponent(JTable t, Object v,
                boolean sel, boolean foc, int r, int c) {
            boolean hover = false;
            try {
                Point mp = t.getMousePosition();
                if (mp != null) hover = t.rowAtPoint(mp) == r;
            } catch (Exception ex) { /* ignorar */ }
            setBackground(sel ? new Color(92, 51, 181, 30)
                : r % 2 == 0 ? C_WHITE : new Color(245, 243, 255));
            btnEditar.setBackground(hover ? C_BLUE : C_WHITE);
            btnEditar.setForeground(hover ? C_WHITE : C_BLUE);
            btnEliminar.setBackground(hover ? C_RED : C_WHITE);
            btnEliminar.setForeground(hover ? C_WHITE : C_RED);
            return this;
        }
    }

    private void cargarDatos() {
        modeloTabla.setRowCount(0);
        for (Producto p : productoController.obtenerTodos()) {
            String estado;
            if (p.getStock() == 0) estado = "Sin stock";
            else if (p.getStock() <= 10) estado = "Stock bajo";
            else estado = "Disponible";
            modeloTabla.addRow(new Object[]{
                p.getId(), p.getNombre(),
                p.getCategoria() != null ? p.getCategoria().getNombre() : "-",
                p.getProveedor() != null ? p.getProveedor().getNombre() : "-",
                String.format("%.2f €", p.getPrecio()),
                p.getStock(), estado, ""
            });
        }
    }

    private void dialogoAnadir() {
        List<Categoria> cats = categoriaController.obtenerTodas();
        List<Proveedor> provs = proveedorController.obtenerTodos();

        JTextField fNombre = new JTextField();
        JTextField fDesc   = new JTextField();
        JTextField fPrecio = new JTextField();
        JTextField fStock  = new JTextField();
        JComboBox<String> cbCat  = new JComboBox<>(cats.stream().map(Categoria::getNombre).toArray(String[]::new));
        JComboBox<String> cbProv = new JComboBox<>(provs.stream().map(Proveedor::getNombre).toArray(String[]::new));

        JPanel form = construirForm(
            new String[]{"Nombre", "Descripción", "Precio", "Stock", "Categoría", "Proveedor"},
            new JComponent[]{fNombre, fDesc, fPrecio, fStock, cbCat, cbProv}
        );

        if (JOptionPane.showConfirmDialog(this, form, "Añadir producto",
                JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE) == JOptionPane.OK_OPTION) {
            try {
                double precio = Double.parseDouble(fPrecio.getText().replace(",", "."));
                int stock = Integer.parseInt(fStock.getText());
                int idCat  = cats.get(cbCat.getSelectedIndex()).getId();
                int idProv = provs.get(cbProv.getSelectedIndex()).getId();
                if (productoController.añadirProducto(fNombre.getText(), fDesc.getText(), precio, stock, idCat, idProv)) {
                    exito("Producto añadido correctamente."); refrescar();
                } else error("No se pudo añadir. Revisa los datos.");
            } catch (NumberFormatException ex) { error("Precio y stock deben ser números válidos."); }
        }
    }

    private void dialogoEditar(int fila) {
        int id = Integer.parseInt(modeloTabla.getValueAt(fila, 0).toString());
        Producto p = productoController.obtenerPorId(id);
        if (p == null) return;

        List<Categoria> cats = categoriaController.obtenerTodas();
        List<Proveedor> provs = proveedorController.obtenerTodos();

        JTextField fNombre = new JTextField(p.getNombre());
        JTextField fDesc   = new JTextField(p.getDescripcion());
        JTextField fPrecio = new JTextField(String.valueOf(p.getPrecio()));
        JTextField fStock  = new JTextField(String.valueOf(p.getStock()));
        JComboBox<String> cbCat  = new JComboBox<>(cats.stream().map(Categoria::getNombre).toArray(String[]::new));
        JComboBox<String> cbProv = new JComboBox<>(provs.stream().map(Proveedor::getNombre).toArray(String[]::new));

        cats.stream().filter(c -> c.getId() == p.getCategoria().getId()).findFirst()
            .ifPresent(c -> cbCat.setSelectedIndex(cats.indexOf(c)));
        provs.stream().filter(v -> v.getId() == p.getProveedor().getId()).findFirst()
            .ifPresent(v -> cbProv.setSelectedIndex(provs.indexOf(v)));

        JPanel form = construirForm(
            new String[]{"Nombre", "Descripción", "Precio", "Stock", "Categoría", "Proveedor"},
            new JComponent[]{fNombre, fDesc, fPrecio, fStock, cbCat, cbProv}
        );

        if (JOptionPane.showConfirmDialog(this, form, "Editar producto",
                JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE) == JOptionPane.OK_OPTION) {
            try {
                double precio = Double.parseDouble(fPrecio.getText().replace(",", "."));
                int stock = Integer.parseInt(fStock.getText());
                int idCat  = cats.get(cbCat.getSelectedIndex()).getId();
                int idProv = provs.get(cbProv.getSelectedIndex()).getId();
                if (productoController.actualizarProducto(id, fNombre.getText(), fDesc.getText(), precio, stock, idCat, idProv)) {
                    exito("Producto actualizado."); refrescar();
                } else error("No se pudo actualizar.");
            } catch (NumberFormatException ex) { error("Precio y stock deben ser números válidos."); }
        }
    }

    private void eliminarFila(int fila) {
        int id = Integer.parseInt(modeloTabla.getValueAt(fila, 0).toString());
        String nombre = modeloTabla.getValueAt(fila, 1).toString();
        if (JOptionPane.showConfirmDialog(this, "¿Eliminar \"" + nombre + "\"?",
                "Confirmar", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE) == JOptionPane.YES_OPTION) {
            if (productoController.eliminarProducto(id)) { exito("Producto eliminado."); refrescar(); }
            else error("No se pudo eliminar.");
        }
    }

    private JPanel construirForm(String[] labels, JComponent[] campos) {
        JPanel p = new JPanel(new GridBagLayout());
        p.setBackground(C_WHITE);
        p.setBorder(new EmptyBorder(12, 12, 12, 12));
        GridBagConstraints g = new GridBagConstraints();
        g.insets = new Insets(6, 6, 6, 6);
        g.fill = GridBagConstraints.HORIZONTAL;
        for (int i = 0; i < labels.length; i++) {
            g.gridx = 0; g.gridy = i; g.weightx = 0;
            JLabel lbl = new JLabel(labels[i]);
            lbl.setFont(new Font("Consolas", Font.BOLD, 11));
            lbl.setForeground(C_PURPLE);
            p.add(lbl, g);
            g.gridx = 1; g.weightx = 1;
            campos[i].setPreferredSize(new Dimension(260, 32));
            p.add(campos[i], g);
        }
        return p;
    }

    private JButton crearBotonTop(String texto, Color bg, Color fg) {
        JButton b = new JButton(texto);
        b.setBackground(bg); b.setForeground(fg);
        b.setFont(new Font("Segoe UI", Font.BOLD, 13));
        b.setBorder(new EmptyBorder(8, 18, 8, 18));
        b.setFocusPainted(false); b.setOpaque(true); b.setBorderPainted(false);
        b.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        b.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override public void mouseEntered(java.awt.event.MouseEvent e) {
                b.setBackground(new Color(Math.min(bg.getRed()+20,255), Math.min(bg.getGreen()+20,255), Math.min(bg.getBlue()+20,255)));
            }
            @Override public void mouseExited(java.awt.event.MouseEvent e) { b.setBackground(bg); }
        });
        return b;
    }

    private JButton crearBotonInline(String texto, Color color) {
        JButton b = new JButton(texto);
        b.setFont(new Font("Segoe UI", Font.BOLD, 11));
        b.setForeground(color);
        b.setBackground(C_WHITE);
        b.setBorder(BorderFactory.createLineBorder(color, 1));
        b.setFocusPainted(false);
        b.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        b.setPreferredSize(new Dimension(62, 26));
        return b;
    }

    private JButton crearBotonRefrescar() {
        JButton b = new JButton("↻ Refrescar");
        b.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        b.setForeground(C_PURPLE);
        b.setBackground(C_WHITE);
        b.setBorder(BorderFactory.createLineBorder(C_BORDER, 1));
        b.setFocusPainted(false);
        b.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        b.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override public void mouseEntered(java.awt.event.MouseEvent e) {
                b.setBackground(new Color(245, 243, 255));
            }
            @Override public void mouseExited(java.awt.event.MouseEvent e) { b.setBackground(C_WHITE); }
        });
        return b;
    }

    private void exito(String msg) { JOptionPane.showMessageDialog(this, msg, "Éxito", JOptionPane.INFORMATION_MESSAGE); }
    private void error(String msg)  { JOptionPane.showMessageDialog(this, msg, "Error", JOptionPane.ERROR_MESSAGE); }
}
