package cl.duoc.casasembrujadas.dto.response;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;


@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PaginaResponse<T> {

    private List<T> contenido;
    private int pagina;
    private int tamano;
    private long totalElementos;
    private int totalPaginas;
}