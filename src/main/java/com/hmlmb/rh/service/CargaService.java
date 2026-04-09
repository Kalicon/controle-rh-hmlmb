package com.hmlmb.rh.service;

import com.hmlmb.rh.model.Funcionario;
import com.hmlmb.rh.repository.FuncionarioRepository;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.InputStream;
import java.time.LocalDate; // Importar LocalDate
import java.time.ZoneId; // Importar ZoneId
import java.util.Iterator;
import java.util.Optional;

@Service
public class CargaService {

    private final FuncionarioRepository funcionarioRepository;

    public CargaService(FuncionarioRepository funcionarioRepository) {
        this.funcionarioRepository = funcionarioRepository;
    }

    public void carregarFuncionarios(MultipartFile file) throws Exception {
        try (InputStream inputStream = file.getInputStream()) {
            XSSFWorkbook workbook = new XSSFWorkbook(inputStream);
            XSSFSheet sheet = workbook.getSheetAt(0);

            Iterator<Row> rowIterator = sheet.iterator();
            if (rowIterator.hasNext()) {
                rowIterator.next(); // Pula o cabeçalho
            }

            while (rowIterator.hasNext()) {
                Row row = rowIterator.next();
                String rs = getStringValue(row.getCell(1));

                if (rs != null && !rs.isEmpty()) {
                    Optional<Funcionario> funcionarioExistente = funcionarioRepository.findByRs(rs);
                    if (funcionarioExistente.isEmpty()) {
                        Funcionario funcionario = new Funcionario();
                        funcionario.setNome(getStringValue(row.getCell(0)));
                        funcionario.setRs(rs);
                        funcionario.setPv(getIntegerValue(row.getCell(2)));
                        funcionario.setCargo(getStringValue(row.getCell(3)));
                        funcionario.setRegimeJuridico(getStringValue(row.getCell(4)));
                        funcionario.setDataEntrada(getLocalDateValue(row.getCell(5))); // Novo campo adicionado
                        funcionarioRepository.save(funcionario);
                    }
                }
            }
        }
    }

    private String getStringValue(Cell cell) {
        if (cell == null) {
            return null;
        }
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
        if (cell == null) {
            return null;
        }
        if (cell.getCellType() == CellType.NUMERIC) {
            return (int) cell.getNumericCellValue();
        }
        return null;
    }

    // Novo método para obter valor de data
    private LocalDate getLocalDateValue(Cell cell) {
        if (cell == null) {
            return null;
        }
        if (cell.getCellType() == CellType.NUMERIC) {
            // Apache POI armazena datas como números (dias desde 1900)
            return cell.getDateCellValue().toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
        } else if (cell.getCellType() == CellType.STRING) {
            // Tenta parsear a string como data, se for o caso
            try {
                return LocalDate.parse(cell.getStringCellValue()); // Assumindo formato ISO_LOCAL_DATE (YYYY-MM-DD)
            } catch (Exception e) {
                // Logar ou tratar erro de parse, se necessário
                return null;
            }
        }
        return null;
    }
}
