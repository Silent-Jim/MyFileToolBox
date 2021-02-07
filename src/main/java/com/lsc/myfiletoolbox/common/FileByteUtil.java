package com.lsc.myfiletoolbox.common;

import java.io.FileInputStream;
import java.io.RandomAccessFile;

public class FileByteUtil {
    /**
     * 修改文件头的前两个字节
     *
     * @param header
     * @param filePath
     * @throws Exception
     */
    public static void modifyFileHeader(byte[] header, String filePath) {
        try (RandomAccessFile src = new RandomAccessFile(filePath, "rw")) {
            int srcLength = (int) src.length();
            src.seek(0);
            src.write(header);
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("修改文件header失败!" + filePath);
        }
    }

    /**
     * 根据文件路径获取文件头前两个字节
     *
     * @param filePath 文件路径
     * @return 文件头前两个字节信息
     */
    public static byte[] getFileHeader(String filePath, int len) {
        byte[] value = null;
        try (FileInputStream is = new FileInputStream(filePath)) {
            value = new byte[len];
            is.read(value, 0, len);
        } catch (Exception e) {
            System.out.println("获取文件header失败!" + filePath);
        }
        return value;
    }

}
