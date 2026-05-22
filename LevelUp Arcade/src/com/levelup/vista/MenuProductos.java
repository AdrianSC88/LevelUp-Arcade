package com.levelup.vista;

import com.levelup.controlador.ProductoController;
import com.levelup.controlador.LlmController;
import com.levelup.controlador.CategoriaController;
import com.levelup.controlador.ProveedorController;
import com.levelup.modelo.Producto;
import com.levelup.modelo.Usuario;
import java.util.List;
import java.util.Scanner;

/**
 * Clase que gestiona el menú de productos.
 */
public class MenuProductos {

    private final ProductoController productoController;
    private final CategoriaController categoriaController;
    private final ProveedorController proveedorController;
    private final LlmController llmController = new LlmController();
    private final Scanner scanner;
    private final Usuario usuarioActivo;

    /**
     * Constructor. Inicializa los controladores de productos, categorías y proveedores y los parámetros de sesión.
     *
     * @param scanner       scanner para la lectura de datos por consola
     * @param usuarioActivo usuario que ha iniciado sesión en el sistema
     */
    public MenuProductos(Scanner scanner, Usuario usuarioActivo) {
        this.productoController = new ProductoController();
        this.categoriaController = new CategoriaController();
        this.proveedorController = new ProveedorController();
        this.scanner = scanner;
        this.usuarioActivo = usuarioActivo;
    }

    /**
     * Muestra el menú de gestión de productos.
     */
    public void mostrar() {
        boolean salir = false;
        while (!salir) {
            System.out.println("\n╔══════════════════════════════════════════╗");
            System.out.println("║          GESTIÓN DE PRODUCTOS            ║");
            System.out.println("╠══════════════════════════════════════════╣");
            System.out.println("║  1. Listar todos los productos           ║");
            System.out.println("║  2. Buscar producto por id               ║");
            if (usuarioActivo.getRol().equals("administrador")) {
                System.out.println("║  3. Añadir producto                      ║");
                System.out.println("║  4. Actualizar producto                  ║");
                System.out.println("║  5. Eliminar producto                    ║");
            }
            System.out.println("║  0. Volver al menú principal             ║");
            System.out.println("╚══════════════════════════════════════════╝");
            System.out.print("Selecciona una opción: ");

            switch (scanner.nextLine()) {
                case "1" -> listarProductos();
                case "2" -> buscarProducto();
                case "3" -> {
                    if (esAdmin()) añadirProducto();
                    else opcionNoValida();
                }
                case "4" -> {
                    if (esAdmin()) actualizarProducto();
                    else opcionNoValida();
                }
                case "5" -> {
                    if (esAdmin()) eliminarProducto();
                    else opcionNoValida();
                }
                case "0" -> salir = true;
                default -> opcionNoValida();
            }
        }
    }

    /**
     * Lista todos los productos en formato tabla.
     */
    private void listarProductos() {
        List<Producto> productos = productoController.obtenerTodos();
        if (productos.isEmpty()) {
            System.out.println("No hay productos registrados.");
            return;
        }
        System.out.println("\n" + String.format("| %-5s | %-50s | %-10s | %-8s | %-25s | %-25s |",
                "ID", "NOMBRE", "PRECIO", "STOCK", "CATEGORÍA", "PROVEEDOR"));
        System.out.println("-".repeat(142));
        productos.forEach(System.out::println);
    }

    /**
     * Busca y muestra un producto por su id.
     */
    private void buscarProducto() {
        System.out.print("Introduce el id del producto: ");
        try {
            int id = Integer.parseInt(scanner.nextLine());
            Producto producto = productoController.obtenerPorId(id);
            if (producto != null) {
                System.out.println("\n" + String.format("| %-5s | %-50s | %-10s | %-8s | %-25s | %-25s |",
                        "ID", "NOMBRE", "PRECIO", "STOCK", "CATEGORÍA", "PROVEEDOR"));
                System.out.println("-".repeat(142));
                System.out.println(producto);
                System.out.println("\nDescripción: " + 
                        (producto.getDescripcion() != null && !producto.getDescripcion().isBlank() 
                        ? producto.getDescripcion() : "Sin descripción"));
            } else {
                System.out.println("Producto no encontrado.");
            }
        } catch (NumberFormatException e) {
            System.err.println("El id debe ser un número entero.");
        }
    }

    /**
     * Solicita los datos para añadir un nuevo producto, con opción de usar
     * la IA para generar la descripción y sugerir la categoría.
     */
    private void añadirProducto() {
        System.out.println("\n--- AÑADIR PRODUCTO ---");
        System.out.print("Nombre: ");
        String nombre = scanner.nextLine();

        // Descripción
        System.out.println("¿Cómo quieres introducir la descripción?");
        System.out.println("  1. Escribirla manualmente");
        System.out.println("  2. Generarla con IA");
        System.out.print("Selecciona una opción: ");
        String descripcion = "";
        switch (scanner.nextLine().trim()) {
            case "1" -> {
                System.out.print("Descripción: ");
                descripcion = scanner.nextLine();
            }
            case "2" -> {
                System.out.println("Generando descripción, espera...");
                descripcion = llmController.generarDescripcion(nombre);
                System.out.println("Descripción generada: " + descripcion);
            }
            default -> {
                System.out.print("Opción no válida, introduce la descripción manualmente: ");
                descripcion = scanner.nextLine();
            }
        }

        double precio = 0;
        try {
            System.out.print("Precio: ");
            precio = Double.parseDouble(scanner.nextLine());
        } catch (NumberFormatException e) {
            System.err.println("Precio no válido.");
            return;
        }

        int stock = 0;
        try {
            System.out.print("Stock: ");
            stock = Integer.parseInt(scanner.nextLine());
        } catch (NumberFormatException e) {
            System.err.println("Stock no válido.");
            return;
        }

        // Categoría
        System.out.println("¿Cómo quieres asignar la categoría?");
        System.out.println("  1. Seleccionarla manualmente");
        System.out.println("  2. Sugerirla con IA");
        System.out.print("Selecciona una opción: ");
        int idCategoria = 0;
        switch (scanner.nextLine().trim()) {
            case "1" -> {
                listarCategorias();
                try {
                    System.out.print("Id categoría: ");
                    idCategoria = Integer.parseInt(scanner.nextLine());
                } catch (NumberFormatException e) {
                    System.err.println("Id de categoría no válido.");
                    return;
                }
            }
            case "2" -> {
                System.out.println("Consultando categoría sugerida, espera...");
                String categoriaSugerida = llmController.sugerirCategoria(nombre);
                System.out.println("Categoría sugerida por la IA: " + categoriaSugerida);
                listarCategorias();
                try {
                    System.out.print("Introduce el Id de la categoría que corresponda: ");
                    idCategoria = Integer.parseInt(scanner.nextLine());
                } catch (NumberFormatException e) {
                    System.err.println("Id de categoría no válido.");
                    return;
                }
            }
            default -> {
                listarCategorias();
                try {
                    System.out.print("Opción no válida, introduce el Id de categoría manualmente: ");
                    idCategoria = Integer.parseInt(scanner.nextLine());
                } catch (NumberFormatException e) {
                    System.err.println("Id de categoría no válido.");
                    return;
                }
            }
        }

        listarProveedores();
        int idProveedor = 0;
        try {
            System.out.print("Id proveedor: ");
            idProveedor = Integer.parseInt(scanner.nextLine());
        } catch (NumberFormatException e) {
            System.err.println("Id de proveedor no válido.");
            return;
        }

        if (productoController.añadirProducto(nombre, descripcion, precio, stock, idCategoria, idProveedor)) {
            System.out.println("Producto añadido correctamente.");
        } else {
            System.out.println("Error al añadir el producto.");
        }
    }

    /**
     * Solicita los datos para actualizar un producto existente, con opción
     * de usar la IA para regenerar la descripción y sugerir la categoría.
     */
    private void actualizarProducto() {
        System.out.println("\n--- ACTUALIZAR PRODUCTO ---");
        try {
            System.out.print("Id del producto a actualizar: ");
            int id = Integer.parseInt(scanner.nextLine());
            Producto producto = productoController.obtenerPorId(id);
            if (producto == null) {
                System.out.println("Producto no encontrado.");
                return;
            }

            System.out.print("Nuevo nombre (" + producto.getNombre() + "): ");
            String nombre = scanner.nextLine();

            // Descripción
            System.out.println("¿Cómo quieres actualizar la descripción?");
            System.out.println("  1. Escribirla manualmente");
            System.out.println("  2. Generarla con IA");
            System.out.println("  3. Mantener la actual (" + producto.getDescripcion() + ")");
            System.out.print("Selecciona una opción: ");
            String descripcion = "";
            switch (scanner.nextLine().trim()) {
                case "1" -> {
                    System.out.print("Nueva descripción: ");
                    descripcion = scanner.nextLine();
                }
                case "2" -> {
                    System.out.println("Generando descripción, espera...");
                    descripcion = llmController.generarDescripcion(nombre.isBlank() ? producto.getNombre() : nombre);
                    System.out.println("Descripción generada: " + descripcion);
                }
                case "3" -> descripcion = producto.getDescripcion();
                default -> {
                    System.out.print("Opción no válida, introduce la descripción manualmente: ");
                    descripcion = scanner.nextLine();
                }
            }

            System.out.print("Nuevo precio (" + producto.getPrecio() + "): ");
            double precio = Double.parseDouble(scanner.nextLine());

            System.out.print("Nuevo stock (" + producto.getStock() + "): ");
            int stock = Integer.parseInt(scanner.nextLine());

            // Categoría
            System.out.println("¿Cómo quieres actualizar la categoría?");
            System.out.println("  1. Seleccionarla manualmente");
            System.out.println("  2. Sugerirla con IA");
            System.out.println("  3. Mantener la actual (" + producto.getCategoria().getId() + ")");
            System.out.print("Selecciona una opción: ");
            int idCategoria = 0;
            switch (scanner.nextLine().trim()) {
                case "1" -> {
                    listarCategorias();
                    System.out.print("Id categoría: ");
                    idCategoria = Integer.parseInt(scanner.nextLine());
                }
                case "2" -> {
                    System.out.println("Consultando categoría sugerida, espera...");
                    String categoriaSugerida = llmController.sugerirCategoria(nombre.isBlank() ? producto.getNombre() : nombre);
                    System.out.println("Categoría sugerida por la IA: " + categoriaSugerida);
                    listarCategorias();
                    System.out.print("Introduce el Id de la categoría que corresponda: ");
                    idCategoria = Integer.parseInt(scanner.nextLine());
                }
                case "3" -> idCategoria = producto.getCategoria().getId();
                default -> {
                    listarCategorias();
                    System.out.print("Opción no válida, introduce el Id de categoría manualmente: ");
                    idCategoria = Integer.parseInt(scanner.nextLine());
                }
            }

            listarProveedores();
            System.out.print("Id proveedor (" + producto.getProveedor().getId() + "): ");
            int idProveedor = Integer.parseInt(scanner.nextLine());

            if (productoController.actualizarProducto(id, nombre, descripcion, precio, stock, idCategoria, idProveedor)) {
                System.out.println("Producto actualizado correctamente.");
            } else {
                System.out.println("Error al actualizar el producto.");
            }
        } catch (NumberFormatException e) {
            System.err.println("Valor no válido.");
        }
    }

    /**
     * Solicita confirmación y elimina un producto.
     */
    private void eliminarProducto() {
        System.out.println("\n--- ELIMINAR PRODUCTO ---");
        try {
            System.out.print("Id del producto a eliminar: ");
            int id = Integer.parseInt(scanner.nextLine());
            Producto producto = productoController.obtenerPorId(id);
            if (producto == null) {
                System.out.println("Producto no encontrado.");
                return;
            }
            System.out.println("¿Seguro que quieres eliminar '" + producto.getNombre() + "'? (s/n): ");
            if (scanner.nextLine().equalsIgnoreCase("s")) {
                if (productoController.eliminarProducto(id)) {
                    System.out.println("Producto eliminado correctamente.");
                } else {
                    System.out.println("Error al eliminar el producto.");
                }
            } else {
                System.out.println("Operación cancelada.");
            }
        } catch (NumberFormatException e) {
            System.err.println("El id debe ser un número entero.");
        }
    }

    /**
     * Lista las categorías disponibles como ayuda al usuario.
     */
    private void listarCategorias() {
        System.out.println("\nCategorías disponibles:");
        categoriaController.obtenerTodas().forEach(c ->
                System.out.println("  " + c.getId() + " - " + c.getNombre()));
    }

    /**
     * Lista los proveedores disponibles como ayuda al usuario.
     */
    private void listarProveedores() {
        System.out.println("\nProveedores disponibles:");
        proveedorController.obtenerTodos().forEach(p ->
                System.out.println("  " + p.getId() + " - " + p.getNombre()));
    }

    private boolean esAdmin() {
        return usuarioActivo.getRol().equals("administrador");
    }

    private void opcionNoValida() {
        System.out.println("Opción no válida o sin permisos suficientes.");
    }
}