package com.levelup.gui.paneles;

import com.levelup.controlador.LlmController;
import com.levelup.gui.GUIUtils;
import com.levelup.modelo.Usuario;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;

/**
 * Panel del asistente de Inteligencia Artificial.
 * Permite generar descripciones de productos y sugerir categorías
 * mediante la API de OpenRouter.
 */
public class IAPanel extends JPanel {

    private final LlmController llmController;
    private JTextField campoProducto;
    private JTextArea areaRespuesta;
    private JLabel labelEstado;

    public IAPanel(Usuario usuarioActivo) {
        this.llmController = new LlmController();
        setLayout(new BorderLayout(0, 0));
        setBackground(GUIUtils.C_BG);
        construirUI();
    }

    private void construirUI() {
        add(GUIUtils.construirTopbar("Asistente de Inteligencia Artificial", null), BorderLayout.NORTH);
        add(construirCuerpo(), BorderLayout.CENTER);
    }

    private JPanel construirCuerpo() {
        JPanel cuerpo = new JPanel(new BorderLayout(0, 20));
        cuerpo.setBackground(GUIUtils.C_BG);
        cuerpo.setBorder(new EmptyBorder(28, 32, 28, 32));

        // Panel entrada
        JPanel panelEntrada = new JPanel(new BorderLayout(0, 12));
        panelEntrada.setBackground(GUIUtils.C_WHITE);
        panelEntrada.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(GUIUtils.C_BORDER, 1),
            new EmptyBorder(20, 24, 20, 24)
        ));

        JLabel lblTitulo = new JLabel("Nombre del producto");
        lblTitulo.setFont(new Font("Consolas", Font.BOLD, 11));
        lblTitulo.setForeground(GUIUtils.C_PURPLE);

        campoProducto = new JTextField();
        campoProducto.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        campoProducto.setBackground(GUIUtils.C_FIELD_BG);
        campoProducto.setForeground(GUIUtils.C_TEXT);
        campoProducto.setCaretColor(GUIUtils.C_PURPLE);
        campoProducto.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(GUIUtils.C_BORDER, 1),
            new EmptyBorder(10, 14, 10, 14)
        ));
        campoProducto.addFocusListener(new java.awt.event.FocusAdapter() {
            @Override public void focusGained(java.awt.event.FocusEvent e) {
                campoProducto.setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createLineBorder(GUIUtils.C_BLUE, 2), new EmptyBorder(9, 13, 9, 13)));
            }
            @Override public void focusLost(java.awt.event.FocusEvent e) {
                campoProducto.setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createLineBorder(GUIUtils.C_BORDER, 1), new EmptyBorder(10, 14, 10, 14)));
            }
        });

        JPanel panelBotones = new JPanel(new FlowLayout(FlowLayout.LEFT, 12, 0));
        panelBotones.setBackground(GUIUtils.C_WHITE);

        JButton btnDesc = GUIUtils.crearBotonTop("Generar descripción", GUIUtils.C_PURPLE, GUIUtils.C_WHITE);
        JButton btnCat  = GUIUtils.crearBotonTop("Sugerir categoría",   GUIUtils.C_BLUE,   GUIUtils.C_WHITE);
        btnDesc.addActionListener(e -> ejecutar("descripcion"));
        btnCat.addActionListener(e -> ejecutar("categoria"));

        panelBotones.add(btnDesc);
        panelBotones.add(btnCat);

        labelEstado = new JLabel(" ");
        labelEstado.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        labelEstado.setForeground(GUIUtils.C_MUTED);

        panelEntrada.add(lblTitulo, BorderLayout.NORTH);
        panelEntrada.add(campoProducto, BorderLayout.CENTER);
        panelEntrada.add(panelBotones, BorderLayout.SOUTH);

        // Panel respuesta
        JPanel panelRespuesta = new JPanel(new BorderLayout(0, 8));
        panelRespuesta.setBackground(GUIUtils.C_WHITE);
        panelRespuesta.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(GUIUtils.C_BORDER, 1),
            new EmptyBorder(20, 24, 20, 24)
        ));

        JPanel cabeceraResp = new JPanel(new BorderLayout());
        cabeceraResp.setBackground(GUIUtils.C_WHITE);
        JLabel lblRespuesta = new JLabel("Respuesta del asistente");
        lblRespuesta.setFont(new Font("Consolas", Font.BOLD, 11));
        lblRespuesta.setForeground(GUIUtils.C_PURPLE);
        cabeceraResp.add(lblRespuesta, BorderLayout.WEST);
        cabeceraResp.add(labelEstado, BorderLayout.EAST);

        areaRespuesta = new JTextArea();
        areaRespuesta.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        areaRespuesta.setForeground(GUIUtils.C_TEXT);
        areaRespuesta.setBackground(GUIUtils.C_ROW_ALT);
        areaRespuesta.setLineWrap(true);
        areaRespuesta.setWrapStyleWord(true);
        areaRespuesta.setEditable(false);
        areaRespuesta.setBorder(new EmptyBorder(14, 16, 14, 16));
        areaRespuesta.setText("Introduce el nombre de un producto y pulsa uno de los botones para obtener una respuesta del asistente. Recuerda que la respuesta solo es una sugerencia, no se aplica automáticamente.");

        JScrollPane scrollResp = new JScrollPane(areaRespuesta);
        scrollResp.setBorder(BorderFactory.createLineBorder(GUIUtils.C_BORDER, 1));

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
}
