package com.levelup.gui;

import com.levelup.controlador.UsuarioController;
import com.levelup.modelo.Usuario;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.geom.RoundRectangle2D;

public class LoginFrame extends JFrame {

    private static final Color C_HEADER_TOP = new Color(92, 51, 181);
    private static final Color C_HEADER_MID = new Color(45, 21, 114);
    private static final Color C_ORANGE     = new Color(249, 115, 22);
    private static final Color C_BLUE       = new Color(59, 130, 246);
    private static final Color C_BG         = new Color(248, 247, 255);
    private static final Color C_PURPLE     = new Color(92, 51, 181);
    private static final Color C_TEXT       = new Color(26, 18, 37);
    private static final Color C_MUTED      = new Color(107, 114, 128);
    private static final Color C_BORDER     = new Color(229, 224, 248);
    private static final Color C_FIELD_BG   = new Color(252, 251, 255);
    private static final Color C_ERROR      = new Color(239, 68, 68);
    private static final Color C_INFO_BG    = new Color(239, 246, 255);
    private static final Color C_INFO_BORDER= new Color(191, 219, 254);

    private final UsuarioController usuarioController;
    private JTextField campoUsuario;
    private JPasswordField campoPassword;
    private JLabel labelError;
    private JButton botonLogin;
    private int intentos = 0;

    public LoginFrame() {
        this.usuarioController = new UsuarioController();
        configurarVentana();
        construirUI();
    }

    private void configurarVentana() {
        setTitle("LevelUp Arcade — Iniciar sesión");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(440, 700);
        setLocationRelativeTo(null);
        setResizable(false);
        getContentPane().setBackground(C_BG);
        setAlwaysOnTop(true);
        toFront();
    }

    private void construirUI() {
        setLayout(new BorderLayout());
        add(construirHeader(), BorderLayout.NORTH);
        add(construirFormulario(), BorderLayout.CENTER);
        add(construirFooter(), BorderLayout.SOUTH);
    }

    private JPanel construirHeader() {
        JPanel header = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                // Gradiente morado → azul
                GradientPaint gp = new GradientPaint(
                	    0, 0, C_HEADER_TOP,
                	    getWidth(), getHeight(), C_HEADER_MID
                	);
                g2.setPaint(gp);
                g2.fillRect(0, 0, getWidth(), getHeight());
                // Línea naranja inferior
                GradientPaint lineaGp = new GradientPaint(0, 0, C_ORANGE, getWidth(), 0, C_BLUE);
                g2.setPaint(lineaGp);
                g2.fillRect(0, getHeight() - 4, getWidth(), 4);
                g2.dispose();
            }
        };
        header.setLayout(new BoxLayout(header, BoxLayout.Y_AXIS));
        header.setPreferredSize(new Dimension(440, 200));
        header.setBorder(new EmptyBorder(28, 0, 20, 0));

        JLabel labelLogo = new JLabel();
        labelLogo.setAlignmentX(Component.CENTER_ALIGNMENT);
        try {
            java.net.URL imgURL = getClass().getClassLoader().getResource("resources/logo.png");
            if (imgURL != null) {
                ImageIcon icon = new ImageIcon(imgURL);
                Image scaled = icon.getImage().getScaledInstance(160, 80, Image.SCALE_SMOOTH);
                labelLogo.setIcon(new ImageIcon(scaled));
            }
        } catch (Exception ex) { /* sin logo */ }

        JLabel labelTitulo = new JLabel("LevelUp Arcade");
        labelTitulo.setFont(new Font("Segoe UI", Font.BOLD, 24));
        labelTitulo.setForeground(Color.WHITE);
        labelTitulo.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel labelSub = new JLabel("Sistema de gestión empresarial");
        labelSub.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        labelSub.setForeground(new Color(255, 255, 255, 180));
        labelSub.setAlignmentX(Component.CENTER_ALIGNMENT);

        header.add(labelLogo);
        header.add(Box.createVerticalStrut(10));
        header.add(labelTitulo);
        header.add(Box.createVerticalStrut(4));
        header.add(labelSub);
        return header;
    }

    private JPanel construirFormulario() {
        JPanel outer = new JPanel(new GridBagLayout());
        outer.setBackground(C_BG);

        JPanel form = new JPanel();
        form.setLayout(new BoxLayout(form, BoxLayout.Y_AXIS));
        form.setBackground(C_BG);
        form.setBorder(new EmptyBorder(28, 36, 16, 36));
        form.setPreferredSize(new Dimension(440, 460));

        JLabel saludo = new JLabel(obtenerSaludo());
        saludo.setFont(new Font("Segoe UI", Font.BOLD, 20));
        saludo.setForeground(C_TEXT);
        saludo.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel instruccion = new JLabel("Introduce tus credenciales para acceder al sistema");
        instruccion.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        instruccion.setForeground(C_MUTED);
        instruccion.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel lblUsuario = crearLabel("Usuario");
        campoUsuario = new JTextField();
        estilizarCampo(campoUsuario);

        JLabel lblPassword = crearLabel("Contraseña");
        campoPassword = new JPasswordField();
        estilizarCampo(campoPassword);

        labelError = new JLabel(" ");
        labelError.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        labelError.setForeground(C_ERROR);
        labelError.setAlignmentX(Component.LEFT_ALIGNMENT);

        botonLogin = crearBotonLogin();

        JPanel aviso = crearAviso();

        form.add(saludo);
        form.add(Box.createVerticalStrut(4));
        form.add(instruccion);
        form.add(Box.createVerticalStrut(24));
        form.add(lblUsuario);
        form.add(Box.createVerticalStrut(6));
        form.add(campoUsuario);
        form.add(Box.createVerticalStrut(16));
        form.add(lblPassword);
        form.add(Box.createVerticalStrut(6));
        form.add(campoPassword);
        form.add(Box.createVerticalStrut(6));
        form.add(labelError);
        form.add(Box.createVerticalStrut(16));
        form.add(botonLogin);
        form.add(Box.createVerticalStrut(16));
        form.add(aviso);

        outer.add(form);
        campoPassword.addActionListener(e -> intentarLogin());
        campoUsuario.addActionListener(e -> campoPassword.requestFocus());
        return outer;
    }

    private JPanel crearAviso() {
        JPanel aviso = new JPanel(new BorderLayout());
        aviso.setBackground(C_INFO_BG);
        aviso.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(C_INFO_BORDER, 1),
            new EmptyBorder(10, 14, 10, 14)
        ));
        aviso.setMaximumSize(new Dimension(Integer.MAX_VALUE, 72));
        aviso.setAlignmentX(Component.LEFT_ALIGNMENT);
        JLabel txt = new JLabel("<html>Acceso restringido. Solo personal autorizado.<br>" +
            "Las contraseñas están protegidas con cifrado seguro.</html>");
        txt.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        txt.setForeground(new Color(29, 78, 216));
        aviso.add(txt, BorderLayout.CENTER);
        return aviso;
    }

    private JPanel construirFooter() {
        JPanel footer = new JPanel(new BorderLayout());
        footer.setBackground(Color.WHITE);
        footer.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createMatteBorder(1, 0, 0, 0, C_BORDER),
            new EmptyBorder(8, 20, 8, 20)
        ));
        JLabel izq = new JLabel("v1.0.0");
        izq.setFont(new Font("Consolas", Font.PLAIN, 10));
        izq.setForeground(C_ORANGE);
        JLabel der = new JLabel("© 2026 LevelUp Arcade");
        der.setFont(new Font("Consolas", Font.PLAIN, 10));
        der.setForeground(C_MUTED);
        footer.add(izq, BorderLayout.WEST);
        footer.add(der, BorderLayout.EAST);
        return footer;
    }

    private JLabel crearLabel(String texto) {
        JLabel lbl = new JLabel(texto);
        lbl.setFont(new Font("Segoe UI", Font.BOLD, 12));
        lbl.setForeground(C_TEXT);
        lbl.setAlignmentX(Component.LEFT_ALIGNMENT);
        return lbl;
    }

    private void estilizarCampo(JTextField campo) {
        campo.setBackground(C_FIELD_BG);
        campo.setForeground(C_TEXT);
        campo.setCaretColor(C_PURPLE);
        campo.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        campo.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(C_BORDER, 1),
            new EmptyBorder(10, 14, 10, 14)
        ));
        campo.setMaximumSize(new Dimension(Integer.MAX_VALUE, 44));
        campo.setAlignmentX(Component.LEFT_ALIGNMENT);
    }

    private JButton crearBotonLogin() {
        JButton boton = new JButton("→  Acceder al sistema") {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setPaint(new GradientPaint(
                    0, 0, getModel().isRollover() ? new Color(110, 70, 200) : C_PURPLE,
                    getWidth(), 0, getModel().isRollover() ? C_BLUE : new Color(59, 100, 220)
                ));
                g2.fill(new RoundRectangle2D.Float(0, 0, getWidth(), getHeight(), 8, 8));
                g2.dispose();
                super.paintComponent(g);
            }
        };
        boton.setForeground(Color.WHITE);
        boton.setFont(new Font("Segoe UI", Font.BOLD, 14));
        boton.setBorderPainted(false);
        boton.setContentAreaFilled(false);
        boton.setFocusPainted(false);
        boton.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        boton.setMaximumSize(new Dimension(Integer.MAX_VALUE, 48));
        boton.setAlignmentX(Component.LEFT_ALIGNMENT);
        boton.addActionListener(e -> intentarLogin());
        return boton;
    }

    private void intentarLogin() {
        String nombreUsuario = campoUsuario.getText().trim();
        String password = new String(campoPassword.getPassword());
        if (nombreUsuario.isEmpty() || password.isEmpty()) {
            labelError.setText("Introduce usuario y contraseña.");
            return;
        }
        Usuario usuario = usuarioController.login(nombreUsuario, password);
        if (usuario != null) {
            dispose();
            SwingUtilities.invokeLater(() -> new MainFrame(usuario).setVisible(true));
        } else {
            intentos++;
            campoPassword.setText("");
            if (intentos >= 3) {
                labelError.setText("Demasiados intentos. Cerrando.");
                botonLogin.setEnabled(false);
                Timer timer = new Timer(2000, e -> System.exit(0));
                timer.setRepeats(false);
                timer.start();
            } else {
                labelError.setText("Credenciales incorrectas. Intento " + intentos + "/3.");
            }
        }
    }

    private String obtenerSaludo() {
        int hora = java.time.LocalTime.now().getHour();
        if (hora < 12) return "Buenos días";
        if (hora < 20) return "Buenas tardes";
        return "Buenas noches";
    }
}