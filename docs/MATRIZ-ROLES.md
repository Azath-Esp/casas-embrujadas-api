# Matriz de acceso por rol

Público: sin token. Las reglas están en `SecurityConfig`; algunas además en `@PreAuthorize` y en la verificación de dueño de los services.

| Endpoints | Público | CLIENTE | VENDEDOR | ADMIN |
|---|---|---|---|---|
| POST /api/v1/auth/registro, POST /api/v1/auth/login | Sí | Sí | Sí | Sí |
| GET /api/v1/auth/me | No | Sí | Sí | Sí |
| GET productos v1 y v2, imágenes, mitos, ficha, mapa y comparar | Sí | Sí | Sí | Sí |
| POST, PUT, DELETE de productos, imágenes, mitos y ficha | No | No | No | Sí |
| GET /api/v1/categorias, GET /api/v1/regiones y comunas | Sí | Sí | Sí | Sí |
| POST, PUT, DELETE /api/v1/categorias | No | No | No | Sí |
| GET, PUT /api/v1/usuarios/me | No | Sí | No | Sí |
| CRUD /api/v1/usuarios | No | No | No | Sí |
| /api/v1/favoritos | No | Sí, los propios | No | No |
| POST /api/v1/reservas, GET /mis-reservas, DELETE /{id} | No | Sí, las propias | No | No |
| GET /api/v1/reservas | No | No | No | Sí |
| POST /api/v1/boletas, GET /mis-boletas | No | Sí, las propias | No | No |
| GET /api/v1/boletas, GET /api/v1/boletas/{id} | No | No | Sí | Sí |
| GET /api/v1/boletas/{id}/pdf | No | Solo las propias | Sí | Sí |
| PUT /api/v1/boletas/{id}/estado | No | No | No | Sí |
| GET /api/v1/dashboard/resumen | No | No | No | Sí |

Sin token, un endpoint protegido responde 401. Con token y sin permiso, 403.
