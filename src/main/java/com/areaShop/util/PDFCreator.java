package com.areaShop.util;


import com.itextpdf.text.*;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;
import com.areaShop.model.dto.ProductDto;
import org.springframework.stereotype.Component;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Stream;

@Component
public class PDFCreator {

    public ByteArrayInputStream customerPDFReport(List<ProductDto> products) {
        Document document = new Document();
        ByteArrayOutputStream out = new ByteArrayOutputStream();

        try {

            PdfWriter.getInstance(document, out);
            document.open();

            // Add Text to PDF file ->
            Font font = FontFactory.getFont(FontFactory.COURIER, 14, BaseColor.BLACK);
            Paragraph para = new Paragraph( "Your product", font);
            para.setAlignment(Element.ALIGN_CENTER);
            document.add(para);
            document.add(Chunk.NEWLINE);

            PdfPTable table = new PdfPTable(3);
            // Add PDF Table Header ->
            Stream.of("Name", "Description", "Price")
                    .forEach(headerTitle -> {
                        PdfPCell header = new PdfPCell();
                        Font headFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD);
                        header.setBackgroundColor(BaseColor.LIGHT_GRAY);
                        header.setHorizontalAlignment(Element.ALIGN_CENTER);
                        header.setBorderWidth(2);
                        header.setPhrase(new Phrase(headerTitle, headFont));
                        table.addCell(header);
                    });

            for (ProductDto productDto : products) {
                PdfPCell idCell = new PdfPCell(new Phrase(productDto.getName()));
                idCell.setPaddingLeft(4);
                idCell.setVerticalAlignment(Element.ALIGN_MIDDLE);
                idCell.setHorizontalAlignment(Element.ALIGN_CENTER);
                table.addCell(idCell);

                PdfPCell firstNameCell = new PdfPCell(new Phrase(productDto.getDescription()));
                firstNameCell.setPaddingLeft(4);
                firstNameCell.setVerticalAlignment(Element.ALIGN_MIDDLE);
                firstNameCell.setHorizontalAlignment(Element.ALIGN_LEFT);
                table.addCell(firstNameCell);

                PdfPCell lastNameCell = new PdfPCell(new Phrase(String.valueOf(productDto.getItemTotal())));
                lastNameCell.setVerticalAlignment(Element.ALIGN_MIDDLE);
                lastNameCell.setHorizontalAlignment(Element.ALIGN_RIGHT);
                lastNameCell.setPaddingRight(4);
                table.addCell(lastNameCell);
            }


            Stream.of("Total", "", getTotalForBill(products))
                    .forEach(headerTitle -> {
                        PdfPCell footer = new PdfPCell();
                        Font headFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD);
                        footer.setBackgroundColor(BaseColor.LIGHT_GRAY);
                        footer.setHorizontalAlignment(Element.ALIGN_CENTER);
                        footer.setBorderWidth(2);
                        footer.setPhrase(new Phrase(headerTitle, headFont));
                        table.addCell(footer);
                    });

            document.add(table);
            document.close();
        }catch(DocumentException e) {
           System.err.println(e);
        }
        return new ByteArrayInputStream(out.toByteArray());
    }

    private String getTotalForBill(List<ProductDto> list){
        BigDecimal totalSum = BigDecimal.ZERO;
        for (ProductDto pr: list){
           totalSum = totalSum.add(pr.getItemTotal());
        }
        return String.valueOf(totalSum);
    }
}
