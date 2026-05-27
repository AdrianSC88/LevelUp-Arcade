package com.levelup.gui.paneles;

import com.levelup.controlador.ClienteController;
import com.levelup.gui.GUIUtils;
import com.levelup.modelo.Cliente;
import com.levelup.modelo.Usuario;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.*;
import java.awt.*;
import java.util.List;

/**
 * Panel de gestión de clientes. Permite visualizar, buscar, añadir,
 * editar y eliminar clientes según el rol del usuario.
 * <p>
 * Los administradores ven una columna de acciones con botones "Editar" y
 * "Eliminar" en cada fila. Los empleados solo tienen acceso de lectura.
 * </p>
 */
public class ClientesPanel extends JPanel {

    private final ClienteController clienteController;
    private final Usuario usuarioActivo;
    private final boolean esAdmin;
    private JTable tabla;
    private DefaultTableModel modeloTabla;
    private TableRowSorter<DefaultTableModel> sorter;
    private JPanel cuerpo;

    /**
     * Construye el panel de clientes para el usuario indicado.
     * Inicializa el controlador, construye la interfaz y carga los datos.
     *
     * @param usuarioActivo el usuario autenticado; determina si se muestran
     *                      las acciones de administrador
     */
    public ClientesPanel(Usuario usuarioActivo) {
        this.usuarioActivo = usuarioActivo;
        this.esAdmin = "administrador".equals(usuarioActivo.getRol());
        this.clienteController = new ClienteController();
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
            btnAnadir = GUIUtils.crearBotonTop("+ Añadir cliente", GUIUtils.C_ORANGE, GUIUtils.C_WHITE);
            btnAnadir.addActionListener(e -> dialogoAnadir());
        }
        add(GUIUtils.construirTopbar("Gestión de Clientes", GUIUtils.panelAccionTopbar(btnAnadir)), BorderLayout.NORTH);
        cuerpo = new JPanel(new BorderLayout(0, 16));
        cuerpo.setBackground(GUIUtils.C_BG);
        cuerpo.setBorder(new EmptyBorder(20, 28, 20, 28));
        cuerpo.add(construirTarjetas(), BorderLayout.NORTH);
        cuerpo.add(construirPanelTabla(), BorderLayout.CENTER);
        add(cuerpo, BorderLayout.CENTER);
    }

    /**
     * Construye el panel de tarjetas de estadísticas con el total de clientes,
     * cuántos tienen email registrado y cuántos tienen teléfono registrado.
     *
     * @return el panel de tarjetas listo para añadir al cuerpo
     */
    private JPanel construirTarjetas() {
        JPanel panel = new JPanel(new GridLayout(1, 3, 12, 0));
        panel.setBackground(GUIUtils.C_BG);
        panel.setPreferredSize(new Dimension(0, 110));
        List<Cliente> todos = clienteController.obtenerTodos();
        long conEmail    = todos.stream().filter(c -> c.getEmail()    != null && !c.getEmail().isEmpty()).count();
        long conTelefono = todos.stream().filter(c -> c.getTelefono() != null && !c.getTelefono().isEmpty()).count();
        panel.add(GUIUtils.crearTarjeta("Total clientes",  String.valueOf(todos.size()), "registrados en el sistema", GUIUtils.C_PURPLE));
        panel.add(GUIUtils.crearTarjeta("Con email",       String.valueOf(conEmail),     "contacto por correo",       GUIUtils.C_BLUE));
        panel.add(GUIUtils.crearTarjeta("Con teléfono",    String.valueOf(conTelefono),  "contacto directo",          GUIUtils.C_ORANGE));
        return panel;
    }

    /**
     * Construye el panel de tabla con cabecera de búsqueda y columnas
     * ID, Nombre, Email, Teléfono, Dirección y (si es admin) Acciones.
     * Conecta los listeners de ratón para gestionar los clics en los botones
     * de acción y el efecto hover en las filas.
     *
     * @return el panel de tabla listo para añadir al cuerpo
     */
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

        panel.add(GUIUtils.construirCabeceraTabla("Directorio de clientes", "Buscar por ID, nombre, email...", sorter, this::refrescar), BorderLayout.NORTH);
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
        cuerpo.revalidate(); cuerpo.repaint();
    }

    /**
     * Renderer de la columna de acciones. Muestra dos botones inline
     * ("Editar" y "Eliminar") con efecto hover al pasar el ratón por la fila.
     */
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

    /**
     * Vacía el modelo de la tabla y lo rellena con todos los clientes
     * obtenidos del controlador.
     */
    private void cargarDatos() {
        modeloTabla.setRowCount(0);
        for (Cliente c : clienteController.obtenerTodos())
            modeloTabla.addRow(new Object[]{c.getId(), c.getNombre(), c.getEmail(), c.getTelefono(), c.getDireccion(), ""});
    }

    /**
     * Abre un diálogo para introducir los datos de un nuevo cliente.
     * Si el usuario confirma y la operación tiene éxito, refresca la vista.
     */
    private void dialogoAnadir() {
        JTextField fN = new JTextField(), fE = new JTextField(), fT = new JTextField(), fD = new JTextField();
        JPanel form = GUIUtils.construirForm(new String[]{"Nombre","Email","Teléfono","Dirección"}, new JComponent[]{fN,fE,fT,fD});
        if (JOptionPane.showConfirmDialog(this, form, "Añadir cliente", JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE) == JOptionPane.OK_OPTION) {
            if (clienteController.añadirCliente(fN.getText(), fE.getText(), fT.getText(), fD.getText())) { GUIUtils.exito(this, "Cliente añadido."); refrescar(); }
            else GUIUtils.error(this, "No se pudo añadir. Revisa los datos.");
        }
    }

    /**
     * Abre un diálogo para editar el cliente de la fila indicada.
     * Precarga los datos actuales en los campos del formulario.
     *
     * @param modelRow índice de fila en el modelo (no en la vista)
     */
    private void dialogoEditar(int modelRow) {
        int id = Integer.parseInt(modeloTabla.getValueAt(modelRow, 0).toString());
        Cliente c = clienteController.obtenerPorId(id);
        if (c == null) return;
        JTextField fN = new JTextField(c.getNombre()), fE = new JTextField(c.getEmail()),
                   fT = new JTextField(c.getTelefono()), fD = new JTextField(c.getDireccion());
        JPanel form = GUIUtils.construirForm(new String[]{"Nombre","Email","Teléfono","Dirección"}, new JComponent[]{fN,fE,fT,fD});
        if (JOptionPane.showConfirmDialog(this, form, "Editar cliente", JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE) == JOptionPane.OK_OPTION) {
            if (clienteController.actualizarCliente(id, fN.getText(), fE.getText(), fT.getText(), fD.getText())) { GUIUtils.exito(this, "Cliente actualizado."); refrescar(); }
            else GUIUtils.error(this, "No se pudo actualizar.");
        }
    }

    /**
     * Solicita confirmación y elimina el cliente de la fila indicada.
     *
     * @param modelRow índice de fila en el modelo (no en la vista)
     */
    private void eliminarFila(int modelRow) {
        int id = Integer.parseInt(modeloTabla.getValueAt(modelRow, 0).toString());
        String nombre = modeloTabla.getValueAt(modelRow, 1).toString();
        if (JOptionPane.showConfirmDialog(this, "¿Eliminar al cliente \"" + nombre + "\"?",
                "Confirmar", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE) == JOptionPane.YES_OPTION) {
            if (clienteController.eliminarCliente(id)) { GUIUtils.exito(this, "Cliente eliminado."); refrescar(); }
            else GUIUtils.error(this, "No se pudo eliminar.");
        }
    }
}
