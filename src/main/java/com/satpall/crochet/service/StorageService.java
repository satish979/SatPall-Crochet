package com.satpall.crochet.service;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

@Service
public class StorageService {

    private final Path uploadDir;

    public StorageService(@Value("${product.upload.dir:uploads}") String uploadDir) {
        this.uploadDir = Paths.get(uploadDir).toAbsolutePath().normalize();
        try {
            Files.createDirectories(this.uploadDir);
        } catch (IOException e) {
            throw new IllegalStateException("Could not create upload directory", e);
        }
    }

    public Path getUploadDir() {
        return uploadDir;
    }

    public String storeFile(MultipartFile file) {
        if (file == null || file.isEmpty()) {
            return null;
        }

        String originalFilename = StringUtils.cleanPath(file.getOriginalFilename());
        if (originalFilename.contains("..")) {
            throw new IllegalArgumentException("Invalid file path: " + originalFilename);
        }

        String extension = "";
        int i = originalFilename.lastIndexOf('.');
        if (i > 0) {
            extension = originalFilename.substring(i);
        }
        String filename = UUID.randomUUID().toString() + extension;
        Path targetPath = uploadDir.resolve(filename);

        try {
            Files.copy(file.getInputStream(), targetPath);
        } catch (IOException e) {
            throw new IllegalStateException("Failed to store file " + filename, e);
        }

        return filename;
    }

    public void deleteFile(String fileName) {
        if (fileName == null || fileName.trim().isEmpty()) {
            return;
        }

        Path path = uploadDir.resolve(fileName).normalize();
        try {
            Files.deleteIfExists(path);
        } catch (IOException e) {
            // ignore deletion failure to keep admin operations safe
        }
    }
}
