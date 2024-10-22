package com.example.pdf.entity;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

@Service
public class PdfService {

    private final TemplateEngine templateEngine;
    private final String storageDirectory = "pdf-storage/";  
    @Autowired
    public PdfService(TemplateEngine templateEngine) {
        this.templateEngine = templateEngine;

        
        File dir = new File(storageDirectory);
        if (!dir.exists()) {
            dir.mkdirs();  
        }
    }

  
    public String generateAndStorePdf(PdfRequest pdfRequest) {
        try {
            
            String fileName = pdfRequest.getSeller().replaceAll(" ", "_") + "_invoice.pdf"; // Use underscores instead of spaces
            File file = new File(storageDirectory + fileName);
            if (file.exists()) {
                return file.getAbsolutePath();  
            }

          
            if (pdfRequest.getItems() == null || pdfRequest.getItems().isEmpty()) {
                throw new IllegalArgumentException("Items cannot be null or empty.");
            }

            
            Context context = new Context();
            context.setVariable("data", pdfRequest);

            
            String htmlContent = templateEngine.process("invoice-template", context);

            
            try (FileOutputStream fos = new FileOutputStream(file)) {
                
                PdfGenerator.generatePdfFromHtml(htmlContent, fos);
            }

            return file.getAbsolutePath();  
        } catch (IOException e) {
            throw new RuntimeException("Error generating PDF: " + e.getMessage(), e);
        } catch (IllegalArgumentException e) {
            throw new RuntimeException("Invalid input: " + e.getMessage(), e);
        } catch (Exception e) {
            throw new RuntimeException("An unexpected error occurred while generating PDF", e);
        }
    }

    
    public ResponseEntity<?> downloadPdf(String fileName) {
        try {
            File file = new File(storageDirectory + fileName);
            if (!file.exists()) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("File not found");
            }

            byte[] pdfBytes = Files.readAllBytes(Paths.get(file.getAbsolutePath()));
            return ResponseEntity.ok()
                    .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=" + fileName)
                    .contentType(MediaType.APPLICATION_PDF)
                    .body(pdfBytes);
        } catch (IOException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error downloading PDF: " + e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("An unexpected error occurred while downloading PDF");
        }
    }
}
