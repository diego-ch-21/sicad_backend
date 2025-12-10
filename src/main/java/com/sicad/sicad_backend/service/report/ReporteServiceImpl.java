package com.sicad.sicad_backend.service.report;


import com.itextpdf.text.*;
import com.itextpdf.text.Font;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;
import com.itextpdf.text.pdf.draw.LineSeparator;
import com.sicad.sicad_backend.dto.CicloCargaCurso.AsignaturaAgrupadaResponse;
import com.sicad.sicad_backend.dto.CicloCargaCurso.CursoAgrupadoResponse;
import com.sicad.sicad_backend.dto.CicloCargaCurso.CursoConDocenteResponse;
import com.sicad.sicad_backend.dto.CicloCargaCurso.HorarioCursoResponse;
import com.sicad.sicad_backend.dto.asignacion.AsignacionCicloResumenResponse;
import com.sicad.sicad_backend.dto.carga.CargaDetalleResponse;
import com.sicad.sicad_backend.dto.docente.DocenteResumenResponse;
import com.sicad.sicad_backend.dto.escuela.EscuelaDetalleResponse;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.xssf.usermodel.*;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

import com.sicad.sicad_backend.dto.asignacion.AsignacionResumenResponse;
import com.sicad.sicad_backend.dto.curso.CursoAsignacionResponse;
import com.sicad.sicad_backend.dto.Horario.HorarioDetalleResponse;
import com.sicad.sicad_backend.dto.docente.DocenteAsignacionResponse;

import static com.sicad.sicad_backend.utils.CodigoGeneratorUtil.generarCodigoAlfanumerico;
import static com.sicad.sicad_backend.utils.CodigoGeneratorUtil.generarCodigoNumerico;
import static com.sicad.sicad_backend.utils.TextUtils.formatearListaComoTexto;

@Service
public class ReporteServiceImpl
    implements IReporteService {

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
        String[] encabezados = {"GRUPO", "CURSO", "PLAN", "TIPO", "DIA", "HORARIO","N° HRS","ESCUELA PROFESIONAL"};
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
            List<HorarioDetalleResponse> horarios = curso.getHorarios();
            int rowspan = horarios.size();

            // Columnas fijas con rowspan
            PdfPCell celdaNum = new PdfPCell(new Phrase(curso.getGrupo(), fontDatos));
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

            PdfPCell celdaPlan = new PdfPCell(new Phrase(formatearListaComoTexto(curso.getPlanDeEstudios()), fontDatos));
            celdaPlan.setRowspan(rowspan);
            celdaPlan.setHorizontalAlignment(Element.ALIGN_CENTER);
            celdaPlan.setVerticalAlignment(Element.ALIGN_MIDDLE);
            celdaPlan.setPadding(8);
            tabla.addCell(celdaPlan);

            for (int i = 0; i < horarios.size(); i++) {
                HorarioDetalleResponse horario = horarios.get(i);

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
    //----------------------------------------------------------------------------------------------------------------
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
        String[] encabezados = {"Grupo°", "CURSO", "PLAN", "TIPO", "DIA", "HORARIO", "N° HRS", "ESCUELA PROFESIONAL"};
        for (int i = 0; i < encabezados.length; i++) {
            cell = row.createCell(i);
            cell.setCellValue(encabezados[i]);
            cell.setCellStyle(estiloEncabezado);
        }

        // Datos de asignaciones
        int contador = 1;
        for (AsignacionResumenResponse asignacion : docente.getAsignaciones()) {
            CursoAsignacionResponse curso = asignacion.getCurso();
            List<HorarioDetalleResponse> horarios = curso.getHorarios();

            int startRow = rowNum;

            for (int i = 0; i < horarios.size(); i++) {
                row = sheet.createRow(rowNum++);
                HorarioDetalleResponse horario = horarios.get(i);

                // Solo en la primera fila de cada asignación
                if (i == 0) {
                    // N°
                    cell = row.createCell(0);
                    cell.setCellValue(curso.getGrupo());
                    cell.setCellStyle(estiloCentrado);

                    // Curso
                    cell = row.createCell(1);
                    cell.setCellValue(curso.getAsignatura().getNombre());
                    cell.setCellStyle(estiloDatos);

                    // Plan
                    cell = row.createCell(2);
                    cell.setCellValue(formatearListaComoTexto(curso.getPlanDeEstudios()));
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

    //----------------------------------------------------------------------


    @Override
    public byte[] generarPdfCursosAgrupadoCarga(List<CursoAgrupadoResponse> data, CargaDetalleResponse carga) {
        try {
            ByteArrayOutputStream out = new ByteArrayOutputStream();
            Document document = new Document(PageSize.A4.rotate());
            PdfWriter.getInstance(document, out);
            document.open();

            // ---------------- ENCABEZADO ----------------
            agregarEncabezado(document, carga,null);

            // ---------------- COLORES ----------------
            BaseColor azulOscuro = new BaseColor(0, 70, 140);
            BaseColor celeste = new BaseColor(173, 216, 230);
            BaseColor grisClaro = new BaseColor(240, 240, 240);

            // ---------------- ESTILOS ----------------
            Font cicloFont = new Font(Font.FontFamily.HELVETICA, 14, Font.BOLD, azulOscuro);
            Font asignaturaFont = new Font(Font.FontFamily.HELVETICA, 12, Font.BOLD, new BaseColor(30, 30, 30));
            Font textoFont = new Font(Font.FontFamily.HELVETICA, 10);

            // ---------------- CONTENIDO ----------------
            for (CursoAgrupadoResponse ciclo : data) {

                Paragraph pciclo = new Paragraph("CICLO " + ciclo.getCiclo(), cicloFont);
                pciclo.setSpacingBefore(15f);
                pciclo.setSpacingAfter(10f);
                document.add(pciclo);

                for (AsignaturaAgrupadaResponse asig : ciclo.getAsignaturas()) {

                    Paragraph pasig = new Paragraph(
                            asig.getNombre() + " (" + asig.getCodigo() + ")",
                            asignaturaFont
                    );
                    pasig.setSpacingAfter(5f);
                    document.add(pasig);

                    // Tabla principal de cursos
                    PdfPTable table = new PdfPTable(6); // Código, Grupo, Plan, Horarios, Escuela, Docente
                    table.setWidthPercentage(100);
                    table.setWidths(new float[]{10f, 8f, 12f, 45f, 15f, 20f}); // columnas ajustadas

                    agregarCeldaHeaderColor(table, "Código", celeste);
                    agregarCeldaHeaderColor(table, "Grupo", celeste);
                    agregarCeldaHeaderColor(table, "Plan", celeste);
                    agregarCeldaHeaderColor(table, "Horarios", celeste);
                    agregarCeldaHeaderColor(table, "Escuela", celeste);
                    agregarCeldaHeaderColor(table, "Docente", celeste);

                    for (CursoConDocenteResponse c : asig.getCursos()) {
                        // Código
                        table.addCell(crearCelda(c.getCodigoCurso(), textoFont));
                        // Grupo
                        table.addCell(crearCelda(c.getGrupo(), textoFont));
                        // Plan de estudios
                        table.addCell(crearCelda(String.join(", ", c.getPlanDeEstudios()), textoFont));

                        // Horarios: subtabla
                        PdfPTable horariosTable = new PdfPTable(3); // Día, Hora, Tipo
                        horariosTable.setWidthPercentage(100);
                        for (HorarioCursoResponse h : c.getHorarios()) {
                            horariosTable.addCell(crearCelda(h.getDia(), textoFont));
                            horariosTable.addCell(crearCelda(h.getHoraInicio() + " - " + h.getHoraFin(), textoFont));
                            horariosTable.addCell(crearCelda(h.getTipoSesion(), textoFont));
                        }
                        PdfPCell horariosCell = new PdfPCell(horariosTable);
                        horariosCell.setPadding(2f);
                        table.addCell(horariosCell);

                        // Escuela
                        table.addCell(crearCelda(c.getEscuela(), textoFont));

                        // Docente
                        String docente = (c.getDocente() != null && !c.getDocente().isEmpty())
                                ? c.getDocente()
                                : "-";
                        table.addCell(crearCelda(docente, textoFont));
                    }

                    document.add(table);
                    document.add(new Paragraph("\n"));
                }
            }

            document.close();
            return out.toByteArray();

        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    @Override
    public byte[] generarPdfCursosAgrupadoCarga(List<CursoAgrupadoResponse> data, CargaDetalleResponse carga, EscuelaDetalleResponse escuela) {
        try {
            ByteArrayOutputStream out = new ByteArrayOutputStream();
            Document document = new Document(PageSize.A4.rotate());
            PdfWriter.getInstance(document, out);
            document.open();

            // ---------------- ENCABEZADO ----------------
            agregarEncabezado(document, carga, escuela);

            // ---------------- COLORES ----------------
            BaseColor azulOscuro = new BaseColor(0, 70, 140);
            BaseColor celeste = new BaseColor(173, 216, 230);

            // ---------------- ESTILOS ----------------
            Font cicloFont = new Font(Font.FontFamily.HELVETICA, 14, Font.BOLD, azulOscuro);
            Font asignaturaFont = new Font(Font.FontFamily.HELVETICA, 12, Font.BOLD, new BaseColor(30, 30, 30));
            Font textoFont = new Font(Font.FontFamily.HELVETICA, 10);

            // ---------------- CONTENIDO ----------------
            for (CursoAgrupadoResponse ciclo : data) {

                // Filtramos asignaturas que tengan al menos un curso de la escuela (si se pasó)
                List<AsignaturaAgrupadaResponse> asignaturasFiltradas = ciclo.getAsignaturas().stream()
                        .map(asig -> {
                            List<CursoConDocenteResponse> cursosFiltrados = asig.getCursos().stream()
                                    .filter(c -> escuela == null || escuela.getNombre().equalsIgnoreCase(c.getEscuela()))
                                    .toList();
                            if (cursosFiltrados.isEmpty()) return null;
                            AsignaturaAgrupadaResponse nuevaAsig = new AsignaturaAgrupadaResponse();
                            nuevaAsig.setNombre(asig.getNombre());
                            nuevaAsig.setCodigo(asig.getCodigo());
                            nuevaAsig.setCursos(cursosFiltrados);
                            return nuevaAsig;
                        })
                        .filter(Objects::nonNull)
                        .toList();

                if (asignaturasFiltradas.isEmpty()) continue; // No hay asignaturas para mostrar

                // CICLO
                Paragraph pciclo = new Paragraph("CICLO " + ciclo.getCiclo(), cicloFont);
                pciclo.setSpacingBefore(15f);
                pciclo.setSpacingAfter(10f);
                document.add(pciclo);

                for (AsignaturaAgrupadaResponse asig : asignaturasFiltradas) {

                    Paragraph pasig = new Paragraph(
                            asig.getNombre() + " (" + asig.getCodigo() + ")",
                            asignaturaFont
                    );
                    pasig.setSpacingAfter(5f);
                    document.add(pasig);

                    // Tabla principal de cursos
                    PdfPTable table = new PdfPTable(6); // Código, Grupo, Plan, Horarios, Escuela, Docente
                    table.setWidthPercentage(100);
                    table.setWidths(new float[]{10f, 8f, 12f, 45f, 15f, 20f});

                    agregarCeldaHeaderColor(table, "Código", celeste);
                    agregarCeldaHeaderColor(table, "Grupo", celeste);
                    agregarCeldaHeaderColor(table, "Plan", celeste);
                    agregarCeldaHeaderColor(table, "Horarios", celeste);
                    agregarCeldaHeaderColor(table, "Escuela", celeste);
                    agregarCeldaHeaderColor(table, "Docente", celeste);

                    for (CursoConDocenteResponse c : asig.getCursos()) {
                        // Código
                        table.addCell(crearCelda(c.getCodigoCurso(), textoFont));
                        // Grupo
                        table.addCell(crearCelda(c.getGrupo(), textoFont));
                        // Plan de estudios
                        table.addCell(crearCelda(String.join(", ", c.getPlanDeEstudios()), textoFont));

                        // Horarios: subtabla
                        PdfPTable horariosTable = new PdfPTable(3); // Día, Hora, Tipo
                        horariosTable.setWidthPercentage(100);
                        for (HorarioCursoResponse h : c.getHorarios()) {
                            horariosTable.addCell(crearCelda(h.getDia(), textoFont));
                            horariosTable.addCell(crearCelda(h.getHoraInicio() + " - " + h.getHoraFin(), textoFont));
                            horariosTable.addCell(crearCelda(h.getTipoSesion(), textoFont));
                        }
                        PdfPCell horariosCell = new PdfPCell(horariosTable);
                        horariosCell.setPadding(2f);
                        table.addCell(horariosCell);

                        // Escuela
                        table.addCell(crearCelda(c.getEscuela(), textoFont));

                        // Docente
                        String docente = (c.getDocente() != null && !c.getDocente().isEmpty())
                                ? c.getDocente()
                                : "-";
                        table.addCell(crearCelda(docente, textoFont));
                    }

                    document.add(table);
                    document.add(new Paragraph("\n"));
                }
            }

            document.close();
            return out.toByteArray();

        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }



// ================= HELPERS =================

    private void agregarCeldaHeaderColor(PdfPTable table, String texto, BaseColor color) {
        Font headerFont = new Font(Font.FontFamily.HELVETICA, 10, Font.BOLD, BaseColor.BLACK);
        PdfPCell cell = new PdfPCell(new Phrase(texto, headerFont));
        cell.setBackgroundColor(color);
        cell.setHorizontalAlignment(Element.ALIGN_CENTER);
        cell.setPadding(4f);
        table.addCell(cell);
    }

    private PdfPCell crearCelda(String texto, Font font) {
        PdfPCell cell = new PdfPCell(new Phrase(texto, font));
        cell.setPadding(4f);
        return cell;
    }
    private void agregarEncabezado(Document document, CargaDetalleResponse carga, EscuelaDetalleResponse escuela) throws Exception {
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
        textCell.addElement(new Paragraph("CARGA LECTIVA - SEMESTRE ACADÉMICO " + carga.getCicloAcademico().getNombre(),
                new Font(Font.FontFamily.HELVETICA, 11)));

        // Si se pasa una escuela válida, se agrega al encabezado
        if (escuela != null) {
            textCell.addElement(new Paragraph("ESCUELA: " + escuela.getNombre(),
                    new Font(Font.FontFamily.HELVETICA, 11, Font.BOLD, BaseColor.DARK_GRAY)));
        }

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

    @Override
    public byte[] generarExcelCursosAgrupadoCarga(List<CursoAgrupadoResponse> data, CargaDetalleResponse carga) {
        try (XSSFWorkbook workbook = new XSSFWorkbook();
             ByteArrayOutputStream out = new ByteArrayOutputStream()) {
            String numeroId = "#"+generarCodigoAlfanumerico(4);

            XSSFSheet sheet = workbook.createSheet(numeroId+" Carga electiva - "+carga.getCicloAcademico().getNombre());

            // Estilos
            XSSFCellStyle estiloTitulo = crearEstiloTitulo(workbook);
            XSSFCellStyle estiloEncabezado = crearEstiloEncabezado(workbook);
            XSSFCellStyle estiloDatos = crearEstiloDatos(workbook);
            XSSFCellStyle estiloCentrado = crearEstiloCentrado(workbook);
            XSSFCellStyle estiloCiclo = workbook.createCellStyle();
            XSSFFont fontCiclo = workbook.createFont();
            fontCiclo.setBold(true);
            fontCiclo.setColor(IndexedColors.WHITE.getIndex()); // Letra blanca
            estiloCiclo.setFont(fontCiclo);
            estiloCiclo.setAlignment(HorizontalAlignment.CENTER);
            estiloCiclo.setVerticalAlignment(VerticalAlignment.CENTER);
            estiloCiclo.setFillForegroundColor(new XSSFColor(new byte[]{64, 64, 64}, null)); // Gris oscuro
            estiloCiclo.setFillPattern(FillPatternType.SOLID_FOREGROUND);

            int rowNum = 0;

            // Encabezado principal
            Row row = sheet.createRow(rowNum++);
            Cell cell = row.createCell(0);
            cell.setCellValue("UNIVERSIDAD NACIONAL MAYOR DE SAN MARCOS");
            cell.setCellStyle(estiloTitulo);
            sheet.addMergedRegion(new CellRangeAddress(rowNum-1, rowNum-1, 0, 5));

            row = sheet.createRow(rowNum++);
            cell = row.createCell(0);
            cell.setCellValue("DEPARTAMENTO ACADÉMICO DE CIENCIAS DE LA COMPUTACIÓN - DACC");
            cell.setCellStyle(estiloTitulo);
            sheet.addMergedRegion(new CellRangeAddress(rowNum-1, rowNum-1, 0, 5));

            row = sheet.createRow(rowNum++);
            cell = row.createCell(0);
            cell.setCellValue("CARGA LECTIVA - SEMESTRE ACADÉMICO " + carga.getCicloAcademico().getNombre());
            cell.setCellStyle(estiloTitulo);
            sheet.addMergedRegion(new CellRangeAddress(rowNum-1, rowNum-1, 0, 5));

            rowNum++; // Espacio

            // Recorremos los ciclos
            for (CursoAgrupadoResponse ciclo : data) {
                row = sheet.createRow(rowNum++);
                cell = row.createCell(0);
                cell.setCellValue("CICLO " + ciclo.getCiclo());
                cell.setCellStyle(estiloCiclo); // <-- aquí usamos el estilo nuevo
                sheet.addMergedRegion(new CellRangeAddress(rowNum-1, rowNum-1, 0, 7)); // ajustar a todas las columnas visibles

                for (AsignaturaAgrupadaResponse asig : ciclo.getAsignaturas()) {
                    row = sheet.createRow(rowNum++);
                    cell = row.createCell(0);
                    cell.setCellValue(asig.getNombre() + " (" + asig.getCodigo() + ")");
                    cell.setCellStyle(estiloDatos);
                    sheet.addMergedRegion(new CellRangeAddress(rowNum-1, rowNum-1, 0, 5));

                    // Encabezados de tabla
                    row = sheet.createRow(rowNum++);
                    String[] encabezados = {"Código Curso", "Grupo", "Plan de Estudios", "Día", "Horario", "Tipo", "Escuela", "Docente"};
                    for (int i = 0; i < encabezados.length; i++) {
                        cell = row.createCell(i);
                        cell.setCellValue(encabezados[i]);
                        cell.setCellStyle(estiloEncabezado);
                    }

                    // Datos de cursos
                    for (CursoConDocenteResponse c : asig.getCursos()) {
                        int startRow = rowNum; // Para combinar celdas de curso
                        for (HorarioCursoResponse h : c.getHorarios()) {
                            row = sheet.createRow(rowNum++);

                            // Código Curso, Grupo, Plan, Escuela y Docente solo en la primera fila del horario
                            if (h.equals(c.getHorarios().get(0))) {
                                cell = row.createCell(0);
                                cell.setCellValue(c.getCodigoCurso());
                                cell.setCellStyle(estiloCentrado);

                                cell = row.createCell(1);
                                cell.setCellValue(c.getGrupo());
                                cell.setCellStyle(estiloCentrado);

                                cell = row.createCell(2);
                                cell.setCellValue(String.join(", ", c.getPlanDeEstudios()));
                                cell.setCellStyle(estiloCentrado);

                                cell = row.createCell(6);
                                cell.setCellValue(c.getEscuela());
                                cell.setCellStyle(estiloCentrado);

                                cell = row.createCell(7);
                                String docente = (c.getDocente() != null && !c.getDocente().isEmpty())
                                        ? c.getDocente()
                                        : "-";
                                cell.setCellValue(docente);
                                cell.setCellStyle(estiloDatos);
                            }

                            // Día
                            cell = row.createCell(3);
                            cell.setCellValue(h.getDia());
                            cell.setCellStyle(estiloCentrado);

                            // Horario
                            cell = row.createCell(4);
                            cell.setCellValue(h.getHoraInicio() + " - " + h.getHoraFin());
                            cell.setCellStyle(estiloCentrado);

                            // Tipo
                            cell = row.createCell(5);
                            cell.setCellValue(h.getTipoSesion());
                            cell.setCellStyle(estiloCentrado);
                        }

                        // Combinar celdas de Código, Grupo, Plan, Escuela y Docente si hay varios horarios
                        if (c.getHorarios().size() > 1) {
                            sheet.addMergedRegion(new CellRangeAddress(startRow, rowNum-1, 0, 0)); // Código
                            sheet.addMergedRegion(new CellRangeAddress(startRow, rowNum-1, 1, 1)); // Grupo
                            sheet.addMergedRegion(new CellRangeAddress(startRow, rowNum-1, 2, 2)); // Plan
                            sheet.addMergedRegion(new CellRangeAddress(startRow, rowNum-1, 6, 6)); // Escuela
                            sheet.addMergedRegion(new CellRangeAddress(startRow, rowNum-1, 7, 7)); // Docente
                        }
                    }

                    rowNum++; // Espacio entre asignaturas
                }

                rowNum++; // Espacio entre ciclos
            }

            // Ajuste de columnas
            sheet.setColumnWidth(0, 4000);  // Código
            sheet.setColumnWidth(1, 3000);  // Grupo
            sheet.setColumnWidth(2, 4000);  // Plan
            sheet.setColumnWidth(3, 4000);  // Día
            sheet.setColumnWidth(4, 4000);  // Horario
            sheet.setColumnWidth(5, 3000);  // Tipo
            sheet.setColumnWidth(6, 6000);  // Escuela
            sheet.setColumnWidth(7, 10000);  // Docente

            workbook.write(out);
            return out.toByteArray();

        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }


    @Override
    public byte[] generarExcelCursosAgrupadoCarga(List<CursoAgrupadoResponse> data, CargaDetalleResponse carga, EscuelaDetalleResponse escuela) {
        try (XSSFWorkbook workbook = new XSSFWorkbook();
             ByteArrayOutputStream out = new ByteArrayOutputStream()) {
            String numeroId = "#"+generarCodigoAlfanumerico(4);

            XSSFSheet sheet = workbook.createSheet(numeroId+" Carga electiva - "+carga.getCicloAcademico().getNombre());

            // Estilos
            XSSFCellStyle estiloTitulo = crearEstiloTitulo(workbook);
            XSSFCellStyle estiloEncabezado = crearEstiloEncabezado(workbook);
            XSSFCellStyle estiloDatos = crearEstiloDatos(workbook);
            XSSFCellStyle estiloCentrado = crearEstiloCentrado(workbook);
            // Agregar después de crear los demás estilos
            XSSFCellStyle estiloCiclo = workbook.createCellStyle();
            XSSFFont fontCiclo = workbook.createFont();
            fontCiclo.setBold(true);
            fontCiclo.setColor(IndexedColors.WHITE.getIndex()); // Letra blanca
            estiloCiclo.setFont(fontCiclo);
            estiloCiclo.setAlignment(HorizontalAlignment.CENTER);
            estiloCiclo.setVerticalAlignment(VerticalAlignment.CENTER);
            estiloCiclo.setFillForegroundColor(new XSSFColor(new byte[]{64, 64, 64}, null)); // Gris oscuro
            estiloCiclo.setFillPattern(FillPatternType.SOLID_FOREGROUND);


            int rowNum = 0;

            // Encabezado principal
            Row row = sheet.createRow(rowNum++);
            Cell cell = row.createCell(0);
            cell.setCellValue("UNIVERSIDAD NACIONAL MAYOR DE SAN MARCOS");
            cell.setCellStyle(estiloTitulo);
            sheet.addMergedRegion(new CellRangeAddress(rowNum-1, rowNum-1, 0, 7));

            row = sheet.createRow(rowNum++);
            cell = row.createCell(0);
            cell.setCellValue("DEPARTAMENTO ACADÉMICO DE CIENCIAS DE LA COMPUTACIÓN - DACC");
            cell.setCellStyle(estiloTitulo);
            sheet.addMergedRegion(new CellRangeAddress(rowNum-1, rowNum-1, 0, 7));

            row = sheet.createRow(rowNum++);
            cell = row.createCell(0);
            cell.setCellValue("CARGA LECTIVA - SEMESTRE ACADÉMICO " + carga.getCicloAcademico().getNombre());
            cell.setCellStyle(estiloTitulo);
            sheet.addMergedRegion(new CellRangeAddress(rowNum-1, rowNum-1, 0, 7));

            row = sheet.createRow(rowNum++);
            cell = row.createCell(0);
            cell.setCellValue("ESCUELA: " + escuela.getNombre());
            cell.setCellStyle(estiloTitulo);
            sheet.addMergedRegion(new CellRangeAddress(rowNum-1, rowNum-1, 0, 7));

            rowNum++; // Espacio

            // Recorremos los ciclos
            for (CursoAgrupadoResponse ciclo : data) {
                row = sheet.createRow(rowNum++);
                cell = row.createCell(0);
                cell.setCellValue("CICLO " + ciclo.getCiclo());
                cell.setCellStyle(estiloCiclo); // <-- aquí usamos el estilo nuevo
                sheet.addMergedRegion(new CellRangeAddress(rowNum-1, rowNum-1, 0, 7)); // ajustar a todas las columnas visibles

                for (AsignaturaAgrupadaResponse asig : ciclo.getAsignaturas()) {
                    // Filtramos cursos solo de la escuela indicada
                    List<CursoConDocenteResponse> cursosFiltrados = asig.getCursos().stream()
                            .filter(c -> c.getEscuela().equalsIgnoreCase(escuela.getNombre()))
                            .toList();

                    if (cursosFiltrados.isEmpty()) continue; // Si no hay cursos en esa escuela, se salta la asignatura

                    row = sheet.createRow(rowNum++);
                    cell = row.createCell(0);
                    cell.setCellValue(asig.getNombre() + " (" + asig.getCodigo() + ")");
                    cell.setCellStyle(estiloDatos);
                    sheet.addMergedRegion(new CellRangeAddress(rowNum-1, rowNum-1, 0, 7));

                    // Encabezados de tabla
                    row = sheet.createRow(rowNum++);
                    String[] encabezados = {"Código Curso", "Grupo", "Plan de Estudios", "Día", "Horario", "Tipo", "Escuela", "Docente"};
                    for (int i = 0; i < encabezados.length; i++) {
                        cell = row.createCell(i);
                        cell.setCellValue(encabezados[i]);
                        cell.setCellStyle(estiloEncabezado);
                    }

                    // Datos de cursos filtrados
                    for (CursoConDocenteResponse c : cursosFiltrados) {
                        int startRow = rowNum;
                        for (HorarioCursoResponse h : c.getHorarios()) {
                            row = sheet.createRow(rowNum++);

                            // Código Curso, Grupo, Plan, Escuela y Docente solo en la primera fila del horario
                            if (h.equals(c.getHorarios().get(0))) {
                                cell = row.createCell(0);
                                cell.setCellValue(c.getCodigoCurso());
                                cell.setCellStyle(estiloCentrado);

                                cell = row.createCell(1);
                                cell.setCellValue(c.getGrupo());
                                cell.setCellStyle(estiloCentrado);

                                cell = row.createCell(2);
                                cell.setCellValue(String.join(", ", c.getPlanDeEstudios()));
                                cell.setCellStyle(estiloCentrado);

                                cell = row.createCell(6);
                                cell.setCellValue(c.getEscuela());
                                cell.setCellStyle(estiloCentrado);

                                cell = row.createCell(7);
                                String docente = (c.getDocente() != null && !c.getDocente().isEmpty())
                                        ? c.getDocente()
                                        : "-";
                                cell.setCellValue(docente);
                                cell.setCellStyle(estiloDatos);
                            }

                            // Día
                            cell = row.createCell(3);
                            cell.setCellValue(h.getDia());
                            cell.setCellStyle(estiloCentrado);

                            // Horario
                            cell = row.createCell(4);
                            cell.setCellValue(h.getHoraInicio() + " - " + h.getHoraFin());
                            cell.setCellStyle(estiloCentrado);

                            // Tipo
                            cell = row.createCell(5);
                            cell.setCellValue(h.getTipoSesion());
                            cell.setCellStyle(estiloCentrado);
                        }

                        // Combinar celdas de Código, Grupo, Plan, Escuela y Docente si hay varios horarios
                        if (c.getHorarios().size() > 1) {
                            sheet.addMergedRegion(new CellRangeAddress(startRow, rowNum-1, 0, 0)); // Código
                            sheet.addMergedRegion(new CellRangeAddress(startRow, rowNum-1, 1, 1)); // Grupo
                            sheet.addMergedRegion(new CellRangeAddress(startRow, rowNum-1, 2, 2)); // Plan
                            sheet.addMergedRegion(new CellRangeAddress(startRow, rowNum-1, 6, 6)); // Escuela
                            sheet.addMergedRegion(new CellRangeAddress(startRow, rowNum-1, 7, 7)); // Docente
                        }
                    }

                    rowNum++; // Espacio entre asignaturas
                }

                rowNum++; // Espacio entre ciclos
            }

            // Ajuste de columnas
            sheet.setColumnWidth(0, 4000);  // Código
            sheet.setColumnWidth(1, 3000);  // Grupo
            sheet.setColumnWidth(2, 4000);  // Plan
            sheet.setColumnWidth(3, 4000);  // Día
            sheet.setColumnWidth(4, 4000);  // Horario
            sheet.setColumnWidth(5, 3000);  // Tipo
            sheet.setColumnWidth(6, 6000);  // Escuela
            sheet.setColumnWidth(7, 10000);  // Docente

            workbook.write(out);
            return out.toByteArray();

        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }



}