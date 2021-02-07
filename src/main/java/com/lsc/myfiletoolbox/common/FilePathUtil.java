package com.lsc.myfiletoolbox.common;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @Description 目录下文件的扫描
 * @Author yueyh
 * @Date 2019/5/27 0027 15:55
 **/
public class FilePathUtil {

    public static String suffix = "";

    public static Map<String, List<String>> getAllDirFilePath(String filePath) {
        return getAllDirFilePath(filePath, "heptabledesign");
    }

    public static Map<String, List<String>> getAllDirFilePath(String filePath, String suffix) {
        if (suffix != null) {
            FilePathUtil.suffix = suffix;
        }
        Map<String, List<String>> filePathMap = new HashMap<>();
        File[] files = new File(filePath).listFiles();
        try {
            if (null != files) {
                for (File file : files) {
                    if (!file.isDirectory()) {
                        continue;
                    }
                    String childPath = file.getPath();
                    String fileName = file.getName();
                    // System.out.println(childPath);
                    List<String> list = new ArrayList<>();
                    scanFiles(childPath, list);
                    filePathMap.put(fileName, list);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        FilePathUtil.suffix = "";
        return filePathMap;
    }

    // private static ArrayList<String> scanFiles = new ArrayList<String>();

    public static List<String> scanFiles(String folderPath, List<String> scanFiles) throws Exception {
        List<String> dirctorys = new ArrayList<String>();
        File directory = new File(folderPath);
        if (!directory.isDirectory()) {
            throw new Exception('"' + folderPath + '"' + " input path is not a Directory , please input the right path of the Directory. ^_^...^_^");
        }
        if (directory.isDirectory()) {
            File[] filelist = directory.listFiles();
            for (int i = 0; i < filelist.length; i++) {
                /**如果当前是文件夹，进入递归扫描文件夹**/
                if (filelist[i].isDirectory()) {
                    dirctorys.add(filelist[i].getAbsolutePath());
                    /**递归扫描下面的文件夹**/
                    scanFiles(filelist[i].getAbsolutePath(), scanFiles);
                }
                /**非文件夹**/
                else {
                    String filePath = filelist[i].getAbsolutePath();
                    scanFiles.add(filelist[i].getAbsolutePath());
                }
            }
        }
        return scanFiles;
    }
}
