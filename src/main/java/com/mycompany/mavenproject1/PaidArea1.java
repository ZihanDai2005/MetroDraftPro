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
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfPage;
import com.itextpdf.kernel.pdf.PdfReader;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.kernel.pdf.WriterProperties;
import com.itextpdf.kernel.pdf.canvas.PdfCanvas;
import com.itextpdf.kernel.pdf.canvas.PdfCanvasConstants;
import com.itextpdf.kernel.pdf.xobject.PdfFormXObject;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.element.Image;
import com.itextpdf.svg.converter.SvgConverter;
import com.itextpdf.svg.processors.ISvgConverterProperties;
import com.itextpdf.svg.processors.impl.SvgConverterProperties;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFCell;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.Random;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 *
 * @author daizhenjin
 */
public class PaidArea1 {

    public static void main(ArrayList<Object> dataList) throws FileNotFoundException, IOException {
        final int starterX = (int) dataList.get(0);
        final int starterY = (int) dataList.get(1);
        final float pageSizeX = (int) dataList.get(2) * 2.83464567f;
        final float pageSizeY = (int) dataList.get(3) * 2.83464567f;
        final int startRow = 2;
        final int col = (int) dataList.get(6);
        File f = (File) dataList.get(7);
        String path = (String) dataList.get(8);

        final float differenceX = pageSizeX - 685 * 2.83464567f;
        final float differenceY = pageSizeY - 1725 * 2.83464567f;

        PdfFont heiTi;
        PdfFont arial;
        PdfFont arialBold;
        try (InputStream fontStream1 = UnpaidArea.class.getResourceAsStream("/fonts/simhei.ttf")) {
            heiTi = PdfFontFactory.createFont(StreamUtil.inputStreamToArray(fontStream1), PdfEncodings.IDENTITY_H, PdfFontFactory.EmbeddingStrategy.PREFER_EMBEDDED);
        }
        try (InputStream fontStream2 = UnpaidArea.class.getResourceAsStream("/fonts/arial.ttf")) {
            arial = PdfFontFactory.createFont(StreamUtil.inputStreamToArray(fontStream2), PdfEncodings.IDENTITY_H, PdfFontFactory.EmbeddingStrategy.PREFER_EMBEDDED);
        }
        try (InputStream fontStream3 = UnpaidArea.class.getResourceAsStream("/fonts/arial bold.ttf")) {
            arialBold = PdfFontFactory.createFont(StreamUtil.inputStreamToArray(fontStream3), PdfEncodings.IDENTITY_H, PdfFontFactory.EmbeddingStrategy.PREFER_EMBEDDED);
        }

        final double lineWidth = pageSizeX - (28.641 * 2) * 2.83464567f;
        final double interval = 93.741 * 2.83464567f;

        final float bottomTop = (float) (172.534 * 2.83464567f) + differenceY;
        final float bottomDifference = bottomTop - (float) 49.423 * 2.83464567f;
        String stationNumber = "";
        String direction = "left";
        boolean arrowType = true;
        int entranceNum = 1;
        int upNum = 6;
        int downNum = 7;
        int lineType = 2;

        String firstEntrance = "";
        String secondEntrance = "";
        String thirdEntrance = "";
        String fourthEntrance = "";

        ArrayList<String[]> exitInfo1 = new ArrayList<>();
        ArrayList<String[]> exitInfo2 = new ArrayList<>();
        ArrayList<String[]> exitInfo3 = new ArrayList<>();
        ArrayList<String[]> exitInfo4 = new ArrayList<>();

        ArrayList<String> facilityInfo1 = new ArrayList<>();
        ArrayList<String> facilityInfo2 = new ArrayList<>();
        ArrayList<String> facilityInfo3 = new ArrayList<>();
        ArrayList<String> facilityInfo4 = new ArrayList<>();

        ArrayList<String[]> facilityInfoBottom = new ArrayList<>();

        try {
            BufferedInputStream bis = new BufferedInputStream(new FileInputStream(f));
            XSSFWorkbook workbook = new XSSFWorkbook(bis);
            XSSFSheet sheet = workbook.getSheetAt(0);

            int currentCol = col;
            int totalCol = 0;
            while (sheet.getRow(startRow + 3).getCell(currentCol) != null && sheet.getRow(startRow + 3).getCell(currentCol).getStringCellValue() != "") {
                System.out.println(sheet.getRow(startRow + 3).getCell(currentCol));
                totalCol += 1;
                currentCol += 1;
            }
            System.out.println(totalCol);

            entranceNum = totalCol;
            //继续回到这里 判断上？下？

            //第一个出口的信息
            for (int row = startRow + 4; row <= startRow + 25;) {
                XSSFCell chineseCell = sheet.getRow(row).getCell(col);
                XSSFCell englishCell = sheet.getRow(row + 1).getCell(col);
                if (chineseCell != null && englishCell != null) {
                    String chinese = replaceChars(chineseCell.getStringCellValue());
                    String english = replaceChars(englishCell.getStringCellValue());
                    if (english.equals("") == false) {
                        exitInfo1.add(new String[]{chinese, english});
                    }
                }
                row += 2;
            }

            XSSFCell exitCell1 = sheet.getRow(startRow + 3).getCell(col);
            firstEntrance = findEnglishLetters(exitCell1.getStringCellValue()); //字母编号
            path += firstEntrance;
            facilityInfo1 = findFacilityInfo(exitCell1.getStringCellValue()); //当前出口设施

            //第二个出口的信息
            if (entranceNum > 1) {
                for (int row = startRow + 4; row <= startRow + 25;) {
                    XSSFCell chineseCell = sheet.getRow(row).getCell(col + 1);
                    XSSFCell englishCell = sheet.getRow(row + 1).getCell(col + 1);
                    if (chineseCell != null && englishCell != null) {
                        String chinese = replaceChars(chineseCell.getStringCellValue());
                        String english = replaceChars(englishCell.getStringCellValue());
                        if (english.equals("") == false) {
                            exitInfo2.add(new String[]{chinese, english});
                        }
                    }
                    row += 2;
                }

                XSSFCell exitCell2 = sheet.getRow(startRow + 3).getCell(col + 1);
                secondEntrance = findEnglishLetters(exitCell2.getStringCellValue()); //字母编号2
                path += secondEntrance;
                facilityInfo2 = findFacilityInfo(exitCell2.getStringCellValue()); //当前出口设施2
            }

            //第三个出口的信息
            if (entranceNum > 2) {
                for (int row = startRow + 4; row <= startRow + 25;) {
                    XSSFCell chineseCell = sheet.getRow(row).getCell(col + 2);
                    XSSFCell englishCell = sheet.getRow(row + 1).getCell(col + 2);
                    if (chineseCell != null && englishCell != null) {
                        String chinese = replaceChars(chineseCell.getStringCellValue());
                        String english = replaceChars(englishCell.getStringCellValue());
                        if (english.equals("") == false) {
                            exitInfo3.add(new String[]{chinese, english});
                        }
                    }
                    row += 2;
                }

                XSSFCell exitCell3 = sheet.getRow(startRow + 3).getCell(col + 2);
                thirdEntrance = findEnglishLetters(exitCell3.getStringCellValue()); //字母编号2
                path += thirdEntrance;
                facilityInfo3 = findFacilityInfo(exitCell3.getStringCellValue()); //当前出口设施2
            }

            //第四个出口的信息
            if (entranceNum > 3) {
                for (int row = startRow + 4; row <= startRow + 25;) {
                    XSSFCell chineseCell = sheet.getRow(row).getCell(col + 3);
                    XSSFCell englishCell = sheet.getRow(row + 1).getCell(col + 3);
                    if (chineseCell != null && englishCell != null) {
                        String chinese = replaceChars(chineseCell.getStringCellValue());
                        String english = replaceChars(englishCell.getStringCellValue());
                        if (english.equals("") == false) {
                            exitInfo4.add(new String[]{chinese, english});
                        }
                    }
                    row += 2;
                }

                XSSFCell exitCell4 = sheet.getRow(startRow + 3).getCell(col + 3);
                fourthEntrance = findEnglishLetters(exitCell4.getStringCellValue()); //字母编号4
                path += fourthEntrance;
                facilityInfo4 = findFacilityInfo(exitCell4.getStringCellValue()); //当前出口设施2
            }

            XSSFCell stationCell = sheet.getRow(0).getCell(0);
            stationNumber = getStationNumber(stationCell.getStringCellValue())[1]; //二维码编号
            path += getStationNumber(stationCell.getStringCellValue())[0]; //文件名

            XSSFCell arrowCell = sheet.getRow(startRow + 2).getCell(col);
            direction = detectArrowDirection(arrowCell.getStringCellValue()); //对齐方向

            XSSFCell bottomFacilityCell = sheet.getRow(startRow + 26).getCell(col);
            facilityInfoBottom = parseFacilityInfo(bottomFacilityCell.getStringCellValue()); //其他出口设施

            XSSFCell whetherArrowCell = sheet.getRow(startRow + 2).getCell(col);
            arrowType = checkArrow(whetherArrowCell.getStringCellValue()); //是否带箭头
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        Random random = new Random();
        path += "_" + Integer.toString(random.nextInt(900) + 100) + ".pdf";

//        PdfWriter pdfWriter = new PdfWriter(path, new WriterProperties().setCompressionLevel(0));
//        PdfDocument pdfDocument = new PdfDocument(pdfWriter);

        PdfDocument pdfDocument = (PdfDocument) dataList.get(4);
        PdfPage page = (PdfPage) dataList.get(5);

        if (entranceNum == 1) {
            upNum = 14;
        }

        Document document = new Document(pdfDocument);

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
        float qrBaseX = (float) (pageSizeX - 80.906 * 2.83464567f);
        float qrBaseY = bottomTop - bottomDifference / 2 - (float) 50.15 * 2.83464567f / 2 + (float) 0.12556 * bottomDifference;
        canvas.saveState()
                .setFillColor(backgroundColor)
                .rectangle(qrBaseX, qrBaseY, 47.574 * 2.83464567f, 50.15 * 2.83464567f)
                .fill()
                .restoreState();

        //qr code text
        PdfCanvas canvas6 = new PdfCanvas(page);
        canvas6.beginText();
        canvas6.setFontAndSize(heiTi, 21);
        backgroundColor = new DeviceCmyk(0, 0, 0, 100);
        canvas6.setFillColor(backgroundColor);
        canvas6.setCharacterSpacing((float) 1.8);
        canvas6.moveText(qrBaseX, qrBaseY - 11.093 * 2.83464567f); //设置文本的起始位置
        canvas6.showText("扫码获取实时");
        canvas6.endText();

        canvas6.beginText();
        canvas6.moveText(qrBaseX, qrBaseY - 20.286 * 2.83464567f); //设置文本的起始位置
        canvas6.showText("公交换乘信息");
        canvas6.endText();

        //qr code image
        ImageData data;
        try (InputStream qrCodeStream = UnpaidArea.class.getClassLoader().getResourceAsStream("images/qrcode" + stationNumber + ".png")) {
            data = ImageDataFactory.create(StreamUtil.inputStreamToArray(qrCodeStream));
        }
        Image qrCode = new Image(data);
        qrCode.setFixedPosition((float) ((47.574 * 2.83464567f - 44.409 * 2.83464567) / 2 + qrBaseX), (float) ((50.15 * 2.83464567f - 46.945 * 2.83464567) / 2 + qrBaseY));
        qrCode.setWidth((float) 44.409 * 2.83464567f);
        qrCode.setHeight((float) 46.945 * 2.83464567f);
        document.add(qrCode);

        float xOutline = 0;
        float yOutline = 0;
        float yOutline2 = 0;

        if (entranceNum == 1) { //单14 横线
            backgroundColor = new DeviceCmyk(0, 0, 0, 100);
            float y = bottomTop;
            for (int i = 1; i <= upNum; i++) { //Down
                canvas.saveState()
                        .setFillColor(backgroundColor)
                        .rectangle(28.641 * 2.83464567f, y, lineWidth, 1.578 * 2.83464567f)
                        .fill()
                        .restoreState();
                y += interval;
            }
            //字母外框
            if (direction.equals("right")) {
                xOutline = (float) (((pageSizeX / 2.83464567f) - 101.805) * 2.83464567f);
            } else {
                xOutline = (float) 28.83 * 2.83464567f;
            }
            yOutline = (float) (((topsBottom / 2.83464567f) - 112.643) * 2.83464567f);
            PdfCanvas canvas3 = new PdfCanvas(pdfDocument.getFirstPage());
            try (InputStream pdfStreamIcon = UnpaidArea.class.getResourceAsStream("/pdfs/paid/outline.pdf")) {
                PdfDocument srcPdfDocument = new PdfDocument(new PdfReader(pdfStreamIcon));
                PdfFormXObject pageXObject = srcPdfDocument.getFirstPage().copyAsFormXObject(pdfDocument);
                srcPdfDocument.close();

                canvas3.addXObject(pageXObject, xOutline, yOutline);
            }

            //出入口字母
            canvas3.beginText();
            canvas3.setFontAndSize(arialBold, 200);
            if (firstEntrance.length() != 1) {

            } else if (firstEntrance.equals("B") || firstEntrance.equals("D") || firstEntrance.equals("G")) {
                float textHeight = arialBold.getAscent(firstEntrance, 200) - arialBold.getDescent(firstEntrance, 200);
                float textWidth = arialBold.getWidth(firstEntrance, 200);
                canvas3.moveText(xOutline + (((73.16 - textWidth / 2.83464567f) / 2) + 0.8) * 2.83464567f, yOutline + ((73.16 - textHeight / 2.83464567f) / 2) * 2.83464567f); //设置文本的起始位置
                canvas3.showText(firstEntrance);
                canvas3.endText();
            } else {
                float textHeight = arialBold.getAscent(firstEntrance, 200) - arialBold.getDescent(firstEntrance, 200);
                float textWidth = arialBold.getWidth(firstEntrance, 200);
                canvas3.moveText(xOutline + ((73.16 - textWidth / 2.83464567f) / 2) * 2.83464567f, yOutline + ((73.16 - textHeight / 2.83464567f) / 2) * 2.83464567f); //设置文本的起始位置
                canvas3.showText(firstEntrance);
                canvas3.endText();
            }
            canvas3.endText();

        } else { //双6/7 横线
            backgroundColor = new DeviceCmyk(0, 0, 0, 100);
            float y = bottomTop;
            for (int i = 1; i <= downNum; i++) { //Down
                canvas.saveState()
                        .setFillColor(backgroundColor)
                        .rectangle(28.641 * 2.83464567f, y, lineWidth, 1.578 * 2.83464567f)
                        .fill()
                        .restoreState();
                y += interval;
            }
            y += interval;
            for (int i = 1; i <= upNum; i++) { //Up
                canvas.saveState()
                        .setFillColor(backgroundColor)
                        .rectangle(28.641 * 2.83464567f, y, lineWidth, 1.578 * 2.83464567f)
                        .fill()
                        .restoreState();
                y += interval;
            }

            //字母外框
            if (direction.equals("right")) {
                xOutline = (float) (((pageSizeX / 2.83464567f) - 101.805) * 2.83464567f);
            } else {
                xOutline = (float) 28.83 * 2.83464567f;
            }
            yOutline = (float) (((topsBottom / 2.83464567f) - 112.643) * 2.83464567f);
            yOutline2 = (float) (yOutline - (92.698 + 94.784) * 2.83464567f - (upNum - 2) * interval);
            yOutline2 -= (float) 93.2 * 2.83464567f;
            PdfCanvas canvas3 = new PdfCanvas(pdfDocument.getFirstPage());
            try (InputStream pdfStreamIcon = UnpaidArea.class.getResourceAsStream("/pdfs/paid/outline.pdf")) {
                PdfDocument srcPdfDocument = new PdfDocument(new PdfReader(pdfStreamIcon));
                PdfFormXObject pageXObject = srcPdfDocument.getFirstPage().copyAsFormXObject(pdfDocument);
                srcPdfDocument.close();

                canvas3.addXObject(pageXObject, xOutline, yOutline);
                canvas3.addXObject(pageXObject, xOutline, yOutline2);
            }

            //上出入口字母
            canvas3.beginText();
            canvas3.setFontAndSize(arialBold, 200);
            if (firstEntrance.length() != 1) {

            } else if (firstEntrance.equals("B") || firstEntrance.equals("D") || firstEntrance.equals("G")) {
                float textHeight = arialBold.getAscent(firstEntrance, 200) - arialBold.getDescent(firstEntrance, 200);
                float textWidth = arialBold.getWidth(firstEntrance, 200);
                canvas3.moveText(xOutline + (((73.16 - textWidth / 2.83464567f) / 2) + 0.8) * 2.83464567f, yOutline + ((73.16 - textHeight / 2.83464567f) / 2) * 2.83464567f); //设置文本的起始位置
                canvas3.showText(firstEntrance);
                canvas3.endText();
            } else {
                float textHeight = arialBold.getAscent(firstEntrance, 200) - arialBold.getDescent(firstEntrance, 200);
                float textWidth = arialBold.getWidth(firstEntrance, 200);
                canvas3.moveText(xOutline + ((73.16 - textWidth / 2.83464567f) / 2) * 2.83464567f, yOutline + ((73.16 - textHeight / 2.83464567f) / 2) * 2.83464567f); //设置文本的起始位置
                canvas3.showText(firstEntrance);
                canvas3.endText();
            }
            canvas3.endText();

            //下出入口字母
            canvas3.beginText();
            canvas3.setFontAndSize(arialBold, 200);
            if (secondEntrance.length() != 1) {

            } else if (secondEntrance.equals("B") || secondEntrance.equals("D") || secondEntrance.equals("G")) {
                float textHeight = arialBold.getAscent(secondEntrance, 200) - arialBold.getDescent(secondEntrance, 200);
                float textWidth = arialBold.getWidth(secondEntrance, 200);
                canvas3.moveText(xOutline + (((73.16 - textWidth / 2.83464567f) / 2) + 0.8) * 2.83464567f, yOutline2 + ((73.16 - textHeight / 2.83464567f) / 2) * 2.83464567f); //设置文本的起始位置
                canvas3.showText(secondEntrance);
                canvas3.endText();
            } else {
                float textHeight = arialBold.getAscent(secondEntrance, 200) - arialBold.getDescent(secondEntrance, 200);
                float textWidth = arialBold.getWidth(secondEntrance, 200);
                canvas3.moveText(xOutline + ((73.16 - textWidth / 2.83464567f) / 2) * 2.83464567f, yOutline2 + ((73.16 - textHeight / 2.83464567f) / 2) * 2.83464567f); //设置文本的起始位置
                canvas3.showText(secondEntrance);
                canvas3.endText();
            }
            canvas3.endText();
        }

        backgroundColor = new DeviceCmyk(90, 0, 100, 0);
        PdfFormXObject pageXExit;
        try (InputStream pdfStreamIcon = UnpaidArea.class.getResourceAsStream("/pdfs/paid/exit.pdf")) {
            PdfDocument srcPdfDocument = new PdfDocument(new PdfReader(pdfStreamIcon));
            pageXExit = srcPdfDocument.getFirstPage().copyAsFormXObject(pdfDocument);
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
                canvas2.addXObject(pageXExit, xExit, yExit);
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
                canvas2.addXObject(pageXExit, xExit, yExit);
            }
        } else { //带箭头
            PdfFormXObject pageXArrow;
            float y = (float) (((topsBottom / 2.83464567f) + 20.37) * 2.83464567f);
            float x;
            PdfCanvas canvas2 = new PdfCanvas(pdfDocument.getFirstPage());
            float yExit = (float) (((topsBottom / 2.83464567f) + 22.164) * 2.83464567f);
            if (direction.equals("right")) { //右对齐 右箭头
                try (InputStream pdfStreamIcon = UnpaidArea.class.getResourceAsStream("/pdfs/paid/arrow_right.pdf")) {
                    PdfDocument srcPdfDocument = new PdfDocument(new PdfReader(pdfStreamIcon));
                    pageXArrow = srcPdfDocument.getFirstPage().copyAsFormXObject(pdfDocument);
                    srcPdfDocument.close();
                }
                x = (float) (((pageSizeX / 2.83464567f) - 20.211 - 88.173) * 2.83464567f);
                canvas.saveState()
                        .setFillColor(backgroundColor)
                        .rectangle(((x / 2.83464567f) - 20.211 - 128.5) * 2.83464567f, topsBottom, 128.5 * 2.83464567f, 128.5 * 2.83464567f)
                        .fill()
                        .restoreState();
                canvas2.addXObject(pageXExit, (float) (((x / 2.83464567f) - 20.211 - 128.5 + 32.89) * 2.83464567f), yExit);
            } else { //左对齐 左箭头
                try (InputStream pdfStreamIcon = UnpaidArea.class.getResourceAsStream("/pdfs/paid/arrow_left.pdf")) {
                    PdfDocument srcPdfDocument = new PdfDocument(new PdfReader(pdfStreamIcon));
                    pageXArrow = srcPdfDocument.getFirstPage().copyAsFormXObject(pdfDocument);
                    srcPdfDocument.close();
                }
                x = (float) (20.211 * 2.83464567f / 0.883);
                canvas.saveState()
                        .setFillColor(backgroundColor)
                        .rectangle(((x / 2.83464567f) + 20.211 + 88.173) * 2.83464567f, topsBottom, 128.5 * 2.83464567f, 128.5 * 2.83464567f)
                        .fill()
                        .restoreState();
                canvas2.addXObject(pageXExit, (float) ((x / 2.83464567f) + 20.211 + 88.173 + 32.89) * 2.83464567f, yExit);
            }
            canvas2.addXObject(pageXArrow, x, y);
        }

        try (InputStream pdfStreamIcon = UnpaidArea.class.getResourceAsStream("/pdfs/paid/logo.pdf")) {
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

        if (entranceNum == 1 || (exitInfo2.isEmpty() && exitInfo3.isEmpty() && exitInfo4.isEmpty())) { //二级信息列表 单出入口
            int currentRow = 1;
            double y = yOutline - 54.27 * 2.83464567f;
            double y2 = yOutline - 80.22 * 2.83464567f;
            double x;
            double x2;
            int chineseSpace = 7;
            //Up
            for (String[] element : exitInfo1) {
                //中文
                PdfCanvas canvas4 = new PdfCanvas(page);
                canvas4.beginText();
                canvas4.setFontAndSize(heiTi, 100);
                canvas4.setCharacterSpacing(chineseSpace);
                canvas4.setTextRenderingMode(PdfCanvasConstants.TextRenderingMode.FILL_STROKE);
                canvas4.setLineWidth(0.21f);
                backgroundColor = new DeviceCmyk(0, 0, 0, 100);
                canvas4.setStrokeColor(backgroundColor);
                canvas4.setFillColor(backgroundColor);
                float textWidth = heiTi.getWidth(element[0], 100) + chineseSpace * (element[0].length() - 1);
                double scale = 1;
                if (currentRow <= upNum && currentRow + upNum <= exitInfo1.size() && textWidth >= (lineWidth - 10) * 0.49) {
                    scale = (lineWidth - 10) * 0.49 / textWidth;
                    canvas4.saveState();
                    canvas4.setTextMatrix((float) scale, 0, 0, 1, 0, 0);
                    textWidth = textWidth * (float) scale;
                } else if (currentRow <= upNum && textWidth >= lineWidth - 10) {
                    scale = (lineWidth - 15) / textWidth;
                    canvas4.saveState();
                    canvas4.setTextMatrix((float) scale, 0, 0, 1, 0, 0);
                    textWidth = textWidth * (float) scale;
                } else if (currentRow > upNum && textWidth >= (lineWidth - 10) * 0.405) {
                    scale = (lineWidth - 10) * 0.405 / textWidth;
                    canvas4.saveState();
                    canvas4.setTextMatrix((float) scale, 0, 0, 1, 0, 0);
                    textWidth = textWidth * (float) scale;
                }
                if (currentRow <= upNum) {
                    if (direction.equals("right")) {
                        x = pageSizeX - 32.93 * 2.83464567f - textWidth;
                    } else {
                        x = 32.93 * 2.83464567f;
                    }
                } else {
                    if (direction.equals("right")) {
                        x = pageSizeX - 33.93 * 2.83464567f - textWidth - (lineWidth - 10) * 0.59;
                    } else {
                        x = 33.93 * 2.83464567f + (lineWidth - 10) * 0.59;
                    }
                }
                canvas4.moveText(x / scale, y); //设置文本的起始位置
                canvas4.showText(element[0]);
                y -= interval;
                canvas4.endText();

                //英文
                PdfCanvas canvas5 = new PdfCanvas(page);
                canvas5.beginText();
                canvas5.setFontAndSize(arial, 66);
                canvas5.setCharacterSpacing(0);
                canvas5.setTextRenderingMode(PdfCanvasConstants.TextRenderingMode.FILL_STROKE);
                canvas5.setLineWidth(0.21f);
                canvas5.setStrokeColor(backgroundColor);
                canvas5.setFillColor(backgroundColor);
                float textWidth2 = arial.getWidth(element[1], 66);
                scale = 1;
                if (currentRow <= upNum && currentRow + upNum <= exitInfo1.size() && textWidth2 >= (lineWidth - 10) * 0.49) {
                    scale = (lineWidth - 10) * 0.49 / textWidth2;
                    canvas5.saveState();
                    canvas5.setTextMatrix((float) scale, 0, 0, 1, 0, 0);
                    textWidth2 = textWidth2 * (float) scale;
                } else if (currentRow <= upNum && textWidth2 >= lineWidth - 10) {
                    scale = (lineWidth - 15) / textWidth2;
                    canvas5.saveState();
                    canvas5.setTextMatrix((float) scale, 0, 0, 1, 0, 0);
                    textWidth2 = textWidth2 * (float) scale;
                } else if (currentRow > upNum && textWidth2 >= (lineWidth - 10) * 0.405) {
                    scale = (lineWidth - 10) * 0.405 / textWidth2;
                    canvas5.saveState();
                    canvas5.setTextMatrix((float) scale, 0, 0, 1, 0, 0);
                    textWidth2 = textWidth2 * (float) scale;
                }
                if (currentRow <= upNum) {
                    if (direction.equals("right")) {
                        x2 = pageSizeX - 33.957 * 2.83464567f - textWidth2;
                    } else {
                        x2 = 33.957 * 2.83464567f;
                    }
                } else {
                    if (direction.equals("right")) {
                        x2 = pageSizeX - 33.957 * 2.83464567f - textWidth2 - (lineWidth - 10) * 0.59;
                    } else {
                        x2 = 33.957 * 2.83464567f + (lineWidth - 10) * 0.59;
                    }
                }
                canvas5.moveText(x2 / scale, y2); //设置文本的起始位置
                canvas5.showText(element[1]);
                y2 -= interval;
                canvas5.endText();

                currentRow += 1;
                if (currentRow == upNum + 1) {
                    y = yOutline - 54.27 * 2.83464567f;
                    y2 = yOutline - 80.22 * 2.83464567f;
                }
            }
        } else { //二级信息列表 双出入口
            int currentRow = 1;
            double y = yOutline - 54.27 * 2.83464567f;
            double y2 = yOutline - 80.22 * 2.83464567f;
            double x;
            double x2;
            int chineseSpace = 7;
            //Up
            for (String[] element : exitInfo1) {
                //中文
                PdfCanvas canvas4 = new PdfCanvas(page);
                canvas4.beginText();
                canvas4.setFontAndSize(heiTi, 100);
                canvas4.setCharacterSpacing(chineseSpace);
                canvas4.setTextRenderingMode(PdfCanvasConstants.TextRenderingMode.FILL_STROKE);
                canvas4.setLineWidth(0.21f);
                backgroundColor = new DeviceCmyk(0, 0, 0, 100);
                canvas4.setStrokeColor(backgroundColor);
                canvas4.setFillColor(backgroundColor);
                float textWidth = heiTi.getWidth(element[0], 100) + chineseSpace * (element[0].length() - 1);
                double scale = 1;
                if (currentRow <= upNum && currentRow + upNum <= exitInfo1.size() && textWidth >= (lineWidth - 10) * 0.49) {
                    scale = (lineWidth - 10) * 0.49 / textWidth;
                    canvas4.saveState();
                    canvas4.setTextMatrix((float) scale, 0, 0, 1, 0, 0);
                    textWidth = textWidth * (float) scale;
                } else if (currentRow <= upNum && textWidth >= lineWidth - 10) {
                    scale = (lineWidth - 15) / textWidth;
                    canvas4.saveState();
                    canvas4.setTextMatrix((float) scale, 0, 0, 1, 0, 0);
                    textWidth = textWidth * (float) scale;
                } else if (currentRow > upNum && textWidth >= (lineWidth - 10) * 0.405) {
                    scale = (lineWidth - 10) * 0.405 / textWidth;
                    canvas4.saveState();
                    canvas4.setTextMatrix((float) scale, 0, 0, 1, 0, 0);
                    textWidth = textWidth * (float) scale;
                }
                if (currentRow <= upNum) {
                    if (direction.equals("right")) {
                        x = pageSizeX - 32.93 * 2.83464567f - textWidth;
                    } else {
                        x = 32.93 * 2.83464567f;
                    }
                } else {
                    if (direction.equals("right")) {
                        x = pageSizeX - 33.93 * 2.83464567f - textWidth - (lineWidth - 10) * 0.59;
                    } else {
                        x = 33.93 * 2.83464567f + (lineWidth - 10) * 0.59;
                    }
                }
                canvas4.moveText(x / scale, y); //设置文本的起始位置
                canvas4.showText(element[0]);
                y -= interval;
                canvas4.endText();

                //英文
                PdfCanvas canvas5 = new PdfCanvas(page);
                canvas5.beginText();
                canvas5.setFontAndSize(arial, 66);
                canvas5.setCharacterSpacing(0);
                canvas5.setTextRenderingMode(PdfCanvasConstants.TextRenderingMode.FILL_STROKE);
                canvas5.setLineWidth(0.21f);
                canvas5.setStrokeColor(backgroundColor);
                canvas5.setFillColor(backgroundColor);
                float textWidth2 = arial.getWidth(element[1], 66);
                scale = 1;
                if (currentRow <= upNum && currentRow + upNum <= exitInfo1.size() && textWidth2 >= (lineWidth - 10) * 0.49) {
                    scale = (lineWidth - 10) * 0.49 / textWidth2;
                    canvas5.saveState();
                    canvas5.setTextMatrix((float) scale, 0, 0, 1, 0, 0);
                    textWidth2 = textWidth2 * (float) scale;
                } else if (currentRow <= upNum && textWidth2 >= lineWidth - 10) {
                    scale = (lineWidth - 15) / textWidth2;
                    canvas5.saveState();
                    canvas5.setTextMatrix((float) scale, 0, 0, 1, 0, 0);
                    textWidth2 = textWidth2 * (float) scale;
                } else if (currentRow > upNum && textWidth2 >= (lineWidth - 10) * 0.405) {
                    scale = (lineWidth - 10) * 0.405 / textWidth2;
                    canvas5.saveState();
                    canvas5.setTextMatrix((float) scale, 0, 0, 1, 0, 0);
                    textWidth2 = textWidth2 * (float) scale;
                }
                if (currentRow <= upNum) {
                    if (direction.equals("right")) {
                        x2 = pageSizeX - 33.957 * 2.83464567f - textWidth2;
                    } else {
                        x2 = 33.957 * 2.83464567f;
                    }
                } else {
                    if (direction.equals("right")) {
                        x2 = pageSizeX - 33.957 * 2.83464567f - textWidth2 - (lineWidth - 10) * 0.59;
                    } else {
                        x2 = 33.957 * 2.83464567f + (lineWidth - 10) * 0.59;
                    }
                }
                canvas5.moveText(x2 / scale, y2); //设置文本的起始位置
                canvas5.showText(element[1]);
                y2 -= interval;
                canvas5.endText();

                currentRow += 1;
                if (currentRow == upNum + 1) {
                    y = yOutline - 54.27 * 2.83464567f;
                    y2 = yOutline - 80.22 * 2.83464567f;
                }
            }

            currentRow = 1;
            y = yOutline2 - 54.27 * 2.83464567f;
            y2 = yOutline2 - 80.22 * 2.83464567f;

            //Down
            for (String[] element : exitInfo2) {
                //中文
                PdfCanvas canvas4 = new PdfCanvas(page);
                canvas4.beginText();
                canvas4.setFontAndSize(heiTi, 100);
                canvas4.setCharacterSpacing(chineseSpace);
                canvas4.setTextRenderingMode(PdfCanvasConstants.TextRenderingMode.FILL_STROKE);
                canvas4.setLineWidth(0.21f);
                backgroundColor = new DeviceCmyk(0, 0, 0, 100);
                canvas4.setStrokeColor(backgroundColor);
                canvas4.setFillColor(backgroundColor);
                float textWidth = heiTi.getWidth(element[0], 100) + chineseSpace * (element[0].length() - 1);
                double scale = 1;
                if (currentRow <= downNum && currentRow + downNum <= exitInfo2.size() && textWidth >= (lineWidth - 10) * 0.49) {
                    scale = (lineWidth - 10) * 0.49 / textWidth;
                    canvas4.saveState();
                    canvas4.setTextMatrix((float) scale, 0, 0, 1, 0, 0);
                    textWidth = textWidth * (float) scale;
                } else if (currentRow <= downNum && textWidth >= lineWidth - 10) {
                    scale = (lineWidth - 15) / textWidth;
                    canvas4.saveState();
                    canvas4.setTextMatrix((float) scale, 0, 0, 1, 0, 0);
                    textWidth = textWidth * (float) scale;
                } else if (currentRow > downNum && textWidth >= (lineWidth - 10) * 0.405) {
                    scale = (lineWidth - 10) * 0.405 / textWidth;
                    canvas4.saveState();
                    canvas4.setTextMatrix((float) scale, 0, 0, 1, 0, 0);
                    textWidth = textWidth * (float) scale;
                }
                if (currentRow <= downNum) {
                    if (direction.equals("right")) {
                        x = pageSizeX - 32.93 * 2.83464567f - textWidth;
                    } else {
                        x = 32.93 * 2.83464567f;
                    }
                } else {
                    if (direction.equals("right")) {
                        x = pageSizeX - 33.93 * 2.83464567f - textWidth - (lineWidth - 10) * 0.59;
                    } else {
                        x = 33.93 * 2.83464567f + (lineWidth - 10) * 0.59;
                    }
                }
                canvas4.moveText(x / scale, y); //设置文本的起始位置
                canvas4.showText(element[0]);
                y -= interval;
                canvas4.endText();

                //英文
                PdfCanvas canvas5 = new PdfCanvas(page);
                canvas5.beginText();
                canvas5.setFontAndSize(arial, 66);
                canvas5.setCharacterSpacing(0);
                canvas5.setTextRenderingMode(PdfCanvasConstants.TextRenderingMode.FILL_STROKE);
                canvas5.setLineWidth(0.21f);
                canvas5.setStrokeColor(backgroundColor);
                canvas5.setFillColor(backgroundColor);
                float textWidth2 = arial.getWidth(element[1], 66);
                scale = 1;
                if (currentRow <= downNum && currentRow + downNum <= exitInfo2.size() && textWidth2 >= (lineWidth - 10) * 0.49) {
                    scale = (lineWidth - 10) * 0.49 / textWidth2;
                    canvas5.saveState();
                    canvas5.setTextMatrix((float) scale, 0, 0, 1, 0, 0);
                    textWidth2 = textWidth2 * (float) scale;
                } else if (currentRow <= downNum && textWidth2 >= lineWidth - 10) {
                    scale = (lineWidth - 15) / textWidth2;
                    canvas5.saveState();
                    canvas5.setTextMatrix((float) scale, 0, 0, 1, 0, 0);
                    textWidth2 = textWidth2 * (float) scale;
                } else if (currentRow > downNum && textWidth2 >= (lineWidth - 10) * 0.405) {
                    scale = (lineWidth - 10) * 0.405 / textWidth2;
                    canvas5.saveState();
                    canvas5.setTextMatrix((float) scale, 0, 0, 1, 0, 0);
                    textWidth2 = textWidth2 * (float) scale;
                }
                if (currentRow <= downNum) {
                    if (direction.equals("right")) {
                        x2 = pageSizeX - 33.957 * 2.83464567f - textWidth2;
                    } else {
                        x2 = 33.957 * 2.83464567f;
                    }
                } else {
                    if (direction.equals("right")) {
                        x2 = pageSizeX - 33.957 * 2.83464567f - textWidth2 - (lineWidth - 10) * 0.59;
                    } else {
                        x2 = 33.957 * 2.83464567f + (lineWidth - 10) * 0.59;
                    }
                }
                canvas5.moveText(x2 / scale, y2); //设置文本的起始位置
                canvas5.showText(element[1]);
                y2 -= interval;
                canvas5.endText();

                currentRow += 1;
                if (currentRow == downNum + 1) {
                    y = yOutline2 - 54.27 * 2.83464567f;
                    y2 = yOutline2 - 80.22 * 2.83464567f;
                }
            }
        }

        //底部设施信息
        // 创建一个临时的 PdfFormXObject
        PageSize tempPageSize = new PageSize(pageSizeX, 72 * 2.83464567f);
        PdfFormXObject template = new PdfFormXObject(tempPageSize);
        //在临时 canvas 上添加内容
        PdfCanvas canvas7 = new PdfCanvas(template, pdfDocument);

        float infoYStarter = 0;
        float infoYStarterUpText = infoYStarter + (float) 35.495 * 2.83464567f;
        float infoYStarterDownText = infoYStarter + (float) 4.734 * 2.83464567f;
        final float maxInfoX = (float) 530 * 2.83464567f + differenceX;

        final float iconWidth = (float) 56.831 * 2.83464567f;
        final float iconInterval = (float) 5.549 * 2.83464567f;
        final float iconTextInterval = (float) 12.771 * 2.83464567f;
        final float spacing = (float) 6.2;

        float nextInfoX = 0;
        int index = 1;

        for (String[] element : facilityInfoBottom) {
            if (element[0].equals("卫生间")) {
                try (InputStream pdfStreamIcon = UnpaidArea.class.getResourceAsStream("/pdfs/paid/s_paid_toilet.pdf")) {
                    PdfDocument srcPdfDocument = new PdfDocument(new PdfReader(pdfStreamIcon));
                    PdfFormXObject pageXObject = srcPdfDocument.getFirstPage().copyAsFormXObject(pdfDocument);
                    srcPdfDocument.close();

                    canvas7.addXObject(pageXObject, nextInfoX, infoYStarter);
                }
                nextInfoX += iconWidth + iconInterval;

                try (InputStream pdfStreamIcon = UnpaidArea.class.getResourceAsStream("/pdfs/paid/s_paid_wheelchair.pdf")) {
                    PdfDocument srcPdfDocument = new PdfDocument(new PdfReader(pdfStreamIcon));
                    PdfFormXObject pageXObject = srcPdfDocument.getFirstPage().copyAsFormXObject(pdfDocument);
                    srcPdfDocument.close();

                    canvas7.addXObject(pageXObject, nextInfoX, infoYStarter);
                }
                nextInfoX += iconWidth + iconTextInterval;

                canvas7.beginText();
                canvas7.setFontAndSize(heiTi, 69);
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

                nextInfoX += heiTi.getWidth("位于", 69) + spacing * ("位于".length() - 1) + spacing + 1 * 2.83464567f;

                String place = element[1]; //"A口/B口通道"

                // 将字符串转换为字符数组
                char[] charArray = place.toCharArray();

                for (char c : charArray) {
                    canvas7.beginText();
                    if (Character.isDigit(c)) {
                        nextInfoX -= (0.5 * spacing);
                    }
                    canvas7.moveText(nextInfoX, infoYStarterDownText); //设置文本的起始位置
                    if (String.valueOf(c).matches("[A-Z]") || String.valueOf(c).equals("/")) {
                        canvas7.setFontAndSize(arial, 69);
                        nextInfoX += arial.getWidth(String.valueOf(c), 69) + spacing;
                    } else if (Character.isDigit(c)) {
                        canvas7.setFontAndSize(arial, 45);
                        nextInfoX += arial.getWidth(String.valueOf(c), 45) + spacing;
                    } else {
                        canvas7.setFontAndSize(heiTi, 69);
                        nextInfoX += heiTi.getWidth(String.valueOf(c), 69) + spacing;
                    }
                    canvas7.showText(String.valueOf(c));
                    canvas7.endText();
                }
            } else if (element[0].equals("母婴室")) {
                try (InputStream pdfStreamIcon = UnpaidArea.class.getResourceAsStream("/pdfs/paid/s_paid_nursing.pdf")) {
                    PdfDocument srcPdfDocument = new PdfDocument(new PdfReader(pdfStreamIcon));
                    PdfFormXObject pageXObject = srcPdfDocument.getFirstPage().copyAsFormXObject(pdfDocument);
                    srcPdfDocument.close();

                    canvas7.addXObject(pageXObject, nextInfoX, infoYStarter);
                }
                nextInfoX += iconWidth + iconTextInterval;

                canvas7.beginText();
                canvas7.setFontAndSize(heiTi, 69);
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

                nextInfoX += heiTi.getWidth("位于", 69) + spacing * ("位于".length() - 1) + spacing + 1 * 2.83464567f;

                String place = element[1]; //"A口/B口通道"

                //将字符串转换为字符数组
                char[] charArray = place.toCharArray();

                for (char c : charArray) {
                    canvas7.beginText();
                    if (Character.isDigit(c)) {
                        nextInfoX -= (0.5 * spacing);
                    }
                    canvas7.moveText(nextInfoX, infoYStarterDownText); //设置文本的起始位置
                    if (String.valueOf(c).matches("[A-Z]") || String.valueOf(c).equals("/")) {
                        canvas7.setFontAndSize(arial, 69);
                        nextInfoX += arial.getWidth(String.valueOf(c), 69) + spacing;
                    } else if (Character.isDigit(c)) {
                        canvas7.setFontAndSize(arial, 45);
                        nextInfoX += arial.getWidth(String.valueOf(c), 45) + spacing;
                    } else {
                        canvas7.setFontAndSize(heiTi, 69);
                        nextInfoX += heiTi.getWidth(String.valueOf(c), 69) + spacing;
                    }
                    canvas7.showText(String.valueOf(c));
                    canvas7.endText();
                }
            } else if (element[0].equals("电梯(站厅-地面)")) {
                try (InputStream pdfStreamIcon = UnpaidArea.class.getResourceAsStream("/pdfs/paid/s_paid_elevator.pdf")) {
                    PdfDocument srcPdfDocument = new PdfDocument(new PdfReader(pdfStreamIcon));
                    PdfFormXObject pageXObject = srcPdfDocument.getFirstPage().copyAsFormXObject(pdfDocument);
                    srcPdfDocument.close();

                    canvas7.addXObject(pageXObject, nextInfoX, infoYStarter);
                }
                nextInfoX += iconWidth + iconInterval;

                try (InputStream pdfStreamIcon = UnpaidArea.class.getResourceAsStream("/pdfs/paid/s_paid_wheelchair.pdf")) {
                    PdfDocument srcPdfDocument = new PdfDocument(new PdfReader(pdfStreamIcon));
                    PdfFormXObject pageXObject = srcPdfDocument.getFirstPage().copyAsFormXObject(pdfDocument);
                    srcPdfDocument.close();

                    canvas7.addXObject(pageXObject, nextInfoX, infoYStarter);
                }
                nextInfoX += iconWidth + iconTextInterval;

                canvas7.beginText();
                canvas7.setFontAndSize(heiTi, 69);
                canvas7.setTextRenderingMode(PdfCanvasConstants.TextRenderingMode.FILL_STROKE);
                canvas7.setLineWidth(0.5f);
                backgroundColor = new DeviceCmyk(0, 0, 0, 100);
                canvas7.setStrokeColor(backgroundColor);
                canvas7.setFillColor(backgroundColor);
                canvas7.setCharacterSpacing((float) spacing);
                canvas7.moveText(nextInfoX, infoYStarterUpText); //设置文本的起始位置
                canvas7.showText("电梯(站厅-地面)");
                canvas7.endText();

                canvas7.beginText();
                canvas7.moveText(nextInfoX, infoYStarterDownText); //设置文本的起始位置
                canvas7.showText("位于");
                canvas7.endText();

                final float tempWidthUp = heiTi.getWidth("电梯(站厅-地面)", 69) + spacing * ("电梯(站厅-地面)".length() - 2);//此处-2因为括号会多占空位 因此少加一个space
                float tempWidthDown = heiTi.getWidth("位于", 69) + spacing * ("位于".length() - 1) + spacing + 1 * 2.83464567f;
                float tempNextInfoX = nextInfoX + heiTi.getWidth("位于", 69) + spacing * ("位于".length() - 1) + spacing + 1 * 2.83464567f;

                String place = element[1]; //"A口/B口通道"

                //将字符串转换为字符数组
                char[] charArray = place.toCharArray();

                for (char c : charArray) {
                    canvas7.beginText();
                    if (Character.isDigit(c)) {
                        tempNextInfoX -= 0.5 * spacing;
                        tempWidthDown -= 0.5 * spacing;
                    }
                    canvas7.moveText(tempNextInfoX, infoYStarterDownText); //设置文本的起始位置
                    if (String.valueOf(c).matches("[A-Z]") || String.valueOf(c).equals("/")) {
                        canvas7.setFontAndSize(arial, 69);
                        tempNextInfoX += arial.getWidth(String.valueOf(c), 69) + spacing;
                        tempWidthDown += arial.getWidth(String.valueOf(c), 69) + spacing;
                    } else if (Character.isDigit(c)) {
                        canvas7.setFontAndSize(arial, 45);
                        tempNextInfoX += arial.getWidth(String.valueOf(c), 45) + spacing;
                        tempWidthDown += arial.getWidth(String.valueOf(c), 45) + spacing;
                    } else {
                        canvas7.setFontAndSize(heiTi, 69);
                        tempNextInfoX += heiTi.getWidth(String.valueOf(c), 69) + spacing;
                        tempWidthDown += heiTi.getWidth(String.valueOf(c), 69) + spacing;
                    }
                    canvas7.showText(String.valueOf(c));
                    canvas7.endText();
                }

                if (tempWidthUp > tempWidthDown) { //判断第一行文字和第二行文字哪个更长 以长的为准
                    //nextInfoX += tempWidthUp + iconTextInterval + 3 * 2.83464567f;
                    nextInfoX += tempWidthUp;
                } else {
                    nextInfoX = tempNextInfoX;
                }
            }
            nextInfoX -= spacing;

            if (index < facilityInfoBottom.size()) {
                nextInfoX += iconTextInterval + 3 * 2.83464567f;

                //竖线
                canvas7.saveState()
                        .setFillColor(backgroundColor)
                        .rectangle(nextInfoX, infoYStarter, 1.279 * 2.83464567f, iconWidth)
                        .fill()
                        .restoreState();

                nextInfoX += 1.279 * 2.83464567f + iconTextInterval + 4 * 2.83464567f;
            }

            index++;
        }

        float scale = maxInfoX / nextInfoX;
        float correctX = (float) 28.641 * 2.83464567f;
        float correctY;
        if (scale < 1) {
            correctY = bottomTop - bottomDifference / 2 - (float) 56.831 * scale * 2.83464567f / 2 + (float) 0.05694 * bottomDifference;
        } else {
            correctY = bottomTop - bottomDifference / 2 - (float) 56.831 * 2.83464567f / 2 + (float) 0.05694 * bottomDifference;
        }
        PdfCanvas finalCanvas = new PdfCanvas(page);

        if (scale < 1) {
            finalCanvas.addXObjectWithTransformationMatrix(template, scale, 0, 0, scale, correctX, correctY);
        } else {
            finalCanvas.addXObjectWithTransformationMatrix(template, 1, 0, 0, 1, correctX, correctY);
        }

        //出入口上方图标
        //创建一个临时的 PdfFormXObject
        PdfFormXObject template2 = new PdfFormXObject(tempPageSize);
        //在临时 canvas 上添加内容
        PdfCanvas canvas8 = new PdfCanvas(template2, pdfDocument);

        final float iconExitInterval = (float) 15 * 2.83464567f;
        final float maxInfoX2 = (float) ((lineWidth + 28.641 * 2.83464567f) - ((28.83 + 73.16) * 2.83464567f + iconExitInterval));

        final float iconWidth2 = (float) 70.649 * 2.83464567f;
        final float iconInterval2 = (float) 8.603 * 2.83464567f;

        if (direction.equals("left")) { //左对齐 设施图标
            float nextInfoX2 = 0;
            int index2 = 1;
            int publicTransport = 0;
            for (String element : facilityInfo1) {//计算交通设施数量 只有最后一个加竖线
                if (element.equals("卫生间") == false && element.equals("母婴室") == false && element.equals("垂梯") == false) {
                    publicTransport++;
                }
            }

            for (String element : facilityInfo1) {
                if (element.equals("卫生间")) {
                    try (InputStream pdfStreamIcon = UnpaidArea.class.getResourceAsStream("/pdfs/paid/paid_toilet.pdf")) {
                        PdfDocument srcPdfDocument = new PdfDocument(new PdfReader(pdfStreamIcon));
                        PdfFormXObject pageXObject = srcPdfDocument.getFirstPage().copyAsFormXObject(pdfDocument);
                        srcPdfDocument.close();

                        canvas8.addXObject(pageXObject, nextInfoX2, 0);
                    }
                    nextInfoX2 += iconWidth2 + iconInterval2;

                    try (InputStream pdfStreamIcon = UnpaidArea.class.getResourceAsStream("/pdfs/paid/paid_wheelchair_left.pdf")) {
                        PdfDocument srcPdfDocument = new PdfDocument(new PdfReader(pdfStreamIcon));
                        PdfFormXObject pageXObject = srcPdfDocument.getFirstPage().copyAsFormXObject(pdfDocument);
                        srcPdfDocument.close();

                        canvas8.addXObject(pageXObject, nextInfoX2, 0);
                    }
                } else if (element.equals("母婴室")) {
                    try (InputStream pdfStreamIcon = UnpaidArea.class.getResourceAsStream("/pdfs/paid/paid_nursing.pdf")) {
                        PdfDocument srcPdfDocument = new PdfDocument(new PdfReader(pdfStreamIcon));
                        PdfFormXObject pageXObject = srcPdfDocument.getFirstPage().copyAsFormXObject(pdfDocument);
                        srcPdfDocument.close();

                        canvas8.addXObject(pageXObject, nextInfoX2, 0);
                    }
                } else if (element.equals("垂梯")) {
                    try (InputStream pdfStreamIcon = UnpaidArea.class.getResourceAsStream("/pdfs/paid/paid_elevator.pdf")) {
                        PdfDocument srcPdfDocument = new PdfDocument(new PdfReader(pdfStreamIcon));
                        PdfFormXObject pageXObject = srcPdfDocument.getFirstPage().copyAsFormXObject(pdfDocument);
                        srcPdfDocument.close();

                        canvas8.addXObject(pageXObject, nextInfoX2, 0);
                    }
                    nextInfoX2 += iconWidth2 + iconInterval2;

                    try (InputStream pdfStreamIcon = UnpaidArea.class.getResourceAsStream("/pdfs/paid/paid_wheelchair_left.pdf")) {
                        PdfDocument srcPdfDocument = new PdfDocument(new PdfReader(pdfStreamIcon));
                        PdfFormXObject pageXObject = srcPdfDocument.getFirstPage().copyAsFormXObject(pdfDocument);
                        srcPdfDocument.close();

                        canvas8.addXObject(pageXObject, nextInfoX2, 0);
                    }
                } else if (element.equals("国铁")) {
                    PdfFormXObject svgObjectRailway;
                    try (InputStream svgStreamRailway = UnpaidArea.class.getResourceAsStream("/svgs/paid/railway_title.svg")) {
                        ISvgConverterProperties propertiesRailway = new SvgConverterProperties().setBaseUri(".");
                        svgObjectRailway = SvgConverter.convertToXObject(svgStreamRailway, pdfDocument, propertiesRailway);
                    }
                    canvas8.addXObject(svgObjectRailway, nextInfoX2, 0);
                } else if (element.equals("长途客运")) {
                    PdfFormXObject svgObjectCoach;
                    try (InputStream svgStreamCoach = UnpaidArea.class.getResourceAsStream("/svgs/paid/coach_title.svg")) {
                        ISvgConverterProperties propertiesCoach = new SvgConverterProperties().setBaseUri(".");
                        svgObjectCoach = SvgConverter.convertToXObject(svgStreamCoach, pdfDocument, propertiesCoach);
                    }
                    canvas8.addXObject(svgObjectCoach, nextInfoX2, 0);
                } else if (element.equals("机场巴士")) {
                    PdfFormXObject svgObjectAirportbus;
                    try (InputStream svgStreamAirportbus = UnpaidArea.class.getResourceAsStream("/svgs/paid/airportbus_title.svg")) {
                        ISvgConverterProperties propertiesAirportbus = new SvgConverterProperties().setBaseUri(".");
                        svgObjectAirportbus = SvgConverter.convertToXObject(svgStreamAirportbus, pdfDocument, propertiesAirportbus);
                    }
                    canvas8.addXObject(svgObjectAirportbus, nextInfoX2, 0);
                } else if (element.equals("公交")) {
                    PdfFormXObject svgObjectBus;
                    try (InputStream svgStreamBus = UnpaidArea.class.getResourceAsStream("/svgs/paid/bus_title.svg")) {
                        ISvgConverterProperties propertiesBus = new SvgConverterProperties().setBaseUri(".");
                        svgObjectBus = SvgConverter.convertToXObject(svgStreamBus, pdfDocument, propertiesBus);
                    }
                    canvas8.addXObject(svgObjectBus, nextInfoX2, 0);
                } else if (element.equals("停车场")) {
                    PdfFormXObject svgObjectParking;
                    try (InputStream svgStreamParking = UnpaidArea.class.getResourceAsStream("/svgs/paid/parking_title.svg")) {
                        ISvgConverterProperties propertiesParking = new SvgConverterProperties().setBaseUri(".");
                        svgObjectParking = SvgConverter.convertToXObject(svgStreamParking, pdfDocument, propertiesParking);
                    }
                    canvas8.addXObject(svgObjectParking, nextInfoX2, 0);
                } else if (element.equals("出租车")) {
                    PdfFormXObject svgObjectTaxi;
                    try (InputStream svgStreamTaxi = UnpaidArea.class.getResourceAsStream("/svgs/paid/taxi_title.svg")) {
                        ISvgConverterProperties propertiesTaxi = new SvgConverterProperties().setBaseUri(".");
                        svgObjectTaxi = SvgConverter.convertToXObject(svgStreamTaxi, pdfDocument, propertiesTaxi);
                    }
                    canvas8.addXObject(svgObjectTaxi, nextInfoX2, 0);
                } else if (element.equals("网约车")) {
                    PdfFormXObject svgObjectEhailing;
                    try (InputStream svgStreamEhailing = UnpaidArea.class.getResourceAsStream("/svgs/paid/ehailing_title.svg")) {
                        ISvgConverterProperties propertiesEhailing = new SvgConverterProperties().setBaseUri(".");
                        svgObjectEhailing = SvgConverter.convertToXObject(svgStreamEhailing, pdfDocument, propertiesEhailing);
                    }
                    canvas8.addXObject(svgObjectEhailing, nextInfoX2, 0);
                }

                if (index2 < facilityInfo1.size()) {
                    nextInfoX2 += iconWidth2 + iconInterval2 * 1.1;
                } else {
                    nextInfoX2 += iconWidth2;
                }

                if (index2 >= publicTransport && index2 < facilityInfo1.size()) {//只有最后一个公共交通设施加竖线
                    //竖线
                    canvas8.saveState()
                            .setFillColor(backgroundColor)
                            .rectangle(nextInfoX2, 0, 1.59 * 2.83464567f, iconWidth2)
                            .fill()
                            .restoreState();

                    nextInfoX2 += 1.59 * 2.83464567f + iconInterval2 * 1.1;
                }

                index2++;
            }

            float scale2 = maxInfoX2 / nextInfoX2;
            float correctX2 = (float) (28.641 + 73.16) * 2.83464567f + iconExitInterval;
            float correctY2;
            PdfCanvas finalCanvas2 = new PdfCanvas(page);
            if (scale2 < 1) {
                correctY2 = (float) (yOutline + (73.16 * 2.83464567f - iconWidth2 * scale2) / 2);
                finalCanvas2.addXObjectWithTransformationMatrix(template2, scale2, 0, 0, scale2, correctX2, correctY2);
            } else {
                correctY2 = (float) (yOutline + (73.16 * 2.83464567f - iconWidth2) / 2);
                finalCanvas2.addXObjectWithTransformationMatrix(template2, 1, 0, 0, 1, correctX2, correctY2);
            }

            //第二个出入口设施
            //创建一个临时的 PdfFormXObject
            if ((lineType == 2 || lineType == 3) && !facilityInfo2.isEmpty()) {
                PdfFormXObject template3 = new PdfFormXObject(tempPageSize);
                //在临时 canvas 上添加内容
                PdfCanvas canvas9 = new PdfCanvas(template3, pdfDocument);

                float nextInfoX3 = 0;
                int index3 = 1;
                int publicTransport2 = 0;
                for (String element : facilityInfo2) {//计算交通设施数量 只有最后一个加竖线
                    if (element.equals("卫生间") == false && element.equals("母婴室") == false && element.equals("垂梯") == false) {
                        publicTransport2++;
                    }
                }

                for (String element : facilityInfo2) {
                    if (element.equals("卫生间")) {
                        try (InputStream pdfStreamIcon = UnpaidArea.class.getResourceAsStream("/pdfs/paid/paid_toilet.pdf")) {
                            PdfDocument srcPdfDocument = new PdfDocument(new PdfReader(pdfStreamIcon));
                            PdfFormXObject pageXObject = srcPdfDocument.getFirstPage().copyAsFormXObject(pdfDocument);
                            srcPdfDocument.close();

                            canvas9.addXObject(pageXObject, nextInfoX3, 0);
                        }
                        nextInfoX3 += iconWidth2 + iconInterval2;

                        try (InputStream pdfStreamIcon = UnpaidArea.class.getResourceAsStream("/pdfs/paid/paid_wheelchair_left.pdf")) {
                            PdfDocument srcPdfDocument = new PdfDocument(new PdfReader(pdfStreamIcon));
                            PdfFormXObject pageXObject = srcPdfDocument.getFirstPage().copyAsFormXObject(pdfDocument);
                            srcPdfDocument.close();

                            canvas9.addXObject(pageXObject, nextInfoX3, 0);
                        }
                    } else if (element.equals("母婴室")) {
                        try (InputStream pdfStreamIcon = UnpaidArea.class.getResourceAsStream("/pdfs/paid/paid_nursing.pdf")) {
                            PdfDocument srcPdfDocument = new PdfDocument(new PdfReader(pdfStreamIcon));
                            PdfFormXObject pageXObject = srcPdfDocument.getFirstPage().copyAsFormXObject(pdfDocument);
                            srcPdfDocument.close();

                            canvas9.addXObject(pageXObject, nextInfoX3, 0);
                        }
                    } else if (element.equals("垂梯")) {
                        try (InputStream pdfStreamIcon = UnpaidArea.class.getResourceAsStream("/pdfs/paid/paid_elevator.pdf")) {
                            PdfDocument srcPdfDocument = new PdfDocument(new PdfReader(pdfStreamIcon));
                            PdfFormXObject pageXObject = srcPdfDocument.getFirstPage().copyAsFormXObject(pdfDocument);
                            srcPdfDocument.close();

                            canvas9.addXObject(pageXObject, nextInfoX3, 0);
                        }
                        nextInfoX3 += iconWidth2 + iconInterval2;

                        try (InputStream pdfStreamIcon = UnpaidArea.class.getResourceAsStream("/pdfs/paid/paid_wheelchair_left.pdf")) {
                            PdfDocument srcPdfDocument = new PdfDocument(new PdfReader(pdfStreamIcon));
                            PdfFormXObject pageXObject = srcPdfDocument.getFirstPage().copyAsFormXObject(pdfDocument);
                            srcPdfDocument.close();

                            canvas8.addXObject(pageXObject, nextInfoX, 0);
                        }
                    } else if (element.equals("国铁")) {
                        PdfFormXObject svgObjectRailway;
                        try (InputStream svgStreamRailway = UnpaidArea.class.getResourceAsStream("/svgs/paid/railway_title.svg")) {
                            ISvgConverterProperties propertiesRailway = new SvgConverterProperties().setBaseUri(".");
                            svgObjectRailway = SvgConverter.convertToXObject(svgStreamRailway, pdfDocument, propertiesRailway);
                        }
                        canvas9.addXObject(svgObjectRailway, nextInfoX3, 0);
                    } else if (element.equals("长途客运")) {
                        PdfFormXObject svgObjectCoach;
                        try (InputStream svgStreamCoach = UnpaidArea.class.getResourceAsStream("/svgs/paid/coach_title.svg")) {
                            ISvgConverterProperties propertiesCoach = new SvgConverterProperties().setBaseUri(".");
                            svgObjectCoach = SvgConverter.convertToXObject(svgStreamCoach, pdfDocument, propertiesCoach);
                        }
                        canvas9.addXObject(svgObjectCoach, nextInfoX3, 0);
                    } else if (element.equals("机场巴士")) {
                        PdfFormXObject svgObjectAirportbus;
                        try (InputStream svgStreamAirportbus = UnpaidArea.class.getResourceAsStream("/svgs/paid/airportbus_title.svg")) {
                            ISvgConverterProperties propertiesAirportbus = new SvgConverterProperties().setBaseUri(".");
                            svgObjectAirportbus = SvgConverter.convertToXObject(svgStreamAirportbus, pdfDocument, propertiesAirportbus);
                        }
                        canvas9.addXObject(svgObjectAirportbus, nextInfoX3, 0);
                    } else if (element.equals("公交")) {
                        PdfFormXObject svgObjectBus;
                        try (InputStream svgStreamBus = UnpaidArea.class.getResourceAsStream("/svgs/paid/bus_title.svg")) {
                            ISvgConverterProperties propertiesBus = new SvgConverterProperties().setBaseUri(".");
                            svgObjectBus = SvgConverter.convertToXObject(svgStreamBus, pdfDocument, propertiesBus);
                        }
                        canvas9.addXObject(svgObjectBus, nextInfoX3, 0);
                    } else if (element.equals("停车场")) {
                        PdfFormXObject svgObjectParking;
                        try (InputStream svgStreamParking = UnpaidArea.class.getResourceAsStream("/svgs/paid/parking_title.svg")) {
                            ISvgConverterProperties propertiesParking = new SvgConverterProperties().setBaseUri(".");
                            svgObjectParking = SvgConverter.convertToXObject(svgStreamParking, pdfDocument, propertiesParking);
                        }
                        canvas9.addXObject(svgObjectParking, nextInfoX3, 0);
                    } else if (element.equals("出租车")) {
                        PdfFormXObject svgObjectTaxi;
                        try (InputStream svgStreamTaxi = UnpaidArea.class.getResourceAsStream("/svgs/paid/taxi_title.svg")) {
                            ISvgConverterProperties propertiesTaxi = new SvgConverterProperties().setBaseUri(".");
                            svgObjectTaxi = SvgConverter.convertToXObject(svgStreamTaxi, pdfDocument, propertiesTaxi);
                        }
                        canvas9.addXObject(svgObjectTaxi, nextInfoX3, 0);
                    } else if (element.equals("网约车")) {
                        PdfFormXObject svgObjectEhailing;
                        try (InputStream svgStreamEhailing = UnpaidArea.class.getResourceAsStream("/svgs/paid/ehailing_title.svg")) {
                            ISvgConverterProperties propertiesEhailing = new SvgConverterProperties().setBaseUri(".");
                            svgObjectEhailing = SvgConverter.convertToXObject(svgStreamEhailing, pdfDocument, propertiesEhailing);
                        }
                        canvas9.addXObject(svgObjectEhailing, nextInfoX3, 0);
                    }

                    if (index3 < facilityInfo2.size()) {
                        nextInfoX3 += iconWidth2 + iconInterval2 * 1.1;
                    } else {
                        nextInfoX3 += iconWidth2;
                    }

                    if (index3 >= publicTransport2 && index3 < facilityInfo2.size()) {//只有最后一个公共交通设施加竖线
                        //竖线
                        canvas9.saveState()
                                .setFillColor(backgroundColor)
                                .rectangle(nextInfoX3, 0, 1.59 * 2.83464567f, iconWidth2)
                                .fill()
                                .restoreState();

                        nextInfoX3 += 1.59 * 2.83464567f + iconInterval2 * 1.1;
                    }

                    index3++;
                }

                float scale3 = maxInfoX2 / nextInfoX3;
                float correctX3 = (float) (28.641 + 73.16) * 2.83464567f + iconExitInterval;
                float correctY3;
                PdfCanvas finalCanvas3 = new PdfCanvas(page);
                if (scale3 < 1) {
                    correctY3 = (float) (yOutline2 + (73.16 * 2.83464567f - iconWidth2 * scale3) / 2);
                    finalCanvas3.addXObjectWithTransformationMatrix(template3, scale3, 0, 0, scale3, correctX3, correctY3);
                } else {
                    correctY3 = (float) (yOutline2 + (73.16 * 2.83464567f - iconWidth2) / 2);
                    finalCanvas3.addXObjectWithTransformationMatrix(template3, 1, 0, 0, 1, correctX3, correctY3);
                }
            }
        } else { //右对齐 设施图标
            if (arrowType == false) {
                swap(facilityInfo1);
                swap(facilityInfo2);
            }

            float nextInfoX2 = 0;
            int index2 = 1;
            int publicTransport = 0;
            for (String element : facilityInfo1) {//计算交通设施数量 只有最后一个加竖线
                if (element.equals("卫生间") == false && element.equals("母婴室") == false && element.equals("垂梯") == false) {
                    publicTransport++;
                }
            }

            for (int i = facilityInfo1.size() - 1; i >= 0; i--) {
                String element = facilityInfo1.get(i);
                if (element.equals("卫生间")) {
                    if (arrowType == false) {
                        PdfFormXObject svgObjectToilet;
                        try (InputStream pdfStreamIcon = UnpaidArea.class.getResourceAsStream("/pdfs/paid/paid_toilet.pdf")) {
                            PdfDocument srcPdfDocument = new PdfDocument(new PdfReader(pdfStreamIcon));
                            PdfFormXObject pageXObject = srcPdfDocument.getFirstPage().copyAsFormXObject(pdfDocument);
                            srcPdfDocument.close();

                            canvas8.addXObject(pageXObject, nextInfoX2, 0);
                        }
                        nextInfoX2 += iconWidth2 + iconInterval2;

                        try (InputStream pdfStreamIcon = UnpaidArea.class.getResourceAsStream("/pdfs/paid/paid_wheelchair_left.pdf")) {
                            PdfDocument srcPdfDocument = new PdfDocument(new PdfReader(pdfStreamIcon));
                            PdfFormXObject pageXObject = srcPdfDocument.getFirstPage().copyAsFormXObject(pdfDocument);
                            srcPdfDocument.close();

                            canvas8.addXObject(pageXObject, nextInfoX2, 0);
                        }
                    } else {
                        try (InputStream pdfStreamIcon = UnpaidArea.class.getResourceAsStream("/pdfs/paid/paid_wheelchair_right.pdf")) {
                            PdfDocument srcPdfDocument = new PdfDocument(new PdfReader(pdfStreamIcon));
                            PdfFormXObject pageXObject = srcPdfDocument.getFirstPage().copyAsFormXObject(pdfDocument);
                            srcPdfDocument.close();

                            canvas8.addXObject(pageXObject, nextInfoX2, 0);
                        }
                        nextInfoX2 += iconWidth2 + iconInterval2;

                        try (InputStream pdfStreamIcon = UnpaidArea.class.getResourceAsStream("/pdfs/paid/paid_toilet.pdf")) {
                            PdfDocument srcPdfDocument = new PdfDocument(new PdfReader(pdfStreamIcon));
                            PdfFormXObject pageXObject = srcPdfDocument.getFirstPage().copyAsFormXObject(pdfDocument);
                            srcPdfDocument.close();

                            canvas8.addXObject(pageXObject, nextInfoX2, 0);
                        }
                    }
                } else if (element.equals("母婴室")) {
                    try (InputStream pdfStreamIcon = UnpaidArea.class.getResourceAsStream("/pdfs/paid/paid_nursing.pdf")) {
                        PdfDocument srcPdfDocument = new PdfDocument(new PdfReader(pdfStreamIcon));
                        PdfFormXObject pageXObject = srcPdfDocument.getFirstPage().copyAsFormXObject(pdfDocument);
                        srcPdfDocument.close();

                        canvas8.addXObject(pageXObject, nextInfoX2, 0);
                    }
                } else if (element.equals("垂梯")) {
                    if (arrowType == false) {
                        try (InputStream pdfStreamIcon = UnpaidArea.class.getResourceAsStream("/pdfs/paid/paid_elevator.pdf")) {
                            PdfDocument srcPdfDocument = new PdfDocument(new PdfReader(pdfStreamIcon));
                            PdfFormXObject pageXObject = srcPdfDocument.getFirstPage().copyAsFormXObject(pdfDocument);
                            srcPdfDocument.close();

                            canvas8.addXObject(pageXObject, nextInfoX2, 0);
                        }
                        nextInfoX2 += iconWidth2 + iconInterval2;

                        try (InputStream pdfStreamIcon = UnpaidArea.class.getResourceAsStream("/pdfs/paid/paid_wheelchair_left.pdf")) {
                            PdfDocument srcPdfDocument = new PdfDocument(new PdfReader(pdfStreamIcon));
                            PdfFormXObject pageXObject = srcPdfDocument.getFirstPage().copyAsFormXObject(pdfDocument);
                            srcPdfDocument.close();

                            canvas8.addXObject(pageXObject, nextInfoX2, 0);
                        }
                    } else {
                        try (InputStream pdfStreamIcon = UnpaidArea.class.getResourceAsStream("/pdfs/paid/paid_wheelchair_right.pdf")) {
                            PdfDocument srcPdfDocument = new PdfDocument(new PdfReader(pdfStreamIcon));
                            PdfFormXObject pageXObject = srcPdfDocument.getFirstPage().copyAsFormXObject(pdfDocument);
                            srcPdfDocument.close();

                            canvas8.addXObject(pageXObject, nextInfoX2, 0);
                        }
                        nextInfoX2 += iconWidth2 + iconInterval2;

                        try (InputStream pdfStreamIcon = UnpaidArea.class.getResourceAsStream("/pdfs/paid/paid_elevator.pdf")) {
                            PdfDocument srcPdfDocument = new PdfDocument(new PdfReader(pdfStreamIcon));
                            PdfFormXObject pageXObject = srcPdfDocument.getFirstPage().copyAsFormXObject(pdfDocument);
                            srcPdfDocument.close();

                            canvas8.addXObject(pageXObject, nextInfoX2, 0);
                        }
                    }
                } else if (element.equals("国铁")) {
                    PdfFormXObject svgObjectRailway;
                    try (InputStream svgStreamRailway = UnpaidArea.class.getResourceAsStream("/svgs/paid/railway_title.svg")) {
                        ISvgConverterProperties propertiesRailway = new SvgConverterProperties().setBaseUri(".");
                        svgObjectRailway = SvgConverter.convertToXObject(svgStreamRailway, pdfDocument, propertiesRailway);
                    }
                    canvas8.addXObject(svgObjectRailway, nextInfoX2, 0);
                } else if (element.equals("长途客运")) {
                    PdfFormXObject svgObjectCoach;
                    try (InputStream svgStreamCoach = UnpaidArea.class.getResourceAsStream("/svgs/paid/coach_title.svg")) {
                        ISvgConverterProperties propertiesCoach = new SvgConverterProperties().setBaseUri(".");
                        svgObjectCoach = SvgConverter.convertToXObject(svgStreamCoach, pdfDocument, propertiesCoach);
                    }
                    canvas8.addXObject(svgObjectCoach, nextInfoX2, 0);
                } else if (element.equals("机场巴士")) {
                    PdfFormXObject svgObjectAirportbus;
                    try (InputStream svgStreamAirportbus = UnpaidArea.class.getResourceAsStream("/svgs/paid/airportbus_title.svg")) {
                        ISvgConverterProperties propertiesAirportbus = new SvgConverterProperties().setBaseUri(".");
                        svgObjectAirportbus = SvgConverter.convertToXObject(svgStreamAirportbus, pdfDocument, propertiesAirportbus);
                    }
                    canvas8.addXObject(svgObjectAirportbus, nextInfoX2, 0);
                } else if (element.equals("公交")) {
                    PdfFormXObject svgObjectBus;
                    try (InputStream svgStreamBus = UnpaidArea.class.getResourceAsStream("/svgs/paid/bus_title.svg")) {
                        ISvgConverterProperties propertiesBus = new SvgConverterProperties().setBaseUri(".");
                        svgObjectBus = SvgConverter.convertToXObject(svgStreamBus, pdfDocument, propertiesBus);
                    }
                    canvas8.addXObject(svgObjectBus, nextInfoX2, 0);
                } else if (element.equals("停车场")) {
                    PdfFormXObject svgObjectParking;
                    try (InputStream svgStreamParking = UnpaidArea.class.getResourceAsStream("/svgs/paid/parking_title.svg")) {
                        ISvgConverterProperties propertiesParking = new SvgConverterProperties().setBaseUri(".");
                        svgObjectParking = SvgConverter.convertToXObject(svgStreamParking, pdfDocument, propertiesParking);
                    }
                    canvas8.addXObject(svgObjectParking, nextInfoX2, 0);
                } else if (element.equals("出租车")) {
                    PdfFormXObject svgObjectTaxi;
                    try (InputStream svgStreamTaxi = UnpaidArea.class.getResourceAsStream("/svgs/paid/taxi_title.svg")) {
                        ISvgConverterProperties propertiesTaxi = new SvgConverterProperties().setBaseUri(".");
                        svgObjectTaxi = SvgConverter.convertToXObject(svgStreamTaxi, pdfDocument, propertiesTaxi);
                    }
                    canvas8.addXObject(svgObjectTaxi, nextInfoX2, 0);
                } else if (element.equals("网约车")) {
                    PdfFormXObject svgObjectEhailing;
                    try (InputStream svgStreamEhailing = UnpaidArea.class.getResourceAsStream("/svgs/paid/ehailing_title.svg")) {
                        ISvgConverterProperties propertiesEhailing = new SvgConverterProperties().setBaseUri(".");
                        svgObjectEhailing = SvgConverter.convertToXObject(svgStreamEhailing, pdfDocument, propertiesEhailing);
                    }
                    canvas8.addXObject(svgObjectEhailing, nextInfoX2, 0);
                }

                if (index2 < facilityInfo1.size()) {
                    nextInfoX2 += iconWidth2 + iconInterval2 * 1.1;
                } else {
                    nextInfoX2 += iconWidth2;
                }

                if (index2 <= (facilityInfo1.size() - publicTransport) && index2 < facilityInfo1.size()) {//只有最后一个公共交通设施加竖线
                    //竖线
                    canvas8.saveState()
                            .setFillColor(backgroundColor)
                            .rectangle(nextInfoX2, 0, 1.59 * 2.83464567f, iconWidth2)
                            .fill()
                            .restoreState();

                    nextInfoX2 += 1.59 * 2.83464567f + iconInterval2 * 1.1;
                }

                index2++;
            }

            float scale2 = maxInfoX2 / nextInfoX2;
            float correctX2;
            float correctY2;
            PdfCanvas finalCanvas2 = new PdfCanvas(page);
            if (scale2 < 1) {
                correctX2 = (float) (pageSizeX - (28.641 + 73.16) * 2.83464567f - iconExitInterval - nextInfoX2 * scale2);
            } else {
                correctX2 = (float) (pageSizeX - (28.641 + 73.16) * 2.83464567f - iconExitInterval - nextInfoX2);
            }
            if (scale2 < 1) {
                correctY2 = (float) (yOutline + (73.16 * 2.83464567f - iconWidth2 * scale2) / 2);
                finalCanvas2.addXObjectWithTransformationMatrix(template2, scale2, 0, 0, scale2, correctX2, correctY2);
            } else {
                correctY2 = (float) (yOutline + (73.16 * 2.83464567f - iconWidth2) / 2);
                finalCanvas2.addXObjectWithTransformationMatrix(template2, 1, 0, 0, 1, correctX2, correctY2);
            }

            //第二个出入口设施
            //创建一个临时的 PdfFormXObject
            if ((lineType == 2 || lineType == 3) && !facilityInfo2.isEmpty()) {
                PdfFormXObject template3 = new PdfFormXObject(tempPageSize);
                //在临时 canvas 上添加内容
                PdfCanvas canvas9 = new PdfCanvas(template3, pdfDocument);

                float nextInfoX3 = 0;
                int index3 = 1;
                int publicTransport2 = 0;
                for (String element : facilityInfo2) {//计算交通设施数量 只有最后一个加竖线
                    if (element.equals("卫生间") == false && element.equals("母婴室") == false && element.equals("垂梯") == false) {
                        publicTransport2++;
                    }
                }

                for (int i = facilityInfo2.size() - 1; i >= 0; i--) {
                    String element = facilityInfo2.get(i);
                    if (element.equals("卫生间")) {
                        if (arrowType == false) {
                            try (InputStream pdfStreamIcon = UnpaidArea.class.getResourceAsStream("/pdfs/paid/paid_toilet.pdf")) {
                                PdfDocument srcPdfDocument = new PdfDocument(new PdfReader(pdfStreamIcon));
                                PdfFormXObject pageXObject = srcPdfDocument.getFirstPage().copyAsFormXObject(pdfDocument);
                                srcPdfDocument.close();

                                canvas9.addXObject(pageXObject, nextInfoX3, 0);
                            }
                            nextInfoX3 += iconWidth2 + iconInterval2;

                            try (InputStream pdfStreamIcon = UnpaidArea.class.getResourceAsStream("/pdfs/paid/paid_wheelchair_left.pdf")) {
                                PdfDocument srcPdfDocument = new PdfDocument(new PdfReader(pdfStreamIcon));
                                PdfFormXObject pageXObject = srcPdfDocument.getFirstPage().copyAsFormXObject(pdfDocument);
                                srcPdfDocument.close();

                                canvas9.addXObject(pageXObject, nextInfoX3, 0);
                            }
                        } else {
                            try (InputStream pdfStreamIcon = UnpaidArea.class.getResourceAsStream("/pdfs/paid/paid_wheelchair_right.pdf")) {
                                PdfDocument srcPdfDocument = new PdfDocument(new PdfReader(pdfStreamIcon));
                                PdfFormXObject pageXObject = srcPdfDocument.getFirstPage().copyAsFormXObject(pdfDocument);
                                srcPdfDocument.close();

                                canvas9.addXObject(pageXObject, nextInfoX3, 0);
                            }
                            nextInfoX3 += iconWidth2 + iconInterval2;

                            try (InputStream pdfStreamIcon = UnpaidArea.class.getResourceAsStream("/pdfs/paid/paid_toilet.pdf")) {
                                PdfDocument srcPdfDocument = new PdfDocument(new PdfReader(pdfStreamIcon));
                                PdfFormXObject pageXObject = srcPdfDocument.getFirstPage().copyAsFormXObject(pdfDocument);
                                srcPdfDocument.close();

                                canvas9.addXObject(pageXObject, nextInfoX3, 0);
                            }
                        }
                    } else if (element.equals("母婴室")) {
                        try (InputStream pdfStreamIcon = UnpaidArea.class.getResourceAsStream("/pdfs/paid/paid_nursing.pdf")) {
                            PdfDocument srcPdfDocument = new PdfDocument(new PdfReader(pdfStreamIcon));
                            PdfFormXObject pageXObject = srcPdfDocument.getFirstPage().copyAsFormXObject(pdfDocument);
                            srcPdfDocument.close();

                            canvas9.addXObject(pageXObject, nextInfoX3, 0);
                        }
                    } else if (element.equals("垂梯")) {
                        if (arrowType == false) {
                            try (InputStream pdfStreamIcon = UnpaidArea.class.getResourceAsStream("/pdfs/paid/paid_elevator.pdf")) {
                                PdfDocument srcPdfDocument = new PdfDocument(new PdfReader(pdfStreamIcon));
                                PdfFormXObject pageXObject = srcPdfDocument.getFirstPage().copyAsFormXObject(pdfDocument);
                                srcPdfDocument.close();

                                canvas9.addXObject(pageXObject, nextInfoX3, 0);
                            }
                            nextInfoX3 += iconWidth2 + iconInterval2;

                            try (InputStream pdfStreamIcon = UnpaidArea.class.getResourceAsStream("/pdfs/paid/paid_wheelchair_left.pdf")) {
                                PdfDocument srcPdfDocument = new PdfDocument(new PdfReader(pdfStreamIcon));
                                PdfFormXObject pageXObject = srcPdfDocument.getFirstPage().copyAsFormXObject(pdfDocument);
                                srcPdfDocument.close();

                                canvas9.addXObject(pageXObject, nextInfoX3, 0);
                            }
                        } else {
                            try (InputStream pdfStreamIcon = UnpaidArea.class.getResourceAsStream("/pdfs/paid/paid_wheelchair_right.pdf")) {
                                PdfDocument srcPdfDocument = new PdfDocument(new PdfReader(pdfStreamIcon));
                                PdfFormXObject pageXObject = srcPdfDocument.getFirstPage().copyAsFormXObject(pdfDocument);
                                srcPdfDocument.close();

                                canvas9.addXObject(pageXObject, nextInfoX3, 0);
                            }
                            nextInfoX3 += iconWidth2 + iconInterval2;

                            try (InputStream pdfStreamIcon = UnpaidArea.class.getResourceAsStream("/pdfs/paid/paid_elevator.pdf")) {
                                PdfDocument srcPdfDocument = new PdfDocument(new PdfReader(pdfStreamIcon));
                                PdfFormXObject pageXObject = srcPdfDocument.getFirstPage().copyAsFormXObject(pdfDocument);
                                srcPdfDocument.close();

                                canvas9.addXObject(pageXObject, nextInfoX3, 0);
                            }
                        }
                    } else if (element.equals("国铁")) {
                        PdfFormXObject svgObjectRailway;
                        try (InputStream svgStreamRailway = UnpaidArea.class.getResourceAsStream("/svgs/paid/railway_title.svg")) {
                            ISvgConverterProperties propertiesRailway = new SvgConverterProperties().setBaseUri(".");
                            svgObjectRailway = SvgConverter.convertToXObject(svgStreamRailway, pdfDocument, propertiesRailway);
                        }
                        canvas9.addXObject(svgObjectRailway, nextInfoX3, 0);
                    } else if (element.equals("长途客运")) {
                        PdfFormXObject svgObjectCoach;
                        try (InputStream svgStreamCoach = UnpaidArea.class.getResourceAsStream("/svgs/paid/coach_title.svg")) {
                            ISvgConverterProperties propertiesCoach = new SvgConverterProperties().setBaseUri(".");
                            svgObjectCoach = SvgConverter.convertToXObject(svgStreamCoach, pdfDocument, propertiesCoach);
                        }
                        canvas9.addXObject(svgObjectCoach, nextInfoX3, 0);
                    } else if (element.equals("机场巴士")) {
                        PdfFormXObject svgObjectAirportbus;
                        try (InputStream svgStreamAirportbus = UnpaidArea.class.getResourceAsStream("/svgs/paid/airportbus_title.svg")) {
                            ISvgConverterProperties propertiesAirportbus = new SvgConverterProperties().setBaseUri(".");
                            svgObjectAirportbus = SvgConverter.convertToXObject(svgStreamAirportbus, pdfDocument, propertiesAirportbus);
                        }
                        canvas9.addXObject(svgObjectAirportbus, nextInfoX3, 0);
                    } else if (element.equals("公交")) {
                        PdfFormXObject svgObjectBus;
                        try (InputStream svgStreamBus = UnpaidArea.class.getResourceAsStream("/svgs/paid/bus_title.svg")) {
                            ISvgConverterProperties propertiesBus = new SvgConverterProperties().setBaseUri(".");
                            svgObjectBus = SvgConverter.convertToXObject(svgStreamBus, pdfDocument, propertiesBus);
                        }
                        canvas9.addXObject(svgObjectBus, nextInfoX3, 0);
                    } else if (element.equals("停车场")) {
                        PdfFormXObject svgObjectParking;
                        try (InputStream svgStreamParking = UnpaidArea.class.getResourceAsStream("/svgs/paid/parking_title.svg")) {
                            ISvgConverterProperties propertiesParking = new SvgConverterProperties().setBaseUri(".");
                            svgObjectParking = SvgConverter.convertToXObject(svgStreamParking, pdfDocument, propertiesParking);
                        }
                        canvas9.addXObject(svgObjectParking, nextInfoX3, 0);
                    } else if (element.equals("出租车")) {
                        PdfFormXObject svgObjectTaxi;
                        try (InputStream svgStreamTaxi = UnpaidArea.class.getResourceAsStream("/svgs/paid/taxi_title.svg")) {
                            ISvgConverterProperties propertiesTaxi = new SvgConverterProperties().setBaseUri(".");
                            svgObjectTaxi = SvgConverter.convertToXObject(svgStreamTaxi, pdfDocument, propertiesTaxi);
                        }
                        canvas9.addXObject(svgObjectTaxi, nextInfoX3, 0);
                    } else if (element.equals("网约车")) {
                        PdfFormXObject svgObjectEhailing;
                        try (InputStream svgStreamEhailing = UnpaidArea.class.getResourceAsStream("/svgs/paid/ehailing_title.svg")) {
                            ISvgConverterProperties propertiesEhailing = new SvgConverterProperties().setBaseUri(".");
                            svgObjectEhailing = SvgConverter.convertToXObject(svgStreamEhailing, pdfDocument, propertiesEhailing);
                        }
                        canvas9.addXObject(svgObjectEhailing, nextInfoX3, 0);
                    }

                    if (index3 < facilityInfo2.size()) {
                        nextInfoX3 += iconWidth2 + iconInterval2 * 1.1;
                    } else {
                        nextInfoX3 += iconWidth2;
                    }

                    if (index3 <= (facilityInfo2.size() - publicTransport) && index3 < facilityInfo2.size()) {//只有最后一个公共交通设施加竖线
                        //竖线
                        canvas9.saveState()
                                .setFillColor(backgroundColor)
                                .rectangle(nextInfoX3, 0, 1.59 * 2.83464567f, iconWidth2)
                                .fill()
                                .restoreState();

                        nextInfoX3 += 1.59 * 2.83464567f + iconInterval2 * 1.1;
                    }

                    index3++;
                }

                float scale3 = maxInfoX2 / nextInfoX3;
                float correctX3;
                float correctY3;
                PdfCanvas finalCanvas3 = new PdfCanvas(page);
                if (scale3 < 1) {
                    correctX3 = (float) (pageSizeX - (28.641 + 73.16) * 2.83464567f - iconExitInterval - nextInfoX3 * scale3);
                } else {
                    correctX3 = (float) (pageSizeX - (28.641 + 73.16) * 2.83464567f - iconExitInterval - nextInfoX3);
                }
                if (scale3 < 1) {
                    correctY3 = (float) (yOutline2 + (73.16 * 2.83464567f - iconWidth2 * scale3) / 2);
                    finalCanvas3.addXObjectWithTransformationMatrix(template3, scale3, 0, 0, scale3, correctX3, correctY3);
                } else {
                    correctY3 = (float) (yOutline2 + (73.16 * 2.83464567f - iconWidth2) / 2);
                    finalCanvas3.addXObjectWithTransformationMatrix(template3, 1, 0, 0, 1, correctX3, correctY3);
                }
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

    public static String replaceChars(String input) {
        // 将字符串中的 "（" 替换为 "("
        String replacedInput = input.replace("（", "(");
        // 将字符串中的 "）" 替换为 ")"
        replacedInput = replacedInput.replace("）", ")");
        // 将字符串中的 "’" 替换为 "'"
        replacedInput = replacedInput.replace("’", "'");
        return replacedInput;
    }

    public static boolean getWhetherSingle(String input) {
        // 判断输入字符串是否包含"单"
        if (input.contains("单") || input.contains("双")) {
            return input.contains("单");
        } else {
            System.out.println("温馨提示：尚未指定双或单出入口，为保护程序，已自动选择单出入口。");
            return true;
        }
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

    public static boolean checkArrow(String input) {
        return input.contains("有箭头");
    }

    public static int getEntranceNumber(String input) {
        //查找字符串中第一个出现的“口”字的位置
        int indexOfKou = input.indexOf("口");

        //如果找到了“口”字
        if (indexOfKou != -1 && indexOfKou > 0) {
            //使用charAt方法获取“口”字前面的字符，然后将其转换为整数后返回
            return Character.getNumericValue(input.charAt(indexOfKou - 1));
        }

        //如果没有找到“口”字，返回1
        return 1;
    }
}
