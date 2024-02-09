/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.mavenproject1;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;

import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFCell;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
import com.itextpdf.kernel.pdf.canvas.PdfCanvas;
import com.itextpdf.kernel.pdf.canvas.PdfCanvasConstants;
import com.itextpdf.kernel.pdf.xobject.PdfFormXObject;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.element.Image;

/**
 *
 * @author Z.D.
 */
public class PaidArea {

    public static void main(ArrayList<Object> dataList) throws FileNotFoundException, IOException {
        final float starterX = (float) dataList.get(0) * 2.83464567f;
        final float starterY = (float) dataList.get(1) * 2.83464567f;
        final float pageSizeX = (float) dataList.get(2) * 2.83464567f;
        final float pageSizeY = (float) dataList.get(3) * 2.83464567f;

        final int startRow = 2;
        final int col = (int) dataList.get(4);
        String stationName = (String) dataList.get(5);
        String stationNumber = getStationNumber(stationName)[1];

        PdfDocument pdfDocument = (PdfDocument) dataList.get(6);
        PdfPage page = (PdfPage) dataList.get(7);
        Document document = (Document) dataList.get(9);
        File f = (File) dataList.get(8);

        final float differenceX = pageSizeX - 685 * 2.83464567f;

        final double lineWidth = pageSizeX - (28.641 * 2) * 2.83464567f;
        final double interval = 93.741 * 2.83464567f;

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
        Color black = new DeviceCmyk(0, 0, 0, 100);

        final float bottomTop = (float) 49.423 * 2.83464567f;
        PageSize tempPageSize = new PageSize(pageSizeX * 2, 72 * 2.83464567f);
        String direction = ""; // 初始化对齐方向
        boolean arrowType = true; // 初始化是否带箭头
        int colNeeded = 0;

        ArrayList<String> entrance = new ArrayList<>();
        ArrayList<ArrayList<String[]>> exitInfo = new ArrayList<>();
        ArrayList<ArrayList<String>> facilityInfo = new ArrayList<>();
        ArrayList<String[]> facilityInfoBottom = new ArrayList<>();
        ArrayList<Integer> numLines = new ArrayList<>();

        try {
            BufferedInputStream bis = new BufferedInputStream(new FileInputStream(f));
            XSSFWorkbook workbook = new XSSFWorkbook(bis);
            XSSFSheet sheet = workbook.getSheetAt(0);

            int currentCol = col;
            // 向右遍历直到空列 添加每个出入口的信息
            while (sheet.getRow(startRow + 3).getCell(currentCol) != null && !"".equals(sheet.getRow(startRow + 3).getCell(currentCol).getStringCellValue())) {
                // 获取出入口编号并加入数组
                entrance.add(findEnglishLetters(sheet.getRow(startRow + 3).getCell(currentCol).getStringCellValue()));

                // 获取当前出入口设施 (卫生间/母婴室/垂梯)
                facilityInfo.add(findFacilityInfo(sheet.getRow(startRow + 3).getCell(currentCol).getStringCellValue()));

                ArrayList<String[]> exitInfoBranch = new ArrayList<>();
                // 获取每个出入口的中英文信息
                for (int row = startRow + 4; row <= startRow + 25;) {
                    XSSFCell chineseCell = sheet.getRow(row).getCell(currentCol);
                    XSSFCell englishCell = sheet.getRow(row + 1).getCell(currentCol);
                    if (chineseCell != null && englishCell != null) {
                        String chinese = replaceChars(chineseCell.getStringCellValue());
                        String english = replaceChars(englishCell.getStringCellValue());
                        if (chinese.equals("") == false && english.equals("") == false) {
                            exitInfoBranch.add(new String[]{chinese, english});
                        }
                    }
                    row += 2;
                }
                exitInfo.add(exitInfoBranch);
                currentCol += 1;
            }
            final int entranceNum = entrance.size(); // 出入口数量

            // 如果出入口数量>1，获取行数分配
            if (entranceNum > 1) {
                XSSFCell numLinesCell = sheet.getRow(startRow + 2).getCell(col + 1);
                numLines = parseNumLines(numLinesCell.getStringCellValue());
            } else {
                numLines = new ArrayList<>();
                numLines.add(14);
            }

            // 对齐方向及是否带箭头
            XSSFCell arrowCell = sheet.getRow(startRow + 2).getCell(col);
            direction = detectArrowDirection(arrowCell.getStringCellValue());
            arrowType = checkArrow(arrowCell.getStringCellValue());

            // 其他出口设施
            XSSFCell bottomFacilityCell = sheet.getRow(startRow + 26).getCell(col);
            facilityInfoBottom = parseFacilityInfo(bottomFacilityCell.getStringCellValue());

            // 稿件排版列数
            for (int i = 0; i < exitInfo.size(); i++) {
                int tempColNeeded = (int) Math.ceil((double) exitInfo.get(i).size() / numLines.get(i)); // 向上取整
                if (tempColNeeded > colNeeded) {
                    colNeeded = tempColNeeded;
                }
            }
        } catch (FileNotFoundException e) {
            System.err.println(e.getMessage());
        } catch (IOException e) {
            System.err.println(e.getMessage());
        }

        PdfCanvas canvas = new PdfCanvas(page);
        // 白色背景
        Color backgroundColor = new DeviceCmyk(0, 0, 0, 0);
        canvas.saveState()
                .setFillColor(backgroundColor)
                .rectangle(starterX, starterY, pageSizeX, pageSizeY)
                .fill()
                .restoreState();

        double topsBottom = starterY + pageSizeY - (128.5 * 2.83464567f); // 顶部灰带的底部坐标
        // 灰色块
        backgroundColor = new DeviceCmyk(0, 0, 0, 10);
        canvas.saveState()
                .setFillColor(backgroundColor)
                .rectangle(starterX, topsBottom, pageSizeX, 128.5 * 2.83464567f) // 顶部灰色快
                .rectangle(starterX, starterY, pageSizeX, 49.423 * 2.83464567f) // 底部灰色块
                .fill()
                .restoreState();

        float xOutline;
        float yOutline;
        if (direction.equals("right")) {
            xOutline = starterX + (float) (pageSizeX - 101.805 * 2.83464567f);
        } else {
            xOutline = starterX + (float) 28.83 * 2.83464567f;
        }
        yOutline = (float) (topsBottom - 112.643 * 2.83464567f);
        float lastLineY = 0;

        for (int i = 0; i < entrance.size(); i++) {
            // 字母外框
            PdfCanvas canvas3 = new PdfCanvas(pdfDocument.getFirstPage());
            try (InputStream pdfStreamIcon = UnpaidArea.class.getResourceAsStream("/pdfs/paid/outline.pdf"); PdfDocument srcPdfDocument = new PdfDocument(new PdfReader(pdfStreamIcon))) {
                PdfFormXObject pageXObject = srcPdfDocument.getFirstPage().copyAsFormXObject(pdfDocument);
                canvas3.addXObject(pageXObject, xOutline, yOutline);
            }

            // 出入口字母
            canvas3.beginText();
            canvas3.setFillColor(black);
            String currentEntrance = entrance.get(i);
            // 带角标口
            if (currentEntrance.length() != 1) {
                float textHeight = arialBold.getAscent(currentEntrance.substring(0, 1), 183) - arialBold.getDescent(currentEntrance.substring(0, 1), 183);
                float textWidth = arialBold.getWidth(currentEntrance.substring(0, 1), 183);
                float textWidthBranch = arialBold.getWidth(currentEntrance.substring(1, currentEntrance.length()), 110);
                float totalWidth = textWidth + textWidthBranch;

                canvas3.setFontAndSize(arialBold, 183);
                double tempX = xOutline + (((73.16 - totalWidth / 2.83464567f) / 2) - 0.3) * 2.83464567f;
                if (currentEntrance.contains("A")) {
                    tempX += 0.9 * 2.83464567f;
                }
                if (currentEntrance.substring(1, 2).equals("1")) {
                    tempX += 1.4 * 2.83464567f;
                }
                canvas3.moveText(tempX, yOutline + ((73.16 - textHeight / 2.83464567f) / 2) * 2.83464567f); // 设置文本的起始位置
                canvas3.showText(currentEntrance.substring(0, 1));

                canvas3.setFontAndSize(arialBold, 110);
                canvas3.moveText(textWidth, 0); // 设置文本的起始位置
                canvas3.showText(currentEntrance.substring(1, currentEntrance.length()));
            } else { // 不带角标口
                canvas3.setFontAndSize(arialBold, 200);
                if (currentEntrance.equals("B") || currentEntrance.equals("D") || currentEntrance.equals("G")) {
                    float textHeight = arialBold.getAscent(currentEntrance, 200) - arialBold.getDescent(currentEntrance, 200);
                    float textWidth = arialBold.getWidth(currentEntrance, 200);
                    canvas3.moveText(xOutline + (((73.16 - textWidth / 2.83464567f) / 2) + 0.8) * 2.83464567f, yOutline + ((73.16 - textHeight / 2.83464567f) / 2) * 2.83464567f); // 设置文本的起始位置
                    canvas3.showText(currentEntrance);
                } else {
                    float textHeight = arialBold.getAscent(currentEntrance, 200) - arialBold.getDescent(currentEntrance, 200);
                    float textWidth = arialBold.getWidth(currentEntrance, 200);
                    canvas3.moveText(xOutline + ((73.16 - textWidth / 2.83464567f) / 2) * 2.83464567f, yOutline + ((73.16 - textHeight / 2.83464567f) / 2) * 2.83464567f); // 设置文本的起始位置
                    canvas3.showText(currentEntrance);
                }
            }
            canvas3.endText();

            // 横线
            float tempLineY = (float) (yOutline - 92.692 * 2.83464567f);
            for (int j = 1; j <= numLines.get(i); j++) {
                canvas.saveState()
                        .setFillColor(black)
                        .rectangle(starterX + 28.641 * 2.83464567f, tempLineY, lineWidth, 1.578 * 2.83464567f)
                        .fill()
                        .restoreState();
                tempLineY -= interval;
            }

            int currentRow = 1;
            double yChinese = yOutline - 54.27 * 2.83464567f;
            double yEnglish = yOutline - 80.22 * 2.83464567f;
            double xChinese;
            double xEnglish;
            int branchNumLines = numLines.get(i);
            int chineseSpace = 7;
            double intervalPercent;
            double expandPercent = 1;
            intervalPercent = switch (colNeeded) {
                case 2 ->
                    0.1;
                case 3 ->
                    0.06;
                default ->
                    0.05;
            };
            if (colNeeded == 2) {
                expandPercent = 1.195;
            }
            // n表示colNeeded, y表示每个空隙占比(intervalPercent), x表示每一列宽度占比
            // 1 - (n - 1) * y = (n - 1) * x + 1.195 * x
            float intervalTotalPercent = (float) ((colNeeded - 1) * intervalPercent); // (列数-1)*每个空隙占比=空隙总占比
            float maxWidthPercent = (float) ((1 - intervalTotalPercent) / ((colNeeded - 1) + expandPercent));

            for (String[] element : exitInfo.get(i)) {
                // 中文
                // 需要检查有没有改括号
                PdfCanvas canvas4 = new PdfCanvas(page);

                boolean ad = false;
                if (element[0].contains("**")) {
                    element[0] = element[0].substring(2);
                    ad = true;
                } else if (element[0].contains("*")) {
                    element[0] = element[0].substring(1);
                }
                float textWidth = heiTi.getWidth(element[0], 100) + chineseSpace * (element[0].length() - 1);

                double scaleChinese = 1;
                if (currentRow + branchNumLines <= exitInfo.get(i).size()) { // 右侧有文字
                    if (currentRow <= branchNumLines) { // 第1列
                        if (textWidth >= (lineWidth - 10) * maxWidthPercent * expandPercent) { // 如果超长
                            scaleChinese = (lineWidth - 10) * maxWidthPercent * expandPercent / textWidth;
                        }
                    } else {  // 后续列
                        if (textWidth >= (lineWidth - 10) * maxWidthPercent) {  // 如果超长
                            scaleChinese = (lineWidth - 10) * maxWidthPercent / textWidth;
                        }
                    }
                } else { // 右侧无文字
                    if (currentRow <= branchNumLines) { // 第1列
                        if (textWidth >= (lineWidth - 10) - (lineWidth - 10) * 0.005) {
                            scaleChinese = ((lineWidth - 10) - (lineWidth - 10) * 0.005) / textWidth;
                        }
                    } else if (currentRow <= branchNumLines * (colNeeded - 1)) { // 第2列-最后一列
                        int currentRowReal2 = (int) Math.ceil((double) currentRow / branchNumLines); // 向上取整
                        int numInterval = currentRowReal2 - 1;
                        int numMaxWidth = currentRowReal2 - 2;
                        if (numMaxWidth < 0) { // 专门解决第2列时可能会为负的问题
                            numMaxWidth = 0;
                        }
                        if (textWidth >= (lineWidth - 10) - (lineWidth - 10) * maxWidthPercent * expandPercent - (lineWidth - 10) * numInterval * intervalPercent - (lineWidth - 10) * numMaxWidth * maxWidthPercent - (lineWidth - 10) * 0.005) {
                            scaleChinese = ((lineWidth - 10) - (lineWidth - 10) * maxWidthPercent * expandPercent - (lineWidth - 10) * numInterval * intervalPercent - (lineWidth - 10) * numMaxWidth * maxWidthPercent - (lineWidth - 10) * 0.005) / textWidth;
                        }
                    } else { // 最后一列(实际位置最后一列)
                        if (textWidth >= (lineWidth - 10) * (maxWidthPercent - 0.005)) { // 如果超长
                            scaleChinese = (lineWidth - 10) * (maxWidthPercent - 0.005) / textWidth;
                        }
                    }
                }
                textWidth = textWidth * (float) scaleChinese;

                if (currentRow <= branchNumLines) {
                    if (direction.equals("right")) {
                        xChinese = starterX + pageSizeX - 32.93 * 2.83464567f - textWidth;
                    } else {
                        xChinese = starterX + 32.93 * 2.83464567f;
                    }
                } else {
                    double firstColWidth = 33.93 * 2.83464567f + (lineWidth - 10) * maxWidthPercent * expandPercent;
                    // 每1列最后一项因为可以整除 会误判 因此加入判断如果currentRow算出的实际列数为整数 就-1
                    double currentRowReal = (double) currentRow / branchNumLines;
                    int intCurrentRowReal = (int) currentRowReal;
                    if (intCurrentRowReal == currentRowReal) {
                        intCurrentRowReal -= 1;
                    }
                    if (direction.equals("right")) {
                        xChinese = starterX + pageSizeX - firstColWidth - (intCurrentRowReal - 1) * (lineWidth - 10) * (maxWidthPercent + intervalPercent) - (lineWidth - 10) * intervalPercent - textWidth;
                    } else {
                        xChinese = starterX + firstColWidth + (intCurrentRowReal - 1) * (lineWidth - 10) * (maxWidthPercent + intervalPercent) + (lineWidth - 10) * intervalPercent;
                    }
                }

                // 英文
                PdfCanvas canvas5 = new PdfCanvas(page);
                float textWidth2 = arial.getWidth(element[1], 66);

                double scaleEnglish = 1;
                if (currentRow + branchNumLines <= exitInfo.get(i).size()) { // 右侧有文字
                    if (currentRow <= branchNumLines) { // 第1列
                        if (textWidth2 >= (lineWidth - 10) * maxWidthPercent * expandPercent) { // 如果超长
                            scaleEnglish = (lineWidth - 10) * maxWidthPercent * expandPercent / textWidth2;
                        }
                    } else {  // 后续列
                        if (textWidth2 >= (lineWidth - 10) * maxWidthPercent) {  // 如果超长
                            scaleEnglish = (lineWidth - 10) * maxWidthPercent / textWidth2;
                        }
                    }
                } else { // 右侧无文字
                    if (currentRow <= branchNumLines) { // 第1列
                        if (textWidth2 >= (lineWidth - 10) - (lineWidth - 10) * 0.005) {
                            scaleEnglish = ((lineWidth - 10) - (lineWidth - 10) * 0.005) / textWidth2;
                        }
                    } else if (currentRow <= branchNumLines * (colNeeded - 1)) { // 第2列-最后一列
                        int currentRowReal2 = (int) Math.ceil((double) currentRow / branchNumLines); // 向上取整
                        int numInterval = currentRowReal2 - 1;
                        int numMaxWidth = currentRowReal2 - 2;
                        if (numMaxWidth < 0) { // 专门解决第2列时可能会为负的问题
                            numMaxWidth = 0;
                        }
                        if (textWidth2 >= (lineWidth - 10) - (lineWidth - 10) * maxWidthPercent * expandPercent - (lineWidth - 10) * numInterval * intervalPercent - (lineWidth - 10) * numMaxWidth * maxWidthPercent - (lineWidth - 10) * 0.005) {
                            scaleEnglish = ((lineWidth - 10) - (lineWidth - 10) * maxWidthPercent * expandPercent - (lineWidth - 10) * numInterval * intervalPercent - (lineWidth - 10) * numMaxWidth * maxWidthPercent - (lineWidth - 10) * 0.005) / textWidth2;
                        }
                    } else { // 最后一列(实际位置最后一列)
                        if (textWidth2 >= (lineWidth - 10) * (maxWidthPercent - 0.005)) { // 如果超长
                            scaleEnglish = (lineWidth - 10) * (maxWidthPercent - 0.005) / textWidth2;
                        }
                    }
                }
                textWidth2 = textWidth2 * (float) scaleEnglish;

                if (currentRow <= branchNumLines) {
                    if (direction.equals("right")) {
                        xEnglish = starterX + pageSizeX - 33.957 * 2.83464567f - textWidth2;
                    } else {
                        xEnglish = starterX + 33.957 * 2.83464567f;
                    }
                } else {
                    double firstColWidth = 33.957 * 2.83464567f + (lineWidth - 10) * maxWidthPercent * expandPercent;
                    // 每1列最后一项因为可以整除 会误判 因此加入判断如果currentRow算出的实际列数为整数 就-1
                    double currentRowReal = (double) currentRow / branchNumLines;
                    int intCurrentRowReal = (int) currentRowReal;
                    if (intCurrentRowReal == currentRowReal) {
                        intCurrentRowReal -= 1;
                    }
                    if (direction.equals("right")) {
                        xEnglish = starterX + pageSizeX - firstColWidth - (intCurrentRowReal - 1) * (lineWidth - 10) * (maxWidthPercent + intervalPercent) - (lineWidth - 10) * intervalPercent - textWidth2;
                    } else {
                        xEnglish = starterX + firstColWidth + (intCurrentRowReal - 1) * (lineWidth - 10) * (maxWidthPercent + intervalPercent) + (lineWidth - 10) * intervalPercent;
                    }
                }

                // 广告Logo
                if (ad == true) {
                    // 获取广告Logo图片地址
                    String logoPath = "";
                    try {
                        File excelFile = new File("/Users/daizhenjin/Downloads/logoAddress.xlsx");
                        BufferedInputStream bis = new BufferedInputStream(new FileInputStream(excelFile));
                        XSSFWorkbook workbook = new XSSFWorkbook(bis);
                        XSSFSheet sheet = workbook.getSheetAt(0);
                        int lastRow = sheet.getLastRowNum();

                        for (int j = 0; j <= lastRow; j++) {
                            if (sheet.getRow(j).getCell(0).getStringCellValue().equals(element[0])) {
                                logoPath = sheet.getRow(j).getCell(1).getStringCellValue();
                            }
                        }
                    } catch (FileNotFoundException e) {
                        System.err.println(e.getMessage());
                    } catch (IOException e) {
                        System.err.println(e.getMessage());
                    }

                    if (logoPath.substring(logoPath.lastIndexOf('.')).contains("pdf")) { // 如果Logo是pdf格式的
                        // 创建画布
                        PdfCanvas canvas8 = new PdfCanvas(page);

                        // 打开源PDF文档
                        PdfDocument srcPdfDocument = new PdfDocument(new PdfReader(logoPath));
                        PdfPage srcPage = srcPdfDocument.getFirstPage();
                        // 获取Logo宽高
                        PdfFormXObject formXObject = srcPage.copyAsFormXObject(pdfDocument);
                        float logoWidth = srcPage.getPageSize().getWidth();
                        float logoHeight = srcPage.getPageSize().getHeight();

                        if (exitInfo.get(i).size() <= branchNumLines // 只有1列, 加在右侧
                                || (exitInfo.get(i).size() > branchNumLines // 多列, 位于最后1列 (不能是物理意义的最后1列)
                                && currentRow + branchNumLines > exitInfo.get(i).size())) {
                            double longerTextWidth;
                            if (textWidth > textWidth2) {
                                longerTextWidth = textWidth;
                            } else {
                                longerTextWidth = textWidth2;
                            }

                            double moreLeftTextX;
                            if (xChinese < xEnglish) {
                                moreLeftTextX = xChinese;
                            } else {
                                moreLeftTextX = xEnglish;
                            }

                            double remainingWidth;
                            if (direction.equals("left")) {
                                remainingWidth = 28.641 * 2.83464567f + (lineWidth - 10) - (xChinese - starterX) - longerTextWidth;
                            } else {
                                remainingWidth = moreLeftTextX - (starterX + 28.641 * 2.83464567f + 10);
                            }

                            if ((51.5 / logoHeight) * logoWidth * 2.83464567f + 5 * 2.83464567f < remainingWidth) { // 高度为51.5时, 宽度没有超过剩余空间
                                float logoWidthModified = (float) 51.5 * 2.83464567f / logoHeight * logoWidth;
                                float logoScale = (float) 51.5 * 2.83464567f / logoHeight;
                                if (direction.equals("left")) {
                                    canvas8.addXObjectWithTransformationMatrix(formXObject, logoScale, 0, 0, logoScale, (float) (xChinese + longerTextWidth + 5 * 2.83464567f), (float) yEnglish + 2.83464567f);
                                } else {
                                    canvas8.addXObjectWithTransformationMatrix(formXObject, logoScale, 0, 0, logoScale, (float) moreLeftTextX - logoWidthModified - 5 * 2.83464567f, (float) yEnglish + 2.83464567f);
                                }
                            } else { // 扁、宽图或文字超长
                                float logoWidthModified = (float) (intervalPercent * (lineWidth - 10));
                                float logoHeightModified = logoWidthModified / logoWidth * logoHeight;
                                if (logoHeightModified > 51.5 * 2.83464567f) {
                                    logoWidthModified = (float) 51.5 * 2.83464567f / logoHeight * logoWidth;
                                    logoHeightModified = (float) 51.5 * 2.83464567f;
                                }
                                float logoScale = logoWidthModified / logoWidth;

                                // 修改中文压缩比例、位置
                                // 剩余空间计算
                                double chineseRemainingWidth;
                                if (direction.equals("left")) {
                                    chineseRemainingWidth = 28.641 * 2.83464567f + (lineWidth - 10) - (xChinese - starterX) - textWidth;
                                } else {
                                    chineseRemainingWidth = xChinese - (starterX + 28.641 * 2.83464567f + 10);
                                }
                                if (chineseRemainingWidth < logoWidthModified + 10 * 2.83464567f) { // 中文超长
                                    double addScale;
                                    if (direction.equals("left")) {
                                        addScale = (28.641 * 2.83464567f + lineWidth - (xChinese - starterX) - logoWidthModified - 10 * 2.83464567f) / textWidth; // 横线右侧终点 - 文字左端坐标 - Logo宽度
                                    } else {
                                        addScale = ((xChinese + textWidth - starterX) - (28.641 * 2.83464567f + 10 * 2.83464567f) - logoWidthModified) / textWidth; // 文字右端坐标 - 横线左侧起点 - Logo宽度
                                    }
                                    scaleChinese *= addScale;
                                    if (direction.equals("right")) {
                                        double newTextWidth = textWidth * addScale; // 计算调整后的新文字长度
                                        xChinese += textWidth - newTextWidth; // 文字x坐标加上新旧文字长度差
                                    }
                                }

                                // 修改英文压缩比例、位置
                                // 剩余空间计算
                                double englishRemainingWidth;
                                if (direction.equals("left")) {
                                    englishRemainingWidth = 28.641 * 2.83464567f + (lineWidth - 10) - (xEnglish - starterX) - textWidth2;
                                } else {
                                    englishRemainingWidth = xEnglish - (starterX + 28.641 * 2.83464567f + 10);
                                }
                                if (englishRemainingWidth < logoWidthModified + 10 * 2.83464567f) { // 英文超长
                                    double addScale;
                                    if (direction.equals("left")) {
                                        addScale = (28.641 * 2.83464567f + lineWidth - (xEnglish - starterX) - logoWidthModified - 10 * 2.83464567f) / textWidth2; // 横线右侧终点 - 文字左端坐标 - Logo宽度
                                    } else {
                                        addScale = ((xEnglish + textWidth2 - starterX) - (28.641 * 2.83464567f + 10 * 2.83464567f) - logoWidthModified) / textWidth2; // 文字右端坐标 - 横线左侧起点 - Logo宽度
                                    }
                                    scaleEnglish *= addScale;
                                    if (direction.equals("right")) {
                                        double newTextWidth2 = textWidth2 * addScale; // 计算调整后的新文字长度
                                        xEnglish += textWidth2 - newTextWidth2; // 文字x坐标加上新旧文字长度差
                                    }
                                }

                                if (direction.equals("left")) {
                                    canvas8.addXObjectWithTransformationMatrix(formXObject, logoScale, 0, 0, logoScale, (float) (starterX + 28.641 * 2.83464567f + (lineWidth - 10) - logoWidthModified), (float) yEnglish + (54 * 2.83464567f - logoHeightModified) / 2);
                                } else {
                                    canvas8.addXObjectWithTransformationMatrix(formXObject, logoScale, 0, 0, logoScale, (float) (starterX + 28.641 * 2.83464567f + 10), (float) yEnglish + (54 * 2.83464567f - logoHeightModified) / 2);
                                }
                            }
                        } else { // 有多列
                            if (currentRow <= branchNumLines) { // 第1列
                                double longerTextWidth;
                                if (textWidth > textWidth2) {
                                    longerTextWidth = textWidth;
                                } else {
                                    longerTextWidth = textWidth2;
                                }
                                double moreLeftTextX;
                                if (xChinese < xEnglish) {
                                    moreLeftTextX = xChinese;
                                } else {
                                    moreLeftTextX = xEnglish;
                                }
                                if ((51.5 / logoHeight) * logoWidth * 2.83464567f < intervalPercent * (lineWidth - 10) - 2.83464567f) { // 高度为51.5时, 宽度没有超过间隔 
                                    float logoWidthModified = (float) 51.5 * 2.83464567f / logoHeight * logoWidth; // 按照高度调整宽度
                                    float logoScale = (float) 51.5 * 2.83464567f / logoHeight;
                                    if (direction.equals("left")) {
                                        canvas8.addXObjectWithTransformationMatrix(formXObject, logoScale, 0, 0, logoScale, (float) (xChinese + longerTextWidth + 5 * 2.83464567f), (float) yEnglish + 2.83464567f);
                                    } else {
                                        canvas8.addXObjectWithTransformationMatrix(formXObject, logoScale, 0, 0, logoScale, (float) (moreLeftTextX - 5 * 2.83464567f - logoWidthModified), (float) yEnglish + 2.83464567f);
                                    }
                                } else { // 扁、宽图
                                    float logoWidthModified = (float) (intervalPercent * (lineWidth - 10) - 10 * 2.83464567f);
                                    float logoHeightModified = logoWidthModified / logoWidth * logoHeight;
                                    float logoScale = logoWidthModified / logoWidth;
                                    if (direction.equals("left")) {
                                        canvas8.addXObjectWithTransformationMatrix(formXObject, logoScale, 0, 0, logoScale, (float) (xChinese + longerTextWidth + 5 * 2.83464567f), (float) yEnglish + (54 * 2.83464567f - logoHeightModified) / 2);
                                    } else {
                                        canvas8.addXObjectWithTransformationMatrix(formXObject, logoScale, 0, 0, logoScale, (float) (moreLeftTextX - 5 * 2.83464567f - logoWidthModified), (float) yEnglish + (54 * 2.83464567f - logoHeightModified) / 2);
                                    }
                                }
                            } else { // 后续列, Logo在前
                                if ((51.5 / logoHeight) * logoWidth * 2.83464567f < intervalPercent * (lineWidth - 10) - 2.83464567f) { // 高度为51.5时, 宽度没有超过间隔 
                                    float logoWidthModified = (float) 51.5 * 2.83464567f / logoHeight * logoWidth; // 按照高度调整宽度
                                    float logoScale = (float) 51.5 * 2.83464567f / logoHeight;
                                    if (direction.equals("left")) {
                                        canvas8.addXObjectWithTransformationMatrix(formXObject, logoScale, 0, 0, logoScale, (float) xChinese - logoWidthModified - 5 * 2.83464567f, (float) yEnglish + 2.83464567f);
                                    } else {
                                        canvas8.addXObjectWithTransformationMatrix(formXObject, logoScale, 0, 0, logoScale, (float) xChinese + textWidth + 5 * 2.83464567f, (float) yEnglish + 2.83464567f);
                                    }
                                } else { // 扁、宽图
                                    float logoWidthModified = (float) (intervalPercent * (lineWidth - 10) - 10 * 2.83464567f);
                                    float logoHeightModified = logoWidthModified / logoWidth * logoHeight;
                                    float logoScale = logoWidthModified / logoWidth;
                                    if (direction.equals("left")) {
                                        canvas8.addXObjectWithTransformationMatrix(formXObject, logoScale, 0, 0, logoScale, (float) xChinese - logoWidthModified - 5 * 2.83464567f, (float) yEnglish + (54 * 2.83464567f - logoHeightModified) / 2);
                                    } else {
                                        canvas8.addXObjectWithTransformationMatrix(formXObject, logoScale, 0, 0, logoScale, (float) xChinese + textWidth + 5 * 2.83464567f, (float) yEnglish + (54 * 2.83464567f - logoHeightModified) / 2);
                                    }
                                }
                            }
                        }
                    } else {
                        File adLogoFile = new File(logoPath);
                        ImageData imageData = ImageDataFactory.create(adLogoFile.getAbsolutePath());
                        Image adLogo = new Image(imageData);
                        float logoWidth = adLogo.getImageScaledWidth();
                        float logoHeight = adLogo.getImageScaledHeight();

                        if (exitInfo.get(i).size() <= branchNumLines // 只有1列, 加在右侧
                                || (exitInfo.get(i).size() > branchNumLines // 多列, 位于最后1列 (不能是物理意义的最后1列)
                                && currentRow + branchNumLines > exitInfo.get(i).size())) {
                            double longerTextWidth;
                            if (textWidth > textWidth2) {
                                longerTextWidth = textWidth;
                            } else {
                                longerTextWidth = textWidth2;
                            }

                            double moreLeftTextX;
                            if (xChinese < xEnglish) {
                                moreLeftTextX = xChinese;
                            } else {
                                moreLeftTextX = xEnglish;
                            }

                            double remainingWidth;
                            if (direction.equals("left")) {
                                remainingWidth = 28.641 * 2.83464567f + (lineWidth - 10) - (xChinese - starterX) - longerTextWidth;
                            } else {
                                remainingWidth = moreLeftTextX - (starterX + 28.641 * 2.83464567f + 10);
                            }

                            if ((51.5 / logoHeight) * logoWidth * 2.83464567f + 5 * 2.83464567f < remainingWidth) { // 高度为51.5时, 宽度没有超过剩余空间
                                float logoWidthModified = (float) (51.5 / logoHeight) * logoWidth * 2.83464567f;
                                adLogo.setWidth(logoWidthModified);
                                adLogo.setHeight((float) 51.5 * 2.83464567f);
                                if (direction.equals("left")) {
                                    adLogo.setFixedPosition((float) (xChinese + longerTextWidth + 5 * 2.83464567f), (float) yEnglish + 2.83464567f);
                                } else {
                                    adLogo.setFixedPosition((float) moreLeftTextX - logoWidthModified - 5 * 2.83464567f, (float) yEnglish + 2.83464567f);
                                }
                            } else { // 扁、宽图或文字超长
                                float logoWidthModified = (float) (intervalPercent * (lineWidth - 10));
                                float logoHeightModified = (float) (logoWidthModified / 2.83464567f / logoWidth * logoHeight) * 2.83464567f;
                                if (logoHeightModified > 51.5 * 2.83464567f) {
                                    logoWidthModified = (float) (51.5 / logoHeight * logoWidth) * 2.83464567f;
                                    logoHeightModified = (float) 51.5 * 2.83464567f;
                                }
                                adLogo.setWidth(logoWidthModified);
                                adLogo.setHeight(logoHeightModified); // 按照宽度调整高度

                                // 修改中文压缩比例、位置
                                // 剩余空间计算
                                double chineseRemainingWidth;
                                if (direction.equals("left")) {
                                    chineseRemainingWidth = 28.641 * 2.83464567f + (lineWidth - 10) - (xChinese - starterX) - textWidth;
                                } else {
                                    chineseRemainingWidth = xChinese - (starterX + 28.641 * 2.83464567f + 10);
                                }
                                if (chineseRemainingWidth < logoWidthModified + 10 * 2.83464567f) { // 中文超长
                                    double addScale;
                                    if (direction.equals("left")) {
                                        addScale = (28.641 * 2.83464567f + lineWidth - (xChinese - starterX) - logoWidthModified - 10 * 2.83464567f) / textWidth; // 横线右侧终点 - 文字左端坐标 - Logo宽度
                                    } else {
                                        addScale = ((xChinese + textWidth - starterX) - (28.641 * 2.83464567f + 10 * 2.83464567f) - logoWidthModified) / textWidth; // 文字右端坐标 - 横线左侧起点 - Logo宽度
                                    }
                                    scaleChinese *= addScale;
                                    if (direction.equals("right")) {
                                        double newTextWidth = textWidth * addScale; // 计算调整后的新文字长度
                                        xChinese += textWidth - newTextWidth; // 文字x坐标加上新旧文字长度差
                                    }
                                }

                                // 修改英文压缩比例、位置
                                // 剩余空间计算
                                double englishRemainingWidth;
                                if (direction.equals("left")) {
                                    englishRemainingWidth = 28.641 * 2.83464567f + (lineWidth - 10) - (xEnglish - starterX) - textWidth2;
                                } else {
                                    englishRemainingWidth = xEnglish - (starterX + 28.641 * 2.83464567f + 10);
                                }
                                if (englishRemainingWidth < logoWidthModified + 10 * 2.83464567f) { // 英文超长
                                    double addScale;
                                    if (direction.equals("left")) {
                                        addScale = (28.641 * 2.83464567f + lineWidth - (xEnglish - starterX) - logoWidthModified - 10 * 2.83464567f) / textWidth2; // 横线右侧终点 - 文字左端坐标 - Logo宽度
                                    } else {
                                        addScale = ((xEnglish + textWidth2 - starterX) - (28.641 * 2.83464567f + 10 * 2.83464567f) - logoWidthModified) / textWidth2; // 文字右端坐标 - 横线左侧起点 - Logo宽度
                                    }
                                    scaleEnglish *= addScale;
                                    if (direction.equals("right")) {
                                        double newTextWidth2 = textWidth2 * addScale; // 计算调整后的新文字长度
                                        xEnglish += textWidth2 - newTextWidth2; // 文字x坐标加上新旧文字长度差
                                    }
                                }

                                if (direction.equals("left")) {
                                    adLogo.setFixedPosition((float) (starterX + 28.641 * 2.83464567f + (lineWidth - 10) - logoWidthModified), (float) yEnglish + (54 * 2.83464567f - logoHeightModified) / 2);
                                } else {
                                    adLogo.setFixedPosition((float) (starterX + 28.641 * 2.83464567f + 10), (float) yEnglish + (54 * 2.83464567f - logoHeightModified) / 2);
                                }
                            }
                        } else { // 有多列
                            if (currentRow <= branchNumLines) { // 第1列
                                double longerTextWidth;
                                if (textWidth > textWidth2) {
                                    longerTextWidth = textWidth;
                                } else {
                                    longerTextWidth = textWidth2;
                                }
                                double moreLeftTextX;
                                if (xChinese < xEnglish) {
                                    moreLeftTextX = xChinese;
                                } else {
                                    moreLeftTextX = xEnglish;
                                }
                                if ((51.5 / logoHeight) * logoWidth * 2.83464567f < intervalPercent * (lineWidth - 10) - 2.83464567f) { // 高度为51.5时, 宽度没有超过间隔 
                                    float logoWidthModified = (float) (51.5 / logoHeight * logoWidth) * 2.83464567f; // 按照高度调整宽度
                                    adLogo.setWidth(logoWidthModified);
                                    adLogo.setHeight((float) 51.5 * 2.83464567f);
                                    if (direction.equals("left")) {
                                        adLogo.setFixedPosition((float) (xChinese + longerTextWidth + 5 * 2.83464567f), (float) yEnglish + 2.83464567f); // 自动浮动高度
                                    } else {
                                        adLogo.setFixedPosition((float) (moreLeftTextX - 5 * 2.83464567f - logoWidthModified), (float) yEnglish + 2.83464567f); // 自动浮动高度
                                    }
                                } else { // 扁、宽图
                                    float logoWidthModified = (float) (intervalPercent * (lineWidth - 10) - 10 * 2.83464567f);
                                    float logoHeightModified = (float) (logoWidthModified / 2.83464567f / logoWidth * logoHeight) * 2.83464567f;
                                    adLogo.setWidth(logoWidthModified);
                                    adLogo.setHeight(logoHeightModified); // 按照宽度调整高度
                                    if (direction.equals("left")) {
                                        adLogo.setFixedPosition((float) (xChinese + longerTextWidth + 5 * 2.83464567f), (float) yEnglish + (54 * 2.83464567f - logoHeightModified) / 2); // 自动浮动高度
                                    } else {
                                        adLogo.setFixedPosition((float) (moreLeftTextX - 5 * 2.83464567f - logoWidthModified), (float) yEnglish + (54 * 2.83464567f - logoHeightModified) / 2); // 自动浮动高度
                                    }
                                }
                            } else { // 后续列, Logo在前
                                if ((51.5 / logoHeight) * logoWidth * 2.83464567f < intervalPercent * (lineWidth - 10) - 2.83464567f) { // 高度为51.5时, 宽度没有超过间隔 
                                    float logoWidthModified = (float) (51.5 / logoHeight * logoWidth) * 2.83464567f; // 按照高度调整宽度
                                    adLogo.setWidth(logoWidthModified);
                                    adLogo.setHeight((float) 51.5 * 2.83464567f);
                                    if (direction.equals("left")) {
                                        adLogo.setFixedPosition((float) xChinese - logoWidthModified - 5 * 2.83464567f, (float) yEnglish + 2.83464567f);
                                    } else {
                                        adLogo.setFixedPosition((float) xChinese + textWidth + 5 * 2.83464567f, (float) yEnglish + 2.83464567f);
                                    }
                                } else { // 扁、宽图
                                    float logoWidthModified = (float) (intervalPercent * (lineWidth - 10) - 10 * 2.83464567f);
                                    float logoHeightModified = (float) (logoWidthModified / 2.83464567f / logoWidth * logoHeight) * 2.83464567f;
                                    adLogo.setWidth(logoWidthModified);
                                    adLogo.setHeight(logoHeightModified); // 按照宽度调整高度
                                    if (direction.equals("left")) {
                                        adLogo.setFixedPosition((float) xChinese - logoWidthModified - 5 * 2.83464567f, (float) yEnglish + (54 * 2.83464567f - logoHeightModified) / 2);
                                    } else {
                                        adLogo.setFixedPosition((float) xChinese + textWidth + 5 * 2.83464567f, (float) yEnglish + (54 * 2.83464567f - logoHeightModified) / 2);
                                    }
                                }
                            }
                        }

                        document.add(adLogo);
                    }
                }

                // 中文输出
                canvas4.beginText();
                canvas4.setFontAndSize(heiTi, 100);
                canvas4.setCharacterSpacing(chineseSpace);
                canvas4.setTextRenderingMode(PdfCanvasConstants.TextRenderingMode.FILL_STROKE);
                canvas4.setLineWidth(0.21f);
                canvas4.setStrokeColor(black);
                canvas4.setFillColor(black);

                canvas4.saveState();
                canvas4.setTextMatrix((float) scaleChinese, 0, 0, 1, 0, 0);

                canvas4.moveText(xChinese / scaleChinese, yChinese); // 设置文本的起始位置
                canvas4.showText(element[0]);
                yChinese -= interval;
                canvas4.endText();

                // 英文输出
                canvas5.beginText();
                canvas5.setFontAndSize(arial, 66);
                canvas5.setCharacterSpacing(0);
                canvas5.setTextRenderingMode(PdfCanvasConstants.TextRenderingMode.FILL_STROKE);
                canvas5.setLineWidth(0.21f);
                canvas5.setStrokeColor(black);
                canvas5.setFillColor(black);

                canvas5.saveState();
                canvas5.setTextMatrix((float) scaleEnglish, 0, 0, 1, 0, 0);

                canvas5.moveText(xEnglish / scaleEnglish, yEnglish); // 设置文本的起始位置
                canvas5.showText(element[1]);
                yEnglish -= interval;
                canvas5.endText();

                currentRow += 1;
                // 不位于第1列 且正好是新的一列的第1个
                if (currentRow > branchNumLines && currentRow % branchNumLines == 1) {
                    yChinese = yOutline - 54.27 * 2.83464567f;
                    yEnglish = yOutline - 80.22 * 2.83464567f;
                }
            }
            // for循环输出出入口信息结束

            // 出入口上方图标
            // 创建一个临时的 PdfFormXObject
            PdfFormXObject template2 = new PdfFormXObject(tempPageSize);
            // 在临时 canvas 上添加内容
            PdfCanvas canvas8 = new PdfCanvas(template2, pdfDocument);

            final float iconExitInterval = (float) 15 * 2.83464567f;
            final float maxInfoX2 = (float) ((lineWidth + 28.641 * 2.83464567f) - ((28.83 + 73.16) * 2.83464567f + iconExitInterval));

            final float iconWidth2 = (float) 70.649 * 2.83464567f;
            final float iconInterval2 = (float) 8.603 * 2.83464567f;

            // 当前口设施图标
            float nextInfoX2 = 0;
            if (direction.equals("left") || (direction.equals("right") && arrowType == false)) { // 左对齐或右对齐但无箭头
                int index2 = 1;
                int publicTransport = 0;
                for (String element : facilityInfo.get(i)) { // 计算交通设施数量 只有最后一个加竖线
                    if (element.equals("卫生间") == false && element.equals("母婴室") == false && element.equals("垂梯") == false) {
                        publicTransport++;
                    }
                }

                for (String element : facilityInfo.get(i)) {
                    switch (element) {
                        case "卫生间" -> {
                            try (InputStream pdfStreamIcon = UnpaidArea.class.getResourceAsStream("/pdfs/paid/paid_toilet.pdf"); PdfDocument srcPdfDocument = new PdfDocument(new PdfReader(pdfStreamIcon))) {
                                PdfFormXObject pageXObject = srcPdfDocument.getFirstPage().copyAsFormXObject(pdfDocument);
                                canvas8.addXObject(pageXObject, nextInfoX2, 0);
                            }
                            nextInfoX2 += iconWidth2 + iconInterval2;

                            try (InputStream pdfStreamIcon = UnpaidArea.class.getResourceAsStream("/pdfs/paid/paid_wheelchair.pdf"); PdfDocument srcPdfDocument = new PdfDocument(new PdfReader(pdfStreamIcon))) {
                                PdfFormXObject pageXObject = srcPdfDocument.getFirstPage().copyAsFormXObject(pdfDocument);
                                canvas8.addXObject(pageXObject, nextInfoX2, 0);
                            }
                        }
                        case "母婴室" -> {
                            try (InputStream pdfStreamIcon = UnpaidArea.class.getResourceAsStream("/pdfs/paid/paid_nursing.pdf"); PdfDocument srcPdfDocument = new PdfDocument(new PdfReader(pdfStreamIcon))) {
                                PdfFormXObject pageXObject = srcPdfDocument.getFirstPage().copyAsFormXObject(pdfDocument);
                                canvas8.addXObject(pageXObject, nextInfoX2, 0);
                            }
                        }
                        case "垂梯" -> {
                            try (InputStream pdfStreamIcon = UnpaidArea.class.getResourceAsStream("/pdfs/paid/paid_elevator.pdf"); PdfDocument srcPdfDocument = new PdfDocument(new PdfReader(pdfStreamIcon))) {
                                PdfFormXObject pageXObject = srcPdfDocument.getFirstPage().copyAsFormXObject(pdfDocument);
                                canvas8.addXObject(pageXObject, nextInfoX2, 0);
                            }
                            nextInfoX2 += iconWidth2 + iconInterval2;

                            try (InputStream pdfStreamIcon = UnpaidArea.class.getResourceAsStream("/pdfs/paid/paid_wheelchair.pdf"); PdfDocument srcPdfDocument = new PdfDocument(new PdfReader(pdfStreamIcon))) {
                                PdfFormXObject pageXObject = srcPdfDocument.getFirstPage().copyAsFormXObject(pdfDocument);
                                canvas8.addXObject(pageXObject, nextInfoX2, 0);
                            }
                        }
                        case "国铁" -> {
                            try (InputStream pdfStreamIcon = UnpaidArea.class.getResourceAsStream("/pdfs/paid/paid_railway.pdf"); PdfDocument srcPdfDocument = new PdfDocument(new PdfReader(pdfStreamIcon))) {
                                PdfFormXObject pageXObject = srcPdfDocument.getFirstPage().copyAsFormXObject(pdfDocument);
                                canvas8.addXObject(pageXObject, nextInfoX2, 0);
                            }
                        }
                        case "长途客运" -> {
                            try (InputStream pdfStreamIcon = UnpaidArea.class.getResourceAsStream("/pdfs/paid/paid_coach.pdf"); PdfDocument srcPdfDocument = new PdfDocument(new PdfReader(pdfStreamIcon))) {
                                PdfFormXObject pageXObject = srcPdfDocument.getFirstPage().copyAsFormXObject(pdfDocument);
                                canvas8.addXObject(pageXObject, nextInfoX2, 0);
                            }
                        }
                        case "机场巴士" -> {
                            try (InputStream pdfStreamIcon = UnpaidArea.class.getResourceAsStream("/pdfs/paid/paid_airportbus.pdf"); PdfDocument srcPdfDocument = new PdfDocument(new PdfReader(pdfStreamIcon))) {
                                PdfFormXObject pageXObject = srcPdfDocument.getFirstPage().copyAsFormXObject(pdfDocument);
                                canvas8.addXObject(pageXObject, nextInfoX2, 0);
                            }
                        }
                        case "公交" -> {
                            try (InputStream pdfStreamIcon = UnpaidArea.class.getResourceAsStream("/pdfs/paid/paid_bus.pdf"); PdfDocument srcPdfDocument = new PdfDocument(new PdfReader(pdfStreamIcon))) {
                                PdfFormXObject pageXObject = srcPdfDocument.getFirstPage().copyAsFormXObject(pdfDocument);
                                canvas8.addXObject(pageXObject, nextInfoX2, 0);
                            }
                        }
                        case "停车场" -> {
                            try (InputStream pdfStreamIcon = UnpaidArea.class.getResourceAsStream("/pdfs/paid/paid_parking.pdf"); PdfDocument srcPdfDocument = new PdfDocument(new PdfReader(pdfStreamIcon))) {
                                PdfFormXObject pageXObject = srcPdfDocument.getFirstPage().copyAsFormXObject(pdfDocument);
                                canvas8.addXObject(pageXObject, nextInfoX2, 0);
                            }
                        }
                        case "出租车" -> {
                            try (InputStream pdfStreamIcon = UnpaidArea.class.getResourceAsStream("/pdfs/paid/paid_taxi.pdf"); PdfDocument srcPdfDocument = new PdfDocument(new PdfReader(pdfStreamIcon))) {
                                PdfFormXObject pageXObject = srcPdfDocument.getFirstPage().copyAsFormXObject(pdfDocument);
                                canvas8.addXObject(pageXObject, nextInfoX2, 0);
                            }
                        }
                        case "网约车" -> {
                            try (InputStream pdfStreamIcon = UnpaidArea.class.getResourceAsStream("/pdfs/paid/paid_ehailing.pdf"); PdfDocument srcPdfDocument = new PdfDocument(new PdfReader(pdfStreamIcon))) {
                                PdfFormXObject pageXObject = srcPdfDocument.getFirstPage().copyAsFormXObject(pdfDocument);
                                canvas8.addXObject(pageXObject, nextInfoX2, 0);
                            }
                        }
                        default -> {
                        }
                    }

                    if (index2 < facilityInfo.get(i).size()) {
                        nextInfoX2 += iconWidth2 + iconInterval2 * 1.1;
                    } else {
                        nextInfoX2 += iconWidth2;
                    }

                    if (index2 >= publicTransport && index2 < facilityInfo.get(i).size()) { // 只有最后一个公共交通设施加竖线
                        // 竖线
                        canvas8.saveState()
                                .setFillColor(black)
                                .rectangle(nextInfoX2, 0, 1.59 * 2.83464567f, iconWidth2)
                                .fill()
                                .restoreState();

                        nextInfoX2 += 1.59 * 2.83464567f + iconInterval2 * 1.1;
                    }

                    index2++;
                }
            } else { // 右对齐 右箭头
                int index2 = 1;
                int publicTransport = 0;
                for (String element : facilityInfo.get(i)) { // 计算交通设施数量 只有最后一个加竖线
                    if (element.equals("卫生间") == false && element.equals("母婴室") == false && element.equals("垂梯") == false) {
                        publicTransport++;
                    }
                }

                for (int n = facilityInfo.get(i).size() - 1; n >= 0; n--) {
                    String element = facilityInfo.get(i).get(n);
                    switch (element) {
                        case "卫生间" -> {
                            try (InputStream pdfStreamIcon = UnpaidArea.class.getResourceAsStream("/pdfs/paid/paid_wheelchair_inverse.pdf"); PdfDocument srcPdfDocument = new PdfDocument(new PdfReader(pdfStreamIcon))) {
                                PdfFormXObject pageXObject = srcPdfDocument.getFirstPage().copyAsFormXObject(pdfDocument);
                                canvas8.addXObject(pageXObject, nextInfoX2, 0);
                            }
                            nextInfoX2 += iconWidth2 + iconInterval2;

                            try (InputStream pdfStreamIcon = UnpaidArea.class.getResourceAsStream("/pdfs/paid/paid_toilet.pdf"); PdfDocument srcPdfDocument = new PdfDocument(new PdfReader(pdfStreamIcon))) {
                                PdfFormXObject pageXObject = srcPdfDocument.getFirstPage().copyAsFormXObject(pdfDocument);
                                canvas8.addXObject(pageXObject, nextInfoX2, 0);
                            }
                        }
                        case "母婴室" -> {
                            try (InputStream pdfStreamIcon = UnpaidArea.class.getResourceAsStream("/pdfs/paid/paid_nursing.pdf"); PdfDocument srcPdfDocument = new PdfDocument(new PdfReader(pdfStreamIcon))) {
                                PdfFormXObject pageXObject = srcPdfDocument.getFirstPage().copyAsFormXObject(pdfDocument);
                                canvas8.addXObject(pageXObject, nextInfoX2, 0);
                            }
                        }
                        case "垂梯" -> {
                            try (InputStream pdfStreamIcon = UnpaidArea.class.getResourceAsStream("/pdfs/paid/paid_wheelchair_inverse.pdf"); PdfDocument srcPdfDocument = new PdfDocument(new PdfReader(pdfStreamIcon))) {
                                PdfFormXObject pageXObject = srcPdfDocument.getFirstPage().copyAsFormXObject(pdfDocument);
                                canvas8.addXObject(pageXObject, nextInfoX2, 0);
                            }
                            nextInfoX2 += iconWidth2 + iconInterval2;

                            try (InputStream pdfStreamIcon = UnpaidArea.class.getResourceAsStream("/pdfs/paid/paid_elevator.pdf"); PdfDocument srcPdfDocument = new PdfDocument(new PdfReader(pdfStreamIcon))) {
                                PdfFormXObject pageXObject = srcPdfDocument.getFirstPage().copyAsFormXObject(pdfDocument);
                                canvas8.addXObject(pageXObject, nextInfoX2, 0);
                            }
                        }
                        case "国铁" -> {
                            try (InputStream pdfStreamIcon = UnpaidArea.class.getResourceAsStream("/pdfs/paid/paid_railway.pdf"); PdfDocument srcPdfDocument = new PdfDocument(new PdfReader(pdfStreamIcon))) {
                                PdfFormXObject pageXObject = srcPdfDocument.getFirstPage().copyAsFormXObject(pdfDocument);
                                canvas8.addXObject(pageXObject, nextInfoX2, 0);
                            }
                        }
                        case "长途客运" -> {
                            try (InputStream pdfStreamIcon = UnpaidArea.class.getResourceAsStream("/pdfs/paid/paid_coach.pdf"); PdfDocument srcPdfDocument = new PdfDocument(new PdfReader(pdfStreamIcon))) {
                                PdfFormXObject pageXObject = srcPdfDocument.getFirstPage().copyAsFormXObject(pdfDocument);
                                canvas8.addXObject(pageXObject, nextInfoX2, 0);
                            }
                        }
                        case "机场巴士" -> {
                            try (InputStream pdfStreamIcon = UnpaidArea.class.getResourceAsStream("/pdfs/paid/paid_airportbus.pdf"); PdfDocument srcPdfDocument = new PdfDocument(new PdfReader(pdfStreamIcon))) {
                                PdfFormXObject pageXObject = srcPdfDocument.getFirstPage().copyAsFormXObject(pdfDocument);
                                canvas8.addXObject(pageXObject, nextInfoX2, 0);
                            }
                        }
                        case "公交" -> {
                            try (InputStream pdfStreamIcon = UnpaidArea.class.getResourceAsStream("/pdfs/paid/paid_bus.pdf"); PdfDocument srcPdfDocument = new PdfDocument(new PdfReader(pdfStreamIcon))) {
                                PdfFormXObject pageXObject = srcPdfDocument.getFirstPage().copyAsFormXObject(pdfDocument);
                                canvas8.addXObject(pageXObject, nextInfoX2, 0);
                            }
                        }
                        case "停车场" -> {
                            try (InputStream pdfStreamIcon = UnpaidArea.class.getResourceAsStream("/pdfs/paid/paid_parking.pdf"); PdfDocument srcPdfDocument = new PdfDocument(new PdfReader(pdfStreamIcon))) {
                                PdfFormXObject pageXObject = srcPdfDocument.getFirstPage().copyAsFormXObject(pdfDocument);
                                canvas8.addXObject(pageXObject, nextInfoX2, 0);
                            }
                        }
                        case "出租车" -> {
                            try (InputStream pdfStreamIcon = UnpaidArea.class.getResourceAsStream("/pdfs/paid/paid_taxi.pdf"); PdfDocument srcPdfDocument = new PdfDocument(new PdfReader(pdfStreamIcon))) {
                                PdfFormXObject pageXObject = srcPdfDocument.getFirstPage().copyAsFormXObject(pdfDocument);
                                canvas8.addXObject(pageXObject, nextInfoX2, 0);
                            }
                        }
                        case "网约车" -> {
                            try (InputStream pdfStreamIcon = UnpaidArea.class.getResourceAsStream("/pdfs/paid/paid_ehailing.pdf"); PdfDocument srcPdfDocument = new PdfDocument(new PdfReader(pdfStreamIcon))) {
                                PdfFormXObject pageXObject = srcPdfDocument.getFirstPage().copyAsFormXObject(pdfDocument);
                                canvas8.addXObject(pageXObject, nextInfoX2, 0);
                            }
                        }
                        default -> {
                        }
                    }

                    if (index2 < facilityInfo.get(i).size()) {
                        nextInfoX2 += iconWidth2 + iconInterval2 * 1.1;
                    } else {
                        nextInfoX2 += iconWidth2;
                    }

                    if (index2 <= (facilityInfo.get(i).size() - publicTransport) && index2 < facilityInfo.get(i).size()) { // 只有最后一个公共交通设施加竖线
                        // 竖线
                        canvas8.saveState()
                                .setFillColor(black)
                                .rectangle(nextInfoX2, 0, 1.59 * 2.83464567f, iconWidth2)
                                .fill()
                                .restoreState();

                        nextInfoX2 += 1.59 * 2.83464567f + iconInterval2 * 1.1;
                    }

                    index2++;
                }
            }
            yOutline = (float) (yOutline - (92.692 + 94.252) * 2.83464567f - (numLines.get(i) - 1) * interval);

            // 输出图标
            if (direction.equals("left")) {
                float scale2 = maxInfoX2 / nextInfoX2;
                float correctX2 = starterX + (float) (28.641 + 73.16) * 2.83464567f + iconExitInterval;
                float correctY2;
                PdfCanvas finalCanvas2 = new PdfCanvas(page);
                if (scale2 < 1) {
                    correctY2 = (float) (yOutline + (73.16 * 2.83464567f - iconWidth2 * scale2) / 2);
                    finalCanvas2.addXObjectWithTransformationMatrix(template2, scale2, 0, 0, scale2, correctX2, correctY2);
                } else {
                    correctY2 = (float) (yOutline + (73.16 * 2.83464567f - iconWidth2) / 2);
                    finalCanvas2.addXObjectWithTransformationMatrix(template2, 1, 0, 0, 1, correctX2, correctY2);
                }
            } else {
                float scale2 = maxInfoX2 / nextInfoX2;
                float correctX2;
                float correctY2;
                PdfCanvas finalCanvas2 = new PdfCanvas(page);
                if (scale2 < 1) {
                    correctX2 = starterX + (float) (pageSizeX - (28.641 + 73.16) * 2.83464567f - iconExitInterval - nextInfoX2 * scale2);
                } else {
                    correctX2 = starterX + (float) (pageSizeX - (28.641 + 73.16) * 2.83464567f - iconExitInterval - nextInfoX2);
                }
                if (scale2 < 1) {
                    correctY2 = (float) (yOutline + (73.16 * 2.83464567f - iconWidth2 * scale2) / 2);
                    finalCanvas2.addXObjectWithTransformationMatrix(template2, scale2, 0, 0, scale2, correctX2, correctY2);
                } else {
                    correctY2 = (float) (yOutline + (73.16 * 2.83464567f - iconWidth2) / 2);
                    finalCanvas2.addXObjectWithTransformationMatrix(template2, 1, 0, 0, 1, correctX2, correctY2);
                }
            }

            if (i == entrance.size() - 1) {
                lastLineY = tempLineY + (float) interval - starterY;
            }
        }
        // 总循环结束

        backgroundColor = new DeviceCmyk(90, 0, 100, 0);
        PdfFormXObject pageXExit;
        // 白色出字
        try (InputStream pdfStreamIcon = UnpaidArea.class.getResourceAsStream("/pdfs/paid/exit.pdf"); PdfDocument srcPdfDocument = new PdfDocument(new PdfReader(pdfStreamIcon))) {
            pageXExit = srcPdfDocument.getFirstPage().copyAsFormXObject(pdfDocument);
        }
        if (arrowType == false) { // 无箭头
            if (direction.equals("right")) { // 右对齐
                // 绿色出矩形
                canvas.saveState()
                        .setFillColor(backgroundColor)
                        .rectangle(starterX + pageSizeX - (128.5 * 2.83464567f), topsBottom, 128.5 * 2.83464567f, 128.5 * 2.83464567f)
                        .fill()
                        .restoreState();
                PdfCanvas canvas2 = new PdfCanvas(pdfDocument.getFirstPage());
                float xExit = starterX + (float) (pageSizeX - 95.61 * 2.83464567f);
                float yExit = (float) (((topsBottom / 2.83464567f) + 22.164) * 2.83464567f);
                canvas2.addXObject(pageXExit, xExit, yExit);
            } else { // 左对齐
                // 绿色出矩形
                canvas.saveState()
                        .setFillColor(backgroundColor)
                        .rectangle(starterX, topsBottom, 128.5 * 2.83464567f, 128.5 * 2.83464567f)
                        .fill()
                        .restoreState();
                PdfCanvas canvas2 = new PdfCanvas(pdfDocument.getFirstPage());
                float xExit = starterX + (float) (32.89 * 2.83464567f);
                float yExit = (float) (((topsBottom / 2.83464567f) + 22.164) * 2.83464567f);
                canvas2.addXObject(pageXExit, xExit, yExit);
            }
        } else { // 带箭头
            PdfFormXObject pageXArrow;
            float y = (float) (((topsBottom / 2.83464567f) + 20.37) * 2.83464567f);
            float x;
            PdfCanvas canvas2 = new PdfCanvas(pdfDocument.getFirstPage());
            float yExit = (float) (((topsBottom / 2.83464567f) + 22.164) * 2.83464567f);
            if (direction.equals("right")) { // 右对齐 右箭头
                try (InputStream pdfStreamIcon = UnpaidArea.class.getResourceAsStream("/pdfs/paid/arrow_right.pdf"); PdfDocument srcPdfDocument = new PdfDocument(new PdfReader(pdfStreamIcon))) {
                    pageXArrow = srcPdfDocument.getFirstPage().copyAsFormXObject(pdfDocument);
                }
                x = starterX + (float) (((pageSizeX / 2.83464567f) - 20.211 - 88.173) * 2.83464567f);
                canvas.saveState()
                        .setFillColor(backgroundColor)
                        .rectangle(((x / 2.83464567f) - 20.211 - 128.5) * 2.83464567f, topsBottom, 128.5 * 2.83464567f, 128.5 * 2.83464567f)
                        .fill()
                        .restoreState();
                canvas2.addXObject(pageXExit, (float) (((x / 2.83464567f) - 20.211 - 128.5 + 32.89) * 2.83464567f), yExit);
            } else { // 左对齐 左箭头
                try (InputStream pdfStreamIcon = UnpaidArea.class.getResourceAsStream("/pdfs/paid/arrow_left.pdf"); PdfDocument srcPdfDocument = new PdfDocument(new PdfReader(pdfStreamIcon))) {
                    pageXArrow = srcPdfDocument.getFirstPage().copyAsFormXObject(pdfDocument);
                }
                x = starterX + (float) (22.814 * 2.83464567f);
                canvas.saveState()
                        .setFillColor(backgroundColor)
                        .rectangle(((x / 2.83464567f) + 20.211 + 88.173) * 2.83464567f, topsBottom, 128.5 * 2.83464567f, 128.5 * 2.83464567f)
                        .fill()
                        .restoreState();
                canvas2.addXObject(pageXExit, (float) ((x / 2.83464567f) + 20.211 + 88.173 + 32.89) * 2.83464567f, yExit);
            }
            canvas2.addXObject(pageXArrow, x, y);
        }

        // 底部LOGO
        try (InputStream pdfStreamIcon = UnpaidArea.class.getResourceAsStream("/pdfs/paid/logo.pdf"); PdfDocument srcPdfDocument = new PdfDocument(new PdfReader(pdfStreamIcon))) {
            PdfFormXObject pageXObject = srcPdfDocument.getFirstPage().copyAsFormXObject(pdfDocument);
            canvas.addXObject(pageXObject, starterX + (float) (pageSizeX - (129.4 * 2.83464567f)) / 2, starterY + (float) 7.117 * 2.83464567f);
        }

        // 出口信息文字
        canvas.beginText();
        canvas.setFontAndSize(arial, 85);
        canvas.setTextRenderingMode(PdfCanvasConstants.TextRenderingMode.FILL_STROKE);
        canvas.setLineWidth(0.287f);
        canvas.setCharacterSpacing(0);
        canvas.setStrokeColor(black);
        canvas.setFillColor(black);
        if (direction.equals("left")) { // 左对齐
            if (arrowType == false) {
                canvas.moveText(starterX + 155.801 * 2.83464567f, starterY + pageSizeY - 103.458 * 2.83464567f); // 设置文本的起始位置
            } else {
                canvas.moveText(starterX + 287.197 * 2.83464567f, starterY + pageSizeY - 103.458 * 2.83464567f); // 设置文本的起始位置
            }
        } else { // 右对齐
            float textWidth = arial.getWidth("Exit Information", 85);
            if (arrowType == false) {
                canvas.moveText(starterX + pageSizeX - textWidth - 155.95 * 2.83464567f, starterY + pageSizeY - 103.458 * 2.83464567f); // 设置文本的起始位置
            } else {
                canvas.moveText(starterX + pageSizeX - textWidth - 284.536 * 2.83464567f, starterY + pageSizeY - 103.458 * 2.83464567f); // 设置文本的起始位置
            }
        }
        canvas.showText("Exit Information");
        canvas.endText();

        canvas.beginText();
        canvas.setFontAndSize(heiTi, 148);
        canvas.setTextRenderingMode(PdfCanvasConstants.TextRenderingMode.FILL_STROKE);
        canvas.setLineWidth(0.287f);
        if (direction.equals("left")) { // 左对齐
            if (arrowType == false) {
                canvas.moveText(starterX + 152.501 * 2.83464567f, starterY + pageSizeY - 67.62 * 2.83464567f); // 设置文本的起始位置
            } else {
                canvas.moveText(starterX + 283.897 * 2.83464567f, starterY + pageSizeY - 67.62 * 2.83464567f); // 设置文本的起始位置
            }
        } else { // 右对齐
            float textWidth = heiTi.getWidth("出口信息", 148);
            if (arrowType == false) {
                canvas.moveText(starterX + pageSizeX - textWidth - 155.766 * 2.83464567f, starterY + pageSizeY - 67.62 * 2.83464567f); // 设置文本的起始位置
            } else {
                canvas.moveText(starterX + pageSizeX - textWidth - 284.363 * 2.83464567f, starterY + pageSizeY - 67.62 * 2.83464567f); // 设置文本的起始位置
            }
        }
        canvas.showText("出口信息");
        canvas.endText();

        // 底部设施信息
        // 创建一个临时的 PdfFormXObject
        PdfFormXObject template = new PdfFormXObject(tempPageSize);
        // 在临时 canvas 上添加内容
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
            switch (element[0]) {
                case "卫生间" -> {
                    try (InputStream pdfStreamIcon = UnpaidArea.class.getResourceAsStream("/pdfs/paid/s_paid_toilet.pdf"); PdfDocument srcPdfDocument = new PdfDocument(new PdfReader(pdfStreamIcon))) {
                        PdfFormXObject pageXObject = srcPdfDocument.getFirstPage().copyAsFormXObject(pdfDocument);
                        canvas7.addXObject(pageXObject, nextInfoX, infoYStarter);
                    }
                    nextInfoX += iconWidth + iconInterval;
                    try (InputStream pdfStreamIcon = UnpaidArea.class.getResourceAsStream("/pdfs/paid/s_paid_wheelchair.pdf"); PdfDocument srcPdfDocument = new PdfDocument(new PdfReader(pdfStreamIcon))) {
                        PdfFormXObject pageXObject = srcPdfDocument.getFirstPage().copyAsFormXObject(pdfDocument);
                        canvas7.addXObject(pageXObject, nextInfoX, infoYStarter);
                    }
                    nextInfoX += iconWidth + iconTextInterval;

                    canvas7.beginText();
                    canvas7.setFontAndSize(heiTi, 69);
                    canvas7.setTextRenderingMode(PdfCanvasConstants.TextRenderingMode.FILL_STROKE);
                    canvas7.setLineWidth(0.5f);
                    canvas7.setStrokeColor(black);
                    canvas7.setFillColor(black);
                    canvas7.setCharacterSpacing((float) spacing);
                    canvas7.moveText(nextInfoX, infoYStarterUpText); // 设置文本的起始位置
                    canvas7.showText("卫生间");
                    canvas7.endText();

                    canvas7.beginText();
                    canvas7.moveText(nextInfoX, infoYStarterDownText); // 设置文本的起始位置
                    canvas7.showText("位于");
                    canvas7.endText();

                    nextInfoX += heiTi.getWidth("位于", 69) + spacing * ("位于".length() - 1) + spacing + 1 * 2.83464567f;

                    String place = element[1]; // "A口/B口通道"

                    // 将字符串转换为字符数组
                    char[] charArray = place.toCharArray();

                    for (int i = 0; i < charArray.length; i++) {
                        char c = charArray[i];
                        canvas7.beginText();
                        if (Character.isDigit(c) && i - 1 >= 0 && String.valueOf(charArray[i - 1]).matches("[A-Z]")) {
                            nextInfoX -= (0.5 * spacing);
                        }
                        canvas7.moveText(nextInfoX, infoYStarterDownText); // 设置文本的起始位置
                        if (Character.isDigit(c) && i - 1 >= 0 && String.valueOf(charArray[i - 1]).matches("[A-Z]")) { // 角标
                            canvas7.setFontAndSize(arial, 45);
                            nextInfoX += arial.getWidth(String.valueOf(c), 45) + spacing;
                        } else if (String.valueOf(c).matches("[A-Z]") || String.valueOf(c).equals("/") || Character.isDigit(c)) {
                            canvas7.setFontAndSize(arial, 69);
                            nextInfoX += arial.getWidth(String.valueOf(c), 69) + spacing;
                        } else {
                            canvas7.setFontAndSize(heiTi, 69);
                            nextInfoX += heiTi.getWidth(String.valueOf(c), 69) + spacing;
                        }
                        canvas7.showText(String.valueOf(c));
                        canvas7.endText();
                    }
                }
                case "母婴室" -> {
                    try (InputStream pdfStreamIcon = UnpaidArea.class.getResourceAsStream("/pdfs/paid/s_paid_nursing.pdf"); PdfDocument srcPdfDocument = new PdfDocument(new PdfReader(pdfStreamIcon))) {
                        PdfFormXObject pageXObject = srcPdfDocument.getFirstPage().copyAsFormXObject(pdfDocument);
                        canvas7.addXObject(pageXObject, nextInfoX, infoYStarter);
                    }
                    nextInfoX += iconWidth + iconTextInterval;

                    canvas7.beginText();
                    canvas7.setFontAndSize(heiTi, 69);
                    canvas7.setTextRenderingMode(PdfCanvasConstants.TextRenderingMode.FILL_STROKE);
                    canvas7.setLineWidth(0.5f);
                    canvas7.setStrokeColor(black);
                    canvas7.setFillColor(black);
                    canvas7.setCharacterSpacing((float) spacing);
                    canvas7.moveText(nextInfoX, infoYStarterUpText); // 设置文本的起始位置
                    canvas7.showText("母婴室");
                    canvas7.endText();

                    canvas7.beginText();
                    canvas7.moveText(nextInfoX, infoYStarterDownText); // 设置文本的起始位置
                    canvas7.showText("位于");
                    canvas7.endText();

                    nextInfoX += heiTi.getWidth("位于", 69) + spacing * ("位于".length() - 1) + spacing + 1 * 2.83464567f;
                    String place = element[1]; // "A口/B口通道"

                    // 将字符串转换为字符数组
                    char[] charArray = place.toCharArray();
                    for (int i = 0; i < charArray.length; i++) {
                        char c = charArray[i];
                        canvas7.beginText();
                        if (Character.isDigit(c) && i - 1 >= 0 && String.valueOf(charArray[i - 1]).matches("[A-Z]")) {
                            nextInfoX -= (0.5 * spacing);
                        }
                        canvas7.moveText(nextInfoX, infoYStarterDownText); // 设置文本的起始位置
                        if (Character.isDigit(c) && i - 1 >= 0 && String.valueOf(charArray[i - 1]).matches("[A-Z]")) { // 角标
                            canvas7.setFontAndSize(arial, 45);
                            nextInfoX += arial.getWidth(String.valueOf(c), 45) + spacing;
                        } else if (String.valueOf(c).matches("[A-Z]") || String.valueOf(c).equals("/") || Character.isDigit(c)) {
                            canvas7.setFontAndSize(arial, 69);
                            nextInfoX += arial.getWidth(String.valueOf(c), 69) + spacing;
                        } else {
                            canvas7.setFontAndSize(heiTi, 69);
                            nextInfoX += heiTi.getWidth(String.valueOf(c), 69) + spacing;
                        }
                        canvas7.showText(String.valueOf(c));
                        canvas7.endText();
                    }
                }
                case "电梯(站厅-地面)" -> {
                    try (InputStream pdfStreamIcon = UnpaidArea.class.getResourceAsStream("/pdfs/paid/s_paid_elevator.pdf"); PdfDocument srcPdfDocument = new PdfDocument(new PdfReader(pdfStreamIcon))) {
                        PdfFormXObject pageXObject = srcPdfDocument.getFirstPage().copyAsFormXObject(pdfDocument);
                        canvas7.addXObject(pageXObject, nextInfoX, infoYStarter);
                    }
                    nextInfoX += iconWidth + iconInterval;
                    try (InputStream pdfStreamIcon = UnpaidArea.class.getResourceAsStream("/pdfs/paid/s_paid_wheelchair.pdf"); PdfDocument srcPdfDocument = new PdfDocument(new PdfReader(pdfStreamIcon))) {
                        PdfFormXObject pageXObject = srcPdfDocument.getFirstPage().copyAsFormXObject(pdfDocument);
                        canvas7.addXObject(pageXObject, nextInfoX, infoYStarter);
                    }
                    nextInfoX += iconWidth + iconTextInterval;

                    canvas7.beginText();
                    canvas7.setFontAndSize(heiTi, 69);
                    canvas7.setTextRenderingMode(PdfCanvasConstants.TextRenderingMode.FILL_STROKE);
                    canvas7.setLineWidth(0.5f);
                    canvas7.setStrokeColor(black);
                    canvas7.setFillColor(black);
                    canvas7.setCharacterSpacing((float) spacing);
                    canvas7.moveText(nextInfoX, infoYStarterUpText); // 设置文本的起始位置
                    canvas7.showText("电梯(站厅-地面)");
                    canvas7.endText();

                    canvas7.beginText();
                    canvas7.moveText(nextInfoX, infoYStarterDownText); // 设置文本的起始位置
                    canvas7.showText("位于");
                    canvas7.endText();

                    final float tempWidthUp = heiTi.getWidth("电梯(站厅-地面)", 69) + spacing * ("电梯(站厅-地面)".length() - 2); // 此处-2因为括号会多占空位 因此少加一个space
                    float tempWidthDown = heiTi.getWidth("位于", 69) + spacing * ("位于".length() - 1) + spacing + 1 * 2.83464567f;
                    float tempNextInfoX = nextInfoX + heiTi.getWidth("位于", 69) + spacing * ("位于".length() - 1) + spacing + 1 * 2.83464567f;

                    String place = element[1]; // "A口/B口通道"

                    // 将字符串转换为字符数组
                    char[] charArray = place.toCharArray();

                    for (int i = 0; i < charArray.length; i++) {
                        char c = charArray[i];
                        canvas7.beginText();
                        if (Character.isDigit(c) && i - 1 >= 0 && String.valueOf(charArray[i - 1]).matches("[A-Z]")) {
                            tempNextInfoX -= 0.5 * spacing;
                            tempWidthDown -= 0.5 * spacing;
                        }
                        canvas7.moveText(tempNextInfoX, infoYStarterDownText); // 设置文本的起始位置
                        if (Character.isDigit(c) && i - 1 >= 0 && String.valueOf(charArray[i - 1]).matches("[A-Z]")) { // 角标
                            canvas7.setFontAndSize(arial, 45);
                            tempNextInfoX += arial.getWidth(String.valueOf(c), 45) + spacing;
                            tempWidthDown += arial.getWidth(String.valueOf(c), 45) + spacing;
                        } else if (String.valueOf(c).matches("[A-Z]") || String.valueOf(c).equals("/") || Character.isDigit(c)) {
                            canvas7.setFontAndSize(arial, 69);
                            tempNextInfoX += arial.getWidth(String.valueOf(c), 69) + spacing;
                            tempWidthDown += arial.getWidth(String.valueOf(c), 69) + spacing;
                        } else {
                            canvas7.setFontAndSize(heiTi, 69);
                            tempNextInfoX += heiTi.getWidth(String.valueOf(c), 69) + spacing;
                            tempWidthDown += heiTi.getWidth(String.valueOf(c), 69) + spacing;
                        }
                        canvas7.showText(String.valueOf(c));
                        canvas7.endText();
                    }

                    if (tempWidthUp > tempWidthDown) { // 判断第一行文字和第二行文字哪个更长 以长的为准
                        // nextInfoX += tempWidthUp + iconTextInterval + 3 * 2.83464567f;
                        nextInfoX += tempWidthUp;
                    } else {
                        nextInfoX = tempNextInfoX;
                    }
                }
                default -> {
                }
            }
            nextInfoX -= spacing;

            if (index < facilityInfoBottom.size()) {
                nextInfoX += iconTextInterval + 3 * 2.83464567f;

                // 竖线
                canvas7.saveState()
                        .setFillColor(black)
                        .rectangle(nextInfoX, infoYStarter, 1.279 * 2.83464567f, iconWidth)
                        .fill()
                        .restoreState();

                nextInfoX += 1.279 * 2.83464567f + iconTextInterval + 4 * 2.83464567f;
            }

            index++;
        }

        // 底部其他设施最终输出坐标
        float scale = maxInfoX / nextInfoX;
        float correctX = (float) 28.641 * 2.83464567f;
        float correctY;
        if (scale < 1) {
            correctY = bottomTop + (lastLineY - bottomTop) * (float) 0.5569 - iconWidth / 2 + (iconWidth - iconWidth * scale) / 2;
        } else {
            correctY = bottomTop + (lastLineY - bottomTop) * (float) 0.5569 - iconWidth / 2;
        }
        PdfCanvas finalCanvas = new PdfCanvas(page);

        if (scale < 1) {
            finalCanvas.addXObjectWithTransformationMatrix(template, scale, 0, 0, scale, starterX + correctX, starterY + correctY);
        } else {
            finalCanvas.addXObjectWithTransformationMatrix(template, 1, 0, 0, 1, starterX + correctX, starterY + correctY);
        }

        // 二维码绿色矩形
        backgroundColor = new DeviceCmyk(100, 0, 100, 0);
        float qrBaseX = starterX + (float) (pageSizeX - 80.906 * 2.83464567f);
        // float qrBaseY = starterY + bottomTop + (lastLineY - bottomTop) / 2 - (lastLineY - bottomTop) * (float) 0.0781;
        float qrBaseY = starterY + bottomTop + (lastLineY - bottomTop) * (float) 0.5569 - iconWidth / 2 + (float) 11.786 * 2.83464567f;
        canvas.saveState()
                .setFillColor(backgroundColor)
                .rectangle(qrBaseX, qrBaseY, 47.574 * 2.83464567f, 50.15 * 2.83464567f)
                .fill()
                .restoreState();

        // 二维码文字
        PdfCanvas canvas6 = new PdfCanvas(page);
        canvas6.beginText();
        canvas6.setFontAndSize(heiTi, 21);
        canvas6.setFillColor(black);
        canvas6.setCharacterSpacing((float) 1.8);
        canvas6.moveText(qrBaseX, qrBaseY - 11.093 * 2.83464567f); // 设置文本的起始位置
        canvas6.showText("扫码获取实时");
        canvas6.endText();

        canvas6.beginText();
        canvas6.moveText(qrBaseX, qrBaseY - 20.286 * 2.83464567f); // 设置文本的起始位置
        canvas6.showText("公交换乘信息");
        canvas6.endText();

        // 二维码图片
        ImageData data;
        try (InputStream qrCodeStream = UnpaidArea.class.getClassLoader().getResourceAsStream("images/" + stationNumber + stationName + ".png")) {
            data = ImageDataFactory.create(StreamUtil.inputStreamToArray(qrCodeStream));
        }
        Image qrCode = new Image(data);
        qrCode.setFixedPosition((float) ((47.574 * 2.83464567f - 44.409 * 2.83464567) / 2 + qrBaseX), (float) ((50.15 * 2.83464567f - 46.945 * 2.83464567) / 2 + qrBaseY));
        qrCode.setWidth((float) 44.409 * 2.83464567f);
        qrCode.setHeight((float) 46.945 * 2.83464567f);
        document.add(qrCode);

        // 灰色最外圈描边
        Color outlineColor = new DeviceCmyk(0, 0, 0, 10);
        canvas.saveState()
                .setStrokeColor(outlineColor)
                .setLineWidth(2.4f)
                .rectangle(starterX, starterY, pageSizeX, pageSizeY)
                .stroke()
                .restoreState();
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
        System.out.print("温馨提示：尚未指定对齐方向，已默认向左对齐。"); // 如果没有检测到特定字符，发出提示
        return "left";
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
        facilityKeywords.put("卫生间", "卫生间|洗手间");
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
        // 查找字符串中第一个出现的“口”字的位置
        int indexOfKou = input.indexOf("口");

        // 如果找到了“口”字
        if (indexOfKou != -1 && indexOfKou > 0) {
            // 使用charAt方法获取“口”字前面的字符，然后将其转换为整数后返回
            return Character.getNumericValue(input.charAt(indexOfKou - 1));
        }

        // 如果没有找到“口”字，返回1
        return 1;
    }

    public static ArrayList<Integer> parseNumLines(String input) { // 获取每一个出入口有多少条横线
        ArrayList<Integer> list = new ArrayList<>();
        String[] parts = input.split("\\+");

        for (String part : parts) {
            list.add(Integer.parseInt(part));
        }

        return list;
    }
}
