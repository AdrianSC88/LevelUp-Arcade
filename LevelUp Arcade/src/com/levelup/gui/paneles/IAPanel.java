package com.levelup.gui.paneles;

import com.levelup.controlador.LlmController;
import com.levelup.modelo.Usuario;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;

public class IAPanel extends JPanel {

    private static final Color C_BG       = new Color(248, 247, 255);
    private static final Color C_PURPLE   = new Color(92, 51, 181);
    private static final Color C_PURPLE_L = new Color(196, 181, 253);
    private static final Color C_ORANGE   = new Color(249, 115, 22);
    private static final Color C_BLUE     = new Color(59, 130, 246);
    private static final Color C_BORDER   = new Color(229, 224, 248);
    private static final Color C_WHITE    = Color.WHITE;
    private static final Color C_TEXT     = new Color(26, 18, 37);
    private static final Color C_MUTED    = new Color(107, 114, 128);
    private static final Color C_HEADER   = new Color(45, 21, 114);
    private static final Color C_FIELD_BG = new Color(252, 251, 255);

    private final LlmController llmController;
    private JTextField campoProducto;
    private JTextArea areaRespuesta;
    private JLabel labelEstado;

    public IAPanel(Usuario usuarioActivo) {
        this.llmController = new LlmController();
        setLayout(new BorderLayout(0, 0));
        setBackground(C_BG);
        construirUI();
    }

    private void construirUI() {
        add(construirTopbar(), BorderLayout.NORTH);
        add(construirCuerpo(), BorderLayout.CENTER);
    }

    private JPanel construirTopbar() {
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
            java.net.URL imgURL = getClass().getClassLoader().getResource("resources/logo2.png");
            if (imgURL != null) {
                Image scaled = new ImageIcon(imgURL).getImage().getScaledInstance(44, 44, Image.SCALE_SMOOTH);
                labelLogo.setIcon(new ImageIcon(scaled));
            }
        } catch (Exception ex) { /* sin logo */ }

        JLabel titulo = new JLabel("Asistente de Inteligencia Artificial");
        titulo.setFont(new Font("Segoe UI", Font.BOLD, 20));
        titulo.setForeground(C_WHITE);

        izquierda.add(labelLogo);
        izquierda.add(titulo);
        bar.add(izquierda, BorderLayout.WEST);
        return bar;
    }

    private JPanel construirCuerpo() {
        JPanel cuerpo = new JPanel(new BorderLayout(0, 20));
        cuerpo.setBackground(C_BG);
        cuerpo.setBorder(new EmptyBorder(28, 32, 28, 32));

        // Panel superior — entrada
        JPanel panelEntrada = new JPanel(new BorderLayout(0, 12));
        panelEntrada.setBackground(C_WHITE);
        panelEntrada.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(C_BORDER, 1),
            new EmptyBorder(20, 24, 20, 24)
        ));

        JLabel lblTitulo = new JLabel("Nombre del producto");
        lblTitulo.setFont(new Font("Consolas", Font.BOLD, 11));
        lblTitulo.setForeground(C_PURPLE);

        campoProducto = new JTextField();
        campoProducto.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        campoProducto.setBackground(C_FIELD_BG);
        campoProducto.setForeground(C_TEXT);
        campoProducto.setCaretColor(C_PURPLE);
        campoProducto.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(C_BORDER, 1),
            new EmptyBorder(10, 14, 10, 14)
        ));

        campoProducto.addFocusListener(new java.awt.event.FocusAdapter() {
            @Override public void focusGained(java.awt.event.FocusEvent e) {
                campoProducto.setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createLineBorder(C_BLUE, 2),
                    new EmptyBorder(9, 13, 9, 13)
                ));
            }
            @Override public void focusLost(java.awt.event.FocusEvent e) {
                campoProducto.setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createLineBorder(C_BORDER, 1),
                    new EmptyBorder(10, 14, 10, 14)
                ));
            }
        });

        JPanel panelBotones = new JPanel(new FlowLayout(FlowLayout.LEFT, 12, 0));
        panelBotones.setBackground(C_WHITE);

        JButton btnDescripcion = crearBoton("Generar descripción", C_PURPLE, C_WHITE);
        JButton btnCategoria   = crearBoton("Sugerir categoría", C_BLUE, C_WHITE);

        btnDescripcion.addActionListener(e -> ejecutar("descripcion"));
        btnCategoria.addActionListener(e -> ejecutar("categoria"));

        panelBotones.add(btnDescripcion);
        panelBotones.add(btnCategoria);

        labelEstado = new JLabel(" ");
        labelEstado.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        labelEstado.setForeground(C_MUTED);

        panelEntrada.add(lblTitulo, BorderLayout.NORTH);
        panelEntrada.add(campoProducto, BorderLayout.CENTER);
        panelEntrada.add(panelBotones, BorderLayout.SOUTH);

        // Panel inferior — respuesta
        JPanel panelRespuesta = new JPanel(new BorderLayout(0, 8));
        panelRespuesta.setBackground(C_WHITE);
        panelRespuesta.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(C_BORDER, 1),
            new EmptyBorder(20, 24, 20, 24)
        ));

        JPanel cabeceraResp = new JPanel(new BorderLayout());
        cabeceraResp.setBackground(C_WHITE);

        JLabel lblRespuesta = new JLabel("Respuesta del asistente");
        lblRespuesta.setFont(new Font("Consolas", Font.BOLD, 11));
        lblRespuesta.setForeground(C_PURPLE);

        cabeceraResp.add(lblRespuesta, BorderLayout.WEST);
        cabeceraResp.add(labelEstado, BorderLayout.EAST);

        areaRespuesta = new JTextArea();
        areaRespuesta.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        areaRespuesta.setForeground(C_TEXT);
        areaRespuesta.setBackground(new Color(245, 243, 255));
        areaRespuesta.setLineWrap(true);
        areaRespuesta.setWrapStyleWord(true);
        areaRespuesta.setEditable(false);
        areaRespuesta.setBorder(new EmptyBorder(14, 16, 14, 16));
        areaRespuesta.setText("Introduce el nombre de un producto y pulsa uno de los botones para obtener una respuesta del asistente.");

        JScrollPane scrollResp = new JScrollPane(areaRespuesta);
        scrollResp.setBorder(BorderFactory.createLineBorder(C_BORDER, 1));

        panelRespuesta.add(cabeceraResp, BorderLayout.NORTH);
        panelRespuesta.add(scrollResp, BorderLayout.CENTER);

        cuerpo.add(panelEntrada,   BorderLayout.NORTH);
        cuerpo.add(panelRespuesta, BorderLayout.CENTER);
        return cuerpo;
    }

    private void ejecutar(String tipo) {
        String nombre = campoProducto.getText().trim();
        if (nombre.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Introduce el nombre de un producto.", "Aviso", JOptionPane.WARNING_MESSAGE);
            return;
        }
        labelEstado.setText("Consultando...");
        areaRespuesta.setText("Esperando respuesta del asistente...");

        SwingWorker<String, Void> worker = new SwingWorker<>() {
            @Override protected String doInBackground() {
                return tipo.equals("descripcion")
                    ? llmController.generarDescripcion(nombre)
                    : llmController.sugerirCategoria(nombre);
            }
            @Override protected void done() {
                try {
                    areaRespuesta.setText(get());
                    labelEstado.setText("Respuesta recibida");
                } catch (Exception ex) {
                    areaRespuesta.setText("Error al obtener la respuesta: " + ex.getMessage());
                    labelEstado.setText("Error");
                }
            }
        };
        worker.execute();
    }

    private JButton crearBoton(String texto, Color bg, Color fg) {
        JButton b = new JButton(texto);
        b.setBackground(bg); b.setForeground(fg);
        b.setFont(new Font("Segoe UI", Font.BOLD, 13));
        b.setBorder(new EmptyBorder(10, 20, 10, 20));
        b.setFocusPainted(false); b.setOpaque(true); b.setBorderPainted(false);
        b.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        b.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override public void mouseEntered(java.awt.event.MouseEvent e) {
                b.setBackground(new Color(Math.min(bg.getRed()+20,255), Math.min(bg.getGreen()+20,255), Math.min(bg.getBlue()+20,255)));
            }
            @Override public void mouseExited(java.awt.event.MouseEvent e) { b.setBackground(bg); }
        });
        return b;
    }
}
