package com.levelup.gui.paneles;

import com.levelup.controlador.UsuarioController;
import com.levelup.gui.GUIUtils;
import com.levelup.modelo.Usuario;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.*;
import java.awt.*;
import java.util.List;

/**
 * Panel de gestión de usuarios. Solo accesible para administradores.
 * Permite crear usuarios, cambiar contraseñas y eliminar cuentas.
 */
public class UsuariosPanel extends JPanel {

    private final UsuarioController usuarioController;
    private final Usuario usuarioActivo;
    private JTable tabla;
    private DefaultTableModel modeloTabla;
    private TableRowSorter<DefaultTableModel> sorter;
    private JPanel cuerpo;

    public UsuariosPanel(Usuario usuarioActivo) {
        this.usuarioActivo = usuarioActivo;
        this.usuarioController = new UsuarioController();
        setLayout(new BorderLayout(0, 0));
        setBackground(GUIUtils.C_BG);
        construirUI();
        cargarDatos();
    }

    private void construirUI() {
        JButton btnAnadir = GUIUtils.crearBotonTop("+ Añadir usuario", GUIUtils.C_ORANGE, GUIUtils.C_WHITE);
        btnAnadir.addActionListener(e -> dialogoAnadir());
        add(GUIUtils.construirTopbar("Gestión de Usuarios", GUIUtils.panelAccionTopbar(btnAnadir)), BorderLayout.NORTH);
        cuerpo = new JPanel(new BorderLayout(0, 16));
        cuerpo.setBackground(GUIUtils.C_BG);
        cuerpo.setBorder(new EmptyBorder(20, 28, 20, 28));
        cuerpo.add(construirTarjetas(), BorderLayout.NORTH);
        cuerpo.add(construirPanelTabla(), BorderLayout.CENTER);
        add(cuerpo, BorderLayout.CENTER);
    }

    private JPanel construirTarjetas() {
        JPanel panel = new JPanel(new GridLayout(1, 2, 12, 0));
        panel.setBackground(GUIUtils.C_BG);
        panel.setPreferredSize(new Dimension(0, 110));
        List<Usuario> todos = usuarioController.obtenerTodos();
        long admins    = todos.stream().filter(u -> "administrador".equals(u.getRol())).count();
        long empleados = todos.stream().filter(u -> "empleado".equals(u.getRol())).count();
        panel.add(GUIUtils.crearTarjeta("Administradores", String.valueOf(admins),    "acceso completo",   GUIUtils.C_PURPLE));
        panel.add(GUIUtils.crearTarjeta("Empleados",       String.valueOf(empleados), "acceso de consulta", GUIUtils.C_BLUE));
        return panel;
    }

    private JPanel construirPanelTabla() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(GUIUtils.C_WHITE);
        panel.setBorder(BorderFactory.createLineBorder(GUIUtils.C_BORDER, 1));

        String[] cols = {"ID", "Usuario", "Nombre", "Rol", ""};
        modeloTabla = new DefaultTableModel(cols, 0) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };
        tabla = new JTable(modeloTabla);
        sorter = GUIUtils.estilizarTabla(tabla, modeloTabla);

        tabla.getColumnModel().getColumn(0).setMaxWidth(48);
        tabla.getColumnModel().getColumn(3).setMinWidth(140);
        tabla.getColumnModel().getColumn(3).setMaxWidth(140);
        tabla.getColumnModel().getColumn(4).setMinWidth(180);
        tabla.getColumnModel().getColumn(4).setMaxWidth(180);

        // Renderer Rol con color
        tabla.getColumnModel().getColumn(3).setCellRenderer(new DefaultTableCellRenderer() {
            @Override public Component getTableCellRendererComponent(JTable t, Object v,
                    boolean sel, boolean foc, int r, int c) {
                JLabel lbl = (JLabel) super.getTableCellRendererComponent(t, v, sel, foc, r, c);
                String val = v != null ? v.toString() : "";
                lbl.setHorizontalAlignment(SwingConstants.CENTER);
                lbl.setFont(new Font("Segoe UI", Font.BOLD, 11));
                lbl.setBackground(sel ? GUIUtils.C_SEL_BG : r % 2 == 0 ? GUIUtils.C_WHITE : GUIUtils.C_ROW_ALT);
                lbl.setForeground("administrador".equals(val) ? GUIUtils.C_PURPLE : GUIUtils.C_BLUE);
                return lbl;
            }
        });

        tabla.getColumnModel().getColumn(4).setCellRenderer(new AccionesRenderer());
        tabla.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override public void mouseClicked(java.awt.event.MouseEvent e) {
                int fila = tabla.rowAtPoint(e.getPoint());
                int col  = tabla.columnAtPoint(e.getPoint());
                if (col != 4 || fila < 0) return;
                int modelRow = tabla.convertRowIndexToModel(fila);
                int x = e.getX() - tabla.getCellRect(fila, col, true).x;
                if (x < 90) dialogoCambiarPassword(modelRow); else eliminarFila(modelRow);
            }
        });
        tabla.addMouseMotionListener(new java.awt.event.MouseMotionAdapter() {
            @Override public void mouseMoved(java.awt.event.MouseEvent e) { tabla.repaint(); }
        });

        panel.add(GUIUtils.construirCabeceraTabla("Listado de usuarios", "Buscar por ID, usuario, nombre...", sorter, this::refrescar), BorderLayout.NORTH);
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
        private final JButton btnPass     = GUIUtils.crearBotonInline("Contraseña", GUIUtils.C_BLUE, 76);
        private final JButton btnEliminar = GUIUtils.crearBotonInline("Eliminar",   GUIUtils.C_RED,  76);
        AccionesRenderer() { setLayout(new FlowLayout(FlowLayout.CENTER, 4, 6)); setOpaque(true); add(btnPass); add(btnEliminar); }
        @Override public Component getTableCellRendererComponent(JTable t, Object v, boolean sel, boolean foc, int r, int c) {
            boolean hover = false;
            try { Point mp = t.getMousePosition(); if (mp != null) hover = t.rowAtPoint(mp) == r; } catch (Exception ex) {}
            setBackground(sel ? GUIUtils.C_SEL_BG : r % 2 == 0 ? GUIUtils.C_WHITE : GUIUtils.C_ROW_ALT);
            btnPass.setBackground(hover ? GUIUtils.C_BLUE : GUIUtils.C_WHITE); btnPass.setForeground(hover ? GUIUtils.C_WHITE : GUIUtils.C_BLUE);
            btnEliminar.setBackground(hover ? GUIUtils.C_RED : GUIUtils.C_WHITE); btnEliminar.setForeground(hover ? GUIUtils.C_WHITE : GUIUtils.C_RED);
            return this;
        }
    }

    private void cargarDatos() {
        modeloTabla.setRowCount(0);
        for (Usuario u : usuarioController.obtenerTodos())
            modeloTabla.addRow(new Object[]{u.getId(), u.getNombreUsuario(), u.getNombre(), u.getRol(), ""});
    }

    private void dialogoAnadir() {
        JTextField fU = new JTextField(), fN = new JTextField();
        JPasswordField fP = new JPasswordField();
        JComboBox<String> cbRol = new JComboBox<>(new String[]{"administrador", "empleado"});
        JPanel form = GUIUtils.construirForm(new String[]{"Usuario","Nombre completo","Contraseña","Rol"}, new JComponent[]{fU,fN,fP,cbRol});
        if (JOptionPane.showConfirmDialog(this, form, "Añadir usuario", JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE) == JOptionPane.OK_OPTION) {
            if (usuarioController.añadirUsuario(fU.getText(), fN.getText(), new String(fP.getPassword()), cbRol.getSelectedItem().toString())) {
                GUIUtils.exito(this, "Usuario añadido correctamente."); refrescar();
            } else GUIUtils.error(this, "No se pudo añadir. Revisa los datos.");
        }
    }

    private void dialogoCambiarPassword(int modelRow) {
        int id = Integer.parseInt(modeloTabla.getValueAt(modelRow, 0).toString());
        String nombre = modeloTabla.getValueAt(modelRow, 1).toString();
        JPasswordField fP = new JPasswordField();
        JPanel form = GUIUtils.construirForm(new String[]{"Nueva contraseña para " + nombre}, new JComponent[]{fP});
        if (JOptionPane.showConfirmDialog(this, form, "Cambiar contraseña", JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE) == JOptionPane.OK_OPTION) {
            if (usuarioController.actualizarPassword(id, new String(fP.getPassword()))) GUIUtils.exito(this, "Contraseña actualizada.");
            else GUIUtils.error(this, "No se pudo actualizar. Mínimo 6 caracteres.");
        }
    }

    private void eliminarFila(int modelRow) {
        int id = Integer.parseInt(modeloTabla.getValueAt(modelRow, 0).toString());
        String nombre = modeloTabla.getValueAt(modelRow, 1).toString();
        if (id == usuarioActivo.getId()) { GUIUtils.error(this, "No puedes eliminar tu propio usuario."); return; }
        if (JOptionPane.showConfirmDialog(this, "¿Eliminar al usuario \"" + nombre + "\"?",
                "Confirmar", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE) == JOptionPane.YES_OPTION) {
            if (usuarioController.eliminarUsuario(id)) { GUIUtils.exito(this, "Usuario eliminado."); refrescar(); }
            else GUIUtils.error(this, "No se pudo eliminar.");
        }
    }
}
