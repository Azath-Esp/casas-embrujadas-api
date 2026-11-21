package cl.duoc.casasembrujadas.report;

import java.awt.Color;
import java.io.ByteArrayOutputStream;
import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.time.format.DateTimeFormatter;

import cl.duoc.casasembrujadas.dto.response.BoletaResponse;
import cl.duoc.casasembrujadas.dto.response.DetalleBoletaResponse;
import cl.duoc.casasembrujadas.model.EstadoBoleta;
import org.openpdf.text.Chunk;
import org.openpdf.text.Document;
import org.openpdf.text.Element;
import org.openpdf.text.Font;
import org.openpdf.text.PageSize;
import org.openpdf.text.Paragraph;
import org.openpdf.text.Phrase;
import org.openpdf.text.Rectangle;
import org.openpdf.text.pdf.PdfPCell;
import org.openpdf.text.pdf.PdfPTable;
import org.openpdf.text.pdf.PdfWriter;
import org.springframework.stereotype.Service;

@Service
public class BoletaPdfService {

    private static final Color CIRUELA = new Color(0x2A, 0x1A, 0x2F);
    private static final Color AMBAR = new Color(0xB9, 0x7A, 0x1E);
    private static final Color HUESO = new Color(0xF2, 0xEB, 0xDA);
    private static final Color GRIS = new Color(0x5F, 0x5A, 0x52);
    private static final Color LINEA = new Color(0xD8, 0xD2, 0xC4);
    private static final Color ROJO = new Color(0xA8, 0x32, 0x2D);

    private static final Font MARCA = new Font(Font.TIMES_ROMAN, 22, Font.BOLD, HUESO);
    private static final Font LEMA = new Font(Font.TIMES_ROMAN, 10, Font.ITALIC, HUESO);
    private static final Font TITULO = new Font(Font.TIMES_ROMAN, 15, Font.BOLD, CIRUELA);
    private static final Font NORMAL = new Font(Font.HELVETICA, 10, Font.NORMAL, Color.BLACK);
    private static final Font ETIQUETA = new Font(Font.HELVETICA, 9, Font.BOLD, GRIS);
    private static final Font ENCABEZADO_TABLA = new Font(Font.HELVETICA, 10, Font.BOLD, HUESO);
    private static final Font TOTAL = new Font(Font.HELVETICA, 12, Font.BOLD, CIRUELA);
    private static final Font PIE = new Font(Font.HELVETICA, 8, Font.ITALIC, GRIS);
    private static final Font ANULADA = new Font(Font.HELVETICA, 16, Font.BOLD, ROJO);

    private static final DateTimeFormatter FECHA = DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm");

    public byte[] generar(BoletaResponse boleta) {
        ByteArrayOutputStream salida = new ByteArrayOutputStream();
        Document documento = new Document(PageSize.LETTER, 50, 50, 40, 50);
        PdfWriter.getInstance(documento, salida);
        documento.addTitle("Boleta " + numero(boleta));
        documento.addAuthor("Casas Embrujadas");
        documento.open();

        documento.add(encabezado());
        documento.add(espacio(14));
        documento.add(datosBoleta(boleta));
        if (boleta.getEstado() == EstadoBoleta.ANULADA) {
            Paragraph anulada = new Paragraph("BOLETA ANULADA", ANULADA);
            anulada.setAlignment(Element.ALIGN_CENTER);
            anulada.setSpacingBefore(10);
            documento.add(anulada);
        }
        documento.add(espacio(12));
        documento.add(detalle(boleta));
        documento.add(espacio(8));
        documento.add(totales(boleta));
        documento.add(espacio(30));
        Paragraph pie = new Paragraph("Tienda ficticia con fines académicos. Los lugares y leyendas son reales; las ventas no.", PIE);
        pie.setAlignment(Element.ALIGN_CENTER);
        documento.add(pie);

        documento.close();
        return salida.toByteArray();
    }

    public String nombreArchivo(BoletaResponse boleta) {
        return "boleta-" + numero(boleta) + ".pdf";
    }

    private PdfPTable encabezado() {
        PdfPTable tabla = new PdfPTable(1);
        tabla.setWidthPercentage(100);
        Paragraph contenido = new Paragraph();
        contenido.add(new Chunk("Casas Embrujadas", MARCA));
        contenido.add(Chunk.NEWLINE);
        contenido.add(new Chunk("Lugares embrujados reales de Chile", LEMA));
        PdfPCell celda = new PdfPCell();
        celda.addElement(contenido);
        celda.setBackgroundColor(CIRUELA);
        celda.setBorder(Rectangle.BOTTOM);
        celda.setBorderColorBottom(AMBAR);
        celda.setBorderWidthBottom(3);
        celda.setPaddingTop(10);
        celda.setPaddingBottom(14);
        celda.setPaddingLeft(14);
        tabla.addCell(celda);
        return tabla;
    }

    private PdfPTable datosBoleta(BoletaResponse boleta) {
        PdfPTable tabla = new PdfPTable(new float[]{1, 1});
        tabla.setWidthPercentage(100);
        PdfPCell izquierda = celdaSinBorde();
        izquierda.addElement(new Paragraph("Boleta N° " + numero(boleta), TITULO));
        izquierda.addElement(linea("Fecha", boleta.getFecha().format(FECHA)));
        izquierda.addElement(linea("Estado", boleta.getEstado().name()));
        PdfPCell derecha = celdaSinBorde();
        derecha.addElement(new Paragraph("Cliente", TITULO));
        derecha.addElement(linea("Nombre", boleta.getClienteNombre()));
        derecha.addElement(linea("RUN", formatearRun(boleta.getClienteRun())));
        derecha.addElement(linea("Correo", boleta.getClienteCorreo()));
        tabla.addCell(izquierda);
        tabla.addCell(derecha);
        return tabla;
    }

    private PdfPTable detalle(BoletaResponse boleta) {
        PdfPTable tabla = new PdfPTable(new float[]{5, 1, 2});
        tabla.setWidthPercentage(100);
        tabla.setHeaderRows(1);
        tabla.addCell(celdaEncabezado("Casa", Element.ALIGN_LEFT));
        tabla.addCell(celdaEncabezado("Cantidad", Element.ALIGN_CENTER));
        tabla.addCell(celdaEncabezado("Precio", Element.ALIGN_RIGHT));
        for (DetalleBoletaResponse linea : boleta.getDetalles()) {
            tabla.addCell(celdaDetalle(linea.getProductoTitulo(), Element.ALIGN_LEFT));
            tabla.addCell(celdaDetalle(String.valueOf(linea.getCantidad()), Element.ALIGN_CENTER));
            tabla.addCell(celdaDetalle(formatearPesos(linea.getSubtotal()), Element.ALIGN_RIGHT));
        }
        return tabla;
    }

    private PdfPTable totales(BoletaResponse boleta) {
        PdfPTable tabla = new PdfPTable(new float[]{2, 3});
        tabla.setWidthPercentage(60);
        tabla.setHorizontalAlignment(Element.ALIGN_RIGHT);
        filaTotal(tabla, "Neto", formatearPesos(boleta.getNeto()), NORMAL);
        filaTotal(tabla, "IVA 19 %", formatearPesos(boleta.getIva()), NORMAL);
        filaTotal(tabla, "Total", formatearPesos(boleta.getTotal()), TOTAL);
        return tabla;
    }

    private void filaTotal(PdfPTable tabla, String etiqueta, String valor, Font fuente) {
        PdfPCell izquierda = new PdfPCell(new Phrase(etiqueta, fuente));
        PdfPCell derecha = new PdfPCell(new Phrase(valor, fuente));
        derecha.setHorizontalAlignment(Element.ALIGN_RIGHT);
        for (PdfPCell celda : new PdfPCell[]{izquierda, derecha}) {
            celda.setBorder(Rectangle.BOTTOM);
            celda.setBorderColor(LINEA);
            celda.setPadding(5);
            tabla.addCell(celda);
        }
    }

    private PdfPCell celdaEncabezado(String texto, int alineacion) {
        PdfPCell celda = new PdfPCell(new Phrase(texto, ENCABEZADO_TABLA));
        celda.setBackgroundColor(CIRUELA);
        celda.setHorizontalAlignment(alineacion);
        celda.setBorderColor(CIRUELA);
        celda.setPadding(6);
        return celda;
    }

    private PdfPCell celdaDetalle(String texto, int alineacion) {
        PdfPCell celda = new PdfPCell(new Phrase(texto, NORMAL));
        celda.setHorizontalAlignment(alineacion);
        celda.setBorder(Rectangle.BOTTOM);
        celda.setBorderColor(LINEA);
        celda.setPadding(6);
        return celda;
    }

    private PdfPCell celdaSinBorde() {
        PdfPCell celda = new PdfPCell();
        celda.setBorder(Rectangle.NO_BORDER);
        return celda;
    }

    private Paragraph linea(String etiqueta, String valor) {
        Paragraph parrafo = new Paragraph();
        parrafo.add(new Chunk(etiqueta + ": ", ETIQUETA));
        parrafo.add(new Chunk(valor, NORMAL));
        return parrafo;
    }

    private Paragraph espacio(float puntos) {
        Paragraph parrafo = new Paragraph(" ");
        parrafo.setLeading(puntos);
        return parrafo;
    }

    private String numero(BoletaResponse boleta) {
        return String.format("%06d", boleta.getNumero());
    }

    static String formatearPesos(BigDecimal monto) {
        DecimalFormatSymbols simbolos = new DecimalFormatSymbols();
        simbolos.setGroupingSeparator('.');
        return "$" + new DecimalFormat("#,##0", simbolos).format(monto);
    }

    static String formatearRun(String run) {
        String[] partes = run.split("-");
        DecimalFormatSymbols simbolos = new DecimalFormatSymbols();
        simbolos.setGroupingSeparator('.');
        return new DecimalFormat("#,##0", simbolos).format(Long.parseLong(partes[0])) + "-" + partes[1];
    }
}
