# Casas Embrujadas API

![Pruebas del backend](https://github.com/Azath-Esp/casas-embrujadas-api/actions/workflows/ci.yml/badge.svg)

API REST de la tienda ficticia "Casas Embrujadas", que "vende" lugares embrujados reales de Chile. Evaluación Parcial 3 de DSY1104 Desarrollo Full Stack II, Duoc UC.

Autor: Dinko Ocampo

- Repositorio del frontend: https://github.com/Azath-Esp/casas-embrujadas-frontend
- API publicada: https://casas-embrujadas-api.onrender.com
- Sitio publicado: https://casas-embrujadas-frontend.vercel.app
- Documentación interactiva: https://casas-embrujadas-api.onrender.com/swagger-ui.html

## Stack

| Pieza | Versión | Para qué |
|---|---|---|
| Java | 21 | Lenguaje |
| Spring Boot | 4.1 | Web, validación, seguridad, Actuator |
| PostgreSQL en Neon | 17 | Base de datos en la nube |
| JDBC con NamedParameterJdbcTemplate | | Acceso a datos con SQL escrito a mano, sin JPA |
| Spring Security + jjwt | 0.13 | Autenticación con JWT y autorización por rol |
| springdoc-openapi | 3.2 | Swagger UI con grupos v1 y v2 |
| OpenPDF | 3.0 | Boleta en PDF |
| JUnit 5, Mockito, MockMvc | | Pruebas unitarias, de la capa web y de integración |

## Arquitectura

```
controller  →  service  →  repository (JDBC)  →  PostgreSQL
    ↑             ↑
   DTO        reglas de negocio, @Transactional
```

- `controller`: recibe la solicitud, valida el DTO y devuelve el código HTTP correcto. Rutas `/api/v1` y `/api/v2`.
- `service`: reglas de negocio (compra, reservas de 24 horas, permisos sobre recursos propios).
- `repository`: SQL con parámetros nombrados y `RowMapper`.
- `security`: filtro JWT sin estado, roles ADMIN, VENDEDOR y CLIENTE.
- `exception`: manejo global de errores con una respuesta JSON uniforme.
- `scheduler`: expira las reservas vencidas cada minuto.

## Roles y permisos

| Recurso | Visitante | CLIENTE | VENDEDOR | ADMIN |
|---|---|---|---|---|
| Catálogo, detalle, mapa, comparador, categorías, regiones | Leer | Leer | Leer | Leer y administrar |
| Registro e inicio de sesión | Sí | — | — | — |
| Perfil propio (`/usuarios/me`) | — | Sí | — | Sí |
| Comprar, reservar, favoritos, mis boletas y reservas | — | Sí | — | — |
| Boletas de todos (lista, detalle, PDF) | — | — | Leer | Leer y anular |
| Usuarios, reservas de todos, dashboard | — | — | — | Sí |

## Requisitos

- JDK 21 con la variable `JAVA_HOME` configurada
- Un proyecto en Neon (neon.tech) con la base `casas_embrujadas`
- No se necesita instalar Maven: el proyecto incluye Maven Wrapper

## Instalación

1. En la consola de Neon, crear el proyecto (PostgreSQL 17, región AWS US East 1) y la base `casas_embrujadas`.
2. En el SQL Editor de Neon, con el rol dueño de la base, ejecutar `database/schema.sql` y luego `database/data.sql`.
3. En el mismo SQL Editor, crear el rol de la aplicación con permisos solo sobre los datos:

```sql
CREATE ROLE casas_app WITH LOGIN PASSWORD 'una_clave_larga_y_aleatoria';
GRANT USAGE ON SCHEMA public TO casas_app;
GRANT SELECT, INSERT, UPDATE, DELETE ON ALL TABLES IN SCHEMA public TO casas_app;
GRANT USAGE, SELECT ON ALL SEQUENCES IN SCHEMA public TO casas_app;
```

4. Copiar `.env.example` como `.env` y completar las variables.

## Variables de entorno

| Variable | Ejemplo | Para qué |
|---|---|---|
| `DB_URL` | `jdbc:postgresql://ep-…-pooler.us-east-1.aws.neon.tech/casas_embrujadas?sslmode=require` | Conexión a Neon |
| `DB_USERNAME` | `casas_app` | Rol de la aplicación |
| `DB_PASSWORD` | | Contraseña de `casas_app` |
| `JWT_SECRET` | | Secreto en Base64 de al menos 32 bytes para firmar los tokens |
| `JWT_EXPIRATION_MINUTES` | `60` | Duración del token |
| `CORS_ALLOWED_ORIGINS` | `http://localhost:5173` | Orígenes del frontend permitidos, separados por comas |
| `PORT` | `8080` | Puerto del servidor |

El archivo `.env` está en `.gitignore`: ninguna clave se sube al repositorio.

## Ejecución

```
.\mvnw spring-boot:run
```

- Estado del servicio: http://localhost:8080/actuator/health
- Documentación Swagger: http://localhost:8080/swagger-ui.html
- Colección de Postman: `postman/casas-embrujadas.postman_collection.json` con el entorno `postman/casas-embrujadas-local.postman_environment.json`

## Pruebas

| Tipo | Cantidad | Comando | Necesita base |
|---|---|---|---|
| Unitarias (Mockito, MockMvc, JWT, PDF, permisos por rol) | 82 | `.\mvnw test` | No |
| Integración (repositorios, servicios, controladores, seguridad) | 46 | `.\mvnw test "-Dtest=RepositoriosIT,ServiciosIT,ControllersIT,SeguridadIT,ExtrasIT"` | Sí, con los datos iniciales |
| Postman (flujo completo y permisos de cada rol) | 64 solicitudes, 91 comprobaciones | Runner de Postman | Servidor encendido |

GitHub Actions ejecuta las 82 unitarias y, después, las 46 de integración contra un PostgreSQL 17 temporal en cada push y Pull Request a `main` y `develop` (`.github/workflows/ci.yml`). El detalle de cada prueba está en [docs/PRUEBAS.md](docs/PRUEBAS.md).

## Estructura

```
casas-embrujadas-api/
├── .github/workflows/ci.yml   Pruebas automáticas
├── database/                  schema.sql y data.sql
├── docs/                      CHECKLIST.md y PRUEBAS.md
├── postman/                   Colección y entorno
├── src/main/java/cl/duoc/casasembrujadas/
│   ├── config/  controller/  dto/  exception/  mapper/  model/
│   └── report/  repository/  scheduler/  security/  service/
└── src/test/java/…            Pruebas unitarias (…Test) y de integración (…IT)
```

## Usuarios de prueba

| Rol | Correo | Contraseña |
|---|---|---|
| ADMIN | admin@casasembrujadas.cl | Admin123! |
| VENDEDOR | vendedor@casasembrujadas.cl | Vendedor123! |
| CLIENTE | cliente@casasembrujadas.cl | Cliente123! |

Son cuentas de demostración incluidas en `database/data.sql`.

## Fuentes

Las fuentes de cada leyenda y la licencia de cada imagen están en [FUENTES.md](FUENTES.md).

## Aviso

Tienda ficticia con fines académicos. Los lugares y leyendas son reales; las ventas no.
