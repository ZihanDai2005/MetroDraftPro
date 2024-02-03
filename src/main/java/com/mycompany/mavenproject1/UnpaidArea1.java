/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.mavenproject1;

import com.itextpdf.io.font.PdfEncodings;
import com.itextpdf.io.image.ImageData;
import com.itextpdf.io.image.ImageDataFactory;
import com.itextpdf.io.util.StreamUtil;
import com.itextpdf.kernel.colors.Color;
import com.itextpdf.kernel.colors.DeviceCmyk;
import com.itextpdf.kernel.font.PdfFont;
import com.itextpdf.kernel.font.PdfFontFactory;
import com.itextpdf.kernel.geom.PageSize;
import com.itextpdf.kernel.geom.Rectangle;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfPage;
import com.itextpdf.kernel.pdf.PdfReader;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.kernel.pdf.WriterProperties;
import com.itextpdf.kernel.pdf.canvas.PdfCanvas;
import com.itextpdf.kernel.pdf.canvas.PdfCanvasConstants;
import com.itextpdf.kernel.pdf.xobject.PdfFormXObject;
import com.itextpdf.layout.Canvas;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.element.Image;
import com.itextpdf.layout.element.Paragraph;
import com.itextpdf.layout.layout.LayoutArea;
import com.itextpdf.layout.layout.LayoutContext;
import com.itextpdf.layout.layout.LayoutResult;
import com.itextpdf.layout.properties.Property;
import com.itextpdf.layout.renderer.IRenderer;
import java.io.ByteArrayOutputStream;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFCell;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 *
 * @author daizhenjin
 */
public class UnpaidArea1 {

    public static void main(ArrayList<Object> dataList) throws FileNotFoundException, IOException {
        //待办: 最底下的、，提示
        //公交车，都改成、
        final float starterX = (int) dataList.get(0) * 2.83464567f;
        final float starterY = (int) dataList.get(1) * 2.83464567f;
        final float pageSizeX = (int) dataList.get(2) * 2.83464567f;
        final float pageSizeY = (int) dataList.get(3) * 2.83464567f;
        
        final int startRow = 2;
        final int col = (int) dataList.get(4);
        String stationName = (String) dataList.get(5);
        
        PdfDocument pdfDocument = (PdfDocument) dataList.get(6);
        PdfPage page = (PdfPage) dataList.get(7);
        File f = (File) dataList.get(8);

        final float differenceX = pageSizeX - 685 * 2.83464567f;
        final float differenceY = pageSizeY - 1725 * 2.83464567f;
        final float smallEdge = (float) 34.596 * 2.83464567f;

        PdfFont heiTi;
        PdfFont arial;
        PdfFont arialBold;
        try (InputStream fontStream1 = UnpaidArea1.class.getResourceAsStream("/fonts/simhei.ttf")) {
            heiTi = PdfFontFactory.createFont(StreamUtil.inputStreamToArray(fontStream1), PdfEncodings.IDENTITY_H, PdfFontFactory.EmbeddingStrategy.PREFER_EMBEDDED);
        }
        try (InputStream fontStream2 = UnpaidArea1.class.getResourceAsStream("/fonts/arial.ttf")) {
            arial = PdfFontFactory.createFont(StreamUtil.inputStreamToArray(fontStream2), PdfEncodings.IDENTITY_H, PdfFontFactory.EmbeddingStrategy.PREFER_EMBEDDED);
        }
        try (InputStream fontStream3 = UnpaidArea1.class.getResourceAsStream("/fonts/arial bold.ttf")) {
            arialBold = PdfFontFactory.createFont(StreamUtil.inputStreamToArray(fontStream3), PdfEncodings.IDENTITY_H, PdfFontFactory.EmbeddingStrategy.PREFER_EMBEDDED);
        }

        final double edge = 171 * 2.83464567f;
        final double lineWidth = pageSizeX - edge;
        final double interval = 97.869 * 2.83464567f;
        String stationNumber = ""; //二维码车站编号
        String direction = "left"; //对齐方向，默认为left
        String firstEntrance = ""; //字母编号
        String buslines = "";
        boolean arrowType = false;
        ArrayList<String> exitDirection = new ArrayList<>(); //方位词(东西南北)
        ArrayList<String> facilityInfo = new ArrayList<>(); //当前出口设施
        ArrayList<String[]> facilityInfoBottom = new ArrayList<>(); //其他出口设施
        ArrayList<String[]> exitInfo = new ArrayList<>(); //所有二级信息

        try {
            BufferedInputStream bis = new BufferedInputStream(new FileInputStream(f));
            XSSFWorkbook workbook = new XSSFWorkbook(bis);
            XSSFSheet sheet = workbook.getSheetAt(0);

            for (int row = startRow + 4; row <= startRow + 25;) {
                XSSFCell chineseCell = sheet.getRow(row).getCell(col);
                XSSFCell englishCell = sheet.getRow(row + 1).getCell(col);
                if (chineseCell != null && englishCell != null) {
                    String chinese = replaceChars(chineseCell.getStringCellValue());
                    String english = replaceChars(englishCell.getStringCellValue());
                    if (english.equals("") == false) {
                        exitInfo.add(new String[]{chinese, english});
                    }
                }
                row += 2;
            }

            for (int row = startRow + 27; row <= startRow + 27 + 10; row++) {
                XSSFCell busCell = sheet.getRow(row).getCell(col);
                if (busCell == null || busCell.getStringCellValue().isEmpty()) {
                    break;
                } else {
                    buslines += replaceChars(busCell.getStringCellValue()) + "\n";
                }
            }
            buslines = buslines.substring(0, buslines.length() - 1);

            XSSFCell stationCell = sheet.getRow(0).getCell(0);
            stationNumber = getStationNumber(stationCell.getStringCellValue())[1]; //二维码编号

            XSSFCell arrowCell = sheet.getRow(startRow + 2).getCell(col);
            direction = detectArrowDirection(arrowCell.getStringCellValue()); //对齐方向

            XSSFCell exitCell = sheet.getRow(startRow + 3).getCell(col);
            firstEntrance = findEnglishLetters(exitCell.getStringCellValue()); //字母编号
            exitDirection = findChineseDirections(exitCell.getStringCellValue()); //方位词(东西南北)
            facilityInfo = findFacilityInfo(exitCell.getStringCellValue()); //当前出口设施

            XSSFCell bottomFacilityCell = sheet.getRow(startRow + 26).getCell(col);
            facilityInfoBottom = parseFacilityInfo(bottomFacilityCell.getStringCellValue()); //其他出口设施

            XSSFCell whetherArrowCell = sheet.getRow(startRow + 2).getCell(col);
            arrowType = checkArrow(whetherArrowCell.getStringCellValue()); //是否带箭头
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        int upNum = 10;

        Document document = new Document(pdfDocument);
        document.setProperty(Property.SPLIT_CHARACTERS, new Split());

        PdfCanvas canvas = new PdfCanvas(page);
        //background
        Color backgroundColor = new DeviceCmyk(0, 0, 0, 0);
        canvas.saveState()
                .setFillColor(backgroundColor)
                .rectangle(0, 0, pageSizeX, pageSizeY)
                .fill()
                .restoreState();

        double topsBottom = pageSizeY - (128.5 * 2.83464567f); //顶部灰带的底部坐标
        //top
        backgroundColor = new DeviceCmyk(0, 0, 0, 10);
        canvas.saveState()
                .setFillColor(backgroundColor)
                .rectangle(0, topsBottom, pageSizeX, 128.5 * 2.83464567f)
                .fill()
                .restoreState();

        //bottom
        backgroundColor = new DeviceCmyk(0, 0, 0, 10);
        canvas.saveState()
                .setFillColor(backgroundColor)
                .rectangle(0, 0, pageSizeX, 49.423 * 2.83464567f)
                .fill()
                .restoreState();

        //qrcode rectangle
        backgroundColor = new DeviceCmyk(100, 0, 100, 0);
        float qrBaseX = smallEdge;
        float qrBaseY = (float) (pageSizeY - 1619.932 * 2.83464567f);
        canvas.saveState()
                .setFillColor(backgroundColor)
                .rectangle(qrBaseX, qrBaseY, 84.019 * 2.83464567f, 87.972 * 2.83464567f)
                .fill()
                .restoreState();

        //qr code text
        PdfCanvas canvas6 = new PdfCanvas(page);
        canvas6.beginText();
        canvas6.setFontAndSize(heiTi, 36);
        backgroundColor = new DeviceCmyk(0, 0, 0, 100);
        canvas6.setFillColor(backgroundColor);
        canvas6.setCharacterSpacing((float) 4);
        canvas6.moveText(qrBaseX, qrBaseY - 18.258 * 2.83464567f); //设置文本的起始位置
        canvas6.showText("扫码获取实时");
        canvas6.endText();

        canvas6.beginText();
        canvas6.moveText(qrBaseX, qrBaseY - 33.498 * 2.83464567f); //设置文本的起始位置
        canvas6.showText("公交换乘信息");
        canvas6.endText();

        //qr code image
        ImageData data;
        try (InputStream qrCodeStream = UnpaidArea1.class.getClassLoader().getResourceAsStream("images/qrcode" + stationNumber + ".png")) {
            data = ImageDataFactory.create(StreamUtil.inputStreamToArray(qrCodeStream));
        }
        Image qrCode = new Image(data);
        qrCode.setFixedPosition((float) ((84.019 * 2.83464567f - 79.076 * 2.83464567) / 2 + qrBaseX), (float) ((87.972 * 2.83464567f - 83.03 * 2.83464567) / 2 + qrBaseY));
        qrCode.setWidth((float) 79.076 * 2.83464567f);
        qrCode.setHeight((float) 83.03 * 2.83464567f);
        document.add(qrCode);

        //公交图标
        float xOutlineBus = smallEdge;
        float yOutlineBus = (float) (pageSizeY - 1473.394 * 2.83464567f);
        try (InputStream pdfStreamIcon = UnpaidArea1.class.getResourceAsStream("/pdfs/unpaid/bus_black.pdf")) {
            PdfDocument srcPdfDocument = new PdfDocument(new PdfReader(pdfStreamIcon));
            PdfFormXObject pageXObject = srcPdfDocument.getFirstPage().copyAsFormXObject(pdfDocument);
            srcPdfDocument.close();

            canvas6.addXObject(pageXObject, xOutlineBus, yOutlineBus);
        }

        //公交图标文字
        canvas6.beginText();
        canvas6.setFontAndSize(heiTi, 59);
        backgroundColor = new DeviceCmyk(0, 0, 0, 100);
        canvas6.setFillColor(backgroundColor);
        canvas6.setCharacterSpacing((float) 1);
        canvas6.moveText(xOutlineBus, yOutlineBus - 24.151 * 2.83464567f); //设置文本的起始位置
        canvas6.showText("公交信息");
        canvas6.endText();

        canvas6.beginText();
        canvas6.setFontAndSize(arial, 35);
        canvas6.setCharacterSpacing(0);
        canvas6.moveText(xOutlineBus - 0.94 * 2.83464567f, yOutlineBus - 38.416 * 2.83464567f); //设置文本的起始位置
        canvas6.showText("Bus Information");
        canvas6.endText();

        //公交信息提示词
        Paragraph paragraph1 = new Paragraph("以下公交信息仅供参考，请以扫码界面或公交站点实际公布为准。 ")
                .setFontSize(64)
                .setFont(heiTi)
                .setMultipliedLeading(1f) //设置行间距
                .setCharacterSpacing((float) 2.8) //设置字间距
                .setWidth((float) 519.791 * 2.83464567f) //设置文本框宽度
                .setHeight((float) 55 * 2.83464567f); //设置文本框高度
        paragraph1.setTextRenderingMode(PdfCanvasConstants.TextRenderingMode.FILL);
        paragraph1.setFixedPosition(148 * 2.83464567f, (float) 286.031 * 2.83464567f + differenceY, (float) 519.791 * 2.83464567f);// x, y, 宽
        document.add(paragraph1);

        //公交站点/公交线路
        Paragraph paragraph2 = new Paragraph(buslines)
                .setFontSize(64)
                .setFont(heiTi)
                .setMultipliedLeading(1f) //设置行间距
                .setCharacterSpacing((float) 2.5); //设置字间距
        paragraph2.setTextRenderingMode(PdfCanvasConstants.TextRenderingMode.FILL);
        float tempHeight = checkAndAdjustFontSize(paragraph2, 64);
        paragraph2.setFixedPosition(148 * 2.83464567f, (float) 73.348 * 2.83464567f + tempHeight + differenceY, (float) 519.791 * 2.83464567f);
        document.add(paragraph2);

        float xOutline;
        float yOutline;

        backgroundColor = new DeviceCmyk(0, 0, 0, 100);
        float tempY = (float) (pageSizeY - 287.64 * 2.83464567f);
        float tempX;
        if (direction.equals("left")) {
            tempX = (float) edge;
        } else {
            tempX = 0;
        }
        for (int i = 1; i <= upNum; i++) { //横线
            canvas.saveState()
                    .setFillColor(backgroundColor)
                    .rectangle(tempX, tempY, lineWidth, 1.975 * 2.83464567f)
                    .fill()
                    .restoreState();
            tempY -= interval;
        }
        for (int i = 1; i <= 2; i++) { //底部横线
            canvas.saveState()
                    .setFillColor(backgroundColor)
                    .rectangle(0, tempY, pageSizeX - 42.504 * 2.83464567f, 1.975 * 2.83464567f)
                    .fill()
                    .restoreState();
            tempY -= interval;
        }
        tempY += interval;
        canvas.saveState()
                .setFillColor(backgroundColor)
                .rectangle(148.268 * 2.83464567f, tempY - 83.514 * 2.83464567f, pageSizeX - 148.268 * 2.83464567f, 2 * 2.83464567f)
                .fill()
                .restoreState();
        //字母外框
        if (direction.equals("right")) {
            xOutline = (float) (pageSizeX - (32.125 + 106.753) * 2.83464567f);
        } else {
            xOutline = (float) 32.125 * 2.83464567f;
        }
        yOutline = (float) (pageSizeY - 274.791 * 2.83464567f);
        PdfCanvas canvas3 = new PdfCanvas(pdfDocument.getFirstPage());
        try (InputStream pdfStreamIcon = UnpaidArea1.class.getResourceAsStream("/pdfs/unpaid/outline_big.pdf")) {
            PdfDocument srcPdfDocument = new PdfDocument(new PdfReader(pdfStreamIcon));
            PdfFormXObject pageXObject = srcPdfDocument.getFirstPage().copyAsFormXObject(pdfDocument);
            srcPdfDocument.close();

            canvas3.addXObject(pageXObject, xOutline, yOutline);
        }

        //出入口字母
        canvas3.beginText();
        canvas3.setFontAndSize(arialBold, 294);
        if (firstEntrance.length() != 1) {

        } else if (firstEntrance.equals("B") || firstEntrance.equals("D") || firstEntrance.equals("G")) {
            float textHeight = arialBold.getAscent(firstEntrance, 294) - arialBold.getDescent(firstEntrance, 294);
            float textWidth = arialBold.getWidth(firstEntrance, 294);
            canvas3.moveText(xOutline + (106.753 * 2.83464567f - textWidth + 3) / 2, yOutline + (106.753 * 2.83464567f - textHeight) / 2); //设置文本的起始位置
            canvas3.showText(firstEntrance);
            canvas3.endText();
        } else {
            float textHeight = arialBold.getAscent(firstEntrance, 294) - arialBold.getDescent(firstEntrance, 294);
            float textWidth = arialBold.getWidth(firstEntrance, 294);
            canvas3.moveText(xOutline + (106.753 * 2.83464567f - textWidth) / 2, yOutline + (106.753 * 2.83464567f - textHeight) / 2); //设置文本的起始位置
            canvas3.showText(firstEntrance);
            canvas3.endText();
        }
        canvas3.endText();

        //方位 方向
        canvas3.beginText();
        if (exitDirection.size() == 1) {
            //中文
            canvas3.setFontAndSize(heiTi, 104);
            float spacing = 6;
            canvas3.setCharacterSpacing(spacing);
            float textWidth = heiTi.getWidth(exitDirection.get(0), 104) + (exitDirection.get(0).length() - 1) * spacing;
            canvas3.setTextRenderingMode(PdfCanvasConstants.TextRenderingMode.FILL_STROKE);
            canvas3.setLineWidth((float) 0.074 * 2.83464567f);
            backgroundColor = new DeviceCmyk(0, 0, 0, 100);
            canvas3.setStrokeColor(backgroundColor);
            canvas3.setFillColor(backgroundColor);
            canvas3.moveText(xOutline + 106.753 * 2.83464567f / 2 - textWidth / 2, yOutline - 42.038 * 2.83464567f); //设置文本的起始位置
            canvas3.showText(exitDirection.get(0));
            canvas3.endText();

            //英文
            spacing = 0;
            canvas3.beginText();
            canvas3.setFontAndSize(arial, 69);
            canvas3.setCharacterSpacing(spacing);
            String english = translateDirection(exitDirection.get(0));
            textWidth = arial.getWidth(english, 69) + (english.length() - 1) * spacing;
            canvas3.setTextRenderingMode(PdfCanvasConstants.TextRenderingMode.FILL_STROKE);
            canvas3.setLineWidth((float) 0.074 * 2.83464567f);
            backgroundColor = new DeviceCmyk(0, 0, 0, 100);
            canvas3.setStrokeColor(backgroundColor);
            canvas3.setFillColor(backgroundColor);
            canvas3.moveText(xOutline + 106.753 * 2.83464567f / 2 - textWidth / 2, yOutline - 70.876 * 2.83464567f); //设置文本的起始位置
            canvas3.showText(english);
            canvas3.endText();

        } else {

        }

        backgroundColor = new DeviceCmyk(90, 0, 100, 0);
        PdfFormXObject pageObjectExit;
        try (InputStream pdfStreamIcon = UnpaidArea1.class.getResourceAsStream("/pdfs/unpaid/exit.pdf")) {
            PdfDocument srcPdfDocument = new PdfDocument(new PdfReader(pdfStreamIcon));
            pageObjectExit = srcPdfDocument.getFirstPage().copyAsFormXObject(pdfDocument);
            srcPdfDocument.close();
        }
        if (arrowType == false) { //无箭头
            if (direction.equals("right")) { //右对齐
                //绿色出矩形
                canvas.saveState()
                        .setFillColor(backgroundColor)
                        .rectangle(pageSizeX - (128.5 * 2.83464567f), topsBottom, 128.5 * 2.83464567f, 128.5 * 2.83464567f)
                        .fill()
                        .restoreState();
                PdfCanvas canvas2 = new PdfCanvas(pdfDocument.getFirstPage());
                float xExit = (float) (pageSizeX - 95.61 * 2.83464567f);
                float yExit = (float) (((topsBottom / 2.83464567f) + 22.164) * 2.83464567f);
                canvas2.addXObject(pageObjectExit, xExit, yExit);
            } else { //左对齐
                //绿色出矩形
                canvas.saveState()
                        .setFillColor(backgroundColor)
                        .rectangle(0, topsBottom, 128.5 * 2.83464567f, 128.5 * 2.83464567f)
                        .fill()
                        .restoreState();
                PdfCanvas canvas2 = new PdfCanvas(pdfDocument.getFirstPage());
                float xExit = (float) (32.89 * 2.83464567f);
                float yExit = (float) (((topsBottom / 2.83464567f) + 22.164) * 2.83464567f);
                canvas2.addXObject(pageObjectExit, xExit, yExit);
            }
        } else { //带箭头
            PdfFormXObject pageObjectArrow;
            float y = (float) (((topsBottom / 2.83464567f) + 20.37) * 2.83464567f);
            float x;
            PdfCanvas canvas2 = new PdfCanvas(pdfDocument.getFirstPage());
            float yExit = (float) (((topsBottom / 2.83464567f) + 22.164) * 2.83464567f);
            if (direction.equals("right")) { //右对齐 右箭头
                try (InputStream pdfStreamIcon = UnpaidArea1.class.getResourceAsStream("/pdfs/unpaid/arrow_right.pdf")) {
                    PdfDocument srcPdfDocument = new PdfDocument(new PdfReader(pdfStreamIcon));
                    pageObjectArrow = srcPdfDocument.getFirstPage().copyAsFormXObject(pdfDocument);
                    srcPdfDocument.close();
                }
                x = (float) (((pageSizeX / 2.83464567f) - 20.211 - 88.173) * 2.83464567f);
                canvas.saveState()
                        .setFillColor(backgroundColor)
                        .rectangle(((x / 2.83464567f) - 20.211 - 128.5) * 2.83464567f, topsBottom, 128.5 * 2.83464567f, 128.5 * 2.83464567f)
                        .fill()
                        .restoreState();
                canvas2.addXObject(pageObjectExit, (float) (((x / 2.83464567f) - 20.211 - 128.5 + 32.89) * 2.83464567f), yExit);
            } else { //左对齐 左箭头
                try (InputStream pdfStreamIcon = UnpaidArea1.class.getResourceAsStream("/pdfs/unpaid/arrow_left.pdf")) {
                    PdfDocument srcPdfDocument = new PdfDocument(new PdfReader(pdfStreamIcon));
                    pageObjectArrow = srcPdfDocument.getFirstPage().copyAsFormXObject(pdfDocument);
                    srcPdfDocument.close();
                }
                x = (float) (22.814 * 2.83464567f);
                canvas.saveState()
                        .setFillColor(backgroundColor)
                        .rectangle(((x / 2.83464567f) + 20.211 + 88.173) * 2.83464567f, topsBottom, 128.5 * 2.83464567f, 128.5 * 2.83464567f)
                        .fill()
                        .restoreState();
                canvas2.addXObject(pageObjectExit, (float) ((x / 2.83464567f) + 20.211 + 88.173 + 32.89) * 2.83464567f, yExit);
            }
            canvas2.addXObject(pageObjectArrow, x, y);
        }

        //logo
        try (InputStream pdfStreamIcon = UnpaidArea1.class.getResourceAsStream("/pdfs/unpaid/logo.pdf")) {
            PdfDocument srcPdfDocument = new PdfDocument(new PdfReader(pdfStreamIcon));
            PdfFormXObject pageXObject = srcPdfDocument.getFirstPage().copyAsFormXObject(pdfDocument);
            srcPdfDocument.close();

            canvas.addXObject(pageXObject, (float) (pageSizeX - (129.4 * 2.83464567f)) / 2, (float) 7.117 * 2.83464567f);
        }

        canvas.beginText();
        canvas.setFontAndSize(arial, 85);
        canvas.setTextRenderingMode(PdfCanvasConstants.TextRenderingMode.FILL_STROKE);
        canvas.setLineWidth(0.287f);
        canvas.setCharacterSpacing(0);
        backgroundColor = new DeviceCmyk(0, 0, 0, 100);
        canvas.setStrokeColor(backgroundColor);
        canvas.setFillColor(backgroundColor);
        if (direction.equals("left")) { //左对齐
            if (arrowType == false) {
                canvas.moveText(155.801 * 2.83464567f, pageSizeY - 103.458 * 2.83464567f); //设置文本的起始位置
            } else {
                canvas.moveText(287.197 * 2.83464567f, pageSizeY - 103.458 * 2.83464567f); //设置文本的起始位置
            }
        } else { //右对齐
            float textWidth = arial.getWidth("Exit Information", 85);
            if (arrowType == false) {
                canvas.moveText(pageSizeX - textWidth - 155.95 * 2.83464567f, pageSizeY - 103.458 * 2.83464567f); //设置文本的起始位置
            } else {
                canvas.moveText(pageSizeX - textWidth - 284.536 * 2.83464567f, pageSizeY - 103.458 * 2.83464567f); //设置文本的起始位置
            }
        }
        canvas.showText("Exit Information");
        canvas.endText();

        canvas.beginText();
        canvas.setFontAndSize(heiTi, 148);
        canvas.setTextRenderingMode(PdfCanvasConstants.TextRenderingMode.FILL_STROKE);
        canvas.setLineWidth(0.287f);
        if (direction.equals("left")) { //左对齐
            if (arrowType == false) {
                canvas.moveText(152.501 * 2.83464567f, pageSizeY - 67.62 * 2.83464567f); //设置文本的起始位置
            } else {
                canvas.moveText(283.897 * 2.83464567f, pageSizeY - 67.62 * 2.83464567f); //设置文本的起始位置
            }
        } else { //右对齐
            float textWidth = heiTi.getWidth("出口信息", 148);
            if (arrowType == false) {
                canvas.moveText(pageSizeX - textWidth - 155.766 * 2.83464567f, pageSizeY - 67.62 * 2.83464567f); //设置文本的起始位置
            } else {
                canvas.moveText(pageSizeX - textWidth - 284.363 * 2.83464567f, pageSizeY - 67.62 * 2.83464567f); //设置文本的起始位置
            }
        }
        canvas.showText("出口信息");
        canvas.endText();

        float y;
        float y2;
        if (facilityInfo.isEmpty()) { //看设施是否为空判断输出多少个二级信息
            y = (float) (pageSizeY - 246.361 * 2.83464567f);
            y2 = (float) (pageSizeY - 274.607 * 2.83464567f);
            if (exitInfo.size() > 11) {
                String notice = "温馨提示：空间不足，";
                notice += Integer.toString(exitInfo.size() - 11);
                notice += "条出入口信息未能展示。";
                System.out.println(notice);
                exitInfo = new ArrayList<>(exitInfo.subList(0, 11));
            }
        } else {
            y = (float) (pageSizeY - 246.361 * 2.83464567f - interval);
            y2 = (float) (pageSizeY - 274.607 * 2.83464567f - interval);
            if (exitInfo.size() > 10) {
                String notice = "温馨提示：空间不足，";
                notice += Integer.toString(exitInfo.size() - 10);
                notice += "条出入口信息未能展示。";
                System.out.println(notice);
                exitInfo = new ArrayList<>(exitInfo.subList(0, 10));
            }
        }
        double x;
        double x2;
        int chineseSpace = 7;
        //Up
        for (String[] element : exitInfo) {
            //中文
            PdfCanvas canvas4 = new PdfCanvas(page);
            canvas4.beginText();
            canvas4.setFontAndSize(heiTi, 104);
            canvas4.setCharacterSpacing(chineseSpace);
            canvas4.setTextRenderingMode(PdfCanvasConstants.TextRenderingMode.FILL_STROKE);
            canvas4.setLineWidth((float) 0.074 * 2.83464567f);
            backgroundColor = new DeviceCmyk(0, 0, 0, 100);
            canvas4.setStrokeColor(backgroundColor);
            canvas4.setFillColor(backgroundColor);
            float textWidth = heiTi.getWidth(element[0], 104) + chineseSpace * (element[0].length() - 1);
            double scale = 1;
            if (textWidth >= lineWidth - 15 * 2.83464567f) {
                scale = (lineWidth - 15 * 2.83464567f) / textWidth;
                canvas4.saveState();
                canvas4.setTextMatrix((float) scale, 0, 0, 1, 0, 0);
                textWidth = textWidth * (float) scale;
            }
            if (direction.equals("left")) {
                x = edge;
            } else {
                x = pageSizeX - edge - textWidth;
            }
            canvas4.moveText(x / scale, y); //设置文本的起始位置
            canvas4.showText(element[0]);
            y -= interval;
            canvas4.endText();

            //英文
            PdfCanvas canvas5 = new PdfCanvas(page);
            canvas5.beginText();
            canvas5.setFontAndSize(arial, 69);
            canvas5.setCharacterSpacing(0);
            canvas5.setTextRenderingMode(PdfCanvasConstants.TextRenderingMode.FILL_STROKE);
            canvas5.setLineWidth((float) 0.074 * 2.83464567f);
            canvas5.setStrokeColor(backgroundColor);
            canvas5.setFillColor(backgroundColor);
            float textWidth2 = arial.getWidth(element[1], 69);
            scale = 1;
            if (textWidth2 >= lineWidth - 15 * 2.83464567f) {
                scale = (lineWidth - 15 * 2.83464567f) / textWidth2;
                canvas5.saveState();
                canvas5.setTextMatrix((float) scale, 0, 0, 1, 0, 0);
                textWidth2 = textWidth2 * (float) scale;
            }
            if (direction.equals("left")) {
                x2 = edge;
            } else {
                x2 = pageSizeX - edge - textWidth2;
            }
            canvas5.moveText(x2 / scale, y2); //设置文本的起始位置
            canvas5.showText(element[1]);
            y2 -= interval;
            canvas5.endText();
        }

        // 创建一个临时的 PdfFormXObject
        PageSize tempPageSize = new PageSize(pageSizeX * 2, 72 * 2.83464567f);
        PdfFormXObject template = new PdfFormXObject(tempPageSize);
        //在临时 canvas 上添加内容
        PdfCanvas canvas7 = new PdfCanvas(template, pdfDocument);

        float infoYStarter = 0;
        float infoYStarterUpText = infoYStarter + (float) 29.684 * 2.83464567f;
        float infoYStarterDownText = infoYStarter + (float) 3.536 * 2.83464567f;
        final float maxInfoX = (float) (pageSizeX - smallEdge - 42.504 * 2.83464567f + differenceX);

        final float iconWidth = (float) 49.423 * 2.83464567f;
        final float iconInterval = (float) 12.356 * 2.83464567f;
        final float iconTextInterval = iconInterval;
        final float spacing = (float) 6.2;

        float nextInfoX = 0;
        float nextInfoXTemp = 0;
        float nextInfoXTempExtreme = 0;
        int index = 1;

        for (String[] element : facilityInfoBottom) {
            if (element[0].equals("卫生间")) {
                nextInfoX += iconWidth + iconInterval;
                nextInfoX += iconWidth + iconTextInterval;
                nextInfoX += heiTi.getWidth("位于", 64) + spacing * ("位于".length() - 1) + spacing + 1 * 2.83464567f;

                String place = element[1]; //"A口/B口通道"

                // 将字符串转换为字符数组
                char[] charArray = place.toCharArray();

                for (int i = 0; i < charArray.length; i++) {
                    char c = charArray[i];
                    if (Character.isDigit(c)) {
                        nextInfoX -= (0.5 * spacing);
                    }
                    if (String.valueOf(c).matches("[A-Z]")) {
                        nextInfoX += arial.getWidth(String.valueOf(c), 69);
                        if (i + 1 < charArray.length && String.valueOf(charArray[i + 1]).equals("/")) {
                            nextInfoX += spacing;
                        }
                    } else if (String.valueOf(c).equals("/")) {
                        nextInfoX += arial.getWidth(String.valueOf(c), 69) + spacing;
                    } else if (Character.isDigit(c)) {
                        nextInfoX += arial.getWidth(String.valueOf(c), 45);
                        if (i + 1 < charArray.length && String.valueOf(charArray[i + 1]).equals("/")) {
                            nextInfoX += spacing;
                        }
                    } else {
                        nextInfoX += heiTi.getWidth(String.valueOf(c), 69) + spacing * 0.3;
                    }
                }
            } else if (element[0].equals("母婴室")) {
                nextInfoX += iconWidth + iconTextInterval;
                nextInfoX += heiTi.getWidth("位于", 64) + spacing * ("位于".length() - 1) + spacing + 1 * 2.83464567f;

                String place = element[1]; //"A口/B口通道"

                //将字符串转换为字符数组
                char[] charArray = place.toCharArray();

                for (int i = 0; i < charArray.length; i++) {
                    char c = charArray[i];
                    if (Character.isDigit(c)) {
                        nextInfoX -= (0.5 * spacing);
                    }
                    if (String.valueOf(c).matches("[A-Z]")) {
                        nextInfoX += arial.getWidth(String.valueOf(c), 69);
                        if (i + 1 < charArray.length && String.valueOf(charArray[i + 1]).equals("/")) {
                            nextInfoX += spacing;
                        }
                    } else if (String.valueOf(c).equals("/")) {
                        nextInfoX += arial.getWidth(String.valueOf(c), 69) + spacing;
                    } else if (Character.isDigit(c)) {
                        nextInfoX += arial.getWidth(String.valueOf(c), 45);
                        if (i + 1 < charArray.length && String.valueOf(charArray[i + 1]).equals("/")) {
                            nextInfoX += spacing;
                        }
                    } else {
                        nextInfoX += heiTi.getWidth(String.valueOf(c), 69) + spacing * 0.3;
                    }
                }
            } else if (element[0].equals("电梯(站厅-地面)")) {
                nextInfoX += iconWidth + iconInterval;
                nextInfoX += iconWidth + iconTextInterval;

                final float tempWidthUp = heiTi.getWidth("电梯(站厅-地面)", 64) + spacing * 3; //符号不加spacing
                float tempWidthDown = heiTi.getWidth("位于", 64) + spacing * ("位于".length() - 1) + spacing + 1 * 2.83464567f;
                float tempNextInfoX = nextInfoX + heiTi.getWidth("位于", 64) + spacing * ("位于".length() - 1) + spacing + 1 * 2.83464567f;

                String place = element[1]; //"A口/B口通道"

                //将字符串转换为字符数组
                char[] charArray = place.toCharArray();

                for (int i = 0; i < charArray.length; i++) {
                    char c = charArray[i];
                    if (Character.isDigit(c)) {
                        tempNextInfoX -= 0.5 * spacing;
                        tempWidthDown -= 0.5 * spacing;
                    }
                    if (String.valueOf(c).matches("[A-Z]")) {
                        tempNextInfoX += arial.getWidth(String.valueOf(c), 69);
                        tempWidthDown += arial.getWidth(String.valueOf(c), 69);
                        if (i + 1 < charArray.length && String.valueOf(charArray[i + 1]).equals("/")) {
                            tempNextInfoX += spacing;
                            tempWidthDown += spacing;
                        }
                    } else if (String.valueOf(c).equals("/")) {
                        tempNextInfoX += arial.getWidth(String.valueOf(c), 69) + spacing;
                        tempWidthDown += arial.getWidth(String.valueOf(c), 69) + spacing;
                    } else if (Character.isDigit(c)) {
                        tempNextInfoX += arial.getWidth(String.valueOf(c), 45);
                        tempWidthDown += arial.getWidth(String.valueOf(c), 45);
                        if (i + 1 < charArray.length && String.valueOf(charArray[i + 1]).equals("/")) {
                            tempNextInfoX += spacing;
                            tempWidthDown += spacing;
                        }
                    } else {
                        tempNextInfoX += heiTi.getWidth(String.valueOf(c), 69) + spacing * 0.3;
                        tempWidthDown += heiTi.getWidth(String.valueOf(c), 69) + spacing * 0.3;
                    }
                }

                if (tempWidthUp > tempWidthDown) { //判断第一行文字和第二行文字哪个更长 以长的为准
                    nextInfoX += tempWidthUp;
                } else {
                    nextInfoX = tempNextInfoX;
                }
            }
            nextInfoX -= spacing;

            nextInfoXTemp = nextInfoX;
            nextInfoXTempExtreme = nextInfoX;

            if (facilityInfoBottom.size() == 2) {
                nextInfoXTemp += (32.705 * 2.83464567f * 2 - 2.83464567f) * (facilityInfoBottom.size() - 1);
                nextInfoXTemp += 1.483 * 2.83464567f * (facilityInfoBottom.size() - 1);
                nextInfoXTempExtreme += (49.667 * 2.83464567f * 2 - 2.83464567f) * (facilityInfoBottom.size() - 1);
                nextInfoXTempExtreme += 1.483 * 2.83464567f * (facilityInfoBottom.size() - 1);
            } else {
                nextInfoXTemp += (19.161 * 2.83464567f * 2 - 2.83464567f) * (facilityInfoBottom.size() - 1);
                nextInfoXTemp += 1.483 * 2.83464567f * (facilityInfoBottom.size() - 1);
            }

            index++;
        }

        float scale = 1;
        float verticleLineInterval;
        float error = (float) 5.2 * 2.83464567f;
        if (nextInfoXTemp - error <= maxInfoX) {
            if (nextInfoXTempExtreme - error <= maxInfoX) {
                verticleLineInterval = (float) 49.667 * 2.83464567f;
            } else {
                verticleLineInterval = (float) 32.705 * 2.83464567f;
            }
        } else if (nextInfoXTemp * 0.9 - error <= maxInfoX) {
            if (facilityInfoBottom.size() == 2) {
                verticleLineInterval = (float) 32.705 * 2.83464567f;
            } else {
                verticleLineInterval = (float) 19.161 * 2.83464567f;
            }
            scale = (float) 0.9;
        } else if (nextInfoXTemp * 0.8 - error <= maxInfoX) {
            if (facilityInfoBottom.size() == 2) {
                verticleLineInterval = (float) 32.705 * 2.83464567f;
            } else {
                verticleLineInterval = (float) 19.161 * 2.83464567f;
            }
            scale = (float) 0.8;
        } else {
            verticleLineInterval = (float) 19.161 * 2.83464567f;
            scale = maxInfoX / (nextInfoXTemp - error);
        }
        ///////////////////////////////////////////////////////////////////////////////////////
        nextInfoX = 0;
        index = 1;
        for (String[] element : facilityInfoBottom) {
            if (element[0].equals("卫生间")) {
                try (InputStream pdfStreamIcon = UnpaidArea1.class.getResourceAsStream("/pdfs/unpaid/s_unpaid_toilet.pdf")) {
                    PdfDocument srcPdfDocument = new PdfDocument(new PdfReader(pdfStreamIcon));
                    PdfFormXObject pageXObject = srcPdfDocument.getFirstPage().copyAsFormXObject(pdfDocument);
                    srcPdfDocument.close();

                    canvas7.addXObject(pageXObject, nextInfoX, 0);
                }
                nextInfoX += iconWidth + iconInterval;

                try (InputStream pdfStreamIcon = UnpaidArea1.class.getResourceAsStream("/pdfs/unpaid/s_unpaid_wheelchair.pdf")) {
                    PdfDocument srcPdfDocument = new PdfDocument(new PdfReader(pdfStreamIcon));
                    PdfFormXObject pageXObject = srcPdfDocument.getFirstPage().copyAsFormXObject(pdfDocument);
                    srcPdfDocument.close();

                    canvas7.addXObject(pageXObject, nextInfoX, 0);
                }
                nextInfoX += iconWidth + iconTextInterval;

                canvas7.beginText();
                canvas7.setFontAndSize(heiTi, 64);
                canvas7.setTextRenderingMode(PdfCanvasConstants.TextRenderingMode.FILL_STROKE);
                canvas7.setLineWidth(0.5f);
                backgroundColor = new DeviceCmyk(0, 0, 0, 100);
                canvas7.setStrokeColor(backgroundColor);
                canvas7.setFillColor(backgroundColor);
                canvas7.setCharacterSpacing((float) spacing);
                canvas7.moveText(nextInfoX, infoYStarterUpText); //设置文本的起始位置
                canvas7.showText("卫生间");
                canvas7.endText();

                canvas7.beginText();
                canvas7.moveText(nextInfoX, infoYStarterDownText); //设置文本的起始位置
                canvas7.showText("位于");
                canvas7.endText();

                nextInfoX += heiTi.getWidth("位于", 64) + spacing * ("位于".length() - 1) + spacing + 1 * 2.83464567f;

                String place = element[1]; //"A口/B口通道"

                // 将字符串转换为字符数组
                char[] charArray = place.toCharArray();

                for (int i = 0; i < charArray.length; i++) {
                    char c = charArray[i];
                    canvas7.beginText();
                    if (Character.isDigit(c)) {
                        nextInfoX -= (0.5 * spacing);
                    }
                    canvas7.moveText(nextInfoX, infoYStarterDownText); //设置文本的起始位置
                    if (String.valueOf(c).matches("[A-Z]")) {
                        canvas7.setFontAndSize(arial, 64);
                        nextInfoX += arial.getWidth(String.valueOf(c), 69);
                        if (i + 1 < charArray.length && String.valueOf(charArray[i + 1]).equals("/")) {
                            nextInfoX += spacing;
                        }
                    } else if (String.valueOf(c).equals("/")) {
                        canvas7.setFontAndSize(arial, 64);
                        nextInfoX += arial.getWidth(String.valueOf(c), 69) + spacing;
                    } else if (Character.isDigit(c)) {
                        canvas7.setFontAndSize(arial, 44);
                        nextInfoX += arial.getWidth(String.valueOf(c), 45);
                        if (i + 1 < charArray.length && String.valueOf(charArray[i + 1]).equals("/")) {
                            nextInfoX += spacing;
                        }
                    } else {
                        canvas7.setFontAndSize(heiTi, 64);
                        nextInfoX += heiTi.getWidth(String.valueOf(c), 69) + spacing * 0.3;
                    }
                    canvas7.showText(String.valueOf(c));
                    canvas7.endText();
                }
            } else if (element[0].equals("母婴室")) {
                try (InputStream pdfStreamIcon = UnpaidArea1.class.getResourceAsStream("/pdfs/unpaid/s_unpaid_nursing.pdf")) {
                    PdfDocument srcPdfDocument = new PdfDocument(new PdfReader(pdfStreamIcon));
                    PdfFormXObject pageXObject = srcPdfDocument.getFirstPage().copyAsFormXObject(pdfDocument);
                    srcPdfDocument.close();

                    canvas7.addXObject(pageXObject, nextInfoX, 0);
                }
                nextInfoX += iconWidth + iconTextInterval;

                canvas7.beginText();
                canvas7.setFontAndSize(heiTi, 64);
                canvas7.setTextRenderingMode(PdfCanvasConstants.TextRenderingMode.FILL_STROKE);
                canvas7.setLineWidth(0.5f);
                backgroundColor = new DeviceCmyk(0, 0, 0, 100);
                canvas7.setStrokeColor(backgroundColor);
                canvas7.setFillColor(backgroundColor);
                canvas7.setCharacterSpacing((float) spacing);
                canvas7.moveText(nextInfoX, infoYStarterUpText); //设置文本的起始位置
                canvas7.showText("母婴室");
                canvas7.endText();

                canvas7.beginText();
                canvas7.moveText(nextInfoX, infoYStarterDownText); //设置文本的起始位置
                canvas7.showText("位于");
                canvas7.endText();

                nextInfoX += heiTi.getWidth("位于", 64) + spacing * ("位于".length() - 1) + spacing + 1 * 2.83464567f;

                String place = element[1]; //"A口/B口通道"

                //将字符串转换为字符数组
                char[] charArray = place.toCharArray();

                for (int i = 0; i < charArray.length; i++) {
                    char c = charArray[i];
                    canvas7.beginText();
                    if (Character.isDigit(c)) {
                        nextInfoX -= (0.5 * spacing);
                    }
                    canvas7.moveText(nextInfoX, infoYStarterDownText); //设置文本的起始位置
                    if (String.valueOf(c).matches("[A-Z]")) {
                        canvas7.setFontAndSize(arial, 64);
                        nextInfoX += arial.getWidth(String.valueOf(c), 69);
                        if (i + 1 < charArray.length && String.valueOf(charArray[i + 1]).equals("/")) {
                            nextInfoX += spacing;
                        }
                    } else if (String.valueOf(c).equals("/")) {
                        canvas7.setFontAndSize(arial, 64);
                        nextInfoX += arial.getWidth(String.valueOf(c), 69) + spacing;
                    } else if (Character.isDigit(c)) {
                        canvas7.setFontAndSize(arial, 44);
                        nextInfoX += arial.getWidth(String.valueOf(c), 45);
                        if (i + 1 < charArray.length && String.valueOf(charArray[i + 1]).equals("/")) {
                            nextInfoX += spacing;
                        }
                    } else {
                        canvas7.setFontAndSize(heiTi, 64);
                        nextInfoX += heiTi.getWidth(String.valueOf(c), 69) + spacing * 0.3;
                    }
                    canvas7.showText(String.valueOf(c));
                    canvas7.endText();
                }
            } else if (element[0].equals("电梯(站厅-地面)")) {
                try (InputStream pdfStreamIcon = UnpaidArea1.class.getResourceAsStream("/pdfs/unpaid/s_unpaid_elevator.pdf")) {
                    PdfDocument srcPdfDocument = new PdfDocument(new PdfReader(pdfStreamIcon));
                    PdfFormXObject pageXObject = srcPdfDocument.getFirstPage().copyAsFormXObject(pdfDocument);
                    srcPdfDocument.close();

                    canvas7.addXObject(pageXObject, nextInfoX, 0);
                }
                nextInfoX += iconWidth + iconInterval;

                try (InputStream pdfStreamIcon = UnpaidArea1.class.getResourceAsStream("/pdfs/unpaid/s_unpaid_wheelchair.pdf")) {
                    PdfDocument srcPdfDocument = new PdfDocument(new PdfReader(pdfStreamIcon));
                    PdfFormXObject pageXObject = srcPdfDocument.getFirstPage().copyAsFormXObject(pdfDocument);
                    srcPdfDocument.close();

                    canvas7.addXObject(pageXObject, nextInfoX, 0);
                }
                nextInfoX += iconWidth + iconTextInterval;

                canvas7.beginText();
                canvas7.setFontAndSize(heiTi, 64);
                canvas7.setTextRenderingMode(PdfCanvasConstants.TextRenderingMode.FILL_STROKE);
                canvas7.setLineWidth(0.5f);
                backgroundColor = new DeviceCmyk(0, 0, 0, 100);
                canvas7.setStrokeColor(backgroundColor);
                canvas7.setFillColor(backgroundColor);

                canvas7.setCharacterSpacing((float) spacing);
                canvas7.moveText(nextInfoX, infoYStarterUpText); //设置文本的起始位置
                float tempPuncX = nextInfoX;
                canvas7.showText("电梯");
                tempPuncX += heiTi.getWidth("电梯", 64) + spacing * 1;
                canvas7.endText();

                canvas7.beginText();
                canvas7.moveText(tempPuncX, infoYStarterUpText); //设置文本的起始位置
                canvas7.showText("(");
                tempPuncX += heiTi.getWidth("(", 64);
                canvas7.endText();

                canvas7.beginText();
                canvas7.moveText(tempPuncX, infoYStarterUpText); //设置文本的起始位置
                canvas7.showText("站厅");
                canvas7.endText();
                tempPuncX += heiTi.getWidth("站厅", 64) + spacing * 1;
                canvas7.endText();

                canvas7.beginText();
                canvas7.moveText(tempPuncX, infoYStarterUpText); //设置文本的起始位置
                canvas7.showText("-");
                tempPuncX += heiTi.getWidth("-", 64);
                canvas7.endText();

                canvas7.beginText();
                canvas7.moveText(tempPuncX, infoYStarterUpText); //设置文本的起始位置
                canvas7.showText("地面");
                tempPuncX += heiTi.getWidth("地面", 64) + spacing * 1;
                canvas7.endText();

                canvas7.beginText();
                canvas7.moveText(tempPuncX, infoYStarterUpText); //设置文本的起始位置
                canvas7.showText(")");
                canvas7.endText();

                canvas7.beginText();
                canvas7.moveText(nextInfoX, infoYStarterDownText); //设置文本的起始位置
                canvas7.showText("位于");
                canvas7.endText();

                final float tempWidthUp = heiTi.getWidth("电梯(站厅-地面)", 64) + spacing * 3; //符号不加spacing
                float tempWidthDown = heiTi.getWidth("位于", 64) + spacing * ("位于".length() - 1) + spacing + 1 * 2.83464567f;
                float tempNextInfoX = nextInfoX + heiTi.getWidth("位于", 64) + spacing * ("位于".length() - 1) + spacing + 1 * 2.83464567f;

                String place = element[1]; //"A口/B口通道"

                //将字符串转换为字符数组
                char[] charArray = place.toCharArray();

                for (int i = 0; i < charArray.length; i++) {
                    char c = charArray[i];
                    canvas7.beginText();
                    if (Character.isDigit(c)) {
                        tempNextInfoX -= (0.5 * spacing);
                        tempWidthDown -= (0.5 * spacing);
                    }
                    canvas7.moveText(tempNextInfoX, infoYStarterDownText); //设置文本的起始位置
                    if (String.valueOf(c).matches("[A-Z]")) {
                        canvas7.setFontAndSize(arial, 64);
                        tempNextInfoX += arial.getWidth(String.valueOf(c), 69);
                        tempWidthDown += arial.getWidth(String.valueOf(c), 69);
                        if (i + 1 < charArray.length && String.valueOf(charArray[i + 1]).equals("/")) {
                            tempNextInfoX += spacing;
                            tempWidthDown += spacing;
                        }
                    } else if (String.valueOf(c).equals("/")) {
                        canvas7.setFontAndSize(arial, 64);
                        tempNextInfoX += arial.getWidth(String.valueOf(c), 69) + spacing;
                        tempWidthDown += arial.getWidth(String.valueOf(c), 69) + spacing;
                    } else if (Character.isDigit(c)) {
                        canvas7.setFontAndSize(arial, 44);
                        tempNextInfoX += arial.getWidth(String.valueOf(c), 45);
                        tempWidthDown += arial.getWidth(String.valueOf(c), 45);
                        if (i + 1 < charArray.length && String.valueOf(charArray[i + 1]).equals("/")) {
                            tempNextInfoX += spacing;
                            tempWidthDown += arial.getWidth(String.valueOf(c), 45);
                        }
                    } else {
                        canvas7.setFontAndSize(heiTi, 64);
                        tempNextInfoX += heiTi.getWidth(String.valueOf(c), 69) + spacing * 0.3;
                        tempWidthDown += heiTi.getWidth(String.valueOf(c), 69) + spacing * 0.3;
                    }
                    canvas7.showText(String.valueOf(c));
                    canvas7.endText();
                }

                if (tempWidthUp > tempWidthDown) { //判断第一行文字和第二行文字哪个更长 以长的为准
                    nextInfoX += tempWidthUp;
                } else {
                    nextInfoX = tempNextInfoX;
                }
            }
            nextInfoX -= spacing;

            if (index < facilityInfoBottom.size()) {
                nextInfoX += verticleLineInterval - 2.83464567f;

                //竖线
                canvas7.saveState()
                        .setFillColor(backgroundColor)
                        .rectangle(nextInfoX, infoYStarter, 1.483 * 2.83464567f, iconWidth)
                        .fill()
                        .restoreState();

                nextInfoX += 1.483 * 2.83464567f + verticleLineInterval;
            }

            index++;
        }

        float correctX = smallEdge;
        float correctY = (float) (pageSizeY - 1364.062 * 2.83464567f + ((95.88 * 2.83464567f) - iconWidth * scale) / 2);
        PdfCanvas finalCanvas = new PdfCanvas(page);

        finalCanvas.addXObjectWithTransformationMatrix(template, scale, 0, 0, scale, correctX, correctY);

        //编号旁设施图标
        //创建一个临时的 PdfFormXObject
        PdfFormXObject template2 = new PdfFormXObject(tempPageSize);
        //在临时 canvas 上添加内容
        PdfCanvas canvas8 = new PdfCanvas(template2, pdfDocument);

        final float maxInfoX2 = (float) (pageSizeX - edge - 58.071 * 2.83464567f);

        final float iconWidth2 = (float) 71.169 * 2.83464567f;
        final float iconInterval2 = (float) 17.792 * 2.83464567f;

        if (direction.equals("left")) { //左对齐 设施图标
            float nextInfoX2 = 0;
            int index2 = 1;
            int publicTransport = 0;
            for (String element : facilityInfo) {//计算交通设施数量 只有最后一个加竖线
                if (element.equals("卫生间") == false && element.equals("母婴室") == false && element.equals("垂梯") == false) {
                    publicTransport++;
                }
            }

            for (String element : facilityInfo) {
                if (element.equals("卫生间")) {
                    try (InputStream pdfStreamIcon = UnpaidArea1.class.getResourceAsStream("/pdfs/unpaid/unpaid_toilet.pdf")) {
                        PdfDocument srcPdfDocument = new PdfDocument(new PdfReader(pdfStreamIcon));
                        PdfFormXObject pageXObject = srcPdfDocument.getFirstPage().copyAsFormXObject(pdfDocument);
                        srcPdfDocument.close();

                        canvas8.addXObject(pageXObject, nextInfoX2, 0);
                    }
                    nextInfoX2 += iconWidth2 + iconInterval2;

                    try (InputStream pdfStreamIcon = UnpaidArea1.class.getResourceAsStream("/pdfs/unpaid/unpaid_wheelchair.pdf")) {
                        PdfDocument srcPdfDocument = new PdfDocument(new PdfReader(pdfStreamIcon));
                        PdfFormXObject pageXObject = srcPdfDocument.getFirstPage().copyAsFormXObject(pdfDocument);
                        srcPdfDocument.close();

                        canvas8.addXObject(pageXObject, nextInfoX2, 0);
                    }
                } else if (element.equals("母婴室")) {
                    try (InputStream pdfStreamIcon = UnpaidArea1.class.getResourceAsStream("/pdfs/unpaid/unpaid_nursing.pdf")) {
                        PdfDocument srcPdfDocument = new PdfDocument(new PdfReader(pdfStreamIcon));
                        PdfFormXObject pageXObject = srcPdfDocument.getFirstPage().copyAsFormXObject(pdfDocument);
                        srcPdfDocument.close();

                        canvas8.addXObject(pageXObject, nextInfoX2, 0);
                    }
                } else if (element.equals("垂梯")) {//回到
                    try (InputStream pdfStreamIcon = UnpaidArea1.class.getResourceAsStream("/pdfs/unpaid/unpaid_elevator.pdf")) {
                        PdfDocument srcPdfDocument = new PdfDocument(new PdfReader(pdfStreamIcon));
                        PdfFormXObject pageXObject = srcPdfDocument.getFirstPage().copyAsFormXObject(pdfDocument);
                        srcPdfDocument.close();

                        canvas8.addXObject(pageXObject, nextInfoX2, 0);
                    }
                    nextInfoX2 += iconWidth2 + iconInterval2;

                    try (InputStream pdfStreamIcon = UnpaidArea1.class.getResourceAsStream("/pdfs/unpaid/unpaid_wheelchair.pdf")) {
                        PdfDocument srcPdfDocument = new PdfDocument(new PdfReader(pdfStreamIcon));
                        PdfFormXObject pageXObject = srcPdfDocument.getFirstPage().copyAsFormXObject(pdfDocument);
                        srcPdfDocument.close();

                        canvas8.addXObject(pageXObject, nextInfoX2, 0);
                    }
                } else if (element.equals("国铁")) {
                    try (InputStream pdfStreamIcon = UnpaidArea1.class.getResourceAsStream("/pdfs/unpaid/unpaid_railway.pdf")) {
                        PdfDocument srcPdfDocument = new PdfDocument(new PdfReader(pdfStreamIcon));
                        PdfFormXObject pageXObject = srcPdfDocument.getFirstPage().copyAsFormXObject(pdfDocument);
                        srcPdfDocument.close();

                        canvas8.addXObject(pageXObject, nextInfoX2, 0);
                    }
                } else if (element.equals("长途客运")) {
                    try (InputStream pdfStreamIcon = UnpaidArea1.class.getResourceAsStream("/pdfs/unpaid/unpaid_coach.pdf")) {
                        PdfDocument srcPdfDocument = new PdfDocument(new PdfReader(pdfStreamIcon));
                        PdfFormXObject pageXObject = srcPdfDocument.getFirstPage().copyAsFormXObject(pdfDocument);
                        srcPdfDocument.close();

                        canvas8.addXObject(pageXObject, nextInfoX2, 0);
                    }
                } else if (element.equals("机场巴士")) {
                    try (InputStream pdfStreamIcon = UnpaidArea1.class.getResourceAsStream("/pdfs/unpaid/unpaid_airportbus.pdf")) {
                        PdfDocument srcPdfDocument = new PdfDocument(new PdfReader(pdfStreamIcon));
                        PdfFormXObject pageXObject = srcPdfDocument.getFirstPage().copyAsFormXObject(pdfDocument);
                        srcPdfDocument.close();

                        canvas8.addXObject(pageXObject, nextInfoX2, 0);
                    }
                } else if (element.equals("公交")) {
                    try (InputStream pdfStreamIcon = UnpaidArea1.class.getResourceAsStream("/pdfs/unpaid/unpaid_bus.pdf")) {
                        PdfDocument srcPdfDocument = new PdfDocument(new PdfReader(pdfStreamIcon));
                        PdfFormXObject pageXObject = srcPdfDocument.getFirstPage().copyAsFormXObject(pdfDocument);
                        srcPdfDocument.close();

                        canvas8.addXObject(pageXObject, nextInfoX2, 0);
                    }
                } else if (element.equals("停车场")) {
                    try (InputStream pdfStreamIcon = UnpaidArea1.class.getResourceAsStream("/pdfs/unpaid/unpaid_parking.pdf")) {
                        PdfDocument srcPdfDocument = new PdfDocument(new PdfReader(pdfStreamIcon));
                        PdfFormXObject pageXObject = srcPdfDocument.getFirstPage().copyAsFormXObject(pdfDocument);
                        srcPdfDocument.close();

                        canvas8.addXObject(pageXObject, nextInfoX2, 0);
                    }
                } else if (element.equals("出租车")) {
                    try (InputStream pdfStreamIcon = UnpaidArea1.class.getResourceAsStream("/pdfs/unpaid/unpaid_taxi.pdf")) {
                        PdfDocument srcPdfDocument = new PdfDocument(new PdfReader(pdfStreamIcon));
                        PdfFormXObject pageXObject = srcPdfDocument.getFirstPage().copyAsFormXObject(pdfDocument);
                        srcPdfDocument.close();

                        canvas8.addXObject(pageXObject, nextInfoX2, 0);
                    }
                } else if (element.equals("网约车")) {
                    try (InputStream pdfStreamIcon = UnpaidArea1.class.getResourceAsStream("/pdfs/unpaid/unpaid_ehailing.pdf")) {
                        PdfDocument srcPdfDocument = new PdfDocument(new PdfReader(pdfStreamIcon));
                        PdfFormXObject pageXObject = srcPdfDocument.getFirstPage().copyAsFormXObject(pdfDocument);
                        srcPdfDocument.close();

                        canvas8.addXObject(pageXObject, nextInfoX2, 0);
                    }
                }

                if (index2 < facilityInfo.size()) {
                    nextInfoX2 += iconWidth2 + 16.902 * 2.83464567f;
                } else {
                    nextInfoX2 += iconWidth2;
                }

                if (index2 >= publicTransport && index2 < facilityInfo.size()) {//只有最后一个公共交通设施加竖线
                    //竖线
                    canvas8.saveState()
                            .setFillColor(backgroundColor)
                            .rectangle(nextInfoX2, 0, 1.779 * 2.83464567f, iconWidth2)
                            .fill()
                            .restoreState();

                    nextInfoX2 += 1.59 * 2.83464567f + 16.902 * 2.83464567f;
                }

                index2++;
            }

            float scale2 = maxInfoX2 / nextInfoX2;
            float correctX2 = (float) edge;
            float correctY2;
            PdfCanvas finalCanvas2 = new PdfCanvas(page);
            if (scale2 < 1) {
                correctY2 = (float) (pageSizeY - 221.414 * 2.83464567f - iconWidth2 * scale2 / 2);
                finalCanvas2.addXObjectWithTransformationMatrix(template2, scale2, 0, 0, scale2, correctX2, correctY2);
            } else {
                correctY2 = (float) (pageSizeY - 256.999 * 2.83464567f);
                finalCanvas2.addXObjectWithTransformationMatrix(template2, 1, 0, 0, 1, correctX2, correctY2);
            }
        } else { //右对齐 设施图标
            swap(facilityInfo);

            float nextInfoX2 = 0;
            int index2 = 1;
            int publicTransport = 0;
            for (String element : facilityInfo) {//计算交通设施数量 只有最后一个加竖线
                if (element.equals("卫生间") == false && element.equals("母婴室") == false && element.equals("垂梯") == false) {
                    publicTransport++;
                }
            }

            for (int i = facilityInfo.size() - 1; i >= 0; i--) {
                String element = facilityInfo.get(i);
                if (element.equals("卫生间")) {
                    try (InputStream pdfStreamIcon = UnpaidArea1.class.getResourceAsStream("/pdfs/unpaid/unpaid_toilet.pdf")) {
                        PdfDocument srcPdfDocument = new PdfDocument(new PdfReader(pdfStreamIcon));
                        PdfFormXObject pageXObject = srcPdfDocument.getFirstPage().copyAsFormXObject(pdfDocument);
                        srcPdfDocument.close();

                        canvas8.addXObject(pageXObject, nextInfoX2, 0);
                    }
                    nextInfoX2 += iconWidth2 + iconInterval2;

                    try (InputStream pdfStreamIcon = UnpaidArea1.class.getResourceAsStream("/pdfs/unpaid/unpaid_wheelchair.pdf")) {
                        PdfDocument srcPdfDocument = new PdfDocument(new PdfReader(pdfStreamIcon));
                        PdfFormXObject pageXObject = srcPdfDocument.getFirstPage().copyAsFormXObject(pdfDocument);
                        srcPdfDocument.close();

                        canvas8.addXObject(pageXObject, nextInfoX2, 0);
                    }
                } else if (element.equals("母婴室")) {
                    try (InputStream pdfStreamIcon = UnpaidArea1.class.getResourceAsStream("/pdfs/unpaid/unpaid_nursing.pdf")) {
                        PdfDocument srcPdfDocument = new PdfDocument(new PdfReader(pdfStreamIcon));
                        PdfFormXObject pageXObject = srcPdfDocument.getFirstPage().copyAsFormXObject(pdfDocument);
                        srcPdfDocument.close();

                        canvas8.addXObject(pageXObject, nextInfoX2, 0);
                    }
                } else if (element.equals("垂梯")) {
                    try (InputStream pdfStreamIcon = UnpaidArea1.class.getResourceAsStream("/pdfs/unpaid/unpaid_elevator.pdf")) {
                        PdfDocument srcPdfDocument = new PdfDocument(new PdfReader(pdfStreamIcon));
                        PdfFormXObject pageXObject = srcPdfDocument.getFirstPage().copyAsFormXObject(pdfDocument);
                        srcPdfDocument.close();

                        canvas8.addXObject(pageXObject, nextInfoX2, 0);
                    }
                    nextInfoX2 += iconWidth2 + iconInterval2;

                    try (InputStream pdfStreamIcon = UnpaidArea1.class.getResourceAsStream("/pdfs/unpaid/unpaid_wheelchair.pdf")) {
                        PdfDocument srcPdfDocument = new PdfDocument(new PdfReader(pdfStreamIcon));
                        PdfFormXObject pageXObject = srcPdfDocument.getFirstPage().copyAsFormXObject(pdfDocument);
                        srcPdfDocument.close();

                        canvas8.addXObject(pageXObject, nextInfoX2, 0);
                    }
                } else if (element.equals("国铁")) {
                    try (InputStream pdfStreamIcon = UnpaidArea1.class.getResourceAsStream("/pdfs/unpaid/unpaid_railway.pdf")) {
                        PdfDocument srcPdfDocument = new PdfDocument(new PdfReader(pdfStreamIcon));
                        PdfFormXObject pageXObject = srcPdfDocument.getFirstPage().copyAsFormXObject(pdfDocument);
                        srcPdfDocument.close();

                        canvas8.addXObject(pageXObject, nextInfoX2, 0);
                    }
                } else if (element.equals("长途客运")) {
                    try (InputStream pdfStreamIcon = UnpaidArea1.class.getResourceAsStream("/pdfs/unpaid/unpaid_coach.pdf")) {
                        PdfDocument srcPdfDocument = new PdfDocument(new PdfReader(pdfStreamIcon));
                        PdfFormXObject pageXObject = srcPdfDocument.getFirstPage().copyAsFormXObject(pdfDocument);
                        srcPdfDocument.close();

                        canvas8.addXObject(pageXObject, nextInfoX2, 0);
                    }
                } else if (element.equals("机场巴士")) {
                    try (InputStream pdfStreamIcon = UnpaidArea1.class.getResourceAsStream("/pdfs/unpaid/unpaid_airportbus.pdf")) {
                        PdfDocument srcPdfDocument = new PdfDocument(new PdfReader(pdfStreamIcon));
                        PdfFormXObject pageXObject = srcPdfDocument.getFirstPage().copyAsFormXObject(pdfDocument);
                        srcPdfDocument.close();

                        canvas8.addXObject(pageXObject, nextInfoX2, 0);
                    }
                } else if (element.equals("公交")) {
                    try (InputStream pdfStreamIcon = UnpaidArea1.class.getResourceAsStream("/pdfs/unpaid/unpaid_bus.pdf")) {
                        PdfDocument srcPdfDocument = new PdfDocument(new PdfReader(pdfStreamIcon));
                        PdfFormXObject pageXObject = srcPdfDocument.getFirstPage().copyAsFormXObject(pdfDocument);
                        srcPdfDocument.close();

                        canvas8.addXObject(pageXObject, nextInfoX2, 0);
                    }
                } else if (element.equals("停车场")) {
                    try (InputStream pdfStreamIcon = UnpaidArea1.class.getResourceAsStream("/pdfs/unpaid/unpaid_parking.pdf")) {
                        PdfDocument srcPdfDocument = new PdfDocument(new PdfReader(pdfStreamIcon));
                        PdfFormXObject pageXObject = srcPdfDocument.getFirstPage().copyAsFormXObject(pdfDocument);
                        srcPdfDocument.close();

                        canvas8.addXObject(pageXObject, nextInfoX2, 0);
                    }
                } else if (element.equals("出租车")) {
                    try (InputStream pdfStreamIcon = UnpaidArea1.class.getResourceAsStream("/pdfs/unpaid/unpaid_taxi.pdf")) {
                        PdfDocument srcPdfDocument = new PdfDocument(new PdfReader(pdfStreamIcon));
                        PdfFormXObject pageXObject = srcPdfDocument.getFirstPage().copyAsFormXObject(pdfDocument);
                        srcPdfDocument.close();

                        canvas8.addXObject(pageXObject, nextInfoX2, 0);
                    }
                } else if (element.equals("网约车")) {
                    try (InputStream pdfStreamIcon = UnpaidArea1.class.getResourceAsStream("/pdfs/unpaid/unpaid_ehailing.pdf")) {
                        PdfDocument srcPdfDocument = new PdfDocument(new PdfReader(pdfStreamIcon));
                        PdfFormXObject pageXObject = srcPdfDocument.getFirstPage().copyAsFormXObject(pdfDocument);
                        srcPdfDocument.close();

                        canvas8.addXObject(pageXObject, nextInfoX2, 0);
                    }
                }

                if (index2 < facilityInfo.size()) {
                    nextInfoX2 += iconWidth2 + 16.902 * 2.83464567f;
                } else {
                    nextInfoX2 += iconWidth2;
                }

                if (index2 <= (facilityInfo.size() - publicTransport) && index2 < facilityInfo.size()) {//只有最后一个公共交通设施加竖线
                    //竖线
                    canvas8.saveState()
                            .setFillColor(backgroundColor)
                            .rectangle(nextInfoX2, 0, 1.779 * 2.83464567f, iconWidth2)
                            .fill()
                            .restoreState();

                    nextInfoX2 += 1.59 * 2.83464567f + 16.902 * 2.83464567f;
                }

                index2++;
            }

            float scale2 = maxInfoX2 / nextInfoX2;
            float correctX2;
            if (scale2 < 1) {
                correctX2 = (float) (pageSizeX - edge - nextInfoX2 * scale2);
            } else {
                correctX2 = (float) (pageSizeX - edge - nextInfoX2);
            }
            float correctY2;
            PdfCanvas finalCanvas2 = new PdfCanvas(page);
            if (scale2 < 1) {
                correctY2 = (float) (pageSizeY - 221.414 * 2.83464567f - iconWidth2 * scale2 / 2);
                finalCanvas2.addXObjectWithTransformationMatrix(template2, scale2, 0, 0, scale2, correctX2, correctY2);
            } else {
                correctY2 = (float) (pageSizeY - 256.999 * 2.83464567f);
                finalCanvas2.addXObjectWithTransformationMatrix(template2, 1, 0, 0, 1, correctX2, correctY2);
            }
        }

        Color outlineColor = new DeviceCmyk(0, 0, 0, 10);
        canvas.saveState()
                .setStrokeColor(outlineColor)
                .setLineWidth(2.4f)
                .rectangle(0, 0, pageSizeX, pageSizeY)
                .stroke()
                .restoreState();

        document.close();
    }

    public static void swap(ArrayList<String> list) {
        int index1 = list.indexOf("卫生间");
        int index2 = list.indexOf("母婴室");

        if (index1 != -1 && index2 != -1) {
            Collections.swap(list, index1, index2);
        }
    }

    public static String translateDirection(String chineseDirection) {
        Map<String, String> directionMap = new HashMap<>();
        directionMap.put("东", "East");
        directionMap.put("南", "South");
        directionMap.put("西", "West");
        directionMap.put("北", "North");
        directionMap.put("东南", "Southeast");
        directionMap.put("东北", "Northeast");
        directionMap.put("西南", "Southwest");
        directionMap.put("西北", "Northwest");

        return directionMap.getOrDefault(chineseDirection, "Unknown");
    }

    public static float checkAndAdjustFontSize(Paragraph p, int fontSize) {
        p.setFontSize(fontSize);
        System.out.println(fontSize);

        Rectangle rect = new Rectangle(0, 0, (float) 519.791 * 2.83464567f, (float) 200.384 * 2.83464567f);
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        PdfDocument tempPdfDoc = new PdfDocument(new PdfWriter(baos));
        tempPdfDoc.addNewPage();

        Canvas tempCanvas = new Canvas(tempPdfDoc.getFirstPage(), rect);
        IRenderer renderer = p.createRendererSubTree();
        renderer.setParent(tempCanvas.getRenderer());
        LayoutResult result = renderer.layout(new LayoutContext(new LayoutArea(0, rect)));

        Rectangle occupiedArea = result.getOccupiedArea().getBBox();

        if (fontSize == 62) {
            System.out.println("温馨提示：公交信息内容过多，文字字号已自动缩小。");
        }

        System.out.println(rect.getHeight());
        System.out.println(occupiedArea.getHeight());

        if (occupiedArea.getHeight() <= 565 && occupiedArea.getHeight() >= 515) {
            //内容超出指定区域
            //更改字体大小
            tempCanvas.close();
            return checkAndAdjustFontSize(p, fontSize - 2);
        } else {
            tempCanvas.close();
            return rect.getHeight() - occupiedArea.getHeight();
        }
    }

    public static String findEnglishLetters(String input) {
        for (int i = 0; i < input.length(); i++) {
            char ch = input.charAt(i);
            if (Character.isLetter(ch)) {
                // 如果当前字符是字母
                if (i + 1 < input.length() && Character.isDigit(input.charAt(i + 1))) {
                    // 如果下一个字符是数字，返回字母和数字一起
                    return String.valueOf(ch).toUpperCase() + input.charAt(i + 1);
                } else {
                    // 否则，只返回字母
                    return String.valueOf(ch).toUpperCase();
                }
            }
        }
        return ""; // 如果没有找到匹配的情况，则返回空字符串
    }

    public static ArrayList<String> findChineseDirections(String input) {
        // 定义中文方位词数组
        String[] directions = {"东南", "东北", "西南", "西北", "东", "南", "西", "北"};

        ArrayList<String> matchingDirections = new ArrayList<>();

        // 遍历字符串
        for (int i = 0; i < input.length() - 1; i++) {
            // 获取两个字符组成的子字符串
            String substring = input.substring(i, i + 2);
            for (String direction : directions) {
                if (substring.equals(direction)) {
                    matchingDirections.add(direction);
                }
            }
        }

        return matchingDirections; // 返回包含匹配的中文方位词的 ArrayList
    }

    public static String detectArrowDirection(String input) {
        for (int i = 0; i < input.length(); i++) {
            char ch = input.charAt(i);
            if (ch == '←') {
                return "left";
            } else if (ch == '→') {
                return "right";
            }
        }
        return "温馨提示：尚未指定对齐方向，已自动向左对齐。"; // 如果没有检测到特定字符，则返回空字符串
    }

    public static ArrayList<String> findFacilityInfo(String input) {
        ArrayList<String> facilityInfo = new ArrayList<>();

        // 定义设施信息的词语列表
        List<String> facilityWords = List.of("国铁", "长途客运", "机场巴士", "公交", "停车场", "出租车", "网约车", "卫生间", "母婴室", "垂梯");

        // 遍历文本
        for (String word : facilityWords) {
            if (input.contains(word)) {
                facilityInfo.add(word);
            }
        }

        return facilityInfo;
    }

    public static ArrayList<String[]> parseFacilityInfo(String input) {
        ArrayList<String[]> facilityInfo = new ArrayList<>();

        // 将输入字符串以逗号为分隔符拆分成多个部分
        String[] parts = input.split("、");

        // 定义设施名称的关键词和对应的归类名称
        Map<String, String> facilityKeywords = new HashMap<>();
        facilityKeywords.put("电梯(站厅-地面)", "电梯|垂梯|电梯(站厅-地面)|垂梯(站厅-地面)|电梯（站厅-地面）|垂梯（站厅-地面）");
        facilityKeywords.put("卫生间", "卫生间");
        facilityKeywords.put("母婴室", "母婴室");

        // 设施的顺序
        String[] facilityOrder = {"电梯(站厅-地面)", "卫生间", "母婴室"};

        // 遍历拆分后的部分
        for (String facility : facilityOrder) {
            // 遍历输入的部分
            for (String part : parts) {
                // 获取对应设施关键词
                String keywords = facilityKeywords.get(facility);
                if (keywords != null) {
                    String[] keywordList = keywords.split("\\|");
                    for (String keyword : keywordList) {
                        if (part.contains(keyword)) {
                            // 找到包含设施关键词的部分，添加为对应的设施名称
                            String[] facilityLocation = new String[]{facility, extractLocation(part)};
                            facilityInfo.add(facilityLocation);
                            break;
                        }
                    }
                }
            }
        }

        return facilityInfo;
    }

    // 辅助方法：提取位置信息
    private static String extractLocation(String part) {
        int locationIndex = part.indexOf("位于");
        if (locationIndex != -1) {
            return part.substring(locationIndex + 2); // 位置描述
        }
        return ""; // 如果没有位置描述，默认为空字符串
    }

    public static String replaceChars(String input) {
        // 将字符串中的 "（" 替换为 "("
        String replacedInput = input.replace("（", "(");
        // 将字符串中的 "）" 替换为 ")"
        replacedInput = replacedInput.replace("）", ")");
        // 将字符串中的 "’" 替换为 "'"
        replacedInput = replacedInput.replace("’", "'");
        return replacedInput;
    }

    public static String[] getStationNumber(String stationName) {
        // 创建车站名称到数字的映射
        Map<String, String> stationMap = new HashMap<>();
        stationMap.put("象峰", "01");
        stationMap.put("秀山", "02");
        stationMap.put("罗汉山", "03");
        stationMap.put("福州火车站", "04");
        stationMap.put("斗门", "05");
        stationMap.put("树兜", "06");
        stationMap.put("屏山", "07");
        stationMap.put("东街口", "08");
        stationMap.put("南门兜", "09");
        stationMap.put("茶亭", "10");
        stationMap.put("达道", "11");
        stationMap.put("上藤", "12");
        stationMap.put("三叉街", "13");
        stationMap.put("白湖亭", "14");
        stationMap.put("葫芦阵", "15");
        stationMap.put("黄山", "16");
        stationMap.put("排下", "17");
        stationMap.put("城门", "18");
        stationMap.put("三角埕", "19");
        stationMap.put("胪雷", "20");
        stationMap.put("福州火车南站", "21");
        stationMap.put("安平", "22");
        stationMap.put("梁厝", "23");
        stationMap.put("下洋", "24");
        stationMap.put("三江口", "25");

        // 遍历映射，查找包含站名的部分匹配
        for (Map.Entry<String, String> entry : stationMap.entrySet()) {
            String key = entry.getKey();
            String value = entry.getValue();

            if (stationName.contains(key)) {
                // 找到部分匹配，返回包含中文名称和对应的value的数组
                return new String[]{key, value};
            }
        }

        // 如果没有匹配的部分，返回一个包含空字符串的数组
        return new String[]{"", ""};
    }

    public static boolean checkArrow(String input) {
        return input.contains("有箭头");
    }
}
