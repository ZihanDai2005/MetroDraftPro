/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.mavenproject1;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import javax.swing.JTextArea;
import javax.swing.SwingUtilities;

/**
 *
 * @author daizhenjin
 *//// 自定义输出流，用于将输出重定向到文本区域
public class CustomOutputStream extends ByteArrayOutputStream {
        private JTextArea textArea;

        public CustomOutputStream(JTextArea textArea) {
            this.textArea = textArea;
        }

        @Override
        public void flush() throws IOException {
            super.flush();
            String text = toString("UTF-8");
            SwingUtilities.invokeLater(() -> {
                textArea.append(text);
                textArea.setCaretPosition(textArea.getDocument().getLength());
            });
            reset();
        }
    }
