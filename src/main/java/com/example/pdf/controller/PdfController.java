package com.example.pdf.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.example.pdf.entity.PdfRequest;
import com.example.pdf.entity.PdfService;

@RestController
@RequestMapping("/api/pdf")
public class PdfController {

    @Autowired
    private PdfService pdfService;

    
    @PostMapping("/generate")
    public ResponseEntity<?> generatePdf(@RequestBody PdfRequest pdfRequest) {
        try {
            String pdfPath = pdfService.generateAndStorePdf(pdfRequest);
            return ResponseEntity.ok().body("PDF generated and stored at: " + pdfPath);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

   
    @GetMapping("/download")
    public ResponseEntity<?> downloadPdf(@RequestParam String fileName) {
        return pdfService.downloadPdf(fileName);
    }
}
