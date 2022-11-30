package com.test.service;

import com.test.error.BadRequestException;
import com.test.model.RecordDto;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.nio.charset.StandardCharsets;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

@Service
public class FileService {

    private final RecordService recordService;

    public FileService(RecordService recordService) {
        this.recordService = recordService;
    }

    public ResponseEntity<String> upload(MultipartFile file) throws IOException {

        if (!"text/csv".equals(file.getContentType())) throw new BadRequestException("Invalid file type");

        //Charset assumed to be UTF_8
        Reader reader = new BufferedReader(new InputStreamReader(file.getInputStream(), StandardCharsets.UTF_8));
        //CSV file assumed to be RFC 4180 format
        CSVParser parser = new CSVParser(reader, CSVFormat.RFC4180.withHeader());
        Map<String, Integer> headerMap = parser.getHeaderMap();
        Set<String> headerSet = headerMap.keySet();
        if (headerSet.size() != 4) { throw new BadRequestException("Invalid headers size"); }
        //assumed that columns can come in different order
        if (!headerSet.containsAll(Set.of("PRIMARY_KEY",
                                          "NAME",
                                          "DESCRIPTION",
                                          "UPDATED_TIMESTAMP"))) { throw new BadRequestException("Invalid header names"); }
        int primaryKeyIndex = headerMap.get("PRIMARY_KEY");
        int nameIndex = headerMap.get("NAME");
        int descriptionIndex = headerMap.get("DESCRIPTION");
        int updatedTimestampIndex = headerMap.get("UPDATED_TIMESTAMP");
        List<RecordDto> records = new ArrayList<>();
        parser.getRecords().forEach( r -> {
            if (r.size() != 4) return;
            if (r.get(primaryKeyIndex).isEmpty()) return;
            //assumed that ISO 8601 time is without offset
            if (!r.get(updatedTimestampIndex).isEmpty() && !isIsoDate(r.get(updatedTimestampIndex))) return;
            records.add(new RecordDto(r.get(primaryKeyIndex),
                                         r.get(nameIndex),
                                         r.get(descriptionIndex),
                                         r.get(updatedTimestampIndex)));
            });
        recordService.saveAll(records);

        return ResponseEntity.status(HttpStatus.OK).body("Saved " + records.size() + " records");
    }

    private boolean isIsoDate(String dateString) {
        try {
            DateTimeFormatter.ISO_LOCAL_DATE_TIME.parse(dateString);
            return true;
        } catch (java.time.format.DateTimeParseException e) {
            return false;
        }
    }
}
