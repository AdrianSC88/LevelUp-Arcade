package com.levelup.servicio;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.Properties;
import java.util.logging.Logger;

/**
 * Servicio para la comunicación con el LLM a través de la API de OpenRouter.
 */
public class LlmService {

    private static final String URL_API = "https://openrouter.ai/api/v1/chat/completions";
    private final String apiKey;
    private final String modelo;
    private static final Logger logger = Logger.getLogger(LlmService.class.getName());

    /**
     * Constructor. Carga la API Key y el modelo desde config.properties.
     */
    public LlmService() {
        Properties props = cargarProperties();
        this.apiKey = props.getProperty("openrouter.api.key");
        this.modelo = props.getProperty("openrouter.modelo");
    }

    private Properties cargarProperties() {
        Properties props = new Properties();
        try (var fis = getClass().getClassLoader().getResourceAsStream("config.properties")) {
            if (fis == null) {
                logger.severe("No se encontró config.properties en el classpath");
                return props;
            }
            props.load(fis);
        } catch (IOException e) {
            logger.severe("No se pudo cargar config.properties: " + e.getMessage());
        }
        return props;
    }

    /**
     * Envía un prompt al LLM y devuelve la respuesta como String.
     *
     * @param prompt La instrucción o pregunta para el modelo.
     * @return La respuesta generada por el LLM, o mensaje de error.
     */
    public String consultarLlm(String prompt) {
        if (apiKey == null || apiKey.isBlank()) {
            return "Error: API Key no configurada.";
        }
        if (modelo == null || modelo.isBlank()) {
            return "Error: modelo LLM no configurado.";
        }

        try {
            JsonObject mensaje = new JsonObject();
            mensaje.addProperty("role", "user");
            mensaje.addProperty("content", prompt);

            JsonArray mensajes = new JsonArray();
            mensajes.add(mensaje);

            JsonObject cuerpo = new JsonObject();
            cuerpo.addProperty("model", this.modelo);
            cuerpo.add("messages", mensajes);

            HttpClient client = HttpClient.newHttpClient();
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(URL_API))
                    .header("Content-Type", "application/json")
                    .header("Authorization", "Bearer " + apiKey)
                    .POST(HttpRequest.BodyPublishers.ofString(cuerpo.toString()))
                    .build();

            HttpResponse<String> response = client.send(request,
                    HttpResponse.BodyHandlers.ofString());

            if (response.statusCode() != 200) {
                return "Error HTTP " + response.statusCode() + ": " + response.body();
            }

            JsonObject respuestaJson = JsonParser.parseString(response.body()).getAsJsonObject();
            return respuestaJson
                    .getAsJsonArray("choices")
                    .get(0).getAsJsonObject()
                    .getAsJsonObject("message")
                    .get("content").getAsString();

        } catch (Exception e) {
            logger.severe("Error al consultar el LLM: " + e.getMessage());
            return "Error al conectar con la IA: " + e.getMessage();
        }
    }
}