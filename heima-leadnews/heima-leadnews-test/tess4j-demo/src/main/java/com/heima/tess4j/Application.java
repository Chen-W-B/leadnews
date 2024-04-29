package com.heima.tess4j;

import net.sourceforge.tess4j.ITesseract;
import net.sourceforge.tess4j.Tesseract;

import java.io.File;

public class Application {

    /**
     * 识别图片中的文字
     * @param args
     * */
    public static void main(String[] args) throws Exception {

        //创建实例
        ITesseract tesseract = new Tesseract();

        //设置字体库路径  路径中的 chi_sim.traineddata 文件是 简体中文字体库
        tesseract.setDatapath("D:\\IDEA\\project\\heima-leadnews\\tessdata");

        //设置语言 --> 简体中文
        tesseract.setLanguage("chi_sim");

        File file = new File("C:\\Users\\86177\\Pictures\\Screenshots\\143.png");
        //识别图片
        String result = tesseract.doOCR(file);

        //去掉回车和tab键，就是将识别的结果都放在同一行，用"-"来连接每一行的头尾
        System.out.println("识别的结果为："+result.replaceAll("\\r|\\n","-"));
    }
}
