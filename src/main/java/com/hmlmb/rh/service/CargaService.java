package com.hmlmb.rh.service;

import com.hmlmb.rh.model.Funcionario;
import com.hmlmb.rh.repository.FuncionarioRepository;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.InputStream;
import java.util.Iterator;

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
                Funcionario funcionario = new Funcionario();

                funcionario.setNome(getStringValue(row.getCell(0)));
                funcionario.setRs(getStringValue(row.getCell(1)));
                funcionario.setPv(getIntegerValue(row.getCell(2)));
                funcionario.setCargo(getStringValue(row.getCell(3)));
                funcionario.setRegimeJuridico(getStringValue(row.getCell(4)));

                // Evita salvar funcionários com RS duplicado ou nulo
                if (funcionario.getRs() != null && !funcionario.getRs().isEmpty()) {
                    funcionarioRepository.save(funcionario);
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
        if (cell.getCellType() == Cell.CELL_TYPE_NUMERIC) {
            return (int) cell.getNumericCellValue();
        }
        return null;
    }
}
