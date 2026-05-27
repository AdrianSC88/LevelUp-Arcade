package com.levelup.gui;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;
import javax.swing.table.TableRowSorter;
import java.awt.*;

/**
 * Clase utilitaria con constantes de color y métodos de fábrica compartidos
 * por todos los paneles de la interfaz gráfica de LevelUp Arcade.
 * Centraliza la paleta de colores y los componentes reutilizables para
 * garantizar coherencia visual y reducir la duplicación de código.
 */
public final class GUIUtils {

    // ── PALETA DE COLORES ──────────────────────────────────────────────────────
    public static final Color C_BG        = new Color(248, 247, 255);
    public static final Color C_PURPLE    = new Color(92, 51, 181);
    public static final Color C_PURPLE_L  = new Color(196, 181, 253);
    public static final Color C_ORANGE    = new Color(249, 115, 22);
    public static final Color C_BLUE      = new Color(59, 130, 246);
    public static final Color C_RED       = new Color(239, 68, 68);
    public static final Color C_GREEN     = new Color(34, 197, 94);
    public static final Color C_WHITE     = Color.WHITE;
    public static final Color C_TEXT      = new Color(26, 18, 37);
    public static final Color C_MUTED     = new Color(107, 114, 128);
    public static final Color C_BORDER    = new Color(229, 224, 248);
    public static final Color C_HEADER    = new Color(45, 21, 114);
    public static final Color C_FIELD_BG  = new Color(252, 251, 255);
    public static final Color C_ROW_ALT   = new Color(245, 243, 255);
    public static final Color C_SEL_BG    = new Color(92, 51, 181, 30);
    public static final Color C_HOVER_BG  = new Color(92, 51, 181, 50);

    private GUIUtils() { /* no instanciable */ }

    // ── BOTONES ───────────────────────────────────────────────────────────────

    /**
     * Crea un botón principal de acción (topbar) con color de fondo y hover suave.
     */
    public static JButton crearBotonTop(String texto, Color bg, Color fg) {
        JButton b = new JButton(texto);
        b.setBackground(bg);
        b.setForeground(fg);
        b.setFont(new Font("Segoe UI", Font.BOLD, 13));
        b.setBorder(new EmptyBorder(8, 18, 8, 18));
        b.setFocusPainted(false);
        b.setOpaque(true);
        b.setBorderPainted(false);
        b.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        b.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override public void mouseEntered(java.awt.event.MouseEvent e) {
                b.setBackground(new Color(
                    Math.min(bg.getRed() + 20, 255),
                    Math.min(bg.getGreen() + 20, 255),
                    Math.min(bg.getBlue() + 20, 255)
                ));
            }
            @Override public void mouseExited(java.awt.event.MouseEvent e) {
                b.setBackground(bg);
            }
        });
        return b;
    }

    /**
     * Crea un botón inline de tabla (Editar / Eliminar) con borde de color y hover de relleno.
     */
    public static JButton crearBotonInline(String texto, Color color) {
        JButton b = new JButton(texto);
        b.setFont(new Font("Segoe UI", Font.BOLD, 11));
        b.setForeground(color);
        b.setBackground(C_WHITE);
        b.setBorder(BorderFactory.createLineBorder(color, 1));
        b.setFocusPainted(false);
        b.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        b.setPreferredSize(new Dimension(62, 26));
        b.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override public void mouseEntered(java.awt.event.MouseEvent e) {
                b.setBackground(color);
                b.setForeground(C_WHITE);
            }
            @Override public void mouseExited(java.awt.event.MouseEvent e) {
                b.setBackground(C_WHITE);
                b.setForeground(color);
            }
        });
        return b;
    }

    /**
     * Crea un botón inline de tabla con ancho personalizado.
     */
    public static JButton crearBotonInline(String texto, Color color, int ancho) {
        JButton b = crearBotonInline(texto, color);
        b.setPreferredSize(new Dimension(ancho, 26));
        return b;
    }

    /**
     * Crea el botón de refrescar estándar con hover suave.
     */
    public static JButton crearBotonRefrescar() {
        JButton b = new JButton("↻ Refrescar");
        b.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        b.setForeground(C_PURPLE);
        b.setBackground(C_WHITE);
        b.setBorder(BorderFactory.createLineBorder(C_BORDER, 1));
        b.setFocusPainted(false);
        b.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        b.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override public void mouseEntered(java.awt.event.MouseEvent e) {
                b.setBackground(C_ROW_ALT);
            }
            @Override public void mouseExited(java.awt.event.MouseEvent e) {
                b.setBackground(C_WHITE);
            }
        });
        return b;
    }

    // ── CAMPOS ────────────────────────────────────────────────────────────────

    /**
     * Crea un campo de búsqueda con placeholder, borde animado y filtrado automático
     * sobre el {@link TableRowSorter} proporcionado.
     */
    public static JTextField crearCampoBusqueda(String placeholder, TableRowSorter<DefaultTableModel> sorter) {
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
        campo.setBackground(C_BG);
        campo.setForeground(C_TEXT);
        campo.setCaretColor(C_PURPLE);
        campo.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(C_BORDER, 1),
            new EmptyBorder(4, 10, 4, 10)
        ));
        campo.setPreferredSize(new Dimension(240, 32));
        campo.addFocusListener(new java.awt.event.FocusAdapter() {
            @Override public void focusGained(java.awt.event.FocusEvent e) {
                campo.setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createLineBorder(C_PURPLE, 1),
                    new EmptyBorder(4, 10, 4, 10)
                ));
            }
            @Override public void focusLost(java.awt.event.FocusEvent e) {
                campo.setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createLineBorder(C_BORDER, 1),
                    new EmptyBorder(4, 10, 4, 10)
                ));
            }
        });
        campo.getDocument().addDocumentListener(new DocumentListener() {
            @Override public void insertUpdate(DocumentEvent e)  { filtrar(campo.getText(), sorter); }
            @Override public void removeUpdate(DocumentEvent e)  { filtrar(campo.getText(), sorter); }
            @Override public void changedUpdate(DocumentEvent e) { filtrar(campo.getText(), sorter); }
        });
        return campo;
    }

    /**
     * Aplica o elimina el filtro sobre un {@link TableRowSorter}.
     */
    public static void filtrar(String texto, TableRowSorter<DefaultTableModel> sorter) {
        if (texto == null || texto.trim().isEmpty()) {
            sorter.setRowFilter(null);
        } else {
            sorter.setRowFilter(RowFilter.regexFilter("(?i)" + texto.trim()));
        }
    }

    // ── TABLA ─────────────────────────────────────────────────────────────────

    /**
     * Aplica el estilo estándar a una JTable: fuente, altura de fila, colores,
     * renderer de filas alternas y configuración del header.
     * También inicializa y asigna el {@link TableRowSorter}.
     *
     * @return el sorter creado para poder asignarlo al campo de búsqueda
     */
    public static TableRowSorter<DefaultTableModel> estilizarTabla(JTable tabla, DefaultTableModel modelo) {
        TableRowSorter<DefaultTableModel> sorter = new TableRowSorter<>(modelo);
        tabla.setRowSorter(sorter);

        tabla.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        tabla.setRowHeight(40);
        tabla.setGridColor(C_BORDER);
        tabla.setBackground(C_WHITE);
        tabla.setSelectionBackground(C_SEL_BG);
        tabla.setSelectionForeground(C_TEXT);
        tabla.setShowVerticalLines(false);
        tabla.setFillsViewportHeight(true);
        tabla.setIntercellSpacing(new Dimension(0, 0));

        JTableHeader header = tabla.getTableHeader();
        header.setFont(new Font("Consolas", Font.BOLD, 11));
        header.setBackground(C_ROW_ALT);
        header.setForeground(C_PURPLE);
        header.setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, C_BORDER));
        header.setPreferredSize(new Dimension(0, 36));

        tabla.setDefaultRenderer(Object.class, new DefaultTableCellRenderer() {
            @Override public Component getTableCellRendererComponent(JTable t, Object v,
                    boolean sel, boolean foc, int r, int c) {
                super.getTableCellRendererComponent(t, v, sel, foc, r, c);
                if (sel) setBackground(C_SEL_BG);
                else if (r % 2 == 0) setBackground(C_WHITE);
                else setBackground(C_ROW_ALT);
                setBorder(new EmptyBorder(0, 8, 0, 8));
                return this;
            }
        });

        return sorter;
    }

    /**
     * Crea un JScrollPane estilizado sin borde para envolver una tabla.
     */
    public static JScrollPane crearScrollTabla(JTable tabla) {
        JScrollPane scroll = new JScrollPane(tabla);
        scroll.setBorder(BorderFactory.createEmptyBorder());
        scroll.getViewport().setBackground(C_WHITE);
        return scroll;
    }

    // ── TOPBAR ────────────────────────────────────────────────────────────────

    /**
     * Crea el panel topbar morado con gradiente y línea naranja-azul inferior,
     * con logo y título a la izquierda y un panel derecho para botones de acción.
     *
     * @param titulo      texto del título
     * @param panelDerecha panel con botones de acción (puede ser vacío)
     * @return el topbar construido
     */
    public static JPanel construirTopbar(String titulo, JPanel panelDerecha) {
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
            java.net.URL imgURL = GUIUtils.class.getClassLoader().getResource("resources/logo2.png");
            if (imgURL != null) {
                Image scaled = new ImageIcon(imgURL).getImage().getScaledInstance(44, 44, Image.SCALE_SMOOTH);
                labelLogo.setIcon(new ImageIcon(scaled));
            }
        } catch (Exception ex) { /* sin logo */ }

        JLabel lblTitulo = new JLabel(titulo);
        lblTitulo.setFont(new Font("Segoe UI", Font.BOLD, 20));
        lblTitulo.setForeground(C_WHITE);

        izquierda.add(labelLogo);
        izquierda.add(lblTitulo);

        bar.add(izquierda, BorderLayout.WEST);
        if (panelDerecha != null) bar.add(panelDerecha, BorderLayout.EAST);
        return bar;
    }

    /**
     * Crea el panel derecho del topbar con el botón de añadir en naranja.
     */
    public static JPanel panelAccionTopbar(JButton boton) {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 14));
        panel.setOpaque(false);
        if (boton != null) panel.add(boton);
        return panel;
    }

    // ── TARJETA ESTADÍSTICA ───────────────────────────────────────────────────

    /**
     * Crea una tarjeta de estadística con título, valor destacado y subtexto.
     */
    public static JPanel crearTarjeta(String titulo, String valor, String sub, Color acento) {
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

    // ── FORMULARIO ────────────────────────────────────────────────────────────

    /**
     * Construye un panel de formulario con pares etiqueta-campo usando GridBagLayout.
     */
    public static JPanel construirForm(String[] labels, JComponent[] campos) {
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

    // ── CABECERA DE TABLA ─────────────────────────────────────────────────────

    /**
     * Construye la cabecera estándar de un panel de tabla con título,
     * campo de búsqueda y botón de refrescar.
     *
     * @param titulo      texto del título de la sección
     * @param placeholder texto de ayuda del campo de búsqueda
     * @param sorter      sorter de la tabla para conectar el filtro
     * @param onRefrescar acción a ejecutar al pulsar refrescar
     * @return el panel cabecera construido
     */
    public static JPanel construirCabeceraTabla(String titulo,
            String placeholder,
            TableRowSorter<DefaultTableModel> sorter,
            Runnable onRefrescar) {

        JPanel cabecera = new JPanel(new BorderLayout(8, 0));
        cabecera.setBackground(C_WHITE);
        cabecera.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createMatteBorder(0, 0, 1, 0, C_BORDER),
            new EmptyBorder(10, 16, 10, 16)
        ));

        JLabel lblTabla = new JLabel(titulo);
        lblTabla.setFont(new Font("Segoe UI", Font.BOLD, 14));
        lblTabla.setForeground(C_TEXT);

        JTextField campoBusqueda = crearCampoBusqueda(placeholder, sorter);

        JButton btnRefrescar = crearBotonRefrescar();
        btnRefrescar.addActionListener(e -> {
            campoBusqueda.setText("");
            onRefrescar.run();
        });

        JPanel derechaBar = new JPanel(new FlowLayout(FlowLayout.RIGHT, 6, 0));
        derechaBar.setBackground(C_WHITE);
        derechaBar.add(btnRefrescar);

        cabecera.add(lblTabla, BorderLayout.WEST);
        cabecera.add(campoBusqueda, BorderLayout.CENTER);
        cabecera.add(derechaBar, BorderLayout.EAST);
        return cabecera;
    }

    // ── DIÁLOGO CONFIRMAR SALIDA ──────────────────────────────────────────────

    /**
     * Muestra un diálogo de confirmación antes de cerrar la aplicación.
     * Llama a System.exit(0) si el usuario confirma.
     */
    public static void confirmarSalida(Component parent) {
        int resp = JOptionPane.showConfirmDialog(
            parent,
            "¿Seguro que quieres salir de LevelUp Arcade?",
            "Confirmar salida",
            JOptionPane.YES_NO_OPTION,
            JOptionPane.QUESTION_MESSAGE
        );
        if (resp == JOptionPane.YES_OPTION) System.exit(0);
    }

    // ── MENSAJES ──────────────────────────────────────────────────────────────

    /** Muestra un diálogo de éxito. */
    public static void exito(Component parent, String msg) {
        JOptionPane.showMessageDialog(parent, msg, "Éxito", JOptionPane.INFORMATION_MESSAGE);
    }

    /** Muestra un diálogo de error. */
    public static void error(Component parent, String msg) {
        JOptionPane.showMessageDialog(parent, msg, "Error", JOptionPane.ERROR_MESSAGE);
    }

    /** Muestra un label de "sin resultados" centrado en un panel. */
    public static JPanel panelSinResultados() {
        JPanel p = new JPanel(new GridBagLayout());
        p.setBackground(C_WHITE);
        JLabel lbl = new JLabel("No se encontraron resultados");
        lbl.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        lbl.setForeground(C_MUTED);
        p.add(lbl);
        return p;
    }
}
