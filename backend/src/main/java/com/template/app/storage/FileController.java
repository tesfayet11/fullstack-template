package com.template.app.storage;

import com.template.app.storage.dto.FileUploadResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.util.Map;

@RestController
@RequestMapping("/files")
public class FileController {

    private final FileStorageService fileStorageService;

    public FileController(FileStorageService fileStorageService) {
        this.fileStorageService = fileStorageService;
    }

    @PostMapping("/upload")
    public ResponseEntity<FileUploadResponse> upload(@RequestParam("file") MultipartFile file) {
        return ResponseEntity.ok(fileStorageService.upload(file));
    }

    @GetMapping("/{key}")
    public ResponseEntity<Map<String, String>> download(@PathVariable String key) {
        return ResponseEntity.ok(Map.of("url", fileStorageService.getDownloadUrl(key)));
    }
}
