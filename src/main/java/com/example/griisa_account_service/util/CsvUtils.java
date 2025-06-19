package com.example.griisa_account_service.util;

import com.example.griisa_account_service.dto.UserCsvRecordDto;
import com.opencsv.bean.CsvToBean;
import com.opencsv.bean.CsvToBeanBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.multipart.MultipartFile;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.Reader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.function.BiConsumer;

public class CsvUtils {

    public static boolean isCsvFile(MultipartFile file) {
        String contentType = file.getContentType();
        String fileName = file.getOriginalFilename();

        return contentType != null
                && contentType.equals("text/csv")
                && fileName != null
                && fileName.toLowerCase().endsWith(".csv");
    }

    public static List<UserCsvRecordDto> parseCsvFile(MultipartFile file, BiConsumer<Integer, String> onParseError) throws Exception {
        Logger logger = LoggerFactory.getLogger(CsvUtils.class);
        List<UserCsvRecordDto> records = new ArrayList<>();
        try (
                Reader reader = new BufferedReader(new InputStreamReader(file.getInputStream(), StandardCharsets.UTF_8))
        ) {
            CsvToBean<UserCsvRecordDto> csvToBean = new CsvToBeanBuilder<UserCsvRecordDto>(reader)
                    .withType(UserCsvRecordDto.class)
                    .withIgnoreLeadingWhiteSpace(true)
                    .build();

            Iterator<UserCsvRecordDto> iterator = csvToBean.iterator();
            int rowNum = 1;
            while (iterator.hasNext()) {
                try {
                    UserCsvRecordDto record = iterator.next();
                    records.add(record);
                } catch (Exception ex) {
                    logger.error("Failed to parse record at row {}: {}", rowNum, ex.getMessage());
                    if (onParseError != null) {
                        onParseError.accept(rowNum, ex.getMessage());
                    }
                }
                rowNum++;
            }
            return records;
        } catch (Exception e) {
            throw new Exception("Failed to parse CSV file: " + e.getMessage(), e);
        }
    }
}
