package com.example.griisa_account_service.util;

import com.example.griisa_account_service.dto.UserCsvRecordDto;
import com.opencsv.bean.CsvToBean;
import com.opencsv.bean.CsvToBeanBuilder;
import org.springframework.web.multipart.MultipartFile;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.Reader;
import java.nio.charset.StandardCharsets;
import java.util.List;

public class CsvUtils {

    public static boolean isCsvFile(MultipartFile file) {
        String contentType = file.getContentType();
        String fileName = file.getOriginalFilename();

        return contentType != null
                && contentType.equals("text/csv")
                && fileName != null
                && fileName.toLowerCase().endsWith(".csv");
    }

    public static List<UserCsvRecordDto> parseCsvFile(MultipartFile file) throws Exception {
        try (
                Reader reader = new BufferedReader(new InputStreamReader(file.getInputStream(), StandardCharsets.UTF_8))
        ) {
            CsvToBean<UserCsvRecordDto> csvToBean = new CsvToBeanBuilder<UserCsvRecordDto>(reader)
                    .withType(UserCsvRecordDto.class)
                    .withIgnoreLeadingWhiteSpace(true)
                    .withSkipLines(1) // Assumes CSV has a header
                    .build();

            return csvToBean.parse();
        } catch (Exception e) {
            throw new Exception("Failed to parse CSV file: " + e.getMessage(), e);
        }
    }
}
