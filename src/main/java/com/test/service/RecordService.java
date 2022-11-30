package com.test.service;

import com.test.model.RecordDto;
import com.test.model.RecordEntity;
import com.test.repository.RecordRepository;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class RecordService {

    private final RecordRepository recordRepository;

    public RecordService(RecordRepository recordRepository) {
        this.recordRepository = recordRepository;
    }

    public RecordDto get(String primaryKey) {
        return assemble(recordRepository.findByPrimaryKey(primaryKey));
    }

    public ResponseEntity<String> delete(String primaryKey) {
        if (recordRepository.existsByPrimaryKey(primaryKey)) {
                recordRepository.deleteById(primaryKey);
                return ResponseEntity.status(HttpStatus.OK).body("Record " + primaryKey + " deleted");
        } else {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Record " + primaryKey + " not found");
        }
    }

    public ResponseEntity<String> saveAll(List<RecordDto> dtos) {
        List<RecordEntity> entities = dtos.stream().map(this::disassemble).collect(Collectors.toList());
        recordRepository.saveAll(entities);
        return ResponseEntity.status(HttpStatus.OK).body("Records saved");
    }

    private RecordDto assemble(final RecordEntity entity) {
        return new RecordDto(entity.getPrimaryKey(), entity.getName(), entity.getDescription(), entity.getUpdatedTimestamp());
    }

    private RecordEntity disassemble(final RecordDto dto) {
        return new RecordEntity(dto.getPrimaryKey(), dto.getName(), dto.getDescription(), dto.getUpdatedTimestamp());
    }
}
