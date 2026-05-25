# LevelUp Arcade 🎮
Proyecto final 1ºDAM 

Sistema de gestión de inventario, clientes y proveedores con inteligencia artificial para la empresa LevelUp Arcade.

## Tecnologías

- Java 21
- MySQL (Ubuntu Server)
- OpenRouter API (LLM)
- JDBC · BCrypt · Gson · JUnit 5

## Requisitos previos

- Java 21 o superior instalado
- Servidor MySQL en Ubuntu Server accesible en red
- Archivo `config.properties` configurado correctamente

## Configuración

Crea el archivo `config.properties` en la misma carpeta que el `.jar` con el siguiente contenido:

```properties
db.url=jdbc:mariadb://IP_DEL_SERVIDOR:3306/levelup_arcade
db.usuario=TU_USUARIO
db.password=TU_PASSWORD
```

## Ejecución

```bash
java -jar levelup-arcade.jar
```

## Credenciales por defecto

| Usuario | Contraseña | Rol |
|---------|-----------|-----|
| admin | 123456 | Administrador |

## Estructura del proyecto

LevelUp-Arcade/
├── LevelUp Arcade/        # Proyecto Eclipse
│   ├── src/               # Código fuente
│   ├── test/              # Pruebas unitarias JUnit 5
│   ├── doc/               # Javadoc generado
│   └── logs/              # Registros de actividad
├── docs/                  # Diagramas y documentación
├── lib/                   # Librerías externas
├── sql/                   # Scripts SQL
├── README.md
└── .gitignore

## Funcionalidades

- Gestión completa de productos, categorías, clientes y proveedores
- Sistema de pedidos
- Login con roles (administrador / empleado)
- Generación de descripciones de productos con IA
- Sugerencia de categorías con IA
- Copias de seguridad automáticas en el servidor

## Control de versiones

Proyecto desarrollado con Git. Repositorio en GitHub.