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
 * <p>
 * Esta clase no es instanciable; todos sus miembros son estáticos.
 * </p>
 */
public final class GUIUtils {

    // ── PALETA DE COLORES ─────────────────────────────────────────────────────
    /** Color de fondo general de la aplicación (lila muy claro). */
    public static final Color C_BG       = new Color(248, 247, 255);
    /** Color morado principal de la marca. */
    public static final Color C_PURPLE   = new Color(92, 51, 181);
    /** Color morado claro (usado en el sidebar y acentos secundarios). */
    public static final Color C_PURPLE_L = new Color(196, 181, 253);
    /** Color naranja de acento para botones de acción y alertas. */
    public static final Color C_ORANGE   = new Color(249, 115, 22);
    /** Color azul para acciones secundarias y etiquetas de empleado. */
    public static final Color C_BLUE     = new Color(59, 130, 246);
    /** Color rojo para acciones destructivas y mensajes de error. */
    public static final Color C_RED      = new Color(239, 68, 68);
    /** Color verde para estados positivos (stock disponible, entregado). */
    public static final Color C_GREEN    = new Color(34, 197, 94);
    /** Blanco puro, usado como fondo de tarjetas y tablas. */
    public static final Color C_WHITE    = Color.WHITE;
    /** Color de texto principal (casi negro con tinte morado). */
    public static final Color C_TEXT     = new Color(26, 18, 37);
    /** Color de texto secundario/desactivado (gris neutro). */
    public static final Color C_MUTED    = new Color(107, 114, 128);
    /** Color de borde de paneles y campos (lila suave). */
    public static final Color C_BORDER   = new Color(229, 224, 248);
    /** Color de fondo del header oscuro de la topbar. */
    public static final Color C_HEADER   = new Color(45, 21, 114);
    /** Color de fondo de los campos de texto. */
    public static final Color C_FIELD_BG = new Color(252, 251, 255);
    /** Color de fondo de filas alternas de la tabla. */
    public static final Color C_ROW_ALT  = new Color(245, 243, 255);
    /** Color de fondo de la fila seleccionada en la tabla. */
    public static final Color C_SEL_BG   = new Color(92, 51, 181, 30);
    /** Color de fondo al pasar el ratón sobre un elemento interactivo. */
    public static final Color C_HOVER_BG = new Color(92, 51, 181, 50);

    private GUIUtils() { /* no instanciable */ }

    // ── BOTONES ───────────────────────────────────────────────────────────────

    /**
     * Crea un botón principal de acción con color de fondo y hover suave.
     * Al pasar el ratón por encima, el color de fondo se aclara ligeramente
     * sumando 20 a cada componente RGB.
     *
     * @param texto el texto visible del botón
     * @param bg    el color de fondo base
     * @param fg    el color del texto
     * @return el botón creado y configurado
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
                    Math.min(bg.getRed()   + 20, 255),
                    Math.min(bg.getGreen() + 20, 255),
                    Math.min(bg.getBlue()  + 20, 255)
                ));
            }
            @Override public void mouseExited(java.awt.event.MouseEvent e) {
                b.setBackground(bg);
            }
        });
        return b;
    }

    /**
     * Crea un botón inline de tabla con borde de color y hover de relleno.
     * En reposo tiene fondo blanco y texto del color indicado; al pasar el
     * ratón invierte los colores (fondo de color, texto blanco).
     *
     * @param texto el texto visible del botón
     * @param color el color del borde y del texto en reposo
     * @return el botón inline creado (62 px de ancho por defecto)
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
                b.setBackground(color); b.setForeground(C_WHITE);
            }
            @Override public void mouseExited(java.awt.event.MouseEvent e) {
                b.setBackground(C_WHITE); b.setForeground(color);
            }
        });
        return b;
    }

    /**
     * Crea un botón inline con ancho personalizado.
     * Delega en {@link #crearBotonInline(String, Color)} y sobreescribe el ancho.
     *
     * @param texto  el texto visible del botón
     * @param color  el color del borde y del texto en reposo
     * @param ancho  el ancho preferido en píxeles
     * @return el botón inline creado con el ancho indicado
     */
    public static JButton crearBotonInline(String texto, Color color, int ancho) {
        JButton b = crearBotonInline(texto, color);
        b.setPreferredSize(new Dimension(ancho, 26));
        return b;
    }

    /**
     * Crea el botón de refrescar estándar con hover suave.
     * Muestra el símbolo de recarga (↻) y el texto "Refrescar".
     *
     * @return el botón de refrescar creado y configurado
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
            @Override public void mouseEntered(java.awt.event.MouseEvent e) { b.setBackground(C_ROW_ALT); }
            @Override public void mouseExited(java.awt.event.MouseEvent e)  { b.setBackground(C_WHITE); }
        });
        return b;
    }

    // ── CAMPOS ────────────────────────────────────────────────────────────────

    /**
     * Crea un campo de búsqueda con placeholder, borde animado y filtrado automático
     * sobre el {@link TableRowSorter} proporcionado.
     * <p>
     * El placeholder se dibuja en cursiva cuando el campo está vacío y sin foco.
     * Al ganar el foco el borde cambia a morado; al perderlo vuelve al borde gris.
     * Cada cambio en el texto actualiza el filtro del sorter en tiempo real mediante
     * un {@link DocumentListener}.
     * </p>
     *
     * @param placeholder texto de ayuda que se muestra cuando el campo está vacío
     * @param sorter      sorter de la tabla al que se conecta el filtro;
     *                    puede ser {@code null} para deshabilitar el filtrado
     * @return el campo de búsqueda creado y configurado
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
        if (sorter != null) {
            campo.getDocument().addDocumentListener(new DocumentListener() {
                @Override public void insertUpdate(DocumentEvent e)  { filtrar(campo.getText(), sorter); }
                @Override public void removeUpdate(DocumentEvent e)  { filtrar(campo.getText(), sorter); }
                @Override public void changedUpdate(DocumentEvent e) { filtrar(campo.getText(), sorter); }
            });
        }
        return campo;
    }

    /**
     * Aplica o elimina el filtro de expresión regular sobre un {@link TableRowSorter}.
     * La búsqueda es insensible a mayúsculas/minúsculas (prefijo {@code (?i)}).
     * Si el texto está vacío o es solo espacios, elimina el filtro mostrando todas las filas.
     *
     * @param texto  el texto introducido por el usuario
     * @param sorter el sorter de la tabla sobre el que aplicar el filtro
     */
    public static void filtrar(String texto, TableRowSorter<DefaultTableModel> sorter) {
        if (texto == null || texto.trim().isEmpty()) sorter.setRowFilter(null);
        else sorter.setRowFilter(RowFilter.regexFilter("(?i)" + texto.trim()));
    }

    // ── TABLA ─────────────────────────────────────────────────────────────────

    /**
     * Aplica el estilo estándar a una {@link JTable}: fuente, altura de fila,
     * color de cuadrícula, fondo, selección, cabecera y renderer de filas alternas.
     * También crea y asigna un {@link TableRowSorter} a la tabla.
     *
     * @param tabla  la tabla a estilizar
     * @param modelo el modelo de datos de la tabla
     * @return el sorter creado, ya asignado a la tabla
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
     * Crea un {@link JScrollPane} estilizado sin borde para envolver una tabla.
     * El viewport tiene fondo blanco para evitar huecos de color en tablas cortas.
     *
     * @param tabla la tabla a envolver en el scroll
     * @return el scroll pane configurado
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
     * con logo y título a la izquierda y un panel derecho opcional para botones.
     * El logo se carga desde {@code resources/logo2.png} en el classpath.
     *
     * @param titulo       texto del título de la sección
     * @param panelDerecha panel con botones de acción; puede ser {@code null}
     *                     si no se necesitan botones en este topbar
     * @return el topbar construido listo para añadir al layout
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
     * Crea el panel derecho del topbar para alojar botones de acción.
     * Si el botón es {@code null} devuelve un panel vacío (para mantener
     * el layout consistente).
     *
     * @param boton el botón de acción principal; puede ser {@code null}
     * @return el panel derecho con el botón añadido (o vacío)
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
     * El valor se muestra en grande con el color de acento indicado.
     *
     * @param titulo  texto descriptivo de la estadística (parte superior)
     * @param valor   valor numérico o textual principal
     * @param sub     subtexto explicativo bajo el valor
     * @param acento  color aplicado al valor principal
     * @return el panel de tarjeta construido
     */
    public static JPanel crearTarjeta(String titulo, String valor, String sub, Color acento) {
        JPanel card = new JPanel(new BorderLayout());
        card.setBackground(C_WHITE);
        card.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(C_BORDER, 1), new EmptyBorder(14, 18, 14, 18)));
        JLabel lblTitulo = new JLabel(titulo);
        lblTitulo.setFont(new Font("Segoe UI", Font.PLAIN, 12)); lblTitulo.setForeground(C_MUTED);
        JLabel lblValor  = new JLabel(valor);
        lblValor.setFont(new Font("Segoe UI", Font.BOLD, 26));   lblValor.setForeground(acento);
        JLabel lblSub    = new JLabel(sub);
        lblSub.setFont(new Font("Segoe UI", Font.PLAIN, 11));    lblSub.setForeground(C_MUTED);
        JPanel centro = new JPanel();
        centro.setLayout(new BoxLayout(centro, BoxLayout.Y_AXIS)); centro.setBackground(C_WHITE);
        centro.add(lblValor); centro.add(Box.createVerticalStrut(2)); centro.add(lblSub);
        card.add(lblTitulo, BorderLayout.NORTH); card.add(centro, BorderLayout.CENTER);
        return card;
    }

    // ── FORMULARIO ────────────────────────────────────────────────────────────

    /**
     * Construye un panel de formulario con pares etiqueta-campo usando {@link GridBagLayout}.
     * Cada etiqueta se muestra en {@code Consolas} negrita y color morado; cada campo
     * ocupa el espacio restante en la fila.
     *
     * @param labels  array de textos de etiqueta; debe tener la misma longitud que {@code campos}
     * @param campos  array de componentes de entrada (TextField, ComboBox, etc.)
     * @return el panel de formulario construido
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
            lbl.setFont(new Font("Consolas", Font.BOLD, 11)); lbl.setForeground(C_PURPLE);
            p.add(lbl, g);
            g.gridx = 1; g.weightx = 1;
            campos[i].setPreferredSize(new Dimension(260, 32));
            p.add(campos[i], g);
        }
        return p;
    }

    // ── CABECERA DE TABLA ─────────────────────────────────────────────────────

    /**
     * Construye la cabecera estándar de tabla con título, campo de búsqueda
     * y botón de refrescar. El campo de búsqueda filtra automáticamente el sorter.
     * Al pulsar refrescar se borra el texto de búsqueda y se ejecuta {@code onRefrescar}.
     *
     * @param titulo      texto del título de la sección de tabla
     * @param placeholder texto de ayuda del campo de búsqueda
     * @param sorter      sorter de la tabla para conectar el filtro
     * @param onRefrescar acción a ejecutar al pulsar el botón de refrescar
     * @return el panel cabecera construido
     */
    public static JPanel construirCabeceraTabla(String titulo,
            String placeholder,
            TableRowSorter<DefaultTableModel> sorter,
            Runnable onRefrescar) {

        JTextField campoBusqueda = crearCampoBusqueda(placeholder, sorter);
        JButton btnRefrescar = crearBotonRefrescar();
        btnRefrescar.addActionListener(e -> { campoBusqueda.setText(""); onRefrescar.run(); });

        JPanel derechaBar = new JPanel(new FlowLayout(FlowLayout.RIGHT, 6, 0));
        derechaBar.setBackground(C_WHITE);
        derechaBar.add(btnRefrescar);

        return construirCabeceraTabla(titulo, campoBusqueda, derechaBar);
    }

    /**
     * Construye la cabecera de tabla con título, campo de búsqueda y panel derecho
     * personalizado. Úsalo cuando necesites añadir botones extra además del refrescar.
     *
     * @param titulo        texto del título de la sección de tabla
     * @param campoBusqueda campo de búsqueda ya configurado
     * @param derechaBar    panel derecho con los botones de acción
     * @return el panel cabecera construido
     */
    public static JPanel construirCabeceraTabla(String titulo,
            JTextField campoBusqueda,
            JPanel derechaBar) {

        JPanel cabecera = new JPanel(new BorderLayout(8, 0));
        cabecera.setBackground(C_WHITE);
        cabecera.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createMatteBorder(0, 0, 1, 0, C_BORDER),
            new EmptyBorder(10, 16, 10, 16)
        ));
        JLabel lblTabla = new JLabel(titulo);
        lblTabla.setFont(new Font("Segoe UI", Font.BOLD, 14)); lblTabla.setForeground(C_TEXT);
        cabecera.add(lblTabla,      BorderLayout.WEST);
        cabecera.add(campoBusqueda, BorderLayout.CENTER);
        cabecera.add(derechaBar,    BorderLayout.EAST);
        return cabecera;
    }

    // ── DIÁLOGO CONFIRMAR SALIDA ──────────────────────────────────────────────

    /**
     * Muestra un diálogo de confirmación antes de cerrar la aplicación.
     * Si el usuario confirma, llama a {@link System#exit(int)} con código 0.
     *
     * @param parent el componente padre del diálogo (para centrarlo)
     */
    public static void confirmarSalida(Component parent) {
        int resp = JOptionPane.showConfirmDialog(parent,
            "¿Seguro que quieres salir de LevelUp Arcade?",
            "Confirmar salida", JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE);
        if (resp == JOptionPane.YES_OPTION) System.exit(0);
    }

    // ── MENSAJES ──────────────────────────────────────────────────────────────

    /**
     * Muestra un diálogo de éxito con el mensaje indicado.
     *
     * @param parent el componente padre del diálogo
     * @param msg    el mensaje de éxito a mostrar
     */
    public static void exito(Component parent, String msg) {
        JOptionPane.showMessageDialog(parent, msg, "Éxito", JOptionPane.INFORMATION_MESSAGE);
    }

    /**
     * Muestra un diálogo de error con el mensaje indicado.
     *
     * @param parent el componente padre del diálogo
     * @param msg    el mensaje de error a mostrar
     */
    public static void error(Component parent, String msg) {
        JOptionPane.showMessageDialog(parent, msg, "Error", JOptionPane.ERROR_MESSAGE);
    }

    /**
     * Crea un panel con una etiqueta de "sin resultados" centrada,
     * útil para mostrar cuando una búsqueda no devuelve datos.
     *
     * @return el panel con la etiqueta de sin resultados
     */
    public static JPanel panelSinResultados() {
        JPanel p = new JPanel(new GridBagLayout());
        p.setBackground(C_WHITE);
        JLabel lbl = new JLabel("No se encontraron resultados");
        lbl.setFont(new Font("Segoe UI", Font.PLAIN, 13)); lbl.setForeground(C_MUTED);
        p.add(lbl);
        return p;
    }
}
