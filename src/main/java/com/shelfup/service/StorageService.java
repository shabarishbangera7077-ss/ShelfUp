package com.shelfup.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.UUID;

@Service
public class StorageService {
    private final Path uploadDir;

    public StorageService(@Value("${storage.upload-dir:uploads}") String uploadDir) throws IOException {
        this.uploadDir = Path.of(uploadDir);
        Files.createDirectories(this.uploadDir);
    }

    public String saveFile(MultipartFile file) throws IOException {
        String originalName = file.getOriginalFilename();
        String extension = "";
        if (originalName != null && originalName.contains(".")) {
            extension = originalName.substring(originalName.lastIndexOf('.'));
        }
        String safeName = UUID.randomUUID() + extension;
        Path targetPath = uploadDir.resolve(safeName);
        Files.copy(file.getInputStream(), targetPath);
        return targetPath.toString();
    }
}
