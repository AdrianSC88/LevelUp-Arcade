package com.levelup.gui.paneles;

import com.levelup.controlador.CategoriaController;
import com.levelup.controlador.ProductoController;
import com.levelup.controlador.ProveedorController;
import com.levelup.gui.GUIUtils;
import com.levelup.modelo.Categoria;
import com.levelup.modelo.Producto;
import com.levelup.modelo.Proveedor;
import com.levelup.modelo.Usuario;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.*;
import java.awt.*;
import java.util.List;

/**
 * Panel de gestión de productos. Permite visualizar, buscar, añadir,
 * editar y eliminar productos del catálogo según el rol del usuario.
 */
public class ProductosPanel extends JPanel {

    private final ProductoController productoController;
    private final CategoriaController categoriaController;
    private final ProveedorController proveedorController;
    private final Usuario usuarioActivo;
    private final boolean esAdmin;

    private JTable tabla;
    private DefaultTableModel modeloTabla;
    private TableRowSorter<DefaultTableModel> sorter;
    private JPanel cuerpo;

    public ProductosPanel(Usuario usuarioActivo) {
        this.usuarioActivo = usuarioActivo;
        this.esAdmin = "administrador".equals(usuarioActivo.getRol());
        this.productoController = new ProductoController();
        this.categoriaController = new CategoriaController();
        this.proveedorController = new ProveedorController();
        setLayout(new BorderLayout(0, 0));
        setBackground(GUIUtils.C_BG);
        construirUI();
        cargarDatos();
    }

    private void construirUI() {
        JButton btnAnadir = null;
        if (esAdmin) {
            btnAnadir = GUIUtils.crearBotonTop("+ Añadir producto", GUIUtils.C_ORANGE, GUIUtils.C_WHITE);
            btnAnadir.addActionListener(e -> dialogoAnadir());
        }
        add(GUIUtils.construirTopbar("Gestión de Productos", GUIUtils.panelAccionTopbar(btnAnadir)), BorderLayout.NORTH);

        cuerpo = new JPanel(new BorderLayout(0, 16));
        cuerpo.setBackground(GUIUtils.C_BG);
        cuerpo.setBorder(new EmptyBorder(20, 28, 20, 28));
        cuerpo.add(construirTarjetas(), BorderLayout.NORTH);
        cuerpo.add(construirPanelTabla(), BorderLayout.CENTER);
        add(cuerpo, BorderLayout.CENTER);
    }

    private JPanel construirTarjetas() {
        JPanel panel = new JPanel(new GridLayout(1, 3, 12, 0));
        panel.setBackground(GUIUtils.C_BG);
        panel.setPreferredSize(new Dimension(0, 110));

        List<Producto> todos = productoController.obtenerTodos();
        long sinStock       = todos.stream().filter(p -> p.getStock() == 0).count();
        long stockBajo      = todos.stream().filter(p -> p.getStock() > 0 && p.getStock() <= 10).count();
        long totalUnidades  = todos.stream().mapToLong(Producto::getStock).sum();
        double valorTotal   = todos.stream().mapToDouble(p -> p.getPrecio() * p.getStock()).sum();

        panel.add(GUIUtils.crearTarjeta("Total productos",        String.valueOf(todos.size()),               "unidades en catálogo",             GUIUtils.C_PURPLE));
        panel.add(GUIUtils.crearTarjeta("Stock bajo / sin stock", stockBajo + " / " + sinStock,               "requieren atención",                GUIUtils.C_ORANGE));
        panel.add(GUIUtils.crearTarjeta("Valor inventario",       String.format("%.2f €", valorTotal),        totalUnidades + " uds. en stock",   GUIUtils.C_BLUE));
        return panel;
    }

    private JPanel construirPanelTabla() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(GUIUtils.C_WHITE);
        panel.setBorder(BorderFactory.createLineBorder(GUIUtils.C_BORDER, 1));

        String[] cols = {"ID", "Nombre", "Categoría", "Proveedor", "Precio", "Stock", "Estado", ""};
        modeloTabla = new DefaultTableModel(cols, 0) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
            @Override public Class<?> getColumnClass(int c) { return String.class; }
        };

        tabla = new JTable(modeloTabla);
        sorter = GUIUtils.estilizarTabla(tabla, modeloTabla);

        // Tooltip con descripción al pasar sobre la columna Nombre
        tabla.addMouseMotionListener(new java.awt.event.MouseMotionAdapter() {
            @Override public void mouseMoved(java.awt.event.MouseEvent e) {
                int fila = tabla.rowAtPoint(e.getPoint());
                int col  = tabla.columnAtPoint(e.getPoint());
                tabla.repaint();
                if (fila >= 0 && col == 1) {
                    int modelRow = tabla.convertRowIndexToModel(fila);
                    int id = Integer.parseInt(modeloTabla.getValueAt(modelRow, 0).toString());
                    Producto p = productoController.obtenerPorId(id);
                    String desc = (p != null && p.getDescripcion() != null && !p.getDescripcion().isEmpty())
                        ? "<html><b>Descripción:</b><br>" + p.getDescripcion() + "</html>" : null;
                    tabla.setToolTipText(desc);
                } else {
                    tabla.setToolTipText(null);
                }
            }
        });

        // Anchos de columna
        tabla.getColumnModel().getColumn(0).setMaxWidth(48);
        tabla.getColumnModel().getColumn(4).setMaxWidth(90);
        tabla.getColumnModel().getColumn(5).setMaxWidth(70);
        tabla.getColumnModel().getColumn(6).setMaxWidth(100);
        tabla.getColumnModel().getColumn(7).setMinWidth(esAdmin ? 140 : 0);
        tabla.getColumnModel().getColumn(7).setMaxWidth(esAdmin ? 140 : 0);

        // Renderer Estado con color
        tabla.getColumnModel().getColumn(6).setCellRenderer(new DefaultTableCellRenderer() {
            @Override public Component getTableCellRendererComponent(JTable t, Object v,
                    boolean sel, boolean foc, int r, int c) {
                JLabel lbl = (JLabel) super.getTableCellRendererComponent(t, v, sel, foc, r, c);
                String val = v != null ? v.toString() : "";
                lbl.setHorizontalAlignment(SwingConstants.CENTER);
                lbl.setFont(new Font("Segoe UI", Font.BOLD, 11));
                lbl.setBackground(sel ? GUIUtils.C_SEL_BG : r % 2 == 0 ? GUIUtils.C_WHITE : GUIUtils.C_ROW_ALT);
                lbl.setForeground(switch (val) {
                    case "Sin stock"   -> GUIUtils.C_RED;
                    case "Stock bajo"  -> GUIUtils.C_ORANGE;
                    default            -> GUIUtils.C_GREEN;
                });
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
                    int modelRow = tabla.convertRowIndexToModel(fila);
                    int x = e.getX() - tabla.getCellRect(fila, col, true).x;
                    if (x < 70) dialogoEditar(modelRow);
                    else eliminarFila(modelRow);
                }
            });
        }

        panel.add(GUIUtils.construirCabeceraTabla("Inventario", "Buscar por ID, nombre, categoría...", sorter, this::refrescar), BorderLayout.NORTH);
        panel.add(GUIUtils.crearScrollTabla(tabla), BorderLayout.CENTER);
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
        private final JButton btnEditar   = GUIUtils.crearBotonInline("Editar",   GUIUtils.C_BLUE);
        private final JButton btnEliminar = GUIUtils.crearBotonInline("Eliminar", GUIUtils.C_RED);
        AccionesRenderer() {
            setLayout(new FlowLayout(FlowLayout.CENTER, 4, 6));
            setOpaque(true); add(btnEditar); add(btnEliminar);
        }
        @Override public Component getTableCellRendererComponent(JTable t, Object v,
                boolean sel, boolean foc, int r, int c) {
            boolean hover = false;
            try { Point mp = t.getMousePosition(); if (mp != null) hover = t.rowAtPoint(mp) == r; }
            catch (Exception ex) { /* ignorar */ }
            setBackground(sel ? GUIUtils.C_SEL_BG : r % 2 == 0 ? GUIUtils.C_WHITE : GUIUtils.C_ROW_ALT);
            btnEditar.setBackground(hover ? GUIUtils.C_BLUE : GUIUtils.C_WHITE);
            btnEditar.setForeground(hover ? GUIUtils.C_WHITE : GUIUtils.C_BLUE);
            btnEliminar.setBackground(hover ? GUIUtils.C_RED : GUIUtils.C_WHITE);
            btnEliminar.setForeground(hover ? GUIUtils.C_WHITE : GUIUtils.C_RED);
            return this;
        }
    }

    private void cargarDatos() {
        modeloTabla.setRowCount(0);
        for (Producto p : productoController.obtenerTodos()) {
            String estado = p.getStock() == 0 ? "Sin stock" : p.getStock() <= 10 ? "Stock bajo" : "Disponible";
            modeloTabla.addRow(new Object[]{
                p.getId(), p.getNombre(),
                p.getCategoria()  != null ? p.getCategoria().getNombre()  : "-",
                p.getProveedor()  != null ? p.getProveedor().getNombre()  : "-",
                String.format("%.2f €", p.getPrecio()), p.getStock(), estado, ""
            });
        }
    }

    private void dialogoAnadir() {
        List<Categoria> cats  = categoriaController.obtenerTodas();
        List<Proveedor> provs = proveedorController.obtenerTodos();
        JTextField fNombre = new JTextField(), fDesc = new JTextField(),
                   fPrecio = new JTextField(), fStock = new JTextField();
        JComboBox<String> cbCat  = new JComboBox<>(cats.stream().map(Categoria::getNombre).toArray(String[]::new));
        JComboBox<String> cbProv = new JComboBox<>(provs.stream().map(Proveedor::getNombre).toArray(String[]::new));
        JPanel form = GUIUtils.construirForm(
            new String[]{"Nombre", "Descripción", "Precio", "Stock", "Categoría", "Proveedor"},
            new JComponent[]{fNombre, fDesc, fPrecio, fStock, cbCat, cbProv}
        );
        if (JOptionPane.showConfirmDialog(this, form, "Añadir producto",
                JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE) == JOptionPane.OK_OPTION) {
            try {
                double precio = Double.parseDouble(fPrecio.getText().replace(",", "."));
                int stock = Integer.parseInt(fStock.getText());
                boolean ok = productoController.añadirProducto(fNombre.getText(), fDesc.getText(),
                    precio, stock, cats.get(cbCat.getSelectedIndex()).getId(),
                    provs.get(cbProv.getSelectedIndex()).getId());
                if (ok) { GUIUtils.exito(this, "Producto añadido correctamente."); refrescar(); }
                else GUIUtils.error(this, "No se pudo añadir. Revisa los datos.");
            } catch (NumberFormatException ex) { GUIUtils.error(this, "Precio y stock deben ser números válidos."); }
        }
    }

    private void dialogoEditar(int modelRow) {
        int id = Integer.parseInt(modeloTabla.getValueAt(modelRow, 0).toString());
        Producto p = productoController.obtenerPorId(id);
        if (p == null) return;
        List<Categoria> cats  = categoriaController.obtenerTodas();
        List<Proveedor> provs = proveedorController.obtenerTodos();
        JTextField fNombre = new JTextField(p.getNombre()), fDesc = new JTextField(p.getDescripcion()),
                   fPrecio = new JTextField(String.valueOf(p.getPrecio())), fStock = new JTextField(String.valueOf(p.getStock()));
        JComboBox<String> cbCat  = new JComboBox<>(cats.stream().map(Categoria::getNombre).toArray(String[]::new));
        JComboBox<String> cbProv = new JComboBox<>(provs.stream().map(Proveedor::getNombre).toArray(String[]::new));
        cats.stream().filter(c -> c.getId() == p.getCategoria().getId()).findFirst().ifPresent(c -> cbCat.setSelectedIndex(cats.indexOf(c)));
        provs.stream().filter(v -> v.getId() == p.getProveedor().getId()).findFirst().ifPresent(v -> cbProv.setSelectedIndex(provs.indexOf(v)));
        JPanel form = GUIUtils.construirForm(
            new String[]{"Nombre", "Descripción", "Precio", "Stock", "Categoría", "Proveedor"},
            new JComponent[]{fNombre, fDesc, fPrecio, fStock, cbCat, cbProv}
        );
        if (JOptionPane.showConfirmDialog(this, form, "Editar producto",
                JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE) == JOptionPane.OK_OPTION) {
            try {
                double precio = Double.parseDouble(fPrecio.getText().replace(",", "."));
                int stock = Integer.parseInt(fStock.getText());
                boolean ok = productoController.actualizarProducto(id, fNombre.getText(), fDesc.getText(),
                    precio, stock, cats.get(cbCat.getSelectedIndex()).getId(),
                    provs.get(cbProv.getSelectedIndex()).getId());
                if (ok) { GUIUtils.exito(this, "Producto actualizado."); refrescar(); }
                else GUIUtils.error(this, "No se pudo actualizar.");
            } catch (NumberFormatException ex) { GUIUtils.error(this, "Precio y stock deben ser números válidos."); }
        }
    }

    private void eliminarFila(int modelRow) {
        int id = Integer.parseInt(modeloTabla.getValueAt(modelRow, 0).toString());
        String nombre = modeloTabla.getValueAt(modelRow, 1).toString();
        if (JOptionPane.showConfirmDialog(this, "¿Eliminar \"" + nombre + "\"?",
                "Confirmar", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE) == JOptionPane.YES_OPTION) {
            if (productoController.eliminarProducto(id)) { GUIUtils.exito(this, "Producto eliminado."); refrescar(); }
            else GUIUtils.error(this, "No se pudo eliminar.");
        }
    }
}
