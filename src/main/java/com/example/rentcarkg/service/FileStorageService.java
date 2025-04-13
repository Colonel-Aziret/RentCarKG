package com.example.rentcarkg.service;

import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

@Service
public class FileStorageService {

    private static final String UPLOAD_DIR = "uploads/images/";

    public String saveFile(MultipartFile file) {
        try {
            Path uploadPath = Paths.get(UPLOAD_DIR);
            if (!Files.exists(uploadPath)) {
                Files.createDirectories(uploadPath);
            }

            String filename = System.currentTimeMillis() + "_" + file.getOriginalFilename();
            Path filePath = uploadPath.resolve(filename);
            Files.write(filePath, file.getBytes());

            // Возвращаем относительный путь или URL, если используешь CDN / static
            return "/uploads/images/" + filename;

        } catch (IOException e) {
            throw new RuntimeException("Ошибка при сохранении файла", e);
        }
    }
}