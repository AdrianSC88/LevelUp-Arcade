package com.levelup.gui;

import com.levelup.gui.paneles.*;
import com.levelup.modelo.Usuario;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;

public class MainFrame extends JFrame {

    private static final Color C_BG        = new Color(248, 247, 255);
    private static final Color C_SIDEBAR   = new Color(15, 10, 30);
    private static final Color C_PURPLE    = new Color(92, 51, 181);
    private static final Color C_PURPLE_L  = new Color(196, 181, 253);
    private static final Color C_ORANGE    = new Color(249, 115, 22);
    private static final Color C_TEXT      = new Color(26, 18, 37);
    private static final Color C_MUTED     = new Color(107, 114, 128);
    private static final Color C_BORDER    = new Color(229, 224, 248);
    private static final int   SIDEBAR_W   = 220;

    private final Usuario usuarioActivo;
    private JPanel areaContenido;
    private JLabel labelSeccion;

    public MainFrame(Usuario usuarioActivo) {
        this.usuarioActivo = usuarioActivo;
        configurarVentana();
        construirUI();
        navegarA("productos");
    }

    private void configurarVentana() {
        setTitle("LevelUp Arcade — " + usuarioActivo.getNombre() + " [" + usuarioActivo.getRol() + "]");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1100, 700);
        setMinimumSize(new Dimension(900, 600));
        setLocationRelativeTo(null);
        getContentPane().setBackground(C_BG);
    }

    private void construirUI() {
        setLayout(new BorderLayout());
        add(construirSidebar(), BorderLayout.WEST);
        add(construirTopbar(), BorderLayout.NORTH);
        add(construirAreaContenido(), BorderLayout.CENTER);
        add(construirFooter(), BorderLayout.SOUTH);
    }

    private JPanel construirSidebar() {
        JPanel sidebar = new JPanel();
        sidebar.setBackground(C_SIDEBAR);
        sidebar.setPreferredSize(new Dimension(SIDEBAR_W, 0));
        sidebar.setLayout(new BoxLayout(sidebar, BoxLayout.Y_AXIS));
        sidebar.setBorder(BorderFactory.createMatteBorder(0, 0, 0, 1, new Color(196, 181, 253, 25)));

        // Brand
        JPanel brand = new JPanel();
        brand.setBackground(C_SIDEBAR);
        brand.setLayout(new BoxLayout(brand, BoxLayout.Y_AXIS));
        brand.setBorder(new EmptyBorder(24, 20, 16, 20));
        brand.setMaximumSize(new Dimension(Integer.MAX_VALUE, 90));

        JLabel tagLabel = new JLabel("PRÁCTICAS 1.º DAM");
        tagLabel.setFont(new Font("Consolas", Font.PLAIN, 11));
        tagLabel.setForeground(C_ORANGE);
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
        sidebar.add(crearNavBoton("▪  Asistente IA", "ia"));

        if ("administrador".equals(usuarioActivo.getRol())) {
            sidebar.add(crearNavBoton("▪  Usuarios", "usuarios"));
        }

        sidebar.add(Box.createVerticalGlue());
        sidebar.add(crearSeparador());

        JButton cerrarSesion = new JButton("▪  Cerrar sesión");
        cerrarSesion.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        cerrarSesion.setForeground(new Color(196, 181, 253, 160));
        cerrarSesion.setBackground(C_SIDEBAR);
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

    private JButton crearNavBoton(String texto, String seccion) {
        JButton boton = new JButton(texto) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                if (getModel().isRollover()) {
                    g2.setColor(new Color(92, 51, 181, 50));
                    g2.fillRect(0, 0, getWidth(), getHeight());
                }
                g2.dispose();
                super.paintComponent(g);
            }
        };
        boton.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        boton.setForeground(new Color(255, 255, 255, 160));
        boton.setBackground(C_SIDEBAR);
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

    private JLabel crearNavLabel(String texto) {
        JLabel label = new JLabel(texto);
        label.setFont(new Font("Consolas", Font.BOLD, 11));
        label.setForeground(new Color(196, 181, 253, 160));
        label.setBorder(new EmptyBorder(14, 20, 4, 20));
        label.setMaximumSize(new Dimension(Integer.MAX_VALUE, 28));
        return label;
    }

    private JPanel crearSeparador() {
        JPanel sep = new JPanel();
        sep.setBackground(new Color(196, 181, 253, 25));
        sep.setMaximumSize(new Dimension(Integer.MAX_VALUE, 1));
        sep.setPreferredSize(new Dimension(0, 1));
        return sep;
    }

    private JPanel construirTopbar() {
        JPanel topbar = new JPanel(new BorderLayout());
        topbar.setBackground(Color.WHITE);
        topbar.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createMatteBorder(0, 0, 1, 0, C_BORDER),
            new EmptyBorder(10, 24, 10, 24)
        ));
        topbar.setPreferredSize(new Dimension(0, 46));

        labelSeccion = new JLabel("Inicio");
        labelSeccion.setFont(new Font("Segoe UI", Font.BOLD, 13));
        labelSeccion.setForeground(C_PURPLE);

        JLabel rolTag = new JLabel(usuarioActivo.getRol().toUpperCase());
        rolTag.setFont(new Font("Consolas", Font.BOLD, 10));
        rolTag.setForeground(Color.WHITE);
        rolTag.setBackground("administrador".equals(usuarioActivo.getRol())
            ? C_PURPLE : new Color(59, 130, 246));
        rolTag.setOpaque(true);
        rolTag.setBorder(new EmptyBorder(3, 8, 3, 8));

        topbar.add(labelSeccion, BorderLayout.WEST);
        topbar.add(rolTag, BorderLayout.EAST);
        return topbar;
    }

    private JPanel construirAreaContenido() {
        areaContenido = new JPanel(new BorderLayout());
        areaContenido.setBackground(C_BG);
        return areaContenido;
    }

    private JLabel construirFooter() {
        JLabel footer = new JLabel(
            "Adrián Sánchez Cañadas · CampusFP · 1.º DAM · Curso 2025–2026",
            SwingConstants.CENTER
        );
        footer.setFont(new Font("Consolas", Font.PLAIN, 10));
        footer.setForeground(C_MUTED);
        footer.setBackground(Color.WHITE);
        footer.setOpaque(true);
        footer.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createMatteBorder(1, 0, 0, 0, C_BORDER),
            new EmptyBorder(6, 0, 6, 0)
        ));
        return footer;
    }

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