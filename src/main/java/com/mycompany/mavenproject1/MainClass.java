/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.mavenproject1;

import com.itextpdf.io.font.PdfEncodings;
import com.itextpdf.io.util.StreamUtil;
import com.itextpdf.kernel.font.PdfFont;
import com.itextpdf.kernel.font.PdfFontFactory;
import com.itextpdf.kernel.geom.PageSize;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfPage;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.kernel.pdf.WriterProperties;
import com.itextpdf.kernel.pdf.canvas.PdfCanvas;
import com.itextpdf.layout.Document;
import javax.swing.*;
import java.awt.*;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusAdapter;
import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import com.itextpdf.io.font.PdfEncodings;
import com.itextpdf.io.image.ImageData;
import com.itextpdf.io.image.ImageDataFactory;
import com.itextpdf.io.util.StreamUtil;
import com.itextpdf.kernel.colors.Color;
import com.itextpdf.kernel.colors.DeviceCmyk;
import com.itextpdf.kernel.colors.Separation;
import com.itextpdf.kernel.font.PdfFont;
import com.itextpdf.kernel.font.PdfFontFactory;
import com.itextpdf.kernel.geom.PageSize;
import com.itextpdf.kernel.pdf.PdfArray;
import com.itextpdf.kernel.pdf.PdfDictionary;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfName;
import com.itextpdf.kernel.pdf.PdfNumber;
import com.itextpdf.kernel.pdf.PdfObject;
import com.itextpdf.kernel.pdf.PdfPage;
import com.itextpdf.kernel.pdf.PdfReader;
import com.itextpdf.kernel.pdf.PdfStream;
import com.itextpdf.kernel.pdf.PdfVersion;
import com.itextpdf.kernel.pdf.canvas.PdfCanvas;
import com.itextpdf.kernel.pdf.canvas.PdfCanvasConstants;
import com.itextpdf.kernel.pdf.colorspace.PdfColorSpace;
import com.itextpdf.kernel.pdf.colorspace.PdfSpecialCs;
import com.itextpdf.kernel.pdf.function.PdfFunction;
import com.itextpdf.kernel.pdf.function.PdfFunction.Type2;
import com.itextpdf.kernel.pdf.xobject.PdfFormXObject;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.element.Image;
import java.awt.geom.GeneralPath;

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
import org.apache.poi.ss.usermodel.Row;

/**
 *
 * @author Z.D.
 */
public class MainClass {

    private static String filePath; // 用于存储文件路径
    private static String directoryPath; // 用于存储文件夹路径
    private static boolean isSelected;

    public static void main(String[] args) {
        SwingUtilities.invokeLater(MainClass::createAndShowGUI);
    }

    private static void createAndShowGUI() {
        JFrame frame = new JFrame("二级信息生成");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(600, 400);
        frame.setLocationRelativeTo(null); // 窗口居中

        JPanel topPanel = new JPanel();
        frame.add(topPanel, BorderLayout.NORTH);

        JLabel labelColumn = new JLabel("列数：");
        topPanel.add(labelColumn);

        JTextField textField = new JTextField();
        textField.setColumns(20);
        textField.setText("请用空格分隔");
        textField.setForeground(java.awt.Color.GRAY);
        topPanel.add(textField);

        JCheckBox selectAllCheckbox = new JCheckBox("全选");
        topPanel.add(selectAllCheckbox);

        JPanel buttonsPanel = new JPanel(new GridLayout(1, 2, 10, 0));
        JButton uploadButton = new JButton("上传文件");
        JButton folderButton = new JButton("选择保存地址");
        buttonsPanel.add(uploadButton);
        buttonsPanel.add(folderButton);

        JPanel labelsPanel = new JPanel(new GridLayout(2, 1));
        JLabel filePathLabel = new JLabel("<html>文件地址未选择</html>", SwingConstants.CENTER);
        JLabel directoryPathLabel = new JLabel("<html>文件夹地址未选择</html>", SwingConstants.CENTER);
        labelsPanel.add(filePathLabel);
        labelsPanel.add(directoryPathLabel);

        JPanel centerPanel = new JPanel(new GridLayout(2, 1));
        centerPanel.add(buttonsPanel);
        centerPanel.add(labelsPanel);
        frame.add(centerPanel, BorderLayout.CENTER);

        JTextArea outputTextArea = new JTextArea(5, 20);
        outputTextArea.setEditable(false);
        JScrollPane scrollPane = new JScrollPane(outputTextArea);
        frame.add(scrollPane, BorderLayout.SOUTH);

        JButton generateButton = new JButton("生成");
        frame.add(generateButton, BorderLayout.SOUTH);

        // Event Listeners
        textField.addFocusListener(new FocusAdapter() {
            public void focusGained(FocusEvent e) {
                if (textField.getText().equals("请用空格分隔")) {
                    textField.setText("");
                    textField.setForeground(java.awt.Color.BLACK);
                }
            }

            public void focusLost(FocusEvent e) {
                if (textField.getText().isEmpty()) {
                    textField.setForeground(java.awt.Color.GRAY);
                    textField.setText("请用空格分隔");
                }
            }
        });

        selectAllCheckbox.addActionListener(e -> {
            isSelected = selectAllCheckbox.isSelected();
            textField.setEnabled(!isSelected);
            if (isSelected) {
                textField.setText("");
            } else {
                textField.setForeground(java.awt.Color.GRAY);
                textField.setText("请用空格分隔");
            }
        });

        // 上传文件按钮事件监听
        uploadButton.addActionListener(e -> {
            JFileChooser fileChooser = new JFileChooser();
            FileNameExtensionFilter filter = new FileNameExtensionFilter("Excel文件", "xlsx");
            fileChooser.setFileFilter(filter);
            int returnValue = fileChooser.showOpenDialog(frame);
            if (returnValue == JFileChooser.APPROVE_OPTION) {
                File selectedFile = fileChooser.getSelectedFile();
                filePath = selectedFile.getAbsolutePath();
                filePathLabel.setText("<html>文件地址：" + filePath + "</html>"); // 使文件路径支持自动换行
                uploadButton.setText("上传成功");
                uploadButton.setForeground(new java.awt.Color(68, 147, 60));
            }
        });

        // 选择保存地址按钮事件监听
        folderButton.addActionListener(e -> {
            JFileChooser folderChooser = new JFileChooser();
            folderChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
            int returnValue = folderChooser.showOpenDialog(frame);
            if (returnValue == JFileChooser.APPROVE_OPTION) {
                File selectedDirectory = folderChooser.getSelectedFile();
                directoryPath = selectedDirectory.getAbsolutePath();
                directoryPathLabel.setText("<html>文件夹地址：" + directoryPath + "</html>"); // 使文件夹路径支持自动换行
                folderButton.setText("选择成功");
                folderButton.setForeground(new java.awt.Color(68, 147, 60));
            }
        });

        // 生成按钮事件监听
        generateButton.addActionListener(e -> {
            PdfWriter pdfWriter = null;
            ArrayList<Integer> col = new ArrayList<>();
            try {
                File excelFile = new File(filePath);
                String path = directoryPath;
                String stationNumber = "";
                String stationName = "";
                String titleStationName = "";
                try {
                    BufferedInputStream bis = new BufferedInputStream(new FileInputStream(excelFile));
                    XSSFWorkbook workbook = new XSSFWorkbook(bis);
                    XSSFSheet sheet = workbook.getSheetAt(0);
                    
                    // 获取类型 如"付费区"或"非付费区"
                    XSSFCell titleCell = sheet.getRow(0).getCell(0);
                    stationNumber = getStationNumber(titleCell.getStringCellValue())[1];
                    stationName = getStationNumber(titleCell.getStringCellValue())[0];
                    if (!"站".equals(stationName.substring(stationName.length() - 1, stationName.length()))) {
                        titleStationName = stationName + "站";
                    } else {
                        titleStationName = stationName;
                    }

                    if (textField.getText().equals("") || textField.getText().equals("请用空格分隔") || isSelected == true) {
                        // 如果全选 遍历所有列
                        Row row = sheet.getRow(2);
                        for(int i = 0; i < row.getLastCellNum(); i++){
                            if(row.getCell(i) != null && !row.getCell(i).getStringCellValue().equals("")){
                                col.add(i);
                            }
                        }
                    }else{
                        // 如果输入了列数
                        String[] tempCol = textField.getText().split(" ");
                        for(String i : tempCol){
                            col.add(Integer.parseInt(i) - 1); // 全部-1以符合实际坐标从0开始
                        }
                    }
                    
                } catch (FileNotFoundException ex) {
                    System.err.println(ex.getMessage());
                } catch (IOException ex) {
                    System.err.println(ex.getMessage());
                }

                PdfFont siyuanHei = null;
                float textWidth = 0;
                float textHeight = 0;
                String title = stationNumber + " " + titleStationName;
                int chineseSpace = 10;
                try (InputStream fontStream1 = UnpaidArea.class.getResourceAsStream("/fonts/MSYH_Bold.ttf")) {
                    siyuanHei = PdfFontFactory.createFont(StreamUtil.inputStreamToArray(fontStream1), PdfEncodings.IDENTITY_H, PdfFontFactory.EmbeddingStrategy.PREFER_EMBEDDED);
                    textWidth = siyuanHei.getWidth(title, 192) + chineseSpace * (title.length() - 1);
                    textHeight = siyuanHei.getAscent(title.substring(0, 2), 192) - siyuanHei.getDescent(title.substring(0, 2), 192);
                } catch (IOException ex) {
                    Logger.getLogger(MainClass.class.getName()).log(Level.SEVERE, null, ex);
                }

                path = path + "/" + stationNumber + stationName + ".pdf";

                final int startRow = 2;

                PageSize pageSize = new PageSize(4500 * 2.83464567f, 4500 * 2.83464567f);
                pdfWriter = new PdfWriter(path, new WriterProperties().setCompressionLevel(0));
                PdfDocument pdfDocument = new PdfDocument(pdfWriter);
                PdfPage page = pdfDocument.addNewPage(pageSize);
                Document document = new Document(pdfDocument);

                PdfCanvas canvas = new PdfCanvas(page);

//                // 定义替代色彩空间为CMYK
//                PdfColorSpace alternateCS = PdfColorSpace.makeColorSpace(new PdfName("DeviceCMYK"));
//
//                // 创建颜色转换函数
//                PdfArray domain = new PdfArray(new float[]{0, 1});
//                PdfArray range = new PdfArray(new float[]{0, 1, 0, 1, 0, 1, 0, 1});
//                PdfArray c0 = new PdfArray(new float[]{0, 0, 0, 0});
//                PdfArray c1 = new PdfArray(new float[]{0.87f, 0.53f, 0f, 0f});
//                PdfNumber n = new PdfNumber(1);
//                PdfFunction tintFunction = new PdfFunction.Type2(domain, range, c0, c1, n);
//
//                // 创建专色色彩空间
//                Separation pantoneColor = new Separation("PANTONE 300 C", alternateCS, tintFunction, 1);

                Color backgroundColor = new DeviceCmyk(0, 100, 100, 0);
                canvas.saveState()
                        .setFillColor(backgroundColor)
                        .rectangle(250 * 2.83464567f, 4180 * 2.83464567f, 4000 * 2.83464567f, 120 * 2.83464567f)
                        .fill()
                        .restoreState();
                canvas.beginText();
                canvas.setFontAndSize(siyuanHei, 192);
                canvas.setCharacterSpacing(chineseSpace);
                canvas.setTextRenderingMode(PdfCanvasConstants.TextRenderingMode.FILL);
                canvas.setFillColor(new DeviceCmyk(0, 0, 0, 0));
                canvas.moveText(250 * 2.83464567f + (4000 * 2.83464567f - textWidth) / 2, 4180 * 2.83464567f + (120 * 2.83464567f - textHeight) / 2); //设置文本的起始位置
                canvas.showText(title);
                canvas.endText();

                float x = 500;
                float y = 2300;
                float xl = 685;
                float yl = 1725;
                for (int i = 0; i < col.size(); i++, x += (xl + 200)) {
                    if (i == 4) {
                        x = 500;
                        y = 300;
                    }
                    int currentCol = col.get(i);
                    String type = "";
                    String printInfo = "";
                    try {
                        BufferedInputStream bis = new BufferedInputStream(new FileInputStream(excelFile));
                        XSSFWorkbook workbook = new XSSFWorkbook(bis);
                        XSSFSheet sheet = workbook.getSheetAt(0);
                        // 获取类型 如"付费区"或"非付费区"
                        XSSFCell typeCell = sheet.getRow(startRow).getCell(currentCol);
                        type = deleteBracket(typeCell.getStringCellValue());
                        // 获取打印数据 如张数、材质
                        XSSFCell printInfoCell = sheet.getRow(startRow + 1).getCell(currentCol);
                        printInfo = printInfoCell.getStringCellValue();
                    } catch (IOException ex) {
                        Logger.getLogger(MainClass.class.getName()).log(Level.SEVERE, null, ex);
                    }

                    ArrayList<Object> data = new ArrayList<>();
                    data.add(x);
                    data.add(y);
                    data.add(xl);
                    data.add(yl);
                    data.add(col.get(i));
                    data.add(stationName);

                    data.add(pdfDocument);
                    data.add(page);
                    data.add(excelFile); // Excel文件
                    data.add(document);

                    // 根据选项调用不同的类的main方法
                    if (type.equals("付费区")) {
                        try {
                            PaidArea.main(data);
                        } catch (IOException ex) {
                            Logger.getLogger(MainClass.class.getName()).log(Level.SEVERE, null, ex);
                        }
                    } else if (type.equals("非付费区")) {
                        try {
                            UnpaidArea.main(data);
                        } catch (IOException ex) {
                            Logger.getLogger(MainClass.class.getName()).log(Level.SEVERE, null, ex);
                        }
                    }
                }
                document.close();
            } catch (FileNotFoundException ex) {
                System.err.println(ex.getMessage());
            } finally {
                try {
                    pdfWriter.close();
                } catch (IOException ex) {
                    System.err.println(ex.getMessage());
                }
            }
        });

        frame.setVisible(true);

        // 确保初始化时文本框不自动获得焦点
        frame.requestFocusInWindow();
    }

    public static String deleteBracket(String typeValue) {
        if (typeValue.contains("(")) {
            return typeValue.substring(0, typeValue.indexOf("("));
        } else if (typeValue.contains("（")) {
            return typeValue.substring(0, typeValue.indexOf("（"));
        }
        return typeValue;
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
}
