# Casas Embrujadas API

API REST de la tienda ficticia "Casas Embrujadas", que "vende" lugares embrujados reales de Chile. Evaluación Parcial 3 de DSY1104 Desarrollo Full Stack II, Duoc UC.

Autor: Dinko Ocampo

## Stack

- Java 21 y Spring Boot 4.1
- PostgreSQL 17 en Neon
- JDBC con JdbcTemplate
- Spring Security con JWT
- Swagger (springdoc-openapi)

## Requisitos

- JDK 21 con la variable JAVA_HOME configurada
- Un proyecto en Neon (neon.tech) con la base casas_embrujadas
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

4. Copiar `.env.example` como `.env` y completar la URL de Neon, la contraseña de `casas_app` y el secreto JWT.

## Ejecución

```
.\mvnw spring-boot:run
```

- Estado del servicio: http://localhost:8080/actuator/health
- Documentación Swagger: http://localhost:8080/swagger-ui.html

## Pruebas

Pruebas unitarias, no necesitan base de datos:

```
.\mvnw test
```

Pruebas de integración de los repositorios y de las reglas de negocio. Necesitan el `.env` apuntando a la rama `dev` de Neon con los datos de `database/data.sql` (Reset from parent la deja así). Cada prueba deshace sus cambios al terminar:

```
.\mvnw test "-Dtest=RepositoriosIT,ServiciosIT,ControllersIT"
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
