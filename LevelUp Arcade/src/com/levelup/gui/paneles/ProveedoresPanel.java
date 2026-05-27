package com.levelup.gui.paneles;

import com.levelup.controlador.ProveedorController;
import com.levelup.gui.GUIUtils;
import com.levelup.modelo.Proveedor;
import com.levelup.modelo.Usuario;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.*;
import java.awt.*;
import java.util.List;

/**
 * Panel de gestión de proveedores. Permite visualizar, buscar, añadir,
 * editar y eliminar proveedores según el rol del usuario.
 */
public class ProveedoresPanel extends JPanel {

    private final ProveedorController proveedorController;
    private final Usuario usuarioActivo;
    private final boolean esAdmin;
    private JTable tabla;
    private DefaultTableModel modeloTabla;
    private TableRowSorter<DefaultTableModel> sorter;
    private JPanel cuerpo;

    public ProveedoresPanel(Usuario usuarioActivo) {
        this.usuarioActivo = usuarioActivo;
        this.esAdmin = "administrador".equals(usuarioActivo.getRol());
        this.proveedorController = new ProveedorController();
        setLayout(new BorderLayout(0, 0));
        setBackground(GUIUtils.C_BG);
        construirUI();
        cargarDatos();
    }

    private void construirUI() {
        JButton btnAnadir = null;
        if (esAdmin) {
            btnAnadir = GUIUtils.crearBotonTop("+ Añadir proveedor", GUIUtils.C_ORANGE, GUIUtils.C_WHITE);
            btnAnadir.addActionListener(e -> dialogoAnadir());
        }
        add(GUIUtils.construirTopbar("Gestión de Proveedores", GUIUtils.panelAccionTopbar(btnAnadir)), BorderLayout.NORTH);
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
        List<Proveedor> todos = proveedorController.obtenerTodos();
        long conEmail    = todos.stream().filter(p -> p.getEmail()    != null && !p.getEmail().isEmpty()).count();
        long conTelefono = todos.stream().filter(p -> p.getTelefono() != null && !p.getTelefono().isEmpty()).count();
        panel.add(GUIUtils.crearTarjeta("Total proveedores", String.valueOf(todos.size()), "registrados en el sistema", GUIUtils.C_PURPLE));
        panel.add(GUIUtils.crearTarjeta("Con email",         String.valueOf(conEmail),     "contacto por correo",       GUIUtils.C_BLUE));
        panel.add(GUIUtils.crearTarjeta("Con teléfono",      String.valueOf(conTelefono),  "contacto directo",          GUIUtils.C_ORANGE));
        return panel;
    }

    private JPanel construirPanelTabla() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(GUIUtils.C_WHITE);
        panel.setBorder(BorderFactory.createLineBorder(GUIUtils.C_BORDER, 1));

        String[] cols = {"ID", "Nombre", "Email", "Teléfono", "Dirección", ""};
        modeloTabla = new DefaultTableModel(cols, 0) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };
        tabla = new JTable(modeloTabla);
        sorter = GUIUtils.estilizarTabla(tabla, modeloTabla);

        tabla.getColumnModel().getColumn(0).setMaxWidth(48);
        tabla.getColumnModel().getColumn(5).setMinWidth(esAdmin ? 140 : 0);
        tabla.getColumnModel().getColumn(5).setMaxWidth(esAdmin ? 140 : 0);

        if (esAdmin) {
            tabla.getColumnModel().getColumn(5).setCellRenderer(new AccionesRenderer());
            tabla.addMouseListener(new java.awt.event.MouseAdapter() {
                @Override public void mouseClicked(java.awt.event.MouseEvent e) {
                    int fila = tabla.rowAtPoint(e.getPoint());
                    int col  = tabla.columnAtPoint(e.getPoint());
                    if (col != 5 || fila < 0) return;
                    int modelRow = tabla.convertRowIndexToModel(fila);
                    int x = e.getX() - tabla.getCellRect(fila, col, true).x;
                    if (x < 70) dialogoEditar(modelRow); else eliminarFila(modelRow);
                }
            });
            tabla.addMouseMotionListener(new java.awt.event.MouseMotionAdapter() {
                @Override public void mouseMoved(java.awt.event.MouseEvent e) { tabla.repaint(); }
            });
        }

        panel.add(GUIUtils.construirCabeceraTabla("Directorio de proveedores", "Buscar por ID, nombre, email...", sorter, this::refrescar), BorderLayout.NORTH);
        panel.add(GUIUtils.crearScrollTabla(tabla), BorderLayout.CENTER);
        return panel;
    }

    private void refrescar() {
        cargarDatos();
        cuerpo.remove(0);
        cuerpo.add(construirTarjetas(), BorderLayout.NORTH, 0);
        cuerpo.revalidate(); cuerpo.repaint();
    }

    private class AccionesRenderer extends JPanel implements TableCellRenderer {
        private final JButton btnEditar   = GUIUtils.crearBotonInline("Editar",   GUIUtils.C_BLUE);
        private final JButton btnEliminar = GUIUtils.crearBotonInline("Eliminar", GUIUtils.C_RED);
        AccionesRenderer() { setLayout(new FlowLayout(FlowLayout.CENTER, 4, 6)); setOpaque(true); add(btnEditar); add(btnEliminar); }
        @Override public Component getTableCellRendererComponent(JTable t, Object v, boolean sel, boolean foc, int r, int c) {
            boolean hover = false;
            try { Point mp = t.getMousePosition(); if (mp != null) hover = t.rowAtPoint(mp) == r; } catch (Exception ex) {}
            setBackground(sel ? GUIUtils.C_SEL_BG : r % 2 == 0 ? GUIUtils.C_WHITE : GUIUtils.C_ROW_ALT);
            btnEditar.setBackground(hover ? GUIUtils.C_BLUE : GUIUtils.C_WHITE); btnEditar.setForeground(hover ? GUIUtils.C_WHITE : GUIUtils.C_BLUE);
            btnEliminar.setBackground(hover ? GUIUtils.C_RED : GUIUtils.C_WHITE); btnEliminar.setForeground(hover ? GUIUtils.C_WHITE : GUIUtils.C_RED);
            return this;
        }
    }

    private void cargarDatos() {
        modeloTabla.setRowCount(0);
        for (Proveedor p : proveedorController.obtenerTodos())
            modeloTabla.addRow(new Object[]{p.getId(), p.getNombre(), p.getEmail(), p.getTelefono(), p.getDireccion(), ""});
    }

    private void dialogoAnadir() {
        JTextField fN = new JTextField(), fE = new JTextField(), fT = new JTextField(), fD = new JTextField();
        JPanel form = GUIUtils.construirForm(new String[]{"Nombre","Email","Teléfono","Dirección"}, new JComponent[]{fN,fE,fT,fD});
        if (JOptionPane.showConfirmDialog(this, form, "Añadir proveedor", JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE) == JOptionPane.OK_OPTION) {
            if (proveedorController.añadirProveedor(fN.getText(), fE.getText(), fD.getText(), fT.getText())) { GUIUtils.exito(this, "Proveedor añadido."); refrescar(); }
            else GUIUtils.error(this, "No se pudo añadir. Revisa los datos.");
        }
    }

    private void dialogoEditar(int modelRow) {
        int id = Integer.parseInt(modeloTabla.getValueAt(modelRow, 0).toString());
        Proveedor p = proveedorController.obtenerPorId(id);
        if (p == null) return;
        JTextField fN = new JTextField(p.getNombre()), fE = new JTextField(p.getEmail()),
                   fT = new JTextField(p.getTelefono()), fD = new JTextField(p.getDireccion());
        JPanel form = GUIUtils.construirForm(new String[]{"Nombre","Email","Teléfono","Dirección"}, new JComponent[]{fN,fE,fT,fD});
        if (JOptionPane.showConfirmDialog(this, form, "Editar proveedor", JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE) == JOptionPane.OK_OPTION) {
            if (proveedorController.actualizarProveedor(id, fN.getText(), fE.getText(), fD.getText(), fT.getText())) { GUIUtils.exito(this, "Proveedor actualizado."); refrescar(); }
            else GUIUtils.error(this, "No se pudo actualizar.");
        }
    }

    private void eliminarFila(int modelRow) {
        int id = Integer.parseInt(modeloTabla.getValueAt(modelRow, 0).toString());
        String nombre = modeloTabla.getValueAt(modelRow, 1).toString();
        if (JOptionPane.showConfirmDialog(this, "¿Eliminar al proveedor \"" + nombre + "\"?",
                "Confirmar", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE) == JOptionPane.YES_OPTION) {
            if (proveedorController.eliminarProveedor(id)) { GUIUtils.exito(this, "Proveedor eliminado."); refrescar(); }
            else GUIUtils.error(this, "No se pudo eliminar.");
        }
    }
}
