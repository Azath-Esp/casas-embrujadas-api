# Pruebas del backend

## Unitarias: 82, sin base de datos

`.\mvnw test`

| Clase | Pruebas | Qué cubre |
|---|---|---|
| BoletaServiceImplTest | 10 | Montos, IVA redondeado, casas vendidas, inactivas o reservadas por otro, reserva convertida, dueño, anulación |
| ReservaServiceImplTest | 7 | 24 horas, máximo dos, casa no disponible, reserva simultánea, cancelación por el dueño, expiración |
| ProductoServiceImplTest | 8 | Tope de página, estados permitidos, desactivar en vez de borrar, máquina de estados |
| CategoriaServiceImplTest | 4 | Nombre único, no eliminar con casas |
| ContenidoYFavoritoServiceTest | 4 | Última imagen, imagen de otra casa, favoritos de casas visibles |
| UsuarioYAuthServiceTest | 6 | Hash de la contraseña, datos únicos, protección del admin, login y token |
| DashboardYExploracionServiceTest | 4 | Doce meses y cuatro estados, comparador, región inexistente |
| ReservaSchedulerTest | 2 | Delega en el service, sobrevive a una base caída |
| PermisosPorRolWebMvcTest | 14 | 200, 204, 401 y 403 por rol con @WebMvcTest y @WithMockUser |
| JwtServiceTest | 5 | Generar, vencido, otra clave, modificado, secreto corto |
| BoletaPdfServiceTest | 4 | PDF válido, no vacío, con los datos, anulada |
| OpenApiDocumentacionTest | 3 | Documentación, grupos y endpoints protegidos |
| RunUtilsTest | 4 | Dígito verificador del RUN |
| RegistroRequestValidacionTest | 3 | Validaciones del registro |
| UsuarioMapperTest | 3 | Sin contraseña en la respuesta, registro como CLIENTE |
| CasasEmbrujadasApiApplicationTests | 1 | El contexto arranca |

## Integración: 46, con la rama dev de Neon y los datos iniciales

`.\mvnw test "-Dtest=RepositoriosIT,ServiciosIT,ControllersIT,SeguridadIT,ExtrasIT"`

| Clase | Pruebas | Qué cubre |
|---|---|---|
| RepositoriosIT | 9 | SQL de los nueve repositorios y restricciones de la base |
| ServiciosIT | 14 | Reglas de negocio de punta a punta |
| ControllersIT | 9 | Códigos HTTP, Location y formato de error con tokens reales |
| SeguridadIT | 9 | Login, registro, 401, 403 por rol, token alterado, CORS |
| ExtrasIT | 5 | Dashboard, mapa, comparador y PDF |

## Postman: 64 solicitudes, 91 comprobaciones

`postman/casas-embrujadas.postman_collection.json` con el entorno `postman/casas-embrujadas-local.postman_environment.json`. Ejecutar completa y en orden.
