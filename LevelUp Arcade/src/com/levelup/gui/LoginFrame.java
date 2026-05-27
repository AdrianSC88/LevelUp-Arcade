package com.levelup.gui;

import com.levelup.controlador.UsuarioController;
import com.levelup.modelo.Usuario;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.geom.RoundRectangle2D;

/**
 * Ventana de inicio de sesión de LevelUp Arcade.
 * <p>
 * Muestra un formulario con campos de usuario y contraseña, un saludo
 * dinámico según la hora del día y un sistema de bloqueo tras tres
 * intentos fallidos consecutivos. Si las credenciales son correctas,
 * abre {@link MainFrame} y se cierra a sí misma.
 * </p>
 */
public class LoginFrame extends JFrame {

    private static final Color C_HEADER_TOP = new Color(92, 51, 181);
    private static final Color C_HEADER_MID = new Color(45, 21, 114);
    private static final Color C_ORANGE     = new Color(249, 115, 22);
    private static final Color C_BLUE       = new Color(59, 130, 246);
    private static final Color C_BG         = new Color(248, 247, 255);
    private static final Color C_PURPLE     = new Color(92, 51, 181);
    private static final Color C_TEXT       = new Color(26, 18, 37);
    private static final Color C_MUTED      = new Color(107, 114, 128);
    private static final Color C_BORDER     = new Color(180, 170, 220);
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

    /**
     * Construye la ventana de login inicializando el controlador de usuarios
     * y construyendo todos los componentes de la interfaz.
     */
    public LoginFrame() {
        this.usuarioController = new UsuarioController();
        configurarVentana();
        construirUI();
    }

    /**
     * Configura las propiedades básicas de la ventana: título, tamaño,
     * posición centrada, no redimensionable y siempre al frente.
     */
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

    /**
     * Construye y organiza los tres bloques de la ventana:
     * cabecera, formulario y pie.
     */
    private void construirUI() {
        setLayout(new BorderLayout());
        add(construirHeader(), BorderLayout.NORTH);
        add(construirFormulario(), BorderLayout.CENTER);
        add(construirFooter(), BorderLayout.SOUTH);
    }

    /**
     * Construye el panel de cabecera con gradiente morado, logo de la aplicación,
     * título y subtítulo. La línea inferior tiene un gradiente naranja-azul.
     *
     * @return el panel de cabecera listo para añadir al layout
     */
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

    /**
     * Construye el panel central con el formulario de login: saludo dinámico,
     * campos de usuario y contraseña, etiqueta de error, botón de acceso y
     * aviso de seguridad.
     *
     * @return el panel de formulario listo para añadir al layout
     */
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

    /**
     * Crea el panel informativo que muestra un aviso de acceso restringido
     * y la política de cifrado de contraseñas.
     *
     * @return el panel de aviso listo para añadir al formulario
     */
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

    /**
     * Construye el pie de página con la versión de la aplicación
     * y el aviso de copyright.
     *
     * @return el panel de pie de página listo para añadir al layout
     */
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

    /**
     * Crea una etiqueta de formulario con el estilo estándar de la pantalla
     * de login (negrita, color de texto principal, alineada a la izquierda).
     *
     * @param texto el texto que mostrará la etiqueta
     * @return la etiqueta creada y estilizada
     */
    private JLabel crearLabel(String texto) {
        JLabel lbl = new JLabel(texto);
        lbl.setFont(new Font("Segoe UI", Font.BOLD, 12));
        lbl.setForeground(C_TEXT);
        lbl.setAlignmentX(Component.LEFT_ALIGNMENT);
        return lbl;
    }

    /**
     * Aplica el estilo visual estándar a un campo de texto o contraseña:
     * fondo claro, fuente, borde redondeado y efecto de foco azul al activarse.
     *
     * @param campo el campo ({@link JTextField} o {@link JPasswordField}) a estilizar
     */
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

        campo.addFocusListener(new java.awt.event.FocusAdapter() {
            @Override public void focusGained(java.awt.event.FocusEvent e) {
                campo.setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createLineBorder(C_BLUE, 2),
                    new EmptyBorder(9, 13, 9, 13)
                ));
            }
            @Override public void focusLost(java.awt.event.FocusEvent e) {
                campo.setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createLineBorder(C_BORDER, 1),
                    new EmptyBorder(10, 14, 10, 14)
                ));
            }
        });
    }

    /**
     * Crea el botón principal de acceso con fondo degradado morado-azul pintado
     * manualmente. Cambia de tono al pasar el ratón por encima.
     *
     * @return el botón de login configurado y con su listener asignado
     */
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

    /**
     * Intenta autenticar al usuario con las credenciales introducidas.
     * <p>
     * Si los campos están vacíos muestra un aviso. Si las credenciales son
     * correctas abre {@link MainFrame}. Si son incorrectas incrementa el
     * contador de intentos; al alcanzar tres bloquea el botón y cierra la
     * aplicación tras dos segundos.
     * </p>
     */
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

    /**
     * Devuelve un saludo contextual en función de la hora actual del sistema.
     *
     * @return {@code "Buenos días"} antes de las 12:00,
     *         {@code "Buenas tardes"} entre las 12:00 y las 19:59,
     *         {@code "Buenas noches"} a partir de las 20:00
     */
    private String obtenerSaludo() {
        int hora = java.time.LocalTime.now().getHour();
        if (hora < 12) return "Buenos días";
        if (hora < 20) return "Buenas tardes";
        return "Buenas noches";
    }
}
