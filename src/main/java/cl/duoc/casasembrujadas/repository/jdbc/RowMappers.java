package cl.duoc.casasembrujadas.repository.jdbc;

import java.time.LocalDate;
import java.time.LocalDateTime;

import cl.duoc.casasembrujadas.model.Boleta;
import cl.duoc.casasembrujadas.model.Categoria;
import cl.duoc.casasembrujadas.model.Comuna;
import cl.duoc.casasembrujadas.model.DetalleBoleta;
import cl.duoc.casasembrujadas.model.EstadoBoleta;
import cl.duoc.casasembrujadas.model.EstadoProducto;
import cl.duoc.casasembrujadas.model.EstadoReserva;
import cl.duoc.casasembrujadas.model.FichaParanormal;
import cl.duoc.casasembrujadas.model.Imagen;
import cl.duoc.casasembrujadas.model.Mito;
import cl.duoc.casasembrujadas.model.Producto;
import cl.duoc.casasembrujadas.model.Region;
import cl.duoc.casasembrujadas.model.Reserva;
import cl.duoc.casasembrujadas.model.RolNombre;
import cl.duoc.casasembrujadas.model.Usuario;
import org.springframework.jdbc.core.RowMapper;

public final class RowMappers {

    private RowMappers() {
    }

    public static final RowMapper<Region> REGION = (rs, fila) -> Region.builder()
            .idRegion(rs.getInt("id_region"))
            .nombre(rs.getString("nombre"))
            .build();

    public static final RowMapper<Comuna> COMUNA = (rs, fila) -> Comuna.builder()
            .idComuna(rs.getInt("id_comuna"))
            .nombre(rs.getString("nombre"))
            .idRegion(rs.getInt("id_region"))
            .regionNombre(rs.getString("region_nombre"))
            .build();

    public static final RowMapper<Categoria> CATEGORIA = (rs, fila) -> Categoria.builder()
            .idCategoria(rs.getInt("id_categoria"))
            .nombre(rs.getString("nombre"))
            .descripcion(rs.getString("descripcion"))
            .build();

    public static final RowMapper<Usuario> USUARIO = (rs, fila) -> Usuario.builder()
            .idUsuario(rs.getLong("id_usuario"))
            .run(rs.getString("run"))
            .nombre(rs.getString("nombre"))
            .apellidos(rs.getString("apellidos"))
            .correo(rs.getString("correo"))
            .contrasena(rs.getString("contrasena"))
            .direccion(rs.getString("direccion"))
            .idComuna(rs.getObject("id_comuna", Integer.class))
            .comunaNombre(rs.getString("comuna_nombre"))
            .regionNombre(rs.getString("region_nombre"))
            .rol(RolNombre.valueOf(rs.getString("rol")))
            .activo(rs.getBoolean("activo"))
            .fechaRegistro(rs.getObject("fecha_registro", LocalDateTime.class))
            .build();

    public static final RowMapper<Producto> PRODUCTO = (rs, fila) -> Producto.builder()
            .idProducto(rs.getLong("id_producto"))
            .titulo(rs.getString("titulo"))
            .descripcion(rs.getString("descripcion"))
            .precio(rs.getBigDecimal("precio"))
            .direccion(rs.getString("direccion"))
            .metrosCuadrados(rs.getInt("metros_cuadrados"))
            .dormitorios(rs.getInt("dormitorios"))
            .banos(rs.getInt("banos"))
            .nivelEmbrujo(rs.getInt("nivel_embrujo"))
            .estado(EstadoProducto.valueOf(rs.getString("estado")))
            .latitud(rs.getBigDecimal("latitud"))
            .longitud(rs.getBigDecimal("longitud"))
            .fechaPublicacion(rs.getObject("fecha_publicacion", LocalDateTime.class))
            .idComuna(rs.getInt("id_comuna"))
            .comunaNombre(rs.getString("comuna_nombre"))
            .idRegion(rs.getInt("id_region"))
            .regionNombre(rs.getString("region_nombre"))
            .imagenPrincipal(rs.getString("imagen_principal"))
            .build();

    public static final RowMapper<Imagen> IMAGEN = (rs, fila) -> Imagen.builder()
            .idImagen(rs.getLong("id_imagen"))
            .url(rs.getString("url"))
            .orden(rs.getInt("orden"))
            .autor(rs.getString("autor"))
            .licencia(rs.getString("licencia"))
            .urlFuente(rs.getString("url_fuente"))
            .idProducto(rs.getLong("id_producto"))
            .build();

    public static final RowMapper<Mito> MITO = (rs, fila) -> Mito.builder()
            .idMito(rs.getLong("id_mito"))
            .titulo(rs.getString("titulo"))
            .relato(rs.getString("relato"))
            .anioOrigen(rs.getObject("anio_origen", Integer.class))
            .fuente(rs.getString("fuente"))
            .idProducto(rs.getLong("id_producto"))
            .build();

    public static final RowMapper<FichaParanormal> FICHA = (rs, fila) -> FichaParanormal.builder()
            .idProducto(rs.getLong("id_producto"))
            .tipoEntidad(rs.getString("tipo_entidad"))
            .horarioActividad(rs.getString("horario_actividad"))
            .fenomenosReportados(rs.getString("fenomenos_reportados"))
            .ultimoAvistamiento(rs.getObject("ultimo_avistamiento", LocalDate.class))
            .build();

    public static final RowMapper<Reserva> RESERVA = (rs, fila) -> Reserva.builder()
            .idReserva(rs.getLong("id_reserva"))
            .idProducto(rs.getLong("id_producto"))
            .productoTitulo(rs.getString("producto_titulo"))
            .idUsuario(rs.getLong("id_usuario"))
            .usuarioNombre(rs.getString("usuario_nombre"))
            .fechaInicio(rs.getObject("fecha_inicio", LocalDateTime.class))
            .fechaExpiracion(rs.getObject("fecha_expiracion", LocalDateTime.class))
            .estado(EstadoReserva.valueOf(rs.getString("estado")))
            .build();

    public static final RowMapper<Boleta> BOLETA = (rs, fila) -> Boleta.builder()
            .idBoleta(rs.getLong("id_boleta"))
            .numero(rs.getInt("numero"))
            .fecha(rs.getObject("fecha", LocalDateTime.class))
            .neto(rs.getBigDecimal("neto"))
            .iva(rs.getBigDecimal("iva"))
            .total(rs.getBigDecimal("total"))
            .estado(EstadoBoleta.valueOf(rs.getString("estado")))
            .idUsuario(rs.getLong("id_usuario"))
            .clienteNombre(rs.getString("cliente_nombre"))
            .clienteRun(rs.getString("cliente_run"))
            .clienteCorreo(rs.getString("cliente_correo"))
            .build();

    public static final RowMapper<DetalleBoleta> DETALLE_BOLETA = (rs, fila) -> DetalleBoleta.builder()
            .idDetalle(rs.getLong("id_detalle"))
            .idBoleta(rs.getLong("id_boleta"))
            .idProducto(rs.getLong("id_producto"))
            .productoTitulo(rs.getString("producto_titulo"))
            .cantidad(rs.getInt("cantidad"))
            .precioUnitario(rs.getBigDecimal("precio_unitario"))
            .subtotal(rs.getBigDecimal("subtotal"))
            .build();
}
