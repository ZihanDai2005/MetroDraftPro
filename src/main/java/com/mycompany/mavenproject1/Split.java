/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.mavenproject1;

import com.itextpdf.layout.splitting.ISplitCharacters;
import com.itextpdf.io.font.otf.GlyphLine;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

/**
 *
 * @author daizhenjin
 */
public class Split implements ISplitCharacters {

    @Override
    public boolean isSplitCharacter(GlyphLine text, int glyphPos) {
        // 获取当前字符和下一个字符
        char currentChar = (char) text.get(glyphPos).getUnicode();
        char nextChar = glyphPos + 1 < text.size() ? (char) text.get(glyphPos + 1).getUnicode() : ' ';

        // 检查当前字符是否是数字
        if (Character.isDigit(currentChar)) {
            // 如果当前字符是数字，检查下一个字符是否也是数字
            if (glyphPos + 1 < text.size()) {
                char nextChar2 = (char) text.get(glyphPos + 1).getUnicode();
                if (Character.isDigit(nextChar2)) {
                    // 如果下一个字符也是数字，不允许在这里换行
                    return false;
                }
            }
        }

        // 新增逻辑：检查下一个字符是否是“线”或“路”
        if (glyphPos + 1 < text.size()) {
            char nextChar3 = (char) text.get(glyphPos + 1).getUnicode();
            if (nextChar3 == '路' || nextChar3 == '号') {
                // 如果下一个字符是“线”或“路”，不允许在这里换行
                return false;
            }
        }

        if (currentChar == '（' || currentChar == '(') {
            // 如果当前字符是“（”，不允许在这里换行
            return false;
        }
        
        if (String.valueOf(currentChar).matches("[A-Z]") && Character.isDigit(nextChar)){
            return false;
        }

        // 定义一些规则，例如避免在行首出现的标点
        Set<Character> notAtStart = new HashSet<>(Arrays.asList(',', '，', '、', '.', '。', ';', '；', ')', '）'));

        // 如果下一个字符是不应该出现在行首的标点，则不在当前字符处折行
        if (notAtStart.contains(nextChar)) {
            return false;
        }

        // 默认行为：允许在当前字符处折行
        return true;
    }
}
