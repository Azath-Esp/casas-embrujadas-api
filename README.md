# Casas Embrujadas API

API REST de la tienda ficticia "Casas Embrujadas", que "vende" lugares embrujados reales de Chile. Evaluación Parcial 3 de DSY1104 Desarrollo Full Stack II, Duoc UC.

Autor: Dinko Ocampo

## Stack

- Java 21 y Spring Boot 4.1
- MySQL 8
- JDBC con JdbcTemplate
- Spring Security con JWT
- Swagger (springdoc-openapi)

## Requisitos

- JDK 21 con la variable JAVA_HOME configurada
- MySQL 8 en ejecución
- No se necesita instalar Maven: el proyecto incluye Maven Wrapper

## Instalación

1. Crear la base de datos y el usuario de la aplicación, como root:

```sql
CREATE DATABASE IF NOT EXISTS casas_embrujadas CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci;
CREATE USER IF NOT EXISTS 'casas_app'@'localhost' IDENTIFIED BY 'una_clave_propia';
GRANT SELECT, INSERT, UPDATE, DELETE ON casas_embrujadas.* TO 'casas_app'@'localhost';
```

2. Crear las tablas y cargar los datos, como root, desde la carpeta del proyecto:

```
mysql -u root -p --default-character-set=utf8mb4 -e "source database/schema.sql"
mysql -u root -p --default-character-set=utf8mb4 -e "source database/data.sql"
```

3. Copiar `.env.example` como `.env` y completar la contraseña de la base y el secreto JWT.

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

Pruebas de integración de los repositorios y de las reglas de negocio. Necesitan MySQL en ejecución con los datos de `database/data.sql` recién cargados. Cada prueba deshace sus cambios al terminar:

```
.\mvnw test "-Dtest=RepositoriosIT"
.\mvnw test "-Dtest=ServiciosIT"
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
