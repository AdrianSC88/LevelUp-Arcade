package com.levelup.controlador;

import com.levelup.servicio.LlmService;

/**
 * Controlador para las funcionalidades de Inteligencia Artificial.
 */
public class LlmController {

    private final LlmService llmService;
    private final CategoriaController categoriaController;

    /**
     * Constructor. Inicializa el servicio LLM y el controlador de categorías.
     */
    public LlmController() {
        this.llmService = new LlmService();
        this.categoriaController = new CategoriaController();
    }

    /**
     * Genera una descripción comercial para un producto dado su nombre.
     *
     * @param nombreProducto Nombre del producto.
     * @return Descripción generada por la IA.
     */
    public String generarDescripcion(String nombreProducto) {
        String prompt = "Genera una descripción comercial breve y atractiva (máximo 3 frases) " +
                "para el siguiente producto de una tienda de videojuegos y merchandising: " +
                nombreProducto + ". Responde solo con la descripción, sin títulos ni explicaciones.";
        return llmService.consultarLlm(prompt);
    }

    /**
     * Sugiere una categoría para un producto dado su nombre.
     *
     * @param nombreProducto Nombre del producto.
     * @return Categoría sugerida por la IA.
     */
    public String sugerirCategoria(String nombreProducto) {
        String categorias = categoriaController.obtenerTodas()
                .stream()
                .map(c -> c.getNombre())
                .collect(java.util.stream.Collectors.joining(", "));

        String prompt = "Sugiere una única categoría para clasificar el siguiente producto " +
                "en una tienda de videojuegos: " + nombreProducto +
                ". Las categorías posibles son: " + categorias + ". " +
                "Responde solo con el nombre de la categoría, sin explicaciones.";
        return llmService.consultarLlm(prompt);
    }
}
