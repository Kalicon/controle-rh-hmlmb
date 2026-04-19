package com.hmlmb.rh.service;

import com.hmlmb.rh.model.Requerimento;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.InputStream;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Iterator;

@Service
public class CargaRequerimentoService {

    private final RequerimentoService requerimentoService;

    public CargaRequerimentoService(RequerimentoService requerimentoService) {
        this.requerimentoService = requerimentoService;
    }

    public int carregarRequerimentos(MultipartFile file) throws Exception {
        int count = 0;
        try (InputStream inputStream = file.getInputStream()) {
            XSSFWorkbook workbook = new XSSFWorkbook(inputStream);
            XSSFSheet sheet = workbook.getSheetAt(0);

            Iterator<Row> rowIterator = sheet.iterator();
            if (rowIterator.hasNext()) {
                rowIterator.next(); // Pula o cabeçalho
            }

            while (rowIterator.hasNext()) {
                Row row = rowIterator.next();
                
                // Colunas esperadas: nome(0), rs(1), pv(2), cargo(3), regime juridico(4), 
                // data de entrada(5), quantidade de dias(6), data de inicio(7), data de termino(8), observações(9)
                String nome = getStringValue(row.getCell(0));
                String rs = getStringValue(row.getCell(1));

                if (nome != null && !nome.isEmpty() && rs != null && !rs.isEmpty()) {
                    Requerimento req = new Requerimento();
                    req.setNome(nome);
                    req.setRs(rs);
                    req.setPv(getIntegerValue(row.getCell(2)));
                    req.setCargo(getStringValue(row.getCell(3)));
                    req.setRegimeJuridico(getStringValue(row.getCell(4)));
                    req.setDataEntrada(getLocalDateValue(row.getCell(5)));
                    req.setQuantidadeDias(getIntegerValue(row.getCell(6)));
                    req.setDataInicio(getLocalDateValue(row.getCell(7)));
                    
                    // A data de termino (8) eh calculada pelo @PrePersist, mas se veio preenchida, injetamos tambem
                    LocalDate termino = getLocalDateValue(row.getCell(8));
                    if (termino != null) {
                        req.setDataTermino(termino);
                    }
                    
                    req.setObservacoes(getStringValue(row.getCell(9)));

                    // Assumimos que a Carga Excel é para o ano atual, ou o ano da data de início
                    if (req.getDataInicio() != null) {
                        req.setExercicio(req.getDataInicio().getYear());
                    } else {
                        req.setExercicio(LocalDate.now().getYear());
                    }

                    requerimentoService.salvar(req);
                    count++;
                }
            }
        }
        return count;
    }

    private String getStringValue(Cell cell) {
        if (cell == null) return null;
        switch (cell.getCellType()) {
            case STRING:
                return cell.getStringCellValue();
            case NUMERIC:
                return String.valueOf((long) cell.getNumericCellValue());
            default:
                return null;
        }
    }

    private Integer getIntegerValue(Cell cell) {
        if (cell == null) return null;
        if (cell.getCellType() == CellType.NUMERIC) {
            return (int) cell.getNumericCellValue();
        } else if (cell.getCellType() == CellType.STRING) {
            try {
                return Integer.parseInt(cell.getStringCellValue());
            } catch (NumberFormatException e) {
                return null;
            }
        }
        return null;
    }

    private LocalDate getLocalDateValue(Cell cell) {
        if (cell == null) return null;
        if (cell.getCellType() == CellType.NUMERIC) {
            return cell.getDateCellValue().toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
        } else if (cell.getCellType() == CellType.STRING) {
            try {
                return LocalDate.parse(cell.getStringCellValue());
            } catch (Exception e) {
                return null;
            }
        }
        return null;
    }
}
