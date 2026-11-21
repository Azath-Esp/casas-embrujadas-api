package cl.duoc.casasembrujadas.report;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.List;

import cl.duoc.casasembrujadas.dto.response.BoletaResponse;
import cl.duoc.casasembrujadas.dto.response.DetalleBoletaResponse;
import cl.duoc.casasembrujadas.model.EstadoBoleta;
import org.junit.jupiter.api.Test;
import org.openpdf.text.pdf.PdfReader;
import org.openpdf.text.pdf.parser.PdfTextExtractor;

class BoletaPdfServiceTest {

    private final BoletaPdfService servicio = new BoletaPdfService();

    private BoletaResponse boleta(EstadoBoleta estado) {
        return BoletaResponse.builder()
                .idBoleta(6L)
                .numero(5)
                .fecha(LocalDateTime.of(2026, 11, 17, 19, 30))
                .neto(new BigDecimal("9650000000"))
                .iva(new BigDecimal("1833500000"))
                .total(new BigDecimal("11483500000"))
                .estado(estado)
                .idUsuario(3L)
                .clienteNombre("Camila Reyes Pinto")
                .clienteRun("33333333-3")
                .clienteCorreo("cliente@casasembrujadas.cl")
                .detalles(List.of(
                        new DetalleBoletaResponse(1L, "Casona Dubois", 1, new BigDecimal("1250000000"), new BigDecimal("1250000000")),
                        new DetalleBoletaResponse(6L, "Palacio Cousiño", 1, new BigDecimal("8400000000"), new BigDecimal("8400000000"))))
                .build();
    }

    private String texto(byte[] pdf) throws Exception {
        PdfReader lector = new PdfReader(pdf);
        StringBuilder texto = new StringBuilder();
        PdfTextExtractor extractor = new PdfTextExtractor(lector);
        for (int pagina = 1; pagina <= lector.getNumberOfPages(); pagina++) {
            texto.append(extractor.getTextFromPage(pagina));
        }
        lector.close();
        return texto.toString();
    }

    @Test
    void generaUnPdfValidoYNoVacio() {
        byte[] pdf = servicio.generar(boleta(EstadoBoleta.PAGADA));

        assertTrue(pdf.length > 1000);
        assertEquals("%PDF-", new String(pdf, 0, 5, StandardCharsets.US_ASCII));
    }

    @Test
    void elPdfContieneLosDatosDeLaBoleta() throws Exception {
        String contenido = texto(servicio.generar(boleta(EstadoBoleta.PAGADA)));

        assertTrue(contenido.contains("Boleta N° 000005"), contenido);
        assertTrue(contenido.contains("33.333.333-3"), contenido);
        assertTrue(contenido.contains("Palacio Cousiño"), contenido);
        assertTrue(contenido.contains("$11.483.500.000"), contenido);
        assertTrue(contenido.contains("las ventas no"), contenido);
    }

    @Test
    void unaBoletaAnuladaLoIndica() throws Exception {
        assertTrue(texto(servicio.generar(boleta(EstadoBoleta.ANULADA))).contains("BOLETA ANULADA"));
    }

    @Test
    void formateaPesosYRun() {
        assertEquals("$1.250.000.000", BoletaPdfService.formatearPesos(new BigDecimal("1250000000")));
        assertEquals("1.000.005-K", BoletaPdfService.formatearRun("1000005-K"));
        assertEquals("boleta-000005.pdf", servicio.nombreArchivo(boleta(EstadoBoleta.PAGADA)));
    }
}
