package org.example.file.service;

import org.example.file.entity.FileUpload;
import org.example.file.repository.DataRepository;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Service
public class DataService {
    private final DataRepository dataRepository;
    private final String baseUrl = "C:\\Users\\user\\IdeaProjects\\file\\src\\main\\resources\\storage\\";


    public DataService(DataRepository dataRepository) {
        this.dataRepository = dataRepository;
    }

    public void saveData(MultipartFile file) throws IOException {

        FileUpload data = new FileUpload();
        data.setSize(file.getSize());
        data.setName(file.getOriginalFilename());
        data.setContentType(file.getContentType());
        String originalFilename = file.getOriginalFilename();

        String fileName = originalFilename.substring(0, originalFilename.lastIndexOf("."));
        String extension = originalFilename.substring(originalFilename.lastIndexOf("."));
        String url = generateUniqueFileName(fileName, extension);
        System.out.println("Saving file to: " + url);
        File tempFile = new File(url);
        try (OutputStream os = new FileOutputStream(tempFile)) {
            os.write(file.getBytes());
        } catch (IOException e) {
            System.err.println("Error while saving file: " + e.getMessage());
            throw e;
        }
        data.setUrl(url);
        dataRepository.save(data);
    }

    public ResponseEntity<Resource> get(Long id) throws Exception {
        FileUpload fileUpload = dataRepository.findById(id).orElseThrow();
        Path path = Paths.get(fileUpload.getUrl());
        Resource resource = new UrlResource(path.toUri());

        String contentType = Files.probeContentType(path);
        if (contentType == null) {
            contentType = MediaType.APPLICATION_OCTET_STREAM_VALUE;
        }
        return ResponseEntity.ok()
                .contentType(MediaType.parseMediaType(contentType))
                .body(resource);
    }

    private String generateUniqueFileName(String fileName, String extension) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss");
        String timestamp = LocalDateTime.now().format(formatter);

        String uniqueFileName = baseUrl.concat(fileName + "_" + timestamp).concat(extension);
        return uniqueFileName;
    }


}
