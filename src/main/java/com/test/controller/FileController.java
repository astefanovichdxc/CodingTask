package com.test.controller;

import com.test.service.FileService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@RestController
@RequestMapping("/files")
public class FileController {

    private final FileService fileService;

    public FileController(final FileService fileService) {
        this.fileService = fileService;
    }

    @PostMapping
    public ResponseEntity upload(@RequestBody final MultipartFile file) throws IOException {
        return fileService.upload(file);
    }

}
