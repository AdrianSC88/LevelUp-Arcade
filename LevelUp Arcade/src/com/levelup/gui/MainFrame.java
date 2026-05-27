package com.levelup.gui;

import com.levelup.gui.paneles.*;
import com.levelup.modelo.Usuario;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;

/**
 * Ventana principal de la aplicación tras el login.
 * <p>
 * Contiene el sidebar de navegación lateral, la barra superior con
 * el breadcrumb y la etiqueta de rol, el área de contenido central
 * donde se cargan los paneles de cada sección, y el pie de página.
 * La navegación y los paneles disponibles se adaptan al rol del usuario:
 * el panel de usuarios solo es accesible para administradores.
 * </p>
 */
public class MainFrame extends JFrame {

    /** Anchura fija en píxeles del sidebar de navegación lateral. */
    private static final int SIDEBAR_W = 220;

    private final Usuario usuarioActivo;
    private JPanel areaContenido;
    private JLabel labelSeccion;

    /**
     * Construye la ventana principal para el usuario autenticado.
     * Inicializa la ventana, construye la interfaz y navega al panel
     * de productos como sección de inicio.
     *
     * @param usuarioActivo el usuario que ha iniciado sesión
     */
    public MainFrame(Usuario usuarioActivo) {
        this.usuarioActivo = usuarioActivo;
        configurarVentana();
        construirUI();
        navegarA("productos");
    }

    /**
     * Configura las propiedades de la ventana: título con nombre y rol del
     * usuario, tamaño, tamaño mínimo, posición centrada y diálogo de
     * confirmación antes de cerrar.
     */
    private void configurarVentana() {
        setTitle("LevelUp Arcade — " + usuarioActivo.getNombre() + " [" + usuarioActivo.getRol() + "]");
        // Confirmación antes de cerrar
        setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
        addWindowListener(new java.awt.event.WindowAdapter() {
            @Override public void windowClosing(java.awt.event.WindowEvent e) {
                GUIUtils.confirmarSalida(MainFrame.this);
            }
        });
        setSize(1100, 700);
        setMinimumSize(new Dimension(900, 600));
        setLocationRelativeTo(null);
        getContentPane().setBackground(GUIUtils.C_BG);
    }

    /**
     * Ensambla los cuatro bloques de la ventana en su layout {@link BorderLayout}:
     * sidebar (oeste), topbar (norte), área de contenido (centro) y pie (sur).
     */
    private void construirUI() {
        setLayout(new BorderLayout());
        add(construirSidebar(),       BorderLayout.WEST);
        add(construirTopbar(),        BorderLayout.NORTH);
        add(construirAreaContenido(), BorderLayout.CENTER);
        add(construirFooter(),        BorderLayout.SOUTH);
    }

    /**
     * Construye el sidebar de navegación oscuro con la sección de marca,
     * los botones de navegación agrupados por categoría, el botón de
     * cerrar sesión al final y separadores visuales entre secciones.
     * El botón de usuarios solo se añade si el usuario es administrador.
     *
     * @return el panel sidebar listo para añadir al layout
     */
    private JPanel construirSidebar() {
        JPanel sidebar = new JPanel();
        sidebar.setBackground(GUIUtils.C_PURPLE_L.darker().darker().darker()); // C_SIDEBAR
        sidebar.setBackground(new Color(15, 10, 30));
        sidebar.setPreferredSize(new Dimension(SIDEBAR_W, 0));
        sidebar.setLayout(new BoxLayout(sidebar, BoxLayout.Y_AXIS));
        sidebar.setBorder(BorderFactory.createMatteBorder(0, 0, 0, 1, new Color(196, 181, 253, 25)));

        // Brand
        JPanel brand = new JPanel();
        brand.setBackground(new Color(15, 10, 30));
        brand.setLayout(new BoxLayout(brand, BoxLayout.Y_AXIS));
        brand.setBorder(new EmptyBorder(24, 20, 16, 20));
        brand.setMaximumSize(new Dimension(Integer.MAX_VALUE, 90));

        JLabel tagLabel = new JLabel("PRÁCTICAS 1.º DAM");
        tagLabel.setFont(new Font("Consolas", Font.PLAIN, 11));
        tagLabel.setForeground(GUIUtils.C_ORANGE);
        tagLabel.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel titulo = new JLabel("LevelUp Arcade");
        titulo.setFont(new Font("Segoe UI", Font.BOLD, 16));
        titulo.setForeground(Color.WHITE);
        titulo.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel subtitulo = new JLabel(usuarioActivo.getNombre());
        subtitulo.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        subtitulo.setForeground(new Color(196, 181, 253, 130));
        subtitulo.setAlignmentX(Component.LEFT_ALIGNMENT);

        brand.add(tagLabel);
        brand.add(Box.createVerticalStrut(4));
        brand.add(titulo);
        brand.add(Box.createVerticalStrut(2));
        brand.add(subtitulo);

        sidebar.add(brand);
        sidebar.add(crearSeparador());

        sidebar.add(crearNavLabel("GESTIÓN"));
        sidebar.add(crearNavBoton("▪  Productos",   "productos"));
        sidebar.add(crearNavBoton("▪  Clientes",    "clientes"));
        sidebar.add(crearNavBoton("▪  Proveedores", "proveedores"));
        sidebar.add(crearNavBoton("▪  Categorías",  "categorias"));
        sidebar.add(crearNavBoton("▪  Pedidos",     "pedidos"));

        sidebar.add(crearNavLabel("SISTEMA"));
        if ("administrador".equals(usuarioActivo.getRol())) {
            sidebar.add(crearNavBoton("▪  Usuarios", "usuarios"));
        }
        sidebar.add(crearNavBoton("▪  Asistente IA", "ia"));

        sidebar.add(Box.createVerticalGlue());
        sidebar.add(crearSeparador());

        // Botón cerrar sesión
        JButton cerrarSesion = new JButton("▪  Cerrar sesión") {
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                if (getModel().isRollover()) {
                    g2.setColor(GUIUtils.C_HOVER_BG);
                    g2.fillRect(0, 0, getWidth(), getHeight());
                }
                g2.dispose();
                super.paintComponent(g);
            }
        };
        cerrarSesion.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        cerrarSesion.setForeground(GUIUtils.C_RED);
        cerrarSesion.setBackground(new Color(15, 10, 30));
        cerrarSesion.setBorderPainted(false);
        cerrarSesion.setFocusPainted(false);
        cerrarSesion.setContentAreaFilled(false);
        cerrarSesion.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        cerrarSesion.setHorizontalAlignment(SwingConstants.LEFT);
        cerrarSesion.setBorder(new EmptyBorder(12, 20, 16, 20));
        cerrarSesion.setMaximumSize(new Dimension(Integer.MAX_VALUE, 44));
        cerrarSesion.addActionListener(e -> {
            dispose();
            SwingUtilities.invokeLater(() -> new LoginFrame().setVisible(true));
        });
        sidebar.add(cerrarSesion);
        return sidebar;
    }

    /**
     * Crea un botón de navegación del sidebar que, al pulsarse, llama a
     * {@link #navegarA(String)} con la sección indicada. Muestra un fondo
     * semitransparente al pasar el ratón por encima.
     *
     * @param texto   texto visible del botón
     * @param seccion identificador de la sección destino
     * @return el botón de navegación configurado
     */
    private JButton crearNavBoton(String texto, String seccion) {
        JButton boton = new JButton(texto) {
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                if (getModel().isRollover()) {
                    g2.setColor(GUIUtils.C_HOVER_BG);
                    g2.fillRect(0, 0, getWidth(), getHeight());
                }
                g2.dispose();
                super.paintComponent(g);
            }
        };
        boton.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        boton.setForeground(new Color(255, 255, 255, 160));
        boton.setBackground(new Color(15, 10, 30));
        boton.setBorderPainted(false);
        boton.setFocusPainted(false);
        boton.setContentAreaFilled(false);
        boton.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        boton.setHorizontalAlignment(SwingConstants.LEFT);
        boton.setBorder(new EmptyBorder(9, 20, 9, 20));
        boton.setMaximumSize(new Dimension(Integer.MAX_VALUE, 40));
        boton.addActionListener(e -> navegarA(seccion));
        return boton;
    }

    /**
     * Crea una etiqueta de sección del sidebar (p. ej. "GESTIÓN" o "SISTEMA")
     * con estilo tipográfico en mayúsculas y color violáceo semitransparente.
     *
     * @param texto el texto de la etiqueta de sección
     * @return la etiqueta configurada
     */
    private JLabel crearNavLabel(String texto) {
        JLabel label = new JLabel(texto);
        label.setFont(new Font("Consolas", Font.BOLD, 11));
        label.setForeground(new Color(196, 181, 253, 160));
        label.setBorder(new EmptyBorder(14, 20, 4, 20));
        label.setMaximumSize(new Dimension(Integer.MAX_VALUE, 28));
        return label;
    }

    /**
     * Crea un separador horizontal de 1 px de alto para el sidebar,
     * con color violáceo semitransparente.
     *
     * @return el panel separador
     */
    private JPanel crearSeparador() {
        JPanel sep = new JPanel();
        sep.setBackground(new Color(196, 181, 253, 25));
        sep.setMaximumSize(new Dimension(Integer.MAX_VALUE, 1));
        sep.setPreferredSize(new Dimension(0, 1));
        return sep;
    }

    /**
     * Construye la barra superior con el breadcrumb de sección a la izquierda
     * y la etiqueta de rol del usuario (con color diferenciado según si es
     * administrador o empleado) a la derecha.
     *
     * @return el panel topbar listo para añadir al layout
     */
    private JPanel construirTopbar() {
        JPanel topbar = new JPanel(new BorderLayout());
        topbar.setBackground(Color.WHITE);
        topbar.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createMatteBorder(0, 0, 1, 0, GUIUtils.C_BORDER),
            new EmptyBorder(10, 24, 10, 24)
        ));
        topbar.setPreferredSize(new Dimension(0, 46));

        labelSeccion = new JLabel("Inicio");
        labelSeccion.setFont(new Font("Segoe UI", Font.BOLD, 13));
        labelSeccion.setForeground(GUIUtils.C_PURPLE);

        JLabel rolTag = new JLabel(usuarioActivo.getRol().toUpperCase());
        rolTag.setFont(new Font("Consolas", Font.BOLD, 10));
        rolTag.setForeground(Color.WHITE);
        rolTag.setBackground("administrador".equals(usuarioActivo.getRol()) ? GUIUtils.C_PURPLE : GUIUtils.C_BLUE);
        rolTag.setOpaque(true);
        rolTag.setBorder(new EmptyBorder(3, 8, 3, 8));

        topbar.add(labelSeccion, BorderLayout.WEST);
        topbar.add(rolTag, BorderLayout.EAST);
        return topbar;
    }

    /**
     * Construye el panel central vacío donde se irán cargando los paneles
     * de cada sección al navegar.
     *
     * @return el panel de contenido inicializado
     */
    private JPanel construirAreaContenido() {
        areaContenido = new JPanel(new BorderLayout());
        areaContenido.setBackground(GUIUtils.C_BG);
        return areaContenido;
    }

    /**
     * Construye el pie de página con el nombre del autor, curso y año académico.
     *
     * @return la etiqueta de pie de página configurada
     */
    private JLabel construirFooter() {
        JLabel footer = new JLabel(
            "Adrián Sánchez Cañadas · 1.º DAM · CampusFP · Curso 2025–2026",
            SwingConstants.CENTER
        );
        footer.setFont(new Font("Consolas", Font.PLAIN, 10));
        footer.setForeground(GUIUtils.C_MUTED);
        footer.setBackground(Color.WHITE);
        footer.setOpaque(true);
        footer.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createMatteBorder(1, 0, 0, 0, GUIUtils.C_BORDER),
            new EmptyBorder(6, 0, 6, 0)
        ));
        return footer;
    }

    /**
     * Navega al panel indicado actualizando el área de contenido y el breadcrumb.
     * Elimina el panel anterior, instancia el nuevo según el identificador de
     * sección y actualiza el texto del label de navegación superior.
     *
     * @param seccion identificador de la sección destino (p. ej. {@code "productos"},
     *                {@code "clientes"}, {@code "ia"}, etc.)
     */
    public void navegarA(String seccion) {
        areaContenido.removeAll();

        JPanel panel = switch (seccion) {
            case "productos"   -> new ProductosPanel(usuarioActivo);
            case "clientes"    -> new ClientesPanel(usuarioActivo);
            case "proveedores" -> new ProveedoresPanel(usuarioActivo);
            case "categorias"  -> new CategoriasPanel(usuarioActivo);
            case "pedidos"     -> new PedidosPanel(usuarioActivo);
            case "ia"          -> new IAPanel(usuarioActivo);
            case "usuarios"    -> new UsuariosPanel(usuarioActivo);
            default            -> new ProductosPanel(usuarioActivo);
        };

        String nombre = switch (seccion) {
            case "productos"   -> "Gestión de Productos";
            case "clientes"    -> "Gestión de Clientes";
            case "proveedores" -> "Gestión de Proveedores";
            case "categorias"  -> "Gestión de Categorías";
            case "pedidos"     -> "Gestión de Pedidos";
            case "ia"          -> "Asistente de Inteligencia Artificial";
            case "usuarios"    -> "Gestión de Usuarios";
            default            -> "Inicio";
        };

        labelSeccion.setText("LevelUp Arcade › " + nombre);
        areaContenido.add(panel, BorderLayout.CENTER);
        areaContenido.revalidate();
        areaContenido.repaint();
    }
}
