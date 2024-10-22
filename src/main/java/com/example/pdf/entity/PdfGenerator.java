package com.example.pdf.entity;

import com.itextpdf.html2pdf.HtmlConverter;
import java.io.OutputStream;

public class PdfGenerator {

    public static void generatePdfFromHtml(String htmlContent, OutputStream outputStream) throws Exception {
        
        HtmlConverter.convertToPdf(htmlContent, outputStream);
    }
}

