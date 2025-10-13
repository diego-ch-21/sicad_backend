package com.sicad.sicad_backend.service.report.pdf;


import com.itextpdf.text.*;
import com.itextpdf.text.Font;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;
import com.itextpdf.text.pdf.draw.LineSeparator;
import com.sicad.sicad_backend.dto.carga.CargaDetalleResponse;
import com.sicad.sicad_backend.dto.cursoHorario.CursoHorarioDetalleResponse;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.xssf.usermodel.*;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.util.List;
import com.sicad.sicad_backend.dto.asignacion.AsignacionResumenResponse;
import com.sicad.sicad_backend.dto.curso.CursoAsignacionResponse;
import com.sicad.sicad_backend.dto.cursoHorario.HorarioDetalleResponse;
import com.sicad.sicad_backend.dto.docente.DocenteAsignacionResponse;

@Service
public class PdfGeneratorServiceImpl
    implements IPdfGneratorService {

    @Override
    public byte[] generarPdfCargaElectiva(List<DocenteAsignacionResponse> docentes, CargaDetalleResponse carga) {
        try {
            ByteArrayOutputStream out = new ByteArrayOutputStream();
            Document document = new Document(PageSize.A4.rotate());
            PdfWriter.getInstance(document, out);
            document.open();

            agregarEncabezado(document,carga);

            // Fuentes principales
            Font fontEncabezado = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 11, BaseColor.WHITE);
            Font fontDocente = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 12, new BaseColor(44, 62, 80));
            Font fontDatos = FontFactory.getFont(FontFactory.HELVETICA, 10, BaseColor.BLACK);
            Font fontHorario = FontFactory.getFont(FontFactory.HELVETICA, 9, new BaseColor(52, 73, 94));

            // Contenido de docentes
            for (int i = 0; i < docentes.size(); i++) {
                DocenteAsignacionResponse docente = docentes.get(i);
                agregarTablaAsignaciones(document, docente, fontDocente, fontEncabezado, fontDatos, fontHorario);

                if (i < docentes.size() - 1) {
                    agregarSeparador(document);
                }
            }

            agregarFooter(document, docentes.size(), contarAsignaciones(docentes));

            document.close();
            return out.toByteArray();

        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
    private void agregarEncabezado(Document document,CargaDetalleResponse carga) throws Exception {
        PdfPTable headerTable = new PdfPTable(2);
        headerTable.setWidthPercentage(100);
        headerTable.setWidths(new int[]{70, 30});

        // Texto centrado
        PdfPCell textCell = new PdfPCell();
        textCell.setBorder(Rectangle.NO_BORDER);
        textCell.setHorizontalAlignment(Element.ALIGN_CENTER);
        textCell.setVerticalAlignment(Element.ALIGN_MIDDLE);
        textCell.addElement(new Paragraph("UNIVERSIDAD NACIONAL MAYOR DE SAN MARCOS",
                new Font(Font.FontFamily.HELVETICA, 12, Font.BOLD)));
        textCell.addElement(new Paragraph("DEPARTAMENTO ACADÉMICO DE CIENCIAS DE LA COMPUTACIÓN - DACC",
                new Font(Font.FontFamily.HELVETICA, 11)));
        textCell.addElement(new Paragraph("CARGA LECTIVA - SEMESTRE ACADÉMICO "+carga.getCicloAcademico().getNombre(),
                new Font(Font.FontFamily.HELVETICA, 11)));
        headerTable.addCell(textCell);


        // Logo desde recurso local
        InputStream logoStream = getClass().getResourceAsStream("/static/images/UNMSM.png");
        if (logoStream == null) {
            throw new RuntimeException("No se encontró el archivo UNMSM.png en /resources/static/images/");
        }
        byte[] logoBytes = logoStream.readAllBytes();
        Image logo = Image.getInstance(logoBytes);
        logo.scaleToFit(80, 80);
        PdfPCell logoCell = new PdfPCell(logo);
        logoCell.setBorder(Rectangle.NO_BORDER);
        logoCell.setHorizontalAlignment(Element.ALIGN_RIGHT);
        headerTable.addCell(logoCell);

        document.add(headerTable);
        document.add(Chunk.NEWLINE);
    }

    private void agregarTablaAsignaciones(Document document, DocenteAsignacionResponse docente,
                                          Font fontDocente, Font fontEncabezado,
                                          Font fontDatos, Font fontHorario) throws Exception {

        Paragraph infoDocente = new Paragraph();
        infoDocente.add(new Chunk("Docente: ", fontDocente));
        infoDocente.add(new Chunk(docente.getUsuario().getNombre() + " " + docente.getUsuario().getApellido(), fontDocente));
        infoDocente.add(new Chunk("  |  Código: " + docente.getCodigo(),
                FontFactory.getFont(FontFactory.HELVETICA, 11, new BaseColor(127, 140, 141))));
        infoDocente.setSpacingBefore(15);
        infoDocente.setSpacingAfter(10);
        document.add(infoDocente);

        PdfPTable tabla = new PdfPTable(8);
        tabla.setWidthPercentage(100);
        tabla.setWidths(new float[]{0.5f, 3f, 1f, 1f, 1f, 1.5f, 1f, 2f});

        // Encabezados
        String[] encabezados = {"N°", "CURSO", "PLAN", "TIPO", "DIA", "HORARIO","N° HRS","ESCUELA PROFESIONAL"};
        for (String encabezado : encabezados) {
            PdfPCell celda = new PdfPCell(new Phrase(encabezado, fontEncabezado));
            celda.setBackgroundColor(new BaseColor(24, 104, 139));
            celda.setHorizontalAlignment(Element.ALIGN_CENTER);
            celda.setVerticalAlignment(Element.ALIGN_MIDDLE);
            celda.setPadding(8);
            tabla.addCell(celda);
        }

        int contador = 1;
        for (AsignacionResumenResponse asignacion : docente.getAsignaciones()) {
            CursoAsignacionResponse curso = asignacion.getCurso();
            List<CursoHorarioDetalleResponse> horarios = curso.getCursoHorario();
            int rowspan = horarios.size();

            // Columnas fijas con rowspan
            PdfPCell celdaNum = new PdfPCell(new Phrase(String.valueOf(contador), fontDatos));
            celdaNum.setRowspan(rowspan);
            celdaNum.setHorizontalAlignment(Element.ALIGN_CENTER);
            celdaNum.setVerticalAlignment(Element.ALIGN_MIDDLE);
            celdaNum.setPadding(8);
            tabla.addCell(celdaNum);

            PdfPCell celdaAsignatura = new PdfPCell(new Phrase(curso.getAsignatura().getNombre(), fontDatos));
            celdaAsignatura.setRowspan(rowspan);
            celdaAsignatura.setVerticalAlignment(Element.ALIGN_MIDDLE);
            celdaAsignatura.setPadding(8);
            tabla.addCell(celdaAsignatura);

            PdfPCell celdaPlan = new PdfPCell(new Phrase(curso.getPlanDeEstudio().getNombre(), fontDatos));
            celdaPlan.setRowspan(rowspan);
            celdaPlan.setHorizontalAlignment(Element.ALIGN_CENTER);
            celdaPlan.setVerticalAlignment(Element.ALIGN_MIDDLE);
            celdaPlan.setPadding(8);
            tabla.addCell(celdaPlan);

            for (int i = 0; i < horarios.size(); i++) {
                CursoHorarioDetalleResponse horario = horarios.get(i);

                // Celda Tipo de Sesión
                PdfPCell celdaTipo = new PdfPCell(new Phrase(horario.getTipoSesion(), fontHorario));
                celdaTipo.setHorizontalAlignment(Element.ALIGN_CENTER);
                celdaTipo.setVerticalAlignment(Element.ALIGN_MIDDLE);
                celdaTipo.setPadding(4);
                tabla.addCell(celdaTipo);

                // Celda Día de la Semana (en mayúsculas y capitalizado)
                PdfPCell celdaDia = new PdfPCell(new Phrase(capitalize(horario.getDiaSemana().toUpperCase()), fontHorario));
                celdaDia.setHorizontalAlignment(Element.ALIGN_CENTER);
                celdaDia.setVerticalAlignment(Element.ALIGN_MIDDLE);
                celdaDia.setPadding(4);
                tabla.addCell(celdaDia);

                // Celda Horario
                PdfPCell celdaHora = new PdfPCell(new Phrase(formatearHora(horario.getHoraInicio()) + " - " + formatearHora(horario.getHoraFin()), fontHorario));
                celdaHora.setHorizontalAlignment(Element.ALIGN_CENTER);
                celdaHora.setVerticalAlignment(Element.ALIGN_MIDDLE);
                celdaHora.setPadding(4);
                tabla.addCell(celdaHora);

                // Celda Duración
                PdfPCell celdaDuracion = new PdfPCell(new Phrase(horario.getDuracionHoras() + "", fontHorario));
                celdaDuracion.setHorizontalAlignment(Element.ALIGN_CENTER);
                celdaDuracion.setVerticalAlignment(Element.ALIGN_MIDDLE);
                celdaDuracion.setPadding(4);
                tabla.addCell(celdaDuracion);

                // Celda Escuela Profesional (solo la primera fila, con rowspan)
                if (i == 0) {
                    PdfPCell celdaEscuela = new PdfPCell(new Phrase(curso.getEscuela().getNombre(), fontDatos));
                    celdaEscuela.setRowspan(rowspan);
                    celdaEscuela.setHorizontalAlignment(Element.ALIGN_CENTER);
                    celdaEscuela.setVerticalAlignment(Element.ALIGN_MIDDLE);
                    celdaEscuela.setPadding(8);
                    tabla.addCell(celdaEscuela);
                }
            }


            contador++;
        }

        document.add(tabla);
    }

    private void agregarSeparador(Document document) throws Exception {
        LineSeparator separator = new LineSeparator();
        separator.setLineColor(new BaseColor(189, 195, 199));
        separator.setLineWidth(1);
        Paragraph separatorPara = new Paragraph();
        separatorPara.add(separator);
        separatorPara.setSpacingBefore(10);
        document.add(separatorPara);
    }
    private void agregarFooter(Document document, int totalDocentes, int totalAsignaciones) throws Exception {
        Paragraph footer = new Paragraph(
                "\nTotal de docentes: " + totalDocentes + "  |  Total de asignaciones: " + totalAsignaciones,
                FontFactory.getFont(FontFactory.HELVETICA_BOLD, 11, new BaseColor(41, 128, 185))
        );
        footer.setAlignment(Element.ALIGN_CENTER);
        footer.setSpacingBefore(20);
        document.add(footer);
    }



    private String capitalize(String str) {
        if (str == null || str.isEmpty()) return str;
        return str.substring(0, 1).toUpperCase() + str.substring(1).toLowerCase();
    }

    private String formatearHora(String hora) {
        if (hora == null || hora.isEmpty()) return hora;
        // Formato de HH:MM:SS a HH:MM
        return hora.substring(0, 5);
    }

    private int contarAsignaciones(List<DocenteAsignacionResponse> docentes) {
        return docentes.stream()
                .mapToInt(d -> d.getAsignaciones().size())
                .sum();
    }
    @Override
    public byte[] generarExcelCargaElectiva(List<DocenteAsignacionResponse> docentes, CargaDetalleResponse carga) {
        try (XSSFWorkbook workbook = new XSSFWorkbook();
             ByteArrayOutputStream out = new ByteArrayOutputStream()) {

            XSSFSheet sheet = workbook.createSheet("Carga Lectiva");

            // Estilos
            XSSFCellStyle estiloEncabezado = crearEstiloEncabezado(workbook);
            XSSFCellStyle estiloTitulo = crearEstiloTitulo(workbook);
            XSSFCellStyle estiloDocente = crearEstiloDocente(workbook);
            XSSFCellStyle estiloDatos = crearEstiloDatos(workbook);
            XSSFCellStyle estiloCentrado = crearEstiloCentrado(workbook);
            XSSFCellStyle estiloFooter = crearEstiloFooter(workbook);

            int rowNum = 0;

            // Encabezado
            rowNum = agregarEncabezadoExcel(sheet, carga, estiloTitulo, rowNum);
            rowNum += 2; // Espaciado

            // Contenido de docentes
            for (DocenteAsignacionResponse docente : docentes) {
                rowNum = agregarDocenteExcel(sheet, docente, estiloDocente, estiloEncabezado,
                        estiloDatos, estiloCentrado, rowNum);
                rowNum += 2; // Separación entre docentes
            }

            // Footer
            agregarFooterExcel(sheet, docentes, estiloFooter, rowNum);

            // Ajustar anchos de columna
            ajustarColumnasExcel(sheet);

            workbook.write(out);
            return out.toByteArray();

        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    private int agregarEncabezadoExcel(XSSFSheet sheet, CargaDetalleResponse carga,
                                       XSSFCellStyle estiloTitulo, int rowNum) {
        // Título principal
        Row row = sheet.createRow(rowNum++);
        Cell cell = row.createCell(0);
        cell.setCellValue("UNIVERSIDAD NACIONAL MAYOR DE SAN MARCOS");
        cell.setCellStyle(estiloTitulo);
        sheet.addMergedRegion(new CellRangeAddress(rowNum-1, rowNum-1, 0, 7));

        // Departamento
        row = sheet.createRow(rowNum++);
        cell = row.createCell(0);
        cell.setCellValue("DEPARTAMENTO ACADÉMICO DE CIENCIAS DE LA COMPUTACIÓN - DACC");
        cell.setCellStyle(estiloTitulo);
        sheet.addMergedRegion(new CellRangeAddress(rowNum-1, rowNum-1, 0, 7));

        // Semestre
        row = sheet.createRow(rowNum++);
        cell = row.createCell(0);
        cell.setCellValue("CARGA LECTIVA - SEMESTRE ACADÉMICO " + carga.getCicloAcademico().getNombre());
        cell.setCellStyle(estiloTitulo);
        sheet.addMergedRegion(new CellRangeAddress(rowNum-1, rowNum-1, 0, 7));

        return rowNum;
    }

    private int agregarDocenteExcel(XSSFSheet sheet, DocenteAsignacionResponse docente,
                                    XSSFCellStyle estiloDocente, XSSFCellStyle estiloEncabezado,
                                    XSSFCellStyle estiloDatos, XSSFCellStyle estiloCentrado,
                                    int rowNum) {
        // Información del docente
        Row row = sheet.createRow(rowNum++);
        Cell cell = row.createCell(0);
        cell.setCellValue("Docente: " + docente.getUsuario().getNombre() + " " +
                docente.getUsuario().getApellido() + " | Código: " + docente.getCodigo());
        cell.setCellStyle(estiloDocente);
        sheet.addMergedRegion(new CellRangeAddress(rowNum-1, rowNum-1, 0, 7));

        // Encabezados de columna
        row = sheet.createRow(rowNum++);
        String[] encabezados = {"N°", "CURSO", "PLAN", "TIPO", "DIA", "HORARIO", "N° HRS", "ESCUELA PROFESIONAL"};
        for (int i = 0; i < encabezados.length; i++) {
            cell = row.createCell(i);
            cell.setCellValue(encabezados[i]);
            cell.setCellStyle(estiloEncabezado);
        }

        // Datos de asignaciones
        int contador = 1;
        for (AsignacionResumenResponse asignacion : docente.getAsignaciones()) {
            CursoAsignacionResponse curso = asignacion.getCurso();
            List<CursoHorarioDetalleResponse> horarios = curso.getCursoHorario();

            int startRow = rowNum;

            for (int i = 0; i < horarios.size(); i++) {
                row = sheet.createRow(rowNum++);
                CursoHorarioDetalleResponse horario = horarios.get(i);

                // Solo en la primera fila de cada asignación
                if (i == 0) {
                    // N°
                    cell = row.createCell(0);
                    cell.setCellValue(contador);
                    cell.setCellStyle(estiloCentrado);

                    // Curso
                    cell = row.createCell(1);
                    cell.setCellValue(curso.getAsignatura().getNombre());
                    cell.setCellStyle(estiloDatos);

                    // Plan
                    cell = row.createCell(2);
                    cell.setCellValue(curso.getPlanDeEstudio().getNombre());
                    cell.setCellStyle(estiloCentrado);
                }

                // Tipo
                cell = row.createCell(3);
                cell.setCellValue(horario.getTipoSesion());
                cell.setCellStyle(estiloCentrado);

                // Día
                cell = row.createCell(4);
                cell.setCellValue(capitalize(horario.getDiaSemana().toUpperCase()));
                cell.setCellStyle(estiloCentrado);

                // Horario
                cell = row.createCell(5);
                cell.setCellValue(formatearHora(horario.getHoraInicio()) + " - " +
                        formatearHora(horario.getHoraFin()));
                cell.setCellStyle(estiloCentrado);

                // N° Horas
                cell = row.createCell(6);
                cell.setCellValue(horario.getDuracionHoras());
                cell.setCellStyle(estiloCentrado);

                // Escuela (solo en la primera fila)
                if (i == 0) {
                    cell = row.createCell(7);
                    cell.setCellValue(curso.getEscuela().getNombre());
                    cell.setCellStyle(estiloDatos);
                }
            }

            // Combinar celdas para N°, Curso, Plan y Escuela
            if (horarios.size() > 1) {
                sheet.addMergedRegion(new CellRangeAddress(startRow, rowNum - 1, 0, 0)); // N°
                sheet.addMergedRegion(new CellRangeAddress(startRow, rowNum - 1, 1, 1)); // Curso
                sheet.addMergedRegion(new CellRangeAddress(startRow, rowNum - 1, 2, 2)); // Plan
                sheet.addMergedRegion(new CellRangeAddress(startRow, rowNum - 1, 7, 7)); // Escuela
            }

            contador++;
        }

        return rowNum;
    }

    private void agregarFooterExcel(XSSFSheet sheet, List<DocenteAsignacionResponse> docentes,
                                    XSSFCellStyle estiloFooter, int rowNum) {
        Row row = sheet.createRow(rowNum + 1);
        Cell cell = row.createCell(0);
        cell.setCellValue("Total de docentes: " + docentes.size() +
                " | Total de asignaciones: " + contarAsignaciones(docentes));
        cell.setCellStyle(estiloFooter);
        sheet.addMergedRegion(new CellRangeAddress(rowNum + 1, rowNum + 1, 0, 7));
    }

    private void ajustarColumnasExcel(XSSFSheet sheet) {
        sheet.setColumnWidth(0, 2000);   // N°
        sheet.setColumnWidth(1, 10000);  // Curso
        sheet.setColumnWidth(2, 3000);   // Plan
        sheet.setColumnWidth(3, 3000);   // Tipo
        sheet.setColumnWidth(4, 3000);   // Día
        sheet.setColumnWidth(5, 5000);   // Horario
        sheet.setColumnWidth(6, 3000);   // N° Hrs
        sheet.setColumnWidth(7, 8000);   // Escuela
    }

    // Métodos para crear estilos
    private XSSFCellStyle crearEstiloEncabezado(XSSFWorkbook workbook) {
        XSSFCellStyle style = workbook.createCellStyle();
        XSSFFont font = workbook.createFont();
        font.setBold(true);
        font.setColor(IndexedColors.WHITE.getIndex());
        font.setFontHeightInPoints((short) 11);
        style.setFont(font);
        style.setFillForegroundColor(new XSSFColor(new byte[]{24, 104, (byte)139}, null));
        style.setFillPattern(FillPatternType.SOLID_FOREGROUND);
        style.setAlignment(HorizontalAlignment.CENTER);
        style.setVerticalAlignment(VerticalAlignment.CENTER);
        style.setBorderBottom(BorderStyle.THIN);
        style.setBorderTop(BorderStyle.THIN);
        style.setBorderLeft(BorderStyle.THIN);
        style.setBorderRight(BorderStyle.THIN);
        return style;
    }

    private XSSFCellStyle crearEstiloTitulo(XSSFWorkbook workbook) {
        XSSFCellStyle style = workbook.createCellStyle();
        XSSFFont font = workbook.createFont();
        font.setBold(true);
        font.setFontHeightInPoints((short) 12);
        style.setFont(font);
        style.setAlignment(HorizontalAlignment.CENTER);
        style.setVerticalAlignment(VerticalAlignment.CENTER);
        return style;
    }

    private XSSFCellStyle crearEstiloDocente(XSSFWorkbook workbook) {
        XSSFCellStyle style = workbook.createCellStyle();
        XSSFFont font = workbook.createFont();
        font.setBold(true);
        font.setFontHeightInPoints((short) 11);
        font.setColor(new XSSFColor(new byte[]{44, 62, 80}, null));
        style.setFont(font);
        style.setAlignment(HorizontalAlignment.LEFT);
        style.setVerticalAlignment(VerticalAlignment.CENTER);
        return style;
    }

    private XSSFCellStyle crearEstiloDatos(XSSFWorkbook workbook) {
        XSSFCellStyle style = workbook.createCellStyle();
        XSSFFont font = workbook.createFont();
        font.setFontHeightInPoints((short) 10);
        style.setFont(font);
        style.setAlignment(HorizontalAlignment.LEFT);
        style.setVerticalAlignment(VerticalAlignment.CENTER);
        style.setBorderBottom(BorderStyle.THIN);
        style.setBorderTop(BorderStyle.THIN);
        style.setBorderLeft(BorderStyle.THIN);
        style.setBorderRight(BorderStyle.THIN);
        return style;
    }

    private XSSFCellStyle crearEstiloCentrado(XSSFWorkbook workbook) {
        XSSFCellStyle style = workbook.createCellStyle();
        XSSFFont font = workbook.createFont();
        font.setFontHeightInPoints((short) 10);
        style.setFont(font);
        style.setAlignment(HorizontalAlignment.CENTER);
        style.setVerticalAlignment(VerticalAlignment.CENTER);
        style.setBorderBottom(BorderStyle.THIN);
        style.setBorderTop(BorderStyle.THIN);
        style.setBorderLeft(BorderStyle.THIN);
        style.setBorderRight(BorderStyle.THIN);
        return style;
    }

    private XSSFCellStyle crearEstiloFooter(XSSFWorkbook workbook) {
        XSSFCellStyle style = workbook.createCellStyle();
        XSSFFont font = workbook.createFont();
        font.setBold(true);
        font.setFontHeightInPoints((short) 11);
        font.setColor(new XSSFColor(new byte[]{41, (byte)128, (byte)185}, null));
        style.setFont(font);
        style.setAlignment(HorizontalAlignment.CENTER);
        style.setVerticalAlignment(VerticalAlignment.CENTER);
        return style;
    }
}