package com.test.controller;

import com.test.model.RecordDto;
import com.test.service.RecordService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/records")
public class RecordController {

    private final RecordService recordService;

    public RecordController(RecordService recordService) {
        this.recordService = recordService;
    }

    @GetMapping("/{primaryKey}")
    public RecordDto get(@PathVariable final String primaryKey){
        return recordService.get(primaryKey);
    }

    @DeleteMapping("/{primaryKey}")
    public ResponseEntity<String> delete(@PathVariable final String primaryKey) {
        return recordService.delete(primaryKey);
    }

}
