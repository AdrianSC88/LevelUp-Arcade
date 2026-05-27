package com.levelup.gui.paneles;

import com.levelup.controlador.CategoriaController;
import com.levelup.controlador.LlmController;
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
 * <p>
 * Integra botones de asistente IA en los formularios de añadir y editar,
 * tanto para generar la descripción del producto como para sugerir su categoría.
 * La columna Estado se colorea según el nivel de stock: rojo (sin stock),
 * naranja (stock bajo ≤ 10) y verde (disponible). Al pasar el ratón por la
 * columna Nombre se muestra la descripción del producto como tooltip.
 * </p>
 */
public class ProductosPanel extends JPanel {

    private final ProductoController productoController;
    private final CategoriaController categoriaController;
    private final ProveedorController proveedorController;
    private final LlmController llmController;
    private final Usuario usuarioActivo;
    private final boolean esAdmin;

    private JTable tabla;
    private DefaultTableModel modeloTabla;
    private TableRowSorter<DefaultTableModel> sorter;
    private JPanel cuerpo;

    /**
     * Construye el panel de productos para el usuario indicado.
     * Inicializa todos los controladores necesarios, construye la interfaz
     * y carga los datos desde la base de datos.
     *
     * @param usuarioActivo el usuario autenticado; determina si se muestran
     *                      las acciones de administrador
     */
    public ProductosPanel(Usuario usuarioActivo) {
        this.usuarioActivo = usuarioActivo;
        this.esAdmin = "administrador".equals(usuarioActivo.getRol());
        this.productoController  = new ProductoController();
        this.categoriaController = new CategoriaController();
        this.proveedorController = new ProveedorController();
        this.llmController       = new LlmController();
        setLayout(new BorderLayout(0, 0));
        setBackground(GUIUtils.C_BG);
        construirUI();
        cargarDatos();
    }

    /**
     * Construye la interfaz del panel: topbar con botón de añadir (solo admin),
     * panel de tarjetas de estadísticas y panel de tabla.
     */
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

    /**
     * Construye el panel de tarjetas con estadísticas del inventario:
     * total de productos, unidades con stock bajo o sin stock, y valor
     * total del inventario en euros.
     *
     * @return el panel de tarjetas listo para añadir al cuerpo
     */
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

    /**
     * Construye el panel de tabla con columnas ID, Nombre, Categoría, Proveedor,
     * Precio, Stock, Estado y (si es admin) Acciones.
     * Conecta el tooltip de descripción en la columna Nombre y los listeners de
     * ratón para los botones de acción y el hover.
     *
     * @return el panel de tabla listo para añadir al cuerpo
     */
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

    /**
     * Recarga los datos de la tabla y actualiza las tarjetas de estadísticas.
     * Se llama tras cualquier operación CRUD para mantener la vista sincronizada.
     */
    private void refrescar() {
        cargarDatos();
        cuerpo.remove(0);
        cuerpo.add(construirTarjetas(), BorderLayout.NORTH, 0);
        cuerpo.revalidate();
        cuerpo.repaint();
    }

    /**
     * Renderer de la columna de acciones. Muestra los botones "Editar" y "Eliminar"
     * con efecto hover al pasar el ratón por la fila.
     */
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

    /**
     * Vacía el modelo de la tabla y lo rellena con todos los productos
     * obtenidos del controlador. Calcula el estado de stock de cada producto
     * ("Sin stock", "Stock bajo" o "Disponible") para la columna de estado.
     */
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

    /**
     * Abre un diálogo para introducir los datos de un nuevo producto.
     * Incluye botones de asistente IA para generar la descripción y sugerir
     * la categoría basándose en el nombre introducido.
     * Valida que precio y stock sean valores numéricos antes de guardar.
     */
    private void dialogoAnadir() {
        List<Categoria> cats  = categoriaController.obtenerTodas();
        List<Proveedor> provs = proveedorController.obtenerTodos();

        JTextField fNombre = new JTextField();
        JTextField fPrecio = new JTextField();
        JTextField fStock  = new JTextField();
        JComboBox<String> cbCat  = new JComboBox<>(cats.stream().map(Categoria::getNombre).toArray(String[]::new));
        JComboBox<String> cbProv = new JComboBox<>(provs.stream().map(Proveedor::getNombre).toArray(String[]::new));

        // Campo descripción con botón IA
        JTextField fDesc = new JTextField();
        JPanel panelDesc = construirCampoConIA(fDesc, btn -> {
            String nombre = fNombre.getText().trim();
            if (nombre.isEmpty()) { GUIUtils.error(null, "Introduce primero el nombre del producto."); return; }
            btn.setText("..."); btn.setEnabled(false);
            fDesc.setText("Generando..."); fDesc.setEnabled(false);
            new SwingWorker<String, Void>() {
                @Override protected String doInBackground() { return llmController.generarDescripcion(nombre); }
                @Override protected void done() {
                    try { fDesc.setText(get()); } catch (Exception e) { fDesc.setText(""); }
                    fDesc.setEnabled(true);
                    btn.setText("▪ IA"); btn.setEnabled(true);
                }
            }.execute();
        });

        // Combo categoría con botón IA
        JPanel panelCat = construirComboConIA(cbCat, btn -> {
            String nombre = fNombre.getText().trim();
            if (nombre.isEmpty()) { GUIUtils.error(null, "Introduce primero el nombre del producto."); return; }
            btn.setText("..."); btn.setEnabled(false);
            new SwingWorker<String, Void>() {
                @Override protected String doInBackground() { return llmController.sugerirCategoria(nombre); }
                @Override protected void done() {
                    try {
                        String sugerida = get().trim();
                        for (int i = 0; i < cbCat.getItemCount(); i++) {
                            if (cbCat.getItemAt(i).equalsIgnoreCase(sugerida)) { cbCat.setSelectedIndex(i); return; }
                        }
                        for (int i = 0; i < cbCat.getItemCount(); i++) {
                            if (cbCat.getItemAt(i).toLowerCase().contains(sugerida.toLowerCase()) ||
                                sugerida.toLowerCase().contains(cbCat.getItemAt(i).toLowerCase())) {
                                cbCat.setSelectedIndex(i); return;
                            }
                        }
                    } catch (Exception e) { /* ignorar */ }
                    finally { btn.setText("▪ IA"); btn.setEnabled(true); }
                }
            }.execute();
        });

        JPanel form = new JPanel(new java.awt.GridBagLayout());
        form.setBackground(GUIUtils.C_WHITE);
        form.setBorder(new EmptyBorder(12, 12, 12, 12));
        java.awt.GridBagConstraints g = new java.awt.GridBagConstraints();
        g.insets = new java.awt.Insets(6, 6, 6, 6);
        g.fill = java.awt.GridBagConstraints.HORIZONTAL;

        String[] labels = {"Nombre", "Descripción", "Precio", "Stock", "Categoría", "Proveedor"};
        JComponent[] campos = {fNombre, panelDesc, fPrecio, fStock, panelCat, cbProv};
        for (int i = 0; i < labels.length; i++) {
            g.gridx = 0; g.gridy = i; g.weightx = 0;
            JLabel lbl = new JLabel(labels[i]);
            lbl.setFont(new Font("Consolas", Font.BOLD, 11)); lbl.setForeground(GUIUtils.C_PURPLE);
            form.add(lbl, g);
            g.gridx = 1; g.weightx = 1;
            campos[i].setPreferredSize(new java.awt.Dimension(300, 32));
            form.add(campos[i], g);
        }

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

    /**
     * Abre un diálogo para editar el producto de la fila indicada.
     * Precarga los datos actuales y ofrece los mismos botones de asistente IA
     * que el diálogo de añadir.
     *
     * @param modelRow índice de fila en el modelo (no en la vista)
     */
    private void dialogoEditar(int modelRow) {
        int id = Integer.parseInt(modeloTabla.getValueAt(modelRow, 0).toString());
        Producto p = productoController.obtenerPorId(id);
        if (p == null) return;
        List<Categoria> cats  = categoriaController.obtenerTodas();
        List<Proveedor> provs = proveedorController.obtenerTodos();

        JTextField fNombre = new JTextField(p.getNombre());
        JTextField fPrecio = new JTextField(String.valueOf(p.getPrecio()));
        JTextField fStock  = new JTextField(String.valueOf(p.getStock()));
        JComboBox<String> cbCat  = new JComboBox<>(cats.stream().map(Categoria::getNombre).toArray(String[]::new));
        JComboBox<String> cbProv = new JComboBox<>(provs.stream().map(Proveedor::getNombre).toArray(String[]::new));
        cats.stream().filter(c -> c.getId() == p.getCategoria().getId()).findFirst().ifPresent(c -> cbCat.setSelectedIndex(cats.indexOf(c)));
        provs.stream().filter(v -> v.getId() == p.getProveedor().getId()).findFirst().ifPresent(v -> cbProv.setSelectedIndex(provs.indexOf(v)));

        // Campo descripción con botón IA
        JTextField fDesc = new JTextField(p.getDescripcion());
        JPanel panelDesc = construirCampoConIA(fDesc, btn -> {
            String nombre = fNombre.getText().trim();
            if (nombre.isEmpty()) { GUIUtils.error(null, "Introduce primero el nombre del producto."); return; }
            btn.setText("..."); btn.setEnabled(false);
            fDesc.setText("Generando..."); fDesc.setEnabled(false);
            new SwingWorker<String, Void>() {
                @Override protected String doInBackground() { return llmController.generarDescripcion(nombre); }
                @Override protected void done() {
                    try { fDesc.setText(get()); } catch (Exception e) { fDesc.setText(p.getDescripcion()); }
                    fDesc.setEnabled(true);
                    btn.setText("▪ IA"); btn.setEnabled(true);
                }
            }.execute();
        });

        // Combo categoría con botón IA
        JPanel panelCat = construirComboConIA(cbCat, btn -> {
            String nombre = fNombre.getText().trim();
            if (nombre.isEmpty()) { GUIUtils.error(null, "Introduce primero el nombre del producto."); return; }
            btn.setText("..."); btn.setEnabled(false);
            new SwingWorker<String, Void>() {
                @Override protected String doInBackground() { return llmController.sugerirCategoria(nombre); }
                @Override protected void done() {
                    try {
                        String sugerida = get().trim();
                        for (int i = 0; i < cbCat.getItemCount(); i++) {
                            if (cbCat.getItemAt(i).equalsIgnoreCase(sugerida)) { cbCat.setSelectedIndex(i); return; }
                        }
                        for (int i = 0; i < cbCat.getItemCount(); i++) {
                            if (cbCat.getItemAt(i).toLowerCase().contains(sugerida.toLowerCase()) ||
                                sugerida.toLowerCase().contains(cbCat.getItemAt(i).toLowerCase())) {
                                cbCat.setSelectedIndex(i); return;
                            }
                        }
                    } catch (Exception e) { /* ignorar */ }
                    finally { btn.setText("▪ IA"); btn.setEnabled(true); }
                }
            }.execute();
        });

        JPanel form = new JPanel(new java.awt.GridBagLayout());
        form.setBackground(GUIUtils.C_WHITE);
        form.setBorder(new EmptyBorder(12, 12, 12, 12));
        java.awt.GridBagConstraints g = new java.awt.GridBagConstraints();
        g.insets = new java.awt.Insets(6, 6, 6, 6);
        g.fill = java.awt.GridBagConstraints.HORIZONTAL;

        String[] labels = {"Nombre", "Descripción", "Precio", "Stock", "Categoría", "Proveedor"};
        JComponent[] campos = {fNombre, panelDesc, fPrecio, fStock, panelCat, cbProv};
        for (int i = 0; i < labels.length; i++) {
            g.gridx = 0; g.gridy = i; g.weightx = 0;
            JLabel lbl = new JLabel(labels[i]);
            lbl.setFont(new Font("Consolas", Font.BOLD, 11)); lbl.setForeground(GUIUtils.C_PURPLE);
            form.add(lbl, g);
            g.gridx = 1; g.weightx = 1;
            campos[i].setPreferredSize(new java.awt.Dimension(300, 32));
            form.add(campos[i], g);
        }

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

    /**
     * Crea un panel con un campo de texto y un botón "IA" a la derecha.
     * El botón muestra feedback visual mientras la IA está generando:
     * cambia su texto a "..." y se deshabilita hasta recibir la respuesta.
     *
     * @param campo    el campo de texto donde se volcará el resultado generado
     * @param accionIA acción a ejecutar al pulsar el botón; recibe el propio botón
     *                 para que la acción pueda modificar su estado
     * @return el panel con campo y botón IA dispuestos horizontalmente
     */
    private JPanel construirCampoConIA(JTextField campo, java.util.function.Consumer<JButton> accionIA) {
        JPanel panel = new JPanel(new BorderLayout(4, 0));
        panel.setBackground(GUIUtils.C_WHITE);
        JButton btnIA = GUIUtils.crearBotonInline("▪ IA", GUIUtils.C_PURPLE, 52);
        btnIA.setToolTipText("Generar con Inteligencia Artificial");
        btnIA.addActionListener(e -> accionIA.accept(btnIA));
        panel.add(campo, BorderLayout.CENTER);
        panel.add(btnIA, BorderLayout.EAST);
        return panel;
    }

    /**
     * Crea un panel con un {@link JComboBox} y un botón "IA" a la derecha.
     * El botón muestra feedback visual mientras la IA está pensando y,
     * al recibir la respuesta, selecciona automáticamente la categoría
     * sugerida en el combo (por coincidencia exacta o parcial).
     *
     * @param combo    el combo donde se seleccionará la categoría sugerida
     * @param accionIA acción a ejecutar al pulsar el botón; recibe el propio botón
     *                 para que la acción pueda modificar su estado
     * @return el panel con combo y botón IA dispuestos horizontalmente
     */
    private JPanel construirComboConIA(JComboBox<String> combo, java.util.function.Consumer<JButton> accionIA) {
        JPanel panel = new JPanel(new BorderLayout(4, 0));
        panel.setBackground(GUIUtils.C_WHITE);
        JButton btnIA = GUIUtils.crearBotonInline("▪ IA", GUIUtils.C_PURPLE, 52);
        btnIA.setToolTipText("Sugerir con Inteligencia Artificial");
        btnIA.addActionListener(e -> accionIA.accept(btnIA));
        panel.add(combo, BorderLayout.CENTER);
        panel.add(btnIA, BorderLayout.EAST);
        return panel;
    }

    /**
     * Solicita confirmación y elimina el producto de la fila indicada.
     * Si el producto tiene pedidos asociados muestra un aviso específico
     * en lugar de un error genérico.
     *
     * @param modelRow índice de fila en el modelo (no en la vista)
     */
    private void eliminarFila(int modelRow) {
        int id = Integer.parseInt(modeloTabla.getValueAt(modelRow, 0).toString());
        String nombre = modeloTabla.getValueAt(modelRow, 1).toString();
        if (JOptionPane.showConfirmDialog(this, "¿Eliminar \"" + nombre + "\"?",
                "Confirmar", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE) == JOptionPane.YES_OPTION) {
            if (productoController.eliminarProducto(id)) {
                GUIUtils.exito(this, "Producto eliminado.");
                refrescar();
            } else if (productoController.tienePedidosAsociados(id)) {
                JOptionPane.showMessageDialog(this,
                    "No se puede eliminar \"" + nombre + "\":\ntiene pedidos asociados en el sistema.",
                    "Operación no permitida",
                    JOptionPane.WARNING_MESSAGE);
            } else {
                GUIUtils.error(this, "No se pudo eliminar.");
            }
        }
    }

}
