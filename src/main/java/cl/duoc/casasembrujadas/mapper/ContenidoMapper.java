package cl.duoc.casasembrujadas.mapper;

import cl.duoc.casasembrujadas.dto.request.FichaParanormalRequest;
import cl.duoc.casasembrujadas.dto.request.ImagenRequest;
import cl.duoc.casasembrujadas.dto.request.MitoRequest;
import cl.duoc.casasembrujadas.dto.response.FichaParanormalResponse;
import cl.duoc.casasembrujadas.dto.response.ImagenResponse;
import cl.duoc.casasembrujadas.dto.response.MitoResponse;
import cl.duoc.casasembrujadas.model.FichaParanormal;
import cl.duoc.casasembrujadas.model.Imagen;
import cl.duoc.casasembrujadas.model.Mito;
import org.springframework.stereotype.Component;


@Component
public class ContenidoMapper {

    public ImagenResponse toResponse(Imagen imagen) {
        return ImagenResponse.builder()
                .idImagen(imagen.getIdImagen())
                .url(imagen.getUrl())
                .orden(imagen.getOrden())
                .autor(imagen.getAutor())
                .licencia(imagen.getLicencia())
                .urlFuente(imagen.getUrlFuente())
                .build();
    }

    public Imagen toModel(ImagenRequest request, Long idProducto) {
        return Imagen.builder()
                .url(request.getUrl())
                .orden(request.getOrden())
                .autor(request.getAutor())
                .licencia(request.getLicencia())
                .urlFuente(request.getUrlFuente())
                .idProducto(idProducto)
                .build();
    }

    public MitoResponse toResponse(Mito mito) {
        return MitoResponse.builder()
                .idMito(mito.getIdMito())
                .titulo(mito.getTitulo())
                .relato(mito.getRelato())
                .anioOrigen(mito.getAnioOrigen())
                .fuente(mito.getFuente())
                .build();
    }

    public Mito toModel(MitoRequest request, Long idProducto) {
        return Mito.builder()
                .titulo(request.getTitulo())
                .relato(request.getRelato())
                .anioOrigen(request.getAnioOrigen())
                .fuente(request.getFuente())
                .idProducto(idProducto)
                .build();
    }

    public FichaParanormalResponse toResponse(FichaParanormal ficha) {
        if (ficha == null) {
            return null;
        }
        return FichaParanormalResponse.builder()
                .tipoEntidad(ficha.getTipoEntidad())
                .horarioActividad(ficha.getHorarioActividad())
                .fenomenosReportados(ficha.getFenomenosReportados())
                .ultimoAvistamiento(ficha.getUltimoAvistamiento())
                .build();
    }

    public FichaParanormal toModel(FichaParanormalRequest request, Long idProducto) {
        return FichaParanormal.builder()
                .idProducto(idProducto)
                .tipoEntidad(request.getTipoEntidad())
                .horarioActividad(request.getHorarioActividad())
                .fenomenosReportados(request.getFenomenosReportados())
                .ultimoAvistamiento(request.getUltimoAvistamiento())
                .build();
    }
}